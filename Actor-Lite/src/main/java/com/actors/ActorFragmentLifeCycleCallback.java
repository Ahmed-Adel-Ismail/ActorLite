package com.actors;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.chaining.Chain;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * the Life-Cycle callback for Support Fragments
 * <p>
 * Created by Ahmed Adel Ismail on 1/2/2018.
 */
class ActorFragmentLifeCycleCallback extends FragmentManager.FragmentLifecycleCallbacks {

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        Chain.let(f)
                .when(isActor())
                .thenMap(toActor())
                .apply(registerActor());
    }

    @NonNull
    private Predicate<Fragment> isActor() {
        return new Predicate<Fragment>() {
            @Override
            public boolean test(Fragment fragment) throws Exception {
                return fragment instanceof Actor;
            }
        };
    }

    @NonNull
    private Function<Fragment, Actor> toActor() {
        return new Function<Fragment, Actor>() {
            @Override
            public Actor apply(Fragment fragment) throws Exception {
                return (Actor) fragment;
            }
        };
    }

    @NonNull
    private Consumer<Actor> registerActor() {
        return new Consumer<Actor>() {
            @Override
            public void accept(Actor actor) throws Exception {
                ActorSystem.register(actor);
            }
        };
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        Chain.let(f)
                .when(isActor())
                .then(postponeActor());
    }

    @NonNull
    private Consumer<Fragment> postponeActor() {
        return new Consumer<Fragment>() {
            @Override
            public void accept(Fragment actor) throws Exception {
                ActorSystem.postpone(actor);
            }
        };
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        Chain.let(f instanceof Actor)
                .when(isTrue())
                .thenTo(f)
                .apply(unregisterFromActorSystem())
                .map(toActivity())
                .when(isActivityFinishing())
                .thenTo((Class) f.getClass())
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
    private Consumer<Fragment> unregisterFromActorSystem() {
        return new Consumer<Fragment>() {
            @Override
            public void accept(Fragment fragment) throws Exception {
                ActorSystem.unregister(fragment);
            }
        };
    }

    @NonNull
    private Function<Fragment, Activity> toActivity() {
        return new Function<Fragment, Activity>() {
            @Override
            public Activity apply(Fragment fragment) throws Exception {
                return fragment.getActivity();
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
    private Consumer<Class> cancelScheduledMessages() {
        return new Consumer<Class>() {
            @Override
            public void accept(Class clazz) throws Exception {
                ActorScheduler.cancel(clazz);
            }
        };
    }
}
