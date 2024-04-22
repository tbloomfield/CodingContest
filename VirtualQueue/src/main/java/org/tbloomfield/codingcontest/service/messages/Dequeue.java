package org.tbloomfield.codingcontest.service.messages;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Dequeue {
	private long dequeueTimestamp;	
	private String jwtToken;
}
