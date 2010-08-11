package finder;

// factory that spits out implementations of the FileSearcher interface
public class FileSearcherFactory {
    
    public static FileSearcherFactory getInstance() {
        return new FileSearcherFactory();
    }
    
    public FileSearcher getFileSearcher() {
        return new PipeLineFileSearcher();
    }
}
