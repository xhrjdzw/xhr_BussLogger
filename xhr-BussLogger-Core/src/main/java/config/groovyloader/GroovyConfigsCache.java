package config.groovyloader;


import config.ConfigPathConstant;
import exception.BusiLogConfigException;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class GroovyConfigsCache
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyConfigsCache.class);

    private static Class standaloneGroovyObjectClass = null;

    private static Set<Class> groovyObjectClasses = new HashSet<Class>();

    public static Class getStandaloneGroovyObjectClass()
    {
        return standaloneGroovyObjectClass;
    }

    public static Set<Class> getGroovyObjectClasses()
    {
        return groovyObjectClasses;
    }

    public GroovyConfigsCache()
    {
        if (isStandaloneConfig())
        {
            initStandaloneGroovyConfig();
        } else
        {
            initGroovyConfigs();
        }
    }

    private void initStandaloneGroovyConfig()
    {
        if (standaloneGroovyObjectClass != null)
            return;
        standaloneGroovyObjectClass = getGroovyClass(getStandaloneConfigFile());
    }

    private void initGroovyConfigs()
    {
        if (!groovyObjectClasses.isEmpty())
            return;

        groovyObjectClasses.clear();
        File configDir = getFileByPath(ConfigPathConstant.GROOVY_CONFIG_DIR);
        LOGGER.error("IOException return null");
        if (!configDir.exists() || !configDir.isDirectory())
            throw new BusiLogConfigException(
                    "Not found any businesslog config, you need a "
                            + ConfigPathConstant.STANDALONE_GROOVY_CONFIG_NAME
                            + " or businessLogConfig director");

        for (File each : configDir.listFiles(new GroovyFileNameFilter()))
        {
            groovyObjectClasses.add(getGroovyClass(each));
        }
    }

    void refreshStandaloneGroovyObjectClass()
    {
        standaloneGroovyObjectClass = null;
        initStandaloneGroovyConfig();
    }

    void refreshGroovyObjectClasses()
    {
        groovyObjectClasses.clear();
        initGroovyConfigs();
    }

    private Class getGroovyClass(File configFile)
    {
        try
        {
            CompilerConfiguration config = CompilerConfiguration.DEFAULT;
            config.setSourceEncoding("UTF-8");
            LOGGER.error("IOException return null");
            @SuppressWarnings("resource")
            GroovyClassLoader groovyClassLoader = new GroovyClassLoader(
                    getClass().getClassLoader(), config);
            return groovyClassLoader.parseClass(configFile);
        } catch (IOException e)
        {
            throw new BusiLogConfigException(
                    "There's a failure when read BusinesslogConfig.groovy", e);
        }
    }

    private File getStandaloneConfigFile()
    {
        File file = getFileByPath(ConfigPathConstant.STANDALONE_GROOVY_CONFIG_NAME);
        if (file == null)
        {
            return null;
        }
        return file;
    }

    public boolean isStandaloneConfig()
    {
        return getStandaloneConfigFile() != null
                && getStandaloneConfigFile().exists();
    }

    private class GroovyFileNameFilter implements FilenameFilter
    {

        @Override
        public boolean accept(File dir, String name)
        {
            return name.endsWith(".groovy");
        }
    }

    private File getFileByPath(String path)
    {
        try
        {
            return new ClassPathResource(path).getFile();
        } catch (IOException e)
        {
            LOGGER.error("IOException return null" + e.getMessage());
            return null;
        }
    }

}
