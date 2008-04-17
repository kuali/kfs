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

import static org.kuali.test.suite.JiraRelatedSuite.State.OPEN_OR_IN_PROGRESS_OR_REOPENED;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.OptimisticLockException;
import org.kuali.core.UserSession;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.cache.MethodCacheInterceptor;
import org.kuali.kfs.service.SchedulerService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.KualiTestConstants;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.test.suite.JiraRelatedSuite;
import org.kuali.test.suite.RelatesTo;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springmodules.orm.ojb.OjbOperationException;

import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * This class should be extended by all Kuali unit tests.
 * 
 * @see ConfigureContext
 * @see RelatesTo
 */

public abstract class KualiTestBase extends TestCase implements KualiTestConstants {
    private static final Logger LOG = Logger.getLogger(KualiTestBase.class);
    public static final String SKIP_OPEN_OR_IN_PROGRESS_OR_REOPENED_JIRA_ISSUES = "org.kuali.test.KualiTestBase.skipOpenOrInProgressOrReopenedJiraIssues";
    private static boolean log4jConfigured = false;
    private static RuntimeException configurationFailure;
    private static boolean springContextInitialized = false;
    private static boolean batchScheduleInitialized = false;
    private static TransactionStatus transactionStatus;
    private static UserNameFixture userSessionUsername;
    protected static UserSession userSession;

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
        if (System.getProperty(SKIP_OPEN_OR_IN_PROGRESS_OR_REOPENED_JIRA_ISSUES) != null) {
            Set<RelatesTo.JiraIssue> openOrInProgressOrReopened = JiraRelatedSuite.getMatchingIssues(getRelatedJiraIssues(), OPEN_OR_IN_PROGRESS_OR_REOPENED);
            if (!openOrInProgressOrReopened.isEmpty()) {
                LOG.info("Skipping test '" + testName + "' because of JIRA issues open or in progress or reopened: " + openOrInProgressOrReopened);
                return; // let this test method pass without running it
            }
        }

        LOG.info("Entering test '" + testName + "'");
        GlobalVariables.setErrorMap(new ErrorMap());
        ConfigureContext contextConfiguration = getMethod(getName()).getAnnotation(ConfigureContext.class) != null ? getMethod(getName()).getAnnotation(ConfigureContext.class) : getMethod("setUp").getAnnotation(ConfigureContext.class) != null ? getMethod("setUp").getAnnotation(ConfigureContext.class) : getClass().getAnnotation(ConfigureContext.class);
        if (contextConfiguration != null) {
            configure(contextConfiguration);
        }
        try {
            setUp();
            try {
                runTest();
            }
            catch (OjbOperationException e) {
                // log more detail for OptimisticLockExceptions
                OjbOperationException ooe = (OjbOperationException) e;
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
                    LOG.info( "clearing method cache" );
                    clearMethodCache();
                }
            }
        }
        catch (Throwable t) {
            Set<RelatesTo.JiraIssue> issues = getRelatedJiraIssues();
            if (issues.isEmpty()) {
                throw t;
            }
            else {
                throw new Exception("JIRA issues thought to be related to this test not passing: " + issues, t);
            }
        }
        finally {
            if (contextConfiguration != null) {
                endTestTransaction();
            }
            GlobalVariables.setUserSession(null);
            GlobalVariables.setErrorMap(new ErrorMap());
            LOG.info("Leaving test '" + testName + "'");
        }
    }

    protected void clearMethodCache() {
        GeneralCacheAdministrator cache = (GeneralCacheAdministrator)SpringContext.getBean("methodResultsCacheAdministrator");
        cache.flushAll();
        cache = (GeneralCacheAdministrator)SpringContext.getBean("methodResultsCacheNoCopyAdministrator");
        cache.flushAll();
    }
    
    protected boolean testTransactionIsRollbackOnly() {
        return (transactionStatus != null) && (transactionStatus.isRollbackOnly());
    }

    protected void changeCurrentUser(UserNameFixture sessionUser) throws Exception {
        GlobalVariables.setUserSession(new UserSession(sessionUser.toString()));
    }

    private void configure(ConfigureContext contextConfiguration) throws Exception {
        if (configurationFailure != null) {
            throw configurationFailure;
        }
        if (!springContextInitialized) {
            try {
                SpringContext.initializeTestApplicationContext();
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
            transactionStatus = getTransactionManager().getTransaction(defaultTransactionDefinition);
        }
        else {
            LOG.info("Test transaction not used");
            transactionStatus = null;
        }
        UserNameFixture sessionUser = contextConfiguration.session();
        if (sessionUser != UserNameFixture.NO_SESSION) {
            GlobalVariables.setUserSession(new UserSession(sessionUser.toString()));
        }
    }

    private void endTestTransaction() {
        if (transactionStatus != null) {
            LOG.info("rolling back transaction");
            getTransactionManager().rollback(transactionStatus);
        }
    }

    private PlatformTransactionManager getTransactionManager() {
        return SpringContext.getBean(PlatformTransactionManager.class);
    }

    private Method getMethod(String methodName) {
        Class clazz = getClass();
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

    private Set<RelatesTo.JiraIssue> getRelatedJiraIssues() {
        HashSet<RelatesTo.JiraIssue> issues = new HashSet<RelatesTo.JiraIssue>();
        addJiraIssues(this.getClass().getAnnotation(RelatesTo.class), issues);
        // Test methods must be public, so we can use getMethod(), which handles inheritence. (I recommend not inheriting test
        // methods, however.)
        try {
            addJiraIssues(this.getClass().getMethod(getName()).getAnnotation(RelatesTo.class), issues);
        }
        catch (NoSuchMethodException e) {
            throw new AssertionError("Impossible because tests are named after their test method.");
        }
        return issues;
    }

    private static void addJiraIssues(RelatesTo annotation, HashSet<RelatesTo.JiraIssue> issues) {
        if (annotation != null) {
            issues.addAll(Arrays.asList(annotation.value()));
        }
    }
}