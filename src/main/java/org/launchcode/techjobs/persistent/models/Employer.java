package org.launchcode.techjobs.persistent.models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

// Employer is a class that will be mapped to one of our tables.
// The class has the @Entity annotation as well as the no-arg constructor
// required for Hibernate to create an object.

@Entity
public class Employer extends AbstractEntity {

    @NotBlank
    @Size(min=1, max=255)
    private String location;

    @OneToMany
    @JoinColumn(name="jobId")
    private final List<Job> jobs = new ArrayList<>();

    public Employer() {
    }

    public Employer(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
