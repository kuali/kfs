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

import junit.framework.TestSuite;

/**
 * The suite of all test classes or methods which {@link org.kuali.test.suite.RelatesTo} a Kuali JIRA issue in-progress. IDEs or Ant
 * can run this as JUnit tests.
 * 
 * @see RelatesTo
 */
public class InProgressSuite extends JiraRelatedSuite {

    public static TestSuite suite() throws Exception {
        return new InProgressSuite().getSuite(JiraRelatedSuite.State.IN_PROGRESS);
    }

    /**
     * The suite of all test methods (including those within test class sub-suites) which do not
     * {@link org.kuali.test.suite.RelatesTo} a JIRA issue in-progress. IDEs or Ant can run this as JUnit tests.
     */
    public static class Not extends JiraRelatedSuite {
        public static TestSuite suite() throws Exception {
            return new InProgressSuite().getNegativeSuite(JiraRelatedSuite.State.IN_PROGRESS);
        }
    }
}
