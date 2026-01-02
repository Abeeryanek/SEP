package com.sepdrive.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PauseRequestDTO {

    private Long rideMatchId;
    private Integer currentSimulationIndex;
}
