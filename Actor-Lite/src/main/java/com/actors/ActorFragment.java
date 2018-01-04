package com.actors;

import android.app.Fragment;
import android.support.annotation.CallSuper;

/**
 * a {@link Fragment} that registers and unregisters itself from the {@link ActorSystem} and
 * {@link ActorScheduler} ... you can extend this Class or invoke the register/unregister methods
 * manually in the proper life cycle methods
 * <p>
 * Created by Ahmed Adel Ismail on 12/26/2017.
 *
 * @deprecated since version 0.0.4 all support Fragments are registered in {@link ActorSystem} if they
 * implement {@link Actor} interface, no need to extend this class if you use support Fragment,
 * this class is intended for Android old Fragments only
 */
@Deprecated
public abstract class ActorFragment extends Fragment implements Actor {

    @CallSuper
    @Override
    public void onStart() {
        super.onStart();
        ActorSystem.register(this);
    }

    @CallSuper
    @Override
    public void onStop() {
        ActorSystem.postpone(this);
        super.onStop();
    }

    @CallSuper
    @Override
    public void onDestroy() {
        ActorSystem.unregister(this);
        if (getActivity() == null || getActivity().isFinishing()) {
            ActorScheduler.cancel(getClass());
        }
        super.onDestroy();
    }
}
