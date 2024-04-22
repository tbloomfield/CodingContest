package org.tbloomfield.codingcontest.virtualqueue.metrics;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import lombok.Builder;
import lombok.Getter;

/**
 * Snapshots queue metrics in-memory using a bounded stack and a defined sample rate. 
 *  
 */
public class InMemoryQueueMetrics implements QueueMetrics {
	private AtomicLong queueDepth;	
	
	//sampling thread to calculate metrics for this queue.
	private Timer timer;
	private Clock clock;
	private Duration SAMPLE_RATE_MS = Duration.of(5, ChronoUnit.SECONDS);	
	private AtomicLong sampleQueue;
	private AtomicLong sampleDequeue;
	@Getter private Deque<TimeMetric<Double>> entryTimeWindowQueue;
	//keep entries for 5 minutes; entries are added at a rate of (SAMPLE_RATE_MS)
	private int entryTimeMaxSize = Math.round( (5 * 60) / SAMPLE_RATE_MS.getSeconds() );
	
	private TimerTask sampleTask = new TimerTask() { 
		public void run() {
			calculateMetrics();			
		}
	};
	
	@Builder
	public InMemoryQueueMetrics(String timerName, Clock clock) { 
		sampleQueue = new AtomicLong(0);
		sampleDequeue = new AtomicLong(0);
		queueDepth = new AtomicLong(0);
		entryTimeWindowQueue = new ArrayDeque<>();
		timer = new Timer(timerName);		
		this.clock = clock;
	}
	
	public void startGathering() {
		timer.schedule(sampleTask, SAMPLE_RATE_MS.toMillis() );
	}
	
	public void decrementDepth(int amount) {
		queueDepth.addAndGet(-amount);
		
		//track the rate over time for empty operations
		sampleDequeue.addAndGet(amount);
	}
	
	public void incrementDepth(int amount) { 
		queueDepth.addAndGet(amount);
		sampleQueue.addAndGet(amount);	
	}
	
	//@VisibleForTesting
	void calculateMetrics() { 
		//snapshot the number of queue/dequeue within this sample window, resetting values
		//for the next run
		long numberQueue = sampleQueue.getAndSet(0);
		long numberDequeue = sampleDequeue.getAndSet(0);
		
		//rate of entries being dequeued per second
		double ratePerSecond = (double) numberDequeue / SAMPLE_RATE_MS.getSeconds();
		
		//rate at which the queue is currently falling behind.  A zero value indicates
		//the queue is processing as quickly as entries arrive.
		double lagRatePerSecond = Math.max(0, (numberQueue - numberDequeue) / SAMPLE_RATE_MS.getSeconds());
		
		//a linear estimation of how long each entry will sit in queue before being dequeued.
		double perEntryTimeEstimate = queueDepth.get() / (ratePerSecond - lagRatePerSecond);
		
		//if queue is falling behind, assume that each entry will take at least the SAMPLE window to process.
		if(perEntryTimeEstimate < 0 ) {
			perEntryTimeEstimate = queueDepth.get() * SAMPLE_RATE_MS.getSeconds();
		}		
		
		//smooth entry time estimates by storing a rolling window which we can then use to average.
		//limit to 7 digits of precision for easy display
		TimeMetric<Double> metric = new TimeMetric(clock.systemUTC().millis(), 
				Math.round(perEntryTimeEstimate * 1000000) / 1000000.0 );
		
		//bound window size
		while(entryTimeWindowQueue.size() > entryTimeMaxSize) {
			entryTimeWindowQueue.pop();
		} 
		entryTimeWindowQueue.offer(metric);
	}
}
