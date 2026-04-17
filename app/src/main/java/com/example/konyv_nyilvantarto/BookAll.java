package com.example.konyv_nyilvantarto;

public class BookAll {
    private String title;
    private String author;
    private int imageResource;

    public BookAll(String title, String author, int imageResource) {
        this.title = title;
        this.author = author;
        this.imageResource = imageResource;
    }

    // Getters
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getImageResource() { return imageResource; }
}
