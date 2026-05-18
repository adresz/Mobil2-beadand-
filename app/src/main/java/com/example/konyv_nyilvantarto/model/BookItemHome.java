package com.example.konyv_nyilvantarto.model;

import java.time.format.DateTimeFormatter;
import java.util.Date;

public class BookItemHome {
    private long id;
    private String book_name;
    private String book_author;
    private Date release_year;
    private String type;
    private int max_pages;
    private int current_page;
    private String cover;
    private String genre;
    private float rating;

    private String note;

    private Date start_date;
    private Date finish_date;

    public BookItemHome() {
    }

    public BookItemHome(long id, String book_name, String book_author, Date release_year, int max_pages, int current_page, String cover, String genre, float rating,  String note, Date start_date, Date finish_date, String type) {
        this.book_name = book_name;
        this.book_author = book_author;
        this.release_year = release_year;
        this.max_pages = max_pages;
        this.current_page = current_page;
        this.cover = cover;
        this.genre = genre;
        this.id = id;
        this.rating = rating;
        this.note = note;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getBook_author() {
        return book_author;
    }

    public void setBook_author(String book_author) {
        this.book_author = book_author;
    }

    public Date getRelease_year() {
        return release_year;
    }

    public void setRelease_year(Date release_year) {
        this.release_year = release_year;
    }

    public int getMax_pages() {
        return max_pages;
    }

    public void setMax_pages(int max_pages) {
        this.max_pages = max_pages;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(Date finish_date) {
        this.finish_date = finish_date;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}