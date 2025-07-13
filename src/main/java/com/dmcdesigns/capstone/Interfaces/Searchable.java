package com.dmcdesigns.capstone.Interfaces;

import java.util.List;

public interface Searchable {
    List<String> getSearchableFields();
    String getSearchableContent();
    boolean matchesSearch(String searchTerm);
}
