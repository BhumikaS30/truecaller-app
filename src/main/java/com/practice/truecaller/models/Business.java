package com.practice.truecaller.models;

import com.practice.truecaller.models.common.BusinessSize;
import com.practice.truecaller.models.common.Contact;
import com.practice.truecaller.models.common.PersonalInfo;
import com.practice.truecaller.models.common.SocialInfo;
import com.practice.truecaller.models.common.Tag;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Business {

    private String businessName;

    private String businessDescription;

    private BusinessSize businessSize;

    private Tag businessTag;

    private Contact contact;

    private PersonalInfo personalInfo;

    private SocialInfo socialInfo;

    public Business(String businessName, Tag businessTag) {
        this.businessName = businessName;
        this.businessTag = businessTag;
    }
}
