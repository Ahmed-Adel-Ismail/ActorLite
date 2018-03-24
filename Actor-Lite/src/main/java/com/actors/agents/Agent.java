package com.actors.agents;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.Message;

import org.javatuples.Pair;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * The parent class for any Agent
 * <p>
 * Created by Ahmed Adel Ismail on 3/7/2018.
 */
public abstract class Agent<T> implements Actor {

    final static int MSG_OBSERVE_ON_FIELD = -587;
    final static int MSG_UN_OBSERVE_ON_FIELD = -788;

    @SuppressWarnings("unchecked")
    @Override
    public final void onMessageReceived(Message message) {
        if (MSG_OBSERVE_ON_FIELD == message.getId()) {
            Pair<String, Object> pair = message.getContent();
            observeOn(pair.getValue0(), (Observable<T>) pair.getValue1());
        }
    }

    protected abstract void observeOn(String key, Observable<T> observable);

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.computation();
    }
}
