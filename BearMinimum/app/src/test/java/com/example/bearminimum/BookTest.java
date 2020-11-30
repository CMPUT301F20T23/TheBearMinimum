package com.example.bearminimum;

import org.junit.Test;

/**
 * unit tests for the book model class
 */
public class BookTest {
    @Test
    public void createBook() {
        Book book = new Book("test title", "Rylan C", "testOwnerid", "testBorrowerid", "a book for testing", "1234567891234", "available", "testbid");
    }
}
