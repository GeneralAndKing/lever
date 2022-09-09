package wiki.lever.modal.exception;

/**
 * 2022/9/9 22:34:19
 *
 * @author yue
 */
public class SystemException extends RuntimeException {
    public SystemException(String message) {
        super(message);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }
}
