package org.tbloomfield.codingconteset.container.java.server.metrics;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JVMMetricDelta {
    @JsonIgnore private JVMMetrics start;
    @JsonIgnore private JVMMetrics end;
	
	private float cpuUsage;
	private long elapsedCPUInNs;
	private long elapsedTime;
	private long usedMemoryInBytes;
}