package com.sepdrive.dto;

import com.sepdrive.model.RoutePlan;
import lombok.Data;
import java.util.List;

@Data
public class RideRequestDTO {
    private LocationInputDTO start;
    private LocationInputDTO destination;
    private List<LocationInputDTO> stopovers;
    private String vehicleClass;
    private RoutePlan routePlan;
} 