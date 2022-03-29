package com.campgemini.sample;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * @see Collections
 */
public class CollectionsTest {

    @Test
    void emptyList() {
        // given
        final List<Integer> list = Collections.emptyList();
        // then
        assertThat(list, hasSize(0));
        assertThrows(UnsupportedOperationException.class, () -> list.add(2));
    }

    /**
     * Do czego mozna uzyc {@link Collections#emptyList()}
     */
    private static class FinderImpl implements MyFinder {

        private MyRepository repository;

        @Override
        public List<String> findCustomer(int id) {
            List<String> result = repository.find(id);
            if (result.isEmpty())
                return Collections.emptyList();
            return result;
        }

    }

    private interface MyFinder {

        List<String> findCustomer(int id);

    }

    private interface MyRepository {

        List<String> find(int id);

    }

    @Test
    void singletonList() {
        // given
        final String element = "Jan";
        // when
        final List<String> list = Collections.singletonList(element);
        // then
        assertThat(list, hasSize(1));
        assertThrows(UnsupportedOperationException.class, () -> list.add("Kowalski"));
    }

    @Test
    void sort() {
        // given
        final List<String> words = Arrays.asList("ala", "ma", "czarnego", "kota");
        List<String> copy;
        // when
        copy = new ArrayList<>(words);
        copy.sort(null);
        assertThat(copy, contains("ala", "czarnego", "kota", "ma"));
        // when
        copy = new ArrayList<>(words);
        Collections.sort(copy, null);
        assertThat(copy, contains("ala", "czarnego", "kota", "ma"));
        // when
        copy = new ArrayList<>(words);
        Collections.sort(copy);
        assertThat(copy, contains("ala", "czarnego", "kota", "ma"));
        // when
        copy = new ArrayList<>(words);
        Collections.sort(copy, new StringComparator());
        assertThat(copy, contains("ala", "czarnego", "kota", "ma"));
        // when
        copy = new ArrayList<>(words);
        Collections.sort(copy, (o1, o2) -> o1.compareTo(o2));
        assertThat(copy, contains("ala", "czarnego", "kota", "ma"));
        // when
        copy = new ArrayList<>(words);
        Collections.sort(copy, String::compareTo);
        assertThat(copy, contains("ala", "czarnego", "kota", "ma"));
    }

    private static class StringComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }

    }

    @Test
    void reverse() {
        // given
        final List<String> words = Arrays.asList("ala", "ma", "czarnego", "kota");
        // when
        Collections.reverse(words);
        // then
        assertThat(words, contains("kota", "czarnego", "ma", "ala"));
    }

    @Test
    void sort_reverse() {
        // given
        final List<String> words = Arrays.asList("ala", "ma", "czarnego", "kota");
        List<String> copy;
        // when
        copy = new ArrayList<>(words);
        copy.sort(Comparator.reverseOrder());
        assertThat(copy, contains("ma", "kota", "czarnego", "ala"));
        // when
        copy = new ArrayList<>(words);
        Collections.sort(copy, (o1, o2) -> o2.compareTo(o1));
        assertThat(copy, contains("ma", "kota", "czarnego", "ala"));
        // when
        copy = new ArrayList<>(words);
        StringComparator comparator = new StringComparator();
        Collections.sort(copy, comparator.reversed());
        assertThat(copy, contains("ma", "kota", "czarnego", "ala"));
    }

    @Test
    void sort_defaultOrder() {
        final List<String> list = Arrays.asList("aa", "a", "A", "Aa", "1", "9", "10", " ", " 1", "2a", "Z");
        // when
        Collections.sort(list);
        // then
        // A = 65 (left ALT + numeric)
        // a = 97
        assertThat(list, contains(" ", " 1", "1", "10", "2a", "9", "A", "Aa", "Z", "a", "aa"));
    }

    @Test
    void testBinarySearch_found() {
        // given
        final List<String> list = Arrays.asList("30", "8", "3A", "FF");
        // when
        Collections.sort(list);
        // then
        assertThat(list, contains("30", "3A", "8", "FF"));
        assertThat(Collections.binarySearch(list, "30"), is(0));
        assertThat(Collections.binarySearch(list, "3A"), is(1));
        assertThat(Collections.binarySearch(list, "8"), is(2));
        assertThat(Collections.binarySearch(list, "FF"), is(3));
        assertThat(Collections.binarySearch(list, "4F"), is(-3)); // -2-1
    }

}
