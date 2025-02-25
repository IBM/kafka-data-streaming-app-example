/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 * 
 * Author: Nguyen, Hung (Howie) Sy
 *******************************************************************************/

package example.rne;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RandomIntegerKafkaProducer implements Emitter{
	private final Logger log = LoggerFactory.getLogger(RandomIntegerKafkaProducer.class);

	private Producer<String, String> kafkaProducer;
	private String kafKaTopic;
	
	private int count = 0;
	
	public RandomIntegerKafkaProducer(String kafKaTopic) {
		Properties props = KafkaPropertiesBuilder.createProducerBuilder()
				.useLocalBootstrapServer()
				.build();
		
		kafkaProducer = new KafkaProducer<String, String>(props);
		
		this.kafKaTopic = kafKaTopic;
	}
	
	//this constructor might be useful for testing
	public RandomIntegerKafkaProducer(String kafKaTopic, Producer<String, String> kafkaProducer) {
		this.kafkaProducer = kafkaProducer;
		this.kafKaTopic = kafKaTopic;
	}

	@Override
	public void emitData(Supplier<Boolean> requestStop){
		log.info("Generating and emitting random numbers to Kafka...");
		Random random = new Random();
		try {
			//if 'true' is returned from the function requestStop, then stop the emitting loop
			while (!requestStop.get()) {
				//a random integer from 0 to 99
				int value = random.nextInt(100);
				
				kafkaProducer.send(new ProducerRecord<String, String>(kafKaTopic, "event-key", String.valueOf(value)));
				log.info(String.format("Emit #%s -> %s ", count, value));
				count ++;
				
				//random speed
				TimeUnit.MILLISECONDS.sleep(random.nextInt(30) * 50 + 100);
			}
			log.info("The thread is stopped");
		} catch(InterruptedException e) {
			log.info("The thread is interrupted");
		}
	}
	
	public String getKafkaTopicName() {
		return kafKaTopic;
	}
	
	public int getCount() {
		return count;
	}
	
	@Override
	public void close() {
		kafkaProducer.close();
	}

}
