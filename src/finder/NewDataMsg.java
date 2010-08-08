package finder;

import java.nio.ByteBuffer;

// container class for the message passed between the FileReader and Matcher
// the data buffer contains data being sent from the FileReader to the Matcher
// the boolean last says if this is last message from the FileReader (and no more NewDataMsg's will be sent)
class NewDataMsg {
    private final ByteBuffer mDataBuffer;
    private final boolean mIsLast;
    
    NewDataMsg(ByteBuffer data,boolean last) {
        mDataBuffer = data;
        mIsLast = last;
    }
    
    ByteBuffer getData() {
        return mDataBuffer;
    }
    
    boolean isLast() {
        return mIsLast;
    }
}
