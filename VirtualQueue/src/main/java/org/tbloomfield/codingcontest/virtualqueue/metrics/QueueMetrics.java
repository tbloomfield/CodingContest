package org.tbloomfield.codingcontest.virtualqueue.metrics;

public interface QueueMetrics {
	void decrementDepth(int amount);	
	void incrementDepth(int amount);
	void startGathering();
}
