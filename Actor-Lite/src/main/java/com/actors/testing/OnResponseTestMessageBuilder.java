package com.actors.testing;

import com.actors.Message;

/**
 * a class that builds the Message for testing the Actors communication
 * <p>
 * Created by Ahmed Adel Ismail on 3/4/2018.
 */
public class OnResponseTestMessageBuilder<R> extends ActorsTestMessageBuilder<R> {

    OnResponseTestMessageBuilder(ActorTestBuilder<R> testBuilder, int id) {
        super(testBuilder, id);
    }

    public OnResponseTestMessageBuilder withContent(Object content) {
        super.withContent(content);
        return this;
    }

    public OnResponseTestMessageBuilder withReplyToActor(Class<?> replyToActor) {
        this.replyToActor = replyToActor;
        return this;
    }

    /**
     * send the message to the passed Actor (which is the target for the Unit-test)
     *
     * @param actor the Actor that will receive the {@link Message}
     * @return a {@link ActorsTestAssertion} to handle sending the message
     */
    public ActorsTestAssertion<R> sendTo(Class<?> actor) {
        return new ActorsTestAssertion<>(testBuilder, new Message(id, content, replyToActor), actor);
    }
}
