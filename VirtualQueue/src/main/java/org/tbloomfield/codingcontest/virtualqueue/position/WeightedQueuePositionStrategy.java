package org.tbloomfield.codingcontest.virtualqueue.position;

import java.util.Comparator;

import org.tbloomfield.codingcontest.virtualqueue.usertypes.WeightedUser;

/**
 * Strategy to position users within a queue based on entry time and user weight.  Comparator calculation uses a weighted ratio of:
 * 70% time inserted into the queue
 * 30% user weighting.
 * 
 * This class allows for re-shuffling of existing entries via future business logic which would increase time weighting 
 * the longer the user has sat in queue.
 */
public class WeightedQueuePositionStrategy implements Comparator<WeightedUser> {
	
	private final double timeWeighting = .7;
	private final double userWeighting = .3;

	@Override
	public int compare(WeightedUser user1, WeightedUser user2) {
		//first compare by time
		int timediff = user1.getCreationTime() > user2.getCreationTime() ? 1 : -1;
				
		//then compare by weighting
		int weightDiff = user1.getWeight() > user2.getWeight() ? -1 : 1;
		
		double overallWeight = (timediff * timeWeighting) + (weightDiff * userWeighting);
		int comparison = (int)Math.round(overallWeight) == 0 ? -1 : 1;
		
		return comparison;
	}
}
