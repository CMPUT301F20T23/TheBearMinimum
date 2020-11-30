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

        //set new values to all fields
        book.setTitle("bard");
        book.setAuthor("poly");
        book.setOwner("polypuff");
        book.setBorrower("sam");
        book.setDescription("no bad plays only bard plays");
        book.setISBN("01111222233334444");
        book.setStatus("borrowed");
        book.setBid("reeeeeeeeeeeee");
        book.setLatitude("194.466");
        book.setLongitude("207.842");
        book.setOwner_scan("True");
        book.setBorrower_scan("True");

        //assert return values are equal to newly set values
        assert(book.getTitle().equals("bard"));
        assert(book.getAuthor().equals("poly"));
        assert(book.getOwner().equals("polypuff"));
        assert(book.getBorrower().equals("sam"));
        assert(book.getDescription().equals("no bad plays only bard plays"));
        assert(book.getISBN().equals("01111222233334444"));
        assert(book.getStatus().equals("borrowed"));
        assert(book.getBid().equals("reeeeeeeeeeeee"));
        assert(book.getLatitude().equals("194.466"));
        assert(book.getLongitude().equals("207.842"));
        assert(book.getOwner_scan().equals("True"));
        assert(book.getBorrower_scan().equals("True"));
    }
}
