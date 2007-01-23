/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.test.suite;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.lang.reflect.Method;

import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.GetMethodWebRequest;
import org.w3c.dom.NodeList;
import org.kuali.core.util.AssertionUtils;
import junit.framework.TestSuite;
import junit.framework.TestCase;

/**
 * The abstract superclass of suites of all test classes or methods which {@link RelatesTo} a Kuali JIRA issue
 * that is currently in a certain state (e.g., in-progress).  IDEs or Ant can run the concrete subclasses as JUnit tests.
 *
 * @see org.kuali.test.suite.RelatesTo
 */
public abstract class JiraRelatedSuite {

    public static enum State {
        IN_PROGRESS("status=3&tempMax=1000"),
        OPEN_OR_IN_PROGRESS("status=1&status=3&tempMax=9999"),
        OPEN_OR_IN_PROGRESS_OR_REOPENED("status=1&status=3&status=4&tempMax=9999");

        public final String filterUrl;

        State(String uniquePart) {
            this.filterUrl = "https://test.kuali.org/jira/secure/IssueNavigator.jspa?os_username=kuali-rss-feed-user&os_password=kuali-rss-feed-user&view=rss&reset=true&decorator=none&" + uniquePart;
        }
    }

    private static Map<State, Collection<String>> jiraIssuesByState = new HashMap<State, Collection<String>>();
    private static RuntimeException initializationException = null;
    private final static Pattern EXPECTED_JIRA_KEY = Pattern.compile("\\p{Upper}+-\\p{Digit}+");

    /**
     * Gets the JIRA issues currently in the given state.  Caches the results, to avoid queries to the JIRA server, for speed.
     * 
     * @param state the state to get
     * @return a Set of the names of all JIRA issues currently in the given state
     * @throws RuntimeException if the JIRA server cannot be queried for this list, or its response cannot be understood.
     *                          After this exception is thrown once, it's always thrown immediately thereafter, to fast-fail KualiTestBase.
     */
    private static Collection<String> getNamesOfJiraIssues(State state) {
        if (initializationException != null) {
            throw initializationException;
        }
        if (!jiraIssuesByState.containsKey(state)) {
            try {
                Collection<String> jiraIssues = new HashSet<String>();
                WebResponse response = new WebConversation().getResponse(new GetMethodWebRequest(state.filterUrl));
                NodeList keys = response.getDOM().getElementsByTagName("key");
                for (int i = 0; i < keys.getLength(); i++) {
                    String jiraKey = keys.item(i).getTextContent();
                    AssertionUtils.assertThat(EXPECTED_JIRA_KEY.matcher(jiraKey).matches(), "badly formed key: " + jiraKey);
                    jiraIssues.add(jiraKey);
                }
                jiraIssuesByState.put(state, jiraIssues);
            }
            catch (Throwable e) {
                initializationException = new RuntimeException("test framework cannot get list of " + state + " JIRA issues", e);
                throw initializationException;
            }
        }
        return jiraIssuesByState.get(state);
    }

    /**
     * Filters the JIRA issues which are currently in the given state.
     * The JIRA status is queried once when needed and cached statically for speed.
     *
     * @param from JIRA issues from which to filter
     * @param state JIRA state to filter on
     * @return any of the given issues that are currently in the given state in JIRA
     * @throws RuntimeException if the JIRA server cannot be queried for this list, or its response cannot be understood.
     *                          After this exception is thrown once, it's always thrown immediately thereafter, to fast-fail KualiTestBase.
     */
    public static Set<RelatesTo.JiraIssue> getMatchingIssues(Collection<RelatesTo.JiraIssue> from, State state) {
        HashSet<RelatesTo.JiraIssue> result = new HashSet<RelatesTo.JiraIssue>();
        if (!from.isEmpty()) { // try to avoid the JIRA query
            for (RelatesTo.JiraIssue issue : from) {
                if (getNamesOfJiraIssues(state).contains(issue.toString())) {
                    result.add(issue);
                }
            }
        }
        return result;
    }

    private static boolean hasRelatedIssueInState(RelatesTo annotation, State state) {
        return annotation != null && !getMatchingIssues(Arrays.asList(annotation.value()), state).isEmpty();
    }

    /**
     * Builds the suite of all test methods (including those within test class sub-suites)
     * which {@link RelatesTo} a JIRA issue in the given state.
     * This method is for subclasses; it cannot be run by JUnit directly.
     *
     * @param state the current state to include
     * @return the positive suite
     * @throws java.io.IOException if the directory containing this class file cannot be scanned for other test class files
     * @throws RuntimeException if the JIRA server cannot be queried for this list, or its response cannot be understood.
     *                          After this exception is thrown once, it's always thrown immediately thereafter, to fast-fail KualiTestBase.
     * @throws Exception is not actually thrown, because the criteria inner classes do not throw it
     */
    protected TestSuite getSuite(final State state)
        throws Exception
    {
        TestSuiteBuilder.ClassCriteria classCriteria = new TestSuiteBuilder.ClassCriteria() {
            public boolean includes(Class<? extends TestCase> testClass) {
                return hasRelatedIssueInState(testClass.getAnnotation(RelatesTo.class), state);
            }
        };
        TestSuiteBuilder.MethodCriteria methodCriteria = new TestSuiteBuilder.MethodCriteria() {
            public boolean includes(Method method) {
                return hasRelatedIssueInState(method.getAnnotation(RelatesTo.class), state);
            }
        };
        TestSuite suite = TestSuiteBuilder.build(classCriteria, methodCriteria);
        suite.setName(this.getClass().getName());
        return suite;
    }

    /**
     * Builds the suite of all test methods (including those within test class sub-suites)
     * which do not {@link RelatesTo} a JIRA issue in the given state.
     * This method is for subclasses; it cannot be run by JUnit directly.
     *
     * @param state the current state to exclude
     * @return the negative suite
     * @throws java.io.IOException if the directory containing this class file cannot be scanned for other test class files
     * @throws RuntimeException if the JIRA server cannot be queried for this list, or its response cannot be understood.
     *                          After this exception is thrown once, it's always thrown immediately thereafter, to fast-fail KualiTestBase.
     * @throws Exception is not actually thrown, because the criteria inner class does not throw it
     */
    protected TestSuite getNegativeSuite(final State state)
        throws Exception
    {
        TestSuiteBuilder.MethodCriteria negativeMethodCriteria = new TestSuiteBuilder.MethodCriteria() {
            public boolean includes(Method method) {
                RelatesTo testClassAnnotation = method.getDeclaringClass().getAnnotation(RelatesTo.class);
                return !hasRelatedIssueInState(testClassAnnotation, state) && !hasRelatedIssueInState(method.getAnnotation(RelatesTo.class), state);
            }
        };
        TestSuite suite = TestSuiteBuilder.build(TestSuiteBuilder.NULL_CRITERIA, negativeMethodCriteria);
        suite.setName(this.getClass().getName());
        return suite;
    }
}
