package com.actors.testing;

import com.actors.Message;
import com.actors.MessageBuilder;
import com.actors.MessageSender;

/**
 * a class that builds the Message for testing the Actors communication
 * <p>
 * Created by Ahmed Adel Ismail on 3/3/2018.
 */
public class ActorsTestMessageBuilder<R> {

    private final ActorTestBuilder<R> testBuilder;
    private final int id;
    private Object content;
    private Class<?> replyToActor;

    ActorsTestMessageBuilder(ActorTestBuilder<R> testBuilder, int id) {
        this.testBuilder = testBuilder;
        this.id = id;
    }

    /**
     * Sets the {@code content} and returns a reference to this {@link MessageBuilder} so
     * that the methods can be chained together.
     *
     * @param content the {@code content} to set
     * @return a reference to this Builder
     */
    public ActorsTestMessageBuilder withContent(Object content) {
        this.content = content;
        return this;
    }

    /**
     * Sets the {@code replyToActor} and returns a reference to this {@link MessageBuilder} so
     * that the methods can be chained together.
     *
     * @param replyToActor the {@code replyToActor} to set
     * @return a reference to this Builder
     */
    public ActorsTestMessageBuilder withReplyToActor(Class<?> replyToActor) {
        this.replyToActor = replyToActor;
        return this;
    }

    /**
     * set the receiver Actor
     *
     * @param actor the Actor that will receive the {@link Message}
     * @return a {@link MessageSender} to handle sending the message
     */
    public ActorsTestAssertion<R> withReceiverActor(Class<?> actor) {
        return new ActorsTestAssertion<>(testBuilder, new Message(id, content, replyToActor), actor);
    }

}
