package com.campgemini.sample;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class Database {

    private final Map<Integer, Person> persons = new HashMap<>();

    public Optional<Person> find(int id) {
        if (persons.containsKey(id)) {
            return Optional.of(persons.get(id));
        }
        return Optional.empty();
    }

    public void add(Person person) {
        persons.put(person.getId(), person);
    }

}
