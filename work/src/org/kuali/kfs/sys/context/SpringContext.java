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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.MemoryMonitor;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.core.config.RiceConfigurer;
import org.kuali.rice.core.database.XAPoolDataSource;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.resourceloader.RiceResourceLoaderFactory;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.cache.MethodCacheInterceptor;
import org.kuali.rice.kns.util.spring.ClassPathXmlApplicationContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ConfigurableApplicationContext;

import uk.ltd.getahead.dwr.create.SpringCreator;

public class SpringContext {
    protected static final Logger LOG = Logger.getLogger(SpringContext.class);
    protected static final String APPLICATION_CONTEXT_DEFINITION = "spring-rice-startup.xml";
    protected static final String TEST_CONTEXT_DEFINITION = "spring-rice-startup-test.xml";
    protected static final String MEMORY_MONITOR_THRESHOLD_KEY = "memory.monitor.threshold";
    protected static final String USE_QUARTZ_SCHEDULING_KEY = "use.quartz.scheduling";
    protected static ConfigurableApplicationContext applicationContext;
    protected static Set<Class<? extends Object>> SINGLETON_TYPES = new HashSet<Class<? extends Object>>();
    protected static Set<String> SINGLETON_NAMES = new HashSet<String>();
    protected static Map<Class<? extends Object>, Object> SINGLETON_BEANS_BY_TYPE_CACHE = new HashMap<Class<? extends Object>, Object>();
    protected static Map<String, Object> SINGLETON_BEANS_BY_NAME_CACHE = new HashMap<String, Object>();
    protected static Map<Class<? extends Object>, Map> SINGLETON_BEANS_OF_TYPE_CACHE = new HashMap<Class<? extends Object>, Map>();

    /**
     * Use this method to retrieve a service which may or may not be implemented locally.  (That is,
     * defined in the main Spring ApplicationContext created by Rice.
     */
    public static Object getService(String serviceName) {
        return GlobalResourceLoader.getService(serviceName);
    }
    
