package finder;

interface Link<T> {
    
    // blocking
    void send(T t);
    
    // blocking
    T receive();
    
}
