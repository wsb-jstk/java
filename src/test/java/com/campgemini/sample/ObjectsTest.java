package com.campgemini.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import lombok.RequiredArgsConstructor;

/**
 * @see Objects
 */
class ObjectsTest {

    private static class SimpleA {}

    private static class SimpleB {}

    @Test
    void compareTwoClasses_notNull() {
        // given
        Object a = new SimpleA();
        Object b = new SimpleB();
        // when
        // then
        assertFalse(a == b);
        assertFalse(a.equals(b));
        assertFalse(Objects.equals(a, b));
    }

    @SuppressWarnings("RedundantCast")
    @Test
    void compareTwoClasses_whenNull() {
        // given
        Object a = (SimpleA) null;
        Object b = (SimpleB) null;
        // when
        // then
        assertTrue(null == null);
        assertTrue(a == b);
        // assertFalse(a.equals(b)); // NPE
        assertTrue(Objects.equals(a, b));
    }

    @Test
    void compareTwoObjects() {
        // given
        A a1 = new A(1, "Jan");
        A a2 = new A(1, "Jan");
        // when
        // then
        assertTrue(a1.equals(a2));
        assertTrue(a2.equals(a1));
    }

    @RequiredArgsConstructor
    private static class A {

        private final int id;
        private final String name;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof A)) {
                return false;
            }
            A a = (A) o;
            return id == a.id && Objects.equals(name, a.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, name);
        }

    }

}
