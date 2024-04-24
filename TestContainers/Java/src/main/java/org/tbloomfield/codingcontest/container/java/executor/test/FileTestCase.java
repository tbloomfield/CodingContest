package org.tbloomfield.codingcontest.container.java.executor.test;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.tbloomfield.codingcontest.container.java.service.TestResult;

/**
 * Functionality which all file-based test cases extend; it's expected that each case
 * invokes the {@link #assertResult(Object, Object)} function prior to continuing
 * to the next case.
 */
public abstract class FileTestCase {

    UUID testGroupingId;
    private int assertionId = 0;

    protected TestResult assertResult(Object expected, Object actual) throws TestCaseFailure {
        assertionId++;
        
        TestResult result = TestResult.builder()
                .passing(Objects.deepEquals(expected, actual))
                .result(actual)
                .expected(Optional.of(expected))
                .testCaseId(Integer.toString(assertionId))
                .build();
        
        if(!result.isPassing()) {
            throw new TestCaseFailure("failed test " + assertionId, result);
        }
        return result;        
    }
    
    protected TestResult assertResult(int actual, int expected) { 
        return assertResult(Integer.valueOf(actual), Integer.valueOf(expected));
    }
    
    public abstract boolean executeTest();
}
