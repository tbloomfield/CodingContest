package org.tbloomfield.codingcontest.virtualqueue.usertypes;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
public class QueueUser {
	private String userId;
	private long creationTime;
}
