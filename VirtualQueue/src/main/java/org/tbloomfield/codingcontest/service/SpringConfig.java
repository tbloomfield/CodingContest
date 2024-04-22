package org.tbloomfield.codingcontest.service;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tbloomfield.codingcontest.virtualqueue.InMemoryVirtualQueue;
import org.tbloomfield.codingcontest.virtualqueue.VirtualQueue;
import org.tbloomfield.codingcontest.virtualqueue.consumer.ScheduledQueueConsumer;
import org.tbloomfield.codingcontest.virtualqueue.metrics.InMemoryQueueMetrics;
import org.tbloomfield.codingcontest.virtualqueue.metrics.QueueMetrics;
import org.tbloomfield.codingcontest.virtualqueue.position.FIFOQueuePositionStrategy;
import org.tbloomfield.codingcontest.virtualqueue.usertypes.QueueUser;

/**
 * Encapsulated spring IOC configuration to separate spring-annotations from core business pojos
 */
@Configuration
public class SpringConfig {
	
    @Bean
    public VirtualQueue<QueueUser> inMemoryVirtualQueue(QueueMetrics metrics, Clock clock) {
    	return InMemoryVirtualQueue.<QueueUser>builder()
    	.maximumQueueEntries(100)
    	.metrics(metrics)
    	.timeClock(clock)
    	.queuePositionStrategy(new FIFOQueuePositionStrategy())
    	.build();
    }
    
    @Bean
    public Clock clock() { 
    	return Clock.systemUTC();
    }
    
    @Bean
    public QueueMetrics queueMetrics(Clock clock) { 
    	return InMemoryQueueMetrics.builder()
    			.clock(clock)
    			.timerName("spring-queue")
    			.build();    	
    }
    
    @Bean
    public ScheduledQueueConsumer queueConsumer(Clock clock, DequeueHandler handler, VirtualQueue<QueueUser> virtualQueue) { 
    	return ScheduledQueueConsumer.builder()
	    	.currentTime(clock)
	    	.dequeueSleepTimeMillis(1000l)
	    	.handler(handler)
	    	.queue(virtualQueue)
	    	.triggerTime(clock)
	    	.build();
    }
}
