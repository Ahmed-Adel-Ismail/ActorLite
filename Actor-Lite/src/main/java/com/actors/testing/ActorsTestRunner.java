package com.actors.testing;

import android.support.annotation.RestrictTo;

import com.actors.Actor;
import com.actors.ActorSystem;
import com.actors.ActorSystemGlobalConfiguration;
import com.actors.Message;

import io.reactivex.annotations.Experimental;
import io.reactivex.functions.Function;

/**
 * a class that enables running tests on Actors and mocking other actors on the fly
 * <p>
 * Created by Ahmed Adel Ismail on 3/3/2018.
 */
public class ActorsTestRunner {

    static {
        ActorSystemGlobalConfiguration.setTestingMode(true);
    }

    private ActorsTestRunner() {

    }

    /**
     * supply a function that will be invoked when the Actor under test sends a response message
     * to the passed Actor address (in the first parameter)
     *
     * @param waitingForResponseActor the {@link Actor} that is waiting for a message from the target {@link Actor}
     * @param onMessageReceived       a {@link Function} that will return the expected result to assert on
     * @param <R>                     the type of the expected result
     * @return a {@link ActorTestBuilder} that handles building a Unit test
     */
    public static <R> OnResponseTestBuilder<R> assertReply(
            Class<? extends Actor> waitingForResponseActor, Function<Message, R> onMessageReceived) {
        return new OnResponseTestBuilder<>(waitingForResponseActor, false, onMessageReceived);
    }

    /**
     * supply a function that when the target Actor is updated, it will be executed, this function
     * should return the result that you will validate on later
     *
     * @param targetActor          the {@link Actor} that is waiting for a message from the target {@link Actor}
     * @param onTargetActorUpdated a {@link Function} that will return the expected result to assert on
     * @param <R>                  the type of the expected result
     * @return a {@link ActorTestBuilder} that handles building a Unit test
     */
    public static <T extends Actor, R> OnUpdateTestBuilder<R> assertUpdate(
            Class<? extends Actor> targetActor, Function<T, R> onTargetActorUpdated) {
        return new OnUpdateTestBuilder<>(targetActor, false, onTargetActorUpdated);
    }


}
