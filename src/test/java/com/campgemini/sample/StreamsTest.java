package com.campgemini.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#Reduction">Stream API</a>
 */
@Slf4j
class StreamsTest {

    private Database database;

    @BeforeEach
    void setUp() {
        database = new Database();
        database.add(Person.builder()
                           .id(1)
                           .firstName("Jan")
                           .lastName("Kowalski")
                           .salary(2_000)
                           .build());
        database.add(Person.builder()
                           .id(2)
                           .firstName("Karolina")
                           .lastName("Piekna")
                           .salary(500)
                           .build());
        database.add(Person.builder()
                           .id(3)
                           .firstName("John")
                           .lastName("Smith")
                           .salary(100)
                           .build());
    }

    @Test
    void shouldReturnSizeOfDatabase() {
        // given
        final List<Person> list = database.findAll();
        // when
        assertThat(list).hasSize(3);
    }

    @Test
    void simple_streamTest() {
        // given
        final List<Person> all = database.findAll();
        // when
        final List<Long> salaries = all.stream()
                                       .filter(p -> p.getSalary() > 0)
                                       .peek(p -> log.debug("A: {}", p))
                                       .filter(p -> p.getFirstName()
                                                     .startsWith("J"))
                                       .map(p -> p.getSalary())
                                       .peek(s -> log.debug("B: {}", s))
                                       // .forEach(s -> {})
                                       .collect(Collectors.toList());
        // then
        assertThat(salaries).hasSize(2);
    }

    @Test
    void feature_consumable() {
        // given
        List<Person> all = database.findAll();
        Stream<Person> personStream = all.stream()
                                         .filter(p -> p.getSalary() > 1_000);
        // when
        final long count = personStream.count();
        // then
        assertEquals(1, count);
        // try to re-use stream on which I've already called terminal method (count())
        assertThrows(IllegalStateException.class, () -> personStream.count());
    }

}
