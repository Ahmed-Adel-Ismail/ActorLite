package com.actors;

import android.support.annotation.Nullable;

import io.reactivex.functions.Function;

/**
 * a function that retrieves the proper {@link Class} type
 * <p>
 * Created by Ahmed Adel Ismail on 10/11/2017.
 */
class ObjectTypeRetriever implements Function<Object, Class<?>> {

    @Override
    public Class<?> apply(@Nullable Object object) {
        if (object == null) {
            return NullType.class;
        } else if (object instanceof Class) {
            return (Class) object;
        } else {
            return object.getClass();
        }
    }

    static final class NullType {
    }

}
