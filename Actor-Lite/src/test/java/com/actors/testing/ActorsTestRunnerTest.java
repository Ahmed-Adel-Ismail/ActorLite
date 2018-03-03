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

/**
 * Created by Ahmed Adel Ismail on 3/3/2018.
 */
public class ActorsTestRunnerTest {

    @Test
    public void foo() throws Exception {

        ActorsTestRunner
                .expect(CallbackActor.class, new Function<Message, Integer>() {
                    @Override
                    public Integer apply(Message message) throws Exception {
                        return message.getId();
                    }
                })
                .mock(DependencyActor.class, new BiConsumer<ActorSystemInstance, Message>() {
                    @Override
                    public void accept(ActorSystemInstance actorSystemInstance, Message message) throws Exception {
                        actorSystemInstance.send(3, TargetActor.class);
                    }
                })
                .createMessage(1)
                .withReceiverActor(TargetActor.class)
                .assertResult(new Consumer<Integer>() {
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


@Spawn(DependencyActor.class)
class TargetActor implements Actor {

    public TargetActor() {
    }

    @Override
    public void onMessageReceived(Message message) {
        System.out.println("TargetActor : " + message.getId());
        if (message.getId() == 1) {
            ActorSystem.send(1, DependencyActor.class);
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