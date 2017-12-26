package com.actors;

import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.properties.BooleanProperty;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ahmed Adel Ismail on 5/17/2017.
 */
@SuppressWarnings("deprecation")
@RunWith(ParallelRunner.class)
public class ActorSystemTestConcurrencyTest {

    @Test
    public void sendMessageAndObserveOnDifferentThread() throws Exception {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("sendMessageAndObserveOnDifferentThread");
        BooleanProperty differentThread = new TestAsync<BooleanProperty>().apply(new Function<CountDownLatch, BooleanProperty>() {
            @Override
            public BooleanProperty apply(CountDownLatch countDownLatch) throws Exception {
                final BooleanProperty result = new BooleanProperty(false);
                actorSystem.register(ActorSystemTestOne.class, Schedulers.newThread(), compareThreads(countDownLatch, result));
                actorSystem.send(new Message(0, Thread.currentThread().getId()), ActorSystemTestOne.class);
                return result;
            }
        });
        actorSystem.unregister(ActorSystemTestOne.class);
        assertTrue(differentThread.isTrue());

    }

    @NonNull
    private Consumer<Message> compareThreads(final CountDownLatch countDownLatch, final BooleanProperty result) {
        return new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
                long threadId = message.getContent();
                result.set(threadId != Thread.currentThread().getId());
                countDownLatch.countDown();
            }
        };
    }

    @Test
    public void sendMessageAndObserveOnSameThread() throws Exception {

        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("sendMessageAndObserveOnSameThread");

        final BooleanProperty result = new BooleanProperty(false);
        actorSystem.register(ActorSystemTestTwo.class, Schedulers.trampoline(), compareThreads(result));
        actorSystem.send(new Message(0, Thread.currentThread().getId()), ActorSystemTestTwo.class);
        actorSystem.unregister(ActorSystemTestTwo.class);
        assertTrue(result.isTrue());
    }

    @NonNull
    private Consumer<Message> compareThreads(final BooleanProperty result) {
        return new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
                long threadId = message.getContent();
                result.set(threadId == Thread.currentThread().getId());
            }
        };
    }

}

class ActorSystemTestOne implements Actor {

    @Override
    public void onMessageReceived(Message message) {

    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.trampoline();
    }
}

class ActorSystemTestTwo extends ActorSystemTestOne {

}