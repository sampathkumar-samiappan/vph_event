package com.gcc.victoriapublichallEvent.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gcc.victoriapublichallEvent.entity.EventMaster;
import com.gcc.victoriapublichallEvent.entity.EventRegDetails;
import com.gcc.victoriapublichallEvent.entity.EventTimeSlot;
import com.gcc.victoriapublichallEvent.repository.EventMasterRepository;
import com.gcc.victoriapublichallEvent.repository.EventRegDetailsRepository;
import com.gcc.victoriapublichallEvent.repository.EventTimeSlotRepository;

import com.gcc.victoriapublichallEvent.entity.EventOrderLog;
import com.gcc.victoriapublichallEvent.entity.EventPayment;
import com.gcc.victoriapublichallEvent.repository.EventOrderLogRepository;
import com.gcc.victoriapublichallEvent.repository.EventPaymentRepository;

@Service
public class BookingService {

    @Autowired
    private EventMasterRepository eventMasterRepository;

    @Autowired
    private EventTimeSlotRepository eventTimeSlotRepository;

    @Autowired
    private EventRegDetailsRepository eventRegDetailsRepository;

    public ResponseEntity<?> getbookingDetailsByUser(String mobile) {
        // Implementation for history if needed
        return ResponseEntity.ok().build();
    }

    public List<EventMaster> getAllActiveEvents() {
        String today = LocalDate.now().toString();
        // Assuming eventDate is stored as YYYY-MM-DD
        return eventMasterRepository.findAllByIsActiveAndIsPublishAndEventDateGreaterThanEqualOrderByEventDateAsc(true,
                true, today);
    }

    public EventMaster getEventById(Integer eventId) {
        if (eventId == null)
            return null;
        return eventMasterRepository.findById(eventId).orElse(null);
    }

    public EventMaster getActiveEvent() {
        // Fallback or default
        String today = LocalDate.now().toString();
        List<EventMaster> events = eventMasterRepository
                .findAllByIsActiveAndIsPublishAndEventDateGreaterThanEqualOrderByEventDateAsc(true, true, today);
        if (!events.isEmpty())
            return events.get(0);
        return null;
    }

    public List<EventTimeSlot> getActiveTimeSlots(Integer eventId) {
        return eventTimeSlotRepository.findByEventIdAndIsActiveTrue(eventId);
    }

    public EventRegDetails getBookingByOrderId(String orderId) {
        // We are checking ref_id now as order_id column doesn't exist
        return eventRegDetailsRepository.findByRefId(orderId);
    }

    public EventTimeSlot getTimeSlotById(Integer id) {
        if (id == null)
            return null;
        return eventTimeSlotRepository.findById(id).orElse(null);
    }

    @Autowired
    private EventOrderLogRepository eventOrderLogRepository;

    @Autowired
    private EventPaymentRepository eventPaymentRepository;

