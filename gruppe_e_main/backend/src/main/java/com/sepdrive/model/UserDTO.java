package com.sepdrive.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserDTO {

    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private LocalDate birthdate;
    private Role role;
    private byte[] profilePicture;
    private String profilePictureContentType;
    private Car carType;
    private String password;
}
