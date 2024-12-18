package com.example.demo1111;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserContext {
    private static UserContext instance;
    private User currentUser;

    private UserContext() {
    }

    public static synchronized UserContext getInstance() {
        if (instance == null) {
            instance = new UserContext();
        }
        return instance;
    }

    public void clearCurrentUser() {
        this.currentUser = null;
    }
}