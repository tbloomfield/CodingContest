package org.tbloomfield.codingcontest.service;

import java.time.Clock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Component;
import org.tbloomfield.codingcontest.service.messages.Dequeue;
import org.tbloomfield.codingcontest.virtualqueue.usertypes.QueueUser;

import lombok.extern.slf4j.Slf4j;

/**
 * Business logic to notify a user when dequeued.
 */
@Component
@Slf4j
public class DequeueHandler {	
	@Autowired private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired private Clock clock;
	
	@MessageMapping()
	@SendToUser("/queue")
	public void handleDequeue(QueueUser user) {
		log.info("messaging user" + user);
		String queuePassedToken = DequeueJWTToken.generateToken(user.getUserId());
		Dequeue dequeueMessage = new Dequeue(clock.instant().toEpochMilli(), queuePassedToken);
		
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		headerAccessor.setSessionId(user.getUserId());
		headerAccessor.setLeaveMutable(true);
        simpMessagingTemplate.convertAndSendToUser(user.getUserId(), "/queue/", dequeueMessage, headerAccessor.getMessageHeaders());
	}
}
