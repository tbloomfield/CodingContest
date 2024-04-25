package org.tbloomfield.codingcontest.container.java.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tbloomfield.codingcontest.container.java.bo.ExecutorBo;
import org.tbloomfield.codingcontest.container.java.service.dto.CodeEntryDto;
import org.tbloomfield.codingcontest.container.java.service.dto.ExecutionResultDto;


import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/codeRunner")
public class ContainerController {
    
  @Autowired ExecutorBo logic;
	
	@PostMapping("/execute")
	public ExecutionResultDto executeTest(@RequestBody CodeEntryDto entry) throws ClassNotFoundException {
	  ExecutionResultDto result = new ExecutionResultDto();

		try {
	    if(entry.getTestCases() != null && entry.getTestCases().size() > 0) { 
        result = DtoHelper.fromExecutionResult(logic.executeArgumentBasedTest(DtoHelper.toCodeEntry(entry)));
	    } else { 
        result = DtoHelper.fromExecutionResult(logic.executeFileBasedTest(DtoHelper.toCodeEntry(entry)));
	    }
		} catch (RuntimeException e) {
		  result.setErrors(e.getMessage());	
		}		
		return result;
	}
}
