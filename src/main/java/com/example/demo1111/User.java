package com.example.demo1111;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.mindrot.jbcrypt.BCrypt;

@Setter
@Getter
@Entity
@Data
@Table(name="user")

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Getter
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String email;
    private String role = "USER";

    public User(String username, String password, String email) {
        this.username = username;
        this.setPassword(password);
        this.email = email;
    }

    public User() {
        this.username = "";
        this.password = "";
        this.email = "";
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Геттеры и сеттеры
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}