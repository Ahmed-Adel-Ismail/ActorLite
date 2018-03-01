package com.actors;

import android.support.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * a class that handles sending a scheduled {@link Message}
 */
public class ScheduledMessageSender {

    private final long delayMillis;
    private final ActorSystemInstance actorSystem;

    ScheduledMessageSender(long delayMillis, ActorSystemInstance actorSystem) {
        this.delayMillis = delayMillis;
        this.actorSystem = actorSystem;
    }

    /**
     * send a {@link Message} to the passed Actor after the given delay
     *
     * @param message      the {@link Message} to send
     * @param actorAddress the Actor class to receive the message
     * @return a {@link Cancellable} to cancel this {@link Message} if required
     */
    public Cancellable send(@NonNull Message message, @NonNull final Class<?> actorAddress) {
        synchronized (ActorScheduler.lock) {
            return doSend(message, actorAddress);
        }
    }

    /**
     * send an empty {@link Message} to the passed Actor after the given delay, the
     * {@link Message} will only contain an id that can be accessed through {@link Message#getId()}
     *
     * @param messageId    the id that will be set to the created {@link Message} and will be
     *                     accessed through {@link Message#getId()}
     * @param actorAddress the Actor class to receive the message
     * @return a {@link Cancellable} to cancel this {@link Message} if required
     */
    public Cancellable send(int messageId, @NonNull final Class<?> actorAddress) {
        return send(new Message(messageId), actorAddress);
    }

    @NonNull
    private Cancellable doSend(Message message, Class<?> actorAddress) {
        int id = message.getId();
        DisposablesGroup disposables = ActorScheduler.getNonNullDisposableGroup(actorAddress);
        if (disposables.containsKey(id)) {
            return logDuplicateMessageAndReturnItsCancellable(actorAddress, id);
        }
        disposables.put(id, sendAfterDelay(id, message, actorAddress));
        ActorScheduler.schedules.put(actorAddress, disposables);
        return new Cancellable(actorAddress, id);
    }

    @NonNull
    private Cancellable logDuplicateMessageAndReturnItsCancellable(
            Class<?> actorAddress, int id) {
        return new Cancellable(actorAddress, id);
    }

    private Disposable sendAfterDelay(int id, Message message, Class<?> actorAddress) {
        return Single.just(message)
                .delay(delayMillis, TimeUnit.MILLISECONDS)
                .subscribe(sendFromActorSystemAndDispose(id, actorAddress));
    }

    @NonNull
    private Consumer<Message> sendFromActorSystemAndDispose(
            final int id, final Class<?> actorAddress) {
        return new Consumer<Message>() {
            @Override
            public void accept(Message message) throws Exception {
                synchronized (ActorScheduler.lock) {
                    doSendFromActorSystemAndDispose(message, actorAddress, id);
                }
            }
        };
    }

    private void doSendFromActorSystemAndDispose(Message message, Class<?> actorAddress, int id) {
        actorSystem.send(message, actorAddress);
        DisposablesGroup disposables = ActorScheduler.getNonNullDisposableGroup(actorAddress);
        disposables.remove(id);
        if (disposables.isEmpty()) {
            ActorScheduler.schedules.remove(actorAddress);
        }
    }


}
