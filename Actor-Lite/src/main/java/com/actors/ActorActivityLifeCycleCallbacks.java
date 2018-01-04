package com.actors;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.chaining.Chain;

/**
 * a class that registers {@link Actor} Activities and unregisters them based on the life-cycle
 * events, which should be passed to
 * {@link Application#registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks)}
 * <p>
 * Created by Ahmed Adel Ismail on 7/12/2017.
 */
class ActorActivityLifeCycleCallbacks implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Chain.let(activity)
                .when(act -> act instanceof AppCompatActivity)
                .thenMap(act -> (AppCompatActivity) act)
                .map(AppCompatActivity::getSupportFragmentManager)
                .apply(this::registerFragmentLifecycleCallback);
    }

    private void registerFragmentLifecycleCallback(FragmentManager manager) {
        manager.registerFragmentLifecycleCallbacks(new ActorFragmentLifeCycleCallback(), false);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity instanceof Actor) {
            ActorSystem.register((Actor) activity);
        }


    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity instanceof Actor) {
            ActorSystem.unregister(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity instanceof Actor && activity.isFinishing()) {
            ActorScheduler.cancel(activity.getClass());
        }
    }
}
