package com.actors.testing;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.ActorSystem;
import com.actors.ActorSystemConfiguration;
import com.actors.ActorSystemInstance;
import com.actors.Message;
import com.functional.curry.Curry;

import org.javatuples.Pair;
import org.javatuples.Unit;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * a class that handles building the Unit-Test for Actors communication
 * <p>
 * Created by Ahmed Adel Ismail on 3/3/2018.
 */
@SuppressLint("RestrictedApi")
public class ActorTestBuilder<R> {

    final List<R> result;
    final ActorSystemTestInstance system;
    final Class<?> callbackActor;
    final Function<Message, R> validationFunction;
    final List<Pair<Class<?>, Consumer<Message>>> mockers;

    ActorTestBuilder(Class<?> callbackActor, boolean spawning, Function<Message, R> validationFunction) {
        this.system = actorSystemInstance(spawning);
        this.result = new ArrayList<>(1);
        this.result.add(null);
        this.mockers = new LinkedList<>();
        this.callbackActor = callbackActor;
        this.validationFunction = validationFunction;
    }

    @NonNull
    private static ActorSystemTestInstance actorSystemInstance(boolean spawning) {
        return new ActorSystemTestInstance(configuration(spawning));
    }

    private static ActorSystemConfiguration configuration(boolean spawning) {
        return new ActorSystemConfiguration.Builder()
                .spawnActors(spawning)
                .build();
    }

    /**
     * Mock an Actor, to make this Mocked Actor receive message instead of the original one,
     * and pass it's {@link Actor#onMessageReceived(Message)} as the second parameter
     *
     * @param actor             the Actor that will be mocked
     * @param onMessageReceived the method that will be executed when this actor receives a message,
     *                          it's first parameter is the current {@link ActorSystem}, and the
     *                          second parameter is the {@link Message} received
     * @return {@code this} instance for chaining
     */
    public ActorTestBuilder<R> mock(Class<?> actor,
                                    BiConsumer<ActorSystemInstance, Message> onMessageReceived) {
        mockers.add(mockActorAndOnMessageReceived(actor, onMessageReceived));
        return this;
    }

    @NonNull
    private Pair<Class<?>, Consumer<Message>> mockActorAndOnMessageReceived(
            Class<?> actor, BiConsumer<ActorSystemInstance, Message> onMessageReceived) {
        return Pair.<Class<?>, Consumer<Message>>with(actor, Curry.toConsumer(onMessageReceived, system));
    }

    /**
     * prepare a {@link Message} to run the Unit test
     *
     * @param messageId the message ID
     * @return a {@link ActorsTestMessageBuilder} to handle creating a {@link Message}
     */
    public ActorsTestMessageBuilder<R> createMessage(int messageId) {
        return new ActorsTestMessageBuilder<>(this, messageId);
    }


}
