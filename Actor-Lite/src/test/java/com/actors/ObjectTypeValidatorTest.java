package com.actors;


import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectTypeValidatorTest {

    @Test
    public void compareValidClassAndClassThenReturnTrue() {
        boolean result = new ObjectTypeValidator().test(A.class, A.class);
        assertTrue(result);
    }

    @Test
    public void compareValidObjectAndClassThenReturnTrue() {
        boolean result = new ObjectTypeValidator().test(new A(), A.class);
        assertTrue(result);
    }

    @Test
    public void compareValidObjectAndObjectThenReturnTrue() {
        boolean result = new ObjectTypeValidator().test(new A(), new A());
        assertTrue(result);
    }

    @Test
    public void compareInvalidClassAndClassThenReturnFalse() {
        boolean result = new ObjectTypeValidator().test(A.class, B.class);
        assertFalse(result);
    }

    @Test
    public void compareInvalidObjectAndClassThenReturnFalse() {
        boolean result = new ObjectTypeValidator().test(new A(), B.class);
        assertFalse(result);
    }

    @Test
    public void compareInvalidObjectAndObjectThenReturnTrue() {
        boolean result = new ObjectTypeValidator().test(new A(), new B());
        assertFalse(result);
    }

    @Test
    public void compareNullAndClassThenReturnFalse() {
        boolean result = new ObjectTypeValidator().test(null, A.class);
        assertFalse(result);
    }

    @Test
    public void compareNullAndObjectThenReturnFalse() {
        boolean result = new ObjectTypeValidator().test(null, new A());
        assertFalse(result);
    }

    @Test
    public void compareNullAndNullThenReturnTrue() {
        boolean result = new ObjectTypeValidator().test(null, null);
        assertTrue(result);
    }

    private static class A {
    }

    private static class B {
    }
}