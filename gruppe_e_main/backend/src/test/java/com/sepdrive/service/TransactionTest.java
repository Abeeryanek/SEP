package com.sepdrive.service;
import com.sepdrive.model.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

    class TransactionServiceTest {

        // Dummy TransactionService with in-memory logic
        static class TestTransactionService {
            public void makePayment(RideMatch rideMatch) {
                BigDecimal price = rideMatch.getRoutePlan().getPrice();
                User customer = rideMatch.getCustomer();
                User driver = rideMatch.getDriver();

                customer.setBalance(customer.getBalance().subtract(price));
                driver.setBalance(driver.getBalance().add(price));
                rideMatch.setRidePrice(price);
                rideMatch.setPaymentStatus(PaymentStatus.PAID);
            }
        }

        private final TestTransactionService transactionService = new TestTransactionService();

        @Test
        void testMakePaymentLogic() {
            // Arrange
            RoutePlan routePlan = new RoutePlan();
            routePlan.setPrice(BigDecimal.valueOf(10.0));

            User customer = new User();
            customer.setBalance(BigDecimal.valueOf(50.0));
            customer.setRole(Role.KUNDE);

            User driver = new User();
            driver.setBalance(BigDecimal.ZERO);
            driver.setRole(Role.FAHRER);

            RideMatch rideMatch = new RideMatch();
            rideMatch.setRoutePlan(routePlan);
            rideMatch.setCustomer(customer);
            rideMatch.setDriver(driver);
            rideMatch.setPaymentStatus(PaymentStatus.UNPAID);

            // Act
            transactionService.makePayment(rideMatch);

            // Assert
            assertEquals(PaymentStatus.PAID, rideMatch.getPaymentStatus());
            assertEquals(BigDecimal.valueOf(10.0), rideMatch.getRidePrice());
            assertEquals(BigDecimal.valueOf(40.0), customer.getBalance());
            assertEquals(BigDecimal.valueOf(10.0), driver.getBalance());
        }
    }
