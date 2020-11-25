package com.example.bearminimum;

import java.io.Serializable;

/**
 * Book
 *
 * This class creates a Book object used to store a single book's information.
 *
 * Nov. 3, 2020
 */

public class Book implements Serializable {
    private String title;
    private String author;
    private String owner;
    private String borrower;
    private String description;
    private String ISBN;
    private String status;
    private String bid;
    private String latitude;
    private String longitude;
    private String owner_scan;
    private String borrower_scan;

    public Book(String title, String author, String owner, String borrower, String description, String ISBN, String status, String bid,String owner_scan,String borrower_scan){
        this.title = title;
        this.author = author;
        this.owner = owner;
        this.borrower = borrower;
        this.description = description;
        this.ISBN = ISBN;
        this.status = status;
        this.bid = bid;
        this.latitude = "";
        this.longitude = "";
        this.owner_scan=owner_scan;
        this.borrower_scan=borrower_scan;

    }


    public String getOwner_scan() {
        return owner_scan;
    }

    public void setOwner_scan(String owner_scan) {
        this.owner_scan = owner_scan;
    }

    public String getBorrower_scan() {
        return borrower_scan;
    }

    public void setBorrower_scan(String borrower_scan) {
        this.borrower_scan = borrower_scan;
    }

    /**
     * Return the book's title
     *
     * @return title  String type of book's title
     */

    public String getTitle() {
        return title;
    }


    /**
     * Overwrites the current title of a book to the
     * provided title.
     *
     * @param title  String type of the book's title to save.
     */

    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Return the book's author
     *
     * @return author  String type of book's author
     */

    public String getAuthor() {
        return author;
    }


    /**
     * Overwrites the current author of a book to the
     * provided author.
     *
     * @param author  String type of the book's author to save.
     */

    public void setAuthor(String author) {
        this.author = author;
    }


    /**
     * Return the book's owner
     *
     * @return owner  String type of book's owner
     */

    public String getOwner() {
        return owner;
    }


    /**
     * Overwrites the current owner of a book to the
     * provided owner.
     *
     * @param owner  String type of the book's owner to save.
     */

    public void setOwner(String owner) {
        this.owner = owner;
    }


    /**
     * Return the book's borrower
     *
     * @return borrower  String type of book's borrower
     */

    public String getBorrower() {
        return borrower;
    }


    /**
     * Overwrites the current borrower of a book to the
     * provided borrower.
     *
     * @param borrower  String type of the book's borrower to save.
     */

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }


    /**
     * Return the book's description
     *
     * @return description  String type of book's description
     */

    public String getDescription() {
        return description;
    }


    /**
     * Overwrites the current description of a book to the
     * provided description.
     *
     * @param description  String type of the book's description to save.
     */

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Return the book's ISBN
     *
     * @return ISBN  String type of book's ISBN
     */

    public String getISBN() {
        return ISBN;
    }


    /**
     * Overwrites the current ISBN of a book to the
     * provided ISBN.
     *
     * @param ISBN  String type of the book's ISBN to save.
     */

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }


    /**
     * Return the book's status
     *
     * @return status  String type of book's status
     */

    public String getStatus() {
        return status;
    }


    /**
     * Overwrites the current status of a book to the
     * provided status.
     *
     * @param status  String type of the book's status to save.
     */

    public void setStatus(String status) {
        this.status = status;
    }


    /**
     * Return the book's bid
     *
     * @return bid  String type of book's book id
     */

    public String getBid() {
        return bid;
    }


    /**
     * Overwrites the current book id of a book to the
     * provided book id.
     *
     * @param bid  String type of the book's book id to save.
     */

    public void setBid(String bid) {
        this.bid = bid;
    }

    /**
     * Return the book's coordinate
     *
     * @return latiude  String type of book's lat
     */

    public String getLatitude() {
        return latitude;
    }


    /**
     * Overwrites the current book lat coordinate
     * provided book latidude
     *
     * @param latitude  String type of the book's book lat
     */

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * Return the book's coordinate
     *
     * @return longitude  String type of book's lat
     */

    public String getLongitude() {
        return longitude;
    }


    /**
     * Overwrites the current book long coordinate
     * provided book longitude
     *
     * @param longitude  String type of the book's book long
     */

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


}
