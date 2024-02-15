/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.rne.admin;

public class Command {
	
	public final static String COMMAND_STOP = "stop";
	public final static String COMMAND_START = "start";
	public final static String COMMAND_SHUTDOWN = "shutdown";
	
	private String user;
	
	/**
	 * It can accept either 'command name' or '{user: "username/id", command: "command name", time: "timestamp"}' </br>
	 * The valid values for command name is: stop, start and shutdown
	 */
	private String command;
	
	//to make it simple, use String
	private String time;
	
	Command(String command, String user, String time){
		this.command = command;
		this.user = user;
		this.time = time;
	}
	
	public String toString() {
		return String.format("{user: \"%s\", command: \"%s\", time: \"%s\"}", user, command, time);
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
