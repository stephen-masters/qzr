package com.sctrcd.qzr.facts;

import org.joda.time.LocalDate;

public class Person {

    private String name;
    
    private LocalDate dateOfBirth;
    
    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String toString() {
        return "Person: { name=\"" + name + "\", dateOfBirth=" + dateOfBirth + " }";
    }

}
