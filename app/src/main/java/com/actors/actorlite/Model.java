package com.actors.actorlite;

import android.arch.lifecycle.ViewModel;
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
@Spawn(Repository.class)
public class Model extends ViewModel implements Actor, OnActorUnregistered {

    public static final int MSG_PING = 1;

    public Model() {
        Log.w(getClass().getSimpleName(), "initialized()");
    }

    @Override
    public void onMessageReceived(Message message) {
        Log.w(getClass().getSimpleName(), "Thread : " + Thread.currentThread().getName());
        Log.w(getClass().getSimpleName(), message.getContent().toString());

        if (message.getId() == MSG_PING) {
            ActorSystem.createMessage(Repository.MSG_PING)
                    .withContent("message from Model")
                    .withReplyToActor(Model.class)
                    .withReceiverActors(Repository.class)
                    .send();
        } else if (message.getId() == Repository.MSG_PING_RESPONSE) {
            // handle repository response
        }
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
