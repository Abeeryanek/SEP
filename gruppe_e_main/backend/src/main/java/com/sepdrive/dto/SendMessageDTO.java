package com.sepdrive.dto;

import lombok.Data;

@Data
public class SendMessageDTO {
    private Long rideMatchId;
    private Long senderId;
    private Long receiverId;
    private String message;
}
