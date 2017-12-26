package com.actors;

import android.support.annotation.NonNull;

import java.util.Map.Entry;

/**
 * a class that represents a Tuple (a pair of values), which is an immutable {@link Entry}
 * <p>
 * Created by Ahmed Adel Ismail on 5/16/2017.
 */
public class Tuple<T1, T2>
{

    private final T1 first;
    private final T2 second;

    protected Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public final T1 first() {
        return first;
    }

    public final T2 second() {
        return second;
    }

    /**
     * create a {@link Tuple} holding the passed types
     *
     * @param t1   the first value
     * @param t2   the second value
     * @param <T1> the type of the first value
     * @param <T2> the type of the second value
     * @return a {@link Tuple} that holds both values
     */
    public static <T1, T2> Tuple<T1, T2> from(T1 t1, T2 t2) {
        return new Tuple<>(t1, t2);
    }

    /**
     * create a {@link Tuple} holding the passed {@link Entry} key and value
     *
     * @param entry the {@link Entry} that holds the key and value
     * @param <T1>  the type of the first value
     * @param <T2>  the type of the second value
     * @return a {@link Tuple} that holds both values
     */
    public static <T1, T2> Tuple<T1, T2> from(@NonNull Entry<T1, T2> entry) {
        return new Tuple<>(entry.getKey(), entry.getValue());
    }

    /**
     * create a new {@link Tuple} from the current one with different {@link #first}
     *
     * @param t1  the new {@link #first}
     * @param <T> the type of the new value
     * @return a new {@link Tuple}
     */
    public <T> Tuple<T, T2> withFirst(T t1) {
        return new Tuple<>(t1, this.second);
    }

    /**
     * create a new {@link Tuple} from the current one with different {@link #first}
     *
     * @param t2  the new {@link #first}
     * @param <T> the type of the new value
     * @return a new {@link Tuple}
     */

    public <T> Tuple<T1, T> withSecond(T t2) {
        return new Tuple<>(this.first, t2);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Tuple<?, ?> tuple = (Tuple<?, ?>) o;

        if (first != null ? !first.equals(tuple.first) : tuple.first != null) {
            return false;
        }
        return second != null ? second.equals(tuple.second) : tuple.second == null;

    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        return result;
    }


}
