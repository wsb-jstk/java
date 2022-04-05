package com.campgemini.sample;

import java.util.HashMap;
import java.util.Map;

class Database {

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
