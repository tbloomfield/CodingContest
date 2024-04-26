package org.tbloomfield.codingcontest.container.java.executor.test;

import org.tbloomfield.codingcontest.container.bo.TestResult;

import lombok.Getter;

/**
 * Failure thrown when a test case assertion returns false.
 */
public class TestCaseFailure extends RuntimeException {
    private static final long serialVersionUID = 1L;
    @Getter private TestResult failedTest;
    
    public TestCaseFailure(String errorMessage, TestResult result) {
        super(errorMessage);
        this.failedTest = result;
    }
}