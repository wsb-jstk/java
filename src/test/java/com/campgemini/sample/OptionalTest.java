package com.campgemini.sample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        final Optional<Person> person = database.find(id);
        // then
        assertTrue(person.isPresent());
        assertFalse(person.isEmpty());
    }

    @Test
    void findPersonWithId2() {
        // given
        final int id = 2;
        final Optional<Person> person = database.find(id);
        // then
        assertFalse(person.isPresent());
        assertTrue(person.isEmpty());
    }

}
