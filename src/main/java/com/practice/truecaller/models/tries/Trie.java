package com.practice.truecaller.models.tries;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;
import static java.util.Objects.isNull;

public class Trie {

    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode("");
    }

    public void insertInTrie (String key) {

        TrieNode pCrawl = root;

        for (int i = 0; i < key.length(); i++) {
            int index = Math.abs(key.charAt(i) - 'a');
            TrieNode child = pCrawl.children[index];

            if (isNull(child)) {
                pCrawl.children[index] = new TrieNode(valueOf(key.charAt(i)));
            }
            pCrawl = pCrawl.children[index];
        }
        // mark last node as leaf
        pCrawl.isEndOfWord = true;
    }

    public boolean searchInTrie (String key) {

        TrieNode pCrawl = root;

        for (int i = 0; i < key.length(); i++) {

            int index = Math.abs(key.charAt(i) - 'a');
            TrieNode child = pCrawl.children[index];
            if (isNull(child)) {
                return false;
            } else {
                pCrawl = pCrawl.children[index];
            }
        }
        return pCrawl.isEndOfWord;
    }

    public List<String> allWordsWithPrefix(String prefix) {
        TrieNode trieTrieNode = root;
        List<String> allWords = new ArrayList<>();
        for (int i = 0; i < prefix.length(); ++i) {
            int asciiIndex = Math.abs(prefix.charAt(i) - 'a');
            trieTrieNode = trieTrieNode.children[asciiIndex];
        }
        getSuffixes(trieTrieNode, prefix, allWords);
        return allWords;
    }

    private void getSuffixes(TrieNode trieNode, String prefix, List<String> allWords) {
        if (trieNode == null) return;
        if (trieNode.isEndOfWord) {
            allWords.add(prefix);
        }
        for (TrieNode childTrieNode : trieNode.children) {
            if (isNull(childTrieNode)) continue;
            String childCharacter = childTrieNode.character;
            getSuffixes(childTrieNode, prefix + childCharacter, allWords);
        }
    }

}