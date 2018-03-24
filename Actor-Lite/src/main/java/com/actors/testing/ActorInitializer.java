package com.actors.testing;

import android.support.annotation.NonNull;

import com.actors.Actor;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

class ActorInitializer<T extends Actor> implements Function<Class<?>, T> {

    private final List<Consumer<T>> preparations = new LinkedList<>();

    ActorInitializer(List<Consumer<T>> preparations) {
        if (preparations != null) {
            this.preparations.addAll(preparations);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T apply(Class<?> targetActor) throws Exception {
        Constructor constructor = targetActor.getDeclaredConstructor();
        constructor.setAccessible(true);
        final T actor = (T) constructor.newInstance();
        Observable.fromIterable(preparations).blockingForEach(prepareActor(actor));
        return actor;
    }

    @NonNull
    private Consumer<Consumer<T>> prepareActor(final T actor) {
        return new Consumer<Consumer<T>>() {
            @Override
            public void accept(Consumer<T> preparation) throws Exception {
                preparation.accept(actor);
            }
        };
    }
}
