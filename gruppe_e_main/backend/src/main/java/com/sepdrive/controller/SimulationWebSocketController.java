package com.sepdrive.controller;

import com.sepdrive.model.SimulationIndexMessage;
import com.sepdrive.model.SimulationRouteUpdateMessage;
import com.sepdrive.model.SimulationSpeedMessage;
import com.sepdrive.service.RideMatchService;
import com.sepdrive.service.SimulationStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class SimulationWebSocketController {

    @Autowired
    private SimulationStateService simulationStateService;

    @Autowired
    private RideMatchService rideMatchService;

    @MessageMapping("/simulation/speed")
    public void handleSpeedChange(SimulationSpeedMessage message){
        Long rideMatchId = message.getRideMatchId();
        Double speed = message.getSpeed();

        //in memory save and broadcast frontend
        simulationStateService.updateSpeed(rideMatchId, speed);
    }
}
