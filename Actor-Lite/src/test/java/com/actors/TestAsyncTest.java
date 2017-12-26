package com.actors;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ahmed Adel Ismail on 7/12/2017.
 */
@RunWith(ParallelRunner.class)
public class TestAsyncTest
{

    @Test
    public void waitUntilOperationCompleteOnAnotherThread() throws Exception {

        final long startMillis = System.currentTimeMillis();
        final long waitMillis = 1000;
        final long endMillis = new TestAsync<Long>().apply(new Function<CountDownLatch, Long>()
        {
            @Override
            public Long apply(@NonNull CountDownLatch countDownLatch) throws Exception {
                Thread.sleep(waitMillis);
                countDownLatch.countDown();
                return System.currentTimeMillis();
            }
        });
        assertTrue(endMillis >= (startMillis + waitMillis));
    }

    @Test
    public void parallelWaitUntilOperationCompleteOnAnotherThread() throws Exception {

        final long startMillis = System.currentTimeMillis();
        final long waitMillis = 1000;
        final long endMillis = new TestAsync<Long>().apply(new Function<CountDownLatch, Long>()
        {
            @Override
            public Long apply(@NonNull CountDownLatch countDownLatch) throws Exception {
                Thread.sleep(waitMillis);
                countDownLatch.countDown();
                return System.currentTimeMillis();
            }
        });
        assertTrue(endMillis >= (startMillis + waitMillis));
    }

}