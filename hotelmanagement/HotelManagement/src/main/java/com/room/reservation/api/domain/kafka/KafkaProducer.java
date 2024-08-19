package com.room.reservation.api.domain.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class KafkaProducer {

@Autowired
 private KafkaTemplate<String, String> kafkaTemplate;
 
 
 public void sendMessage(String message)
 {
	 log.info("message sent by producer {} ",message);
	 kafkaTemplate.send("reservationss-topic", message);
	 log.info("sent");
 }

}
