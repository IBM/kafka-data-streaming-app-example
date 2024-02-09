/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.rne.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import example.rne.EmittingDataService;
import example.rne.TaskStartException;

public class CommandHandlerImpl implements CommandHandler {
	private final static Logger log = LoggerFactory.getLogger(CommandHandlerImpl.class);
	
	private EmittingDataService emittingDataService;

	public CommandHandlerImpl(EmittingDataService emittingDataService) {
		this.emittingDataService = emittingDataService;
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
			emittingDataService.signalStop();
			log.info("Requested to stop ...");
		}
		else if(Command.COMMAND_START.equalsIgnoreCase(strCommand)){
			log.info("Trying starting...");
			try {
				emittingDataService.startAsync((s) -> log.info("Finished. The status returned from the task: " + s));
			}catch (TaskStartException e) {
				log.warn(e.getMessage());
			}
		}
		else 
			log.info("The command is not supported. The valid commands are 'stop' and 'start'");
		
	}

}
