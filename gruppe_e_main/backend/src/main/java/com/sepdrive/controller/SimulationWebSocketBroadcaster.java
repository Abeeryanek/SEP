package com.sepdrive.controller;

import com.sepdrive.model.*;
import com.sepdrive.service.SimulationStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SimulationWebSocketBroadcaster {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public void broadcastSimulationStarted(Long rideMatchId, Double speed){

        SimulationIndexMessage message = new SimulationIndexMessage
                (rideMatchId, SimulationStatus.IN_PROGRESS,
                        0, SimulationMessageType.INDEX_CHANGE, speed);

        simpMessagingTemplate.convertAndSend("/topic/simulation/" +
                rideMatchId, message);
    }

    public void broadcastSimulationPaused(Long rideMatchId, int currentIndex, Double speed) {

        SimulationIndexMessage message = new SimulationIndexMessage
                (rideMatchId, SimulationStatus.PAUSED,
                        currentIndex, SimulationMessageType.INDEX_CHANGE, speed);

        simpMessagingTemplate.convertAndSend("/topic/simulation/" +
                rideMatchId, message);

    }

    public void broadcastSimulationResume(Long rideMatchId, int currentIndex, Double speed) {

        SimulationIndexMessage message = new SimulationIndexMessage
                (rideMatchId, SimulationStatus.IN_PROGRESS,
                        currentIndex, SimulationMessageType.INDEX_CHANGE, speed);

        simpMessagingTemplate.convertAndSend("/topic/simulation/" +
                rideMatchId, message);

    }

    public void broadcastSimulationCompleted(Long rideMatchId, int currentIndex, Double speed) {

        SimulationIndexMessage message = new SimulationIndexMessage
                (rideMatchId, SimulationStatus.COMPLETED,
                        currentIndex,SimulationMessageType.INDEX_CHANGE, speed);

        simpMessagingTemplate.convertAndSend("/topic/simulation/" +
                rideMatchId, message);
    }

    public void broadcastSimulationUpdated(Long rideMatchId, int currentIndex, Double speed) {

        SimulationIndexMessage message = new SimulationIndexMessage
                (rideMatchId, SimulationStatus.IN_PROGRESS,
                        currentIndex, SimulationMessageType.INDEX_CHANGE, speed);

        simpMessagingTemplate.convertAndSend("/topic/simulation/" +
                rideMatchId, message);

    }

    public void broadcastSpeedChanged(Long rideMatchId, Double speed) {

        SimulationSpeedMessage message = new SimulationSpeedMessage(rideMatchId, speed, SimulationMessageType.SPEED_CHANGE);

        simpMessagingTemplate.convertAndSend("/topic/simulation/" +
                rideMatchId, message);

    }

    public void broadcastPaymentRequest(Long rideMatchId, BigDecimal amount) {

        PaymentRequestMessage message = new PaymentRequestMessage(rideMatchId, amount, SimulationMessageType.REQUEST_PAYMENT);

        simpMessagingTemplate.convertAndSend("/topic/simulation/" +
                rideMatchId, message);

    }

    public void broadcastPaymentCompleted(Long rideMatchId) {

        PaymentCompletedMessage message = new PaymentCompletedMessage(rideMatchId,SimulationMessageType.COMPLETE);

        simpMessagingTemplate.convertAndSend("/topic/simulation/" +
                rideMatchId, message);
    }

    public void broadcastUpdateRoute(RideMatchVO updateVO) {

        SimulationRouteUpdateMessage message = new SimulationRouteUpdateMessage(updateVO.getId(),
                SimulationMessageType.UPDATE_ROUTE, updateVO);

        simpMessagingTemplate.convertAndSend("/topic/simulation/" +
                updateVO.getId(), message);

    }
}
