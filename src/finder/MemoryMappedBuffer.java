package finder;

import java.nio.ByteBuffer;

// wrapper around java MappedByteBuffer methods used by FileReader
public interface MemoryMappedBuffer {
    MemoryMappedBuffer load();
    
    ByteBuffer getByteBuffer();
}
