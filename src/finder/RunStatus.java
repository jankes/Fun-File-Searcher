package finder;

// object that hold the results of the run of a thread
// The status of running a thread can either be OK, or there could have been an error. These are mutually exclusive.
class RunStatus {
    
    private static final int OK = 0;
    private static final int ERROR = 1;
        
    private final int    mStatus;
    private final Exception mException;
    
    static RunStatus getErrorRunStatus(Exception exception) {
        return new RunStatus(ERROR,exception);
    }
    
    static RunStatus getOkRunStatus() {
        return new RunStatus(OK,new Exception("status OK: No Exception"));
    }
    
    private RunStatus(int status,Exception exception) {
        mStatus = status;
        mException = exception;
    }
    
    boolean isOk() {
        return mStatus == OK;
    }
    
    boolean isError() {
        return mStatus == ERROR;
    }
    
    Exception getException() {
        return mException;
    }
    
    @Override
    public boolean equals(Object other) {
        if( !(other instanceof RunStatus) ) {
            return false;
        }
        RunStatus otherStatus = (RunStatus)other;
        if( mStatus != otherStatus.mStatus ) {
            return false;
        }
        
        // TODO: work out equality of exceptions
        
        return true;
    }
}
