package com.sepdrive.repository;

import com.sepdrive.model.Chat;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByRideMatch_Id(Long rideMatchId);
}
