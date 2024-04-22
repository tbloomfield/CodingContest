package org.tbloomfield.codingcontest.virtualqueue.position;

import java.util.Comparator;

import org.tbloomfield.codingcontest.virtualqueue.usertypes.QueueUser;

public class FIFOQueuePositionStrategy implements Comparator<QueueUser>{

	@Override
	public int compare(QueueUser user1, QueueUser user2) {
		return Math.round(user1.getCreationTime() - user2.getCreationTime());
	}
}
