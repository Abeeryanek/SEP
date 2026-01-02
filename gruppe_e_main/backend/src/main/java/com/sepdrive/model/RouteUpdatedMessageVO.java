package com.sepdrive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RouteUpdatedMessageVO {

    private Long rideMatchId;
    private SimulationMessageType type;
    private RideMatchVO rideMatchVO;
}
