/*
 * Copyright 2005-2007 The Kuali Foundation.
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.OptimisticLockException;
import org.kuali.core.UserSession;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.test.RequiresSpringContext;
import org.kuali.test.KualiTestConstants;
import org.kuali.test.ShouldCommitTransactions;
import org.kuali.test.RequiresSpringContext;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.test.suite.JiraRelatedSuite;
import org.kuali.test.suite.RelatesTo;
import org.springmodules.orm.ojb.OjbOperationException;

import edu.iu.uis.eden.exception.ResourceUnavailableException;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is the superclass for all test cases which may require the use of services, or datasources, or any of the other
 * expensive/time-consuming infrastructure.
 * <p>
 * For test methods or classes with the {@link RelatesTo} annotation, this class also wraps any test errors or failures
 * with a notice that the listed JIRA issues are related.  This is to help developers see on test reports
 * what work may be in progress or recently done for this problem.  It will help distinguish tests which have
 * already been investigated from ones which still need to be.  Test errors before the setUp method,
 * e.g., in connecting to the database to start a test transaction or to Workflow for a user session,
 * are not wrapped with these notices.  Tests not extending KualiTestBase also do not get these notices.
 * For the sake of speed, the current JIRA status of the related issues are not checked.  The original Throwable
 * is the cause of the wrapper, so it appears next in the stacktrace on the test report.
 * <p>
 * If the {@value #SKIP_OPEN_OR_IN_PROGRESS_OR_REOPENED_JIRA_ISSUES} system property is set, then this class passes (without running its contents)
 * any test that {@link RelatesTo} a JIRA issue that is currently open or in-progress or reopened.  This is an alternative to 
 * {@link org.kuali.test.suite.OpenOrInProgressOrReopenedSuite} for Anthill to retain the same format of its test report
 * while not revealing any failures of such tests.  When using this system property, keep in mind that it takes well over a minute
 * to get the list of open issues from JIRA.  The list is cached statically, so it's insignificant to add a minute or two 
 * to the time it takes for the whole Anthill build.  But, developers will probably not want to add this system property
 * to their own environments, because of this delay and so that they can still work on those tests.
 * 
 * @see RequiresSpringContext
 * @see ShouldCommitTransactions
 * @see RelatesTo
 * 
 * 
 */

public abstract class KualiTestBase extends TestCase implements KualiTestConstants {
    private static final Logger LOG = Logger.getLogger(KualiTestBase.class);
    private static final String HIDE_SPRING_FROM_TESTS_MESSAGE = "This test class needs the " + RequiresSpringContext.class.getSimpleName() + " annotation to access Spring.";
    private static final String HIDE_SESSION_FROM_TESTS_MESSAGE = "this test class needs the " + RequiresSpringContext.class.getSimpleName() + " annotation with its 'session' element to initialize the user in the session.";
    public static final String SKIP_OPEN_OR_IN_PROGRESS_OR_REOPENED_JIRA_ISSUES = "org.kuali.test.KualiTestBase.skipOpenOrInProgressOrReopenedJiraIssues";

    private static final Map<String, Level> changedLogLevels = new HashMap<String, Level>();
    private static UserNameFixture userSessionUsername = null;
    protected static UserSession userSession = null;
    private static boolean log4jConfigured = false;

    /**
     * Changes the logging-level associated with the given loggerName to the given level. The original logging-level is saved, and
     * will be automatically restored at the end of each test.
     * 
     * @param loggerName name of the logger whose level to change
     * @param newLevel the level to change to
     */
    protected void setLogLevel(String loggerName, Level newLevel) {
        Logger logger = Logger.getLogger(loggerName);

        if (!changedLogLevels.containsKey(loggerName)) {
            Level originalLevel = logger.getLevel();
            changedLogLevels.put(loggerName, originalLevel);
        }

        logger.setLevel(newLevel);
    }

