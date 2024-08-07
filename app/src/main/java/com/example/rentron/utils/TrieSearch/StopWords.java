package com.example.rentron.utils.TrieSearch;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to handle stop words in the English language.
 */
public class StopWords {

    private static final Map<String, Boolean> engStopWords = new HashMap<String, Boolean>() {{
        put("i", true);
        put("me", true);
        put("my", true);
        put("myself", true);
        put("we", true);
        put("our", true);
        put("ours", true);
        put("ourselves", true);
        put("you", true);
        put("your", true);
        put("yours", true);
        put("yourself", true);
        put("yourselves", true);
        put("he", true);
        put("him", true);
        put("his", true);
        put("himself", true);
        put("she", true);
        put("her", true);
        put("hers", true);
        put("herself", true);
        put("it", true);
        put("its", true);
        put("itself", true);
        put("they", true);
        put("them", true);
        put("their", true);
        put("theirs", true);
        put("themselves", true);
        put("what", true);
        put("which", true);
        put("who", true);
        put("whom", true);
        put("this", true);
        put("that", true);
        put("these", true);
        put("those", true);
        put("am", true);
        put("is", true);
        put("are", true);
        put("was", true);
        put("were", true);
        put("be", true);
        put("been", true);
        put("being", true);
        put("have", true);
        put("has", true);
        put("had", true);
        put("having", true);
        put("do", true);
        put("does", true);
        put("did", true);
        put("doing", true);
        put("a", true);
        put("an", true);
        put("the", true);
        put("and", true);
        put("but", true);
        put("if", true);
        put("or", true);
        put("because", true);
        put("as", true);
        put("until", true);
        put("while", true);
        put("of", true);
        put("at", true);
        put("by", true);
        put("for", true);
        put("with", true);
        put("about", true);
        put("against", true);
        put("between", true);
        put("into", true);
        put("through", true);
        put("during", true);
        put("before", true);
        put("after", true);
        put("above", true);
        put("below", true);
        put("to", true);
        put("from", true);
        put("up", true);
        put("down", true);
        put("in", true);
        put("out", true);
        put("on", true);
        put("off", true);
        put("over", true);
        put("under", true);
        put("again", true);
        put("further", true);
        put("then", true);
        put("once", true);
        put("here", true);
        put("there", true);
        put("when", true);
        put("where", true);
        put("why", true);
        put("how", true);
        put("all", true);
        put("any", true);
        put("both", true);
        put("each", true);
        put("few", true);
        put("more", true);
        put("most", true);
        put("other", true);
        put("some", true);
        put("such", true);
        put("no", true);
        put("nor", true);
        put("not", true);
        put("only", true);
        put("own", true);
        put("same", true);
        put("so", true);
        put("than", true);
        put("too", true);
        put("very", true);
        put("s", true);
        put("t", true);
        put("can", true);
        put("will", true);
        put("just", true);
        put("don", true);
        put("should", true);
        put("now", true);
    }};

    /**
     * Checks if a word is a stop word.
     * @param word the word to check.
     * @return true if the word is a stop word, false otherwise.
     */
    public static boolean isStopWord(String word) {
        return engStopWords.containsKey(word);
    }
}
