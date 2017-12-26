package com.actors;

import android.support.annotation.NonNull;

import io.reactivex.Scheduler;

/**
 * an interface implemented by any class that is considered an Actor
 * and will receive {@link Message} Objects through it's Mailbox
 * <p>
 * Created by Ahmed Adel Ismail on 5/3/2017.
 */
public interface Actor {

    /**
     * this method is triggered when a {@link Message} is received from
     * the Mailbox
     *
     * @param message the {@link Message} to be handled
     */
    void onMessageReceived(Message message);

    /**
     * declare the {@link Scheduler} that the Mailbox of this Actor will work on
     *
     * @return a {@link Scheduler} that will host the execution of the received
     * methods
     */
    @NonNull
    Scheduler observeOnScheduler();


}
