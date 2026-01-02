package com.sepdrive.model;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private Role role;
}