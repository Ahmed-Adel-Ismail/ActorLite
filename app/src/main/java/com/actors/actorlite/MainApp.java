package com.actors.actorlite;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.actors.Actor;
import com.actors.ActorLite;
import com.actors.ActorSystemConfiguration;
import com.actors.Message;
import com.actors.RegistrationStage;
import com.actors.UnregistrationStage;
import com.annotations.Command;
import com.annotations.CommandsMapFactory;
import com.mapper.CommandsMap;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ahmed Adel Ismail on 12/26/2017.
 */
@CommandsMapFactory
public class MainApp extends Application implements Actor {

    private CommandsMap map = CommandsMap.of(this);

    @Override
    public void onCreate() {
        super.onCreate();
        ActorLite.with(this, actorSystemConfiguration());
        startService(new Intent(this, MainService.class));
    }

    @NonNull
    private ActorSystemConfiguration actorSystemConfiguration() {
        return new ActorSystemConfiguration.Builder()
                .activitiesRegistration(RegistrationStage.ON_CREATE)
                .activitiesUnregistration(UnregistrationStage.ON_DESTROY)
                .fragmentRegistration(RegistrationStage.ON_CREATE)
                .fragmentUnregistration(UnregistrationStage.ON_DESTROY)
                .postponeMailboxDisabled(true)
                .build();
    }


    @Override
    public void onMessageReceived(Message message) {
        map.execute(message.getId(), message.getContent());
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.computation();
    }

    @Command(R.id.message_id_print_application_log)
    void onPrintLogMessage(String text) {
        Log.e("MainApp", "Thread : " + Thread.currentThread().getId());
        Log.e("MainApp", text);
    }
}
