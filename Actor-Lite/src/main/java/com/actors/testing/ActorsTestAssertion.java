package com.actors.testing;

import com.actors.Actor;
import com.actors.Message;
import com.actors.R;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * a class that handles running the Unit test and asserting it's result
 * <p>
 * Created by Ahmed Adel Ismail on 3/3/2018.
 */
public class ActorsTestAssertion<T extends Actor, V extends Actor, R> {

    private final List<R> result;
    private final ActorTestBuilder<T, V, R> testBuilder;
    private final ActorSystemTestInstance system;
    private final Message message;
    private final Class<? extends T> targetActor;

    ActorsTestAssertion(ActorTestBuilder<T, V, R> testBuilder, Message message, Class<? extends T> targetActor) {
        this.message = message;
        this.targetActor = targetActor;
        this.system = testBuilder.system;
        this.result = testBuilder.result;
        this.testBuilder = testBuilder;
    }

    public R getResult() throws Exception {
        try {
            testBuilder.registerActors(targetActor);
            system.send(message, targetActor);
            system.testScheduler.triggerActions();
            return result.get(0);
        } finally {
            system.clear();
        }
    }

    /**
     * assert the result of the Actors Communications
     *
     * @param assertion the assertion function
     * @throws Exception if the assertion function threw an {@link Exception}
     */
    public void run(Consumer<R> assertion) throws Exception {
        try {
            testBuilder.registerActors(targetActor);
            system.send(message, targetActor);
            system.testScheduler.triggerActions();
            assertion.accept(result.get(0));
        } finally {
            system.clear();
        }

    }
}
