package org.tbloomfield.codingcontest.container.java;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.tbloomfield.codingcontest.container.java.Main;
import org.tbloomfield.codingcontest.container.java.executor.LocalFileHelper;
import org.tbloomfield.codingcontest.container.java.service.dto.CodeEntryDto;
import org.tbloomfield.codingcontest.container.java.service.dto.ExecutionResultDto;
import org.tbloomfield.codingcontest.container.java.service.dto.TestCaseDto;

import com.google.gson.Gson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


import java.io.IOException;
import java.util.List;

/**
 * Executes REST calls to endpoint, 
 */
@SpringBootTest()
@AutoConfigureMockMvc
@ContextConfiguration(classes = Main.class)
public class RunnerControllerTest {
	@Autowired private MockMvc mvc;
	private final Gson gson = new Gson();
	
	@Test
	void executeCode_HappyPath() throws Exception {
		CodeEntryDto code = testZeroArgCodeEntry();
		String jsonCode = gson.toJson(code);		
		ResultActions response = mvc.perform(
				post("/codeRunner/execute")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonCode)
				.accept(MediaType.APPLICATION_JSON));		
		response.andDo(print()).andExpect(status().isOk());
		
    String body = response.andReturn().getResponse().getContentAsString();
    ExecutionResultDto result = gson.fromJson(body, ExecutionResultDto.class);
    assertNull(result.getErrors());
    assertNotNull(result.getPerformanceInfo());
    assertTrue(result.getPerformanceInfo().getElapsedTime() > 0);
    assertEquals("Your name is User123", result.getTestResults().get(0).getResult());
	}
	
	@Test
	void executeCode_CompileExceptionPath() throws Exception {
		CodeEntryDto code = invalidJavaCodeEntry();
		String jsonCode = gson.toJson(code);
		
		ResultActions response = mvc.perform(
				post("/codeRunner/execute")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonCode)
				.accept(MediaType.APPLICATION_JSON));		
		response.andDo(print()).andExpect(status().isOk());
		
	    String body = response.andReturn().getResponse().getContentAsString();
	    ExecutionResultDto result = gson.fromJson(body, ExecutionResultDto.class);
	    assertNotNull(result.getErrors());
	    assertTrue(result.getErrors().startsWith("PrintName_Invalid.java:4: error: cannot find symbol"));
	}
	
	@Test
	void executeCode_UnknownMethod() throws Exception {
		CodeEntryDto code = testZeroArgCodeEntry();
		code.setMethodNameToTest("nonExistantMethod");
		String jsonCode = gson.toJson(code);
		
		ResultActions response = mvc.perform(
				post("/codeRunner/execute")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonCode)
				.accept(MediaType.APPLICATION_JSON));		
		response.andDo(print()).andExpect(status().isOk());
		
	    String body = response.andReturn().getResponse().getContentAsString();
	    ExecutionResultDto result = gson.fromJson(body, ExecutionResultDto.class);
	    assertNotNull(result.getErrors());
	    assertTrue(result.getErrors().startsWith("java.lang.IllegalArgumentException: no execution method found"));
	}
	
	private CodeEntryDto testZeroArgCodeEntry() throws IOException {		
		//build sample tests to run
		TestCaseDto dto = TestCaseDto.builder()
				.testCaseId("123")
				.arguments(new String[] {"User123"} )
				.expectedResult("Your name is User123")
				.build();		
		
		CodeEntryDto entry = CodeEntryDto.builder()
				.argTypes(new String[] {"java.lang.String"})
				.className("PrintName")
				.codeToExecute(TestHelper.findAndReturnSubmissionContents("PrintName", LocalFileHelper.JAVA_EXTENSION))
				.methodNameToTest("printMyName")
				.testCases(List.of(dto))
				.build();
		
		return entry;
	}
	
	private CodeEntryDto invalidJavaCodeEntry() throws IOException {		
		//build sample tests to run
		TestCaseDto dto = TestCaseDto.builder()
				.testCaseId("123")
				.arguments(new String[] {"User123"} )
				.build();		
		
		CodeEntryDto entry = CodeEntryDto.builder()
				.argTypes(new String[] {"java.lang.String"})
				.className("PrintName_Invalid")
				.codeToExecute(TestHelper.findAndReturnSubmissionContents("PrintName_Invalid", LocalFileHelper.JAVA_EXTENSION))
				.methodNameToTest("printMyName")
				.testCases(List.of(dto))
				.build();
		
		return entry;
	}
}
