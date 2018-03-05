package com.actors.testing;

import com.actors.Message;

import io.reactivex.functions.Consumer;

/**
 * Created by Ahmed Adel Ismail on 3/4/2018.
 */

class OnUpdateTestMessageBuilder<R> extends ActorsTestMessageBuilder<R> {

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
     * send the message to the target Actor that will be updated
     *
     * @return a {@link ActorsTestAssertion} to handle sending the message
     */
    public void run(Consumer<R> assertion) throws Exception {
        new ActorsTestAssertion<>(testBuilder, new Message(id, content, replyToActor),
                testBuilder.validateOnActor)
                .run(assertion);
    }
}
