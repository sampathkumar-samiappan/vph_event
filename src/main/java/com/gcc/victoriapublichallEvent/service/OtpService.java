package com.gcc.victoriapublichallEvent.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    public String sendOtpToMobile(HttpServletRequest request, String userInput, int otp) {
        String urlString = "https://tmegov.onex-aura.com/api/sms?key=pfTEYN6H&to=" + userInput
                + "&from=GCCCRP&body=OTP for your Victoria Public Hall booking " + otp
                + "\r\n- GCC&entityid=1401572690000011081&templateid=1407176491887361640";
        System.out.println("\n urlString---:  " + urlString);
        String bodyMessage = "" + otp;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String Response = restTemplate.getForObject(urlString, String.class);
            bodyMessage = Response;
        } catch (Exception e) {
            e.printStackTrace();
            bodyMessage = "Error";
        }

        HttpSession session = request.getSession();
        session.setAttribute("otp", otp);
        return bodyMessage;
    }

    public void sendOtpToEmail(HttpServletRequest request, String recipientEmail, int otp) throws MessagingException {

        System.out.println("hi from send OTP to Email: " + recipientEmail + "\n" + otp);
        String htmlMsg = "<p>Dear Visitor,</p>" +
                "<p>Your One-Time Password (OTP) for verifying your visit booking to the <b> Victoria Public Hall</b> is:</p>"
                +
                "<h2><b>" + otp + "</b></h2>" +
                "<p>Thank you,<br>" +
                "<b> Victoria Public Hall Team</b></p>";

        String subject = "Victoria Public Hall Booking - Verification Code";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom("noreplyvph@gmail.com");
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(htmlMsg, true);

        mailSender.send(mimeMessage);

        HttpSession session = request.getSession();
        session.setAttribute("otp", otp);
    }

    public void sendConfirmationMessage(String userInput, Map<String, Object> bookingDetails)
            throws MessagingException {

        boolean isMobile = userInput.matches("^[6-9]\\d{9}$");

        // Basic map extraction - assuming bookingDetails has these keys
        String Date = (String) bookingDetails.get("visit_date");
        String Time = (String) bookingDetails.get("timing");
        String mobNo = (String) bookingDetails.get("mob_no");
        String slots = (String) bookingDetails.get("timings");
        String recipientEmail = (String) bookingDetails.get("email");
        int no_of_people = (Integer) bookingDetails.get("no_of_people");

        String bodyMessage = "";
        String urlString = "";

        if (isMobile) {
            urlString = "https://tmegov.onex-aura.com/api/sms?key=pfTEYN6H&to=" + userInput
                    + "&from=GCCCRP&body=Your visit to Victoria Public Hall is confirmed for "
                    + Date + " at " + Time + ".\r\nPhone number " + mobNo + " \r\nAV Experience slots " + slots
                    + "- GCC&entityid=1401572690000011081&templateid=1407176491912950994";

            System.out.println("\n urlString---:  " + urlString);

            try {
                RestTemplate restTemplate = new RestTemplate();
                String Response = restTemplate.getForObject(urlString, String.class);
                bodyMessage = Response;
            } catch (Exception e) {
                e.printStackTrace();
                bodyMessage = "Error";
            }

        } else {
            System.out.println("hi from booking confirmation: " + recipientEmail);
            String htmlMsg = "<p>Dear Visitor,</p>" +
                    "<p>Your booking for <b>Victoria Public Hall</b> has been confirmed.</p>" +
                    "<p><b>Date : </b> " + Date + "</p>" +
                    "<p><b>Time : </b> " + Time + "</p>" +
                    "<p><b>Total Guest(s) : </b> " + no_of_people + "</p>" +
                    "<p><b>AV Experience Slots : </b> " + slots + "</p>" +
                    "<p>Thank you,<br>" +
                    "<b>Victoria Public Hall Team</b></p>";

            String subject = "Victoria Public Hall Booking - Confirmation";

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("noreplyvph@gmail.com");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        }
    }

    public void sendCancelaltionMessage(String userInput, Map<String, Object> bookingDetails)
            throws MessagingException {

        boolean isMobile = userInput.matches("^[6-9]\\d{9}$");

        String Date = (String) bookingDetails.get("visit_date");
        String Time = (String) bookingDetails.get("timing");
        String recipientEmail = (String) bookingDetails.get("email");

        String bodyMessage = "";
        String urlString = "";

        if (isMobile) {
            urlString = "https://tmegov.onex-aura.com/api/sms?key=pfTEYN6H&to=" + userInput
                    + "&from=GCCCRP&body=Your Victoria Public Hall booking for "
                    + Date + " at " + Time
                    + " has been cancelled. You may make a new booking on our website.\r\n- GCC&entityid=1401572690000011081&templateid=1407176491899104951";

            System.out.println("\n urlString---:  " + urlString);

            try {
                RestTemplate restTemplate = new RestTemplate();
                String Response = restTemplate.getForObject(urlString, String.class);
                bodyMessage = Response;
            } catch (Exception e) {
                e.printStackTrace();
                bodyMessage = "Error";
            }

        } else {
            System.out.println("hi from booking cancelaltion: " + recipientEmail);
            String htmlMsg = "<p>Dear Visitor,</p>" +
                    "<p>Your booking for <b>Victoria Public Hall</b> has been <b>Cancelled.</b></p>" +
                    "<p>You may make a new booking anytime through the official Victoria Public Hall website.</p>" +
                    "<p>Regards,<br>" +
                    "<b>Victoria Public Hall Team</b></p>";

            String subject = "Victoria Public Hall Booking - Cancellation";

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom("noreplyvph@gmail.com");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(htmlMsg, true);

            mailSender.send(mimeMessage);
        }
    }
}
