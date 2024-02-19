/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.notification.senders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSender implements Sender {
	private final Logger log = LoggerFactory.getLogger(EmailSender.class);

	@Override
	public void send(String message, Long timeInMilliseconds) {
		log.info("EmailSender.send() received the message: " + message);
		log.info("Nothing happen, not implememented yet");
		// TODO ...
	}
}
