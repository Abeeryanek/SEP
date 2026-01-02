package com.sepdrive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimulationSpeedMessage {
    private Long rideMatchId;
    private Double speed;
    private SimulationMessageType type;
}
