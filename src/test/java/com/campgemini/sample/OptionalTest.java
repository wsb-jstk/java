package com.campgemini.sample;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    /**
     * Get to know {@link Supplier}
     */
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

    /**
     * Get to know {@link Supplier}
     */
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

    /**
     * Get to know {@link Consumer} and {@link Runnable}
     */
    @Test
    void showPassWhenSearchingForExistingPerson() {
        // given
        final int id = 1;
        final Optional<Person> person = database.find(id);
        final Consumer<Person> personConsumer = p -> assertNotNull(p);
        final Runnable runnable = () -> {throw new NoSuchElementException();};
        // when
        person.ifPresentOrElse(personConsumer, runnable);
    }

    /**
     * Get to know {@link Consumer} and {@link Runnable}
     */
    @Test
    void showPassWhenSearchingForNotExistingPerson() {
        // given
        final int id = 2;
        final Optional<Person> person = database.find(id);
        final Consumer<Person> personConsumer = p -> assertNotNull(p);
        final Runnable runnable = () -> {throw new NoSuchElementException();};
        // when
        assertThrows(NoSuchElementException.class, () -> person.ifPresentOrElse(personConsumer, runnable));
    }

    /**
     * This is run in framework's thread. Meaning that it will end up (at some point)
     */
    @Test
    void runThread() {
        final Runnable runnable = () -> {
            while (true) {
                System.out.println(".");
            }
        };
        final Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Set as private, so IDEA won't propose to run it as a program.
     * If You want to test it - change visibility to public
     */
    private static void main(String[] args) {
        final Runnable runnable = () -> {
            for (; ; ) {
                // for(int i = 0; i < 5; i++) {
                System.out.println(".");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    log.error("Error while sleeping", e);
                }
            }
        };
        final Thread thread = new Thread(runnable);
        log.debug("This thread is daemon? {}", thread.isDaemon());
        // thread.setDaemon(true);
        thread.start();
    }

    private String getFullName(Optional<Person> optionalPerson, Supplier<Person> defaultPersonSupplier) {
        // optionalPerson.orElseThrow(RuntimeException::new);
        // optionalPerson.orElseThrow(() -> new RuntimeException());
        // Supplier<RuntimeException> exceptionSupplier = () -> new RuntimeException();
        // optionalPerson.orElseThrow(exceptionSupplier);
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
