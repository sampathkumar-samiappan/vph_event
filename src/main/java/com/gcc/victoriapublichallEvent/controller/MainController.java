package com.gcc.victoriapublichallEvent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import java.util.List;

import com.gcc.victoriapublichallEvent.entity.EventMaster;
import com.gcc.victoriapublichallEvent.entity.EventRegDetails;
import com.gcc.victoriapublichallEvent.entity.EventTimeSlot;
import com.gcc.victoriapublichallEvent.service.BookingService;
import com.gcc.victoriapublichallEvent.service.EncryptionService;
import com.gcc.victoriapublichallEvent.constants.AppConstants;

@Controller
@RequestMapping(value = { "/victoriapublichallevent" })
public class MainController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EncryptionService encryptionService;

    @GetMapping("/bookingdetails")
    public String bookingDetails() {
        return "user/booking-details";
    }

    @GetMapping(value = { "/", "", "/eventhome" })
    public String eventHome(Model model) {
        List<EventMaster> events = bookingService.getAllActiveEvents();
        model.addAttribute("events", events);

        if (!events.isEmpty()) {
            events.forEach(e -> e.setEncryptedEventId(encryptionService.encrypt(String.valueOf(e.getEventId()))));
            model.addAttribute("event", events.get(0));
        }

        // if (!events.isEmpty()) {
        // model.addAttribute("event", events.get(0));
        // }

        return "user/event-home";
    }

    @RequestMapping(value = "/eventbooking", method = { RequestMethod.GET, RequestMethod.POST })
    public String eventBooking(@RequestParam(required = false) String encryptedEventId, Model model) {

        EventMaster event = null;

        if (encryptedEventId != null) {
            String decryptedStr = encryptionService.decrypt(encryptedEventId);
            if (decryptedStr != null) {
                try {
                    Integer decryptedId = Integer.parseInt(decryptedStr);
                    event = bookingService.getEventById(decryptedId);
                } catch (NumberFormatException e) {
                    return "redirect:/eventhome";
                }
            } else {
                return "redirect:/eventhome";
            }
        } else {
            event = bookingService.getActiveEvent();
        }

        if (event != null) {
            model.addAttribute("event", event);
            List<EventTimeSlot> slots = bookingService.getActiveTimeSlots(event.getEventId());
            model.addAttribute("timeSlots", slots);
        }

        // IMPORTANT LINE
        model.addAttribute("keyId", AppConstants.key_id);

        return "user/event-booking";
    }

    @GetMapping("/bookingQRCode")
    public String bookingQRCode(@RequestParam("orderid") String orderId, Model model) {
        System.out.println("Processing bookingQRCode for OrderID: " + orderId);
        EventRegDetails booking = bookingService.getBookingByOrderId(orderId);

        if (booking != null) {
            System.out.println("Booking found: " + booking.getId());
            model.addAttribute("booking", booking);

            if (booking.getEventId() != null) {
                // Time Slot info is not directly available from booking anymore
                EventMaster event = bookingService.getEventById(booking.getEventId());
                model.addAttribute("event", event);

                // Fetch Time Slot using the new service method
                EventTimeSlot timeSlot = bookingService.getBookingTimeSlot(orderId);
                model.addAttribute("timeSlot", timeSlot); // Add to model
            }
        } else {
            System.err.println("Booking NOT found for OrderID: " + orderId);
        }

        return "user/booking-success";
    }
}
