package com.sepdrive.dto;

import lombok.Data;

@Data
public class EditMessageDTO {
    private Long chatId;
    private String newMessage;
    private Long userId;
}
