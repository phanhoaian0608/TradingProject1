package com.example.TradingProject1.service;

import com.example.TradingProject1.entity.UserWallet;
import com.example.TradingProject1.repository.UserWalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@Slf4j
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
        log.info("Get user wallet data");
        return userWalletRepository.findByUserData_Id(userService.getCurrentUserId());
    }
}