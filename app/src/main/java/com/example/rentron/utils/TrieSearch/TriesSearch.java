package com.example.rentron.utils.TrieSearch;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Class to work with a collection of Trie data structures.
 * Each Trie inside this collection of TriesSearch represents one row of a table of data.
 * This row could be a sentence or a list of keywords.
 */
public class TriesSearch {

    /**
     * Map to store all Trie data structures.
     * Each key-value pair represents a String key to identify which Trie had a match and
     * the value is the Trie instance containing all words for that key.
     */
    private Map<String, TrieNode> tries;

    /**
     * Constructor to initialize an empty Trie.
     */
    public TriesSearch() {
        this.tries = new HashMap<>();
    }

    /**
     * Constructor to initialize an empty Trie with a fixed size.
     */
    public TriesSearch(int size) {
        this.tries = new HashMap<>(size);
    }

    /**
     * Constructor to initialize an empty Trie with a fixed size.
     */
    public TriesSearch(Map<String, List<String>> wordsData) {
        this.tries = getTries(wordsData);
    }

    /**
     * Method accepts a list of words and returns a new TrieNode instance.
     *
     * @param words list of words to be added to the TrieNode
     * @return an instance of TrieNode containing the provided words
     */
    public static TrieNode getTrie(List<String> words) {
        // initialize an empty TrieNode
        TrieNode trieNode = new TrieNode();
        // insert all words
        for (String word : words) {
            trieNode.insert(word);
        }
        // return the TrieNode instance
        return trieNode;
    }

    /**
     * Generates a Map containing String as key and a Trie created from a list of words provided for that String key.
     *
     * @param wordsData a Map containing String keys and corresponding list of words
     * @return a Map containing String keys and corresponding Trie objects
     */
    public static Map<String, TrieNode> getTries(Map<String, List<String>> wordsData) {
        // initialize a map to store the result
        Map<String, TrieNode> tries = new HashMap<>(wordsData.size());
        // for each list of words, create a Trie and store it with the corresponding String key
        for (String wordKey : wordsData.keySet()) {
            tries.put(wordKey, getTrie(wordsData.get(wordKey)));
        }
        // return the resulting map
        return tries;
    }

    /**
     * Set the data map containing String keys identifying each Trie and values representing
     * the corresponding Trie instance containing all the list of words for that key.
     *
     * @param wordsData a map in which keys represent string values to identify a trie (or row of data)
     *                  and values are a list of words (like keywords)
     */
    public void setTries(Map<String, List<String>> wordsData) {
        this.tries = getTries(wordsData);
    }

    /**
     * Add data to TrieSearch dataset by providing an id and the keywords.
     *
     * @param trieId   id which is returned if a match is found in the provided keywords
     * @param keywords list of string keywords
     */
    public void addData(String trieId, List<String> keywords) {
        this.tries.put(trieId, getTrie(keywords));
    }

    /**
     * pMatch - Pattern Match.
     * Method performs a non-exact search of a query in all the tries.
     *
     * @param query string representing characters to be found
     * @return list of string values identifying the TriesSearch in which matches were found, empty list if no matches
     */
    public List<String> pMatch(String query) {
        // ensure we have valid data & query
        if (this.tries == null || this.tries.isEmpty() || query == null || query.isEmpty()) {
            return null;
        }

        // list to store matches
        List<String> matches = new ArrayList<>();

        // iterate through all tries
        for (String trieKey : this.tries.keySet()) {
            // if the current trie has a pMatch, add the Trie key to matches
            if (this.tries.get(trieKey) != null && this.tries.get(trieKey).pMatch(query)) {
                matches.add(trieKey);
            }
        }

        // return the result
        return matches;
    }

    /**
     * eMatch - Exact Match.
     * Method performs a search to find an exact match of the query provided in all tries.
     *
     * @param query string representing characters to be found
     * @return list of string values identifying the TriesSearch in which matches were found, empty list if no matches
     */
    public List<String> eMatch(String query) {
        // ensure we have valid data & query
        if (this.tries == null || this.tries.isEmpty() || query == null || query.isEmpty()) {
            return null;
        }

        // list to store matches
        List<String> matches = new ArrayList<>();

        // iterate through all tries
        for (String trieKey : this.tries.keySet()) {
            // if the current trie has an eMatch, add the Trie key to matches
            if (this.tries.get(trieKey) != null && this.tries.get(trieKey).eMatch(query)) {
                matches.add(trieKey);
            }
        }

        // return the result
        return matches;
    }
}
