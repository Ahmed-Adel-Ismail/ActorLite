package com.actors.actorlite;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.ActorScheduler;
import com.actors.ActorSystem;
import com.actors.ActorSystemGlobalConfiguration;
import com.actors.Message;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * a sample Object for documentation purpose
 * <p>
 * Created by Ahmed Adel Ismail on 12/26/2017.
 */
public class ActorObject implements Actor {

    public ActorObject() {
        ActorSystem.register(this);
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.computation();
    }


    @Override
    public void onMessageReceived(Message message) {
        // ...
    }

    public void onDestroy() {
        ActorSystem.unregister(this);
        ActorScheduler.cancel(getClass());
    }
}
