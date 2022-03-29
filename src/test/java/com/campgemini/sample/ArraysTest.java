package com.campgemini.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ArraysTest {

    @Test
    void listInit() {
        // given
        final Integer[] array = { 1, 2, 3 };
        // when
        final List<Integer> list0 = Arrays.asList(array);
        final List<Integer> list1 = Arrays.stream(array)
                                          .collect(Collectors.toList());
        // then
        org.assertj.core.api.Assertions.assertThat(list0)//
                                       .isNotNull()//
                                       .contains(1, 2, 3)//
                                       .isEqualTo(list1)//
                                       .containsExactlyElementsOf(list1);
    }

    @Test
    void asList() {
        // given
        final List<Integer> list = Arrays.asList(1, 2, 3);
        // then
        assertThat(list, hasSize(3));
        assertThat(list, contains(1, 2, 3));
        assertThrows(UnsupportedOperationException.class, () -> list.add(10));
    }

    @Test
    void fill() {
        // given
        final int[] ints = new int[10];
        log.debug("Initial array: {}", ints);
        log.debug("Value on first index: {}", ints[0]);
        // when
        Arrays.fill(ints, 5);
        // then
        org.assertj.core.api.Assertions.assertThat(ints)//
                                       .isNotNull()//
                                       .containsExactly(5, 5, 5, 5, 5, 5, 5, 5, 5, 5);
    }

    @Test
    void fill_withRange() {
        // given
        final int[] ints = new int[5];
        log.debug("Initial array: {}", ints);
        // when
        Arrays.fill(ints, 0, 2, 1);
        Arrays.fill(ints, 2, 4, 2);
        // then
        org.assertj.core.api.Assertions.assertThat(ints)//
                                       .isNotNull()//
                                       .containsExactly(1, 1, 2, 2, 0);
    }

    @Test
    void toString_forEmptyArray() {
        // given
        assertEquals("[0, 0, 0]", Arrays.toString(new int[3]));
        assertEquals("[null, null, null]", Arrays.toString(new Integer[3]));
    }

    /**
     * @see System#arraycopy(Object, int, Object, int, int)
     */
    @Test
    void copyOfRange() {
        // given
        final int[] ints = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        final int[] expectedResult = { 0, 1, 2 };
        // when
        final int[] copied = Arrays.copyOfRange(ints, 0, 3);
        // then
        assertThat(copied, is(expectedResult));
    }

}
