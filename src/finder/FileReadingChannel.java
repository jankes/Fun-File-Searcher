package finder;

import java.io.IOException;
import java.nio.channels.FileChannel.MapMode;

// wrapper interface around the methods in java's FileChannel used by the FileReader.
// FileReader talks to the interface and not FileChannel itself so that interaction with the file system can be faked
interface FileReadingChannel {
    long size() throws IOException;
    
    MemoryMappedBuffer map(MapMode mapMode,long position,long size) throws IOException;
    
    void close() throws IOException;    
}
