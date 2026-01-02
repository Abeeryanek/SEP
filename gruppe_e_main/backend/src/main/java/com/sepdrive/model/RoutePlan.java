package com.sepdrive.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "route_plan")
@Data
public class RoutePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "start_lat")),
            @AttributeOverride(name = "lng", column = @Column(name = "start_lng"))
    })
    private LatLng startPoint;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "lat", column = @Column(name = "end_lat")),
            @AttributeOverride(name = "lng", column = @Column(name = "end_lng"))
    })
    private LatLng endPoint;

    @ElementCollection
    private List<LatLng> stopovers;

    private double totalDistanceKm;
    private double durationMin;
    private BigDecimal price;
}

