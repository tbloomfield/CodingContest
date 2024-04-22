package org.tbloomfield.codingcontest.virtualqueue.consumer;

import java.time.Clock;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.tbloomfield.codingcontest.service.DequeueHandler;
import org.tbloomfield.codingcontest.virtualqueue.VirtualQueue;
import org.tbloomfield.codingcontest.virtualqueue.usertypes.QueueUser;


/**
 * A threaded consumer which waits until the specified wall clock time to dequeue users from queue.  
 */
@Slf4j
@RequiredArgsConstructor
@Builder
public class ScheduledQueueConsumer implements QueueConsumer {
	
	private final Clock currentTime;
	private final Clock triggerTime;
	private final VirtualQueue queue;
	private final long dequeueSleepTimeMillis;
	private final DequeueHandler handler;
	
    /**
     * Condition for blocking when empty.
     */    
    private final ReentrantLock lock = new ReentrantLock();
	private final ThreadFactory factory = Thread.ofVirtual().factory();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(0, factory);
	
	@Override
	public void startConsumption() {		
		final ReentrantLock lock = this.lock;        
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
			return;
		}
				
		//block until consumption should start:
        try { 
			long timeUntilConsumption = Math.min(0, triggerTime.instant().toEpochMilli() - currentTime.instant().toEpochMilli());
			
			//after the window opens, dequeue at intervals.
			scheduler.scheduleAtFixedRate(() -> {			
				List<QueueUser> nextBatch = new ArrayList<>();
				queue.nonBlockingBatchDequeueTo(nextBatch, 5);

				//loop through each batch of users, handling each asynchronously
				nextBatch.forEach( user -> Thread.ofVirtual().start(() -> handler.handleDequeue(user)));
			}, timeUntilConsumption, dequeueSleepTimeMillis, TimeUnit.MILLISECONDS);
        } finally { 
            lock.unlock();
        }
	}

	@Override
	public void pauseConsumption() {
		scheduler.shutdown();
		try {
			scheduler.awaitTermination(10000, TimeUnit.MILLISECONDS);
		} catch(Exception e) { 
			log.error(e.getMessage());
		}
	}

}
