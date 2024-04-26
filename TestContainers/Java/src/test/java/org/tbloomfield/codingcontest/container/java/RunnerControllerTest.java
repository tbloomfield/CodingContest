package org.tbloomfield.codingcontest.container.java;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.tbloomfield.codingcontest.container.dto.CodeEntryWithTestDto;
import org.tbloomfield.codingcontest.container.dto.CodeEntryWithTestFileDto;
import org.tbloomfield.codingcontest.container.java.service.dto.ExecutionResultDto;

import com.google.gson.Gson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
    CodeEntryWithTestDto code = TestCaseEntryBuilder.builder()
      .withTestCase("123", new String[] {"User123"}, "Your name is User123")
      .withExecutionMethod("printMyName", List.of("java.lang.String"))
      .withCodeFromFile("PrintName", "PrintName")
      .testedByCases();

		ResultActions response = http("/codeRunner/execute/testCase", gson.toJson(code));
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
	  CodeEntryWithTestDto code = TestCaseEntryBuilder.builder()
      .withTestCase("123", new String[] {"User123"}, "Your name is User123")
      .withExecutionMethod("printMyName", List.of("java.lang.String"))
      .withCodeFromFile("PrintName_Invalid", "PrintName_Invalid")
      .testedByCases();
	    
	  ResultActions response = http("/codeRunner/execute/testCase", gson.toJson(code));		
		response.andDo(print()).andExpect(status().isOk());
		
    String body = response.andReturn().getResponse().getContentAsString();
    ExecutionResultDto result = gson.fromJson(body, ExecutionResultDto.class);
    assertNotNull(result.getErrors());
    assertTrue(result.getErrors().startsWith("PrintName_Invalid.java:4: error: cannot find symbol"));
	}
	
	@Test
	void executeCode_UnknownMethod() throws Exception {
    CodeEntryWithTestDto code = TestCaseEntryBuilder.builder()
      .withTestCase("123", new String[] {"User123"}, "Your name is User123")
      .withExecutionMethod("unknownMethod", List.of("java.lang.String"))
      .withCodeFromFile("PrintName", "PrintName")
      .testedByCases();
		
    ResultActions response = http("/codeRunner/execute/testCase", gson.toJson(code));
		response.andDo(print()).andExpect(status().isOk());
		
    String body = response.andReturn().getResponse().getContentAsString();
    ExecutionResultDto result = gson.fromJson(body, ExecutionResultDto.class);
    assertNotNull(result.getErrors());
    assertTrue(result.getErrors().startsWith("java.lang.IllegalArgumentException: no execution method found"));
	}
	
	//no test cases - the file on disk "PrintName_Tests" will be executed instead.
	@Test
  void executeCode_fileTests() throws Exception {
	  CodeEntryWithTestFileDto code = TestCaseEntryBuilder.builder()
      .withCodeFromFile("PrintName", "PrintName")
      .withTestCaseFromFile("PrintName")
      .testedByFile();

    ResultActions response = http("/codeRunner/execute/testFile", gson.toJson(code));
    response.andDo(print()).andExpect(status().isOk());
    
    String body = response.andReturn().getResponse().getContentAsString();
    ExecutionResultDto result = gson.fromJson(body, ExecutionResultDto.class);
    assertNull(result.getErrors());
    assertNotNull(result.getPerformanceInfo());
    assertTrue(result.getPerformanceInfo().getElapsedTime() > 0);
  }
	
	private ResultActions http(String endpoint, String content) throws Exception { 
    ResultActions response = mvc.perform(
            post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(content)
            .accept(MediaType.APPLICATION_JSON));
    return response;
	}
}