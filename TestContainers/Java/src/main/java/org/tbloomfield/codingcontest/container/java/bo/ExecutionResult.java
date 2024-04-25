package org.tbloomfield.codingcontest.container.java.bo;

import java.util.List;

import org.tbloomfield.codingcontest.container.java.server.metrics.JVMMetricDelta;
import org.tbloomfield.codingcontest.container.java.service.TestResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResult {
	private JVMMetricDelta performanceInfo;
	private List<TestResult> testResults;
	private String errors;
}
