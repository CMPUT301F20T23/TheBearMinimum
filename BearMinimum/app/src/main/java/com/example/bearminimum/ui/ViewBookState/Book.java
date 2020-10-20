package com.example.bearminimum.ui.ViewBookState;

public class Book implements Comparable<Book>{
    private String BookName;
    private String author;
    private String ISBN;
    public Book(String BookName,String author,String ISBN){
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

    @Override
    public int compareTo(Book book) {
        return this.BookName.compareTo(book.getBookName());
    }
}
