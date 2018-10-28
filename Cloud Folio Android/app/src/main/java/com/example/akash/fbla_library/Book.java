package com.example.akash.fbla_library;

/**
 * Created by akash on 12/28/2017.
 */

public class Book {
    private String author;
    private String description;
    private String genre;
    private String lexile;
    private String pages;
    private String stock;
    private String type;
    private String name;

    public Book() {
    }

    public Book(String name, String author, String description, String genre, String lexile, String pages, String stock, String type) {
        this.name = name;
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.lexile = lexile;
        this.pages = pages;
        this.stock = stock;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLexile() {
        return lexile;
    }

    public void setLexile(String lexile) {
        this.lexile = lexile;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}

