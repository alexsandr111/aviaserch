package com.example.demo1111;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Entity
public class Cargo {
    private Long id; // ID
    @Getter
    private String cargoName; // Имя
    @Getter
    private String cargoContent; // Фамилия
    @Getter
    private String departureCity; // Фамилия
    @Getter
    private LocalDateTime departureDateTime; // номер студенческого
    @Getter
    private String arrivalCity; // Фамилия
    @Getter
    private LocalDateTime arrivalDateTime; // номер студенческого


    protected Cargo() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }


    @Override
    public String toString() {
        return "cargo [id=" + id + ", cargoName=" + cargoName +", cargoContent=" + cargoContent + ", departureCity=" + departureCity + ", departureDateTime=" + departureDateTime + ", arrivalCity=" + arrivalCity + ",arrivalDateTime" + arrivalDateTime + "]";
    }
}