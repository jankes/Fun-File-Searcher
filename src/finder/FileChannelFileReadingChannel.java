package finder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

// implementation of the lighweight wrapper around java FileChannel
class FileChannelFileReadingChannel implements FileReadingChannel {
    
    private FileChannel mChannel;
    
    public FileChannelFileReadingChannel(String fileName) throws IOException {
        mChannel = new FileInputStream(new File(fileName)).getChannel();
    }
        
    @Override
    public void close() throws IOException {
        mChannel.close();
    }

    @Override
    public MappedByteBuffer map(MapMode mapMode, long position, long size) throws IOException {
        return mChannel.map(mapMode,position,size);
    }

    @Override
    public long size() throws IOException {        
        return mChannel.size();
    }

}
