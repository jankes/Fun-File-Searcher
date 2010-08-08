package finder;

import java.util.Iterator;
import java.util.List;

// container class for the result of a search
// identifies a sequence of bytes in a file by line number and index (number of bytes from the start of the file) of a
public class SearchResult {
    private final boolean mError;
    private final List<FindEntry> mFinds;
    
    SearchResult(List<FindEntry> found,boolean error) {
        mError = error;
        mFinds = found;
    }
    
    public boolean hasError() {
        return mError;
    }
    
    public List<FindEntry> getFinds() {
        return mFinds;
    }
    
    @Override
    public boolean equals(Object other) {
        if( !(other instanceof SearchResult) ) {
            return false;
        }
        List<FindEntry> otherFinds = ((SearchResult)other).getFinds();
        if( mFinds.size() != otherFinds.size() ) {
            return false;
        }
        Iterator<FindEntry> myIt = mFinds.iterator();
        Iterator<FindEntry> otherIt = otherFinds.iterator();
        while( myIt.hasNext() ) {
            if( !myIt.next().equals(otherIt.next()) ) {
                return false;
            }
        }
        return true;
    }
}
