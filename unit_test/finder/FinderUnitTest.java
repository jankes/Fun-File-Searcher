package finder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import junit.framework.Assert;

import org.junit.Test;


public class FinderUnitTest {
    
    // subroutine of the doTestCase method
    private Queue<NewDataMsg> getNewDataMsgsFromString(String str,String charsetName,int numMsgs) {
        if( numMsgs > str.length() ) {
            throw new RuntimeException("cannot break of a String of length " + str.length() + "into " + numMsgs + "messages");
        }
        Queue<NewDataMsg> msgQ = new LinkedList<NewDataMsg>();
        Charset charset = Charset.forName(charsetName);
        ByteBuffer allBytes = charset.encode(str);
        int bytesPerMsg = str.length() / numMsgs;
        for(int i=0; i<(numMsgs-1); i++) {
            byte[] msgBytes = new byte[bytesPerMsg];
            allBytes.get(msgBytes);
            msgQ.add(new NewDataMsg(ByteBuffer.wrap(msgBytes),false));
        }
        byte[] lastMsgBytes = new byte[allBytes.remaining()];
        allBytes.get(lastMsgBytes);
        msgQ.add(new NewDataMsg(ByteBuffer.wrap(lastMsgBytes),true));
        return msgQ;
    }
    
    //
    private void doTestCase(String searchString,String charsetName,int numParts,String target,boolean matchCase,SearchResult expectedResult) {
        // the NewDataMsgs the finder will receive.
        Queue<NewDataMsg> receiveMsgs = getNewDataMsgsFromString(searchString,charsetName,numParts);
        
        // create the links the Finder will use
        DummyLink<NewDataMsg> linkIn = new DummyLink<NewDataMsg>(receiveMsgs);
        DummyLink<SearchResult> linkNext = new DummyLink<SearchResult>(new LinkedList<SearchResult>());
        DummyLink<RunStatus> linkTop = new DummyLink<RunStatus>(new LinkedList<RunStatus>());
        
        // run the Finder
        Finder underTest = new Finder(linkIn,linkNext,linkTop,charsetName,target,matchCase);
        underTest.run();
        
        // find should receive all the NewDataMsg sent to it via linkIn, not send to its input link
        Assert.assertTrue(linkIn.getSent().size() == 0);
        Assert.assertTrue(linkIn.getReceived().size() == receiveMsgs.size());
        
        // Finder should send exactly one RunStatus to top, not receive from top
        Assert.assertTrue(linkTop.getSent().size() == 1);
        Assert.assertTrue(linkTop.getReceived().size() == 0);
        
        // Finder should send exactly one SearchResult to next, not receive from next
        Assert.assertTrue(linkNext.getSent().size() == 1);
        Assert.assertTrue(linkNext.getReceived().size() == 0);
        
        // check the Finder returned the expected SearchResult
        Assert.assertEquals(expectedResult,linkNext.getSent().remove());
    }
    
    // convenience method
    private SearchResult createSearchResultWithOneEntry(int index,int lineNum,int colNum) {
        FindEntry entry = new FindEntry(index,lineNum,colNum);
        List<FindEntry> entryList = new LinkedList<FindEntry>();
        entryList.add(entry);
        return new SearchResult(entryList);
    }
    
    // convenience method
    private SearchResult createSearchResultWithMultipleEntries(FindEntry[] entries) {
        List<FindEntry> entryList = new ArrayList<FindEntry>(entries.length);
        for(FindEntry entry : entries) {
            entryList.add(entry);
        }
        return new SearchResult(entryList);
    }
    
    // 
    private void doFind_SingleMatch_MatchCase(int numParts) {
        String searchStr = "Here is a String to search. It is a string.";
        if( numParts > searchStr.length() ) {
            numParts = searchStr.length();
        }
        SearchResult expectedResult = createSearchResultWithOneEntry(10,0,10);
        doTestCase(searchStr,"ASCII",numParts,"String",true,expectedResult);
        
        searchStr = "Here is another string to search.\n It is a\n  String with\n multiple lines\n\n.";
        expectedResult = createSearchResultWithOneEntry(45,2,2);
        doTestCase(searchStr,"ASCII",numParts,"String",true,expectedResult);
    }
    
    // 
    private void doFind_SingleMatch_DontMatchCase(int numParts) {
        String searchStr = "Here is a String to sEaRcH. It is a string.";
        if( numParts > searchStr.length() ) {
            numParts = searchStr.length();
        }
        SearchResult expectedResult = createSearchResultWithOneEntry(20,0,20);
        doTestCase(searchStr,"ASCII",numParts,"SeArCh",false,expectedResult);
        
        searchStr = "Here is another string to search.\n It is a\n  String with\n mUlTiPlE lines\n\n.";
        expectedResult = createSearchResultWithOneEntry(58,3,1);
        doTestCase(searchStr,"ASCII",numParts,"MuLtIpLe",false,expectedResult);
    }
    
