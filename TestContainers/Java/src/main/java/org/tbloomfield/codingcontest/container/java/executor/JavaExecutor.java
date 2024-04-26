package org.tbloomfield.codingcontest.container.java.executor;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.tbloomfield.codingcontest.container.bo.TestCase;
import org.tbloomfield.codingcontest.container.bo.TestResult;
import org.tbloomfield.codingcontest.container.java.executor.test.TestCaseFailure;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaExecutor {
	/**
	 * Compiles the specified resource, placing the results into the OS temporary
	 * directory.
	 * 
	 * @throws URISyntaxException if file to compile can't be loaded
	 * @throws IOException        if compile process doesn't succeed.
	 */
	public CompileResult compile(URI resource) throws IOException, URISyntaxException {
		// write text to the specified filename
		File javaFile = new File(resource);
		ProcessBuilder pb = new ProcessBuilder("javac ", "-classpath", javaFile.getParent(), "-d", javaFile.getParent(), javaFile.getPath());
		pb.redirectErrorStream(true);
		pb.inheritIO();
		pb.redirectError(Redirect.PIPE);
		pb.redirectInput(Redirect.PIPE);
		pb.redirectOutput(Redirect.PIPE);
		
		Process process = pb.start();
		try {
			//block until process returns, or throw exception to prevent endless block.
			//TODO - make this configurable
			process.waitFor(3, TimeUnit.SECONDS);
			process.onExit().get();
		} catch (InterruptedException | ExecutionException e) {
			log.error(e.getMessage(), e);
		}
		
		int procVal = process.exitValue();
		String result = new String(process.getInputStream().readAllBytes());
		return CompileResult.builder()
				.statusCode(procVal)
				.compilationOutput(result)
				.build();
	}

	/**
	 * Invokes test method from a class loaded into memory, passing input arguments using reflection. 
	 * 
	 * @param context
	 * @return 
	 */
	@SuppressWarnings("unchecked")
	public List<TestResult> executeCode(ExecutionContext context) {
		List<TestResult> testResults = null;

		try {
	    Object testObject = createTestObject(context.getFile());
	    Method entryMethod = fetchTestObjectMethod(testObject, context.getEntryMethodName(), context.getMethodParameters());

			// execute method in a thread with a fixed duration of time
			ExecutorService executor = Executors.newSingleThreadExecutor();
			
			Future<List<TestResult>> runnerFuture = null;
			
			//for in-memory testcases
			if(context.getTestCases() != null && context.getTestCases().isPresent()) { 
			  runnerFuture = executeMethodWithVariableParams(context.getTestCases().get(), executor, entryMethod, testObject);
			} else { 
        runnerFuture = executeMethodWithFileParams(executor, entryMethod, testObject);
			}

			try {
				testResults = runnerFuture.get(context.getTtlInSeconds(), TimeUnit.SECONDS);
			} catch (TimeoutException t) {
			    runnerFuture.cancel(true);
			} finally {
				executor.shutdownNow();
			}
		} catch (ExecutionException | InterruptedException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | MalformedURLException | ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		return testResults;
	}
	
	private Future<List<TestResult>> executeMethodWithVariableParams(List<TestCase> testCases, ExecutorService executor, Method entryMethod, Object testObject) {
	    Future<List<TestResult>> future = executor.submit(() -> {
        List<TestResult> executionResults = new ArrayList<>(testCases.size());
        for (TestCase t : testCases) {
          Object result;
          if(CollectionUtils.isEmpty(t.getArguments())) { 
            result = ReflectionUtils.invokeMethod(entryMethod, testObject);
          } else {
            Object[] args = t.getArguments().toArray();  
            result = ReflectionUtils.invokeMethod(entryMethod, testObject, args);
          }
          
          executionResults.add(
                  TestResult.builder()                    
	                  .passing(Objects.deepEquals(t.getExpectedResult(), result))
	                  .testCaseId(t.getTestCaseId())
	                  .expected(t.getExpectedResult())
	                  .result(result)
	                  .build()
                  );
	        }
	        return executionResults;
	     });
	    return future;
	}
	
	private Future<List<TestResult>> executeMethodWithFileParams(ExecutorService executor, Method entryMethod, Object testObject) { 
	  //for file-based test cases
      Future<List<TestResult>> fileBasedFuture = executor.submit(() -> {          
        TestResult testResult= null;
        try { 
            ReflectionUtils.invokeMethod(entryMethod, testObject);
            testResult = TestResult.builder()
                    .passing(true)
                    .build();
        } catch(TestCaseFailure failure) {
            testResult = failure.getFailedTest();
        }
        
        return List.of(testResult);
      });      
      return fileBasedFuture;
	}
	
	/**
   * Dynamically create an object from the classpath.
   * 
   * @param testFile File to load into memory. 
   * @return Object created or IllegalArgumentException if the class couldn't be loaded or doesn't have a default constructor.
   */
  private Object createTestObject(File testFile) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {      
    //Classloader should start at the parent directory of the file to test.
    URL url = testFile.getParentFile().toURI().toURL();
    URL[] urls = new URL[] { url };
    ClassLoader cl = new URLClassLoader(urls);
    Class testClass = cl.loadClass(LocalFileHelper.getNameWithoutExtension(testFile));

    Constructor constructor = ReflectionUtils.accessibleConstructor(testClass);
    if (constructor == null) {
      throw new IllegalArgumentException("no constructor found");
    }      

    return constructor.newInstance();
  }
  
  /**
   * Introspect a target object to find a method matching the specified name and parameters.
   * 
   * @param testObject object to introspect.
   * @param entryMethodName name of method to find
   * @param methodParameters expected method signature 
   * @return Method found or IllegalArgumentException if none could be returned.
   */
  private Method fetchTestObjectMethod(Object testObject, String entryMethodName, Optional<List<Class>> methodParameters) {

    Method entryMethod;     
    if(methodParameters == null || methodParameters.isEmpty()) { 
      entryMethod = ReflectionUtils.findMethod(testObject.getClass(), entryMethodName);
    } else { 
      Class[] argTypes = methodParameters.get().toArray(new Class[methodParameters.get().size()]);
      entryMethod = ReflectionUtils.findMethod(testObject.getClass(), entryMethodName, argTypes);
    }
    if (entryMethod == null) {
      throw new IllegalArgumentException("no execution method found");
    }
    
    return entryMethod;
  }
}