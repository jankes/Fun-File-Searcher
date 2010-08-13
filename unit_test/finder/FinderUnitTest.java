package finder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import junit.framework.Assert;

import org.junit.Test;


public class FinderUnitTest {
    
    private List<NewDataMsg> getMsgs(String str,String charsetName,int bytesPerMsgHint) {
        List<NewDataMsg> outList = new LinkedList<NewDataMsg>();
        
        Charset charset = Charset.forName(charsetName);
        ByteBuffer allBytes = charset.encode(str);
        
        int numWholeMsgs = allBytes.limit() / bytesPerMsgHint;
        for(int i=0; i<numWholeMsgs; i++) {
            boolean lastMsg = (allBytes.remaining() == bytesPerMsgHint) ? true : false;
            byte[] msgBytes = new byte[bytesPerMsgHint];
            allBytes.get(msgBytes,0,bytesPerMsgHint);
            outList.add( new NewDataMsg(ByteBuffer.wrap(msgBytes),lastMsg) );
        }
        if( allBytes.limit() % bytesPerMsgHint != 0) {
            byte[] msgBytes = new byte[allBytes.remaining()];
            allBytes.get(msgBytes);
            outList.add( new NewDataMsg(ByteBuffer.wrap(msgBytes),true) );
        }
        return outList;
    }
    
    
    // test Finder always sends to top and next 
    
    
    
    @Test()
    public void testSimpleFind() {
        
        // start with a string to search, get the data messages
        String testStr = "Here is a String to search";
        List<NewDataMsg> dataMsgs = getMsgs(testStr,"ASCII",testStr.length() / 3 );
        
        // create a dummy link that will return these messages when recieve is called
        // these will be the messages seen by the finder
        Queue<NewDataMsg> msgQueue = new LinkedList<NewDataMsg>();        
        for(NewDataMsg msg : dataMsgs) {
            msgQueue.offer(msg);
        }
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
