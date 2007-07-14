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
package org.kuali.kfs.context;

import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.AssertionUtils;
import org.kuali.core.util.MemoryMonitor;
import org.kuali.core.util.cache.MethodCacheInterceptor;
import org.kuali.core.util.spring.NamedOrderedListBean;
import org.kuali.kfs.service.SchedulerService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.core.Core;
import org.kuali.rice.resourceloader.BaseResourceLoader;
import org.kuali.rice.resourceloader.GlobalResourceLoader;
import org.kuali.rice.resourceloader.ResourceLoader;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import uk.ltd.getahead.dwr.create.SpringCreator;
import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.plugin.PluginClassLoader;

public class SpringContext {
    private static final Logger LOG = Logger.getLogger(SpringServiceLocator.class);
    private static final String APPLICATION_CONTEXT_DEFINITION = "SpringBeans.xml";
    private static final String SPRING_SOURCE_FILES_KEY = "spring.source.files";
    private static final String SPRING_TEST_FILES_KEY = "spring.test.files";
    private static final String MEMORY_MONITOR_THRESHOLD_KEY = "memory.monitor.threshold";
    private static SpringContext instance = new SpringContext();
    private ConfigurableApplicationContext applicationContext;

    public static Object getBean(String beanName) {
        if (hideSpringFromTestsMessage != null) {
            throw new RuntimeException(hideSpringFromTestsMessage);
        }
        if (instance.applicationContext == null) {
            throw new RuntimeException("Spring not initialized properly.  Initialization has begun and the application context is null." + "Probably spring loaded bean is trying to use KNSServiceLocator before the application context is initialized.");
        }
        try {
            return instance.applicationContext.getBean(beanName);
        }
        catch (NoSuchBeanDefinitionException nsbde) {
            LOG.info("Could not find bean named " + beanName + " - checking KNS context");
            try {
                return KNSServiceLocator.getService(beanName);
            }
            catch (Exception e) {
                LOG.error(e);
                throw new NoSuchBeanDefinitionException(beanName, "Unable to locate service in KFS or KNS");
            }
        }
    }
    
    public static <T> List<T> getBeansOfType(Class<T> type) {
        return new ArrayList(instance.applicationContext.getBeansOfType(type).values());
    }
    
    public static List<NamedOrderedListBean> getNamedOrderedListBeans(String listName) {
        return KNSServiceLocator.getNamedOrderedListBeans(listName, instance.applicationContext);
    }
    
    protected static String[] getBeanNames() {
        return instance.applicationContext.getBeanDefinitionNames();
    }
    
    protected static void initializeApplicationContext() {
        initializeApplicationContext(getSpringConfigurationFiles(new String [] {SPRING_SOURCE_FILES_KEY}));
    }

    protected static void close() {
        instance.applicationContext.close();
    }
    
    protected static String getStringConfigurationProperty(String propertyName) {
        return ResourceBundle.getBundle(PropertyLoadingFactoryBean.CONFIGURATION_FILE_NAME).getString(propertyName);
    }
    
    protected static List<String> getListConfigurationProperty(String propertyName) {
        return Arrays.asList(getStringConfigurationProperty(propertyName).split(","));
    }
    
    private static String[] getSpringConfigurationFiles(String[] propertyNames) {
        List<String> springConfigurationFiles = new ArrayList<String>();
        springConfigurationFiles.add(APPLICATION_CONTEXT_DEFINITION);
        for (int i = 0; i < propertyNames.length; i++) {
            springConfigurationFiles.addAll(getListConfigurationProperty(propertyNames[i]));
        }
        return springConfigurationFiles.toArray(new String[] {});
    }
        
