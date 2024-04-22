package org.tbloomfield.codingcontest.service;


import java.time.Clock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.tbloomfield.codingcontest.virtualqueue.VirtualQueue;
import org.tbloomfield.codingcontest.virtualqueue.usertypes.QueueUser;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class QueueController {
	
    @Autowired private VirtualQueue virtualQueue;
    @Autowired private Clock clock;
	
	@MessageMapping("/queue/enqueue")
	public void enqueue(@Header("simpSessionId") String sessionId) throws Exception {
		log.info("queueing user " + sessionId);
		QueueUser user = QueueUser.builder().userId(sessionId).creationTime(clock.instant().toEpochMilli()).build();
		virtualQueue.enqueueUser(user);
	}
}
