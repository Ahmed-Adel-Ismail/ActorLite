package com.actors;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Single;

/**
 * the parent class for Typed Maps
 * <p>
 * Created by Ahmed Adel Ismail on 10/15/2017.
 */
class TypedMap<V> implements Iterable<Entry<Object, V>> {

    private final Map<Object, V> map;

    TypedMap(Map<Object, V> map) {
        this.map = map;
    }

    /**
     * check if a <b>type</b> of an object is available in the {@link Map#keySet()}
     *
     * @param type the type to search for
     * @return a {@link Single} of {@code true} if any Object in the {@link Map#keySet()} is of
     * the same type passed
     */
    @NonNull
    public Single<Boolean> containsKey(Class<?> type) {
        return Observable.fromIterable(map.keySet())
                .map(new ObjectTypeRetriever())
                .any(type::equals);

    }

    /**
     * check if a an object is available in the {@link Map#keySet()}
     *
     * @param key the instance to search for
     * @return a {@link Single} of {@code true} if any Object in the {@link Map#keySet()} is of
     * the same type passed
     */
    @NonNull
    public Single<Boolean> containsKey(Object key) {
        return Single.just(map.containsKey(key));
    }

    /**
     * same as {@link #get(Object)} but it does not return {@link NoSuchElementException}
     * if the element is not found
     *
     * @param key the key to search for
     * @return a {@link Maybe} holding the value mapped to the passed instance, or
     * empty {@link Maybe} if nothing is found
     */
    @NonNull
    public Maybe<V> getOrIgnore(Object key) {
        return get(key).toMaybe().onErrorComplete();
    }

    /**
     * get the value mapped to the passed key, this is the same as
     * {@link Map#get(Object)} with an extra feature, if there is a key of the same type
     * is available, it will return it's value
     *
     * @param key the key to search for
     * @return a {@link Single} holding the value mapped to the passed instance,
     * if no key was found, an error {@link Single} is returned holding {@link NoSuchElementException}
     */
    @NonNull
    public Single<V> get(Object key) {
        if (map.containsKey(key)) {
            return Single.just(map.get(key));
        } else if (key != null && map.containsKey(key.getClass())) {
            return Single.just(map.get(key.getClass()));
        } else {
            return Single.error(new NoSuchElementException("no value mapped to : " + key));
        }
    }

    /**
     * get an {@link Observable} that emits all the values fount that are mapped to
     * keys that there type matches the passed {@link Class} parameter
     *
     * @param type the type of the key for those values
     * @return a {@link Observable} emitting the stored values, or it will emit
     * {@link NoSuchElementException} if no value was found
     */
    @NonNull
    public Observable<V> get(@NonNull Class<?> type) {
        return getOrIgnore(type).switchIfEmpty(errorObservable(type));

    }

    /**
     * same as {@link #get(Class)} but it does not return {@link NoSuchElementException}
     * if the element is not found
     *
     * @param type the type of the key for those values
     * @return a {@link Observable} emitting the stored values
     */
    @NonNull
    public Observable<V> getOrIgnore(Class<?> type) {
        return Observable.fromIterable(map.entrySet())
                .filter(entry -> entry.getKey() != null)
                .filter(entry -> new ObjectTypeValidator().test(type, entry.getKey()))
                .map(Entry::getValue);
    }


    @NonNull
    private Observable<V> errorObservable(@NonNull final Class<?> type) {
        return new Observable<V>() {
            @Override
            protected void subscribeActual(Observer<? super V> observer) {
                observer.onError(new NoSuchElementException("no value mapped to : "
                        + type.getSimpleName()));
            }
        };
    }


    /**
     * put an Object instance as a key, and an item as it's value
     *
     * @param key   the key stored in {@link WeakReference}
     * @param value the value for this key
     */
    @NonNull
    public TypedMap<V> put(Object key, V value) {
        map.put(key, value);
        return this;
    }

    /**
     * remove an {@link Entry} with the passed key
     *
     * @param key the key to remove
     */
    @NonNull
    public TypedMap<V> remove(Object key) {
        map.remove(key);
        return this;
    }

    /**
     * clear the current stored values
     */
    @NonNull
    public TypedMap<V> clear() {
        map.clear();
        return this;
    }

    /**
     * get the size of the current {@link Map}
     *
     * @return the size
     */
    public int size() {
        return map.size();
    }

    /**
     * check if the current {@link Map} is empty or not
     *
     * @return {@code true} if there is nothing stored in this Object
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }


    @Override
    public Iterator<Entry<Object, V>> iterator() {
        return map.entrySet().iterator();
    }
}
