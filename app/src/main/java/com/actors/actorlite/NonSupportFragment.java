package com.actors.actorlite;

import android.support.annotation.NonNull;
import android.util.Log;

import com.actors.ActorFragment;
import com.actors.ActorSystem;
import com.actors.Message;
import com.annotations.Command;
import com.annotations.CommandsMapFactory;
import com.mapper.CommandsMap;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Ahmed Adel Ismail on 1/4/2018.
 */
@CommandsMapFactory
public class NonSupportFragment extends ActorFragment {

    private CommandsMap map = CommandsMap.of(this);

    @Override
    public void onMessageReceived(Message message) {
        map.execute(message.getId(), message.getContent());
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Command(R.id.message_id_print_non_support_fragment_log)
    void onPrintLogMessage(String text) {
        Log.e("NonSupportFragment", "Thread : " + Thread.currentThread().getId());
        Log.e("NonSupportFragment", text);

        Message message = new Message(R.id.message_id_print_activity_log, "message from NonSupportFragment");
        ActorSystem.send(message, MainActivity.class);

    }


}
