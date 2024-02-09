/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.rne;

public class TaskStartException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public TaskStartException(String message) {
		super(message);
	}
}
