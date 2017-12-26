package com.actors;

/**
 * a {@link Tuple} that holds three values, not only two values
 * <p>
 * Created by Ahmed Adel Ismail on 5/16/2017.
 */
public class Tuple3<T1, T2, T3> extends Tuple<T1, T2>
{
    private final T3 third;

    protected Tuple3(T1 t1, T2 t2, T3 third) {
        super(t1, t2);
        this.third = third;
    }

    public final T3 third() {
        return third;
    }

    /**
     * create a {@link Tuple} holding the passed types
     *
     * @param t1   the first value
     * @param t2   the second value
     * @param t3   the third value
     * @param <T1> the type of the first value
     * @param <T2> the type of the second value
     * @param <T3> the type of the third value
     * @return a {@link Tuple} that holds both values
     */
    public static <T1, T2, T3> Tuple3<T1, T2, T3> from(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>(t1, t2, t3);
    }
}
