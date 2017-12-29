package com.actors.actorlite;

import android.support.annotation.NonNull;
import android.util.Log;

import com.actors.ActorService;
import com.actors.Message;
import com.annotations.Command;
import com.annotations.CommandsMapFactory;
import com.mapper.CommandsMap;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ahmed Adel Ismail on 12/26/2017.
 */
@CommandsMapFactory
public class MainService extends ActorService {

    private CommandsMap map = CommandsMap.of(this);

    @Override
    public void onMessageReceived(Message message) {
        map.execute(message.getId(), message.getContent());
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.computation();
    }

    @Command(R.id.message_id_print_service_log)
    void onPrintLogMessage(String text) {
        Log.e("MainService", "Thread : " + Thread.currentThread().getId());
        Log.e("MainService", text);
        stopSelf();
    }
}
