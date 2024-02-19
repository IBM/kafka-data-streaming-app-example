/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.notification.boardcast;

public interface Notification {
	public void broadcast(String message, Long timeInMilliseconds) throws Exception;
}
