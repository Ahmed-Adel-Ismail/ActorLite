package com.actors;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiPredicate;

/**
 * a function that compares between two Objects types
 * <p>
 * Created by Ahmed Adel Ismail on 10/11/2017.
 */
class ObjectTypeValidator implements BiPredicate<Object, Object> {

    @Override
    public boolean test(@NonNull Object objectOne, @NonNull Object objectTwo) {
        ObjectTypeRetriever retriever = new ObjectTypeRetriever();
        return retriever.apply(objectOne).equals(retriever.apply(objectTwo));
    }
}
