package com.sepdrive.repository;

import com.sepdrive.model.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DriverRepository extends JpaRepository<DriverProfile, Long> {

    DriverProfile findByUsername(String username);
    //find all driver
    List<DriverProfile> findAllByUsername(@Param("username") String username);
}
