package org.tbloomfield.codingcontest.container.java.server.metrics;

import lombok.Builder;
import lombok.Data;

/**
 * Class which captures various JVM metrics during 
 */
@Data
@Builder
public class JVMMetrics {
	private int availableProcessors;
	private long uptime;
	private long cpuTime;
	private long freeMemorySize;
}
