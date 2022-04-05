package com.campgemini.sample;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lombok.extern.slf4j.Slf4j;

/**
 * @see Optional
 */
@Slf4j
class OptionalTest {

    private static final String UNKNOWN = "unknown";
    private static final Person DEFAULT_VALUE = new Person(0, UNKNOWN, UNKNOWN);

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

    @Test
    void shouldGetFullName_forPersonWithId1() {
        // given
        final int id = 1;
        final Optional<Person> person = database.find(id);
        final Supplier<Person> defaultPersonSupplier = () -> fetchDefaultPerson();
        // when
        final String fullName = getFullName(person, defaultPersonSupplier);
        // then
        assertEquals("Jan Kowalski", fullName);
    }

    @Test
    void shouldThrowException_whenGettingFullName_forPersonWithId2() {
        // given
        final int id = 2;
        final Optional<Person> person = database.find(id);
        final Supplier<Person> defaultPersonSupplier = () -> fetchDefaultPerson();
        // when
        final String fullName = getFullName(person, defaultPersonSupplier);
        // then
        assertEquals("unknown unknown", fullName);
    }

    private String getFullName(Optional<Person> optionalPerson, Supplier<Person> defaultPersonSupplier) {
        final Person person = optionalPerson.orElseGet(defaultPersonSupplier);
        return person.getFirstName() + " " + person.getLastName();
    }

    private Person fetchDefaultPerson() {
        log.debug("Fetching...");
        try {
            // mimic long lasting operation
            Thread.sleep(2_000);
        } catch (InterruptedException e) {
            log.error("Error when sleeping", e);
        }
        log.debug("Fetched");
        return DEFAULT_VALUE;
    }

    @FunctionalInterface
    public interface TestInterface {

        boolean something(); // key method!!!

        default boolean equals(Person object) {
            return false;
        }

        default boolean equals1(Person object) {
            return false;
        }

        default boolean equals2(Person object) {
            return false;
        }

        default boolean equals3(Person object) {
            return false;
        }

    }

}
