/*
 * package com.room.reservation.api.domain.kafka;
 * 
 * import java.util.List;
 * 
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.kafka.annotation.KafkaListener; import
 * org.springframework.stereotype.Service;
 * 
 * import com.room.reservation.api.domain.entities.RoomDetails; import
 * com.room.reservation.api.domain.enums.CustomHttpStatus; import
 * com.room.reservation.api.domain.exceptions.ValidationException; import
 * com.room.reservation.api.domain.repositories.RoomDetailsRepo;
 * 
 * import lombok.extern.slf4j.Slf4j;
 * 
 * @Slf4j
 * 
 * @Service public class KafkaConsumer {
 * 
 * @Autowired RoomDetailsRepo roomrepo;
 * 
 * @KafkaListener(topics = "reservations-topic", groupId = "reservation-group")
 * public void listen(String message) {
 * log.info("message received to kafka consumer listen , message {}", message);
 * 
 * if (message.startsWith("rooms reserved successfully with booking Id: ")) {
 * 
 * String bookingId = message.substring(message.lastIndexOf(" ") + 1); int
 * bookId = Integer.parseInt(bookingId);
 * 
 * updateroomDetails(bookId);
 * 
 * } }
 * 
 * void updateroomDetails(int bookId) {
 * log.info("kafka consumer method:updateroomDetails bookingId: {}", bookId);
 * try {
 * 
 * List<RoomDetails> rooms = roomrepo.findByBookingIdAndStatus(bookId, null);
 * 
 * if (rooms != null) {
 * 
 * for (RoomDetails eachRoom : rooms) {
 * 
 * eachRoom.setStatus("Occupied");
 * 
 * } roomrepo.saveAll(rooms);
 * 
 * } else { log.
 * error("Method Name:updateroomDetails ,Error while saving reservation: Booking object is null "
 * ); throw new
 * ValidationException("Error while  updating status of reserved rooms",
 * CustomHttpStatus.INTERNAL_SERVER_ERROR); } } catch (Exception e) {
 * log.error("kafka consumer listen not working"); throw new
 * ValidationException("kafka consumer listen not working",
 * CustomHttpStatus.INTERNAL_SERVER_ERROR); } }
 * 
 * }
 */