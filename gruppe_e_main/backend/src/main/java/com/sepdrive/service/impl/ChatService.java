package com.sepdrive.service.impl;



    import com.sepdrive.model.Chat;
    import com.sepdrive.model.ChatDTO;
    import com.sepdrive.repository.ChatRepository;
    import com.sepdrive.repository.RideMatchRepository;
    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

    import java.util.Date;
    import java.util.List;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private RideMatchRepository rideMatchRepository;

    //  Nachrichten senden
    public Chat sendMessage(Chat chat) {
        chat.setRead(false);
        chat.setCreatedAt(new Date());
        chat.setUpdatedAt(new Date());

        return chatRepository.save(chat);
    }

    public List<Chat> getAllMessagesForRideMatch(Long rideMatchId) {
        return chatRepository.findByRideMatch_Id(rideMatchId);
    }

    public void markAsRead(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat nicht gefunden"));
        chat.setRead(true);
        chat.setUpdatedAt(new Date());
        chatRepository.save(chat);
    }

    //  Bearbeiten NUR Absender
    public Chat editMessage(Long chatId, String newMessage, Long currentUserId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat nicht gefunden"));
        if (!chat.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("Nur Absender darf bearbeiten!");
        }
        if (!chat.isRead()) {
            chat.setMessage(newMessage);
            chat.setUpdatedAt(new Date());
            return chatRepository.save(chat);
        } else {
            throw new RuntimeException("Nachricht wurde bereits gelesen und kann nicht bearbeitet werden");
        }
    }

    // Löschen NUR Absender
    public void deleteMessage(Long chatId, Long currentUserId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat nicht gefunden"));
        if (!chat.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("Nur Absender darf löschen!");
        }
        if (!chat.isRead()) {
            chatRepository.delete(chat);
        } else {
            throw new RuntimeException("Nachricht wurde bereits gelesen und kann nicht gelöscht werden");
        }
    }

    public Chat findById(Long chatId) {
        return chatRepository.findById(chatId).orElse(null);
    }

    public ChatDTO toDTO(Chat chat) {
        ChatDTO dto = new ChatDTO();
        dto.setId(chat.getId());
        dto.setMessage(chat.getMessage());
        dto.setSenderId(chat.getSenderId());
        dto.setReceiverId(chat.getReceiverId());
        dto.setRideMatchId(chat.getRideMatch().getId());
        dto.setRead(chat.isRead());
        dto.setCreatedAt(chat.getCreatedAt());
        dto.setUpdatedAt(chat.getUpdatedAt());
        return dto;
    }
}

