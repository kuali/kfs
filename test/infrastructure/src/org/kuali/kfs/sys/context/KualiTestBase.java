/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.context;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.OptimisticLockException;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KualiTestConstants;
import org.kuali.kfs.sys.batch.service.CacheService;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.service.ConfigurableDateService;
import org.kuali.rice.core.framework.persistence.jdbc.datasource.XAPoolDataSource;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springmodules.orm.ojb.OjbOperationException;

/**
 * This class should be extended by all Kuali unit tests.
 *
 * @see ConfigureContext
 * @see RelatesTo
 */

public abstract class KualiTestBase extends TestCase implements KualiTestConstants {
    private static final Logger LOG = Logger.getLogger(KualiTestBase.class);
    protected static boolean log4jConfigured = false;
    protected static RuntimeException configurationFailure;
    protected static boolean springContextInitialized = false;
    protected static boolean batchScheduleInitialized = false;
    protected static TransactionStatus transactionStatus;
    protected static UserNameFixture userSessionUsername;
    protected static UserSession userSession;
    protected static Set<String> generatedFiles = new HashSet<String>();

    /**
     * Determines whether to actually run the test using the RelatesTo annotation, onfigures the appropriate context using the
     * ConfigureContext annotation, and logs extra details if the test invocation's OJB operations happen to encounter an
     * OptimisticLockException or if this test has related Jiras.
     *
     * @throws Throwable
     */
    @Override
    public final void runBare() throws Throwable {
        if (!log4jConfigured) {
            Log4jConfigurer.configureLogging(false);
            log4jConfigured = true;
        }
        final String testName = getClass().getName() + "." + getName();

        if ( LOG.isInfoEnabled() ) {
            LOG.info("Entering test '" + testName + "'");
        }
        GlobalVariables.clear();
        ConfigureContext contextConfiguration = getMethod(getName()).getAnnotation(ConfigureContext.class) != null ? getMethod(getName()).getAnnotation(ConfigureContext.class) : getMethod("setUp").getAnnotation(ConfigureContext.class) != null ? getMethod("setUp").getAnnotation(ConfigureContext.class) : getClass().getAnnotation(ConfigureContext.class);
        if (contextConfiguration != null) {
            configure(contextConfiguration);
            SpringContext.getBean(ConfigurableDateService.class).setCurrentDate(new java.util.Date());
        }
        try {
            setUp();
            try {
                runTest();
            }
            catch (OjbOperationException e) {
                // log more detail for OptimisticLockExceptions
                OjbOperationException ooe = e;
                Throwable cause = ooe.getCause();
                if (cause instanceof OptimisticLockException) {
                    OptimisticLockException ole = (OptimisticLockException) cause;
                    StringBuffer message = new StringBuffer("caught OptimisticLockException, caused by ");
                    Object sourceObject = ole.getSourceObject();
                    String suffix = null;
                    try {
                        // try to add instance details
                        suffix = sourceObject.toString();
                    }
                    catch (Exception e2) {
                        // just use the class name
                        suffix = sourceObject.getClass().getName();
                    }
                    message.append(suffix);
                    LOG.error(message.toString());
                }
                throw e;
            }
            finally {
                tearDown();
                if ( springContextInitialized ) {
                    LOG.info( "clearing caches" );
                    clearAllCaches();
                }
            }
        } catch ( ValidationException ex ) {
            fail( "Test threw an unexpected ValidationException: " + dumpMessageMapErrors() );
        } catch (Throwable ex) {
            if ( ex instanceof CannotGetJdbcConnectionException || StringUtils.contains(ex.getMessage(), "GenericPool:checkOut" ) || StringUtils.contains( ex.getMessage(), "no connection available" ) ) {
                LOG.fatal( "UNABLE TO OBTAIN DATABASE CONNECTION!  THIS AND MANY OTHER TESTS WILL LIKELY FAIL!", ex );
                DataSource ds = (DataSource) SpringContext.getBean("datasource");
                if ( ds != null && ds instanceof XAPoolDataSource ) {
                    LOG.fatal( "Datasource Information:" );
                    LOG.fatal( ((XAPoolDataSource)ds).getDataSource().toString() );
                }
                fail( "CONFIGURATION ERROR: UNABLE TO OBTAIN DATABASE CONNECTION!" );
            }
            throw ex;
        } finally {
            if (contextConfiguration != null) {
                endTestTransaction();
            }
            for (String filePath : generatedFiles) {
                try {
                    File file = new File(filePath);
                    if (file.exists()) {
                        file.delete();
                    }
                }
                catch (Exception e) {
                    LOG.warn("Unable to delete file: " + filePath, e);
                }
            }
            generatedFiles.clear();
            GlobalVariables.setUserSession(null);
            GlobalVariables.clear();
            if ( LOG.isInfoEnabled() ) {
                LOG.info("Leaving test '" + testName + "'");
            }
        }
    }

