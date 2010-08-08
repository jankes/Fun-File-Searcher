package finder;

public interface FileSearcher {
    SearchResult searchFile(String fileName,String charsetName,String searchStr,boolean matchCase) throws FileSearcherFailException;
}
