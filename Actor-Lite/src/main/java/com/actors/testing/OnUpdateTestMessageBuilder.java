package com.actors.testing;

import com.actors.Message;

import io.reactivex.functions.Consumer;

/**
 * a class that handles building message for Actors that will be asserted for there update
 * <p>
 * Created by Ahmed Adel Ismail on 3/4/2018.
 */
public class OnUpdateTestMessageBuilder<R> extends ActorsTestMessageBuilder<R> {

    OnUpdateTestMessageBuilder(ActorTestBuilder<R> testBuilder, int id) {
        super(testBuilder, id);
    }

    public OnUpdateTestMessageBuilder withContent(Object content) {
        super.withContent(content);
        return this;
    }

    public OnUpdateTestMessageBuilder withReplyToActor(Class<?> replyToActor) {
        this.replyToActor = replyToActor;
        return this;
    }

    /**
     * send the message to the target Actor that will be updated, and run the assertion for
     * it's update
     */
    public void run(Consumer<R> assertion) throws Exception {
        new ActorsTestAssertion<>(testBuilder, new Message(id, content, replyToActor),
                testBuilder.validateOnActor)
                .run(assertion);
    }
}
