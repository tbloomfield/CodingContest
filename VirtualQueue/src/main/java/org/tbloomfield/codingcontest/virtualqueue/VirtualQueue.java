package org.tbloomfield.codingcontest.virtualqueue;

import java.util.Collection;

public interface VirtualQueue<UserType> {
	/**
	 * The algorithm used to decide a users position in queue.  Default is FIFO
	 */
	//void queueAlgorithm(Comparator qps);
	
	/**
	 * Adds a user to the queue.
	 * @param id a unique identifier for this user.
	 * @return true if user enqueued successfully, false otherwise
	 */
	boolean enqueueUser(UserType user);
	
	/**
	 * Dequeues a batch of users, up to the specified target number of users, into the specified collection.  If the number of
	 * dequeued entries is less than the target number, the actual number dequeued will be returned immediately.
	 * 
	 * @param users collection holding the number of users dequeued.
	 * @param targetBatchSize maximum number of users to dequeue
	 */
	void nonBlockingBatchDequeueTo(Collection<UserType> users, int targetBatchSize);

	
	/**
	 * Deques a user from the queue.
	 * @param id a unique identifier for this user.
	 * @return true if user removed successfully, false otherwise
	 */
	boolean dequeueUser(UserType user);
	
	/**
	 * Maximum number of entries in this queue.
	 * 
	 * @param maximumQueueEntries
	 */
	//void maximumQueueEntries(int maximumQueueEntries);
	
	int queueThrottleRps(int rps);
	
	/**
	 * Returns queue information specific to the enqueued user
	 * @param id
	 * @return
	 */
	QueueInfo queueInfo(UserType id);	
	
}