    private static void initializeApplicationContext(String[] springFiles) {
        instance.applicationContext = new ClassPathXmlApplicationContext(springFiles);

        Log4jConfigurer.completeStartupLogging();

        if (Double.valueOf(((KualiConfigurationService)getBean(KNSServiceLocator.KUALI_CONFIGURATION_SERVICE)).getPropertyString(MEMORY_MONITOR_THRESHOLD_KEY)) > 0) {
            ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
            ManagementFactory.getThreadMXBean().setThreadCpuTimeEnabled(true);
            MemoryMonitor.setPercentageUsageThreshold(Double.valueOf(((KualiConfigurationService)getBean(KNSServiceLocator.KUALI_CONFIGURATION_SERVICE)).getPropertyString(MEMORY_MONITOR_THRESHOLD_KEY)));
            MemoryMonitor memoryMonitor = new MemoryMonitor(APPLICATION_CONTEXT_DEFINITION);
            memoryMonitor.addListener(new MemoryMonitor.Listener() {
                org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MemoryMonitor.class);

                public void memoryUsageLow(String springContextId, long usedMemory, long maxMemory) {
                    LOG.info(new StringBuffer(springContextId).append("$ percent memory used = ").append(((double) usedMemory) / maxMemory).append(" / locked thread ids: ").append(Arrays.toString(ManagementFactory.getThreadMXBean().findMonitorDeadlockedThreads())));
                    Map<Thread, StackTraceElement[]> st = Thread.getAllStackTraces();
                    for (Map.Entry<Thread, StackTraceElement[]> e : st.entrySet()) {
                        StackTraceElement[] el = e.getValue();
                        Thread t = e.getKey();
                        StringBuffer sb = new StringBuffer("\n").append(springContextId).append("$ ").append("thread: name=").append(t.getName()).append(", id=").append(t.getId()).append(", priority=").append(t.getPriority()).append(", state=").append(t.getState());
                        for (StackTraceElement line : el) {
                            sb.append("\n\tstack trace element: " + line);
                        }
                        LOG.info(sb);
                    }
                    MemoryMonitor.setPercentageUsageThreshold(0.95);
                }
            });
        }

        if (((KualiConfigurationService)getBean(KNSServiceLocator.KUALI_CONFIGURATION_SERVICE)).getPropertyAsBoolean("use.quartz.scheduling")) {
            try {
                LOG.info("Attempting to initialize the scheduler");
                ((SchedulerService)getBean(SpringServiceLocator.SCHEDULER_SERVICE)).initialize();
                LOG.info("Starting the scheduler");
                ((Scheduler) getBean("scheduler")).start();
            }
            catch (NoSuchBeanDefinitionException e) {
                LOG.info("Not initializing the scheduler because there is no scheduler bean");
            }
            catch (SchedulerException e) {
                LOG.error("Caught exception while starting the scheduler", e);
            }
        }

