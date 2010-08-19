package finder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import junit.framework.Assert;
import org.junit.Test;

//
// End to end test of the FileSearcher
// Tests finding Strings in test_files/test_file1.txt
//

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
            builder.append("fileName:   ").append(fileName).append('\n')
                   .append("charset:    ").append(charsetName).append('\n')
                   .append("target:     ").append(target).append('\n')
                   .append("match case: ").append(matchCase).append('\n')
                   .append("expected result: ").append(expectedResult.toString()).append('\n');
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
    
    // convenience method
    private TestCase createTestCase(FindEntry[] expectedFinds,String target,boolean matchCase) {
        return new TestCase("test_files/test_file_1.txt","ASCII",target,matchCase, new SearchResult(Arrays.asList(expectedFinds)));
    }
    
    private List<TestCase> getTestCases() {
        List<TestCase> testCases = new LinkedList<TestCase>();
        
        // find "It"
        testCases.add( createTestCase(new FindEntry[] {new FindEntry(52,2,0)},"It",true ) );
        
        // find "This"
        testCases.add( createTestCase(new FindEntry[] {new FindEntry(0,0,0)},"This",true ) );
        
        // find "FileSearcher"
        testCases.add( createTestCase(new FindEntry[] {new FindEntry(83,2,31)},"filesearcher",false ) );
        
        // TODO: add more test cases
        
        return testCases;
    }
    
    private TestResult doTestCase(TestCase test) {
        FileSearcherFactory factory = FileSearcherFactory.getInstance();
        FileSearcher searcher = factory.getFileSearcher();
        try {
            SearchResult result = searcher.searchFile(test.fileName,test.charsetName,test.target,test.matchCase);
            if( !result.equals(test.expectedResult) ) {
                StringBuilder builder = new StringBuilder("SearchResult not as expected").append('\n');
                builder.append("expected: ").append('\n')
                       .append(test.expectedResult.toString()).append('\n')
                       .append("instead got: ").append('\n')
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
