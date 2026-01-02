package com.sepdrive.service;



import com.sepdrive.model.RideOfferDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    public void sendOfferNotification(String customerUsername, RideOfferDTO dto) {
        String destination = "/topic/ride-offer/" + customerUsername;
        simpMessagingTemplate.convertAndSend(destination, dto);
    }
}
