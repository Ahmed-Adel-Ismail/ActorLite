package com.actors.testing;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.ActorSystem;
import com.actors.ActorSystemInstance;
import com.actors.Message;
import com.actors.OnActorUnregistered;
import com.actors.annotations.Spawn;

import org.junit.Test;

import io.reactivex.Scheduler;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.*;

public class ActorsTestRunnerTest {

    @Test
    public void prepareWithCaptureUpdateThenReturnPreparedResult() throws Exception {

        int result = ActorsTestRunner.testActor(TargetActor.class)
                .captureUpdate(new Function<TargetActor, Integer>() {
                    @Override
                    public Integer apply(TargetActor actor) throws Exception {
                        return actor.id;
                    }
                })
                .prepare(new Consumer<TargetActor>() {
                    @Override
                    public void accept(TargetActor actor) throws Exception {
                        actor.id = 1;
                    }
                })
                .sendMessage(10)
                .getUpdate();

        assertEquals(1, result);

    }

    @Test
    public void prepareWithCaptureReplyThenReturnPreparedResult() throws Exception {

        int result = ActorsTestRunner.testActor(TargetActor.class)
                .whenReplyToAddress(CallbackActor.class)
                .captureReply(new Function<Message, Integer>() {
                    @Override
                    public Integer apply(Message message) throws Exception {
                        return message.getId();
                    }
                })
                .prepare(new Consumer<TargetActor>() {
                    @Override
                    public void accept(TargetActor actor) throws Exception {
                        actor.id = 30;
                    }
                })
                .sendMessage(5)
                .getReply();

        assertEquals(30, result);

    }


    @Test
    public void getReplyWithNoMockingThenReturnInValidCommunication() throws Exception {
        Integer result = ActorsTestRunner.testActor(TargetActor.class)
                .whenReplyToAddress(CallbackActor.class)
                .captureReply(new Function<Message, Integer>() {
                    @Override
                    public Integer apply(Message message) throws Exception {
                        System.out.println("MOCKED CALLBACK : " + message.getId());
                        return message.getId();
                    }
                })
                .sendMessage(1)
                .getReply();

        assertNull(result);
    }

    @Test
    public void getReplyWithWithMockingThenReturnValidMockedCommunication() throws Exception {
        int result = ActorsTestRunner.testActor(TargetActor.class)
                .whenReplyToAddress(CallbackActor.class)
                .captureReply(new Function<Message, Integer>() {
                    @Override
                    public Integer apply(Message message) throws Exception {
                        System.out.println("MOCKED CALLBACK : " + message.getId());
                        return message.getId();
                    }
                })
                .mock(DependencyActor.class, new BiConsumer<ActorSystemInstance, Message>() {
                    @Override
                    public void accept(ActorSystemInstance actorSystemInstance, Message message) throws Exception {
                        System.out.println("MOCKED DEPENDENCY : " + message.getId());
                        actorSystemInstance.send(3, TargetActor.class);
                    }
                })
                .sendMessage(1)
                .getReply();

        assertEquals(3, result);
    }


    @Test
    public void getUpdateWithWithNoMocksThenUpdateTheActorWithTheReceivedMessage() throws Exception {
        int result = ActorsTestRunner.testActor(TargetActor.class)
                .captureUpdate(new Function<TargetActor, Integer>() {
                    @Override
                    public Integer apply(TargetActor actor) throws Exception {
                        System.out.println("UPDATED TargetActor : " + actor.message.getId());
                        return actor.message.getId();
                    }
                })
                .sendMessage(1)
                .getUpdate();

        assertEquals(1, result);
    }

    @Test
    public void getUpdateWithWithMockingThenUpdateTheActorWithTheMockedMessage() throws Exception {
        int result = ActorsTestRunner.testActor(TargetActor.class)
                .captureUpdate(new Function<TargetActor, Integer>() {
                    @Override
                    public Integer apply(TargetActor actor) throws Exception {
                        System.out.println("UPDATED TargetActor : " + actor.message.getId());
                        return actor.message.getId();
                    }
                })
                .mock(DependencyActor.class, new BiConsumer<ActorSystemInstance, Message>() {
                    @Override
                    public void accept(ActorSystemInstance actorSystemInstance, Message message) throws Exception {
                        System.out.println("MOCKED DEPENDENCY : " + message.getId());
                        actorSystemInstance.send(3, TargetActor.class);
                    }
                })
                .sendMessage(1)
                .getUpdate();

        assertEquals(3, result);
    }

