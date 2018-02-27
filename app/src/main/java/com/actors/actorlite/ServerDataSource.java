package com.actors.actorlite;

import android.support.annotation.NonNull;
import android.util.Log;

import com.actors.Actor;
import com.actors.Message;
import com.actors.OnActorUnregistered;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ahmed Adel Ismail on 2/27/2018.
 */

public class ServerDataSource implements Actor, OnActorUnregistered {

    public static final int MSG_PING = 1;

    public ServerDataSource() {
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
