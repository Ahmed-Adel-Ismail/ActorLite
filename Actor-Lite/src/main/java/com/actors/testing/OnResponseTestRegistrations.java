package com.actors.testing;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.Message;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

class OnResponseTestRegistrations<R> implements BiConsumer<Class<?>, ActorTestBuilder<R>> {

    @Override
    public void accept(Class<?> targetActor, ActorTestBuilder<R> builder) throws Exception {
        new MocksRegistration().accept(builder);
        registerValidateOnActor(builder);
        registerTargetActor(builder, targetActor);
    }

    private void registerValidateOnActor(ActorTestBuilder<R> builder) throws Exception {
        builder.system.register(builder.validateOnActor,
                builder.system.testScheduler, updateResultOnMessageReceived(builder));
    }

    private void registerTargetActor(ActorTestBuilder<R> builder, final Class<?> targetActor)
            throws Exception {
        final Actor actor = new ActorInitializer().apply(targetActor);
        builder.system.register(actor, builder.system.testScheduler, new Consumer<Message>() {
            @Override
            public void accept(Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        });
    }

    @NonNull
    private Consumer<Message> updateResultOnMessageReceived(final ActorTestBuilder<R> testBuilder) {
        return new Consumer<Message>() {
            @Override
            public void accept(Message message) throws Exception {
                testBuilder.result.set(0, testBuilder.validationFunction.apply(message));
            }
        };
    }


}
