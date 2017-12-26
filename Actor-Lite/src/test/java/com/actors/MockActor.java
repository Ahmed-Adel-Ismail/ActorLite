package com.actors;

import android.support.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Ahmed Adel Ismail on 5/25/2017.
 */

class MockActor implements Actor
{
    CountDownLatch countDownLatch;
    Message message;
    RuntimeException exception;

    MockActor(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void onMessageReceived(Message message) {
        this.message = message;
        if (exception != null) {
            countDownLatch.countDown();
            throw exception;
        }else{
            countDownLatch.countDown();
        }
    }

    @NonNull
    @Override
    public Scheduler observeOnScheduler() {
        return Schedulers.trampoline();
    }
}

class MockActorTwo extends MockActor{
    MockActorTwo(CountDownLatch countDownLatch) {
        super(countDownLatch);
    }
}
class MockActorThree extends MockActor{
    MockActorThree(CountDownLatch countDownLatch) {
        super(countDownLatch);
    }
}
class MockActorFour extends MockActor{
    MockActorFour(CountDownLatch countDownLatch) {
        super(countDownLatch);
    }
}
class MockActorFive extends MockActor{
    MockActorFive(CountDownLatch countDownLatch) {
        super(countDownLatch);
    }
}
class MockActorSix extends MockActor{
    MockActorSix(CountDownLatch countDownLatch) {
        super(countDownLatch);
    }
}
class MockActorSeven extends MockActor{
    MockActorSeven(CountDownLatch countDownLatch) {
        super(countDownLatch);
    }
}
class MockActorEight extends MockActor{
    MockActorEight(CountDownLatch countDownLatch) {
        super(countDownLatch);
    }
}
class MockActorNine extends MockActor{
    MockActorNine(CountDownLatch countDownLatch) {
        super(countDownLatch);
    }
}
class MockActorTen extends MockActor{
    MockActorTen() {
        super(null);
    }

    void setCountDownLatch(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }

}

class MockActorEleven extends MockActor{
    MockActorEleven(CountDownLatch countDownLatch) {
        super(countDownLatch);
    }
}



