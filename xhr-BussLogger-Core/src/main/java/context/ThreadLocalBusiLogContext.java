package context;

import java.util.HashMap;
import java.util.Map;

import static context.ContextKeyConstant.BUSINESS_METHOD;
import static context.ContextKeyConstant.BUSINESS_METHOD_RETURN_VALUE;

/**
 * @author 徐浩然
 * @version ThreadLocalBusiLogContext, 2017-12-15
 */
public class ThreadLocalBusiLogContext
{
    private static ThreadLocal<Map<String, Object>> context = new ThreadLocal<Map<String, Object>>()
    {
        protected synchronized Map<String, Object> initialValue()
        {
            return new HashMap<>();
        }
    };

    public static Map<String, Object> get()
    {
        Map<String, Object> result = new HashMap<String, Object>();
        for (String key : context.get().keySet())
        {
            result.put(key, context.get().get(key));
        }
        return result;
    }

    public static void put(String key, Object value)
    {
        context.get().put(key, value);
    }

    public static Object getBusinessMethod()
    {
        return get().get(BUSINESS_METHOD);
    }

    public static void clear()
    {
        context.set(new HashMap<String, Object>());
    }

    public static void putBusinessLogMethod(String blMappingValue)
    {
        put(BUSINESS_METHOD, blMappingValue);
    }

    //传入切点查询数据结果
    public static void putBusinessLogMethodValue(String logMethodValue)
    {
        put(BUSINESS_METHOD_RETURN_VALUE, logMethodValue);
    }
}