    @Test
    public void captureReplyWithNoMockingThenReturnInValidCommunication() throws Exception {
        ActorsTestRunner.testActor(TargetActor.class)
                .whenReplyToAddress(CallbackActor.class)
                .captureReply(new Function<Message, Integer>() {
                    @Override
                    public Integer apply(Message message) throws Exception {
                        System.out.println("MOCKED CALLBACK : " + message.getId());
                        return message.getId();
                    }
                })
                .sendMessage(1)
                .withContent("")
                .assertReply(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        assertNull(integer);
                    }
                });
    }

    @Test
    public void captureReplyWithWithMockingThenReturnValidMockedCommunication() throws Exception {
        ActorsTestRunner.testActor(TargetActor.class)
                .whenReplyToAddress(CallbackActor.class)
                .captureReply(new Function<Message, Integer>() {
                    @Override
                    public Integer apply(Message message) throws Exception {
                        System.out.println("MOCKED CALLBACK : " + message.getId());
                        return message.getId();
                    }
                })
                .mock(DependencyActor.class, new BiConsumer<ActorSystemInstance, Message>() {
                    @Override
                    public void accept(ActorSystemInstance actorSystemInstance, Message message) throws Exception {
                        System.out.println("MOCKED DEPENDENCY : " + message.getId());
                        actorSystemInstance.send(3, TargetActor.class);
                    }
                })
                .sendMessage(1)
                .assertReply(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        assertTrue(integer.equals(3));
                    }
                });
    }


    @Test
    public void captureUpdateWithWithNoMocksThenUpdateTheActorWithTheReceivedMessage() throws Exception {
        ActorsTestRunner.testActor(TargetActor.class)
                .captureUpdate(new Function<TargetActor, Integer>() {
                    @Override
                    public Integer apply(TargetActor actor) throws Exception {
                        System.out.println("UPDATED TargetActor : " + actor.message.getId());
                        return actor.message.getId();
                    }
                })
                .sendMessage(1)
                .assertUpdated(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        assertTrue(integer.equals(1));
                    }
                });
    }

    @Test
    public void captureUpdateWithWithMockingThenUpdateTheActorWithTheMockedMessage() throws Exception {
        ActorsTestRunner.testActor(TargetActor.class)
                .captureUpdate(new Function<TargetActor, Integer>() {
                    @Override
                    public Integer apply(TargetActor actor) throws Exception {
                        System.out.println("UPDATED TargetActor : " + actor.message.getId());
                        return actor.message.getId();
                    }
                })
                .mock(DependencyActor.class, new BiConsumer<ActorSystemInstance, Message>() {
                    @Override
                    public void accept(ActorSystemInstance actorSystemInstance, Message message) throws Exception {
                        System.out.println("MOCKED DEPENDENCY : " + message.getId());
                        actorSystemInstance.send(3, TargetActor.class);
                    }
                })
                .sendMessage(1)
                .assertUpdated(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        assertTrue(integer.equals(3));
                    }
                });
    }


}


class CallbackActor implements Actor, OnActorUnregistered {

    private final TargetActor targetActor;

    public CallbackActor() {
        this.targetActor = new TargetActor();
        ActorSystem.register(targetActor);

    }

    @Override
    public void onMessageReceived(Message message) {
        System.out.println("CallbackActor : " + message.getId());
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.single();
    }

    @Override
    public void onUnregister() {
        ActorSystem.unregister(targetActor);
    }
}


@Spawn({DependencyActor.class, DependencyTwoActor.class})
class TargetActor implements Actor {

    int id;
    Message message;

    public TargetActor() {
    }

    @Override
    public void onMessageReceived(Message message) {
        this.message = message;
        System.out.println("TargetActor : " + message.getId());
        if (message.getId() == 1) {
            ActorSystem.send(1, DependencyActor.class, DependencyTwoActor.class);
        } else if (message.getId() == 5) {
            ActorSystem.send(id, CallbackActor.class);
        } else {
            ActorSystem.send(message.getId(), CallbackActor.class);
        }

    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.io();
    }
}


class DependencyActor implements Actor {

    public DependencyActor() {
    }

    @Override
    public void onMessageReceived(Message message) {
        System.out.println("DependencyActor : " + message.getId());
        ActorSystem.send(2, TargetActor.class);
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.computation();
    }
}

class DependencyTwoActor implements Actor {

    public DependencyTwoActor() {
    }

    @Override
    public void onMessageReceived(Message message) {
        System.out.println("DependencyTwoActor : " + message.getId());
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.computation();
    }
}