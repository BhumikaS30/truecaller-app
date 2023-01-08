package com.practice.truecaller.models.common;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialInfo {

    private static Map<SocialInfoType, String> socialInfo = new HashMap<>();

}
