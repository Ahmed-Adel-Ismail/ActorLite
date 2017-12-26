package com.actors;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * a class that wraps any Throwable and turns it into a
 * {@link RuntimeException}
 * <p>
 * Created by Ahmed Adel Ismail on 5/3/2017.
 */
class RuntimeExceptionConverter implements Function<Throwable, RuntimeException> {

    @Override
    public RuntimeException apply(@NonNull Throwable e) {
        return (e instanceof RuntimeException)
                ? (RuntimeException) e
                : new RuntimeException(e);
    }

}
