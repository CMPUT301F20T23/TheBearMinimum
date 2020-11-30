package com.example.bearminimum;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationObjectTest {

    public NotificationObject createMockNotification() {
        //using users sam@gmail.com and ham@gmail.com
        //using book Fablehaven
        //request notification (type 1)
        String ownerId = "oDn9PDjGhNZuZIa6jOe67aO02w43";
        String bookId = "7XLWq3Qr86DqtFogdbNQ";
        String requesterId = "5mw3ZyxfkdXPtIe391kLdtIYdBF3";
        String topic = ownerId + "-" + bookId;
        String title = "book request";
        String body = topic + "-" + requesterId;
        int type = 1;

        return new NotificationObject(topic, title, body, type);
    }

    @Test
    public void checkInitialAttributes() {
        //expected values
        int type = 1;
        String topic = "oDn9PDjGhNZuZIa6jOe67aO02w43-7XLWq3Qr86DqtFogdbNQ";
        String title = "book request";
        String body = "oDn9PDjGhNZuZIa6jOe67aO02w43-7XLWq3Qr86DqtFogdbNQ-5mw3ZyxfkdXPtIe391kLdtIYdBF3";

        //create notification
        NotificationObject myNotif = createMockNotification();

        //assert that returns are the same to expected values
        assertEquals(type, myNotif.getType());
        assertEquals(topic, myNotif.getTopic());
        assertEquals(title, myNotif.getTitle());
        assertEquals(body, myNotif.getBody());

    }

    @Test
    public void checkSeparatedAttributes() {
        //expected values
        String ownerId = "oDn9PDjGhNZuZIa6jOe67aO02w43";
        String bookId = "7XLWq3Qr86DqtFogdbNQ";
        String requesterId = "5mw3ZyxfkdXPtIe391kLdtIYdBF3";

        //create notification
        NotificationObject myNotif = createMockNotification();

        //assert that returns are the same to expected values
        assertEquals(ownerId, myNotif.getOwnerId());
        assertEquals(bookId, myNotif.getBookId());
        assertEquals(requesterId, myNotif.getRequesterId());
    }
}
