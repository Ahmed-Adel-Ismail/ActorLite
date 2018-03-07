package com.actors.testing;

import com.actors.Actor;
import com.actors.Message;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * a class that builds the Message for testing the Actors communication
 * <p>
 * Created by Ahmed Adel Ismail on 3/4/2018.
 */
public class OnResponseTestMessageBuilder<R> extends ActorsTestMessageBuilder<R> {

    private final Class<? extends Actor> targetActor;

    OnResponseTestMessageBuilder(ActorTestBuilder<R> testBuilder,
                                 int id,
                                 Class<? extends Actor> targetActor) {
        super(testBuilder, id);
        this.targetActor = targetActor;
    }

    public OnResponseTestMessageBuilder withContent(Object content) {
        super.withContent(content);
        return this;
    }

    public OnResponseTestMessageBuilder withReplyToActor(Class<?> replyToActor) {
        super.withReplyToActor(replyToActor);
        return this;
    }

    /**
     * send the message to the passed Actor (which is the target for the Unit-test)
     *
     * @param assertion a {@link Consumer} that will be passed the value returned by
     *                  {@link ActorsTestRunner.OnReplyToAddress#captureReply(Function)}
     *                  to assert on it
     */
    public void assertReply(Consumer<R> assertion) throws Exception {
        new ActorsTestAssertion<>(testBuilder, new Message(id, content, replyToActor), targetActor)
                .run(assertion);
    }
}
