package org.tbloomfield.codingcontest.container.java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tbloomfield.codingcontest.container.dto.CodeEntryWithTestDto;
import org.tbloomfield.codingcontest.container.dto.CodeEntryWithTestFileDto;
import org.tbloomfield.codingcontest.container.dto.DtoHelper;
import org.tbloomfield.codingcontest.container.java.bo.ExecutorBo;
import org.tbloomfield.codingcontest.container.java.service.dto.ExecutionResultDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/codeRunner")
public class ContainerController {
    
  @Autowired ExecutorBo logic;
	
	@PostMapping("/execute/testCase")
	public ExecutionResultDto executeTest(@RequestBody CodeEntryWithTestDto entry) throws ClassNotFoundException {
	  ExecutionResultDto result = new ExecutionResultDto();

		try {
      result = org.tbloomfield.codingcontest.container.java.service.dto.DtoHelper.fromExecutionResult(
              logic.executeArgumentBasedTest(DtoHelper.toCodeEntry(entry)));	     
		} catch (RuntimeException e) {
		  result.setErrors(e.getMessage());	
		}		
		return result;
	}
	
	@PostMapping("/execute/testFile")
  public ExecutionResultDto executeTest(@RequestBody CodeEntryWithTestFileDto entry) throws ClassNotFoundException {
    ExecutionResultDto result = new ExecutionResultDto();

    try {
      result = org.tbloomfield.codingcontest.container.java.service.dto.DtoHelper.fromExecutionResult(
              logic.executeFileBasedTest(DtoHelper.toCodeEntry(entry), entry.getTestFiles()));     
    } catch (RuntimeException e) {
      result.setErrors(e.getMessage()); 
    }   
    return result;
  }
}
