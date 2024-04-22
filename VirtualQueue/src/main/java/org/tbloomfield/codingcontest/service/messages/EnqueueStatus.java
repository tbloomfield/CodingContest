package org.tbloomfield.codingcontest.service.messages;

import org.tbloomfield.codingcontest.virtualqueue.QueueInfo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class EnqueueStatus {
	private QueueInfo queueInfo;
}
