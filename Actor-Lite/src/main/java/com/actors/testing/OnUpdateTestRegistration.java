package com.actors.testing;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.Message;

import io.reactivex.Scheduler;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

class OnUpdateTestRegistration<T extends Actor, R>
        implements BiConsumer<Class<? extends T>, ActorTestBuilder<T, T, R>> {

    @Override
    public void accept(final Class<? extends T> targetActor, final ActorTestBuilder<T, T, R> builder)
            throws Exception {
        new MocksRegistration().accept(builder);
        registerTargetUpdatableActor(targetActor, builder);
    }

    private void registerTargetUpdatableActor(Class<? extends T> targetActor, ActorTestBuilder<T, T, R> builder)
            throws Exception {
        builder.system.register(targetActor,
                builder.system.testScheduler, invokeOnMessageReceived(targetActor, builder));
    }

    @NonNull
    private Consumer<Message> invokeOnMessageReceived(
            final Class<? extends T> targetActor, final ActorTestBuilder<T, T, R> builder) throws Exception {
        return new Consumer<Message>() {

            T actor = wrapperActor(targetActor, builder);

            @Override
            public void accept(Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        };
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private T wrapperActor(final Class<? extends T> targetActor, final ActorTestBuilder<T, T, R> builder)
            throws Exception {
        return (T) new Actor() {

            final T originalActor = new ActorInitializer<>(builder.preparations).apply(targetActor);

            @Override
            public void onMessageReceived(Message message) {
                originalActor.onMessageReceived(message);
                try {
                    builder.result.set(0, builder.validationFunction.apply(originalActor));
                } catch (Throwable e) {
                    printAndPropagateAsRuntimeException(e);
                }
            }

            @NonNull
            @Override
            public Scheduler observeOnScheduler() {
                return originalActor.observeOnScheduler();
            }
        };
    }

    private void printAndPropagateAsRuntimeException(Throwable e) {
        e.printStackTrace();
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw new RuntimeException(e);
        }
    }
}
