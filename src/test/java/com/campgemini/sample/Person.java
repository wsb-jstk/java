package com.campgemini.sample;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
class Person {

    private final int id;

    private final String firstName;

    private final String lastName;

}
