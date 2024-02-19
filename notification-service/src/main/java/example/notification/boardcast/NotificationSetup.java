/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.notification.boardcast;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.objenesis.instantiator.util.ClassUtils;

import example.notification.senders.Sender;

/**This class is responsible for creating Sender instances 
 * based on the property notification.subscription.senders,
 * and then each of the senders will be registered to receive messages 
 * to deliver them as notifications to the corresponding targets.
 * <p>Whenever a new kind of Sender is required, 
 * it can be configured by simply providing an implementation of the Sender interface, 
 * and then adding the class name of the implementation to notification.subscription.senders in the application.yml </p>
 * **/
@Configuration
public class NotificationSetup {
	private final Logger log = LoggerFactory.getLogger(NotificationSetup.class);
	
	@Autowired
	private ApplicationContext appContext;


	@Value("${notification.subscription.senders}")
	private String senderSubscriptions;
	
	@Bean
	public Notification notificationService() {
		NotificationService notificationService = new NotificationService();
		
		Collection<String> senders = new DefaultListDelimiterHandler(',').split(senderSubscriptions, true);
		senders.forEach(className -> {
			try {
				Sender sender = createSenderByClassName(className);
				notificationService.register(sender);
				log.info("Added an " + className + " instance to the subscription list of senders" );
			} catch(Exception e) {
				log.warn("Skipped. Failed to load or create an instance for the class " + className, e);
			}
		});
		
		return notificationService; 
	}
	
	private Sender createSenderByClassName(final String className) throws IllegalStateException {
		try {
			Sender sender;
			
			Class<Sender> senderClass = ClassUtils.getExistingClass(this.getClass().getClassLoader(), className);
			
			if(senderClass == null)
				throw new ClassNotFoundException(className);
			
			if(senderClass.isAnnotationPresent(org.springframework.stereotype.Component.class)) {
				log.info(className + " is a Spring @Component");
				sender = appContext.getBean(senderClass);
			} 
			else //it is loaded as a normal Java class
				sender = //this.getClass().getClassLoader().loadClass(className)
						senderClass.asSubclass(Sender.class)
						.getDeclaredConstructor().newInstance();
			
			return sender;
			
		} catch (ClassCastException | InstantiationException | IllegalArgumentException | IllegalAccessException
				| ClassNotFoundException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalStateException(e);
		}
	}
}
