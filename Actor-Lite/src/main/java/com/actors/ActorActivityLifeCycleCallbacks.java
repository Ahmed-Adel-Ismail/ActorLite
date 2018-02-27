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

import static com.actors.RegistrationStage.ON_CREATE;
import static com.actors.RegistrationStage.ON_RESUME;
import static com.actors.RegistrationStage.ON_START;
import static com.actors.UnregistrationStage.ON_DESTROY;
import static com.actors.UnregistrationStage.ON_PAUSE;
import static com.actors.UnregistrationStage.ON_STOP;

/**
 * a class that registers {@link Actor} Activities and unregisters them based on the life-cycle
 * events, which should be passed to
 * {@link Application#registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks)}
 * <p>
 * Created by Ahmed Adel Ismail on 7/12/2017.
 */
class ActorActivityLifeCycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private final ActorSystemConfiguration configuration;

    ActorActivityLifeCycleCallbacks(ActorSystemConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

        registerFragmentsCallbacks(activity);

        if (ON_CREATE == configuration.registerActors) {
            registerActor(activity);
        }
    }

    private void registerFragmentsCallbacks(Activity activity) {
        Chain.let(activity)
                .when(isAppCompatActivity())
                .thenMap(toAppCompatActivity())
                .map(toSupportFragmentManager())
                .apply(invokeRegisterFragmentLifecycleCallback());
    }

    private void registerActor(Activity activity) {
        if (activity instanceof Actor) {
            ActorSystem.register((Actor) activity);
        }
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
        manager.registerFragmentLifecycleCallbacks(
                new ActorFragmentLifeCycleCallback(configuration), true);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (ON_START == configuration.registerActors) {
            registerActor(activity);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (ON_RESUME == configuration.registerActors) {
            registerActor(activity);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (ON_PAUSE == configuration.unregisterActors) {
            unregisterActor(activity);
        }

    }

    private void unregisterActor(Activity activity) {
        if (activity instanceof Actor) {
            ActorSystem.unregister(activity);
        }
    }


    @Override
    public void onActivityStopped(Activity activity) {
        if (ON_STOP == configuration.unregisterActors) {
            unregisterActor(activity);
        }

        postponeActorIfRequired(activity);
    }

    private void postponeActorIfRequired(Activity activity) {
        if (activity instanceof Actor
                && configuration.postponeMailboxOnStop
                && ON_DESTROY == configuration.unregisterActors) {
            ActorSystem.postpone(activity);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // do nothing
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        if (ON_DESTROY == configuration.unregisterActors) {
            unregisterActor(activity);
        }


        if (activity.isFinishing()) {
            ActorScheduler.cancel(activity.getClass());
        }

    }

}
