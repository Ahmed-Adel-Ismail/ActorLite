package com.actors.testing;

import com.actors.Actor;
import com.actors.ActorSystemGlobalConfiguration;
import com.actors.Message;

import io.reactivex.functions.Function;

/**
 * a class that enables running tests on Actors and mocking other actors on the fly
 * <p>
 * Created by Ahmed Adel Ismail on 3/3/2018.
 */
public class ActorsTestRunner<T extends Actor> {

    static {
        ActorSystemGlobalConfiguration.setTestingMode(true);
    }

    private final Class<? extends T> targetActor;

    private ActorsTestRunner(Class<? extends T> targetActor) {
        this.targetActor = targetActor;
    }

    /**
     * pass the {@link Actor} that will be the target of the Unit-test, notice that this is the
     * only {@link Actor} that will not be mocked, all the rest of the dependencies will be mocks
     *
     * @param targetActor the {@link Actor} to be tested
     * @return the {@link ActorsTestRunner} to build and run the unit-test
     */
    public static <T extends Actor> ActorsTestRunner<T> testActor(Class<? extends T> targetActor) {
        return new ActorsTestRunner<>(targetActor);
    }

    /**
     * pass the {@link Actor} address that is waiting for a reply from the target
     * test {@link Actor}
     *
     * @param onReplyToAddress the address of the {@link Actor} that will be waiting for a
     *                         reply from the target test {@link Actor}
     * @return a {@link OnReplyToAddress} to handle building the {@link OnResponseTestBuilder}
     */
    public <V extends Actor> OnReplyToAddress<T, V> whenReplyToAddress(Class<? extends V> onReplyToAddress) {
        return new OnReplyToAddress<>(targetActor, onReplyToAddress);
    }


    /**
     * supply a function that when the target Actor is updated, it will be executed, this function
     * should return the result that you will validate on later
     *
     * @param onTargetActorUpdated a {@link Function} that will return the expected result to assert on
     * @param <R>                  the type of the expected result
     * @return a {@link ActorTestBuilder} that handles building a Unit test
     */
    public <R> OnUpdateTestBuilder<T, R> captureUpdate(Function<T, R> onTargetActorUpdated) {
        return new OnUpdateTestBuilder<>(targetActor, onTargetActorUpdated);
    }

    public static class OnReplyToAddress<T extends Actor, V extends Actor> {

        private final Class<? extends T> targetActor;
        private final Class<? extends V> waitingForResponseActor;

        private OnReplyToAddress(Class<? extends T> targetActor,
                                 Class<? extends V> waitingForResponseActor) {
            this.targetActor = targetActor;
            this.waitingForResponseActor = waitingForResponseActor;
        }

        /**
         * supply a function that will be invoked when the {@link Actor} under test sends a
         * response message to the passed {@link Actor} address (in the first parameter)
         *
         * @param onMessageReceived a {@link Function} that will return the expected result to assert on
         * @param <R>               the type of the expected result
         * @return a {@link OnResponseTestBuilder} that handles building a Unit test
         */
        public <R> OnResponseTestBuilder<T, V, R> captureReply(Function<Message, R> onMessageReceived) {
            return new OnResponseTestBuilder<>(waitingForResponseActor, targetActor, onMessageReceived);
        }

    }


}
