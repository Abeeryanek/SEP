package com.sepdrive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimulationIndexMessage {

    private Long rideMatchId;
    private SimulationStatus simulationStatus;
    private Integer currentSimulationIndex;
    private SimulationMessageType type;
    private Double speed;
}
