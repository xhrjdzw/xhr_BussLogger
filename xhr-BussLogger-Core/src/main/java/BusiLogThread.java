import config.groovyloader.GroovyConfigsCache;
import context.ThreadLocalBusiLogContext;
import exception.BusiLogConfigException;
import exception.BusiLogException;
import groovy.lang.GroovyObject;
import model.BusinessLog;
import org.codehaus.groovy.runtime.GStringImpl;
import org.osgi.service.log.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import writer.IBusiLogWriter;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 徐浩然
 * @version BusiLogThread, 2017-12-15
 * 日志处理线程
 */
public class BusiLogThread implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(BusiLogThread.class);

    private static final String LOG_KEY = "log";

    private static final String CATEGORY_KEY = "category";

    private Map<String, Object> context;

    private String BLMappingValue;

    private String LogMethodValue;

    private volatile String tenantid;

    /**
     * 日志导出接口
     */
    private IBusiLogWriter busiLogWriter;

    public BusiLogThread(Map<String, Object> context, String BLMappingValue, IBusiLogWriter busiLogWriter)
    {
        this.context = context;
        this.BLMappingValue = BLMappingValue;
        this.busiLogWriter = busiLogWriter;
    }

    @Override
    public void run()
    {
        //向线程Local中放入东西
        ThreadLocalBusiLogContext.putBusinessLogMethod(BLMappingValue);
        //读取配置文件
        try
        {
            GroovyObject groovyConfig = getGroovyConfig(BLMappingValue);
            if (groovyConfig == null) {return ;}
            // 返回指定映射的不可修改视图
            // 实现原是是包装了下map 不支持改变大小的操作
            // 仅仅返回的Map不能put remove 操作，
            // 但可以对里的对象进行操作
            //读取配置文件
            groovyConfig.setProperty("context", Collections.unmodifiableMap(context));
            //匹配注解的配置项 反射拿方法
            BusinessLog logvo = createBusinessLog(groovyConfig.invokeMethod(BLMappingValue, null));
            //写入配置文件
            busiLogWriter.write(logvo);
        } catch (IOException e)
        {
            e.printStackTrace();
            LOGGER.error("----------insert data exception-------------" + e.getMessage());
        }

    }

    @SuppressWarnings({ "static-access", "unchecked", "rawtypes" })
    private GroovyObject getGroovyConfig(String businessMethod) throws IOException
    {
        GroovyConfigsCache groovyConfigsCache = new GroovyConfigsCache();

        if (groovyConfigsCache.isStandaloneConfig())
            return getGroovyObject(GroovyConfigsCache.getStandaloneGroovyObjectClass());

        for (Class each : GroovyConfigsCache.getGroovyObjectClasses()) {
            try {
                if (each.getMethod(businessMethod) == null)
                    continue;

                return getGroovyObject(each);

            } catch (NoSuchMethodException e) {
                continue;
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    private GroovyObject getGroovyObject(Class clazz) {
        try {
            if (null == clazz)
                throw new BusiLogConfigException("The config must be a groovy class");
            LOGGER.debug("123");
            return (GroovyObject) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new BusiLogConfigException("InstantiationException", e);
        } catch (IllegalAccessException e) {
            throw new BusiLogConfigException("IllegalAccessException", e);
        }
    }

    @SuppressWarnings("unchecked")
    private BusinessLog createBusinessLog(Object object) {
        //声明业务日志实体
        BusinessLog businessLog = new BusinessLog();
        //添加存储了方法信息的上下文 map
        businessLog.addContext(context);
        //判断传入的是否是特定的一个类 ，开始循环
        if (object instanceof LinkedHashMap) {
            //强转换
            Map<String, Object> map = (LinkedHashMap<String, Object>) object;
            LOGGER.debug("123");
            //给要写入log的实体赋值
            businessLog.setLog(map.get(LOG_KEY).toString() + "    |返回方法结果：     "+ context.get("_methodReturnValue").toString().substring(0,100));
            businessLog.setCategory(map.get(CATEGORY_KEY).toString());
        } else if (object instanceof GStringImpl || object instanceof String) {
            businessLog.setLog(object.toString());
        } else {
            throw new BusiLogException("failure to execute groovy");
        }
        return businessLog;
    }
}
