package org.tbloomfield.codingcontest.virtualqueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.tbloomfield.codingcontest.virtualqueue.metrics.NoOpMetrics;
import org.tbloomfield.codingcontest.virtualqueue.position.WeightedQueuePositionStrategy;
import org.tbloomfield.codingcontest.virtualqueue.usertypes.QueueUser;
import org.tbloomfield.codingcontest.virtualqueue.usertypes.WeightedUser;
import org.tbloomfield.codingcontest.virtualqueue.position.FIFOQueuePositionStrategy;


public class InMemoryVirtualQueueIT {
	
	private InMemoryVirtualQueue<QueueUser> fifoQueue;
	private InMemoryVirtualQueue<WeightedUser> weightedQueue;
	private QueueTestHelper helper;
	
	
	@BeforeEach
	public void initTest() {		
		helper = new QueueTestHelper();
		helper.init();
		MockitoAnnotations.initMocks(this);
   }

	@Test
	public void testPriorityWeighting() throws InterruptedException {
		initializeWeightedQueue();		
		
		WeightedUser lowUser = WeightedUser.builder().weight(10).userId("123").creationTime(helper.clockMillis()).build();
		weightedQueue.enqueueUser(lowUser);		

		//simulate a 5 second offset
		helper.advanceTimeByMillis(5000);
		WeightedUser highUser = WeightedUser.builder().weight(20).userId("234").creationTime(helper.clockMillis()).build();
		weightedQueue.enqueueUser(highUser);
		
		//higher priority user should be first in the queue
		List<WeightedUser> queueResults = new ArrayList<>();
		weightedQueue.nonBlockingBatchDequeueTo(queueResults, 5);
		
		assertEquals(2, queueResults.size());
		assertEquals("234", queueResults.get(0).getUserId());
		assertEquals("123", queueResults.get(1).getUserId());
	}
	
	@Test
	public void testFifoWeighting() throws InterruptedException{
		initializeFifoQueue();
		
		QueueUser lowUser = QueueUser.builder().userId("123").creationTime(helper.clockMillis()).build();
		fifoQueue.enqueueUser(lowUser);		

		//simulate a 5 second offset
		helper.advanceTimeByMillis(5000);		
		QueueUser highUser = WeightedUser.builder().userId("234").creationTime(helper.clockMillis()).build();
		fifoQueue.enqueueUser(highUser);
		
		//higher priority user should be first in the queue
		List<QueueUser> queueResults = new ArrayList<>();
		fifoQueue.nonBlockingBatchDequeueTo(queueResults, 5);
		
		assertEquals(2, queueResults.size());
		assertEquals("123", queueResults.get(0).getUserId());
		assertEquals("234", queueResults.get(1).getUserId());		
	}
	
	private void initializeWeightedQueue() {
		//no-op metrics		
		weightedQueue = InMemoryVirtualQueue.<WeightedUser>builder()
				.maximumQueueEntries(5)
				.timeClock(helper.getClock())
				.metrics(new NoOpMetrics())
				.queuePositionStrategy(new WeightedQueuePositionStrategy())
				.build();
	}
	
	private void initializeFifoQueue() {
		//no-op metrics		
		fifoQueue = InMemoryVirtualQueue.<QueueUser>builder()
				.maximumQueueEntries(5)
				.timeClock(helper.getClock())
				.metrics(new NoOpMetrics())
				.queuePositionStrategy(new FIFOQueuePositionStrategy())
				.build();
	}
}
