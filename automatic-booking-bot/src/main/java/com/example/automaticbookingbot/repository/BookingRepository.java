package com.example.automaticbookingbot.repository;

import com.example.automaticbookingbot.entity.Booking;
import com.example.automaticbookingbot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserOrderByBookingTimeDesc(User user);
    List<Booking> findByUserAndActiveOrderByBookingTimeDesc(User user, boolean active);
    List<Booking> findByActiveTrueAndActionTriggeredFalseAndBookingTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
}
