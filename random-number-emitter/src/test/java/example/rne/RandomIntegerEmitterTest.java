/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.rne;

import static org.junit.jupiter.api.Assertions.*;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;

public class RandomIntegerEmitterTest {
	
	private final String TEST_TOPIC_NAME = "number-emitter-test-topic";
    
    private MockProducer<String, String> createMockProducer(boolean autoComplete) {
        return new MockProducer<>(autoComplete, new StringSerializer(), new StringSerializer());
    }

    @Test
    void requestStop() {
        MockProducer<String, String> mockProducer = createMockProducer(true);
    	RandomIntegerKafkaProducer integerEmitter = new RandomIntegerKafkaProducer(TEST_TOPIC_NAME, mockProducer);
    	
    	//try 10 times
    	int n = 10;
    	
    	integerEmitter.emitData(() -> {
    		if (integerEmitter.getCount() >= n)
    			return true;
    		return false;
    	});
     
    	assertTrue(mockProducer.history().size() == n);
    }
    
    @Test
    void emitANumber() {
        MockProducer<String, String> mockProducer = createMockProducer(true);
    	RandomIntegerKafkaProducer integerEmitter = new RandomIntegerKafkaProducer(TEST_TOPIC_NAME, mockProducer);
    	
    	integerEmitter.emitData(() -> {
    		if (integerEmitter.getCount() >= 1)
    			return true;
    		return false;
    	});
     
    	try
    	{
    		int value = Integer.parseInt(mockProducer.history().get(0).value());
    		assertTrue(true);
    			
    	}catch (NumberFormatException e) {
    		assertFalse(true, "It's not a number");
		}
    	
    	
    }

}
