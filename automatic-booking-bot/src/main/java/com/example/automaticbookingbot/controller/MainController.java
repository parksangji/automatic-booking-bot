package com.example.automaticbookingbot.controller;

import com.example.automaticbookingbot.dto.BookingDto;
import com.example.automaticbookingbot.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private final BookingService bookingService;

    public MainController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/main")
    public String mainPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();

        model.addAttribute("htmlPageTitle", username + "'s Booking Dashboard");
        model.addAttribute("pageHeaderTitle", "Booking Dashboard");

        List<BookingDto> bookings = bookingService.getBookingsByUsername(username);
        model.addAttribute("bookings", bookings);

        Map<String, Object> flashMap = model.asMap();
        model.addAttribute("openModalFor", flashMap.get("openModalFor"));
        model.addAttribute("modalBookingId", flashMap.get("modalBookingId"));
        model.addAttribute("modalBookingDataForScript", flashMap.get("modalBookingData"));

        if (flashMap.containsKey("org.springframework.validation.BindingResult.modalBookingForm")) {
            model.addAttribute("bookingData", flashMap.get("modalBookingData"));
            model.addAttribute("org.springframework.validation.BindingResult.bookingData",
                    flashMap.get("org.springframework.validation.BindingResult.modalBookingForm"));
        } else {
            if (!model.containsAttribute("bookingData")) {
                model.addAttribute("bookingData", new BookingDto());
            }
        }

        return "main";
    }
    @GetMapping("/")
    public String home(Principal principal) {
        if (principal != null) {
            return "redirect:/main";
        }
        return "redirect:/login";
    }
}
