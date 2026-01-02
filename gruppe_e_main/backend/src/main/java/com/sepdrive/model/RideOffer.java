package com.sepdrive.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Data
public class RideOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //Many riding offers can be assigned to only one driver(Driver can make multiple offers)
    //Each driver can only have one single rideoffer at a time but historically he has multiple ones
    @ManyToOne(fetch = FetchType.LAZY)
    private DriverProfile driverProfile; //in this table the original pk of the driverProfileobject(in its own table)
    //will be saved here as the foreignkey since objects cant be saved in a db

    //Many driver offers can be assigned to only a single ride request
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private RideRequest rideRequest;

    //Status of the offer
    private offerStatus offerStatus;

    private Double price; //fetched from routeservice


    private LocalDateTime createdOn;
}
