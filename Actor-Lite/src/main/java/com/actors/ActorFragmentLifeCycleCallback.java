package com.actors;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.chaining.Chain;

/**
 * the Life-Cycle callback for Support Fragments
 * <p>
 * Created by Ahmed Adel Ismail on 1/2/2018.
 */
class ActorFragmentLifeCycleCallback extends FragmentManager.FragmentLifecycleCallbacks {

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        Chain.let(f)
                .when(fragment -> fragment instanceof Actor)
                .thenMap(fragment -> (Actor) fragment)
                .apply(ActorSystem::register);
    }


    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        Chain.let(f)
                .when(fragment -> fragment instanceof Actor)
                .thenMap(fragment -> (Actor) fragment)
                .apply(ActorSystem::unregister);
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        Chain.let(f.getActivity())
                .when(Activity::isFinishing)
                .thenTo(f)
                .when(fragment -> fragment instanceof Actor)
                .thenTo(f.getClass())
                .apply(ActorScheduler::cancel);

    }
}