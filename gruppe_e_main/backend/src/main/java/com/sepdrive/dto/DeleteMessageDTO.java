package com.sepdrive.dto;

import lombok.Data;

@Data
public class DeleteMessageDTO {
    private Long chatId;
    private Long userId;
}
