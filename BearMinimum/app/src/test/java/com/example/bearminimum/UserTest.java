package com.example.bearminimum;

import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    public User createMockUser() {
        return new User("test@bearmin.com", "7801234567", "testUserid", "test");
    }

    @Test
    public void checkUserAttributes() {
        User user = createMockUser();
        assertEquals(user.getEmail(), "test@bearmin.com");
        assertEquals(user.getPhonenumber(), "7801234567");
        assertEquals(user.getUid(), "testUserid");
        assertEquals(user.getUsername(), "test");
    }

    @Test
    public void checkSetAttributes() {
        User user = createMockUser();
        user.setEmail("test1@bearmin.com");
        assertEquals(user.getEmail(), "test1@bearmin.com");
        user.setPhonenumber("5871234567");
        assertEquals(user.getPhonenumber(), "5871234567");
        user.setUid("testuserid2");
        assertEquals(user.getUid(), "testuserid2");
        user.setUsername("tester");
        assertEquals(user.getUsername(), "tester");
    }
}
