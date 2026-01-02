package com.sepdrive.controller;


import com.sepdrive.model.*;
import com.sepdrive.service.CustomerService;
import com.sepdrive.service.DriverService;
import com.sepdrive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestPart("user") UserDTO userDTO,
            // required=false-- this file is not must be, can null
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture
    ) throws IOException {

        if (profilePicture != null && !profilePicture.isEmpty()) {

            byte[] imageBytes = profilePicture.getBytes();
            userDTO.setProfilePicture(imageBytes);
            userDTO.setProfilePictureContentType(profilePicture.getContentType());
        }
        userService.register(userDTO);

        return ResponseEntity.ok("success");
    }

    //Dependency Injection from the Spring context
    @Autowired
    private CustomerService customerService;
    @Autowired
    private DriverService driverService;


    //Return the user information via search function and click function
    @GetMapping("/search/{username}")
    public ResponseEntity<?> getUserName(@PathVariable String username) {

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username must not be empty");
        }

        //Überprüfe zuerst die Kundentabelle
        CustomerDTO customerDTO = customerService.searchCustomer(username);
        if (customerDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
        }

        //Falls die Kundentabelle leer ist, überprüfe die Fahrertabelle nach dem Nutzer
        DriverDTO driverDTO = driverService.searchDriver(username);
        if (driverDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(driverDTO);
        }

        //If none of the conditions are true return error Message
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user not found.");

    }
}