    /**
     * Use this method to retrieve a spring bean when one of the following is the case. Pass in the type of the service interface,
     * NOT the service implementation. 1. there is only one bean of the specified type in our spring context 2. there is only one
     * bean of the specified type in our spring context, but you want the one whose bean id is the same as type.getSimpleName() with
     * the exception of the first letter being lower case in the former and upper case in the latter, For example, there are two
     * beans of type DateTimeService in our context � dateTimeService and testDateTimeService. To retrieve the former, you should
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
        } else {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("Bean not already in cache: " + type + " - calling getBeansOfType() ");
            }
            Collection<T> beansOfType = getBeansOfType(type).values();
            if ( !beansOfType.isEmpty() ) {
                if (beansOfType.size() > 1) {
                    bean = getBean(type, StringUtils.uncapitalize(type.getSimpleName()) );
                } else {
                    bean = beansOfType.iterator().next();
                }
            } else { // unable to find bean - check GRL
                // this is needed in case no beans of the given type exist locally
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Bean not found in local context: " + type.getName() + " - calling GRL");
                }
                Object remoteServiceBean = getService( StringUtils.uncapitalize(type.getSimpleName()) );
                if ( remoteServiceBean != null ) {
                    if ( type.isAssignableFrom( remoteServiceBean.getClass() ) ) {
                        bean = (T)remoteServiceBean;
                    }
                }
            }
            if ( bean != null ) {
                if (SINGLETON_TYPES.contains(type) || hasSingletonSuperType(type)) {
                    SINGLETON_TYPES.add(type);
                    SINGLETON_BEANS_BY_TYPE_CACHE.put(type, bean);
                }
            } else {
                throw new RuntimeException( "Request for non-existent bean.  Unable to find in local context on on the GRL: " + type.getName() );
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
                LOG.debug("Bean not already in \"OF_TYPE\" cache: " + type + " - calling getBeansOfType() on Spring context");
            }
            boolean allOfTypeAreSingleton = true;
            beansOfType = applicationContext.getBeansOfType(type);
            for ( String key : beansOfType.keySet() ) {
                if ( !applicationContext.isSingleton(key) ) {
                    allOfTypeAreSingleton = false;
                }                
            }
            if ( allOfTypeAreSingleton ) {
                SINGLETON_TYPES.add(type);
                SINGLETON_BEANS_OF_TYPE_CACHE.put(type, beansOfType);
            }
        }
        return beansOfType;
    }

    @SuppressWarnings("unchecked")
    private static <T> T getBean(Class<T> type, String name) {
        T bean = null;
        if (SINGLETON_BEANS_BY_NAME_CACHE.containsKey(name)) {
            bean = (T) SINGLETON_BEANS_BY_NAME_CACHE.get(name);
        } else {
            try {
                bean = (T) applicationContext.getBean(name);
                if ( applicationContext.isSingleton(name) ) {
                    SINGLETON_BEANS_BY_NAME_CACHE.put(name, bean);
                }
            }
            catch (NoSuchBeanDefinitionException nsbde) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Bean with name and type not found in local context: " + name + "/" + type.getName() + " - calling GRL");
                }
                Object remoteServiceBean = getService( name );
                if ( remoteServiceBean != null ) {
                    if ( type.isAssignableFrom( remoteServiceBean.getClass() ) ) {
                        bean = (T)remoteServiceBean;
                        // assume remote beans are services and thus singletons
                        SINGLETON_BEANS_BY_NAME_CACHE.put(name, bean);
                    }
                }
                throw new RuntimeException("No bean of this type and name exist in the application context or from the GRL: " + type.getName() + ", " + name);
            }
        }
        return bean;
    }

    private static boolean hasSingletonSuperType(Class<? extends Object> type) {
        for (Class<? extends Object> singletonType : SINGLETON_TYPES) {
            if (singletonType.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    public static List<MethodCacheInterceptor> getMethodCacheInterceptors() {
        List<MethodCacheInterceptor> methodCacheInterceptors = new ArrayList<MethodCacheInterceptor>();
        methodCacheInterceptors.add(getBean(MethodCacheInterceptor.class));
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
        initializeApplicationContext(APPLICATION_CONTEXT_DEFINITION, true);
    }

    protected static void initializeTestApplicationContext() {
        initializeApplicationContext(TEST_CONTEXT_DEFINITION, false);
    }

    protected static void close() throws Exception {
        if ( applicationContext == null ) {
            applicationContext = RiceResourceLoaderFactory.getSpringResourceLoader().getContext();
        }
        RiceConfigurer riceConfigurer = null;
        try {
            riceConfigurer = (RiceConfigurer) applicationContext.getBean( "rice" );
        } catch ( Exception ex ) {
            LOG.debug( "Unable to get 'rice' bean - attempting to get from the Rice ConfigContext", ex );
            riceConfigurer = (RiceConfigurer)ConfigContext.getObjectFromConfigHierarchy(RiceConstants.RICE_CONFIGURER_CONFIG_NAME);
        }
        if ( riceConfigurer != null ) {
            riceConfigurer.destroy();
            ConfigContext.destroy();
        } else {
            LOG.error( "Unable to close SpringContext - unable to get a handle to a RiceConfigurer object." );
        }
    }

    private static void verifyProperInitialization() {
        if (applicationContext == null) {
            throw new RuntimeException("Spring not initialized properly.  Initialization has begun and the application context is null.  Probably spring loaded bean is trying to use KNSServiceLocator before the application context is initialized.");
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
                    if ( LOG.isInfoEnabled() ) {
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
                    }
                    MemoryMonitor.setPercentageUsageThreshold(0.95);
                }
            });
        }
        if (getBean(KualiConfigurationService.class).getPropertyAsBoolean(USE_QUARTZ_SCHEDULING_KEY)) {
            try {
                if (initializeSchedule) {
                    LOG.info("Attempting to initialize the scheduler");
                    (getBean(SchedulerService.class)).initialize();
                }
                LOG.info("Starting the scheduler");
                try {
                    (getBean(Scheduler.class)).start();
                } catch ( NullPointerException ex ) {
                    LOG.error("Caught NPE while starting the scheduler", ex);
                }
            }
            catch (NoSuchBeanDefinitionException e) {
                LOG.info("Not initializing the scheduler because there is no scheduler bean");
            }
            catch (SchedulerException e) {
                LOG.error("Caught exception while starting the scheduler", e);
            }
        }
        
        if ( getBean(KualiConfigurationService.class).getPropertyAsBoolean( "periodic.thread.dump" ) ) {
            if ( LOG.isInfoEnabled() ) {
                LOG.info( "Starting the Periodic Thread Dump thread - dumping every " + getBean(KualiConfigurationService.class).getPropertyString("periodic.thread.dump.seconds") + " seconds");
            }
            Runnable processWatch = new Runnable() {
                DateFormat df = new SimpleDateFormat( "yyyyMMdd" );
                DateFormat tf = new SimpleDateFormat( "HH-mm-ss" );
                long sleepPeriod = Long.parseLong( getBean(KualiConfigurationService.class).getPropertyString("periodic.thread.dump.seconds") ) * 1000;
                public void run() {
                    File logDir = new File( getBean(KualiConfigurationService.class).getPropertyString( "logs.directory" ) );
                    File monitoringLogDir = new File( logDir, "monitoring" );
                    if ( !monitoringLogDir.exists() ) {
                        monitoringLogDir.mkdir();
                    }
                    while ( true ) {
                        File todaysLogDir = new File( monitoringLogDir, df.format(new Date()) );
                        if ( !todaysLogDir.exists() ) {
                            todaysLogDir.mkdir();
                        }
                        File logFile = new File( todaysLogDir, "process-"+tf.format(new Date())+".log" );
                        try {
                            BufferedWriter w = new BufferedWriter( new FileWriter( logFile ) );
                            StringBuffer logStatement = new StringBuffer(10240);
                            logStatement.append("Threads Running at: " ).append( new Date() ).append( "\n\n\n" );
                            Map<Thread,StackTraceElement[]> threads = Thread.getAllStackTraces();
                            List<Thread> sortedThreads = new ArrayList<Thread>( threads.keySet() );
                            Collections.sort( sortedThreads, new Comparator<Thread>() {
                                public int compare(Thread o1, Thread o2) {
                                    return o1.getName().compareTo( o2.getName() );
                                }
                            });
                            for ( Thread t : sortedThreads ) {
                                logStatement.append("\tThread: name=").append(t.getName())
                                        .append(", id=").append(t.getId())
                                        .append(", priority=").append(t.getPriority())
                                        .append(", state=").append(t.getState());
                                logStatement.append('\n');
                                for (StackTraceElement stackTraceElement : threads.get(t) ) {
                                    logStatement.append("\t\t" + stackTraceElement).append( '\n' );
                                }
                                logStatement.append('\n');
                            }
                            Object ds = getBean("dataSource");
                            if ( ds != null && ds instanceof XAPoolDataSource ) {
                                logStatement.append( "-----------------------------------------------\n" );
                                logStatement.append( "Datasource Information:\n" );
                                logStatement.append( ((XAPoolDataSource)ds).getDataSource().toString() ).append( '\n' );
                                try {
                                    logStatement.append( "-----------------------------------------------\n" );
                                    logStatement.append( "Active Connection SQL Dump:\n" );
                                    if ( ((XAPoolDataSource)ds).getDriverClassName().contains("Oracle") ) {
                                        String sql = "  SELECT " +
                                                "              sess.USERNAME \r\n" + 
//                                        		" ,            process.SPID \r\n" + 
//                                        		" ,            sess.SID \r\n" + 
//                                        		" ,            sess.SERIAL## AS serial \r\n" + 
                                        		" ,            sess.STATUS \r\n" + 
                                        		" ,            TO_DATE( sql.FIRST_LOAD_TIME, 'YYYY-MM-DD/HH24:MI:SS' ) AS first_load_time\r\n" + 
//                                        		" ,            sql.OPTIMIZER_MODE \r\n" + 
                                        		" ,            sql.EXECUTIONS \r\n" + 
                                        		" ,            sql.DISK_READS \r\n" + 
                                        		" ,            sql.BUFFER_GETS \r\n" + 
                                        		" ,            sql.ROWS_PROCESSED \r\n" + 
                                        		" ,            sql.OPTIMIZER_COST \r\n" + 
                                                " ,            sql.SQL_TEXT \r\n" + 
//                                        		" ,            sql.MODULE, \r\n" + 
//                                        		" ,            sql.loads,\r\n" + 
//                                        		" ,            sql.invalidations,\r\n" + 
//                                        		" ,            sess.MACHINE, \r\n" + 
//                                        		" ,            sess.TERMINAL, \r\n" + 
//                                        		" ,            sess.PROGRAM, \r\n" + 
//                                        		" ,            sess.OSUSER,\r\n" + 
//                                        		" ,            RAWTOHEX( sess.SQL_ADDRESS ) SQL_ADDRESS\r\n" + 
                                        		"        FROM    SYS.V_$SESSION      sess, \r\n" + 
                                        		"                SYS.V_$PROCESS      process, \r\n" + 
                                        		"                SYS.V_$SQL          sql\r\n" + 
                                        		"        WHERE sess.USERNAME IS NOT NULL\r\n" + 
                                        		"          AND sql.SQL_TEXT NOT LIKE '%SYS.V_$%'\r\n" + 
                                        		"          AND sess.STATUS<>'KILLED'\r\n" + 
                                        		"          AND sess.SQL_ADDRESS=sql.ADDRESS\r\n" + 
                                        		"          AND sess.PADDR=process.ADDR\r\n" + 
                                        		"            AND sess.status = 'ACTIVE'\r\n" + 
                                        		"        ORDER BY sess.STATUS ASC,\r\n" + 
                                        		"                    sess.USERNAME ASC,\r\n" + 
                                        		"                    sql.sql_text ASC\r\n";
                                        java.sql.Connection con = ((XAPoolDataSource)ds).getConnection();
                                        try {
                                            Statement stmt = con.createStatement();
                                            ResultSet rs = stmt.executeQuery(sql);
                                            ResultSetMetaData md = rs.getMetaData();
                                            logStatement.append( "Columns: " );
                                            for ( int i = 1; i < md.getColumnCount(); i++ ) {
                                                logStatement.append( md.getColumnName(i) ).append( "|" );
                                            }
                                            while ( rs.next() ) {
                                                logStatement.append( "Statement Info: " );
                                                for ( int i = 1; i < md.getColumnCount(); i++ ) {
                                                    logStatement.append( rs.getString(i) ).append( "|" );
                                                }
                                                logStatement.append( "\nSQL Text: " + rs.getString(md.getColumnCount()) );
                                                logStatement.append( "\n\n" );
                                            }
                                            rs.close();
                                            stmt.close();
                                        } finally {
                                            if ( con != null ) {
                                                con.close();
                                            }
                                        }
                                    } else {
                                        logStatement.append( "Not Running - don't have MySQL specific code.\n" );
                                    }
                                }
                                catch (SQLException e) {
                                    LOG.warn( "Unable to pull current connection SQL: " + e.getMessage() );
                                }
                            }
                            w.write(logStatement.toString());
                            w.close();
                        } catch ( IOException ex ) {
                            LOG.error( "Unable to write the ProcessWatch output file: " + logFile.getAbsolutePath(), ex );
                        }
                        try {
                            Thread.sleep( sleepPeriod );
                        } catch ( InterruptedException ex ) {
                            LOG.error( "woken up during sleep of the ProcessWatch thread", ex );
                        }
                    }
                }
            };
            Thread t = new Thread( processWatch, "ProcessWatch thread" );
            t.setDaemon(true);
            t.start();
        }        
    }
}
