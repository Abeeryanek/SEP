package com.sepdrive.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeRequestDTO {
    @NotNull
    private String username;
    @NotNull(message = "Bitte geben Sie einen Betrag ein.")
    private BigDecimal amount;
}
