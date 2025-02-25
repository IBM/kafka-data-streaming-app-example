/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 * 
 * Author: Nguyen, Hung (Howie) Sy
 *******************************************************************************/

package example.notification.senders;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;

import reactor.core.publisher.Mono;

@Component
public class SlackSender implements Sender {
	
	private final Logger log = LoggerFactory.getLogger(SlackSender.class);
	
	@Value("${notification.slack.channel}")
	private String channel;

	@Value("${notification.slack.token}")
	private String token;
	
	/**Perform sending the message
	 * @param  message
	 * @param timeInMilliseconds if null the current time will be used 
	 * @return void
	 **/
	@Override
	public void send(String message, Long timeInMilliseconds) throws Exception {
		if(timeInMilliseconds == null)
			timeInMilliseconds = System.currentTimeMillis();
		
		Instant instanceTime = Instant.ofEpochMilli(timeInMilliseconds);
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instanceTime, ZoneId.systemDefault());
		
		WebClient client = WebClient.builder()
				  .baseUrl("https://slack.com/api")
				  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
				  .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token) 
				  .build();
		
		
		Gson gson = new Gson();
		String jsonEscapedMsg = gson.toJson("Notification Service >> " + localDateTime + " : *" + message + "*", String.class).toString();
		log.info(jsonEscapedMsg);
		
		Pattern channelPattern = Pattern.compile("^[A-Za-z0-9\\-_]+");
		if (channel == null || !channelPattern.matcher(channel).matches()) {
			 throw new IllegalArgumentException("The channel name is invalid");
		}
		
		Mono<String> response = client.post()
			.uri("/chat.postMessage")
			.body(BodyInserters.fromValue("{channel: \"" + channel + "\", text: " + jsonEscapedMsg + "}"))
			.retrieve()
			.bodyToMono(String.class);
		
		//calling response.block() to make the request, however it is a synchronous operation
		//log.info(response.block());
		
		//we can do it in a non-blocking way (i.e asynchronously) like this:
		response.toFuture().thenAccept(s -> { 
				log.info("The message was sent out. Response: " +  s);
			});
	}

}
