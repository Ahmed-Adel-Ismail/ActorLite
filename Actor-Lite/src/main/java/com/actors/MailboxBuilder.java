package com.actors;

import android.os.Looper;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
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
 * the default mailbox ehavior is a {@link PublishSubject}, you can change this
 * by invoking {@link #mailbox(Subject)}
 * <p>
 * Created by Ahmed Adel Ismail on 5/3/2017.
 */
public class MailboxBuilder {

    private Subject<Message> mailbox;
    private Scheduler actorScheduler;
    private Consumer<Message> onMessageReceived;
    private Action onMailboxClosed;
    private Consumer<Throwable> onMessageError;
    private Disposable actorDisposable;

    MailboxBuilder(Object actor) {
        this.actorScheduler = Schedulers.trampoline();
        this.onMailboxClosed = () -> {};
        this.onMessageError = Throwable::printStackTrace;
    }


    /**
     * set the {@link Subject} that will operate as a Mailbox
     *
     * @param mailbox the {@link Subject} instance
     * @return {@code this} instance for chaining
     */
    public MailboxBuilder mailbox(Subject<Message> mailbox) {
        this.mailbox = mailbox;
        return this;
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

        if (mailbox == null) {
            mailbox = PublishSubject.create();
        }

        actorDisposable = mailbox.observeOn(actorScheduler)
                .subscribe(onMessageReceived, onMessageError, onMailboxClosed);

        return this;
    }

    Subject<Message> getMailbox() {
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
