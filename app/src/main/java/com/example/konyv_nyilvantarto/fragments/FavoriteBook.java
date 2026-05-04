package com.example.konyv_nyilvantarto.fragments;

public class FavoriteBook {

    public String book_name;
    public String book_author;
    public String cover;
    public int max_pages;
    public String release_year;
    private String genre;

    public FavoriteBook(String title, String author, String cover, int pages, String year, String genre) {
        this.book_name = title;
        this.book_author = author;
        this.cover = cover;
        this.max_pages = pages;
        // FIX: Convert "1995" to "1995-01-01" to satisfy the 'date' type in Supabase
        if (year != null && year.length() == 4) {
            this.release_year = year + "-01-01";
        } else if (year == null || year.isEmpty()) {
            this.release_year = "1900-01-01"; // Fallback for missing dates
        } else {
            this.release_year = year;
        }

        this.genre = genre;

    }



}
