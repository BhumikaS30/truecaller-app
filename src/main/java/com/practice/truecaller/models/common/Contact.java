package com.practice.truecaller.models.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Contact {

    private String countryCode;

    private String phoneNumber;

    private String email;
}
