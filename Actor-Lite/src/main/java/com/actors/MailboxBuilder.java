package com.actors;

import android.os.Looper;
import android.support.annotation.NonNull;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;

/**
 * a class that is responsible for building Mailbox to be used in the {@link ActorSystem},
 * notice that {@link #onMessageReceived} is mandatory or {@link UnsupportedOperationException}
 * will be thrown
 * <p>
 * to select which thread this mailbox
 * should receive it's messages, there is {@link #observeOn(Scheduler)} or
 * {@link #observeOn(Looper)}
 * <p>
 * Created by Ahmed Adel Ismail on 5/3/2017.
 */
public class MailboxBuilder {

    private ReplaySubject<Message> mailbox;
    private Scheduler actorScheduler;
    private Consumer<Message> onMessageReceived;
    private Action onMailboxClosed;
    private Consumer<Throwable> onMessageError;
    private Disposable actorDisposable;

    MailboxBuilder(@NonNull ReplaySubject<Message> mailbox) {
        this.mailbox = mailbox;
        this.actorScheduler = Schedulers.computation();
        this.onMailboxClosed = doNothing();
        this.onMessageError = printStackTrace();
    }

    @NonNull
    private Action doNothing() {
        return new Action() {
            @Override
            public void run() throws Exception {

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
     * set the {@link Scheduler} that will host the invocation of
     * {@link #onMessageReceived(Consumer)}
     *
     * @param scheduler the {@link Scheduler} of the Observer
     * @return {@code this} instance for chaining
     */
    public MailboxBuilder observeOn(Scheduler scheduler) {
        this.actorScheduler = scheduler;
        return this;
    }

    /**
     * set the{@link Looper} that will create a  {@link Scheduler} to host the invocation of
     * {@link #onMessageReceived(Consumer)}
     *
     * @param looper the {@link Looper} of the Observer's {@link Scheduler}
     * @return {@code this} instance for chaining
     */
    public MailboxBuilder observeOn(Looper looper) {
        this.actorScheduler = AndroidSchedulers.from(looper);
        return this;
    }

    /**
     * set the {@link Consumer} function that will be invoked when a {@link Message} is received
     *
     * @param onMessageReceived the {@link Consumer} function
     * @return {@code this} instance for chaining
     */
    public MailboxBuilder onMessageReceived(Consumer<Message> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
        return this;
    }


    /**
     * set the {@link Action} function that will be executed when the mailbox is closing, in other
     * words, when the {@link Subject} calls {@link Subject#onComplete()}
     *
     * @param onMailboxClosed the {@link Action} to be done
     * @return {@code this} instance for chaining
     */
    public MailboxBuilder onMailboxClosed(Action onMailboxClosed) {
        this.onMailboxClosed = onMailboxClosed;
        return this;
    }


    /**
     * set the {@link Consumer} function that will be executed when the mailbox faces an error,
     * in other words, when the {@link Subject} calls {@link Subject#onError(Throwable)} }
     *
     * @param onMessageError the {@link Consumer} to handle the {@link Exception}
     * @return {@code this} instance for chaining
     */
    public MailboxBuilder onMessagingError(Consumer<Throwable> onMessageError) {
        this.onMessageError = onMessageError;
        return this;
    }

    MailboxBuilder build() throws UnsupportedOperationException {

        if (onMessageReceived == null) {
            throw new UnsupportedOperationException("onMessageReceived() should be set");
        }

        actorDisposable = mailbox.observeOn(actorScheduler)
                .doFinally(onMailboxClosed)
                .subscribe(onMessageReceived, onMessageError, doNothing());

        return this;
    }

    ReplaySubject<Message> getMailbox() {
        return mailbox;
    }

    Disposable getActorDisposable() {
        return actorDisposable;
    }

    void clear() {
        mailbox = null;
        actorScheduler = null;
        onMessageReceived = null;
        onMailboxClosed = null;
        onMessageError = null;
        actorDisposable = null;
    }

}
