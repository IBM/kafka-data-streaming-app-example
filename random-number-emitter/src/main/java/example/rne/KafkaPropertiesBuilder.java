/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.rne;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public abstract class KafkaPropertiesBuilder {

	protected Properties props = new Properties();
	protected String bootstrapServers;

	public KafkaPropertiesBuilder() {
			//Do something if needed
	}
	
	protected abstract KafkaPropertiesBuilder config() throws IllegalStateException;

	public static ConsumerBuilder createConsumerBuilder() {
		return new ConsumerBuilder();
	}

	public static ProducerBuilder createProducerBuilder() {
		return new ProducerBuilder();
	}

	public KafkaPropertiesBuilder setBootstrapServers(String bootstrapServers) {
		this.bootstrapServers = bootstrapServers;
		return this;
	}

	public KafkaPropertiesBuilder useLocalBootstrapServer() {
		this.bootstrapServers = "localhost:9092";
		return this;
	}

	/**
	 * Load the value from the environment variable KAFKA_BOOTSTRAP_SERVERS if
	 * existed. If not exist, 'localhost:9092' will be used instead
	 */
	public KafkaPropertiesBuilder loadBootstrapServersFromEnv() {
		String bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
		if (bootstrapServers == null || bootstrapServers.isEmpty())
			useLocalBootstrapServer();
		else
			setBootstrapServers(bootstrapServers);
		return this;
	}

	protected void validate() throws IllegalStateException {
		if (bootstrapServers == null || bootstrapServers.isBlank())
			throw new IllegalStateException("No any bootstrap server was specified");
	}


	public Properties build() throws IllegalStateException {
		validate();
		config();
		return props;
	}

	public static class ConsumerBuilder extends KafkaPropertiesBuilder {
		private String groupId;
		
		public KafkaPropertiesBuilder setGroupId(String groupId){
			this.groupId = groupId;
			return this;
		}

		/**
		 * Assume that DESERIALIZER_CLASS if key and value is StringDeserializer
		 **/
		@Override
		protected KafkaPropertiesBuilder config() throws IllegalStateException {
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
			
			if(groupId == null || groupId.isBlank())
				throw new IllegalStateException("ConsumerConfig.GROUP_ID_CONFIG is not specified");
			props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
			
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			return this;
		}
	}

	public static class ProducerBuilder extends KafkaPropertiesBuilder {

		/**
		 * Assume that SERIALIZER_CLASS if key and value is StringSerializer
		 **/
		@Override
		protected KafkaPropertiesBuilder config() throws IllegalStateException {
			props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
			return this;
		}
	}

}
