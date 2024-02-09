/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.rne.admin;

@FunctionalInterface
public interface CommandHandler {
	public void doHandle(String jsonCommand);
}