    /**
     * Restores the logging-levels changed through calls to setLogLevel to their original values.
     */
    protected void resetLogLevels() {
        for (Iterator i = changedLogLevels.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();

            String loggerName = (String) e.getKey();
            Level originalLevel = (Level) e.getValue();

            Logger.getLogger(loggerName).setLevel(originalLevel);
        }
        changedLogLevels.clear();
    }

    /**
     * Surrounds setUp(), tearDown(), and test invocation with Spring test context and transaction if required by this test's annotations.
     * Also surrounds each test-invocation with logging and logs extra details if the test invocation's OJB operations happen to encounter an OptimisticLockException
     * or if this test has a {@link RelatesTo} annotation.  See {@link KualiTestBase the KualiTestBase class Javadoc} about skipping related JIRA issues.
     * 
     * @throws Throwable
     */
    @Override
    public final void runBare() throws Throwable {
        if (!log4jConfigured) {
            Log4jConfigurer.configureLogging();
            log4jConfigured = true;
        }

        final String testName = getClass().getName() + "." + getName();

        if (System.getProperty(SKIP_OPEN_OR_IN_PROGRESS_OR_REOPENED_JIRA_ISSUES) != null) {
            Set<RelatesTo.JiraIssue> openOrInProgressOrReopened = JiraRelatedSuite.getMatchingIssues(getRelatedJiraIssues(), OPEN_OR_IN_PROGRESS_OR_REOPENED);
            if (!openOrInProgressOrReopened.isEmpty()) {
                LOG.info("skipping test '" + testName + "' because of JIRA issues open or in progress or reopened: " + openOrInProgressOrReopened);
                return; // let this test method pass without running it
            }
        }
        LOG.info("entering test '" + testName + "'");

        final boolean needsSpring = getClass().isAnnotationPresent(RequiresSpringContext.class);
        hideSession();
        GlobalVariables.setErrorMap(new ErrorMap());
        if (needsSpring) {
            SpringContext.initializeTestApplicationContext(canUseTestTransaction());
            initializeSessionIfAnnotated();
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
            resetLogLevels();
            if (needsSpring) {
                // Includes an error message for subsequent tests that try to circumvent this framework.
                SpringContext.transitionTestApplicationContext(HIDE_SPRING_FROM_TESTS_MESSAGE);
            }
            hideSession();
            GlobalVariables.setErrorMap(new ErrorMap());
            LOG.info("leaving test '" + testName + "'");
        }
    }

    /**
     * @return the JIRA issues thought to relate to this test method and class
     */
    private Set<RelatesTo.JiraIssue> getRelatedJiraIssues() {
        HashSet<RelatesTo.JiraIssue> issues = new HashSet<RelatesTo.JiraIssue>();
        addJiraIssues(this.getClass().getAnnotation(RelatesTo.class), issues);
        // Test methods must be public, so we can use getMethod(), which handles inheritence.  (I recommend not inheriting test methods, however.)
        try {
            addJiraIssues(this.getClass().getMethod(getName()).getAnnotation(RelatesTo.class), issues);
        }
        catch (NoSuchMethodException e) {
            throw new AssertionError("Impossible because tests are named after their test method.");
        }
        return issues;
    }

    /**
     * Adds the JIRA issues of the given annotation to the given set.
     * @param annotation listing related JIRA issues, or {@code null} if none
     * @param issues to be added to
     */
    private static void addJiraIssues(RelatesTo annotation, HashSet<RelatesTo.JiraIssue> issues) {
        if (annotation != null) {
            issues.addAll(Arrays.asList(annotation.value()));
        }
    }

    /**
     * Prevents tests that do not declare a session from using the session.
     * This includes subsequent non-KualiTestBase tests.  This eases maintenance by making those tests
     * able to run individually and not depend on a session left from the previous test.
     */
    private static void hideSession() {
        GlobalVariables.setHideSessionFromTestsMessage("To use the session, " + HIDE_SESSION_FROM_TESTS_MESSAGE);
        GlobalVariables.setUserSession(null);
    }

