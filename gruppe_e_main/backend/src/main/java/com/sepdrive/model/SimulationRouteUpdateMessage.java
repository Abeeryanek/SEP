package com.sepdrive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimulationRouteUpdateMessage {

    private Long rideMatchId;
    private SimulationMessageType type;
    private RideMatchVO rideMatchVO;

}
