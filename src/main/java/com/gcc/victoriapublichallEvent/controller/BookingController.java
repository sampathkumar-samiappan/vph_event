package com.gcc.victoriapublichallEvent.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcc.victoriapublichallEvent.constants.AppConstants;
import com.gcc.victoriapublichallEvent.entity.EventMaster;
import com.gcc.victoriapublichallEvent.entity.EventRegDetails;
import com.gcc.victoriapublichallEvent.entity.EventTimeSlot;
import com.gcc.victoriapublichallEvent.service.BookingService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("/victoriapublichallevent/bookingapi")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @RequestMapping("/create_order")
    public String createOrder(@RequestBody Map<String, Object> data, @RequestParam("ref_id") String refId)
            throws RazorpayException {
        System.out.println("Create order function executed");
        System.out.println(data);

        // Check if data contains amount, if not, hardcode for test or handle error
        // Frontend sends {amount: X, ...}

        int amt = 500; // Default fallback
        if (data.get("amount") != null) {
            try {
                amt = Integer.parseInt(data.get("amount").toString());
            } catch (NumberFormatException e) {
                // handle fraction or string
                Double d = Double.parseDouble(data.get("amount").toString());
                amt = d.intValue();
            }
        }

        RazorpayClient client = new RazorpayClient(AppConstants.key_id, AppConstants.key_secret);

        JSONObject options = new JSONObject();
        options.put("amount", amt * 100); // Amount in paise
        options.put("currency", "INR");
        options.put("receipt", "txn_" + refId);

        Order order = client.orders.create(options);
        System.out.println(order);

        return order.toString();
    }

    @PostMapping("/Confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, Object> data) {
        System.out.println("Payment Confirmation: " + data);

        EventRegDetails savedBooking = bookingService.saveBooking(data);

        if (savedBooking != null) {
            return ResponseEntity.ok("Confirmed");
        } else {
            return ResponseEntity.status(500).body("Failed to save booking");
        }
    }

    @org.springframework.web.bind.annotation.GetMapping("/check-booking")
    public ResponseEntity<?> checkBooking(@RequestParam("mobile_no") String mobileNo,
            @RequestParam(value = "event_id", required = false) Integer eventId) {

        EventRegDetails existing = null;

        if (eventId != null) {
            existing = bookingService.getActiveBookingByMobileAndEvent(mobileNo, eventId);
        } else {
            // Fallback for backward compatibility or strict mode if no event specified
            existing = bookingService.getActiveBookingByMobile(mobileNo);
        }

        if (existing != null) {
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("found", true);
            response.put("booking", existing);

            // Fetch Event Details
            try {
                if (existing.getEventId() != null) {
                    com.gcc.victoriapublichallEvent.entity.EventMaster event = bookingService
                            .getEventById(existing.getEventId());
                    if (event != null) {
                        response.put("eventName", event.getEventName());
                        response.put("eventDate", event.getEventDate());
                    }
                    // Time Slot ID is lost, so we cannot retrieve generic event time unless we
                    // store it
                    // specifically aside from ID
                    // response.put("eventTime", "N/A");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(java.util.Collections.singletonMap("found", false));
        }
    }

    @org.springframework.web.bind.annotation.GetMapping("/get-all-bookings")
    public ResponseEntity<?> getBookingsList(@RequestParam("mobile_no") String mobileNo) {
        java.util.List<EventRegDetails> list = bookingService.getAllActiveBookingsByMobile(mobileNo);
        java.util.List<Map<String, Object>> result = new java.util.ArrayList<>();

        if (list != null && !list.isEmpty()) {
            for (EventRegDetails reg : list) {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("booking", reg);

                // Fetch Event Details
                try {
                    if (reg.getEventId() != null) {
                        com.gcc.victoriapublichallEvent.entity.EventMaster event = bookingService
                                .getEventById(reg.getEventId());
                        if (event != null) {
                            map.put("eventName", event.getEventName());
                            map.put("eventDate", event.getEventDate());
                            map.put("eventImage", event.getEventImageUrl());
                        }
                        // response.put("eventTime", "N/A");
                    }
                    result.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/cancel-booking")
    public ResponseEntity<?> cancelBooking(@RequestBody Map<String, String> data) {
        String mobileNo = data.get("mobile_no");
        boolean success = bookingService.cancelBooking(mobileNo);
        if (success) {
            return ResponseEntity.ok("Cancelled");
        } else {
            return ResponseEntity.status(400).body("Failed to cancel or booking not found");
        }
    }

    @GetMapping("/downloadReceipt")
    public ResponseEntity<byte[]> downloadReceipt(@RequestParam("orderid") String orderId) throws Exception {

        orderId = orderId.replace("\"", "").trim();
        EventRegDetails booking = bookingService.getBookingByOrderId(orderId);

        if (booking == null) {
            return ResponseEntity.status(404).body(null);
        }

        EventMaster event = bookingService.getEventById(booking.getEventId());
        EventTimeSlot timeSlot = bookingService.getBookingTimeSlot(orderId);

        // ✅ Thymeleaf template HTML generate pannrom
        String html = bookingService.generateReceiptHtmlFromTemplate(booking, event, timeSlot);

        byte[] pdfBytes = bookingService.generateReceiptPdfFromHtml(html);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"Victoria_Public_Hall_Event_Booking_Receipt.pdf\"")

                // .header("Content-Disposition", "attachment; filename=\"receipt_" + orderId +
                // ".pdf\"")
                .header("Content-Type", "application/pdf")
                .header("Content-Length", String.valueOf(pdfBytes.length))
                .body(pdfBytes);
    }

}
