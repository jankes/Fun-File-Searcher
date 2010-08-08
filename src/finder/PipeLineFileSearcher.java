package finder;

import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;

class PipeLineFileSearcher implements FileSearcher {
    
    @Override
    public SearchResult searchFile(String fileName,String charsetName,String searchStr,boolean matchCase) throws FileSearcherFailException {
        
        Link<RunStatus> readerToTopLink = new QueueLink<RunStatus>();
        Link<RunStatus> finderToTopLink = new QueueLink<RunStatus>();
        Link<NewDataMsg> readerToFinderLink = new QueueLink<NewDataMsg>();
        Link<SearchResult> finderToEndLink = new QueueLink<SearchResult>();
        
        FileReader reader = new FileReader(readerToFinderLink,readerToTopLink,fileName,4096);
        
        Finder finder;
        try {
            finder = new Finder(readerToFinderLink,finderToEndLink,finderToTopLink,charsetName,searchStr,matchCase);
        }        
        catch(IllegalCharsetNameException e) {
            throw new FileSearcherFailException("");
        }
        catch(UnsupportedCharsetException e) {
            throw new FileSearcherFailException("");
        }
        
        Thread readerThread = new Thread(reader);
        Thread finderThread = new Thread(finder);
        
        readerThread.start();
        finderThread.start();
        
        RunStatus readerStatus = readerToTopLink.receive();
        RunStatus finderStatus = finderToTopLink.receive();
        
        if( readerStatus.isError() ) {
            throw new FileSearcherFailException("file reading error");
        }
        if( finderStatus.isError() ) {
            throw new FileSearcherFailException("finder had an error");
        }
        
        return finderToEndLink.receive();
    }    
}
