package com.example.automaticbookingbot.controller;

import com.example.automaticbookingbot.dto.BookingDto;
import com.example.automaticbookingbot.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(value = "/add-form", produces = MediaType.TEXT_HTML_VALUE)
    public String getAddBookingForm(Model model) {
        model.addAttribute("bookingData", new BookingDto());
        model.addAttribute("formActionUrl", "/bookings/add");
        model.addAttribute("submitButtonText", "Add Booking");
        return "bookings/_bookingFormModalContents :: bookingFormContents";
    }

    @PostMapping("/add")
    public String addBooking(@Valid @ModelAttribute("bookingData") BookingDto bookingDto,
                             BindingResult result, Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (principal == null) { return "redirect:/login"; }

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.modalBookingForm", result);
            redirectAttributes.addFlashAttribute("modalBookingData", bookingDto);
            redirectAttributes.addFlashAttribute("openModalFor", "add");
            return "redirect:/main";
        }
        try {
            bookingService.createBooking(bookingDto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Booking added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding booking: " + e.getMessage());
            redirectAttributes.addFlashAttribute("modalBookingData", bookingDto);
            redirectAttributes.addFlashAttribute("openModalFor", "add");
        }
        return "redirect:/main";
    }

    @GetMapping(value = "/{id}/edit-form", produces = MediaType.TEXT_HTML_VALUE)
    public String getEditBookingForm(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            model.addAttribute("ajaxError", "Authentication required.");
            return "bookings/_bookingFormModalContents :: bookingFormContents";
        }
        try {
            BookingDto bookingDto = bookingService.getBookingByIdAndUsername(id, principal.getName());
            bookingDto.setLoginPassword("");
            model.addAttribute("bookingData", bookingDto);
            model.addAttribute("formActionUrl", "/bookings/" + id + "/update");
            model.addAttribute("submitButtonText", "Update Booking");
        } catch (RuntimeException e) {
            model.addAttribute("ajaxError", "Booking not found or access denied.");
            model.addAttribute("bookingData", new BookingDto());
            model.addAttribute("formActionUrl", "/bookings");
            model.addAttribute("submitButtonText", "Error");
        }
        return "bookings/_bookingFormModalContents :: bookingFormContents";
    }


    @PostMapping("/{id}/update")
    public String updateBooking(@PathVariable Long id,
                                @Valid @ModelAttribute("bookingData") BookingDto bookingDto,
                                BindingResult result, Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (principal == null) { return "redirect:/login"; }

        bookingDto.setId(id);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.modalBookingForm", result);
            redirectAttributes.addFlashAttribute("modalBookingData", bookingDto);
            redirectAttributes.addFlashAttribute("openModalFor", "edit");
            redirectAttributes.addFlashAttribute("modalBookingId", id);
            return "redirect:/main";
        }
        try {
            bookingService.updateBooking(id, bookingDto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Booking updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update booking: " + e.getMessage());
            redirectAttributes.addFlashAttribute("modalBookingData", bookingDto);
            redirectAttributes.addFlashAttribute("openModalFor", "edit");
            redirectAttributes.addFlashAttribute("modalBookingId", id);
        }
        return "redirect:/main";
    }

    @PostMapping("/{id}/delete")
    public String deleteBooking(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) { return "redirect:/login"; }
        try {
            bookingService.deleteBooking(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Booking deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete booking: " + e.getMessage());
        }
        return "redirect:/main";
    }

    @PostMapping("/{id}/toggle-status")
    public String toggleBookingStatus(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) { return "redirect:/login"; }
        try {
            BookingDto updatedBooking = bookingService.toggleBookingStatus(id, principal.getName());
            String status = updatedBooking.isActive() ? "activated" : "deactivated";
            redirectAttributes.addFlashAttribute("successMessage", "Booking " + status + " successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to change booking status: " + e.getMessage());
        }
        return "redirect:/main";
    }
}