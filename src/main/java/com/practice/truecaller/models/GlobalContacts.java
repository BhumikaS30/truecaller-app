package com.practice.truecaller.models;

import com.practice.truecaller.models.tries.Trie;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class GlobalContacts {

    public static GlobalContacts INSTANCE = new GlobalContacts();
    @Getter
    private Trie contactTrie = new Trie();

}
