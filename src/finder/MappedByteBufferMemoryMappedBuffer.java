package finder;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;

// implementation of a wrapper around MappedBuffer used by FileReader
class MappedByteBufferMemoryMappedBuffer implements MemoryMappedBuffer {
    private MappedByteBuffer mBuffer;
    
    public MappedByteBufferMemoryMappedBuffer(MappedByteBuffer buf) {
        mBuffer = buf;
    }
    
    @Override
    public MemoryMappedBuffer load() {
        mBuffer.load();
        return this;
    }
    
    @Override
    public ByteBuffer getByteBuffer() {
        return mBuffer;
    }
}
