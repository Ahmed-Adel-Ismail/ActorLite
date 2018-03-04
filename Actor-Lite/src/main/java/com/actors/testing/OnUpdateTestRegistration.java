package com.actors.testing;

import android.support.annotation.NonNull;

import com.actors.Actor;
import com.actors.Message;

import io.reactivex.Scheduler;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

class OnUpdateTestRegistration<R> implements BiConsumer<Class<?>, ActorTestBuilder<R>> {

    @Override
    public void accept(final Class<?> targetActor, final ActorTestBuilder<R> builder) throws Exception {
        new MocksRegistration().accept(builder);
        registerTargetUpdatableActor(targetActor, builder);
    }

    private void registerTargetUpdatableActor(Class<?> targetActor, ActorTestBuilder<R> builder) throws Exception {
        builder.system.register(targetActor,
                builder.system.testScheduler, invokeOnMessageReceived(targetActor, builder));
    }

    @NonNull
    private Consumer<Message> invokeOnMessageReceived(
            final Class<?> targetActor, final ActorTestBuilder<R> builder) throws Exception {
        return new Consumer<Message>() {

            Actor actor = wrapperActor(targetActor, builder);

            @Override
            public void accept(Message message) throws Exception {
                actor.onMessageReceived(message);
            }
        };
    }

    @NonNull
    private Actor wrapperActor(final Class<?> targetActor, final ActorTestBuilder<R> builder)
            throws Exception {
        return new Actor() {

            final Actor originalActor = new ActorInitializer().apply(targetActor);

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
