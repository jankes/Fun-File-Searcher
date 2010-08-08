package finder;

class FileReaderFailException extends RuntimeException {
    private static final long serialVersionUID = -1421775753084308950L;
    
    public FileReaderFailException(String msg) {
        super(msg);
    }
}
