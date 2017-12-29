package com.actors;

import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

/**
 * a group of {@link Disposable} instances
 * <p>
 * Created by Ahmed Adel Ismail on 5/26/2017.
 */
class DisposablesGroup extends HashMap<Integer, Disposable> {

    /**
     * remove a {@link Disposable} if found, this method invokes {@link Disposable#dispose()}
     * before removing
     *
     * @param key the key for that {@link Disposable}
     * @return the removed {@link Disposable}
     */
    @Override
    public Disposable remove(Object key) {
        Disposable disposable = get(key);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        return super.remove(key);
    }

    @Override
    public void clear() {
        Observable.fromIterable(keySet()).blockingForEach(this::remove);
        super.clear();
    }
}
