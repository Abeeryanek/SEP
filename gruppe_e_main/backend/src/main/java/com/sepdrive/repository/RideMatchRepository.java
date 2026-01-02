package com.sepdrive.repository;

import com.sepdrive.model.RideMatch;
import com.sepdrive.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideMatchRepository extends JpaRepository<RideMatch, Long> {

    List<RideMatch> findByCustomer(User customer);
    List<RideMatch> findByDriver(User driver);
    RideMatch getRideMatchById(Long id);
    //suche nach ride match id
    Optional<RideMatch> findRideMatchById(Long rideId);
    //fahrthistroie sorting
    @Query("SELECT rm FROM RideMatch rm " +
            "WHERE (rm.customer.username= :username OR rm.driver.username = :username) " +
            "AND rm.simulationStatus = 'COMPLETED'")
    List<RideMatch> findCompletedRidesByUsername(@Param("username") String username);
    //find avg rating
    @Query("SELECT COALESCE(AVG(r.ratingDriver), 0.0) FROM RideMatch r WHERE r.driver.username = :username")
    Double getAverageRatingDriver(@Param("username") String username);
}