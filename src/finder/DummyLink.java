package finder;

import java.util.Queue;

// class used to help with testing
class DummyLink<T> implements Link<T> {
    
    private Queue<T> mDummies;
    
    DummyLink(Queue<T> receiveList) {
        mDummies = receiveList;
    }
    
    @Override
    public T receive() {
        return mDummies.remove();
    }

    @Override
    public void send(T t) { }
    
}
