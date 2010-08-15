package finder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;

class FileReader implements Runnable {
    
    private final Link<NewDataMsg> mLinkNext;
    
    private final Link<RunStatus> mLinkTop;
    
    private final String mReadFile;
    
    private final long mChunkSize;
    
    FileReader(Link<NewDataMsg> linkNext,Link<RunStatus> linkTop,String fileName,int chunkSize) {
        mLinkNext = linkNext;
        mLinkTop = linkTop;
        mReadFile = fileName;
        mChunkSize = chunkSize;
    }
    
    // gets an implementor of FileReadingChannel interface
    // protected to make endo-testing the FileReader possible
    protected FileReadingChannel getFileReadingChannel() throws IOException {
        return new FileChannelFileReadingChannel(mReadFile);
    }
    
    private void doChunk(FileReadingChannel channel,long position,long size,boolean isLast) throws IOException {
        MappedByteBuffer chunk = channel.map(MapMode.READ_ONLY,position,size);
        chunk.load();
        mLinkNext.send(new NewDataMsg(chunk,isLast));
    }
    
    private void doDummyChunk(boolean isLast) {
        ByteBuffer dummy = ByteBuffer.wrap(new byte[0]);
        mLinkNext.send(new NewDataMsg(dummy,isLast));
    }
    
    // if successful == false, then parameter except must not be null
    // if successful == true, parameter except can be null
    private void sendRunStatus(boolean successful,Exception except) {
        if(successful) {
            mLinkTop.send( RunStatus.getOkRunStatus() );
        }
        else {
            mLinkTop.send( RunStatus.getErrorRunStatus(except) );
        }
    }
    
    @Override
    public void run() {
        boolean lastChunkDone = false;
        boolean success = false;
        Exception except = null;
        try {
            FileReadingChannel channel = getFileReadingChannel();
            long numWholeChunks = channel.size() / mChunkSize;
            boolean doPartialChunk = (channel.size() % mChunkSize) != 0;
            int position = 0;
            try {
                for(int chunkNum=0; chunkNum < numWholeChunks-1; chunkNum++) {
                    doChunk(channel,position,mChunkSize,false);
                    position += mChunkSize;
                }
                if(numWholeChunks >= 1) {
                    doChunk(channel,position,mChunkSize,!doPartialChunk);
                    lastChunkDone = !doPartialChunk;
                }
                if(doPartialChunk) {
                    doChunk(channel,position,(channel.size() % mChunkSize),true);
                    lastChunkDone = true;
                }
                success = true;
            }
            finally {
                channel.close();
            }
        }
        catch(IOException e) {
            success = false;
            except =  new FileReaderFailException("FileReader got io exception! The message was " + e.getMessage());
        }
        finally {
            if(!lastChunkDone) {
                doDummyChunk(true);
            }
            sendRunStatus(success,except);
        }
    }
}
