package com.sepdrive.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ride_request")
public class RideRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    private String startAddress;
    private Double startLat;
    private Double startLng;

    private String destinationAddress;
    private Double destinationLat;
    private Double destinationLng;

    @Enumerated(EnumType.STRING)
    private Car vehicleClass;

    @Enumerated(EnumType.STRING)
    private RideRequestStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Lob
    private String stopovers; // JSON/Text für Zwischenstopps (optional)

    //Fahrtplanung
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "route_plan_id", referencedColumnName = "id")
    private RoutePlan routePlan;
    //Fahrtplanung

    //Fahrtangebot
    private Boolean hasBeenAccepted;
} 