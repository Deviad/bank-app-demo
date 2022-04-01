package account.domain;

public class DomainException extends RuntimeException {

    public DomainException(String message, Throwable ex) {
        super(message, ex);
    }

    public DomainException(Throwable ex) {
        super(ex);
    }

    public DomainException(String message) {
        super(message);
    }
}
