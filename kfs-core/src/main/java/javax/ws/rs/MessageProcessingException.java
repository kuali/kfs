package javax.ws.rs;

public class MessageProcessingException extends Exception {

    public MessageProcessingException(Exception e) {
        e.printStackTrace();
    }
}
