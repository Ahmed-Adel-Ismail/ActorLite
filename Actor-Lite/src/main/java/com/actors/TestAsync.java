package com.actors;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * a class that helps making asynchronous tests, it holds a {@link CountDownLatch} that will block
 * the current thread until you invoke {@link CountDownLatch#countDown()}
 * <p>
 * Created by Ahmed Adel Ismail on 7/12/2017.
 *
 * @param <T> the expected return type to assert on
 */
class TestAsync<T> implements Function<Function<CountDownLatch, T>, T>
{
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final long wait;
    private final TimeUnit timeUnit;

    public TestAsync() {
        this(2, TimeUnit.SECONDS);
    }

    public TestAsync(long waitMillis) {
        this(waitMillis, TimeUnit.MILLISECONDS);
    }

    public TestAsync(long wait, TimeUnit timeUnit) {
        this.wait = wait;
        this.timeUnit = timeUnit;
    }

    @Override
    public T apply(@NonNull Function<CountDownLatch, T> testFunction) throws Exception {
        T result = testFunction.apply(countDownLatch);
        countDownLatch.await(wait, timeUnit);
        return result;
    }

}
