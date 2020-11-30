package com.example.bearminimum;

import org.junit.Test;

/**
 * unit tests for the book model class
 */
public class BookTest {

    public Book createMockBook() {
        return new Book("test title", "Rylan C", "testOwnerid",
                "testBorrowerid", "a book for testing", "1234567891234",
                "available", "testbid", "", "123.0000", "False", "False");
    }

    @Test
    public void checkAttributes() {
        Book book = createMockBook();
        assert(book.getTitle().equals("test title"));
        assert(book.getAuthor().equals("Rylan C"));
        assert(book.getOwner().equals("testOwnerid"));
        assert(book.getBorrower().equals("testBorrowerid"));
        assert(book.getDescription().equals("a book for testing"));
        assert(book.getISBN().equals("1234567891234"));
        assert(book.getStatus().equals("available"));
        assert(book.getBid().equals("testbid"));
        assert(book.getLatitude().equals(""));
        assert(book.getLongitude().equals("123.0000"));
        assert(book.getOwner_scan().equals("False"));
        assert(book.getBorrower_scan().equals("False"));
    }

    @Test
    public void setAttributes() {
        Book book = createMockBook();

    }
}
