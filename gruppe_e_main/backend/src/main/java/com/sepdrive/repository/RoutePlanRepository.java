package com.sepdrive.repository;

import com.sepdrive.model.RoutePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RoutePlanRepository extends JpaRepository<RoutePlan, Long> {
}
