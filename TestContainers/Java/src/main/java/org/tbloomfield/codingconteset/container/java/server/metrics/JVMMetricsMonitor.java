package org.tbloomfield.codingconteset.container.java.server.metrics;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import com.sun.management.OperatingSystemMXBean;

public class JVMMetricsMonitor {
	private OperatingSystemMXBean operatingSystemMXBean = 
	         (com.sun.management.OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
	private RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
	
	public JVMMetrics capture() {
	    int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
	    long uptime = runtimeMXBean.getUptime();
	    long cpuTime = operatingSystemMXBean.getProcessCpuTime();
	    long freeMemorySize = operatingSystemMXBean.getFreeMemorySize();
	    
	    return JVMMetrics.builder()
	    		.availableProcessors(availableProcessors)
	    		.cpuTime(cpuTime)
	    		.uptime(uptime)
	    		.freeMemorySize(freeMemorySize)
	    		.build();
	}

	public JVMMetricDelta endCapture(JVMMetrics previousMetrics) {
		JVMMetrics endMetrics = capture();
		
		long elapsedCpu = endMetrics.getCpuTime() - previousMetrics.getCpuTime();
	    long elapsedTime = endMetrics.getUptime() - previousMetrics.getUptime();
	    long totalMemory = endMetrics.getFreeMemorySize() - previousMetrics.getFreeMemorySize();
	    float cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * endMetrics.getAvailableProcessors()));
	    
	    return JVMMetricDelta.builder()
	    		.cpuUsage(cpuUsage)
	    		.elapsedCPU(elapsedCpu)
	    		.elapsedTime(elapsedTime)
	    		.end(endMetrics)
	    		.start(previousMetrics)
	    		.totalMemory(totalMemory)
	    		.build();
	}
}