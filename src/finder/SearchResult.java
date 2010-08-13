package finder;

import java.util.Iterator;
import java.util.List;

// container class for the result of a search
// identifies a sequence of bytes in a file by line number and index (number of bytes from the start of the file) of a
public class SearchResult {
    private final List<FindEntry> mFinds;
    
    SearchResult(List<FindEntry> found) {
        mFinds = found;
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
        if( (mFinds == null) ^ (otherFinds == null) ) {
            return false;
        }
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
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("SearchResult with ")
                                          .append(mFinds.size())
                                          .append( mFinds.size() == 1 ? " find" : " finds" )
                                          .append('\n');
        for(FindEntry find : mFinds) {
            builder.append( find.toString() ).append('\n');
        }
        return builder.toString();
    }
}
