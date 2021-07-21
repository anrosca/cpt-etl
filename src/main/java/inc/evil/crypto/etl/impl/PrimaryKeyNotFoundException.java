package inc.evil.crypto.etl.impl;

public class PrimaryKeyNotFoundException extends RuntimeException {
    public PrimaryKeyNotFoundException(String message) {
        super(message);
    }
}
