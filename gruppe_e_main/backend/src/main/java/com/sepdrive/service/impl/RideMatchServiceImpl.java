package com.sepdrive.service.impl;


import com.sepdrive.controller.SimulationWebSocketBroadcaster;
import com.sepdrive.exception.BalanceException;
import com.sepdrive.exception.InvalidRoleException;
import com.sepdrive.exception.NotFoundException;
import com.sepdrive.model.*;
import com.sepdrive.repository.*;
import com.sepdrive.service.RideMatchService;
import com.sepdrive.service.RoutePlanService;
import com.sepdrive.service.SimulationStateService;
import com.sepdrive.service.TransactionService;
import com.sepdrive.utils.RatingUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideMatchServiceImpl implements RideMatchService {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private SimulationStateService simulationStateService;

    @Autowired
    private RideMatchRepository rideMatchRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimulationWebSocketBroadcaster broadcaster;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @Autowired
    private RoutePlanService routePlanService;


    @Override
    public RideMatchVO findByUsername(String username) {

        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        List<RideMatch> rideMatches = null;
        RideMatch rideMatch = null;

        switch (user.getRole()) {
            case KUNDE:
                rideMatches = rideMatchRepository.findByCustomer(user);
                break;
            case FAHRER:
                rideMatches = rideMatchRepository.findByDriver(user);
                break;
            default:
                throw new InvalidRoleException("Unsupported role: " + user.getRole());
        }

        if (rideMatches == null) {
            throw new NotFoundException("User do not have a ride match");
        }

        rideMatch = rideMatches.stream()
                .filter(r -> r.getSimulationStatus() != SimulationStatus.COMPLETED
                || r.getPaymentStatus() != PaymentStatus.PAID)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User does not have a active ride match"));


        RoutePlan routePlan = rideMatch.getRoutePlan();
        if (routePlan == null) {
            throw new NotFoundException("Route plan not found");
        }

        return toRideMatchVO(rideMatch, routePlan);
    }

    private RideMatchVO toRideMatchVO(RideMatch rideMatch, RoutePlan routePlan) {

        RideMatchVO rideMatchVO = new RideMatchVO();
        BeanUtils.copyProperties(rideMatch, rideMatchVO);

        rideMatchVO.setStartPoint(routePlan.getStartPoint());
        rideMatchVO.setEndPoint(routePlan.getEndPoint());
        rideMatchVO.setStopovers(routePlan.getStopovers());

        rideMatchVO.setTotalDistanceKm(routePlan.getTotalDistanceKm());
        rideMatchVO.setTotalDurationMin(routePlan.getDurationMin());
        rideMatchVO.setExpectedPrice(routePlan.getPrice().doubleValue());

        //Joseph
        rideMatchVO.setCustomer(toUserVO(rideMatch.getCustomer()));
        rideMatchVO.setDriver(toUserVO(rideMatch.getDriver()));

        return rideMatchVO;
    }

    @Override
    public void updateSimulation(PauseRequestDTO pauseRequest) {
        RideMatch rideMatch = rideMatchRepository.getRideMatchById(pauseRequest.getRideMatchId());

        if (rideMatch == null) {
            throw new NotFoundException("RideMatch not found");
        }

        rideMatch.setCurrentSimulationIndex(pauseRequest.getCurrentSimulationIndex());
        Double currentSpeed = simulationStateService.getSpeed(pauseRequest.getRideMatchId());

        rideMatchRepository.save(rideMatch);

        broadcaster.broadcastSimulationUpdated(pauseRequest.getRideMatchId(), pauseRequest.getCurrentSimulationIndex(), currentSpeed);
    }

    @Override
    public void startSimulation(Long rideMatchId) {
        RideMatch rideMatch = rideMatchRepository.getRideMatchById(rideMatchId);

        if (rideMatch == null) {
            throw new NotFoundException("RideMatch not found");
        }

        rideMatch.setSimulationStatus(SimulationStatus.IN_PROGRESS);

        Double currentSpeed = simulationStateService.getSpeed(rideMatchId);
        rideMatchRepository.save(rideMatch);

        broadcaster.broadcastSimulationStarted(rideMatchId, currentSpeed);
    }

    @Override
    public void pauseSimulation(PauseRequestDTO pauseRequest) {
        RideMatch rideMatch = rideMatchRepository.getRideMatchById(pauseRequest.getRideMatchId());

        if (rideMatch == null) {
            throw new NotFoundException("RideMatch not found");
        }

        rideMatch.setSimulationStatus(SimulationStatus.PAUSED);
        rideMatch.setCurrentSimulationIndex(pauseRequest.getCurrentSimulationIndex());

        Double currentSpeed = simulationStateService.getSpeed(pauseRequest.getRideMatchId());

        rideMatchRepository.save(rideMatch);

        broadcaster.broadcastSimulationPaused(pauseRequest.getRideMatchId(), pauseRequest.getCurrentSimulationIndex(), currentSpeed);
    }

    @Override
    public void resumeSimulation(Long rideMatchId) {
        RideMatch rideMatch = rideMatchRepository.getRideMatchById(rideMatchId);

        if (rideMatch == null) {
            throw new NotFoundException("RideMatch not found");
        }

        rideMatch.setSimulationStatus(SimulationStatus.IN_PROGRESS);
        Double currentSpeed = simulationStateService.getSpeed(rideMatchId);

        rideMatchRepository.save(rideMatch);
        broadcaster.broadcastSimulationResume(rideMatchId, rideMatch.getCurrentSimulationIndex(), currentSpeed);
    }

    @Override
    @Transactional
    public void completeSimulation(RideMatchVO finalRoute) {
        RideMatch rideMatch = rideMatchRepository.getRideMatchById(finalRoute.getId());

        if (rideMatch == null) {
            throw new NotFoundException("RideMatch not found");
        }

        rideMatch.setSimulationStatus(SimulationStatus.COMPLETED);

        User driver = rideMatch.getDriver();
        DriverProfile driverProfile = driverRepository.findByUsername(driver.getUsername());

        RoutePlan routePlan = rideMatch.getRoutePlan();
        routePlan.setStartPoint(finalRoute.getStartPoint());
        routePlan.setEndPoint(finalRoute.getEndPoint());
        routePlan.setStopovers(finalRoute.getStopovers());

        RoutePlan updatePlan = routePlanService.completeRoutePlan(routePlan, driverProfile.getCarType());
        rideMatch.setRoutePlan(updatePlan);
        rideMatch.setUpdateTime(LocalDateTime.now());
        rideMatch.setRidePrice(updatePlan.getPrice());

        rideMatchRepository.save(rideMatch);

        Double currentSpeed = simulationStateService.getSpeed(finalRoute.getId());

        broadcaster.broadcastSimulationCompleted(finalRoute.getId(), rideMatch.getCurrentSimulationIndex(), currentSpeed);
    }

    @Override
    public void requestPayment(Long rideMatchId) {
        RideMatch rideMatch = rideMatchRepository.getRideMatchById(rideMatchId);

        if (rideMatch == null) {
            throw new NotFoundException("RideMatch not found");
        }

        broadcaster.broadcastPaymentRequest(rideMatchId, rideMatch.getRidePrice());
    }

    @Override
    @Transactional
    public void processPayment(Long rideMatchId) {

        RideMatch rideMatch = rideMatchRepository.getRideMatchById(rideMatchId);

        if (rideMatch == null) {
            throw new NotFoundException("RideMatch not found");
        }

        BigDecimal price = rideMatch.getRidePrice();
        BigDecimal balance = rideMatch.getCustomer().getBalance();

        if (balance.compareTo(price) < 0){
            throw new BalanceException("Insufficient balance: please recharge");
        }

        transactionService.makePayment(
                rideMatchId,
                rideMatch.getCustomer(),
                rideMatch.getDriver());

        broadcaster.broadcastPaymentCompleted(rideMatchId);
    }

    @Override
    @Transactional
    public void saveRating(RatingRequestDTO ratingRequest) {
        RideMatch rideMatch = rideMatchRepository.getRideMatchById(ratingRequest.getId());

        if (rideMatch == null) {
            throw new NotFoundException("RideMatch not found");
        }

        if (ratingRequest.getRole() == Role.KUNDE){
            rideMatch.setRatingDriver(ratingRequest.getRating());

            DriverProfile driverProfile = driverRepository.findByUsername(rideMatch.getDriver().getUsername());
            List<Integer> ratings = driverProfile.getRatings();
            double rating = RatingUtil.computeRating(ratings, ratingRequest.getRating());
            driverProfile.setRating(rating);
            ratings.add(ratingRequest.getRating());
            driverProfile.setRatings(ratings);

            User driver = userRepository.findUserByUsername(rideMatch.getDriver().getUsername());

            driverProfile.setBalance(driver.getBalance());
            driverProfile.setDrivenDistance(driverProfile.getDrivenDistance()+rideMatch.getRoutePlan().getTotalDistanceKm());
            double currentRideDuration = driverProfile.getTotalDrivenTime();
            driverProfile.setTotalDrivenTime(currentRideDuration+rideMatch.getRoutePlan().getDurationMin());
            driverProfile.setTotalTrips(driverProfile.getTotalTrips()+1);

            driverProfile.setUpdateTime(LocalDateTime.now());

            driverRepository.save(driverProfile);

        }else if (ratingRequest.getRole() == Role.FAHRER){
            rideMatch.setRatingCustomer(ratingRequest.getRating());

            CustomerProfile customerProfile = customerRepository.findByUsername(rideMatch.getCustomer().getUsername());
            List<Integer> ratings = customerProfile.getRatings();
            double rating = RatingUtil.computeRating(ratings, ratingRequest.getRating());
            customerProfile.setRating(rating);
            ratings.add(ratingRequest.getRating());
            customerProfile.setRatings(ratings);

            User customer = userRepository.findUserByUsername(rideMatch.getCustomer().getUsername());

            customerProfile.setBalance(customer.getBalance());
            customerProfile.setTotalTrips(customerProfile.getTotalTrips()+1);
            customerProfile.setUpdateTime(LocalDateTime.now());
            customerRepository.save(customerProfile);
        }else {
            throw new InvalidRoleException("Invalid role");
        }

        rideMatchRepository.save(rideMatch);

        RideRequest rideRequest = rideRequestRepository.getRideRequestByRoutePlanId(rideMatch.getRoutePlan().getId());
        rideRequest.setStatus(RideRequestStatus.ABGESCHLOSSEN);
        rideRequestRepository.save(rideRequest);
    }

    @Override
    @Transactional
    public void changeRoute(RideMatchVO updateRoute) {

        RideMatch rideMatch = rideMatchRepository.getRideMatchById(updateRoute.getId());

        if (rideMatch == null) {
            throw new NotFoundException("RideMatch not found");
        }

        User driver = rideMatch.getDriver();
        DriverProfile driverProfile = driverRepository.findByUsername(driver.getUsername());

        RoutePlan routePlan = rideMatch.getRoutePlan();
        routePlan.setStartPoint(updateRoute.getStartPoint());
        routePlan.setEndPoint(updateRoute.getEndPoint());
        routePlan.setStopovers(updateRoute.getStopovers());

        RoutePlan updatePlan = routePlanService.completeRoutePlan(routePlan, driverProfile.getCarType());
        rideMatch.setRoutePlan(updatePlan);
        rideMatch.setUpdateTime(LocalDateTime.now());
        rideMatch.setRidePrice(updatePlan.getPrice());

        rideMatchRepository.save(rideMatch);

        RideMatchVO updatedVO = toRideMatchVO(rideMatch, updatePlan);
        broadcaster.broadcastUpdateRoute(updatedVO);
    }

    //joseph
    private UserVO toUserVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setFirstname(user.getFirstname());
        vo.setLastname(user.getLastname());
        vo.setRole(user.getRole());
        return vo;
    }
}
