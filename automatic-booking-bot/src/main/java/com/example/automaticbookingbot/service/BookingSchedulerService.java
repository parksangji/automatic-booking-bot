package com.example.automaticbookingbot.service;

import com.example.automaticbookingbot.entity.Booking;
import com.example.automaticbookingbot.repository.BookingRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class BookingSchedulerService {
    private static final Logger log = LoggerFactory.getLogger(BookingSchedulerService.class);
    private final BookingRepository bookingRepository;
    private final AutomatedBrowserService automatedBrowserService; // AutomatedBrowserService 주입

    public BookingSchedulerService(BookingRepository bookingRepository,
                                   AutomatedBrowserService automatedBrowserService) { // 생성자 수정
        this.bookingRepository = bookingRepository;
        this.automatedBrowserService = automatedBrowserService;
    }

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void checkForUpcomingBookingsAndTriggerAction() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime targetStartTime = now.plusMinutes(9); // 예시: 9분 후 부터
        LocalDateTime targetEndTime = now.plusMinutes(10).plusSeconds(30); // 예시: 10분 30초 후 까지

        log.info("SCHEDULER: Checking for bookings between {} and {}", targetStartTime, targetEndTime);

        List<Booking> upcomingBookings = bookingRepository
                .findByActiveTrueAndActionTriggeredFalseAndBookingTimeBetween(targetStartTime, targetEndTime);

        if (upcomingBookings.isEmpty()) {
            log.info("SCHEDULER: No upcoming bookings to trigger action for at this time.");
            return;
        }

        for (Booking booking : upcomingBookings) {
            log.info("SCHEDULER: Action triggered for booking ID: {}, User: {}, Site: {}, URL: {}, Booking Time: {}",
                    booking.getId(),
                    booking.getUser().getUsername(),
                    booking.getSiteName(),
                    booking.getSiteUrl(),
                    booking.getBookingTime());

            boolean loginSuccess = automatedBrowserService.loginToSite(
                    booking.getSiteUrl(),
                    booking.getLoginId(),
                    booking.getLoginPassword()
            );

            if (loginSuccess) {
                log.info("SCHEDULER: Successfully processed site entry for booking ID: {}", booking.getId());
                booking.setActionTriggered(true);
                bookingRepository.save(booking);
                log.info("SCHEDULER: Booking ID: {} marked as actionTriggered.", booking.getId());
            } else {
                log.warn("SCHEDULER: Failed to process site entry for booking ID: {}. Will retry on next schedule if still applicable.", booking.getId());
            }
        }
    }
}
