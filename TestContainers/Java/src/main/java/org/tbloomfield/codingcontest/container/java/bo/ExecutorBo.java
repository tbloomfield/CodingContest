package org.tbloomfield.codingcontest.container.java.bo;

import java.io.IOException;

public interface ExecutorBo {
    ExecutionResult executeFileBasedTest(CodeEntry entry);
    ExecutionResult executeArgumentBasedTest(CodeEntry entry);
}
