package com.example.konyv_nyilvantarto;

public class BookHome {
    private String title;
    private String author;
    private int year;
    private int progress;
    private int imageResId;

    public BookHome(String title, String author, int year, int progress, int imageResId) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.progress = progress;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public int getProgress() {
        return progress;
    }

    public int getImageResId() {
        return imageResId;
    }
}