        SpringCreator.setOverrideBeanFactory(instance.applicationContext.getBeanFactory());
    }


    /**
     * unit test framework methods
     */

    private static TransactionStatus TRANSACTION_STATUS;
    private static RuntimeException INITIALIZATION_FAILURE;
    private static boolean isTestContextInitialized = false;
    private static String hideSpringFromTestsMessage = null;

    protected static void initializeTestApplicationContext(boolean useTestTransaction) {
        if (INITIALIZATION_FAILURE != null) {
            throw INITIALIZATION_FAILURE;
        }
        if (!isTestContextInitialized) {
            try {
                initializeApplicationContext(getSpringConfigurationFiles(new String[] {SPRING_SOURCE_FILES_KEY, SPRING_TEST_FILES_KEY}));
            }
            catch (RuntimeException e) {
                // settting a flag because JUnit will blithely go on restarting Spring once for each test,
                // even if Spring can't be initialized; with this flag set, I can at least short-circuit
                // the process of getting to failure
                INITIALIZATION_FAILURE = e;
                throw e;
            }
            setSynchronousWorkflow(true);
            // Initialization takes a very long time, so do it only once.
            isTestContextInitialized = true;
        }
        if ((TRANSACTION_STATUS != null) && !TRANSACTION_STATUS.isCompleted()) {
            throw new RuntimeException("Transaction from prior test was not ended.");
        }
        hideSpringFromTestsMessage = null;
        if (useTestTransaction) {
            LOG.info("starting test transaction");
            startTestTransaction();
        }
        else {
            LOG.info("test transaction not used");
            TRANSACTION_STATUS = null;
        }
    }
    
    protected static void transitionTestApplicationContext(String hideSpringFromTestsMessage) {
        endTestTransaction();
        // Prevent subsequent tests from using this framework without proper initialization, so they can also succeed when run
        // individually.
        AssertionUtils.assertThat(hideSpringFromTestsMessage != null);
        SpringContext.hideSpringFromTestsMessage = hideSpringFromTestsMessage;
    }

    protected static boolean testTransactionIsRollbackOnly() {
        return (TRANSACTION_STATUS != null) && (TRANSACTION_STATUS.isRollbackOnly());
    }
    
    protected static boolean methodIsCached(Method method, Object[] arguments) {
        MethodCacheInterceptor kfsMethodCacheInterceptor = (MethodCacheInterceptor) getBean(KNSServiceLocator.METHOD_CACHE_INTERCEPTOR);
        MethodCacheInterceptor knsMethodCacheInterceptor = KNSServiceLocator.getMethodCacheInterceptor();
        String cacheKey = knsMethodCacheInterceptor.buildCacheKey(method.toString(), arguments);
        return kfsMethodCacheInterceptor.containsCacheKey(cacheKey) || knsMethodCacheInterceptor.containsCacheKey(cacheKey);
    }
    
    protected static void removeCachedMethod(Method method, Object[] arguments) {
        MethodCacheInterceptor kfsMethodCacheInterceptor = (MethodCacheInterceptor) getBean(KNSServiceLocator.METHOD_CACHE_INTERCEPTOR);
        MethodCacheInterceptor knsMethodCacheInterceptor = KNSServiceLocator.getMethodCacheInterceptor();
        String cacheKey = knsMethodCacheInterceptor.buildCacheKey(method.toString(), arguments);
        if (kfsMethodCacheInterceptor.containsCacheKey(cacheKey)) {
            kfsMethodCacheInterceptor.removeCacheKey(cacheKey);
        }
        if (knsMethodCacheInterceptor.containsCacheKey(cacheKey)) {
            knsMethodCacheInterceptor.removeCacheKey(cacheKey);
        }
    }

    private static void setSynchronousWorkflow(boolean synchronous) {
        String persistence = synchronous ? EdenConstants.MESSAGING_SYNCHRONOUS : null;
        Core.getCurrentContextConfig().overrideProperty(EdenConstants.MESSAGE_PERSISTENCE, persistence);
        // This is a real drag, because of the fake hierarchy that is being modeled in our configuration system, we have to manually
        // walk down
        // to the Embedded Plugin's configuration and set the synchronous message persistence as well, ugh.
        //
        // See http://beatles.uits.indiana.edu:8081/jira/browse/EN-683
        //
        ResourceLoader resourceLoader = GlobalResourceLoader.getResourceLoader(new QName(Core.getCurrentContextConfig().getMessageEntity(), "embedded"));
        if (resourceLoader instanceof BaseResourceLoader) {
            ClassLoader classLoader = ((BaseResourceLoader) resourceLoader).getClassLoader();
            if (classLoader instanceof PluginClassLoader) {
                ((PluginClassLoader) classLoader).getConfig().overrideProperty(EdenConstants.MESSAGE_PERSISTENCE, persistence);
            }
        }
    }

    private static void startTestTransaction() {
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setTimeout(30);
        TRANSACTION_STATUS = KNSServiceLocator.getTransactionManager().getTransaction(defaultTransactionDefinition);
    }

    private static void endTestTransaction() {
        if (TRANSACTION_STATUS != null) {
            LOG.info("rolling back test transaction");
            KNSServiceLocator.getTransactionManager().rollback(TRANSACTION_STATUS);
        }
    }
}
