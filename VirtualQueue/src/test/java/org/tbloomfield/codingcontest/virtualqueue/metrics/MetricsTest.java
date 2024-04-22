package org.tbloomfield.codingcontest.virtualqueue.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Deque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.tbloomfield.codingcontest.virtualqueue.QueueTestHelper;

public class MetricsTest {
	public InMemoryQueueMetrics metrics;
	private QueueTestHelper helper;

	@BeforeEach
	public void initTest() {
		helper = new QueueTestHelper();
		helper.init();
		MockitoAnnotations.initMocks(this);
		initializeMetrics();
	}

	@Test
	public void testMetric_estimatedWait() {
		//simulate 10 users being added to the queue and dequeued immediately.   
		metrics.incrementDepth(10);
		metrics.decrementDepth(10);
		metrics.calculateMetrics();
		Deque<TimeMetric<Double>> calculatedMetrics = metrics.getEntryTimeWindowQueue();
		
		assertEquals(1, calculatedMetrics.size());
		assertEquals(0.0, calculatedMetrics.peek().getValue());  //immediate dequeue
		
		helper.advanceTimeByMillis(5000);
		metrics.incrementDepth(10);
		metrics.calculateMetrics();

		calculatedMetrics = metrics.getEntryTimeWindowQueue();
		assertEquals(2, calculatedMetrics.size());
		assertEquals(50.0, calculatedMetrics.peekLast().getValue());  //after 5 seconds the last 10 queued are still waiting
		
		helper.advanceTimeByMillis(5000);
		metrics.decrementDepth(3);
		metrics.calculateMetrics();
		calculatedMetrics = metrics.getEntryTimeWindowQueue();
		System.err.println(calculatedMetrics);
		assertEquals(3, calculatedMetrics.size());
		assertEquals(11.666667, calculatedMetrics.peekLast().getValue());  //after 5 seconds the last 10 queued are still waiting
	}
	
	private void initializeMetrics() {
		metrics = InMemoryQueueMetrics.builder()
				.clock(helper.getClock())
				.timerName("unit-test-timer")
				.build();
	}

}