    // 
    private void doFind_MultiMatch_MatchCase(int numParts) {
        String searchString = "Here we have string with a repeated repeated repeated Repeated word";
        SearchResult expectedResult = createSearchResultWithMultipleEntries(new FindEntry[] { new FindEntry(27,0,27),
                                                                                              new FindEntry(36,0,36), 
                                                                                              new FindEntry(45,0,45)
                                                                                            });
        doTestCase(searchString,"ASCII",numParts,"repeated",true,expectedResult);
        
        searchString = "Here is  a String with a repeated word.\nThe word repeated is repeated \n\n repeated four times";
        expectedResult = createSearchResultWithMultipleEntries(new FindEntry[] { new FindEntry(25,0,25),
                                                                                 new FindEntry(49,1,9), 
                                                                                 new FindEntry(61,1,21),
                                                                                 new FindEntry(73,3,1)
                                                                               });
        doTestCase(searchString,"ASCII",numParts,"repeated",true,expectedResult);
    }
    
    // 
    private void doFind_MultiMatch_DontMatchCase(int numParts) {
        String searchString = "red green Blue, red green blue, red green BlUe";
        SearchResult expectedResult = createSearchResultWithMultipleEntries( new FindEntry[] { new FindEntry(10,0,10),
                                                                                               new FindEntry(26,0,26), 
                                                                                               new FindEntry(42,0,42)
                                                                                              });
        doTestCase(searchString,"ASCII",numParts,"blue",false,expectedResult);
        
        searchString = "\n\nMy favorite colors are:\nred \n green blue\n red GreeN blue\n  red gReen\nblue";
        expectedResult = createSearchResultWithMultipleEntries(new FindEntry[] { new FindEntry(32,4,1),
                                                                                 new FindEntry(48,5,5), 
                                                                                 new FindEntry(65,6,6)
                                                                               });
        doTestCase(searchString,"ASCII",numParts,"grEEn",false,expectedResult);
    }
    
    @Test
    public void testFind_SingleMatch_OneMsg_MatchCase() {
        doFind_SingleMatch_MatchCase(1);
    }
    
    @Test
    public void testFind_SingleMatch_MultiMsg_CaseMatch() {
        doFind_SingleMatch_MatchCase(2);
        doFind_SingleMatch_MatchCase(3);
        doFind_SingleMatch_MatchCase(10);
    }
    
    @Test
    public void testFind_SingleMatch_OneMsg_DontMatchCase() {
        doFind_SingleMatch_DontMatchCase(1);
    }
    
    @Test
    public void testFind_SingleMatch_MultiMsg_DontMatchCase() {
        doFind_SingleMatch_DontMatchCase(2);
        doFind_SingleMatch_DontMatchCase(3);
        doFind_SingleMatch_DontMatchCase(10);
    }
    
    @Test
    public void testFind_MultiMatch_OneMsg_MatchCase() {
        doFind_MultiMatch_MatchCase(1);
    }
    
    @Test
    public void testFind_MultiMatch_MultiMsg_MatchCase() {
        doFind_MultiMatch_MatchCase(2);
        doFind_MultiMatch_MatchCase(5);
        doFind_MultiMatch_MatchCase(11);
    }
    
    @Test
    public void testFind_MultiMatch_OneMsg_DontMatchCase() {
        doFind_MultiMatch_DontMatchCase(1);
    }
    
    @Test
    public void testFind_MultiMatch_MultiMsg_DontMatchCase() {
        doFind_MultiMatch_DontMatchCase(3);
        doFind_MultiMatch_DontMatchCase(8);
        doFind_MultiMatch_DontMatchCase(10);
    }
    
    // This test is here to show the steps taken to test the Finder.
    // All if the @Test methods above take the same overall steps listed in this method
    @Test()
    public void testSimpleFind() {
        
        // start with a string to search, get the data messages
        String testStr = "Here is a String to search";
        Queue<NewDataMsg> msgQueue = getNewDataMsgsFromString(testStr,"ASCII",1);
        
        // create a dummy link that will return these messages when recieve is called
        // these will be the messages seen by the finder
        Link<NewDataMsg> finderLinkIn = new DummyLink<NewDataMsg>(msgQueue);
        
        // create other links for Finder to send out to
        Link<SearchResult> finderLinkNext = new QueueLink<SearchResult>();
        Link<RunStatus> finderLinkTop = new QueueLink<RunStatus>();
        
        // find our target String
        String target = "String";
        Finder underTest = new Finder(finderLinkIn,finderLinkNext,finderLinkTop,"ASCII",target,true);
        underTest.run();
        
        // expectations
        List<FindEntry> expectedFinds = new LinkedList<FindEntry>();
        expectedFinds.add(new FindEntry(10,0,10));
        SearchResult expectedResult = new SearchResult(expectedFinds);
        
        // see if it worked
        Assert.assertEquals(RunStatus.getOkRunStatus(),finderLinkTop.receive());        
        Assert.assertEquals(expectedResult,finderLinkNext.receive());
    }
}
