package com.campgemini.sample;

import static org.assertj.core.api.Assertions.assertThat;

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

}
