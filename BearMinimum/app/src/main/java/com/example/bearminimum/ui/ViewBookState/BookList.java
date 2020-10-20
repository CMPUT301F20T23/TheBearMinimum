package com.example.bearminimum.ui.ViewBookState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookList {
    private List<Book> books = new ArrayList<>();

    /**
     * This adds a book to the list if the book does not exist
     * @param book
     * This is a candidate book to add
     */
    public void add (Book book) {
        if (books.contains(book)){
            throw new IllegalArgumentException("book already exist ");
        }
        books.add(book);
    }

    /**
     * This returns a sorted list of cities
     * @return
     * Return the sorted list
     */
    public List<Book> getCities() {
        List<Book> list = books;
        Collections.sort(list);
        return list;
    }


    /**
     * This returns weather you has the book in your bookList
     * @return
     * Return the Boolean
     */
    public Boolean hasBook (Book book){
        for(int i=1;i< books.size();i++){
            if (book.compareTo(books.get(i))==0){
                return true;
            }
        }
        return false;

    }


    /**
     * delete the pass in book
     * @void
     */
    public void delete(Book book){
        if(hasBook(book)) {
            for (int i = 1; i < books.size(); i++) {
                if (book.compareTo(books.get(i)) == 0) {
                    books.remove(i);
                }
            }
        }
        else{
            throw new IllegalArgumentException("book do not exist");
        }

    }

    /**
     * This returns how many cities in the list cities
     * @return
     * Return a number
     */

    public int countCities(){
        return books.size()-1;
    }

}

