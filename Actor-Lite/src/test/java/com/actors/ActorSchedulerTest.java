package com.actors;


import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.properties.Property;

import static org.junit.Assert.assertTrue;

@SuppressWarnings("deprecation")
public class ActorSchedulerTest {

    private final Message message = new Message(1);

    @Test
    public void scheduleMessageToRegisteredActorAfterDelay() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("removeOldActorAfterRegisteringNewActorThenKeepNewActor");
        MockActor actor = new TestAsync<MockActor>().apply(new Function<CountDownLatch, MockActor>() {
            @Override
            public MockActor apply(@NonNull CountDownLatch countDownLatch) {
                MockActor actor = new MockActor(countDownLatch);
                actorSystem.register(actor);
                ActorScheduler.after(10, actorSystem).send(message, MockActor.class);
                return actor;
            }
        });
        actorSystem.unregister(MockActor.class);
        assertTrue(actor.message.getId() == 1);
    }

    @Test
    public void scheduleMessageIdToRegisteredActorAfterDelay() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("scheduleMessageIdToRegisteredActorAfterDelay");
        MockActor actor = new TestAsync<MockActor>().apply(new Function<CountDownLatch, MockActor>() {
            @Override
            public MockActor apply(@NonNull CountDownLatch countDownLatch) {
                MockActor actor = new MockActorEleven(countDownLatch);
                actorSystem.register(actor);
                ActorScheduler.after(10, actorSystem).send(1, MockActorEleven.class);
                return actor;
            }
        });
        actorSystem.unregister(MockActorEleven.class);
        assertTrue(actor.message.getId() == 1);
    }

    @Test
    public void cancelScheduledMessageToRegisteredActorSuccessfully() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("cancelScheduledMessageToRegisteredActorSuccessfully");
        Tuple<MockActor, Disposable> tuple = new TestAsync<Tuple<MockActor, Disposable>>(1000).apply(new Function<CountDownLatch, Tuple<MockActor, Disposable>>() {
            @Override
            public Tuple<MockActor, Disposable> apply(@NonNull CountDownLatch countDownLatch) {
                MockActorFour actor = new MockActorFour(countDownLatch);
                actorSystem.register(actor);
                Disposable d = ActorScheduler.after(100, actorSystem)
                        .send(message, MockActorFour.class).cancel();
                return Tuple.from(actor, d);
            }
        });
        actorSystem.unregister(MockActorFour.class);
        assertTrue(tuple.first().message == null && tuple.second().isDisposed());
    }

    @Test
    public void cancelScheduledMessageToUnRegisteredActorSuccessfully() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("cancelScheduledMessageToUnRegisteredActorSuccessfully");
        Property<Tuple<MockActor, Cancellable>> tuple = new Property<>();

        new TestAsync<Void>(1000).apply(new Function<CountDownLatch, Void>() {
            @Override
            public Void apply(@NonNull CountDownLatch countDownLatch) throws Exception {
                MockActorFive actor = new MockActorFive(countDownLatch);
                actorSystem.register(actor);
                Cancellable c = ActorScheduler.after(100, actorSystem).send(message, MockActorFive.class);
                actorSystem.unregister(MockActorFive.class);
                tuple.set(Tuple.from(actor, c));
                return null;
            }
        });
        Disposable d = tuple.get().second().cancel();
        assertTrue(tuple.get().first().message == null && d == null);

    }

    @Test
    public void cancelScheduledMessageAfterBeingReceivedAndDoNotCrash() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("cancelScheduledMessageAfterBeingReceivedAndDoNotCrash");
        Tuple<MockActor, Cancellable> tuple = new TestAsync<Tuple<MockActor, Cancellable>>().apply(new Function<CountDownLatch, Tuple<MockActor, Cancellable>>() {
            @Override
            public Tuple<MockActor, Cancellable> apply(@NonNull CountDownLatch countDownLatch) throws Exception {
                MockActorSix actor = new MockActorSix(countDownLatch);
                actorSystem.register(actor);
                Cancellable c = ActorScheduler.after(10, actorSystem)
                        .send(message, MockActorSix.class);
                return Tuple.from(actor, c);
            }
        });
        actorSystem.unregister(MockActorSix.class);
        Disposable d = tuple.second().cancel();
        assertTrue(tuple.first().message.getId() == 1 && d == null);
    }

    @Test
    public void cancelAllScheduledMessagesAndDisposablesForActorAfterSendingMessage() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("cancelAllScheduledMessagesAndDisposablesForActorAfterSendingMessage");
        MockActor actor = new TestAsync<MockActor>(1, TimeUnit.SECONDS).apply(new Function<CountDownLatch, MockActor>() {
            @Override
            public MockActor apply(@NonNull CountDownLatch countDownLatch) {
                MockActorSeven actor = new MockActorSeven(countDownLatch);
                actorSystem.register(actor);
                ActorScheduler.after(100, actorSystem).send(message, MockActorSeven.class);
                ActorScheduler.cancel(MockActorSeven.class);
                return actor;
            }
        });
        actorSystem.unregister(MockActorSeven.class);
        assertTrue(actor.message == null && ActorScheduler.schedules.get(MockActorSeven.class) == null);
    }

    @Test
    public void cancelAllScheduledMessagesAndDisposablesForActorBeforeSendingMessage() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("cancelAllScheduledMessagesAndDisposablesForActorBeforeSendingMessage");
        MockActor actor = new TestAsync<MockActor>(1, TimeUnit.SECONDS).apply(new Function<CountDownLatch, MockActor>() {
            @Override
            public MockActor apply(@NonNull CountDownLatch countDownLatch) {
                MockActorEight actor = new MockActorEight(countDownLatch);
                actorSystem.register(actor);
                ActorScheduler.after(100, actorSystem).send(message, MockActorEight.class);
                ActorScheduler.cancel(MockActorEight.class);
                actorSystem.unregister(MockActorEight.class);
                assertTrue(ActorScheduler.schedules.get(MockActorEight.class) == null);
                return actor;
            }
        });
        assertTrue(actor.message == null);
    }

    @Test
    public void disposeAllScheduledDisposablesAfterSendingMessageToRegisteredActor() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("disposeAllScheduledDisposablesAfterSendingMessageToRegisteredActor");
        new TestAsync<Void>().apply(new Function<CountDownLatch, Void>() {
            @Override
            public Void apply(@NonNull CountDownLatch countDownLatch) {
                MockActorTwo actor = new MockActorTwo(countDownLatch);
                actorSystem.register(actor);
                ActorScheduler.after(10, actorSystem).send(message, MockActorTwo.class);
                return null;
            }
        });
        actorSystem.unregister(MockActorTwo.class);
        assertTrue(ActorScheduler.schedules.get(MockActorTwo.class) == null);
    }

    @Test
    public void scheduleMessageToRegisteredCrashingActorAndNotAffected() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("scheduleMessageToRegisteredCrashingActorAndNotAffected");
        new TestAsync<Void>().apply(new Function<CountDownLatch, Void>() {
            @Override
            public Void apply(@NonNull CountDownLatch countDownLatch) {
                MockActorThree actor = new MockActorThree(countDownLatch);
                actorSystem.register(actor);
                actor.exception = new UnsupportedOperationException();
                ActorScheduler.after(10, actorSystem).send(message, MockActorThree.class);
                return null;
            }
        });
        actorSystem.unregister(MockActorThree.class);
        assertTrue(true); // no crash occurred
    }

    @Test
    public void scheduleMessageToNotRegisteredActorAndNotCrash() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("scheduleMessageToNotRegisteredActorAndNotCrash");
        new TestAsync<Void>(1, TimeUnit.SECONDS).apply(new Function<CountDownLatch, Void>() {
            @Override
            public Void apply(@NonNull CountDownLatch countDownLatch) {
                ActorScheduler.after(10, actorSystem).send(message, Actor.class);
                return null;
            }
        });
        assertTrue(true);
    }

    @Test
    public void attemptToSendTheSameMessageTwiceBeforeTheFirstOneReceivedAndIgnoreTheSecond() throws Exception {
        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("attemptToSendTheSameMessageTwiceBeforeTheFirstOneReceivedAndIgnoreTheSecond");
        MockActor actor = new TestAsync<MockActor>().apply(new Function<CountDownLatch, MockActor>() {
            @Override
            public MockActor apply(@NonNull CountDownLatch countDownLatch) {
                Message message = new Message(1, 1);
                Message messageTwo = new Message(1, 2);
                MockActorNine actor = new MockActorNine(countDownLatch);
                actorSystem.register(actor);
                ActorScheduler.after(50, actorSystem).send(message, MockActorNine.class);
                ActorScheduler.after(20, actorSystem).send(messageTwo, MockActorNine.class);
                return actor;
            }
        });
        actorSystem.unregister(MockActorNine.class);
        assertTrue(actor.message.getContent().equals(1));
    }

    @Test
    public void attemptToSendTheSameMessageTwiceAfterTheFirstOneReceivedAndAcceptBoth() throws Exception {

        final ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("attemptToSendTheSameMessageTwiceAfterTheFirstOneReceivedAndAcceptBoth");

        final MockActorTen actor = new MockActorTen();

        new TestAsync<Void>().apply(new Function<CountDownLatch, Void>() {
            @Override
            public Void apply(@NonNull CountDownLatch countDownLatch) throws Exception {

                actor.setCountDownLatch(countDownLatch);
                actorSystem.register(actor);
                ActorScheduler.after(10, actorSystem).send(new Message(1, 1), MockActorTen.class);
                return null;
            }
        });


        new TestAsync<Void>().apply(new Function<CountDownLatch, Void>() {
            @Override
            public Void apply(@NonNull CountDownLatch countDownLatch) throws Exception {
                actor.setCountDownLatch(countDownLatch);
                ActorScheduler.after(10, actorSystem).send(new Message(1, 2), MockActorTen.class);
                return null;
            }
        });


        actorSystem.unregister(MockActorTen.class);
        assertTrue(actor.message.getContent().equals(2));
    }

}