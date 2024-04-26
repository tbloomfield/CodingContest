package org.tbloomfield.codingcontest.container.java.bo;

import java.util.List;

import org.tbloomfield.codingcontest.container.bo.CodeEntry;

public interface ExecutorBo {
    ExecutionResult executeFileBasedTest(CodeEntry entry, List<String> testFiles);
    ExecutionResult executeArgumentBasedTest(CodeEntry entry);
}
