package com.actors;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Ahmed Adel Ismail on 10/11/2017.
 */
public class ObjectTypeRetrieverTest {


    @Test
    public void passObjectAndReturnAsClass() {
        Class<?> result = new ObjectTypeRetriever().apply(new A());
        assertTrue(result.equals(A.class));
    }

    @Test
    public void passClassAndReturnAsClass() {
        Class<?> result = new ObjectTypeRetriever().apply(A.class);
        assertTrue(result.equals(A.class));
    }

    @Test
    public void passNullAndReturnAsNullAddress() {
        Class<?> result = new ObjectTypeRetriever().apply(null);
        assertTrue(result.equals(ObjectTypeRetriever.NullType.class));
    }


    private static class A {

    }


}