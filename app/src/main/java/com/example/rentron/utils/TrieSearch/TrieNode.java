package com.example.rentron.utils.TrieSearch;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a single Trie-node.
 */
public class TrieNode {

    // Map to store children nodes.
    private Map<Character, TrieNode> children;

    // Flag to indicate if a complete word.
    private boolean isCompleteWord;

    /**
     * Constructor to initialize a Trie node.
     */
    protected TrieNode() {
        this.children = new HashMap<>();
        this.isCompleteWord = false;
    }

    /**
     * Inserts a word into the Trie.
     *
     * @param word string representing the word.
     */
    protected void insert(String word) {
        // Current node will initially be root.
        TrieNode currentNode = this;
        // Use only lower case characters.
        word = word.toLowerCase(Locale.ROOT);

        // Add each character.
        for (char c : word.toCharArray()) {
            // Store a new child if character not already there.
            currentNode.children.putIfAbsent(c, new TrieNode());
            // Move to the next node.
            currentNode = currentNode.children.get(c);
        }

        // Once all characters added, mark the word as complete.
        currentNode.isCompleteWord = true;
    }

    /**
     * pMatch - Pattern Match.
     * Method performs a non-exact search of a query in the Trie data.
     *
     * @param query string representing characters to be found in Trie.
     * @return True if match found, else false.
     */
    protected boolean pMatch(String query) {
        // Validate query.
        if (query == null || query.isEmpty()) {
            return false;
        }

        // Turn query to lower case.
        query = query.toLowerCase(Locale.ROOT);

        // Start from root.
        TrieNode currentNode = this;

        // Check presence of characters.
        for (char c : query.toCharArray()) {
            // Get the node for current character.
            currentNode = currentNode.children.get(c);
            // If character doesn't exist, means no match.
            if (currentNode == null) {
                return false;
            }
        }
        // If all characters of query were found.
        return true;
    }

    /**
     * eMatch - Exact Match.
     * Method performs a search to find an exact match of the query provided in Trie data.
     *
     * @param word string representing the word to be found in Trie.
     * @return True if match found, else false.
     */
    protected boolean eMatch(String word) {
        // Validate word.
        if (word == null || word.isEmpty()) {
            return false;
        }

        // Turn word to lower case.
        word = word.toLowerCase(Locale.ROOT);

        // Start from root.
        TrieNode currentNode = this;

        // Check presence of characters.
        for (char c : word.toCharArray()) {
            // Get the node for current character.
            currentNode = currentNode.children.get(c);
            // If character doesn't exist, means no match.
            if (currentNode == null) {
                return false;
            }
        }
        // Return true if all characters were found, and we've reached the end of a complete word.
        return currentNode.isCompleteWord;
    }
}
