package ru.minusd.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.minusd.security.service.TransferService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    @Autowired
    private TransferService transferService;

    @PostMapping
    public ResponseEntity<String> transfer(@RequestParam Long fromUserId, @RequestParam Long toUserId,
                                           @RequestParam BigDecimal amount) {
        transferService.transfer(fromUserId, toUserId, amount);
        return ResponseEntity.ok("Transfer successful");
    }
}
