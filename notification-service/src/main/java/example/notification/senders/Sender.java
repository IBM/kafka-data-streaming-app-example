/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.notification.senders;

public interface Sender {
	public void send(String message, Long timeInMilliseconds) throws Exception;
}
