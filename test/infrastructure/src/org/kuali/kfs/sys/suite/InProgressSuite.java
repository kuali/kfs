/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/infrastructure/src/org/kuali/kfs/sys/suite/InProgressSuite.java,v $
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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xml.sax.SAXException;

/**
 * The suite of all test classes or methods which {@link org.kuali.test.suite.RelatesTo} a Kuali JIRA issue in-progress.
 * IDEs or Ant can run this as JUnit tests.
 *
 * @see RelatesTo
 */
public class InProgressSuite {

    private static Collection<String> jiraIssuesInProgress;
    // This public Confluence space does not require a login like JIRA does.  There could be a better way to do this in the JIRA API, though.
    private static final String JIRA_FILTER_URL = "https://test.kuali.org/confluence/display/KULDOC/Issue+Filter+for+Unit+Test+Framework";

    private static Collection<String> getJiraIssuesInProgress()
        throws IOException, SAXException
    {
        if (jiraIssuesInProgress == null) {
            jiraIssuesInProgress = new HashSet<String>();
            WebResponse response = new WebConversation().getResponse(new GetMethodWebRequest(JIRA_FILTER_URL));
            WebTable results = response.getTableStartingWithPrefix("Kuali: Jira");
            int rowCount = results.getRowCount();
            for (int row = 2; row < rowCount; row++) {
                jiraIssuesInProgress.add(results.getCellAsText(row, 0));
            }
        }
        return jiraIssuesInProgress;
    }

    private static boolean hasRelatedIssueInProgress(RelatesTo annotation)
        throws IOException, SAXException
    {
        if (annotation == null) {
            return false;
        }
        for (RelatesTo.JiraIssue issue : annotation.value()) {
            if (getJiraIssuesInProgress().contains(issue.toString())) {
                return true;
            }
        }
        return false;
    }

    public static TestSuite suite()
        throws Exception
    {
        TestSuiteBuilder.ClassCriteria classCriteria = new TestSuiteBuilder.ClassCriteria() {
            public boolean includes(Class<? extends TestCase> testClass)
                throws IOException, SAXException
            {
                return hasRelatedIssueInProgress(testClass.getAnnotation(RelatesTo.class));
            }
        };
        TestSuiteBuilder.MethodCriteria methodCriteria = new TestSuiteBuilder.MethodCriteria() {
            public boolean includes(Method method)
                throws IOException, SAXException
            {
                return hasRelatedIssueInProgress(method.getAnnotation(RelatesTo.class));
            }
        };
        return TestSuiteBuilder.build(classCriteria, methodCriteria);
    }

    /**
     * The suite of all test methods (including those within test class sub-suites)
     * which do not {@link org.kuali.test.suite.RelatesTo} a JIRA issue in-progress.
     * IDEs or Ant can run this as JUnit tests.
     */
    public static class Not {
        public static TestSuite suite()
            throws Exception
        {
            TestSuiteBuilder.MethodCriteria negativeMethodCriteria = new TestSuiteBuilder.MethodCriteria() {
                public boolean includes(Method method)
                    throws IOException, SAXException
                {
                    RelatesTo testClassAnnotation = method.getDeclaringClass().getAnnotation(RelatesTo.class);
                    return !hasRelatedIssueInProgress(testClassAnnotation) && !hasRelatedIssueInProgress(method.getAnnotation(RelatesTo.class));
                }
            };
            return TestSuiteBuilder.build(TestSuiteBuilder.NULL_CRITERIA, negativeMethodCriteria);
        }
    }
}
