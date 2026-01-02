package com.sepdrive.service.impl;


import com.sepdrive.exception.UserExistsException;
import com.sepdrive.model.*;
import com.sepdrive.repository.CustomerRepository;
import com.sepdrive.repository.DriverRepository;
import com.sepdrive.repository.UserRepository;
import com.sepdrive.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(UserDTO userDTO) {

        boolean exist = userRepository.existsByUsername(userDTO.getUsername());
        if (exist){
            throw new UserExistsException("username already exists");
        }

        User user = new User();
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setSuperCode("supercode123");
        user.setBalance(BigDecimal.ZERO);

        BeanUtils.copyProperties(userDTO,user);
        userRepository.save(user);

        //insert into userprofile table
        if (userDTO.getRole() == Role.KUNDE){
            CustomerProfile profile = new CustomerProfile();
            BeanUtils.copyProperties(userDTO, profile);
            profile.setRating(0.0);
            profile.setTotalTrips(0);
            profile.setCreateTime(LocalDateTime.now());
            profile.setUpdateTime(LocalDateTime.now());
            customerRepository.save(profile);
        }else {
            DriverProfile profile = new DriverProfile();
            BeanUtils.copyProperties(userDTO, profile);
            profile.setRating(0.0);
            profile.setTotalTrips(0);
            profile.setDrivenDistance(0.0);
            profile.setCreateTime(LocalDateTime.now());
            profile.setUpdateTime(LocalDateTime.now());

            driverRepository.save(profile);
        }
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }


}
