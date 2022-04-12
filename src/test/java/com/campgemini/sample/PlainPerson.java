package com.campgemini.sample;

import lombok.Builder;
import lombok.Data;

/**
 * Simpler model for {@link Person}
 */
@Data
@Builder
class PlainPerson {

    private final String name;

}
