package com.actors;

import android.support.annotation.NonNull;

import com.chaining.Chain;

import java.util.LinkedHashMap;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

/**
 * an implementation to the {@link ActorSystem}, this class is not part of the API,
 * it is public just for testing
 * <p>
 * Created by Ahmed Adel Ismail on 10/10/2017.
 */
public class ActorSystemInstance {

    private static final Map<Object, ActorSystemInstance> instances = new LinkedHashMap<>(1);
    private static final int MAILBOX_CAPACITY = 10;

    private final Object lock;
    private final TypedMap<ReplaySubject<Message>> mailboxes;
    private final TypedMap<Disposable> actorsDisposables;
    private final ActorsInjector actorsInjector;


    private ActorSystemInstance() {
        lock = new Object();
        mailboxes = new TypedMap<>(new LinkedHashMap<Object, ReplaySubject<Message>>());
        actorsDisposables = new TypedMap<>(new LinkedHashMap<Object, Disposable>());
        actorsInjector = new ActorsInjector(this);
    }

    public static ActorSystemInstance getInstance(Object key) {
        synchronized (ActorSystemInstance.class) {
            return doGetInstance(key);
        }
    }

    @NonNull
    private static ActorSystemInstance doGetInstance(Object key) {
        ActorSystemInstance actorSystem = instances.get(key);
        if (actorSystem == null) {
            actorSystem = new ActorSystemInstance();
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
                .flatMap(toMailboxMaybe())
                .blockingSubscribe(invokeMailboxOnNext(message), printStackTrace());
    }

    @NonNull
    private Function<Class<?>, Observable<ReplaySubject<Message>>> toMailboxMaybe() {
        return new Function<Class<?>, Observable<ReplaySubject<Message>>>() {
            @Override
            public Observable<ReplaySubject<Message>> apply(Class<?> aClass) throws Exception {
                return mailboxes.getOrIgnore(aClass);
            }
        };
    }

    @NonNull
    private Consumer<ReplaySubject<Message>> invokeMailboxOnNext(final Message message) {
        return new Consumer<ReplaySubject<Message>>() {
            @Override
            public void accept(ReplaySubject<Message> mailbox) throws Exception {
                mailbox.onNext(message);
            }
        };
    }

    @NonNull
    private Consumer<Throwable> printStackTrace() {
        return new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        };
    }

    /**
     * register a class to a mailbox but with the default configurations
     *
     * @param actor             the Actor that will handle messages
     * @param observeOn         the {@link Scheduler} that will host the received messages
     * @param onMessageReceived the {@link Consumer} function that will be invoked
     *                          when a message is received
     * @deprecated use {@link #register(Actor)} instead
     */
    @Deprecated
    public void register(@NonNull Object actor,
                         @NonNull final Scheduler observeOn,
                         @NonNull final Consumer<Message> onMessageReceived) {
        register(actor, defaultMailboxBuilder(actor, observeOn, onMessageReceived));
    }

    /**
     * register a class to a mailbox
     *
     * @param actor          the class (Actor) that will handle messages
     * @param mailboxBuilder a function that takes a {@link MailboxBuilder} and generates a Mailbox
     * @deprecated use {@link #register(Actor)} instead
     */
    @Deprecated
    public void register(@NonNull Object actor,
                         @NonNull Consumer<MailboxBuilder> mailboxBuilder) {
        synchronized (lock) {
            doRegister(actor, mailboxBuilder);
        }
    }

    @NonNull
    private static Consumer<MailboxBuilder> defaultMailboxBuilder(
            final Object actor,
            final Scheduler observeOn,
            final Consumer<Message> onMessageReceived) {

        return new Consumer<MailboxBuilder>() {
            @Override
            public void accept(MailboxBuilder builder) {
                builder.observeOn(observeOn).onMessageReceived(onMessageReceived);
                if (actor instanceof OnActorUnregistered) {
                    builder.onMailboxClosed(invokeOnUnregister((OnActorUnregistered) actor));
                }
            }
        };
    }

    private void doRegister(Object actor, Consumer<MailboxBuilder> mailboxBuilderFunction) {
        mailboxes.getOrIgnore(actor)
                .defaultIfEmpty(ReplaySubject.<Message>create(MAILBOX_CAPACITY))
                .map(toChain())
                .blockingGet()
                .map(toMailboxBuilder())
                .apply(mailboxBuilderFunction)
                .map(toFinalMailboxBuilder())
                .apply(addMailbox(actor))
                .apply(addDisposable(actor))
                .apply(clearMailboxBuilder());

        actorsInjector.injectFor(actor);
    }

    @NonNull
    private static Action invokeOnUnregister(final OnActorUnregistered actor) {
        return new Action() {
            @Override
            public void run() throws Exception {
                actor.onUnregister();
            }
        };
    }

    @NonNull
    private Function<ReplaySubject<Message>, Chain<ReplaySubject<Message>>> toChain() {
        return new Function<ReplaySubject<Message>, Chain<ReplaySubject<Message>>>() {
            @Override
            public Chain<ReplaySubject<Message>> apply(ReplaySubject<Message> item) {
                return Chain.let(item);
            }
        };
    }

    @NonNull
    private Function<ReplaySubject<Message>, MailboxBuilder> toMailboxBuilder() {
        return new Function<ReplaySubject<Message>, MailboxBuilder>() {
            @Override
            public MailboxBuilder apply(ReplaySubject<Message> mailbox) {
                return new MailboxBuilder(mailbox);
            }
        };
    }

