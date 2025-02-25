/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 * 
 * Author: Nguyen, Hung (Howie) Sy
 *******************************************************************************/

package example.dashboard.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Service
public class KafkaConsumeService {
	private final Logger log = LoggerFactory.getLogger(KafkaConsumeService.class);
	
	private final Collection<SseEmitter> mqEmitters = Collections.synchronizedCollection(new HashSet<SseEmitter>());
	private final Collection<SseEmitter> randomNumbersEmitters = Collections.synchronizedCollection(new HashSet<SseEmitter>());
	private final Collection<SseEmitter> logsEmitters = Collections.synchronizedCollection(new HashSet<SseEmitter>());
	private final Collection<SseEmitter> aceEmitters = Collections.synchronizedCollection(new HashSet<SseEmitter>());
	private final Collection<SseEmitter> db2Emitters = Collections.synchronizedCollection(new HashSet<SseEmitter>());

	@KafkaListener(topics = "number-emitter-topic")
	public void consumeMyAppTopic(String message) {
		log.info(String.format("number-emitter-topic: %s", message));
		String id = String.valueOf(System.currentTimeMillis());
		DataItem dataItem = new DataItem(id, message, null);
	    emitEvent(dataItem, randomNumbersEmitters);
	}
	
	//Assume we use this topic to receive message from the log of the random number emitter service
	@KafkaListener(topics = "log4j-topic")
	public void consumeLog4JTopic(String message) {
		log.info(String.format("log4j-topic: %s", message));
		String id = String.valueOf(System.currentTimeMillis());
		DataItem dataItem = new DataItem(id, message, null);
  	    emitEvent(dataItem, logsEmitters);
	}
	
	//Assume we use this topic to receive message from MQ
	@KafkaListener(topics = "mq-topic")
	public void consumeMqTopic(String message) {
		log.info(String.format(" mq-topic: %s", message));
		String id = String.valueOf(System.currentTimeMillis());
		DataItem dataItem = new DataItem(id, message, null);		
		emitEvent(dataItem, mqEmitters);
	}
	
	//Assume we use this topic to receive message from certain an IBM App Connect (ACE) message flow
	@KafkaListener(topics = "ace-topic")
	public void consumeAceTopic(String message) {
		log.info(String.format("ace-topic: %s", message));
		String id = String.valueOf(System.currentTimeMillis());
		DataItem dataItem = new DataItem(id, message, null);
		emitEvent(dataItem, aceEmitters);
	}
	
	//Assume we capture data change events from a table of a certain DB2 database
	@KafkaListener(topics = "db2cdc.demo.mytable-json")
	public void consumeDb2Topic(ConsumerRecord<String, String> record) {
		String value = record.value();
		long timeInMilis = record.timestamp();
		Instant instanceTime = Instant.ofEpochMilli(timeInMilis);
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instanceTime, ZoneId.systemDefault());
		
		String id = String.valueOf(System.currentTimeMillis());
		DataItem dataItem = new DataItem(id, record.value(), timeInMilis);
		log.info(String.format("db2cdc.demo.mytable: [%s] %s", localDateTime.toString(), value));
		emitEvent(dataItem, db2Emitters);
	}
	
	public void registerRandomNumbersEmitters(SseEmitter emitter) {
	    log.info("Register the emitter to the RandomNumbers registration list");
		registerEmitters(emitter, randomNumbersEmitters);
	}
		
	public void registerLogEmitters(SseEmitter emitter) {
	    log.info("Register the emitter to the Logs registration list");
		registerEmitters(emitter, logsEmitters);
	}
		
	public void registerMqEmitters(SseEmitter emitter) {
	    log.info("Register the emitter to the MQ registration list");
		registerEmitters(emitter, mqEmitters);
	}
	
	
	public void registerAceEmitters(SseEmitter emitter) {
	    log.info("Register the emitter to the ACE registration list");
		registerEmitters(emitter, aceEmitters);
	}
	
	
	public void registerDb2Emitters(SseEmitter emitter) {
		log.info("Register the emitter to the DB2 registration list");
		registerEmitters(emitter, db2Emitters);
	}
	
	private void registerEmitters(SseEmitter emitter, Collection<SseEmitter> emitterRegistrationList) {
	    emitter.onTimeout(() -> timeoutHandler(emitter, emitterRegistrationList));
	    emitter.onCompletion(() -> completionHandler(emitter, emitterRegistrationList));
	    emitter.onError((e) -> errorHandler(emitter, emitterRegistrationList, e));
	    emitterRegistrationList.add(emitter);
	}
	
	private void completionHandler(SseEmitter emitter, Collection<SseEmitter> emitterRegistrationList) {
		log.info("Emitter completed - removed from the registration list");
		emitterRegistrationList.remove(emitter);
	}

	private void timeoutHandler(SseEmitter emitter, Collection<SseEmitter> emitterRegistrationList) {
		log.info("Emittermitter timeout - removed from the registration list");
		emitterRegistrationList.remove(emitter);
	}
	
	private void errorHandler(SseEmitter emitter, Collection<SseEmitter> emitterRegistrationList, Throwable throwable) {
		log.error("Emitter error: ", throwable.getMessage());
		log.info("Emitter is removed from the registration list");
		emitterRegistrationList.remove(emitter);
	}
	
	private void emitEvent(DataItem dataItem , Collection<SseEmitter> emitters) {
		
		Long dateTime = dataItem.getDatetime();
		
		//For the sake of the demo, to make it simple, there will be 2 cases based on if timestamp is null or not null 
		SseEventBuilder event;
		if(dateTime == null) {
			event = SseEmitter.event()
	                .data(dataItem.getContent())
	                .id(dataItem.getId())
	                .name("message");
		} else {
			Instant instanceTime = Instant.ofEpochMilli(dateTime);
			LocalDateTime localDateTime = LocalDateTime.ofInstant(instanceTime, ZoneId.systemDefault());
			
			JsonObject jsonObj = new JsonObject();
			jsonObj.addProperty("time", localDateTime.toString());
						
			JsonObject jsonContent =  JsonParser.parseString(dataItem.getContent()).getAsJsonObject();
			jsonObj.add("content", jsonContent);
		
			event = SseEmitter.event()
	                .data(jsonObj.toString())
	                .id(dataItem.getId())
	                .name("message");
		}

		for(SseEmitter emitter : emitters) {
	        try {
	        	emitter.send(event);
	        } catch (Throwable e) {
	        	try	{
	        		emitter.complete();
	        	}
	        	catch (Throwable ex) {
	        		log.warn("Fail to complete emitter - " + ex.getMessage() + ". Remove the emitter");
	        		emitters.remove(emitter);
	        	}
	        }
	    };
	}

}
