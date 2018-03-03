package com.actors.testing;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.ActorSystemConfiguration;
import com.actors.Message;

import io.reactivex.functions.Function;

/**
 * a class that enables running tests on Actors and mocking other actors on the fly
 * <p>
 * Created by Ahmed Adel Ismail on 3/3/2018.
 */
public class ActorsTestRunner {

    private ActorsTestRunner() {
    }

    /**
     * supply the expected callback, when the target {@link Actor} receives a message, it will send
     * a {@link Message} as a callback, this method sets the {@link Actor#onMessageReceived(Message)}
     * of the callback actor instead of the original {@link Actor}
     *
     * @param callbackActor      the {@link Actor} that is waiting for a message from the target {@link Actor}
     * @param validationFunction a {@link Function} that will return the expected result to assert on
     * @param <R>                the type of the expected result
     * @return a {@link ActorTestBuilder} that handles building a Unit test
     */
    public static <R> ActorTestBuilder<R> expect(Class<? extends Actor> callbackActor,
                                                 Function<Message, R> validationFunction) {
        return new ActorTestBuilder<>(callbackActor, false, validationFunction);
    }


}
