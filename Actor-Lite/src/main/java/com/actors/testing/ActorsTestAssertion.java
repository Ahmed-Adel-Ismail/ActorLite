package com.actors.testing;

import com.actors.Message;

import org.javatuples.Unit;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * a class that handles running the Unit test and asserting it's result
 * <p>
 * Created by Ahmed Adel Ismail on 3/3/2018.
 */
public class ActorsTestAssertion<R> {

    private final List<R> result;
    private final ActorTestBuilder<R> testBuilder;
    private final ActorSystemTestInstance system;
    private final Message message;
    private final Class<?> receiverActor;

    ActorsTestAssertion(ActorTestBuilder<R> testBuilder, Message message, Class<?> receiverActor) {
        this.message = message;
        this.receiverActor = receiverActor;
        this.system = testBuilder.system;
        this.result = testBuilder.result;
        this.testBuilder = testBuilder;
    }

    /**
     * assert the result of the Actors Communications
     *
     * @param assertion the assertion function
     * @throws Exception if the assertion function threw an {@link Exception}
     */
    public void assertResult(Consumer<R> assertion) throws Exception {
        new ActorSystemTestRegistrations<R>().accept(receiverActor, testBuilder);
        system.send(message, receiverActor);
        system.testScheduler.triggerActions();
        assertion.accept(result.get(0));
    }
}