    @CacheEvict(allEntries=true, value = { "" })
    protected void clearAllCaches() {
        SpringContext.getBean(CacheService.class).clearSystemCaches();
    }

    protected void clearBoCache( Class boClass ) {
        SpringContext.getBean(CacheService.class).clearKfsBusinessObjectCache(boClass);
    }

    protected void changeCurrentUser(UserNameFixture sessionUser) throws Exception {
        GlobalVariables.setUserSession(new UserSession(sessionUser.toString()));
    }

    protected void addGeneratedFile(String filePath) {
        generatedFiles.add(filePath);
    }

    /**
     *  Do not call this method!  It is used by the ContinuousIntegrationShutdown "test" to stop the context and clear its state.
     *  Any other use will likely break the CI environment.
     */
    protected void stopSpringContext() {
        if ( springContextInitialized ) {
            try {
                SpringContext.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            springContextInitialized = false;
        }
    }

    private void configure(ConfigureContext contextConfiguration) throws Exception {
        if (configurationFailure != null) {
            throw configurationFailure;
        }
        if (!springContextInitialized) {
            try {
                KFSTestStartup.initializeKfsTestContext();
                springContextInitialized = true;
            }
            catch (RuntimeException e) {
                configurationFailure = e;
                throw e;
            }
        }
        if (!batchScheduleInitialized && contextConfiguration.initializeBatchSchedule()) {
            SpringContext.getBean(SchedulerService.class).initialize();
            batchScheduleInitialized = true;
        }
        if (!contextConfiguration.shouldCommitTransactions()) {
            LOG.info("Starting test transaction that will be rolled back");
            DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
            defaultTransactionDefinition.setTimeout(3600);
//            defaultTransactionDefinition.setReadOnly(true);
            transactionStatus = getTransactionManager().getTransaction(defaultTransactionDefinition);
            transactionStatus.setRollbackOnly();
        }
        else {
            LOG.info("Test transaction not used");
            LOG.info("Starting transaction which will COMMIT at the end of the test" );
            DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
            defaultTransactionDefinition.setTimeout(3600);
            transactionStatus = getTransactionManager().getTransaction(defaultTransactionDefinition);
//            transactionStatus = null;
        }
        UserNameFixture sessionUser = contextConfiguration.session();
        if (sessionUser != UserNameFixture.NO_SESSION) {
            GlobalVariables.setUserSession(new UserSession(sessionUser.toString()));
        }
    }

    private void endTestTransaction() {
        if (transactionStatus != null) {
            if ( transactionStatus.isRollbackOnly() ) {
                LOG.info("rolling back transaction");
                try {
                    getTransactionManager().rollback(transactionStatus);
                }
                catch (Exception ex) {
                    LOG.warn( "Error rolling back transaction", ex );
                }
            } else {
                LOG.info("committing test transaction");
                try {
                    getTransactionManager().commit(transactionStatus);
                } catch (Exception ex) {
                    LOG.warn( "Error committing transaction", ex );
                }
            }
        }
    }

    protected PlatformTransactionManager getTransactionManager() {
        return (PlatformTransactionManager) SpringContext.getService("transactionManager");
    }

    private Method getMethod(String methodName) {
        Class<? extends Object> clazz = getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(methodName);
            }
            catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new RuntimeException("KualiTestBase was unable to getMethod: " + methodName);
    }

    /**
     * This method is used during debugging to dump the contents of the error map, including the key names. It is not used by the
     * application in normal circumstances at all.
     */
    protected String dumpMessageMapErrors() {
        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            return "";
        }

        StringBuilder message = new StringBuilder();
        for ( String key : GlobalVariables.getMessageMap().getErrorMessages().keySet() ) {
            List<ErrorMessage> errorList = GlobalVariables.getMessageMap().getErrorMessages().get(key);

            for ( ErrorMessage em : errorList ) {
                message.append(key).append(" = ").append( em.getErrorKey() );
                if (em.getMessageParameters() != null) {
                    message.append( " : " );
                    String delim = "";
                    for ( String parm : em.getMessageParameters() ) {
                        message.append(delim).append("'").append(parm).append("'");
                        if ("".equals(delim)) {
                            delim = ", ";
                        }
                    }
                }
            }
            message.append( '\n' );
        }
        return message.toString();
    }
}
