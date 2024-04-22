package org.tbloomfield.codingcontest.virtualqueue;

import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Encapsulates helper methods used by multiple tests to reduce DRY setup code.
 */
public class QueueTestHelper {
	@Mock private Clock testClock;
	private long testEpochDate = 1713716461558l;
	
	public void init() {
		MockitoAnnotations.initMocks(this);
		when(testClock.instant()).thenReturn(Instant.ofEpochMilli(testEpochDate));
	}
	
	/**
	 * shorthand to remove DRY code.
	 * @return
	 */
	public long clockMillis() { 
		return testClock.instant().toEpochMilli();
	}
	
	public void advanceTimeByMillis(int millis) {
		when(testClock.instant()).thenReturn(Instant.ofEpochMilli(testEpochDate + millis));
	}
	
	public Clock getClock() { 
		return testClock;
	}
}
