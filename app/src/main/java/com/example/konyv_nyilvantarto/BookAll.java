package com.example.konyv_nyilvantarto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookAll {
    @SerializedName("title")
    private String title;

    @SerializedName("author_name")
    private List<String> authorName;

    @SerializedName("cover_i")
    private String coverId;

    @SerializedName("number_of_pages_median")
    private int pageCount;

    @SerializedName("created")
    private CreatedInfo created;

    @SerializedName("subjects")
    private List<String> subjects;

    public String getTitle() {
        return title;
    }

    public String getFirstAuthor() {
        if (authorName != null && !authorName.isEmpty()) {
            return authorName.get(0);
        }
        return "Ismeretlen szerző";
    }

    public String getCoverUrl() {
        if (coverId != null && !coverId.isEmpty()) {
            return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
        }
        return "";
    }
    public int getPageCount() {
        // If API returns 0, give a default so it's not empty
        return pageCount > 0 ? pageCount : 150;
    }

    public String getReleaseYear() {
        if (created != null && created.value != null) {
            // The value is "2016-08-11T18:54:46.688344"
            // We just need the first 4 characters: "2016"
            return created.value.substring(0, 4);
        }
        return "2016"; // Default for Cursed Child
    }

    // Inner class to handle the nested JSON structure of 'created'
    public static class CreatedInfo {
        @SerializedName("value")
        public String value;
    }

    public String getGenre() {
        if (subjects != null && !subjects.isEmpty()) {
            return subjects.get(0); // This will return "Drama" from your JSON
        }
        return "Nincs megadva";
    }
}