package com.actors.actorlite;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.actors.Actor;
import com.actors.ActorScheduler;
import com.actors.ActorSystem;
import com.actors.Message;
import com.annotations.Command;
import com.annotations.CommandsMapFactory;
import com.mapper.CommandsMap;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

@CommandsMapFactory
public class MainActivity extends AppCompatActivity implements Actor {

    private CommandsMap map = CommandsMap.of(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager()
                .beginTransaction()
                .add(new MainFragment(), "MAIN FRAGMENT")
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendMessages("message from Activity");
    }

    private void sendMessages(String text) {
        Message message = new Message(R.id.message_id_print_fragment_log, text);
        ActorSystem.send(message, MainFragment.class);

        message = new Message(R.id.message_id_print_application_log, text);
        ActorSystem.send(message, MainApp.class);

        // send message after 3 seconds :
        message = new Message(R.id.message_id_print_service_log, text);
        ActorScheduler.after(3000)
                .send(message, MainService.class);
    }


    @Override
    public void onMessageReceived(Message message) {
        map.execute(message.getId(), message.getContent());
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @Command(R.id.message_id_print_activity_log)
    void onPrintLogMessage(String text) {
        Log.e("MainActivity", "Thread : " + Thread.currentThread().getId());
        Log.e("MainActivity", text);
    }

}
