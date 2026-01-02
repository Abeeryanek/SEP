package com.sepdrive.service;


import com.sepdrive.model.User;
import com.sepdrive.model.UserDTO;

public interface UserService {

    void register(UserDTO userDTO);

    User findByUsername(String username);
}
