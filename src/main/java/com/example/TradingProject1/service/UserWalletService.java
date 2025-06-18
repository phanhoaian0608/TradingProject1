package com.example.TradingProject1.service;

import com.example.TradingProject1.entity.UserWallet;
import com.example.TradingProject1.repository.UserWalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class UserWalletService {

    private final UserWalletRepository userWalletRepository;
    private final UserService userService;

    @Autowired
    public UserWalletService(UserWalletRepository userWalletRepository, UserService userService) {
        this.userWalletRepository = userWalletRepository;
        this.userService = userService;
    }

    @Transactional
    public void updateWallet(UserWallet userWallet) {
        userWalletRepository.save(userWallet);
    }

    public UserWallet getCurrentUserWallet() {
        return userWalletRepository.findByUserData_Id(userService.getCurrentUserId());
    }
}