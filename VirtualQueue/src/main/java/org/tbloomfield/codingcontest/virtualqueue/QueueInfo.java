package org.tbloomfield.codingcontest.virtualqueue;

import lombok.Data;

@Data
public class QueueInfo {
	/**
	 * Time estimate until this user is dequeue in MS.
	 * @return
	 */
	long timeRemainingEstimateMS;
	
	/**
	 * Time that a user has been enqueued.
	 *  
	 * @return elapsed time in queue.
	 */
	long elapsedTime;

	/**
	 * This users current position in the queue.
	 */
	int currentPosition;
	
	
	/**
	 * Current total depth of this queue
	 */
	int totalDepth;
	
}
