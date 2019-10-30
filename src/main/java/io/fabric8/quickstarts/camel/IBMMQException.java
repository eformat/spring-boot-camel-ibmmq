package io.fabric8.quickstarts.camel;

public class IBMMQException extends RuntimeException {

    public IBMMQException(String message) {
        super(message);
    }

    public IBMMQException(String message, Throwable cause) {
        super(message, cause);
    }

}

