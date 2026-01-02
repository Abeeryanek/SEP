package com.sepdrive.service;

import com.sepdrive.model.DriverDTO;
import com.sepdrive.model.DriverProfile;
import com.sepdrive.repository.DriverRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;


@Service
public class DriverService {


    @Autowired
    private DriverRepository driverRepository;


    public DriverDTO searchDriver(String username) {

        DriverProfile driverProfile = driverRepository.findByUsername(username);
        if (driverProfile == null) {
            return null;
        }

        DriverDTO driverDTO = new DriverDTO();
        BeanUtils.copyProperties(driverProfile, driverDTO);

        byte[] pictureBytes = driverProfile.getProfilePicture();
        String contentType = driverProfile.getProfilePictureContentType();
        if (pictureBytes != null && pictureBytes.length > 0) {
            String base64 = Base64.getEncoder().encodeToString(pictureBytes);

            String dataUrl = "data:" + contentType + ";base64," + base64;
            driverDTO.setProfilePictureBase64(dataUrl);
        }

        return driverDTO;
    }

    public DriverProfile searchDriverProfile(String username) {
        return driverRepository.findByUsername(username);
    }


}
