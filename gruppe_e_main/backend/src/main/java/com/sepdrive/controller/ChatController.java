package com.sepdrive.controller;

import com.sepdrive.model.Chat;
import com.sepdrive.model.ChatDTO;
import com.sepdrive.model.RideMatch;
import com.sepdrive.service.impl.ChatService;
import com.sepdrive.repository.RideMatchRepository;
import com.sepdrive.dto.SendMessageDTO;
import com.sepdrive.dto.EditMessageDTO;
import com.sepdrive.dto.DeleteMessageDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @Autowired private ChatService chatService;
    @Autowired private RideMatchRepository rideMatchRepository;

    // Nachricht senden
    @MessageMapping("/sendMessage/{rideMatchId}")
    @SendTo("/topic/messages/{rideMatchId}")
    public ChatDTO handleSendMessage(
            @DestinationVariable Long rideMatchId,
            @Payload SendMessageDTO dto) {

        System.out.println("Chat empfangen: " + dto.getMessage());
        // RideMatch aus DB holen:
        RideMatch rideMatch = rideMatchRepository.findById(rideMatchId)
                .orElseThrow(() -> new RuntimeException("RideMatch nicht gefunden"));

        // Chat-Objekt bauen
        Chat chat = new Chat();
        chat.setRideMatch(rideMatch);
        chat.setMessage(dto.getMessage());
        chat.setSenderId(dto.getSenderId());
        chat.setReceiverId(dto.getReceiverId());
        Chat saved = chatService.sendMessage(chat);
        return chatService.toDTO(saved);
    }

    //Ridematchid nur als routing marker
    // Nachricht als gelesen markieren
    @MessageMapping("/markAsRead/{rideMatchId}")
    @SendTo("/topic/messages/{rideMatchId}")
    public ChatDTO handleMarkAsRead(
            @DestinationVariable Long rideMatchId,
            @Payload Long chatId)
    {
        chatService.markAsRead(chatId);
        return chatService.toDTO(chatService.findById(chatId));
    }


    //Ridematchid nur als routing marker
    // Nachricht bearbeiten
    @MessageMapping("/editMessage/{rideMatchId}")
    @SendTo("/topic/messages/{rideMatchId}")
    public ChatDTO handleEditMessage(
            @DestinationVariable Long rideMatchId,
            @Payload EditMessageDTO payload)
    {
        return chatService.toDTO(chatService.editMessage(payload.getChatId(), payload.getNewMessage(), payload.getUserId()));
    }

    //Ridematchid nur als routing marker
    // Nachricht löschen
    @MessageMapping("/deleteMessage/{rideMatchId}")
    @SendTo("/topic/messages/{rideMatchId}")
    public Long handleDeleteMessage(
            @DestinationVariable Long rideMatchId,
            @Payload DeleteMessageDTO payload)
    {
        chatService.deleteMessage(payload.getChatId(), payload.getUserId());
        return payload.getChatId();
    }
}