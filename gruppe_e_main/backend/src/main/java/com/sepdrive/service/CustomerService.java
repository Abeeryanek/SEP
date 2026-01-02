package com.sepdrive.service;


import com.sepdrive.model.CustomerDTO;
import com.sepdrive.model.CustomerProfile;
import com.sepdrive.repository.CustomerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;


@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public CustomerDTO searchCustomer(String username) {

        CustomerProfile customerProfile = customerRepository.findByUsername(username);
        if (customerProfile == null) {
            return null;
        }
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customerProfile, customerDTO);

        byte[] pictureBytes = customerProfile.getProfilePicture();
        String contentType = customerProfile.getProfilePictureContentType();
        if (pictureBytes != null && pictureBytes.length > 0) {
            String base64 = Base64.getEncoder().encodeToString(pictureBytes);

            String dataUrl = "data:" + contentType + ";base64," + base64;
            customerDTO.setProfilePictureBase64(dataUrl);
        }

        return customerDTO;
    }


}
