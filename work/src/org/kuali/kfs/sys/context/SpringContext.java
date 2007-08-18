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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.lookup.LookupableHelperService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.MemoryMonitor;
import org.kuali.core.util.cache.MethodCacheInterceptor;
import org.kuali.kfs.batch.BatchInputFileSetType;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.batch.JobDescriptor;
import org.kuali.kfs.batch.Step;
import org.kuali.kfs.batch.TriggerDescriptor;
import org.kuali.kfs.service.SchedulerService;
import org.kuali.module.chart.bo.OrganizationReversionCategory;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;
import org.kuali.rice.KNSServiceLocator;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import uk.ltd.getahead.dwr.create.SpringCreator;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;

public class SpringContext {
    private static final Logger LOG = Logger.getLogger(SpringContext.class);
    private static final String APPLICATION_CONTEXT_DEFINITION = "SpringBeans.xml";
    private static final String SPRING_SOURCE_FILES_KEY = "spring.source.files";
    private static final String SPRING_TEST_FILES_KEY = "spring.test.files";
    private static final String MEMORY_MONITOR_THRESHOLD_KEY = "memory.monitor.threshold";
    private static ConfigurableApplicationContext applicationContext;

    /**
     * Use this method to retrieve a spring bean when one of the following is the case. Pass in the type of the service interface,
     * NOT the service implementation.
     * 1. there is only one bean of the specified type in our spring context
     * 2. there is only one bean of the specified type in our spring context, but you want the one whose bean id is the same as 
     * type.getSimpleName() with the exception of the first letter being lower case in the former and upper case in the latter,
     * 
     * For example, there are two beans of type DateTimeService in our context – dateTimeService and testDateTimeService.  To
     * retrieve the former, you should specific DateTimeService.class as the type.  To retrieve the latter, you should specify
     * ConfigurableDateService.class as the type.
     * 
     * Unless you are writing a unit test and need to down cast to an implementation, you do not need to cast the result of this method.
     * 
     * @param <T>
     * @param type
     * @return an object that has been defined as a bean in our spring context and is of the specified type
     */
    public static <T> T getBean(Class<T> type) {
        verifyProperInitialization();
        try {
            Collection<T> beansOfType = getBeansOfType(type).values();
            if (beansOfType.size() > 1) {
                return getBean(type, type.getSimpleName().substring(0, 1).toLowerCase() + type.getSimpleName().substring(1));
            }
            return beansOfType.iterator().next();
        }
        catch (NoSuchBeanDefinitionException nsbde) {
            LOG.info("Could not find bean of type " + type.getName() + " - checking KNS context");
            try {
                return KNSServiceLocator.getBean(type);
            }
            catch (Exception e) {
                LOG.error(e);
                throw new NoSuchBeanDefinitionException("No beans of this type in the in KFS or KNS application contexts: " + type.getName());
            }
        }
    }

    /**
     * Use this method to retrieve all beans of a give type in our spring context.  Pass in the type of the service interface,
     * NOT the service implementation.
     * 
     * @param <T>
     * @param type
     * @return a map of the spring bean ids / beans that are of the specified type
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        verifyProperInitialization();
        Map<String, T> beansOfType = KNSServiceLocator.getBeansOfType(type);
        beansOfType.putAll(new HashMap(applicationContext.getBeansOfType(type)));
        return beansOfType;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getBean(Class<T> type, String name) {
        verifyProperInitialization();
        try {
            return (T) applicationContext.getBean(name);
        }
        catch (NoSuchBeanDefinitionException nsbde) {
            LOG.info("Could not find bean named " + name + " - checking KNS context");
            try {
                return KNSServiceLocator.getBean(type, name);
            }
            catch (Exception e) {
                LOG.error(e);
                throw new NoSuchBeanDefinitionException(name, new StringBuffer("No bean of this type and name in the in KFS or KNS application contexts: ").append(type.getName()).append(", ").append(name).toString());
            }
        }
    }

    protected static List<MethodCacheInterceptor> getMethodCacheInterceptors() {
        List<MethodCacheInterceptor> methodCacheInterceptors = new ArrayList();
        methodCacheInterceptors.add(getBean(MethodCacheInterceptor.class));
        methodCacheInterceptors.add(KNSServiceLocator.getBean(MethodCacheInterceptor.class));
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
        initializeApplicationContext(getSpringConfigurationFiles(new String[] { SPRING_SOURCE_FILES_KEY }), true);
    }

    protected static void initializeTestApplicationContext() {
        initializeApplicationContext(getSpringConfigurationFiles(new String[] { SPRING_SOURCE_FILES_KEY, SPRING_TEST_FILES_KEY }), false);
    }

    protected static void close() {
        applicationContext.close();
    }

    protected static String getStringConfigurationProperty(String propertyName) {
        return ResourceBundle.getBundle(PropertyLoadingFactoryBean.CONFIGURATION_FILE_NAME).getString(propertyName);
    }

    protected static List<String> getListConfigurationProperty(String propertyName) {
        return Arrays.asList(getStringConfigurationProperty(propertyName).split(","));
    }

    private static void verifyProperInitialization() {
        if (applicationContext == null) {
            throw new RuntimeException("Spring not initialized properly.  Initialization has begun and the application context is null." + "Probably spring loaded bean is trying to use KNSServiceLocator before the application context is initialized.");
        }
    }

    private static String[] getSpringConfigurationFiles(String[] propertyNames) {
        List<String> springConfigurationFiles = new ArrayList<String>();
        springConfigurationFiles.add(APPLICATION_CONTEXT_DEFINITION);
        for (int i = 0; i < propertyNames.length; i++) {
            springConfigurationFiles.addAll(getListConfigurationProperty(propertyNames[i]));
        }
        return springConfigurationFiles.toArray(new String[] {});
    }

    private static void initializeApplicationContext(String[] springFiles, boolean initializeSchedule) {
        applicationContext = new ClassPathXmlApplicationContext(springFiles);
        if (Double.valueOf((getBean(KualiConfigurationService.class)).getPropertyString(MEMORY_MONITOR_THRESHOLD_KEY)) > 0) {
            ManagementFactory.getThreadMXBean().setThreadContentionMonitoringEnabled(true);
            ManagementFactory.getThreadMXBean().setThreadCpuTimeEnabled(true);
            MemoryMonitor.setPercentageUsageThreshold(Double.valueOf((getBean(KualiConfigurationService.class)).getPropertyString(MEMORY_MONITOR_THRESHOLD_KEY)));
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
        SpringCreator.setOverrideBeanFactory(applicationContext.getBeanFactory());
    }
}
