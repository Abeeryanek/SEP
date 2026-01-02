package com.sepdrive.repository;

import com.sepdrive.model.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CustomerRepository extends JpaRepository<CustomerProfile, Long> {

    CustomerProfile findByUsername(String username);
}
