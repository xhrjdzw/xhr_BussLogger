package exception;

/**
 * 日志异常
 */
public class BusiLogException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public BusiLogException() {
    }

    public BusiLogException(String message) {
        super(message);
    }

    public BusiLogException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusiLogException(Throwable cause) {
        super(cause);
    }
}
