package client;

public class FormatException extends ClientException {
    public FormatException(String message, String format) {
        super(message + (format.isEmpty() ? "" : "\n " + format));
    }
}
