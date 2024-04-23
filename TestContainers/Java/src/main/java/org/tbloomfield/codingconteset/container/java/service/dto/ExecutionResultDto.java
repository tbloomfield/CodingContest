package org.tbloomfield.codingconteset.container.java.service.dto;

import java.util.List;

import org.springframework.lang.Nullable;
import org.tbloomfield.codingconteset.container.java.server.metrics.JVMMetricDelta;
import org.tbloomfield.codingconteset.container.java.service.TestResult;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExecutionResultDto {
	private JVMMetricDelta performanceInfo;
	private List<TestResult> testResults;
	@Nullable private String errors;
}
