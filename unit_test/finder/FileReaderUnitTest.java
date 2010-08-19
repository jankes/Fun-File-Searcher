package finder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel.MapMode;

import org.junit.Assert;
import org.junit.Test;

public class FileReaderUnitTest {
    
    // TODO: was working here
    // refactor the redundancy out of the current tests, and at least write the one below
    
    // FileReader should send to top and next when it successfully reads a file
        
    
    // FileReader should send to top and next when getFileReadingChannel throws an IOException
    @Test
    public void testFileReaderHandlesIOExceptionOnGetFileReadingChannel() {
        // Links the FileReader will send to
        DummyLink<NewDataMsg> linkNext = new DummyLink<NewDataMsg>(null);
        DummyLink<RunStatus> linkTop = new DummyLink<RunStatus>(null);
        
        // create and run a FileReader whose getFileReadingChanel method always throws an IOException
        FileReader underTest = new FileReader(linkNext,linkTop,"fileToSearch",100) {
            @Override
            protected FileReadingChannel getFileReadingChannel() throws IOException {
                throw new IOException();
            }
        };
        underTest.run();
        
        // check the FileReaders outbound links are sent to exactly once
        if( linkNext.getSent().size() != 1 ) {
            Assert.fail("Expected FileReader to send 1 NewDataMsg to its next link. Instead it sent " + linkNext.getSent().size());
        }
        if( linkTop.getSent().size() != 1 ) {
            Assert.fail("Expected FileReader to send 1 RunStatus to its top link. Instead it sent " + linkTop.getSent().size());
        }
        
        // should have a "last" message to next
        Assert.assertTrue("FileReader should send last NewDataMsg",linkNext.getSent().peek().isLast());
        
        // should have sent error run status
        Assert.assertEquals(linkTop.getSent().peek(),RunStatus.getErrorRunStatus(null));
    }
    
    // FileReader should send to top and next when calling map on its FileReadingChannel throws an IOException
    @Test
    public void testFileReaderHandlesIOExceptionOnMapFileReadingChannel() {
        // out links for the FileReader
        DummyLink<NewDataMsg> linkNext = new DummyLink<NewDataMsg>(null);
        DummyLink<RunStatus> linkTop = new DummyLink<RunStatus>(null);
        
        // FileReadingChannel that throws IOException on call to map method
        class FakeFileReadingChannel implements FileReadingChannel {
            @Override
            public void close() throws IOException { }

            @Override
            public MemoryMappedBuffer map(MapMode mapMode, long position, long size) throws IOException {
                throw new IOException();
            }

            @Override
            public long size() throws IOException {
                return 100;
            }
        }
        
        FileReader underTest = new FileReader(linkNext,linkTop,"searchFile",50) {
            @Override
            protected FileReadingChannel getFileReadingChannel() {
                return new FakeFileReadingChannel();
            }
        };
        underTest.run();
        
        // check the FileReaders outbound links are sent to exactly once
        if( linkNext.getSent().size() != 1 ) {
            Assert.fail("Expected FileReader to send 1 NewDataMsg to its next link. Instead it sent " + linkNext.getSent().size());
        }
        if( linkTop.getSent().size() != 1 ) {
            Assert.fail("Expected FileReader to send 1 RunStatus to its top link. Instead it sent " + linkTop.getSent().size());
        }
        
        // should have a "last" message to next
        Assert.assertTrue("FileReader should send last NewDataMsg",linkNext.getSent().peek().isLast());
        
        // should have sent error run status
        Assert.assertEquals(linkTop.getSent().peek(),RunStatus.getErrorRunStatus(null));
    }
    
    // FileReader should send to top and next when closing the FileReading channel throws an IOException
    @Test
    public void testFileReaderHandlesIOExceptionOnCloseFileReadingChannel() {
        // out links for the FileReader
        DummyLink<NewDataMsg> linkNext = new DummyLink<NewDataMsg>(null);
        DummyLink<RunStatus> linkTop = new DummyLink<RunStatus>(null);
        
        // dummy implementation of MemoryMappedBuffer
        class FakeBuffer implements MemoryMappedBuffer {
            @Override
            public ByteBuffer getByteBuffer() {
                return ByteBuffer.wrap(new byte[0]);
            }

            @Override
            public MemoryMappedBuffer load() {
                return this;
            }
            
        }
        
        // FileReadingChannel that throws IOException on call to close method
        class FakeFileReadingChannel implements FileReadingChannel {
            @Override
            public void close() throws IOException {
                throw new IOException();
            }

            @Override
            public MemoryMappedBuffer map(MapMode mapMode, long position, long size) throws IOException {
                return new FakeBuffer();
            }

            @Override
            public long size() throws IOException {
                return 10;
            }
        }
        
        FileReader underTest = new FileReader(linkNext,linkTop,"searchFile",50) {
            @Override
            protected FileReadingChannel getFileReadingChannel() {
                return new FakeFileReadingChannel();
            }
        };
        underTest.run();
        
        // check the FileReaders outbound links are sent to exactly once
        if( linkNext.getSent().size() != 1 ) {
            Assert.fail("Expected FileReader to send 1 NewDataMsg to its next link. Instead it sent " + linkNext.getSent().size());
        }
        if( linkTop.getSent().size() != 1 ) {
            Assert.fail("Expected FileReader to send 1 RunStatus to its top link. Instead it sent " + linkTop.getSent().size());
        }
        
        // should have a "last" message to next
        Assert.assertTrue("FileReader should send last NewDataMsg",linkNext.getSent().peek().isLast());
        
        // should have sent error run status
        Assert.assertEquals(linkTop.getSent().peek(),RunStatus.getErrorRunStatus(null));
    }
    
}
