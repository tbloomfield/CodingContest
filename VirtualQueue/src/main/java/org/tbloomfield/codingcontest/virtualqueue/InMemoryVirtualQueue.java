package org.tbloomfield.codingcontest.virtualqueue;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.tbloomfield.codingcontest.virtualqueue.metrics.QueueMetrics;

import lombok.Builder;

public class InMemoryVirtualQueue<UserType> implements VirtualQueue<UserType> {
	
	private PriorityBlockingQueue<UserType> queue = null;
	public static final long MAXIMUM_DEQUEUE_WAIT_NANOS = 100;
	public static final int MAXIMUM_QUEUE_ENTRIES = Integer.MAX_VALUE;
	private Clock currentTimeClock;	
	private QueueMetrics metrics;
	
	@Builder
	public InMemoryVirtualQueue(int maximumQueueEntries, Comparator<UserType> queuePositionStrategy, Clock timeClock, QueueMetrics metrics) {
		queue = new PriorityBlockingQueue<>(maximumQueueEntries, queuePositionStrategy);
		this.currentTimeClock = timeClock;
		this.metrics = metrics;
	}
	
	/**
	 * Dequeues a batch of users, up to the specified target number of users, into the specified collection.  If the number of
	 * dequeued entries is less than the target number, the actual number dequeued will be returned immediately.
	 * 
	 * @param users collection holding the number of users dequeued.
	 * @param targetBatchSize maximum number of users to dequeue
	 * @throws InterruptedException
	 */
	@Override
	public void nonBlockingBatchDequeueTo(Collection<UserType> users, int targetBatchSize) {
		try { 
			batchDequeueTo(users, targetBatchSize, Optional.of(Duration.of(0, ChronoUnit.NANOS)));
		} catch(Exception e) { 
			
		}
	}
	
	/**
	 * Dequeues a batch of users into the specified collection; if the number of dequeued entries is less than
	 * the batch size, continues to fill for up to the specified maximum duration of nanoseconds.  If no max duration
	 * is specified, uses a default value of {@value #MAXIMUM_DEQUEUE_WAIT_NANOS} 
	 * 
	 * @param users collection holding the number of users dequeued.
	 * @param targetBatchSize maximum number of users to dequeue
	 * @param maxDuration an optional amount of time to wait for the targetBatchSize to be reached.
	 */
	public void batchDequeueTo(Collection<UserType> users, int targetBatchSize, Optional<Duration> maxDuration) throws InterruptedException{
		int numberDequeued = 0;
		long fillByTime;
		long now = currentTimeClock.instant().getNano();
		
		if(maxDuration.isPresent()) { 
			fillByTime = now + maxDuration.get().toNanos();
		} else { 
			fillByTime = now + MAXIMUM_DEQUEUE_WAIT_NANOS;
		}
		
		while(numberDequeued < targetBatchSize) {
			numberDequeued += queue.drainTo(users, targetBatchSize - numberDequeued);			

			//batch size returned by drain was less than expected 
			if(numberDequeued < targetBatchSize) {
				
				//optionally wait for more entries to arrive into the queue if the batch size hasn't been satisfied
				if(fillByTime < currentTimeClock.instant().getNano()) { 
					break;
				}
				
				//reentrant lock blocks until offer() triggers condition to return another entry or times out.
				UserType user = queue.poll(fillByTime - currentTimeClock.instant().getNano(), TimeUnit.NANOSECONDS);
				
				//timed out while waiting for more buffer to fill
				if(user == null) { 
					break;
				}
			}
		}
		
		metrics.decrementDepth(numberDequeued);	
		return;		
	}

	@Override
	public boolean enqueueUser(UserType user) {	
		return queue.offer(user);
	}

	@Override
	public boolean dequeueUser(UserType user) {
		return true;
		//return queue.removeIf( 
		//		(QueueUser) entry -> (QueueUser) entry.getUserId().equals( ((QueueUser)user).getUserId()))
	}

	@Override
	public int queueThrottleRps(int rps) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public QueueInfo queueInfo(UserType user) {
		// TODO Auto-generated method stub
		return null;
	}
}
