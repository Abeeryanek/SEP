package com.sepdrive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestMessage {
    private Long rideMatchId;
    private BigDecimal amount;
    private SimulationMessageType type;
}