    private boolean canUseTestTransaction() {
        boolean can = true;
        can &= !getSetUpOrTearDownMethod(true).isAnnotationPresent(ShouldCommitTransactions.class);
        can &= !getSetUpOrTearDownMethod(false).isAnnotationPresent(ShouldCommitTransactions.class);
        try {
            // Test methods must be public, so we can use getMethod(), which handles inheritence.  (I recommend not inheriting test methods, however.)
            can &= !getClass().getMethod(getName()).isAnnotationPresent(ShouldCommitTransactions.class);
        }
        catch (NoSuchMethodException e) {
            throw new AssertionError("Impossible because tests are named after their test method.");
        }
        return can;
    }

    /**
     * The setUp and tearDown methods are not public, so we need to search up the inheritence heirarchy explicitly.
     * @param setUp true to get the setUp method (false for tearDown)
     * @return the most recently inherited method
     */
    private Method getSetUpOrTearDownMethod(boolean setUp) {
        Class clazz = getClass();
        while (clazz != null) {
            try {
                return clazz.getDeclaredMethod(setUp ? "setUp" : "tearDown");
            }
            catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        throw new AssertionError("Impossible because TestCase defines both setUp and tearDown, so they will be found.");
    }
    
    private void initializeSessionIfAnnotated()
        throws UserNotFoundException
    {
        UserNameFixture sessionUser = getClass().getAnnotation(RequiresSpringContext.class).session();
        if (sessionUser != UserNameFixture.NO_SESSION) {
            // reset the userSession between tests, iff it was changed during the preceding test
            changeCurrentUser(sessionUser);
            GlobalVariables.setHideSessionFromTestsMessage(null);
        }
    }

    /**
     * Creates a userSession for the given username into GlobalVariables, if and only if the given username doesn't match the
     * username used to create the current userSession.  Tests that call this method or use the global UserSession at all
     * must be annotated RequiresSpringContext with a session element of the default UserNameFixture to use before changing.
     * For example, {@code @RequiresSpringContext(session = UserNameFixture.KHUNTLEY) public class MyTest extends KualiTestBase}
     * The UserNameFixture may be imported statically to reduce verbosity: {@code @RequiresSpringContext(session = KHUNTLEY)}.
     * 
     * @param fixture the fixture of the user name to change the session to
     * @throws org.kuali.core.exceptions.UserNotFoundException if Workflow doesn't know that user
     */
    protected synchronized void changeCurrentUser(UserNameFixture fixture) throws UserNotFoundException {
        RequiresSpringContext annotation = getClass().getAnnotation(RequiresSpringContext.class);
        if (annotation == null || annotation.session() == UserNameFixture.NO_SESSION) {
            throw new RuntimeException("To change the user, " + HIDE_SESSION_FROM_TESTS_MESSAGE);
        }
        if (userSessionUsername != fixture) {
            try {
                String prefix = (userSession == null) ? "set" : "updated";

                // todo: evaluate this optimization.  If it's worth it, should we cache more than one?  If not, why cache any?
                userSessionUsername = fixture;
                userSession = new UserSession(fixture.toString());

                if (LOG.isDebugEnabled()) {
                    LOG.debug(prefix + " GlobalVariables.userSession for username '" + fixture + "'");
                }
            }
            catch (ResourceUnavailableException e) {
                throw new InfrastructureException("unable to create UserSession", e);
            }
            catch (WorkflowException e) {
                throw new UserNotFoundException("unable to find workflowUser for username '" + fixture + "'");
            }
        }
        GlobalVariables.setUserSession(userSession);
    }
   
    /**
     * Gives a Logger to all inheriting classes whether they like it or not. Usefule to not have to keep defining LOG if you need it
     * and forgetting to remove it when you don't.
     * 
     * @return Logger
     */
    protected Logger LOG() {
        return Logger.getLogger(getClass());
    }
}
