/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 * 
 * Author: Nguyen, Hung (Howie) Sy
 *******************************************************************************/

package example.rne.admin;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import example.rne.EmittingDataService;
import example.rne.TaskStartException;

public class EmittingServiceCommandHandler implements CommandHandler {
	private final static Logger log = LoggerFactory.getLogger(EmittingServiceCommandHandler.class);
	
	private EmittingDataService emittingDataService;
	private SystemLogger systemLogger = new DefaultSystemLogger();
	
	public static EmittingServiceCommandHandler createInstance(EmittingDataService emittingDataService, SystemLogger systemLogger) {
		return new EmittingServiceCommandHandler(emittingDataService, systemLogger);
	}

	/**
	 * @param emittingDataService required
	 * @param systemLogger can be null. If null, DefaultSystemLogger will be used **/
	public EmittingServiceCommandHandler(EmittingDataService emittingDataService, SystemLogger systemLogger) {
		this.emittingDataService = emittingDataService;
		
		if(systemLogger != null)
			this.systemLogger = systemLogger;
	}

	@Override
	public void doHandle(String jsonCommandStr) {
		log.info("Got a command: " + jsonCommandStr);
		
		Gson gson = new Gson();
		Command command = gson.fromJson(jsonCommandStr, Command.class);
		String strCommand = command.getCommand();
		
		if(strCommand == null) strCommand = "";
		
		if(Command.COMMAND_STOP.equalsIgnoreCase(strCommand) || Command.COMMAND_SHUTDOWN.equalsIgnoreCase(strCommand))
		{
			systemLogger.log("The Emitting Service has received a stop request by the command: " + command.toString());
			emittingDataService.signalStop();
			log.info("Requested to stop ...");
		}
		else if(Command.COMMAND_START.equalsIgnoreCase(strCommand)){
			log.info("Trying starting...");
			systemLogger.log("The Emitting Service is being started as requested by the command: " + command.toString());

			try {
				Consumer<String> onFinish = (s) -> {
					log.info("Finished. The status returned from the task: " + s);
					systemLogger.log("The Emitting Service has been stopped as requested");
				};
				
				emittingDataService.startAsync(onFinish);
				
			}catch (TaskStartException e) {
				log.warn(e.getMessage());
				systemLogger.log("Warning: the Emitting Service failed to run: " + e.getMessage());
			}
		}
		else 
			log.info("The command is not supported. The valid commands are 'stop' and 'start'");
	}
}
