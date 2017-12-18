import com.alibaba.fastjson.JSONObject;
import config.annotation.BusiLogConfig;
import context.ThreadLocalBusiLogContext;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import writer.IBusiLogWriter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import static context.ContextKeyConstant.*;

/**
 * 用户配置切点 增强方法进入拦截器
 * 日志拦截器
 */
public class BusiLogInterceptor
{
    private static final Logger logger = LoggerFactory.getLogger(BusiLogInterceptor.class);

    private static final String BUSINESS_LOG_CONFIG_PROPERTIES_NAME = "busilog-Config.properties";

    private static final String LOG_ENABLE = "logger.enable";

    private static Boolean isLogEnabled = null;

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private IBusiLogWriter busiLogWriter;

    /**
     * 开始处理日志逻辑
     */
    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor)
    {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    public ThreadPoolTaskExecutor getThreadPoolTaskExecutor()
    {

        return threadPoolTaskExecutor;
    }

    public void logAfter(JoinPoint joinPoint, Object result)
    {
        try
        {
            log(joinPoint, result, null);
        } catch (Exception e)
        {
            logger.error("logAfter-----" + e.getMessage());
        }
    }


    /**
     * 开始处理日志逻辑
     **/
    public void log(JoinPoint joinPoint, Object result, Throwable error)
    {
        // 获取方法的日志配置模板名（获取注解配置的名字）塞入线程和配置文件匹配
        String BLMappingValue = getBLMapping(joinPoint);
        //如果没配 直接返回
        if ("".equals(BLMappingValue))
        {
            return;
        }
        //日志开关校验
        if (isLogEnabled()
                //todo 多线程情况 会发生什么 为什么要这么判断
                || ThreadLocalBusiLogContext.get().get(BUSINESS_METHOD) != null)
        {
            return;
        }
        //启动线程执行run()方法
        BusiLogThread busiLogThread = getThread(
                Collections.unmodifiableMap(createDefaultContext(joinPoint, result, error)), BLMappingValue, getBusiLogWriter());

    }

    /**
     * 创建线程上下文中的信息
     **/
    private Map<String, Object> createDefaultContext(JoinPoint joinPoint, Object result, Throwable error)
    {
        //在这个时候开始向里面线程的ThreadLocal中塞入信息
        Map<String, Object> context = ThreadLocalBusiLogContext.get();
        //获取所有参数，按照类型需要塞入其中
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++)
        {
            context.put(PRE_OPERATOR_OF_METHOD_KEY + i, args[i]);
        }
        //这里获取返回的详细信息
        if (null != result)
        {
            //业务方法的参数
            context.put(BUSINESS_METHOD_RETURN_VALUE_KEY, result);
            //返回结果的详细信息 暂时截取100位
            context.put(BUSINESS_METHOD_RETURN_VALUE, JSONObject.toJSONString(((PageImpl) result).getContent()).substring(0, 100));
            logger.debug("logAfter-----");
        }

        if (null != error)
        {
            //业务方法执行失败的异常信息
            context.put(BUSINESS_METHOD_EXECUTE_ERROR, error.getMessage());
        }

        //业务方法
        context.put(BUSINESS_METHOD, getBLMapping(joinPoint));
        //操作时间
        context.put(BUSINESS_OPERATION_TIME, new Date());
        //ip地址
        context.put(BUSINESS_OPERATION_IP, ThreadLocalBusiLogContext.get().get(BUSINESS_OPERATION_IP));

        return context;
    }


    /**
     * 获取线程  todo 每次都会新启动一个线程?
     **/
    protected BusiLogThread getThread(Map<String, Object> context, String BLMappingValue,
                                      IBusiLogWriter busiLogWriter)
    {

        return new BusiLogThread(context, BLMappingValue, busiLogWriter);
    }

    //获得方法信息
    private String getBLMapping(JoinPoint joinPoint)
    {
        Method method = invocationMethod(joinPoint);
        //返回注释的类
        if (method.isAnnotationPresent(BusiLogConfig.class))
        {
            return method.getAnnotation(BusiLogConfig.class).value();
        }
        return "";
    }

    private Method invocationMethod(JoinPoint joinPoint)
    {
        try
        {   //获取类或接口的指定已声明字段
            Field methodInvocationField = MethodInvocationProceedingJoinPoint.class.getDeclaredField("methodInvocation");
            //反射时访问私有变量
            methodInvocationField.setAccessible(true);
            //
            ProxyMethodInvocation methodInvocation = (ProxyMethodInvocation) methodInvocationField.get(joinPoint);
            return methodInvocation.getMethod();
        } catch (NoSuchFieldException e)
        {
            return null;
        } catch (IllegalAccessException e)
        {
            logger.error("invocationMethod IllegalAccessException return null" + e.getMessage());
            return null;
        }

    }

    public IBusiLogWriter getBusiLogWriter()
    {
        return busiLogWriter;
    }

    private boolean isLogEnabled()
    {
        //如果之前检验过  直接返回
        if (isLogEnabled != null)
        {
            return isLogEnabled;
        }
        //加载配置文件设置
        Properties properties = new Properties();
        try
        {
            properties.load(new FileInputStream(new ClassPathResource(BUSINESS_LOG_CONFIG_PROPERTIES_NAME).getFile()));
            //判断是否已经开启日志
            isLogEnabled = Boolean.valueOf(properties.getProperty(LOG_ENABLE, "true"));
            return isLogEnabled;
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
