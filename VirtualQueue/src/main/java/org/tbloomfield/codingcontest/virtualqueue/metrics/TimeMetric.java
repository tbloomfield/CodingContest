package org.tbloomfield.codingcontest.virtualqueue.metrics;

import lombok.AllArgsConstructor;

import lombok.Data;

/**
 * Container for a metric coorelating to a specified time.
 * @param <V>
 */
@Data
@AllArgsConstructor
public class TimeMetric<V> {
	private final long timestamp;
	private V value;
}
