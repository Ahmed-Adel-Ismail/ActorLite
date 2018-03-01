package com.actors.actorlite;

import android.support.annotation.NonNull;
import android.util.Log;

import com.actors.Actor;
import com.actors.ActorSystem;
import com.actors.Message;
import com.actors.OnActorUnregistered;
import com.actors.annotations.Spawn;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */
@Spawn(value = ServerDataSource.class, actorClasses = "com.actors.actorlite.DatabaseDataSource")
public class Repository implements Actor, OnActorUnregistered {

    public static final int MSG_PING = 1;
    public static final int MSG_PING_RESPONSE = 2;

    public Repository() {
        Log.w(getClass().getSimpleName(), "initialized()");
    }

    @Override
    public void onMessageReceived(Message message) {

        Log.w(getClass().getSimpleName(), "Thread : " + Thread.currentThread().getName());
        Log.w(getClass().getSimpleName(), message.getContent().toString());

        ActorSystem.createMessage(DatabaseDataSource.MSG_PING)
                .withContent("message from repository")
                .withReceiverActors("com.actors.actorlite.DatabaseDataSource")
                .send();

        ActorSystem.createMessage(ServerDataSource.MSG_PING)
                .withContent("message from repository")
                .withReceiverActors(ServerDataSource.class)
                .send();

        ActorSystem.createMessage(MSG_PING_RESPONSE)
                .withContent("response from repository")
                .withReceiverActors(message.getReplyToActor())
                .send();

    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.computation();
    }

    @Override
    public void onUnregister() {
        Log.w(getClass().getSimpleName(), "onCleared()");
    }
}
