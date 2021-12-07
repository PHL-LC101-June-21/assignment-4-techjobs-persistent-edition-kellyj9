package org.launchcode.techjobs.persistent.models;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

// Employer is a class that will be mapped to one of our tables.
// The class has the @Entity annotation as well as the no-arg constructor
// required for Hibernate to create an object.

@Entity
public class Employer extends AbstractEntity {

    @NotBlank
    @Size(min=1, max=255)
    private String location;

    public Employer() {
    }

    public Employer(String location) {
        this.location = location;
    }
}
