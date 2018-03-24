package com.actors.testing;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.ActorSystemInstance;
import com.actors.Message;
import com.actors.R;

import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * a class that handles building the Unit-Test for Actors communication
 * <p>
 * Created by Ahmed Adel Ismail on 3/4/2018.
 */
public class OnResponseTestBuilder<T extends Actor, V extends Actor, R> extends ActorTestBuilder<T, V, R> {

    private final Class<? extends T> targetActor;

    OnResponseTestBuilder(Class<? extends V> callbackActor,
                          Class<? extends T> targetActor,
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
    public OnResponseTestBuilder<T, V, R> mock(Class<?> actor,
                                               BiConsumer<ActorSystemInstance, Message> onMessageReceived) {
        super.mock(actor, onMessageReceived);
        return this;
    }

    @Override
    public OnResponseTestBuilder<T, V, R> prepare(Consumer<T> preparation) {
        super.prepare(preparation);
        return this;
    }

    /**
     * prepare a {@link Message} to run the Unit test
     *
     * @param messageId the message ID
     * @return a {@link ActorsTestMessageBuilder} to handle creating a {@link Message}
     */
    public OnResponseTestMessageBuilder<T, V, R> sendMessage(int messageId) {
        return new OnResponseTestMessageBuilder<>(this, messageId, targetActor);
    }

    @Override
    void registerActors(Class<? extends T> targetActor) throws Exception {
        new OnResponseTestRegistrations<T, V, R>().accept(targetActor, this);
    }
}
