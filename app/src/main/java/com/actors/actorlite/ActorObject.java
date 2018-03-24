package com.actors.actorlite;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.ActorScheduler;
import com.actors.ActorSystem;
import com.actors.Message;
import com.actors.annotations.ObserveByAgent;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * a sample Object for documentation purpose
 * <p>
 * Created by Ahmed Adel Ismail on 12/26/2017.
 */
public class ActorObject implements Actor {

    @ObserveByAgent(key = AuthAgent.USER_NAME, agents = AuthAgent.class)
    final BehaviorSubject<String> userName = BehaviorSubject.create();

    @ObserveByAgent(key = AuthAgent.PASSWORD, agents = AuthAgent.class)
    final BehaviorSubject<String> password = BehaviorSubject.create();

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
