package com.example.bearminimum.ui.ViewBookState;

public class Book implements Comparable<Book>{
    private String BookName;
    private String author;
    private String ISBN;
    private boolean state = false;
    private  String Borrower;
    public Book(String BookName, String author, String ISBN){
        this.BookName = BookName;
        this.author = author;
        this.ISBN = ISBN;
    }

    public String getBookName(){
        return BookName;
    }
    public String getAuthor(){
        return author;
    }
    public String getISBN(){
        return getISBN();
    }
    public void setBorrower(String Name){
        this.Borrower = Name;

    }
    public String getBorrower(){
        return this.Borrower;

    }
    public void setState(boolean state){
        this.state = state;

    }
    public boolean getState(){
        return this.state;

    }

    @Override
    public int compareTo(Book book) {
        return this.BookName.compareTo(book.getBookName());
    }
}
