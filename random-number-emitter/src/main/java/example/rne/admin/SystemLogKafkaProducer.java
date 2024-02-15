/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.rne.admin;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import example.rne.KafkaPropertiesBuilder;

public class SystemLogKafkaProducer implements SystemLogger, AutoCloseable {
	
	private Producer<String, String> kafkaProducer;
	
	private String systemLogTopic;
	
	public SystemLogKafkaProducer(String systemLogTopic) {
		
		this.systemLogTopic = systemLogTopic;
		
		Properties props = KafkaPropertiesBuilder.createProducerBuilder()
				.useLocalBootstrapServer()
				.build();
		
		kafkaProducer = new KafkaProducer<String, String>(props);
	}
	
	public static SystemLogger createInstance(String systemLogTopic) {
		return new SystemLogKafkaProducer(systemLogTopic);  
	}

	public String getSystemLogTopic() {
		return systemLogTopic;
	}

	@Override
	public void log(String message) {
		kafkaProducer.send(new ProducerRecord<String, String>(systemLogTopic, 
				String.valueOf(System.currentTimeMillis()), message));
	}
	
	@Override
	public void close() {
		kafkaProducer.close();
	}
}
