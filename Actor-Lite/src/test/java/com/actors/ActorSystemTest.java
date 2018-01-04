package com.actors;

import org.junit.Test;

import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ahmed Adel Ismail on 5/17/2017.
 */
public class ActorSystemTest {


    @Test
    public void registerActorAndDisposableThroughRegisterWithConsumer() throws Exception {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("registerActorAndDisposableThroughRegisterWithConsumer");

        final TestActor actor = new TestActor();

        actorSystem.register(TestActor.class, Schedulers.trampoline(), new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        });

        assertTrue(actorSystem.mailboxes.containsKey(TestActor.class).blockingGet()
                && !actorSystem.actorsDisposables.get(TestActor.class).blockingFirst().isDisposed());

    }

    @Test
    public void registerActorAndDisposableThroughRegisterWithActor() throws Exception {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("registerActorAndDisposableThroughRegisterWithActor");

        final TestActorTwo actor = new TestActorTwo();

        actorSystem.register(actor);

        assertTrue(actorSystem.mailboxes.containsKey(TestActorTwo.class).blockingGet()
                && !actorSystem.actorsDisposables.get(TestActorTwo.class).blockingFirst().isDisposed());

    }

    @Test
    public void unregisterAlreadyRegisteredActorAndDisposable() throws Exception {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("unregisterAlreadyRegisteredActorAndDisposable");

        final TestActor actor = new TestActor();
        actorSystem.register(TestActor.class, Schedulers.trampoline(), new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        });

        Disposable disposable = actorSystem.actorsDisposables.get(TestActor.class).blockingFirst();
        actorSystem.unregister(TestActor.class);

        assertTrue(!actorSystem.mailboxes.containsKey(TestActor.class).blockingGet()
                && !actorSystem.actorsDisposables.containsKey(TestActor.class).blockingGet()
                && disposable.isDisposed());

    }

    @Test
    public void unregisterNonRegisteredActorAndDoNotCrash() throws Exception {
        ActorSystemImpl
                .getInstance("unregisterNonRegisteredActorAndDoNotCrash")
                .unregister(TestActorFive.class);
    }

    @Test
    public void sendMessageAndReceiveItInActorsOnMessageReceivedConsumer() throws Exception {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("sendMessageAndReceiveItInActorsOnMessageReceivedConsumer");

        final TestActor actor = new TestActor();
        actorSystem.register(TestActor.class, Schedulers.trampoline(), new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        });

        actorSystem.send(new Message(1), TestActor.class);

        assertTrue(actor.message != null);

    }

    @Test
    public void sendMessageIdAndReceiveItInActorsOnMessageReceivedConsumer() throws Exception {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("sendMessageIdAndReceiveItInActorsOnMessageReceivedConsumer");

        final TestActor actor = new TestActor();
        actorSystem.register(TestActor.class, Schedulers.trampoline(), new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        });

        actorSystem.send(1, TestActor.class);

        assertTrue(actor.message != null);

    }

    @Test(expected = UnsupportedOperationException.class)
    public void sendMessageWithoutPassingActorClassesThenCrash() throws Exception {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("sendMessageWithoutPassingActorClassesThenCrash");
        actorSystem.send(new Message(1));
    }

    @Test
    public void registerThenUnregisterActorThroughActorParameterMethodsSuccessfully() {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("registerThenUnregisterActorThroughActorParameterMethodsSuccessfully");
        TestActorThree actor = new TestActorThree();
        actorSystem.register(actor);
        actorSystem.send(new Message(1), TestActorThree.class);
        actorSystem.unregister(actor);
        assertTrue(actor.message.getId() == 1);

    }


    @Test
    public void removeOldActorAfterRegisteringNewActorThenKeepNewActor() {

        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("removeOldActorAfterRegisteringNewActorThenKeepNewActor");

        TestActorFour oldActor = new TestActorFour();
        TestActorFour newActor = new TestActorFour();

        actorSystem.register(oldActor);
        actorSystem.register(newActor);
        actorSystem.unregister(oldActor);

        actorSystem.send(1, TestActorFour.class);
        assertTrue(newActor.message.getId() == 1);
    }

    @Test
    public void postponeAndRegisterAgainThenReceivePostponedMessages() {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("postponeAndRegisterAgainThenReceivePostponedMessages");

        Message message = new Message(1);
        TestActor actor = new TestActor();
        actorSystem.register(actor);
        actorSystem.postpone(actor);
        actorSystem.send(message,TestActor.class);
        actorSystem.register(actor);

        assertEquals(1, actor.message.getId());
    }

    @Test
    public void postponeAndUnregisterThenDoNotReceiveMessage() {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("postponeAndUnregisterThenDropPostponedMessages");

        Message message = new Message(1);
        TestActor actor = new TestActor();
        actorSystem.register(actor);
        actorSystem.postpone(actor);
        actorSystem.send(message,TestActor.class);
        actorSystem.unregister(actor);

        assertNull(actor.message);
    }

    @Test
    public void postponeAndUnregisterAndRegisterThenDropPostponedMessage() {
        ActorSystemImpl actorSystem = ActorSystemImpl
                .getInstance("postponeAndUnregisterAndRegisterThenReceivePostponedMessages");

        Message message = new Message(1);
        TestActor actor = new TestActor();
        actorSystem.register(actor);
        actorSystem.postpone(actor);
        actorSystem.send(message,TestActor.class);
        actorSystem.unregister(actor);
        actorSystem.register(actor);

        assertNull(actor.message);
    }

    private static class TestActor implements Actor {

        Message message;

        @Override
        public void onMessageReceived(Message message) {
            this.message = message;
        }

        @Override
        public Scheduler observeOnScheduler() {
            return Schedulers.trampoline();
        }
    }

    private static class TestActorTwo extends TestActor {
    }

    private static class TestActorThree extends TestActor {
    }

    private static class TestActorFour extends TestActor {
    }

    private static class TestActorFive extends TestActor {
    }

}