package com.example.rentron.ui.screens.search;

import android.util.Log;

import com.example.rentron.utils.TrieSearch.TriesSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchProperties {

    // map to store SearchPropertyItem with their id's as the key value
    Map<String, SearchPropertyItem> searchPropertyItems;
    // instance of TrieSearch - the utility we use for search
    TriesSearch triesSearch;
    // store a reference to the SearchPropertyScreen so it could be notified of the updates to the list
    SearchScreen searchScreen;

    public SearchProperties() {
        this.searchPropertyItems = new HashMap<>();
        this.triesSearch = new TriesSearch();
    }

    public Map<String, SearchPropertyItem> getSearchPropertyItems() {
        return searchPropertyItems;
    }

    public void addItems(List<SearchPropertyItem> items) {
        // TODO test
        Log.e("searchProperties", "adding new items: " + items.size());
        for (SearchPropertyItem item: items) {
            // store the item in our map
            this.searchPropertyItems.put(item.getId(), item);
            // add property's keywords to the TriesSearch dataset with an associated SearchPropertyItem id
            // if there is a match in these keywords, we would get the corresponding SearchPropertyItem id
            this.triesSearch.addData(item.getId(), item.getProperty().getKeywords());
        }
        // if we have a subscribed search screen observing data changes
        if (this.searchScreen != null) {
            // notify search screen of changes
            this.searchScreen.newSearchItemsAdded(items);
        }
    }

    public List<SearchPropertyItem> searchPropertyItems(String query) {
        // use TriesSearch to perform a pattern match and
        // get a list containing ids of SearchPropertyItems which have a match
        List<String> triesSearchResult = this.triesSearch.pMatch(query);
        // store result
        List<SearchPropertyItem> sMItems = new ArrayList<>();
        // for each id in search result
        for (String sMItemId: triesSearchResult) {
            // get the corresponding SearchPropertyItem from our local map and add to result list
            sMItems.add(this.searchPropertyItems.get(sMItemId));
        }
        // return result list, will be empty if no match
        return sMItems;
    }

    public void subscribeToDataChanges(SearchScreen dataObserver) {
        this.searchScreen = dataObserver;
    }
}
