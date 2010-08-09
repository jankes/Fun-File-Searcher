package finder;

// container class for a single find
// identifies a sequence of bytes in a file by line number and index (number of bytes from the start of the file)
// 
public class FindEntry {
    private final long mIndex;
    private final long  mLineNum;
    private final long mColumnNum;
    
    FindEntry(long index,long lineNum,long colNum) {
        mIndex = index;
        mLineNum = lineNum;
        mColumnNum = colNum;
    }
        
    public long getIndex() {
        return mIndex;
    }
    
    public long getLineNum() {
        return mLineNum;
    }
    
    public long getColumn() {
        return mColumnNum;
    }
    
    @Override
    public boolean equals(Object other) {
        if( !(other instanceof FindEntry) ) {
            return false;
        }
        FindEntry otherEntry = (FindEntry)other;
        return mIndex == otherEntry.getIndex() && mLineNum == otherEntry.getLineNum() && mColumnNum == otherEntry.mColumnNum;
    }
}
