import org.tbloomfield.codingcontest.container.java.executor.test.FileTestCase;
import org.tbloomfield.codingcontest.container.bo.TestResult;

public class Fibonacci_Tests extends FileTestCase{
    Fibonacci testClass;
    
    public Fibonacci_Tests() { 
        testClass = new Fibonacci();
    }
    
    @Override
    public boolean executeTest() {
        assertResult(1, testClass.findFib(1));
        assertResult(34, testClass.findFib(8));
        assertResult(10946, testClass.findFib(20));
        assertResult(514229, testClass.findFib(28));
        assertResult(-1, testClass.findFib(-20));
        return true;
    }
}