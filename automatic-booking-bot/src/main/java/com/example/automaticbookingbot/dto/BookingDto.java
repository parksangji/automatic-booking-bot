package com.example.automaticbookingbot.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {

    private Long id;

    @NotEmpty(message = "Site name cannot be empty.")
    private String siteName;

    @NotEmpty(message = "Site URL cannot be empty.")
    @URL(message = "Please enter a valid URL.")
    private String siteUrl;

    @NotEmpty(message = "Login ID cannot be empty.")
    private String loginId;

//    @NotEmpty(message = "Password cannot be empty.")
    private String loginPassword;

    @NotNull(message = "Booking time cannot be empty.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @FutureOrPresent(message = "Booking time must be in the present or future.")
    private LocalDateTime bookingTime;

    private boolean active = true;

    private String displayBookingTime;
    private String createdByUsername;
}