    public EventRegDetails saveBooking(Map<String, Object> data) {
        try {
            EventRegDetails reg = new EventRegDetails();

            if (data.get("mobile_no") != null)
                reg.setMobNo(data.get("mobile_no").toString());

            Double totalAmount = 0.0;
            if (data.get("amt") != null) {
                totalAmount = Double.parseDouble(data.get("amt").toString()) / 100.0;
                reg.setTotalAmount(totalAmount);
            }

            // Map Razorpay Order ID to ref_id column
            String refId = null;
            if (data.get("order_id") != null)
                refId = data.get("order_id").toString();
            else if (data.get("id") != null) // Fallback to internal ref if order_id missing (unlikely)
                refId = data.get("id").toString();

            reg.setRefId(refId);

            // Payment ID cannot be saved as column is missing
            String paymentId = null;
            if (data.get("payment_id") != null) {
                paymentId = data.get("payment_id").toString();
                // reg.setPaymentId(data.get("payment_id").toString());
            }

            reg.setPaymentStatus("SUCCESS");
            reg.setBookingFlag("BOOKED");

            // We might need to extract time_slot_id and no_of_people from data if passed,
            // or if we stored them in session/temp.
            // For now, let's assume valid data is passed or update controller to pass it.
            if (data.get("time_slot_id") != null) {
                try {
                    Integer tId = Integer.parseInt(data.get("time_slot_id").toString());
                    // We need to look up the slot to get the eventId and to update capacity
                    EventTimeSlot slot = getTimeSlotById(tId);
                    if (slot != null) {
                        reg.setEventId(slot.getEventId());

                        // Capacity Update (Moved here as we have the slot)
                        if (reg.getNoOfPeople() != null && slot.getMaxCapacity() != null) {
                            int currentCapacity = slot.getMaxCapacity();
                            int bookedCount = reg.getNoOfPeople();
                            slot.setMaxCapacity(currentCapacity - bookedCount);
                            eventTimeSlotRepository.save(slot);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing time slot: " + e.getMessage());
                }
            }

            if (data.get("no_of_people") != null) {
                try {
                    reg.setNoOfPeople(Integer.parseInt(data.get("no_of_people").toString()));
                } catch (Exception e) {
                }
            }

            // Set default fields requested by user
            reg.setSource(true); // Assuming 'true' for Online/Web source
            reg.setActive(true);
            reg.setDelete(false);
            // cdate is set by default in entity to LocalDateTime.now()

            EventRegDetails savedReg = eventRegDetailsRepository.save(reg);

            // --- Save to EventOrderLog ---
            try {
                EventOrderLog orderLog = new EventOrderLog();
                orderLog.setOrderId(refId);
                orderLog.setRefId(refId); // Using order_id as ref_id logic based on request

                try {
                    org.json.JSONObject json = new org.json.JSONObject(data);
                    orderLog.setOrderInfo(json.toString());
                } catch (Exception ex) {
                    orderLog.setOrderInfo("Order Placed - JSON Error");
                }

                orderLog.setOrderStatus("SUCCESS");
                orderLog.setPayAttempt(1);
                // id and cdate are auto-generated
                eventOrderLogRepository.save(orderLog);
            } catch (Exception e) {
                System.err.println("Error saving EventOrderLog: " + e.getMessage());
            }

            // --- Save to EventPayment ---
            try {
                EventPayment payment = new EventPayment();
                payment.setMobileNo(reg.getMobNo());
                payment.setOrderId(refId); // Razorpay Order ID
                payment.setPaymentId(paymentId);
                payment.setPaymentStatus("SUCCESS");

                // Use the internal ref_id from data if available
                String internalRefId = null;
                if (data.get("id") != null) {
                    internalRefId = data.get("id").toString();
                }
                payment.setRefId(internalRefId != null ? internalRefId : refId); // Fallback to order_id if null, or
                                                                                 // just set internalRefId

                payment.setAmount(totalAmount);
                // id and paymentDate are auto-generated
                eventPaymentRepository.save(payment);
            } catch (Exception e) {
                System.err.println("Error saving EventPayment: " + e.getMessage());
            }

            return savedReg;

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error saving booking: " + e.getMessage());
            return null;
        }
    }

    public EventRegDetails getActiveBookingByMobile(String mobileNo) {
        List<EventRegDetails> list = eventRegDetailsRepository.findByMobNoAndIsDeleteFalse(mobileNo);
        if (list != null && !list.isEmpty()) {
            return list.get(0); // Return the first active booking
        }
        return null;
    }

    public List<EventRegDetails> getAllActiveBookingsByMobile(String mobileNo) {
        return eventRegDetailsRepository.findByMobNoAndIsDeleteFalse(mobileNo);
    }

    public boolean cancelBooking(String mobileNo) {
        try {
            List<EventRegDetails> bookings = eventRegDetailsRepository.findByMobNoAndIsDeleteFalse(mobileNo);
            if (bookings != null && !bookings.isEmpty()) {
                for (EventRegDetails reg : bookings) {
                    // Restore capacity - DISABLED as we lost time_slot_id link
                    /*
                     * if (reg.getTimeSlotId() != null && reg.getNoOfPeople() != null) {
                     * Optional<EventTimeSlot> timeSlotOpt =
                     * eventTimeSlotRepository.findById(reg.getTimeSlotId());
                     * if (timeSlotOpt.isPresent()) {
                     * EventTimeSlot timeSlot = timeSlotOpt.get();
                     * if (timeSlot.getMaxCapacity() != null) {
                     * timeSlot.setMaxCapacity(timeSlot.getMaxCapacity() + reg.getNoOfPeople());
                     * eventTimeSlotRepository.save(timeSlot);
                     * }
                     * }
                     * }
                     */
                    // Soft delete
                    reg.setDelete(true);
                    eventRegDetailsRepository.save(reg);
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public EventRegDetails getActiveBookingByMobileAndEvent(String mobileNo, Integer eventId) {
        List<EventRegDetails> list = eventRegDetailsRepository.findByMobNoAndIsDeleteFalse(mobileNo);
        if (list != null && !list.isEmpty()) {
            for (EventRegDetails reg : list) {
                if (reg.getEventId() != null && reg.getEventId().equals(eventId)) {
                    return reg;
                }
            }
        }
        return null;
    }
}
