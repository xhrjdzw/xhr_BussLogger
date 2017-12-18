package exception;

/**
 * 日志配置异常
 */
public class BusiLogConfigException extends RuntimeException {

	private static final long serialVersionUID = -2206962256871381372L;

	public BusiLogConfigException() {
        super();
    }

    public BusiLogConfigException(String message) {
        super(message);
    }

    public BusiLogConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusiLogConfigException(Throwable cause) {
        super(cause);
    }

    protected BusiLogConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        //FIXME 编译出错
        //super(message, cause, enableSuppression, writableStackTrace);
    }
}
