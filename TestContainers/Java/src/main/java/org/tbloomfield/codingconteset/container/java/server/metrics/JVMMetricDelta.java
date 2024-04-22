package org.tbloomfield.codingconteset.container.java.server.metrics;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JVMMetricDelta {
	private JVMMetrics start;
	private JVMMetrics end;
	
	private float cpuUsage;
	private long elapsedCPU;
	private long elapsedTime;
	private long totalMemory;
}