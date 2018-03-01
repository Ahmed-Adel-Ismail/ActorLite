package com.actors;

import android.annotation.SuppressLint;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.HashMap;

/**
 * a class that schedules messages in {@link ActorSystem}, where it gives the ability to send
 * a message after a certain delay to an Actor
 * <p>
 * Created by Ahmed Adel Ismail on 5/25/2017.
 */
@SuppressWarnings("deprecation")
@SuppressLint("UseSparseArrays")
public class ActorScheduler {

    static final Object lock = new Object();
    static final HashMap<Class<?>, DisposablesGroup> schedules = new HashMap<>();

    ActorScheduler() {

    }

    /**
     * schedule to send a {@link Message} after the passed milliseconds
     *
     * @param millis the delay in milliseconds
     * @return a {@link ScheduledMessageSender} to send a scheduled {@link Message}
     */

    @NonNull
    public static ScheduledMessageSender after(@IntRange(from = 0) long millis) {
        return after(millis, ActorSystemInstance.getInstance(null));
    }

    /**
     * schedule to send a {@link Message} after the passed milliseconds
     *
     * @param millis      the delay in milliseconds
     * @param actorSystem the custom {@link ActorSystemInstance} : for unit testing only
     * @return a {@link ScheduledMessageSender} to send a scheduled {@link Message}
     */
    @NonNull
    static ScheduledMessageSender after(@IntRange(from = 0) long millis, ActorSystemInstance actorSystem) {
        return new ScheduledMessageSender(millis, actorSystem);
    }


    @NonNull
    static DisposablesGroup getNonNullDisposableGroup(Class<?> actorAddress) {
        DisposablesGroup schedule = ActorScheduler.schedules.get(actorAddress);
        if (schedule == null) {
            schedule = new DisposablesGroup();
        }
        return schedule;
    }

    public static void cancel(@NonNull Class<?> actorAddress) {
        synchronized (lock) {
            doCancel(actorAddress);
        }
    }

    private static void doCancel(@NonNull Class<?> actorAddress) {
        DisposablesGroup disposablesGroup = schedules.get(actorAddress);
        if (disposablesGroup != null) {
            disposablesGroup.clear();
        }
        schedules.remove(actorAddress);
    }
}



