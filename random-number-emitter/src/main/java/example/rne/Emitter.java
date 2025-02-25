/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 * 
 * Author: Nguyen, Hung (Howie) Sy
 *******************************************************************************/

package example.rne;

import java.util.function.Supplier;

/**You may want to have different kinds of emitters. 
 * 
 * Having this interface to help with that. 
 * In that way, The EmittingDataService doesn't depend on or care what the real emitters (concrete class) are 
 * since it invokes the emitters via this interface
 * **/
public interface Emitter {
	public void close();
	
	/** Execute the emitting process
	 * @param requestStop the call back method is provided to tell the emitting process when to stop. 
	 * To stop, return a true value
	 * **/
	public void emitData(Supplier<Boolean> requestStop);
}
