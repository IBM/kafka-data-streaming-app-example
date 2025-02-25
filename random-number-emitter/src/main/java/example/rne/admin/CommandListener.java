/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 * 
 * Author: Nguyen, Hung (Howie) Sy
 *******************************************************************************/

package example.rne.admin;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import example.rne.KafkaPropertiesBuilder;

public class CommandListener {
	private final Logger log = LoggerFactory.getLogger(CommandListener.class);
	
	private volatile static CommandListener instance;
	
	private KafkaConsumer<String, String> kafkaConsumer;
	
	public static CommandListener getInstance() {
		//double check locking
		if (instance == null) { 
            synchronized (CommandListener.class) 
            { 
                if (instance == null) { 
                    instance = new CommandListener(); 
                } 
            } 
        } 
        return instance; 
	}

	public CommandListener() {
		
		Properties consumerProperties = KafkaPropertiesBuilder.createConsumerBuilder()
					.setGroupId("randome-number-command-listener")
					.useLocalBootstrapServer()
					.build();

		kafkaConsumer = new KafkaConsumer<>(consumerProperties);
	}
	
	public void startListeningOnCommands(String commandTopic, CommandHandler commandHandler) {
		final Thread currentThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log.info("Detected a shutdown. Calling kafkaConsumer.wakeup() to gracefully stop the polling loop...");
                kafkaConsumer.wakeup();
                try {
                	currentThread.join();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            }
        });

        try {
			kafkaConsumer.subscribe(Arrays.asList(commandTopic));
			Gson gson = new Gson();
			boolean isShutdownRequested = false;
			
			// the polling loop
	        while(!isShutdownRequested){
	            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(1000));
	
	            for (ConsumerRecord<String, String> record : records) {
	            	
	            	String recordValue = record.value();
	            	Command command;
	            	try {
	            		command = gson.fromJson(recordValue, Command.class);
	            		command.setTime(String.valueOf(record.timestamp()));
	            		
	            	}catch (JsonSyntaxException ex) {
	            		//in this case, treat recordValue as command
	            		command = new Command(recordValue, "unknown", String.valueOf(record.timestamp()));
					}
	            	
	            	if (Command.COMMAND_SHUTDOWN.equalsIgnoreCase(command.getCommand())) {
	            		isShutdownRequested = true;
	            	}

	            	//request an async execution of the commandHandler 
	            	String json = gson.toJson(command);
	    			Runnable consumeTask = () -> commandHandler.doHandle(json);
	            	CompletableFuture.runAsync(consumeTask);
	            }
	        }
        } catch (WakeupException e) {
        	log.info("The polling loop is signaled to be aborted by kafkaConsumer.wakeup()");
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        } finally {
        	kafkaConsumer.close(); 
            log.info("The consumer is gracefully closed");
        }
	}
}
