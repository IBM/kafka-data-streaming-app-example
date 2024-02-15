/*******************************************************************************
 * Copyright IBM Corp. 2024-
 * SPDX-License-Identifier: Apache2.0
 *******************************************************************************/

package example.rne;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmittingDataService {
	private final Logger log = LoggerFactory.getLogger(EmittingDataService.class);
	private volatile  boolean stopFlag = false;
	private ExecutorService executor;
	
	private Emitter emitter;
	
	private Supplier<String> _emittingTask = () -> {
		emitter.emitData(() -> stopFlag);
		return "The emitting task has finished";
	};
	
	private CompletableFuture<Void> _completableFutureEmittingTask;

	public static EmittingDataService createInstance(Emitter emitter) {
		return new EmittingDataService(emitter);  
	}
	
	public EmittingDataService(Emitter emitter)
	{
		this.emitter = emitter;
		executor = Executors.newFixedThreadPool(3);
	}

	/** Produce random integers and send to the topic **/
	public void startAsync(Consumer<String> onFinish) throws TaskStartException {
		
		if(_completableFutureEmittingTask != null && !_completableFutureEmittingTask.isDone())
			throw new TaskStartException("Failed to start at this time. The task might have been started aleady and it may have been still running. Only one thread is allowed at a time");
		
		resetStopFlag();
		
		log.info(" Requesting to execute the emitting task...");
		CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(_emittingTask, executor);
		
		//when _emittingTask finishes, onFinish()  will be called
		_completableFutureEmittingTask =  completableFuture.thenAcceptAsync(onFinish);
	}
	
	public void shutdown()
	{
		executor.shutdown();
		try {
			if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
		}
		
		emitter.close();
	}
	
	/**Set stopFlag to true to signal all threads to stop	**/
	public synchronized void signalStop() {
		stopFlag = true;
	}
	
	/**Reset stopFlag to false**/
	public synchronized void resetStopFlag() {
		stopFlag = false;
	}
	
	public boolean getStopFlag() {
		return stopFlag;
	}
}
