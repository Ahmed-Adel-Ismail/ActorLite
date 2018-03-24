package com.actors.actorlite;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.ActorSystem;
import com.actors.ActorSystemInstance;
import com.actors.Message;
import com.actors.OnActorUnregistered;
import com.actors.annotations.Spawn;
import com.actors.testing.ActorsTestRunner;

import org.junit.Test;

import java.util.concurrent.Callable;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import static org.junit.Assert.*;

/**
 * Created by Ahmed Adel Ismail on 3/24/2018.
 */
public class ActorObjectTest {

    @Test
    public void sendMessageToTargetThenUpdateLastMessageValue() throws Exception {
        Message lastMessage = ActorsTestRunner.testActor(TargetActor.class)
                .captureUpdate(TargetActor::getLastMessage)
                .sendMessage(1)
                .withContent("one")
                .getUpdate();

        assertEquals("one", lastMessage.getContent());
    }

    @Test
    public void sendMessageWithIdOneToDependencyActorThenHandleItsResponse()
            throws Exception {

        Message lastMessage = ActorsTestRunner.testActor(TargetActor.class)
                .captureUpdate(TargetActor::getLastMessage)
                .mock(DependencyActor.class, this::handleMessageWithIdOne)
                .sendMessage(1)
                .getUpdate();

        assertEquals(100, lastMessage.getId());
    }

    private void handleMessageWithIdOne(ActorSystemInstance systemInstance, Message message) {
        if (message.getId() == 1) {
            systemInstance.send(new Message(100, "fake-message"), TargetActor.class);
        }
    }

    @Test
    public void sendMessageWithIdTwoThenReceiveMessageWithIdThreeOnCallbackActor()
            throws Exception {

        int messageId = ActorsTestRunner.testActor(TargetActor.class)
                .whenReplyToAddress(CallbackActor.class)
                .captureReply(Message::getId)
                .sendMessage(2)
                .getReply();

        assertEquals(3,messageId);

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


@Spawn(DependencyActor.class)
class TargetActor implements Actor {

    private Message lastMessage;

    TargetActor() {
    }

    @Override
    public void onMessageReceived(Message message) {
        this.lastMessage = message;
        if (message.getId() == 1) {
            // do some logic
            ActorSystem.send(1, DependencyActor.class);
        } else if (message.getId() == 2) {
            // do some logic
            ActorSystem.send(3, CallbackActor.class);
        }

    }

    Message getLastMessage() {
        return lastMessage;
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