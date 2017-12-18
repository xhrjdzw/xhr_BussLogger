package config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.security.SecureRandom;

/**
 * @author 徐浩然
 * @version BusiLogConfig, 2017-12-15
 * 日志方法注解
 */
//元注解的作用就是负责注解其他注解
@Target(ElementType.METHOD) //用于描述方法
//@Retention定义了该Annotation被保留的时间长短
@Retention(RetentionPolicy.RUNTIME) //在运行时有效（即运行时保留）
public @interface BusiLogConfig
{
    public String value();
}
