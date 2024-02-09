/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/
 
package example.rne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import example.rne.admin.CommandHandler;
import example.rne.admin.CommandHandlerImpl;
import example.rne.admin.CommandListener;

public class RandomNumberEmitterMain {
	private final static Logger log = LoggerFactory.getLogger(RandomNumberEmitterMain.class);
	
	//you can change the topic name as needed
	private final static String COMMAND_TOPIC_NAME = "number-emitter-admin-topic";
	
	//you can change the topic name as needed
	private final static String RANDOM_NUMBERS_TOPIC_NAME = "number-emitter-topic";
	

	public static void main(String[] args) {
		//For now we have only RandomIntegerEmitter in this example. 
		//However in the future we may have another types of Emitter, for example let say RandomDecimalEmitter
		Emitter emitter = new RandomIntegerEmitter(RandomNumberEmitterMain.RANDOM_NUMBERS_TOPIC_NAME);
		
		EmittingDataService emittingDataService = EmittingDataService.createInstance(emitter);

		CommandHandler commandHandler = new CommandHandlerImpl(emittingDataService);
		
		log.info("Start listenning on the command topic (%s)...", RandomNumberEmitterMain.COMMAND_TOPIC_NAME);
		CommandListener commandListener = CommandListener.getInstance();
		commandListener.startListeningOnCommands(RandomNumberEmitterMain.COMMAND_TOPIC_NAME, commandHandler);
		
		emittingDataService.shutdown();
		log.info("The program was stopped!");
	}
	
	

}
