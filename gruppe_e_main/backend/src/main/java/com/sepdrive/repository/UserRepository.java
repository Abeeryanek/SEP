package com.sepdrive.repository;

import com.sepdrive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
// <User, Long>: user is the entity class, Long is the type of the primary key
public interface UserRepository extends JpaRepository<User, Long> {

    //1. extends JpaRepository, 2. Spring scan Respository interface
    // and create implementation for every interface method and analyse mehthod name with Query method keywords
    // for example: existesby--> existence, username--> username, parameter is String username to generate sql query
    // proxy class run sql and get result
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    User findUserByUsername(String username);
}
