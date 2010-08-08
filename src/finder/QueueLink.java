package finder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class QueueLink<T> implements Link<T> {
    
    private BlockingQueue<T> mQueue;
    
    QueueLink() {
        mQueue = new LinkedBlockingQueue<T>();
    }
    
    @Override
    public T receive() {
        try {
            return mQueue.take();
        }
        catch(InterruptedException e) {
            String name = Thread.currentThread().getName();
            throw new RuntimeException("thread " + name + "interrupted while attempting to receive on Link");
        }
    }

    @Override
    public void send(T buf) {
        mQueue.add(buf);
    }
    
}
