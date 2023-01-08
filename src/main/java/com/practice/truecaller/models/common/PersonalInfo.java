package com.practice.truecaller.models.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonalInfo {

    private String firstName;

    private String middleName;

    private String lastName;

    private String title;

    private String dob;

    private Gender gender;

    private Address address;

    private String companyName;

    public PersonalInfo(String firstName) {
        this.firstName = firstName;
    }

    public PersonalInfo(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
