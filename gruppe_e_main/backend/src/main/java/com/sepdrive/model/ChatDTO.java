package com.sepdrive.model;

import lombok.Data;

import java.util.Date;

@Data
public class ChatDTO {
    private Long id;
    private String message;
    private Long senderId;
    private Long receiverId;
    private Long rideMatchId;
    private boolean isRead;
    private Date createdAt;
    private Date updatedAt;
}