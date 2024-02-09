/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.dashboard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import example.dashboard.services.*;

@CrossOrigin
@RestController
public class KafkaConsumer {
	
	private KafkaConsumeService consumeService;
	
	@Autowired
	public void setConsumeService(KafkaConsumeService consumeService) {
	        this.consumeService = consumeService;
	}
	
	@GetMapping("/liveness")
	public String liveness() {
		
		return "alive!";
	}
	
	@GetMapping("/sse-random-numbers")
	public SseEmitter sseRandomNumbers() {
		final SseEmitter emitter = new SseEmitter(0L);
		consumeService.registerRandomNumbersEmitters(emitter);
	    return emitter;
	}
	
	@GetMapping("/sse-logs")
	public SseEmitter logsMonitor() {
		final SseEmitter emitter = new SseEmitter(0L);
		consumeService.registerLogEmitters(emitter);
	    return emitter;
	}
	
	@GetMapping("/sse-mq")
	public SseEmitter sseMQ() {
		final SseEmitter emitter = new SseEmitter(0L);
		consumeService.registerMqEmitters(emitter);
	    return emitter;
	}
	
	@GetMapping("/sse-ace")
	public SseEmitter sseACE() {
		final SseEmitter emitter = new SseEmitter(0L);
		consumeService.registerAceEmitters(emitter);
	    return emitter;
	}
	
	@GetMapping("/sse-db2")
	public SseEmitter sseDB2() {
		final SseEmitter emitter = new SseEmitter(0L);
		consumeService.registerDb2Emitters(emitter);
	    return emitter;
	}
}