    @NonNull
    private Function<MailboxBuilder, MailboxBuilder> toFinalMailboxBuilder() {
        return new Function<MailboxBuilder, MailboxBuilder>() {
            @Override
            public MailboxBuilder apply(MailboxBuilder mailboxBuilder) {
                return mailboxBuilder.build();
            }
        };
    }

    @NonNull
    private Consumer<MailboxBuilder> addMailbox(final Object actor) {
        return new Consumer<MailboxBuilder>() {
            @Override
            public void accept(MailboxBuilder builder) {
                mailboxes.put(actor, builder.getMailbox());
            }
        };
    }

    @NonNull
    private Consumer<MailboxBuilder> addDisposable(final Object actor) {
        return new Consumer<MailboxBuilder>() {
            @Override
            public void accept(MailboxBuilder builder) {
                actorsDisposables.put(actor, builder.getActorDisposable());
            }
        };
    }

    @NonNull
    private Consumer<MailboxBuilder> clearMailboxBuilder() {
        return new Consumer<MailboxBuilder>() {
            @Override
            public void accept(MailboxBuilder builder) {
                builder.clear();
            }
        };
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
        register(actor, defaultMailboxBuilder(actor, actor.observeOnScheduler(),
                invokeActorOnMessageReceived(actor)));
    }

    @NonNull
    private Consumer<Message> invokeActorOnMessageReceived(@NonNull final Actor actor) {
        return new Consumer<Message>() {
            @Override
            public void accept(Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        };
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
                    .apply(invokeUnregister())
                    .apply(addTemporaryMailbox());
        }
    }

    @NonNull
    private Consumer<Object> invokeUnregister() {
        return new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                unregister(o);
            }
        };
    }

    @NonNull
    private Consumer<Object> addTemporaryMailbox() {
        return new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                mailboxes.put(o, ReplaySubject.<Message>create(MAILBOX_CAPACITY));
            }
        };
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

    private void doUnregisterClass(final Class<?> actor) {
        actorsInjector.clearFor(actor);
        mailboxes.getOrIgnore(actor)
                .doOnNext(invokeMailboxOnComplete())
                .doOnNext(removeMailboxByClass(actor))
                .flatMap(toActorDisposableObservable(actor))
                .doOnNext(removeDisposableByClass(actor))
                .defaultIfEmpty(dummyDisposable())
                .blockingSubscribe(invokeDisposeIfNotDisposed(), printStackTrace());


    }

    @NonNull
    private Consumer<ReplaySubject<Message>> invokeMailboxOnComplete() {
        return new Consumer<ReplaySubject<Message>>() {
            @Override
            public void accept(ReplaySubject<Message> mailbox) throws Exception {
                mailbox.onComplete();
            }
        };
    }

    @NonNull
    private Consumer<ReplaySubject<Message>> removeMailboxByClass(final Class<?> actor) {
        return new Consumer<ReplaySubject<Message>>() {
            @Override
            public void accept(ReplaySubject<Message> mailbox) {
                mailboxes.remove(actor);
            }
        };
    }

    @NonNull
    private Function<ReplaySubject<Message>, Observable<Disposable>>
    toActorDisposableObservable(final Class<?> actor) {
        return new Function<ReplaySubject<Message>, Observable<Disposable>>() {
            @Override
            public Observable<Disposable> apply(ReplaySubject<Message> mailbox) {
                return actorsDisposables.getOrIgnore(actor);
            }
        };
    }

    @NonNull
    private Consumer<Disposable> removeDisposableByClass(final Class<?> actor) {
        return new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                actorsDisposables.remove(actor);
            }
        };
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

    @NonNull
    private Consumer<Disposable> invokeDisposeIfNotDisposed() {
        return new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                disposeIfNotDisposed(disposable);
            }
        };
    }

    private void disposeIfNotDisposed(Disposable disposable) {
        if (!disposable.isDisposed()) disposable.dispose();
    }

    private void doUnregisterObject(final @NonNull Object actor) {
        actorsInjector.clearFor(actor);
        mailboxes.getOrIgnore(actor)
                .doOnSuccess(invokeMailboxOnComplete())
                .doOnSuccess(removeMailboxByObject(actor))
                .flatMap(toActorDisposableMaybe(actor))
                .doOnSuccess(removeDisposableByObject(actor))
                .defaultIfEmpty(dummyDisposable())
                .subscribe(invokeDisposeIfNotDisposed(), printStackTrace());


    }



    @NonNull
    private Consumer<ReplaySubject<Message>> removeMailboxByObject(final Object actor) {
        return new Consumer<ReplaySubject<Message>>() {
            @Override
            public void accept(ReplaySubject<Message> mailbox) {
                mailboxes.remove(actor);
            }
        };
    }

    @NonNull
    private Function<ReplaySubject<Message>, Maybe<Disposable>>
    toActorDisposableMaybe(final Object actor) {
        return new Function<ReplaySubject<Message>, Maybe<Disposable>>() {
            @Override
            public Maybe<Disposable> apply(ReplaySubject<Message> mailbox) {
                return actorsDisposables.getOrIgnore(actor);
            }
        };
    }

    @NonNull
    private Consumer<Disposable> removeDisposableByObject(final Object actor) {
        return new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                actorsDisposables.remove(actor);
            }
        };
    }

    TypedMap<ReplaySubject<Message>> getMailboxes() {
        return mailboxes;
    }

    TypedMap<Disposable> getActorsDisposables() {
        return actorsDisposables;
    }

    ActorsInjector getActorsInjector() {
        return actorsInjector;
    }
}
