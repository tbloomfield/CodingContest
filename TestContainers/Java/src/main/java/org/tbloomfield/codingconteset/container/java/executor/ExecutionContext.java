package org.tbloomfield.codingconteset.container.java.executor;

import java.io.File;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Holder for information about the class, method, tests and types of data accepted for the Java class under test.
 */
@Data
@Builder
public class ExecutionContext {
	private String className;
	private String entryMethodName;
	private List<Class> methodParameters;
	private File filePath;
	private int ttlInSeconds;
	private List<TestCase<?>> testCases;
}