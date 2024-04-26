package org.tbloomfield.codingcontest.container.java.executor;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.tbloomfield.codingcontest.container.bo.TestCase;

import lombok.Builder;
import lombok.Data;

/**
 * Holder for information about the class, method, tests and types of data accepted for the Java class under test.
 */
@Data
@Builder
public class ExecutionContext {
  private File file;
	private String entryMethodName;
	private int ttlInSeconds;
	private Optional<List<Class>> methodParameters;
	private Optional<List<TestCase>> testCases;
}