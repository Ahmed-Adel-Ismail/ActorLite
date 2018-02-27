package com.actors.actorlite;

import android.support.annotation.NonNull;
import android.util.Log;

import com.actors.Actor;
import com.actors.ClearableActor;
import com.actors.Message;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */

public class DatabaseGateway implements ClearableActor {

    public static final int MSG_PING = 1;

    public DatabaseGateway() {
        Log.w(getClass().getSimpleName(), "initialized()");
    }

    @Override
    public void onMessageReceived(Message message) {
        Log.e(getClass().getSimpleName(), "Thread : " + Thread.currentThread().getName());
        Log.e(getClass().getSimpleName(), message.getContent().toString());
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.io();
    }

    @Override
    public void onUnregister() {
        Log.w(getClass().getSimpleName(), "onCleared()");
    }
}
