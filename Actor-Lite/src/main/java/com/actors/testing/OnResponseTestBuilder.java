package com.actors.testing;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.ActorSystemInstance;
import com.actors.Message;

import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;

/**
 * a class that handles building the Unit-Test for Actors communication
 * <p>
 * Created by Ahmed Adel Ismail on 3/4/2018.
 */
public class OnResponseTestBuilder<R> extends ActorTestBuilder<R> {

    private final Class<? extends Actor> targetActor;

    OnResponseTestBuilder(Class<?> callbackActor,
                          Class<? extends Actor> targetActor,
                          Function<Message, R> validationFunction) {
        super(callbackActor, messageValidationFunction(validationFunction));
        this.targetActor = targetActor;
    }

    @NonNull
    private static <R> Function<Object, R> messageValidationFunction(final Function<Message, R> validationFunction) {
        return new Function<Object, R>() {
            @Override
            public R apply(Object o) throws Exception {
                return validationFunction.apply((Message) o);
            }
        };
    }

    @Override
    public OnResponseTestBuilder<R> mock(Class<?> actor, BiConsumer<ActorSystemInstance, Message> onMessageReceived) {
        super.mock(actor, onMessageReceived);
        return this;
    }

    /**
     * prepare a {@link Message} to run the Unit test
     *
     * @param messageId the message ID
     * @return a {@link ActorsTestMessageBuilder} to handle creating a {@link Message}
     */
    public OnResponseTestMessageBuilder<R> sendMessage(int messageId) {
        return new OnResponseTestMessageBuilder<>(this, messageId, targetActor);
    }

    @Override
    void registerActors(Class<?> targetActor) throws Exception {
        new OnResponseTestRegistrations<R>().accept(targetActor, this);
    }
}
