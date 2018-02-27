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
        ActorSystemInstance actorSystem = ActorSystemInstance
                .getInstance("registerActorAndDisposableThroughRegisterWithConsumer");

        final TestActor actor = new TestActor();

        actorSystem.register(TestActor.class, Schedulers.trampoline(), new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        });

        assertTrue(actorSystem.getMailboxes().containsKey(TestActor.class).blockingGet()
                && !actorSystem.getActorsDisposables().get(TestActor.class).blockingFirst().isDisposed());

    }

    @Test
    public void registerActorAndDisposableThroughRegisterWithActor() throws Exception {
        ActorSystemInstance actorSystem = ActorSystemInstance
                .getInstance("registerActorAndDisposableThroughRegisterWithActor");

        final TestActorTwo actor = new TestActorTwo();

        actorSystem.register(actor);

        assertTrue(actorSystem.getMailboxes().containsKey(TestActorTwo.class).blockingGet()
                && !actorSystem.getActorsDisposables().get(TestActorTwo.class).blockingFirst().isDisposed());

    }

    @Test
    public void unregisterAlreadyRegisteredActorAndDisposable() throws Exception {
        ActorSystemInstance actorSystem = ActorSystemInstance
                .getInstance("unregisterAlreadyRegisteredActorAndDisposable");

        final TestActor actor = new TestActor();
        actorSystem.register(TestActor.class, Schedulers.trampoline(), new Consumer<Message>() {
            @Override
            public void accept(@NonNull Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        });

        Disposable disposable = actorSystem.getActorsDisposables().get(TestActor.class).blockingFirst();
        actorSystem.unregister(TestActor.class);

        assertTrue(!actorSystem.getMailboxes().containsKey(TestActor.class).blockingGet()
                && !actorSystem.getActorsDisposables().containsKey(TestActor.class).blockingGet()
                && disposable.isDisposed());

    }

    @Test
    public void unregisterNonRegisteredActorAndDoNotCrash() throws Exception {
        ActorSystemInstance
                .getInstance("unregisterNonRegisteredActorAndDoNotCrash")
                .unregister(TestActorFive.class);
    }

    @Test
    public void sendMessageAndReceiveItInActorsOnMessageReceivedConsumer() throws Exception {
        ActorSystemInstance actorSystem = ActorSystemInstance
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
        ActorSystemInstance actorSystem = ActorSystemInstance
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
        ActorSystemInstance actorSystem = ActorSystemInstance
                .getInstance("sendMessageWithoutPassingActorClassesThenCrash");
        actorSystem.send(new Message(1));
    }

    @Test
    public void registerThenUnregisterActorThroughActorParameterMethodsSuccessfully() {
        ActorSystemInstance actorSystem = ActorSystemInstance
                .getInstance("registerThenUnregisterActorThroughActorParameterMethodsSuccessfully");
        TestActorThree actor = new TestActorThree();
        actorSystem.register(actor);
        actorSystem.send(new Message(1), TestActorThree.class);
        actorSystem.unregister(actor);
        assertTrue(actor.message.getId() == 1);

    }


    @Test
    public void removeOldActorAfterRegisteringNewActorThenKeepNewActor() {

        ActorSystemInstance actorSystem = ActorSystemInstance
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
        ActorSystemInstance actorSystem = ActorSystemInstance
                .getInstance("postponeAndRegisterAgainThenReceivePostponedMessages");

        Message message = new Message(1);
        TestActor actor = new TestActor();
        actorSystem.register(actor);
        actorSystem.postpone(actor);
        actorSystem.send(message, TestActor.class);
        actorSystem.register(actor);

        assertEquals(1, actor.message.getId());
    }

    @Test
    public void postponeAndUnregisterThenDoNotReceiveMessage() {
        ActorSystemInstance actorSystem = ActorSystemInstance
                .getInstance("postponeAndUnregisterThenDropPostponedMessages");

        Message message = new Message(1);
        TestActor actor = new TestActor();
        actorSystem.register(actor);
        actorSystem.postpone(actor);
        actorSystem.send(message, TestActor.class);
        actorSystem.unregister(actor);

        assertNull(actor.message);
    }

    @Test
    public void postponeAndUnregisterAndRegisterThenDropPostponedMessage() {
        ActorSystemInstance actorSystem = ActorSystemInstance
                .getInstance("postponeAndUnregisterAndRegisterThenReceivePostponedMessages");

        Message message = new Message(1);
        TestActor actor = new TestActor();
        actorSystem.register(actor);
        actorSystem.postpone(actor);
        actorSystem.send(message, TestActor.class);
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