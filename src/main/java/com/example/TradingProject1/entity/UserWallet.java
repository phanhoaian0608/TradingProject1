package com.example.TradingProject1.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class UserWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Double balance;
    private Double ETHUSDT;
    private Double BTCUSDT;
    @OneToOne(mappedBy = "userWallet")
    private UserData userData;
}
