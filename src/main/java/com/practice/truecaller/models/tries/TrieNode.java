package com.practice.truecaller.models.tries;

public class TrieNode {

    static final int ALPHABET_SIZE = 256;

    TrieNode[] children = new TrieNode[ALPHABET_SIZE];

    boolean isEndOfWord;

    String character;

    public TrieNode(String c) {
        character = c;
        isEndOfWord = false;
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            children[i] = null;
        }
    }

}
