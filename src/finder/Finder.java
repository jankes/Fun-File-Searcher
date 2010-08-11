package finder;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedList;
import java.util.List;

// 
class Finder implements Runnable {
    
    // allow for multiple implementations of a method to determine if two characters "match"
    private interface Matcher {
        boolean doCharsMatch(Character c1,Character c2);
    }
    
    // matcher implementation specifying two characters match if and only if they are the same when converted to lowercase
    private class IgnoreCaseMatcher implements Matcher {
        @Override
        public boolean doCharsMatch(Character c1,Character c2) {
            return Character.toLowerCase(c1) == Character.toLowerCase(c2);
        }
    }
    
    // matcher implementation specifying two characters match if and only if they are the same character
    private class ExactCharMatcher implements Matcher {
        @Override
        public boolean doCharsMatch(Character c1,Character c2) {
            return c1.equals(c2);
        }
    }
    
    //
    private static final int DECODE_BUF_SIZE = 20;
    
    // pipeline links
    private final Link<NewDataMsg> mLinkIn;
    private final Link<SearchResult>  mLinkNext;    
    private final Link<RunStatus> mLinkTop;
    
    private final CharsetDecoder  mDecoder;
    
    private final Matcher mMatcher;
    
    // the String we will try to find
    private final String  mTarget;
    
    // index in the target String we are currently matched up to
    private int mMatchIndex;
    
    // newline count in incoming data
    private long mLineNum;
    
    // byte count within each line in incoming data
    private long mColNum;
    
    // byte count in incoming data
    private long mByteNum;
    
    // package protected constructor for FileSearcher to call
    Finder(Link<NewDataMsg> linkIn,Link<SearchResult> linkNext,Link<RunStatus> linkTop,String charsetName,String target,boolean matchCase)
                                                                             throws IllegalCharsetNameException, UnsupportedCharsetException {
        mLinkIn = linkIn;
        mLinkNext = linkNext;
        mLinkTop = linkTop;
        mDecoder = Charset.forName(charsetName).newDecoder();
        mTarget = target;
        mMatcher = matchCase ? new ExactCharMatcher() : new IgnoreCaseMatcher();
        mMatchIndex = 0;
        mLineNum = 0;
        mColNum = 0;
        mByteNum = 0;
    }
    
    // subroutine of the run method
    // moves the position of buf numBytes forward
    // note that buf.position() + numBytes <= buf.limit() must be true to call this method
    private void fastForward(ByteBuffer buf,int numBytes) {
        for(int i=0; i<numBytes; i++) {
            buf.get();
        }
        mByteNum += numBytes;
        mColNum += numBytes;
    }
    
    // 
    private void resetFind() {
        mMatchIndex = 0;
    }
        
    // searches the given CharBuffer for target string
    // specifically, searches on the range [ start , in.position() )
    // start <= in.position() must be true to call this method
    // this method does not modify the position or limit of in
    // returns null if no matches of the target String are found, or a list of find entries containing matches
    private List<FindEntry> search(CharBuffer in) {
        List<FindEntry> resList = null;
        while( in.hasRemaining() ) {
            Character nextChar = in.get();
            if( mMatcher.doCharsMatch(nextChar,mTarget.charAt(mMatchIndex)) ) {
                mMatchIndex++;
                if( mMatchIndex == mTarget.length() ) {
                    if(resList == null) {
                        resList = new LinkedList<FindEntry>();
                    }
                    resList.add( new FindEntry(mByteNum - mTarget.length() + 1,mLineNum,mColNum - mTarget.length() + 1) );
                    resetFind();
                }
            }
            else {
                // no matching character
                resetFind();
            }
            if( nextChar.equals('\n') ) {
                mLineNum ++;
                mColNum = 0;
            }
            else {
                mColNum ++;
            }
            mByteNum ++;
        }
        return resList;
    }
    
    // main run loop for searching
    // 
    @Override
    public void run() {
        CharBuffer decodeBuf = CharBuffer.allocate(DECODE_BUF_SIZE);
        List<FindEntry> findList = new LinkedList<FindEntry>();

        NewDataMsg dataMsg = mLinkIn.receive();
        ByteBuffer data = dataMsg.getData();
        while(true) {
            int start = data.position();
            CoderResult coderRes = mDecoder.decode(data,decodeBuf,false);
            List<FindEntry> found = search(decodeBuf);
            decodeBuf.clear();
            if(found != null) {
                findList.addAll( found );
            }                
            if( coderRes.isError() ) {
                resetFind();
                fastForward(data,coderRes.length());
            }
            if( coderRes.isUnderflow() ) {
                if( dataMsg.isLast() ) {
                    // not considering possible malformed input left in the decoder (not calling mDecoder.decode(data,decodeBuf,true) since
                    // since we cannot match any characters in the malformed input anyway
                    mLinkNext.send( new SearchResult(findList) );
                    mLinkTop.send( RunStatus.getOkRunStatus() );
                    return;
                }
                else {
                    dataMsg = mLinkIn.receive();
                    data = dataMsg.getData();
                }
            }
        }
    }
}
