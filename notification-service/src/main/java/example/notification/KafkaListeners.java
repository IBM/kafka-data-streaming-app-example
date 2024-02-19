/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.notification;

import java.time.Instant;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import example.notification.boardcast.Notification;

@Service
public class KafkaListeners {
	//Just in case, how to stop/start individual listeners: github.com/spring-projects/spring-kafka/issues/938
	private final Logger log = LoggerFactory.getLogger(KafkaListeners.class);

	@Autowired
	private Notification notificationService;
	
	@KafkaListener(topics = "number-emitter-admin-topic")
	public void numberEmitterAdminListener(ConsumerRecord<String, String> record)
	{
		broadcast(record);
	}
	
	@KafkaListener(topics = "number-emitter-system-log-topic")
	public void numberEmitterSystemLogListener(ConsumerRecord<String, String> record)
	{
		broadcast(record);
	}
	
	private void broadcast(ConsumerRecord<String, String> record) {
		long timeInMills = record.timestamp();
		String message = record.value();
		log.info(String.format("Got a message: \"%s\" emitted at %s", message, Instant.ofEpochMilli(timeInMills).toString()));
		log.info("Broadcasting the notification...");
		try {
			notificationService.broadcast(message, timeInMills);
		} catch (Exception e) {
			log.error("An error occured when attempting to broadcast the message", e);
		}
	}
}
