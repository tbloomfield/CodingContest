package org.tbloomfield.codingcontest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.tbloomfield.codingcontest.virtualqueue.consumer.ScheduledQueueConsumer;
import org.tbloomfield.codingcontest.virtualqueue.metrics.QueueMetrics;

@Component
public class ApplicationLifecycle implements ApplicationListener<ApplicationReadyEvent> {
	
    @Autowired
    private ApplicationContext applicationContext;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		QueueMetrics metrics = applicationContext.getBean(QueueMetrics.class);
    	ScheduledQueueConsumer queueConsumer = applicationContext.getBean(ScheduledQueueConsumer.class);    	
        metrics.startGathering();
        queueConsumer.startConsumption();		
	}
}
