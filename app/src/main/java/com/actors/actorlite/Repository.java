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
@Spawn({ServerGateway.class, DatabaseGateway.class})
public class Repository implements Actor, OnActorUnregistered {

    public static final int MSG_PING = 1;

    public Repository() {
        Log.w(getClass().getSimpleName(), "initialized()");
    }

    @Override
    public void onMessageReceived(Message message) {
        Log.e(getClass().getSimpleName(), "Thread : " + Thread.currentThread().getName());
        Log.e(getClass().getSimpleName(), message.getContent().toString());

        ActorSystem.send(new Message(ServerGateway.MSG_PING,"message from repository"), ServerGateway.class);
        ActorSystem.send(new Message(DatabaseGateway.MSG_PING,"message from repository"), DatabaseGateway.class);
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
