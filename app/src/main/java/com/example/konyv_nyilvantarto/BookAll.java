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

    @SerializedName("first_publish_year")
    private Integer firstPublishYear;

    @SerializedName("subject")
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
        if (firstPublishYear != null) {
            return String.valueOf(firstPublishYear);
        }
        return "2016";
    }

    // Inner class to handle the nested JSON structure of 'created'
    public static class CreatedInfo {
        @SerializedName("value")
        public String value;
    }

    public String getGenre() {
        if (subjects != null && !subjects.isEmpty()) {
            return subjects.get(0);
        }
        return "Fiction";
    }
}