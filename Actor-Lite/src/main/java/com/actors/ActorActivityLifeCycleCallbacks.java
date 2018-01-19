package com.actors;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.chaining.Chain;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

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
                .when(isAppCompatActivity())
                .thenMap(toAppCompatActivity())
                .map(toSupportFragmentManager())
                .apply(invokeRegisterFragmentLifecycleCallback());
    }

    @NonNull
    private Predicate<Activity> isAppCompatActivity() {
        return new Predicate<Activity>() {
            @Override
            public boolean test(Activity act) throws Exception {
                return act instanceof AppCompatActivity;
            }
        };
    }

    @NonNull
    private Function<Activity, AppCompatActivity> toAppCompatActivity() {
        return new Function<Activity, AppCompatActivity>() {
            @Override
            public AppCompatActivity apply(Activity act) throws Exception {
                return (AppCompatActivity) act;
            }
        };
    }

    @NonNull
    private Function<AppCompatActivity, FragmentManager> toSupportFragmentManager() {
        return new Function<AppCompatActivity, FragmentManager>() {
            @Override
            public FragmentManager apply(AppCompatActivity appCompatActivity) throws Exception {
                return appCompatActivity.getSupportFragmentManager();
            }
        };
    }

    @NonNull
    private Consumer<FragmentManager> invokeRegisterFragmentLifecycleCallback() {
        return new Consumer<FragmentManager>() {
            @Override
            public void accept(FragmentManager manager) throws Exception {
                registerFragmentLifecycleCallback(manager);
            }
        };
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
            ActorSystem.postpone(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Chain.let(activity instanceof Actor)
                .when(isTrue())
                .thenTo(activity)
                .apply(unregisterFromActorSystem())
                .when(isActivityFinishing())
                .thenMap(toActivityClass())
                .apply(cancelScheduledMessages());
    }

    @NonNull
    private Predicate<Boolean> isTrue() {
        return new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean aBoolean) throws Exception {
                return aBoolean;
            }
        };
    }

    @NonNull
    private Consumer<Activity> unregisterFromActorSystem() {
        return new Consumer<Activity>() {
            @Override
            public void accept(Activity activity) throws Exception {
                ActorSystem.unregister(activity);
            }
        };
    }

    @NonNull
    private Predicate<Activity> isActivityFinishing() {
        return new Predicate<Activity>() {
            @Override
            public boolean test(Activity activity) throws Exception {
                return activity.isFinishing();
            }
        };
    }

    @NonNull
    private Function<Activity, Class<?>> toActivityClass() {
        return new Function<Activity, Class<?>>() {
            @Override
            public Class<?> apply(Activity activity) throws Exception {
                return activity.getClass();
            }
        };
    }

    @NonNull
    private Consumer<Class<?>> cancelScheduledMessages() {
        return new Consumer<Class<?>>() {
            @Override
            public void accept(Class<?> clazz) throws Exception {
                ActorScheduler.cancel(clazz);
            }
        };
    }
}
