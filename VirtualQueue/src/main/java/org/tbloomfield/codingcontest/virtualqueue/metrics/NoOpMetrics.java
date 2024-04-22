package org.tbloomfield.codingcontest.virtualqueue.metrics;

public class NoOpMetrics implements QueueMetrics {

	@Override
	public void decrementDepth(int amount) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void incrementDepth(int amount) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public void startGathering() {
		throw new UnsupportedOperationException();
	}
}
