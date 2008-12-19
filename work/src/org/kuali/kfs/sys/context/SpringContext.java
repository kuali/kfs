/*
 * Copyright 2007 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.MemoryMonitor;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.cache.MethodCacheInterceptor;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ltd.getahead.dwr.create.SpringCreator;

public class SpringContext {
    protected static final Logger LOG = Logger.getLogger(SpringContext.class);
    protected static final String APPLICATION_CONTEXT_DEFINITION = "spring-rice-startup.xml";
    protected static final String BATCH_CONTEXT_DEFINITION = "spring-rice-startup-batch.xml";
    protected static final String TEST_CONTEXT_DEFINITION = "spring-rice-startup-test.xml";
    protected static final String STANDALONE_RICE_DATASOURCE_CONTEXT_DEFINITION = "spring-rice-startup-standalone-rice.xml";
    protected static final String PLUGIN_CONTEXT_DEFINITION = "spring-rice-startup-standalone-rice.xml";
    protected static final String MEMORY_MONITOR_THRESHOLD_KEY = "memory.monitor.threshold";
    protected static ConfigurableApplicationContext applicationContext;
    protected static Set<Class> SINGLETON_TYPES = new HashSet<Class>();
    protected static Set<String> SINGLETON_NAMES = new HashSet<String>();
    protected static Map<Class, Object> SINGLETON_BEANS_BY_TYPE_CACHE = new HashMap<Class, Object>();
    protected static Map<String, Object> SINGLETON_BEANS_BY_NAME_CACHE = new HashMap<String, Object>();
    protected static Map<Class, Map> SINGLETON_BEANS_OF_TYPE_CACHE = new HashMap<Class, Map>();

    /**
     * Use this method to retrieve a spring bean when one of the following is the case. Pass in the type of the service interface,
     * NOT the service implementation. 1. there is only one bean of the specified type in our spring context 2. there is only one
     * bean of the specified type in our spring context, but you want the one whose bean id is the same as type.getSimpleName() with
     * the exception of the first letter being lower case in the former and upper case in the latter, For example, there are two
     * beans of type DateTimeService in our context – dateTimeService and testDateTimeService. To retrieve the former, you should
     * specific DateTimeService.class as the type. To retrieve the latter, you should specify ConfigurableDateService.class as the
     * type. Unless you are writing a unit test and need to down cast to an implementation, you do not need to cast the result of
     * this method.
     *
     * @param <T>
     * @param type
     * @return an object that has been defined as a bean in our spring context and is of the specified type
     */
    public static <T> T getBean(Class<T> type) {
        verifyProperInitialization();
        T bean = null;
        if (SINGLETON_BEANS_BY_TYPE_CACHE.containsKey(type)) {
            bean = (T) SINGLETON_BEANS_BY_TYPE_CACHE.get(type);
        }
        else {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Bean not already in cache: " + type + " - calling getBeansOfType() ");
            }
            try {
                Collection<T> beansOfType = getBeansOfType(type).values();
                if (beansOfType.size() > 1) {
                    bean = getBean(type, type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1));
                }
                else {
                    bean = beansOfType.iterator().next();
                }
            }
            catch (NoSuchBeanDefinitionException nsbde) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Could not find bean of type " + type.getName() + " - checking KNS context");
                }
                try {
                    bean = KNSServiceLocator.getNervousSystemContextBean(type);
                }
                catch (Exception e) {
                    LOG.error(e);
                    throw new NoSuchBeanDefinitionException("No beans of this type in the in KFS or KNS application contexts: " + type.getName());
                }
            }
            if (SINGLETON_TYPES.contains(type) || hasSingletonSuperType(type)) {
                SINGLETON_TYPES.add(type);
                SINGLETON_BEANS_BY_TYPE_CACHE.put(type, bean);
            }
        }
        return bean;
    }

    /**
     * Use this method to retrieve all beans of a give type in our spring context. Pass in the type of the service interface, NOT
     * the service implementation.
     *
     * @param <T>
     * @param type
     * @return a map of the spring bean ids / beans that are of the specified type
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        verifyProperInitialization();
        Map<String, T> beansOfType = null;
        if (SINGLETON_BEANS_OF_TYPE_CACHE.containsKey(type)) {
            beansOfType = SINGLETON_BEANS_OF_TYPE_CACHE.get(type);
        }
        else {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Bean not already in \"OF_TYPE\" cache: " + type + " - calling getBeansOfType() on KNS and locally");
            }
            boolean allOfTypeAreSingleton = true;
            beansOfType = KNSServiceLocator.getBeansOfType(type);
            for ( String key : beansOfType.keySet() ) {
                if ( !KNSServiceLocator.isSingleton(key) ) {
                    allOfTypeAreSingleton = false;
                }                
            }
            Map<String,T> localBeansOfType = applicationContext.getBeansOfType(type);
            for ( String key : localBeansOfType.keySet() ) {
                if ( !applicationContext.isSingleton(key) ) {
                    allOfTypeAreSingleton = false;
                }                
            }
            beansOfType.putAll( localBeansOfType );
            if ( allOfTypeAreSingleton ) {
                SINGLETON_TYPES.add(type);
                SINGLETON_BEANS_OF_TYPE_CACHE.put(type, beansOfType);
            }
        }
        return beansOfType;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getBean(Class<T> type, String name) {
        verifyProperInitialization();
        T bean = null;
        boolean isSingleton = true;
        if (SINGLETON_BEANS_BY_NAME_CACHE.containsKey(name)) {
            bean = (T) SINGLETON_BEANS_BY_NAME_CACHE.get(name);
        }
        else {
            try {
                bean = (T) applicationContext.getBean(name);
                isSingleton = applicationContext.isSingleton(name);
            }
            catch (NoSuchBeanDefinitionException nsbde) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Could not find bean named " + name + " - checking KNS context");
                }
                try {
                    bean = KNSServiceLocator.getNervousSystemContextBean(type, name);
                }
                catch (Exception e) {
                    LOG.error(e);
                    throw new NoSuchBeanDefinitionException(name, new StringBuffer("No bean of this type and name in the in KFS or KNS application contexts: ").append(type.getName()).append(", ").append(name).toString());
                }
            }
            if ( isSingleton ) {
                SINGLETON_BEANS_BY_NAME_CACHE.put(name, bean);
            }
        }
        return bean;
    }

    private static boolean hasSingletonSuperType(Class type) {
        
        for (Class singletonType : SINGLETON_TYPES) {
            if (singletonType.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    public static List<MethodCacheInterceptor> getMethodCacheInterceptors() {
        List<MethodCacheInterceptor> methodCacheInterceptors = new ArrayList();
        methodCacheInterceptors.add(getBean(MethodCacheInterceptor.class));
        methodCacheInterceptors.add(KNSServiceLocator.getNervousSystemContextBean(MethodCacheInterceptor.class));
        return methodCacheInterceptors;
    }

    protected static Object getBean(String beanName) {
        return getBean(Object.class, beanName);
    }

    protected static String[] getBeanNames() {
        verifyProperInitialization();
        return applicationContext.getBeanDefinitionNames();
    }

    protected static void initializeApplicationContext() {
        initializeApplicationContext(APPLICATION_CONTEXT_DEFINITION, true);
    }

    protected static void initializeBatchApplicationContext() {
        initializeApplicationContext(BATCH_CONTEXT_DEFINITION, true);
    }

    protected static void initializeTestApplicationContext() {
        initializeApplicationContext(TEST_CONTEXT_DEFINITION, false);
    }

    protected static void initializePluginApplicationContext() {
        initializeApplicationContext(PLUGIN_CONTEXT_DEFINITION, false);
    }

    protected static void close() {
        applicationContext.close();
    }

    private static void verifyProperInitialization() {
        if (applicationContext == null) {
            throw new RuntimeException("Spring not initialized properly.  Initialization has begun and the application context is null." + "Probably spring loaded bean is trying to use KNSServiceLocator before the application context is initialized.");
        }
    }

    private static void initializeApplicationContext( String riceInitializationSpringFile, boolean initializeSchedule ) {
        LOG.info( "Starting Spring context initialization" );
        // use the base config file to bootstrap the real application context started by Rice
        new ClassPathXmlApplicationContext(riceInitializationSpringFile);
        // pull the Rice application context into here for further use and efficiency
        applicationContext = RiceResourceLoaderFactory.getSpringResourceLoader().getContext();
        LOG.info( "Completed Spring context initialization" );
        
        SpringCreator.setOverrideBeanFactory(applicationContext.getBeanFactory());
        
        if (Double.valueOf((getBean(KualiConfigurationService.class)).getPropertyString(MEMORY_MONITOR_THRESHOLD_KEY)) > 0) {
            MemoryMonitor.setPercentageUsageThreshold(Double.valueOf((getBean(KualiConfigurationService.class)).getPropertyString(MEMORY_MONITOR_THRESHOLD_KEY)));
            MemoryMonitor memoryMonitor = new MemoryMonitor(APPLICATION_CONTEXT_DEFINITION);
            memoryMonitor.addListener(new MemoryMonitor.Listener() {
                org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MemoryMonitor.class);

                public void memoryUsageLow(String springContextId, Map<String, String> memoryUsageStatistics, String deadlockedThreadIds) {
                    StringBuffer logStatement = new StringBuffer(springContextId).append("\n\tMemory Usage");
                    for (String memoryType : memoryUsageStatistics.keySet()) {
                        logStatement.append("\n\t\t").append(memoryType.toUpperCase()).append(": ").append(memoryUsageStatistics.get(memoryType));
                    }
                    logStatement.append("\n\tLocked Thread Ids: ").append(deadlockedThreadIds).append("\n\tThread Stacks");
                    for (Map.Entry<Thread, StackTraceElement[]> threadStackTrace : Thread.getAllStackTraces().entrySet()) {
                        logStatement.append("\n\t\tThread: name=").append(threadStackTrace.getKey().getName()).append(", id=").append(threadStackTrace.getKey().getId()).append(", priority=").append(threadStackTrace.getKey().getPriority()).append(", state=").append(threadStackTrace.getKey().getState());
                        for (StackTraceElement stackTraceElement : threadStackTrace.getValue()) {
                            logStatement.append("\n\t\t\t" + stackTraceElement);
                        }
                    }
                    LOG.info(logStatement);
                    MemoryMonitor.setPercentageUsageThreshold(0.95);
                }
            });
        }
        if (getBean(KualiConfigurationService.class).getPropertyAsBoolean("use.quartz.scheduling")) {
            try {
                if (initializeSchedule) {
                    LOG.info("Attempting to initialize the scheduler");
                    (getBean(SchedulerService.class)).initialize();
                }
                LOG.info("Starting the scheduler");
                (getBean(Scheduler.class)).start();
            }
            catch (NoSuchBeanDefinitionException e) {
                LOG.info("Not initializing the scheduler because there is no scheduler bean");
            }
            catch (SchedulerException e) {
                LOG.error("Caught exception while starting the scheduler", e);
            }
        }
    }
}
