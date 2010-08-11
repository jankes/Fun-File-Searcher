package finder;

import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

public class FileSearcherTest {
    
    // container class for the parameters to pass to the FileSearcher, and expected result of the search
    private class TestCase {
        public final String fileName;
        public final String charsetName;
        public final String target;
        public final boolean matchCase;
        public final SearchResult expectedResult;
        
        public TestCase(String fileName,String charsetName,String target,boolean matchCase,SearchResult expectedResult) {
            this.fileName = fileName;
            this.charsetName = charsetName;
            this.target = target;
            this.matchCase = matchCase;
            this.expectedResult = expectedResult;
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("fileName:   ").append(fileName).append(Character.LINE_SEPARATOR)
                   .append("charset:    ").append(charsetName).append(Character.LINE_SEPARATOR)
                   .append("target:     ").append(target).append(Character.LINE_SEPARATOR)
                   .append("match case: ").append(matchCase).append(Character.LINE_SEPARATOR)
                   .append("expected result: ").append(expectedResult.toString()).append(Character.LINE_SEPARATOR);
            return builder.toString();
        }
    }
    
    // container to wrap up the results of running a test
    // exception will be null unless the code under test throws an exception itself (ie FileSearcherFailException)
    private class TestResult {
        public final boolean success;
        public final String message;
        public final Exception exception;
        
        public TestResult(boolean success,String message,Exception exception) {
            this.success = success;
            this.message = message;
            this.exception = exception;
        }
    }
    
    private List<TestCase> getTestCases() {        
        List<TestCase> testCases = new LinkedList<TestCase>();
        
        // find the word "Planning" in test_file_1.txt
        List<FindEntry> expectedFinds = new LinkedList<FindEntry>();
        expectedFinds.add(new FindEntry(0,0,0));
        SearchResult expectedResult = new SearchResult(expectedFinds);
        TestCase case1 = new TestCase("test_files/test_file_1.txt","ASCII","Planning",true,expectedResult);        
        testCases.add(case1);
        
        return testCases;
    }
    
    private TestResult doTestCase(TestCase test) {
        FileSearcherFactory factory = FileSearcherFactory.getInstance();
        FileSearcher searcher = factory.getFileSearcher();
        try {
            SearchResult result = searcher.searchFile(test.fileName,test.charsetName,test.target,test.matchCase);
            if( !result.equals(test.expectedResult) ) {
                StringBuilder builder = new StringBuilder("SearchResult not as expected").append(Character.LINE_SEPARATOR);
                builder.append("expected: ").append(Character.LINE_SEPARATOR)
                       .append(test.expectedResult.toString()).append(Character.LINE_SEPARATOR)
                       .append("instead got: ").append(Character.LINE_SEPARATOR)
                       .append(result.toString());
                return new TestResult(false,builder.toString(),null);
            }
        }
        catch(FileSearcherFailException e) {
            return new TestResult(false,"FileSearcher failed!",e);
        }
        return new TestResult(true,"success",null);
    }
    
    @Test
    public void testSearch() {
        List<TestCase> testCases = getTestCases();
        for(TestCase test : testCases) {
            TestResult result = doTestCase(test);
            if( !result.success ) {
                System.err.println(result.message);
                Assert.fail();
            }
        }
    }
    
}
