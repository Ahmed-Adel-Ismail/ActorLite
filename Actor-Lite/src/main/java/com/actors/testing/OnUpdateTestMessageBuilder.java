package com.actors.testing;

import com.actors.Actor;
import com.actors.Message;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * a class that handles building message for Actors that will be asserted for there update
 * <p>
 * Created by Ahmed Adel Ismail on 3/4/2018.
 */
public class OnUpdateTestMessageBuilder<T extends Actor, R> extends ActorsTestMessageBuilder<T, T, R> {

    OnUpdateTestMessageBuilder(ActorTestBuilder<T, T, R> testBuilder, int id) {
        super(testBuilder, id);
    }

    public OnUpdateTestMessageBuilder<T, R> withContent(Object content) {
        super.withContent(content);
        return this;
    }

    public OnUpdateTestMessageBuilder<T, R> withReplyToActor(Class<?> replyToActor) {
        super.withReplyToActor(replyToActor);
        return this;
    }

    public R getUpdate() throws Exception {
        return new ActorsTestAssertion<>(testBuilder, new Message(id, content, replyToActor),
                testBuilder.validateOnActor)
                .getResult();
    }

    /**
     * send the message to the target Actor that will be updated, and run the assertion for
     * it's update
     *
     * @param assertion a {@link Consumer} that will be passed the value returned by
     *                  {@link ActorsTestRunner#captureUpdate(Function)}  to assert on
     *                  it
     */
    public void assertUpdated(Consumer<R> assertion) throws Exception {
        new ActorsTestAssertion<>(testBuilder, new Message(id, content, replyToActor),
                testBuilder.validateOnActor)
                .run(assertion);
    }
}
