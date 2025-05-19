package com.example.automaticbookingbot.service;

import com.example.automaticbookingbot.dto.BookingDto;
import com.example.automaticbookingbot.entity.Booking;
import com.example.automaticbookingbot.entity.User;
import com.example.automaticbookingbot.repository.BookingRepository;
import com.example.automaticbookingbot.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter DTO_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    public BookingService(BookingRepository bookingRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public BookingDto createBooking(BookingDto bookingDto, String username) {
        User currentUser = getCurrentUser(username);
        Booking booking = new Booking();
        dtoToEntity(bookingDto, booking);
        booking.setUser(currentUser);
        Booking savedBooking = bookingRepository.save(booking);
        return entityToDto(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUsername(String username) {
        User currentUser = getCurrentUser(username);
        return bookingRepository.findByUserOrderByBookingTimeDesc(currentUser)
                .stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingByIdAndUsername(Long id, String username) {
        User currentUser = getCurrentUser(username);
        Booking booking = bookingRepository.findById(id)
                .filter(b -> b.getUser().equals(currentUser))
                .orElseThrow(() -> new RuntimeException("Booking not found or access denied."));
        return entityToDto(booking);
    }

    @Transactional
    public BookingDto updateBooking(Long id, BookingDto bookingDto, String username) {
        User currentUser = getCurrentUser(username);
        Booking booking = bookingRepository.findById(id)
                .filter(b -> b.getUser().equals(currentUser))
                .orElseThrow(() -> new RuntimeException("Booking not found or access denied."));

        dtoToEntity(bookingDto, booking);
        Booking updatedBooking = bookingRepository.save(booking);
        return entityToDto(updatedBooking);
    }

    @Transactional
    public void deleteBooking(Long id, String username) {
        User currentUser = getCurrentUser(username);
        Booking booking = bookingRepository.findById(id)
                .filter(b -> b.getUser().equals(currentUser))
                .orElseThrow(() -> new RuntimeException("Booking not found or access denied."));
        bookingRepository.delete(booking);
    }

    @Transactional
    public BookingDto toggleBookingStatus(Long id, String username) {
        User currentUser = getCurrentUser(username);
        Booking booking = bookingRepository.findById(id)
                .filter(b -> b.getUser().equals(currentUser))
                .orElseThrow(() -> new RuntimeException("Booking not found or access denied."));
        booking.setActive(!booking.isActive());
        Booking updatedBooking = bookingRepository.save(booking);
        return entityToDto(updatedBooking);
    }

    private void dtoToEntity(BookingDto dto, Booking entity) {
        entity.setSiteName(dto.getSiteName());
        entity.setSiteUrl(dto.getSiteUrl());
        entity.setLoginId(dto.getLoginId());
        entity.setLoginPassword(dto.getLoginPassword());
        entity.setBookingTime(dto.getBookingTime());
        entity.setActive(dto.isActive());
    }

    private BookingDto entityToDto(Booking entity) {
        BookingDto dto = new BookingDto();
        dto.setId(entity.getId());
        dto.setSiteName(entity.getSiteName());
        dto.setSiteUrl(entity.getSiteUrl());
        dto.setLoginId(entity.getLoginId());
        dto.setBookingTime(entity.getBookingTime());
        dto.setDisplayBookingTime(entity.getBookingTime().format(DTO_DATE_FORMATTER));
        dto.setActive(entity.isActive());
        if (entity.getUser() != null) {
            dto.setCreatedByUsername(entity.getUser().getUsername());
        }
        return dto;
    }
}
