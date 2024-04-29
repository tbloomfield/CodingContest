import org.tbloomfield.codingcontest.container.java.executor.test.FileTestCase;
import org.tbloomfield.codingcontest.container.bo.TestResult;

public class PrintName_Tests extends FileTestCase{
    PrintName testClass;
    
    public PrintName_Tests() { 
        testClass = new PrintName();
    }
    
    @Override
    public boolean executeTest() {
        assertResult("Your name is Bob", testClass.printMyName("Bob"));
        assertResult("Your name is ", testClass.printMyName(""));
        assertResult("Your name is ", testClass.printMyName(null));
        return true;
    }
}