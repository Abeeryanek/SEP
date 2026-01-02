package com.sepdrive.controller;

import com.sepdrive.dto.RechargeRequestDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sepdrive.service.TransactionService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    //user recharge
    @PostMapping("/recharge")
    public ResponseEntity<?> rechargeTransaction( @Valid @RequestBody RechargeRequestDTO request){
       try{
           BigDecimal newBalance = transactionService.rechargeAccount(
                   request.getUsername(),
                   request.getAmount());
           return ResponseEntity.ok(Map.of(
                   "message", "Account recharged successfully",
                   "balance", "€" +newBalance
           ));
       } catch(Exception e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body(Map.of("error", "recharge failed","details", e.getMessage()));
       }
    }

}

