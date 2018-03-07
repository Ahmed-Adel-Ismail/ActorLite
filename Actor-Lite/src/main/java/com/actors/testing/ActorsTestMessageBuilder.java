package com.actors.testing;

import com.actors.MessageBuilder;

/**
 * a class that builds the Message for testing the Actors communication
 * <p>
 * Created by Ahmed Adel Ismail on 3/3/2018.
 */
abstract class ActorsTestMessageBuilder<R> {

    final ActorTestBuilder<R> testBuilder;
    final int id;
    Object content;
    Class<?> replyToActor;

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
    public ActorsTestMessageBuilder<R> withContent(Object content) {
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
    public ActorsTestMessageBuilder<R> withReplyToActor(Class<?> replyToActor) {
        this.replyToActor = replyToActor;
        return this;
    }


}
