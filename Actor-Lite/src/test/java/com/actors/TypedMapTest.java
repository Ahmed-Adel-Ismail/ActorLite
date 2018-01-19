package com.actors;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class TypedMapTest {


    @Test
    public void putItemThenReturnSizeAsOne() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        assertEquals(1, map.put(0, 0).size());
    }

    @Test
    public void removeTheOnlyItemThenReturnIsEmptyAsTrue() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        assertTrue(map.put(0, 0).remove(0).isEmpty());
    }

    @Test
    public void clearNonEmptyMapThenReturnIsEmptyAsTrue() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        assertTrue(map.put(0, 0).clear().isEmpty());
    }

    @Test
    public void containsValidClassKeyThenReturnTrue() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        assertTrue(map.put(0, 0).put(new Object(), 1).containsKey(Integer.class).blockingGet());
    }

    @Test
    public void containsValidObjectKeyThenReturnTrue() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        assertTrue(map.put(0, 0).put(new Object(), 1).containsKey(0).blockingGet());
    }

    @Test
    public void getBySameObjectKeyThenReturnValue() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        A key = new A();
        map.put(key, 1);
        assertEquals(1L, (long) map.get(key).blockingGet());
    }

    @Test
    public void getBySameClassKeyThenReturnValue() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        map.put(A.class, 1);
        assertEquals(1L, (long) map.get(A.class).blockingFirst());
    }

    @Test
    public void getObjectKeyByClassThenReturnValue() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        map.put(new A(), 1);
        assertTrue(map.get(A.class).blockingFirst() == 1);
    }

    @Test
    public void getClassKeyByObjectThenReturnValue() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        map.put(A.class, 1);
        assertTrue(map.get(new A()).blockingGet() == 1);
    }

    @Test
    public void getOrIgnoreInvalidItemByObjectKeyThenReturnEmptyMaybe() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        assertNull(map.getOrIgnore(new A()).blockingGet());
    }

    @Test
    public void getOrIgnoreValidItemByObjectKeyThenReturnValue() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        A key = new A();
        map.put(key, 1);
        assertEquals(1, (long) map.getOrIgnore(key).blockingGet());
    }

    @Test
    public void getOrIgnoreInvalidItemByClassKeyThenReturnEmptyObservable() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        assertTrue(map.getOrIgnore(A.class).isEmpty().blockingGet());
    }

    @Test
    public void getOrIgnoreValidItemByClassKeyThenReturnValue() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        map.put(A.class, 1);
        assertEquals(1, (long) map.getOrIgnore(A.class).blockingFirst());
    }

    @Test
    public void putDuplicateClassKeyAndGetByClassThenReturnLastOneOnly() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        map.put(A.class, 1).put(A.class, 2);
        assertTrue(map.get(A.class).blockingFirst() == 2);
    }

    @Test
    public void putDuplicateObjectKeyAndGetByClassThenReturnLastOneOnly() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        A key = new A();
        map.put(key, 1).put(key, 2);
        assertTrue(map.get(key).blockingGet() == 2);
    }

    @Test
    public void putObjectKeysWithSameTypeAndGetByObjectThenReturnOneValueOnly() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        A keyOne = new A();
        A keyTwo = new A();
        map.put(keyOne, 1).put(keyTwo, 2);
        assertTrue(map.get(keyTwo).blockingGet() == 2);
    }

    @Test
    public void putObjectKeysWithSameTypeAndGetByClassThenReturnAllValues() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        A keyOne = new A();
        A keyTwo = new A();
        map.put(keyOne, 1).put(keyTwo, 2);
        List<Integer> result = map.get(A.class).toList().blockingGet();
        assertTrue(result.contains(1) && result.contains(2));
    }

    @Test
    public void putObjectKeyAndClassKeyWithSameTypesAndGetByClassKeyThenReturnAllValues() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        A key = new A();
        map.put(key, 1).put(A.class, 2);
        List<Integer> result = map.get(A.class).toList().blockingGet();
        assertTrue(result.contains(1) && result.contains(2));
    }

    @Test
    public void putObjectKeyAndClassKeyWithSameTypesAndGetByObjectKeyThenReturnObjectValues() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        A key = new A();
        map.put(key, 1).put(A.class, 2);
        assertEquals(1, (long) map.get(key).blockingGet());
    }

    @Test(expected = NoSuchElementException.class)
    public void getObjectKeyFromEmptyMapThenThrowNoSuchElement() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        map.get(new A()).blockingGet();
    }

    @Test(expected = NoSuchElementException.class)
    public void getClassKeyFromEmptyMapThenThrowNoSuchElement() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        map.get(A.class).blockingFirst();
    }

    @Test(expected = NoSuchElementException.class)
    public void getNonExistingObjectKeyFromNonEmptyMapThenThrowNoSuchElement() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        map.put(new A(), 1).get(new A()).blockingGet();
    }

    @Test(expected = NoSuchElementException.class)
    public void getNonExistingClassKeyFromNonEmptyMapThenThrowNoSuchElement() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        map.put(B.class, 1).get(A.class).blockingFirst();
    }

    @Test
    public void putTwoObjectKeysOfSameTypeAndRemoveOldThenGetByClassKeyReturnsNewOne() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        A keyOne = new A();
        A keyTwo = new A();
        map.put(keyOne, 1).put(keyTwo, 2).remove(keyOne);
        assertEquals(2, (long) map.get(A.class).blockingFirst());
    }

    @Test
    public void convertToIterableThenReturnEntrySetOfMap() {
        TypedMap<Integer> map = new TypedMap<>(new HashMap<Object,Integer>());
        map.put(new A(), 1).put(new B(), 2);
        List<Integer> result = Observable.fromIterable(map)
                .map(new Function<Entry<Object, Integer>, Integer>() {
                    @Override
                    public Integer apply(@NonNull Entry<Object, Integer> entry) {
                        return entry.getValue();
                    }
                })
                .toList()
                .blockingGet();

        assertEquals(2, result.size());
    }


    private static class A {
    }

    private static class B {
    }


}