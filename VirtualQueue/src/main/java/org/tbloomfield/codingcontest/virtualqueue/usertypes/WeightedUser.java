package org.tbloomfield.codingcontest.virtualqueue.usertypes;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Represents a user with a priority weighting.
 * 
 * A weighting may be used to represent high priority users, users who reconnect to the queue
 * and we wish to prioritize them before other users.
 */
@SuperBuilder 
@Data
@EqualsAndHashCode(callSuper = true)
public class WeightedUser extends QueueUser {	
	private int weight;
}
