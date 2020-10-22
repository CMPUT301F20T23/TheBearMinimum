package com.example.bearminimum;

public class Book {
    private String title;
    private String author;
    private String owner;
    private String borrower;
    private String description;
    private String ISBN;
    private String[] status;  //available, requested, accepted, , borrowed.


    public Book(String title, String author, String owner, String borrower, String description, String ISBN, String[] status) {
        this.title = title;
        this.author = author;
        this.owner = owner;
        this.borrower = borrower;
        this.description = description;
        this.ISBN = ISBN;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String[] getStatus() {
        return status;
    }

    public void setStatus(String[] status) {
        this.status = status;
    }
}
