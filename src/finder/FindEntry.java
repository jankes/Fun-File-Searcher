package finder;

// container class for a single find
// identifies a sequence of bytes in a file by line number and index (number of bytes from the start of the file)
// 
public class FindEntry {
    private final long  mLineNum;
    private final long mIndex;
    
    FindEntry(long lineNum,long index) {
        mLineNum = lineNum;
        mIndex = index;
    }
    
    public long getLineNum() {
        return mLineNum;
    }
    
    public long getIndex() {
        return mIndex;
    }
    
    @Override
    public boolean equals(Object other) {
        if( !(other instanceof FindEntry) ) {
            return false;
        }
        FindEntry otherEntry = (FindEntry)other;
        return mLineNum == otherEntry.getLineNum() && mIndex == otherEntry.getIndex();
    }
}
