package com.gcc.victoriapublichallEvent.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcc.victoriapublichallEvent.service.BookingService;
import com.gcc.victoriapublichallEvent.service.OtpService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/victoriapublichallevent/otpapi")
public class OtpController {

    @Autowired
    OtpService otpService;

    @Autowired
    BookingService bookingService;

    @GetMapping("/send-otp")
    public ResponseEntity<?> sendOtp(HttpServletRequest request, @RequestParam String userInput) {

        HttpSession session = request.getSession();

        boolean isMobile = userInput.matches("^[6-9]\\d{9}$"); // Indian 10-digit mobile
        boolean isEmail = userInput.matches("^[A-Za-z0-9+_.-]+@(.+)$");

        int otp = new Random().nextInt(90000) + 10000;

        session.setAttribute("otp", otp);
        session.setAttribute("otpUser", userInput);

        System.out.println("OTP saved in session: " + otp);

        if (isMobile) {
            String res = otpService.sendOtpToMobile(request, userInput, otp);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "OTP sent to Mobile",
                    "OTP", otp));
        } else if (isEmail) {
            try {
                otpService.sendOtpToEmail(request, userInput, otp);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "OTP sent to Email",
                    "OTP", otp));
        } else {
            return ResponseEntity.ok(Map.of(
                    "status", "error",
                    "message", "Invalid Mobile Number or Email ID"));
        }
    }

    @GetMapping(value = "/verifyOTP")
    public ResponseEntity<?> verifyOtp(HttpServletRequest request, @RequestParam String otp,
            @RequestParam String userInput) {
        HttpSession session = request.getSession();
        Map<String, Object> response = new HashMap<>();
        try {

            Integer session_otp = (Integer) session.getAttribute("otp");
            String otpUser = (String) session.getAttribute("otpUser");
            System.out.println("session_otp---:" + session_otp);

            // Validate OTP matches input user
            if (otpUser == null || !otpUser.equals(userInput)) {
                response.put("verifiedotp", "error");
                response.put("message", "OTP does not belong to this mobile/email.");
                return ResponseEntity.ok(response);
            }

            Integer entered_otp = Integer.parseInt(otp);

            if (session_otp != null && session_otp.equals(entered_otp)) {
                System.out.println("Entered OTP Correct user API");
                session.setAttribute("verifiedotp", "success");
                ResponseEntity<?> regDetails = bookingService.getbookingDetailsByUser(userInput);

                response.put("verifiedotp", "success");
                response.put("message", "OTP verified successfully.");
                response.put("regDetails", regDetails.getBody());
                response.put("userInput", userInput);

                return ResponseEntity.ok(response);

            } else {
                System.out.println("Incorrect OTP. Please enter correct OTPs.");
                response.put("verifiedotp", "error");
                response.put("message", "Incorrect OTP. Please enter correct OTP.");
                return ResponseEntity.badRequest().body(response);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.put("verifiedotp", "error");
            response.put("message", "Invalid OTP format. OTP must be numeric.");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("verifiedotp", "error");
            response.put("message", "Something went wrong while verifying OTP.");
            return ResponseEntity.badRequest().body(response);
        }
    }

}
