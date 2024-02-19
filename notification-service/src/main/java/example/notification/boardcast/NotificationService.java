/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.notification.boardcast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.notification.senders.Sender;

public class NotificationService implements Notification, AutoCloseable  {
	private final static Logger log = LoggerFactory.getLogger(NotificationService.class);

	private List<Sender> registrationList = new ArrayList<Sender>();
	
	private ExecutorService executor;

	public NotificationService(){
		//assume 1 thread in the pool. 
		// In practice, instead of using hard-coded value, we can load it dynamically from a property for example
		// Also the number of threads in the pool can be changed to scale up/down when needed
		executor = Executors.newFixedThreadPool(1);
	}
	
	public void register(Sender sender) {
		registrationList.add(sender);
	}

	/**This implementation of Notification provides the method broadcast() which is asynchronous. 
	 * It means it doesn't block the thread which is calling it**/
	@Override
	public void broadcast(String message, Long timeInMilliseconds) throws Exception {

		registrationList.forEach(sender -> {
			Supplier<String> sendTask = () -> {
				String returnedMsg;
				log.info("Send the notification using " + sender.getClass().getName());
				try {
					sender.send(message, timeInMilliseconds);
					returnedMsg = "The notification \"" + message + "\" has been sent succesfully";
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					returnedMsg = "Failed to sent the notification due to the error: " + e.getMessage();
				}
				
				return returnedMsg;
			};
			
			try {
				//Instead of Future<String> future = executor.submit(emittingTask), 
				//using CompletableFuture.supplyAsync() which allows us to invoke a callback like onFinish 
				//Also we can specify a Executor instance instead of the default 
				CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(sendTask, executor);
				
				//in case we need to do something when it finishes, try thenAcceptAsync
				Consumer<String> onComplete = msgReturnedWhenFinished -> log.info("Result: " + msgReturnedWhenFinished);
				completableFuture.thenAcceptAsync(onComplete, executor);
				
			} catch (Exception e) {
				log.warn("Failed to send the message using " + sender.getClass().getName(), e);
			}
		});
	}

	//instead of using finalize(), use AutoCloseable and close() instead 
	@Override
	public void close() throws Exception {
		executor.shutdown();
		try {
			if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
		}
	}
	
}
