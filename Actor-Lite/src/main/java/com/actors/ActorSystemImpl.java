package com.actors;

import android.support.annotation.NonNull;

import com.chaining.Chain;
import com.functional.curry.SwapCurry;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

/**
 * an implementation to the {@link ActorSystem}, this class is not part of the API,
 * it is public just for testing
 * <p>
 * Created by Ahmed Adel Ismail on 10/10/2017.
 */
class ActorSystemImpl {

    private static final Map<Object, ActorSystemImpl> instances = new LinkedHashMap<>(1);
    private static final int MAILBOX_CAPACITY = 10;

    final TypedMap<ReplaySubject<Message>> mailboxes = new TypedMap<>(new LinkedHashMap<>());
    final TypedMap<Disposable> actorsDisposables = new TypedMap<>(new LinkedHashMap<>());
    private final Object lock = new Object();


    private ActorSystemImpl() {

    }

    static ActorSystemImpl getInstance(Object key) {
        synchronized (ActorSystemImpl.class) {
            return doGetInstance(key);
        }
    }

    @NonNull
    private static ActorSystemImpl doGetInstance(Object key) {
        ActorSystemImpl actorSystem = instances.get(key);
        if (actorSystem == null) {
            actorSystem = new ActorSystemImpl();
            instances.put(key, actorSystem);
        }
        return actorSystem;
    }


    /**
     * send an empty {@link Message} with the passed id
     *
     * @param messageId       the id of the {@link Message}
     * @param actorsAddresses the actor (or group of actorsAddresses) that will receive this message
     */
    public void send(int messageId, @NonNull Class<?>... actorsAddresses) {
        send(new Message(messageId), actorsAddresses);
    }

    /**
     * send a {@link Message} to a mailbox
     *
     * @param message         the {@link Message} object
     * @param actorsAddresses the actor (or group of actors) that will receive this message
     */
    public void send(final Message message, @NonNull Class<?>... actorsAddresses) {
        if (actorsAddresses.length == 0) {
            throw new UnsupportedOperationException("no Actors passed to the parameters");
        }
        Observable.fromArray(actorsAddresses)
                .flatMap(mailboxes::getOrIgnore)
                .blockingSubscribe(mailbox -> mailbox.onNext(message), Throwable::printStackTrace);
    }


    /**
     * register a class to a mailbox but with the default configurations
     *
     * @param actor             the Actor that will handle messages
     * @param observeOn         the {@link Scheduler} that will host the received messages
     * @param onMessageReceived the {@link Consumer} function that will be invoked
     *                          when a message is received
     */
    public void register(@NonNull Object actor,
                         @NonNull final Scheduler observeOn,
                         @NonNull final Consumer<Message> onMessageReceived) {
        register(actor, defaultMailboxBuilder(observeOn, onMessageReceived));
    }

    /**
     * register a class to a mailbox
     *
     * @param actor          the class (Actor) that will handle messages
     * @param mailboxBuilder a function that takes a {@link MailboxBuilder} and generates a Mailbox
     */
    public void register(@NonNull Object actor,
                         @NonNull Consumer<MailboxBuilder> mailboxBuilder) {
        synchronized (lock) {
            doRegister(actor, mailboxBuilder);
        }
    }

    @NonNull
    private static Consumer<MailboxBuilder> defaultMailboxBuilder(
            final Scheduler observeOn,
            final Consumer<Message> onMessageReceived) {

        return mailboxBuilder -> mailboxBuilder
                .observeOn(observeOn)
                .onMessageReceived(onMessageReceived);
    }

    private void doRegister(Object actor, Consumer<MailboxBuilder> mailboxBuilderFunction) {
        mailboxes.getOrIgnore(actor)
                .defaultIfEmpty(ReplaySubject.create(MAILBOX_CAPACITY))
                .map(Chain::let)
                .blockingGet()
                .map(MailboxBuilder::new)
                .apply(mailboxBuilderFunction)
                .map(MailboxBuilder::build)
                .apply(builder -> mailboxes.put(actor, builder.getMailbox()))
                .apply(builder -> actorsDisposables.put(actor, builder.getActorDisposable()))
                .apply(MailboxBuilder::clear);
    }

    /**
     * register a class to a mailbox but with default configurations and will invoke
     * {@link Actor#onMessageReceived(Message)} when the passed {@link Actor} receives a message,
     * the address of this {@link Actor}'s Mailbox will be the {@link Class} of the passed object,
     * and the {@link Scheduler} that will be used to observe on the Mailbox is the
     * one supplied by the {@link Actor#observeOnScheduler()}
     *
     * @param actor the {@link Actor} that will receive messages and it's {@link Class}
     *              will be the address of it's mailbox
     */
    public void register(@NonNull final Actor actor) {
        register(actor, defaultMailboxBuilder(actor.observeOnScheduler(), actor::onMessageReceived));
    }

    private void disposeIfNotDisposed(Disposable disposable) {
        if (!disposable.isDisposed()) disposable.dispose();
    }

    /**
     * postpone the current Actor, this means that this actor will either register again, or
     * unregister itself in a later point ... this function will cause any coming message to be
     * queued until the Actor Registers itself again, and then it will re-send the queued messages
     * <p>
     * if the Actor unregistered itself, the pending messages will be cancelled
     *
     * @param actor the Actor to be postponed
     */
    public void postpone(Object actor) {
        synchronized (lock) {
            Chain.let(actor)
                    .apply(this::unregister)
                    .apply(SwapCurry.toConsumer(mailboxes::put, ReplaySubject.create(MAILBOX_CAPACITY)));
        }
    }

    /**
     * unregister a class from it's mailbox, notice that this method will execute
     * {@link Subject#onComplete()} to notify the actor that it has completed it's task and
     * will not receive messages any more
     *
     * @param actor the Actor that was registered through {@link #register(Object, Consumer)}} or
     *              {@link #register(Object, Consumer)}
     */
    public void unregister(@NonNull Object actor) {
        synchronized (lock) {
            if (actor instanceof Class) {
                doUnregisterClass((Class<?>) actor);
            } else {
                doUnregisterObject(actor);
            }

        }
    }

    private void doUnregisterClass(Class<?> actor) {
        mailboxes.getOrIgnore(actor)
                .doOnNext(Subject::onComplete)
                .doOnNext(mailbox -> mailboxes.remove(actor))
                .flatMap(mailbox -> actorsDisposables.getOrIgnore(actor))
                .doOnNext(disposable -> actorsDisposables.remove(actor))
                .defaultIfEmpty(dummyDisposable())
                .subscribe(this::disposeIfNotDisposed, Throwable::printStackTrace);
    }

    private void doUnregisterObject(@NonNull Object actor) {
        mailboxes.getOrIgnore(actor)
                .doOnSuccess(Subject::onComplete)
                .doOnSuccess(mailbox -> mailboxes.remove(actor))
                .flatMap(mailbox -> actorsDisposables.getOrIgnore(actor))
                .doOnSuccess(disposable -> actorsDisposables.remove(actor))
                .defaultIfEmpty(dummyDisposable())
                .subscribe(this::disposeIfNotDisposed, Throwable::printStackTrace);
    }

    @NonNull
    private Disposable dummyDisposable() {
        return new Disposable() {
            @Override
            public void dispose() {

            }

            @Override
            public boolean isDisposed() {
                return false;
            }
        };
    }

}
