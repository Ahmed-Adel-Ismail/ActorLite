package com.actors.testing;

import com.actors.Actor;

import java.lang.reflect.Constructor;

import io.reactivex.functions.Function;

class ActorInitializer implements Function<Class<?>, Actor> {
    @Override
    public Actor apply(Class<?> targetActor) throws Exception {
        Constructor constructor = targetActor.getDeclaredConstructor();
        constructor.setAccessible(true);
        return (Actor) constructor.newInstance();
    }
}
