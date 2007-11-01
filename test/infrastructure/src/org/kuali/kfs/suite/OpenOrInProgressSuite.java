/*
 * Copyright 2006 The Kuali Foundation.
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
 * The suite of all test classes or methods which {@link RelatesTo} a Kuali JIRA issue which is currently in an open or in-progress
 * state. IDEs or Ant can run this as JUnit tests. It takes well over a minute to get the list of open issues from JIRA, so have
 * patience.
 * 
 * @see org.kuali.test.suite.RelatesTo
 */
public class OpenOrInProgressSuite extends JiraRelatedSuite {

    public static TestSuite suite() throws Exception {
        return new OpenOrInProgressSuite().getSuite(State.OPEN_OR_IN_PROGRESS);
    }

    /**
     * The suite of all test methods (including those within test class sub-suites) which do not {@link RelatesTo} a JIRA issue
     * which is currently in an open or in-progress state. IDEs or Ant can run this as JUnit tests. It takes well over a minute to
     * get the list of open issues from JIRA, so have patience.
     */
    public static class Not extends JiraRelatedSuite {
        public static TestSuite suite() throws Exception {
            return new OpenOrInProgressSuite().getNegativeSuite(State.OPEN_OR_IN_PROGRESS);
        }
    }
}
