package com.campgemini.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lombok.Builder;
import lombok.Data;

/**
 * @see Optional
 */
class OptionalTest {

    private Database database;

    @BeforeEach
    void setUp() {
        database = new Database();
        database.add(Person.builder()
                           .id(1)
                           .firstName("Jan")
                           .lastName("Kowalski")
                           .build());
    }

    @Test
    void findPersonWithId1() {
        // given
        final int id = 1;
        final Person person = database.find(id);
        // then
        assertThat(person).isNotNull();
    }

    @Test
    void findPersonWithId2() {
        // given
        final int id = 2;
        final Person person = database.find(id);
        // then
        assertThat(person).isNull();
    }

    private static class Database {

        private final Map<Integer, Person> persons = new HashMap<>();

        public Person find(int id) {
            if (persons.containsKey(id)) {
                return persons.get(id);
            }
            return null;
        }

        public void add(Person person) {
            persons.put(person.getId(), person);
        }

    }

    @Data
    @Builder
    private static class Person {

        private final int id;
        private final String firstName;
        private final String lastName;

    }

}
