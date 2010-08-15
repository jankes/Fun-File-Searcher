package finder;

import java.util.LinkedList;
import java.util.Queue;

// class used to help with testing
class DummyLink<T> implements Link<T> {
    
    private Queue<T> mOutput;   // what will be received when recieve is called
    private Queue<T> mSent;     // record of what was sent
    private Queue<T> mReceived; // record of what was received
    
    // create a new DummyLink loaded with the queue for receiving
    DummyLink(Queue<T> receiveList) {
        mOutput = new LinkedList<T>();
        for(T t : receiveList) {
            mOutput.add(t);
        }
        mSent = new LinkedList<T>();
        mReceived = new LinkedList<T>();
    }
    
    @Override
    public T receive() {
        T received = mOutput.remove();
        mReceived.add(received);
        return received;
    }

    @Override
    public void send(T t) {
        mSent.add(t);
    }
    
    // for test code to see what was received on the link
    public Queue<T> getReceived() {
        return mReceived;
    }
    
    // for test code to see what was sent to the link
    public Queue<T> getSent() {
        return mSent;
    }
}
