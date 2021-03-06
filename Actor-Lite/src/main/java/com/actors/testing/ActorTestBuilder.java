package com.actors.testing;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.ActorSystem;
import com.actors.ActorSystemConfiguration;
import com.actors.ActorSystemInstance;
import com.actors.Message;
import com.actors.R;
import com.functional.curry.Curry;

import org.javatuples.Pair;

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
abstract class ActorTestBuilder<T extends Actor, V extends Actor, R> {

    final List<R> result;
    final ActorSystemTestInstance system;
    final Class<? extends V> validateOnActor;
    final Function<Object, R> validationFunction;
    final List<Pair<Class<?>, Consumer<Message>>> mocks;
    final List<Consumer<T>> preparations;

    ActorTestBuilder(Class<? extends V> validateOnActor, Function<Object, R> validationFunction) {
        this.system = actorSystemInstance(false);
        this.result = new ArrayList<>(1);
        this.result.add(null);
        this.mocks = new LinkedList<>();
        this.validateOnActor = validateOnActor;
        this.validationFunction = validationFunction;
        this.preparations = new LinkedList<>();
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
     * add a preparation function to update the Target {@link Actor} before receiving the {@link Message}
     *
     * @param preparation a preparation function to update the target Actor
     * @return {@code this} instance for chaining
     */
    public ActorTestBuilder<T, V, R> prepare(Consumer<T> preparation) {
        if (preparation != null) {
            this.preparations.add(preparation);
        }
        return this;
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
    public ActorTestBuilder<T, V, R> mock(Class<?> actor,
                                          BiConsumer<ActorSystemInstance, Message> onMessageReceived) {
        mocks.add(mockActorAndOnMessageReceived(actor, onMessageReceived));
        return this;
    }

    @NonNull
    private Pair<Class<?>, Consumer<Message>> mockActorAndOnMessageReceived(
            Class<?> actor, BiConsumer<ActorSystemInstance, Message> onMessageReceived) {
        return Pair.<Class<?>, Consumer<Message>>with(actor, Curry.toConsumer(onMessageReceived, system));
    }

    abstract void registerActors(Class<? extends T> targetActor) throws Exception;

}
