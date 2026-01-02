package com.sepdrive.service;


import com.sepdrive.model.*;


import com.sepdrive.repository.CustomerRepository;
import com.sepdrive.repository.RideMatchRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sepdrive.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
public class TransactionService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RideMatchRepository rideMatchRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public void makePayment(Long Id, User customer, User driver) {
        //get ride match id
        RideMatch rideMatch = rideMatchRepository.findRideMatchById(Id).orElseThrow(() ->
                new RuntimeException("RideMatch not found"));
        //check if ride is already paid
        if (rideMatch.getPaymentStatus() == PaymentStatus.PAID) {
            throw new RuntimeException("Payment already completed");
        }
        // make sure customer and driver are there and valid
        if (!rideMatch.getCustomer().equals(customer)) {
            updatePaymentStatusUnpaid(rideMatch);
            throw new RuntimeException("Customer does not match this ride");
        }
        if (!rideMatch.getDriver().equals(driver)) {
            updatePaymentStatusUnpaid(rideMatch);
            throw new RuntimeException("Driver does not match this ride");
        }
        //validate the role
        if(!driver.getRole().equals(Role.FAHRER)) {
            updatePaymentStatusUnpaid(rideMatch);
           throw new RuntimeException("User is not driver");
        }
       if(!customer.getRole().equals(Role.KUNDE)) {
           updatePaymentStatusUnpaid(rideMatch);
            throw new RuntimeException("User is not customer");
        }
        //get amount
        RoutePlan routePlan = rideMatch.getRoutePlan();
        BigDecimal amount= routePlan.getPrice();
       //check customer balance
        if(customer.getBalance().compareTo(amount) < 0) {
            updatePaymentStatusUnpaid(rideMatch);
            throw new RuntimeException("Not enough balance");
        }
       //preform transfer
       customer.setBalance(customer.getBalance().subtract(amount));
       driver.setBalance(driver.getBalance().add(amount));
    //save both users
       userRepository.save(customer);
       userRepository.save(driver);
       rideMatch.setRidePrice(amount);
       updatePaymentStatusDone(rideMatch);
    }
    //recharging users account
    @Transactional
    public BigDecimal rechargeAccount(String customerUsername, BigDecimal amount) {
    //validate amount
        if(amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new RuntimeException("Amount must be greater than 0");
        }
    //get user
    User user= userRepository.findByUsername(customerUsername).orElseThrow(() ->
    new RuntimeException("User not found"));
    //check if the user is customer
        if(!user.getRole().equals(Role.KUNDE)) {
            throw new RuntimeException("User: " + customerUsername + " is not customer");
        }
        BigDecimal oldBalance = user.getBalance();
        BigDecimal newBalance= oldBalance.add(amount);
        user.setBalance(newBalance);
        userRepository.save(user);

        //balance in profile change
        CustomerProfile customerProfile = customerRepository.findByUsername(customerUsername);
        if (customerProfile != null) {
            customerProfile.setBalance(newBalance);
            customerRepository.save(customerProfile);
        }

        return newBalance;
    }
    private void updatePaymentStatusDone(RideMatch rideMatch) {
        rideMatch.setPaymentStatus(PaymentStatus.PAID);
        rideMatchRepository.save(rideMatch);
    }
    private void updatePaymentStatusUnpaid(RideMatch rideMatch) {
        rideMatch.setPaymentStatus(PaymentStatus.UNPAID);
        rideMatchRepository.save(rideMatch);
    }

}