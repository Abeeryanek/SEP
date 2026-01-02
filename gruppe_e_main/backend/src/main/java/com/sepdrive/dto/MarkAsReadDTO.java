package com.sepdrive.dto;

import lombok.Data;

@Data
public class MarkAsReadDTO {
    private Long chatId;
    private Long userId;
}
