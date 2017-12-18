package context;

/**
 * @author 徐浩然
 * @version ContextKeyConstant, 2017-12-15
 * 传递给Groovy模板的上下文
 */
public interface ContextKeyConstant
{
    /**
     * 业务方法返回值，在模板中使用的key
     */
    final static String BUSINESS_METHOD_RETURN_VALUE_KEY = "_methodReturn";

    /**
     * 业务方法返回值，在模板中使用的key
     */
    final static String BUSINESS_METHOD_RETURN_VALUE  = "_methodReturnValue";

    /**
     * 业务方法的参数
     */
    final static String PRE_OPERATOR_OF_METHOD_KEY = "_param";

    /**
     * 业务方法执行失败的异常信息
     */
    final static String BUSINESS_METHOD_EXECUTE_ERROR = "_executeError";

    /**
     * 业务方法
     */
    final static String BUSINESS_METHOD = "_businessMethod";

    /**
     * 操作人
     */
    final static String BUSINESS_OPERATION_USER = "_user";

    /**
     * 操作时间
     */
    final static String BUSINESS_OPERATION_TIME = "_time";

    /**
     * ip地址
     */
    final static String BUSINESS_OPERATION_IP = "_ip";

}
