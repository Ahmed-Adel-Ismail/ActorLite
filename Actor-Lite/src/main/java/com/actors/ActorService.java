package com.actors;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

/**
 * a {@link Service} that registers and unregisters itself from the {@link ActorSystem} and
 * {@link ActorScheduler} ... you can extend this Class or invoke the register/unregister methods
 * manually in the proper life cycle methods
 * <p>
 * Created by Ahmed Adel Ismail on 12/26/2017.
 */
public abstract class ActorService extends Service implements ClearableActor {


    @CallSuper
    @Override
    public void onCreate() {
        super.onCreate();
        ActorSystem.register(this);
    }


    @Override
    public void onUnregister() {
        // do nothing
    }

    @CallSuper
    @Override
    public void onDestroy() {
        ActorSystem.unregister(this);
        ActorScheduler.cancel(getClass());
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
