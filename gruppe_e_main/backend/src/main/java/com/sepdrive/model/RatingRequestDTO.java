package com.sepdrive.model;

import lombok.Data;

@Data
public class RatingRequestDTO {
    private Long id;
    private int rating;
    private Role role;
}
