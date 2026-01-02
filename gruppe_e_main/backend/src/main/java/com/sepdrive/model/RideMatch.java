package com.sepdrive.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rideMatch")
public class RideMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "route_plan_id", nullable = false)
    private RoutePlan routePlan;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private User driver;

    @Enumerated(EnumType.STRING)
    private SimulationStatus simulationStatus = SimulationStatus.NOT_STARTED;

    private Integer currentSimulationIndex = 0;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Min(1)
    @Max(5)
    private Integer ratingCustomer;

    @Min(1)
    @Max(5)
    private Integer ratingDriver;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    //price hinzufuegt(gezahlte oder erhaltene Geld)
    private BigDecimal ridePrice;

}