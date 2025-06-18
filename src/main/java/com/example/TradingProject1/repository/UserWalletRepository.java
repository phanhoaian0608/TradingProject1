package com.example.TradingProject1.repository;

import com.example.TradingProject1.entity.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWalletRepository extends JpaRepository<UserWallet, Integer> {
    UserWallet findByUserData_Id(int userDataId);
}