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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kuali.core.util.AssertionUtils;

/**
 * This annotation marks test classes or methods which have failed for reasons relating to certain JIRA issues. Ideally these issues
 * would be about bugs in production code or database tasks, but they could be about test code too.
 * <p>
 * One purpose of this annotation is to remove the related tests from the Anthill results while their issues are in progress. This
 * prevents them from obscuring new test failures that need attention, by allowing the Anthill results to be maintained at 100%
 * success.
 * <p>
 * Another purpose of this annotation is for KualiTestBase to wrap any test errors or failures with a notice that the annotated JIRA
 * issues are related. If the {@value #SKIP_OPEN_OR_IN_PROGRESS_OR_REOPENED_JIRA_ISSUES} system property is set, then the test will
 * pass (without running its contents) any test that {@link RelatesTo} a JIRA issue that is currently open or in-progress or
 * reopened. This is an alternative to {@link org.kuali.test.suite.OpenOrInProgressOrReopenedSuite} for Anthill to retain the same
 * format of its test report while not revealing any failures of such tests. When using this system property, keep in mind that it
 * takes well over a minute to get the list of open issues from JIRA. The list is cached statically, so it's insignificant to add a
 * minute or two to the time it takes for the whole Anthill build. But, developers will probably not want to add this system
 * property to their own environments, because of this delay and so that they can still work on those tests.
 * 
 * @see org.kuali.test.suite.InProgressSuite.Not
 * @see org.kuali.test.KualiTestBase
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE, ElementType.METHOD })
public @interface RelatesTo {

    public JiraIssue[] value();

    /**
     * JIRA issues which have been thought to relate to certain test failures. Using this enumeration makes it easy for the IDE to
     * show which tests relate to which issues.
     */
    public enum JiraIssue {
        
        KULPURAP2226,KULPURAP2283,NONE;

        private final static Pattern PATTERN = Pattern.compile("(\\p{Alpha}+)(\\p{Digit}+)");

        /**
         * @return the JIRA issue name, which is not a legal Java identifier.
         */
        @Override
        public String toString() {
            Matcher m = PATTERN.matcher(name());
            final boolean matched = m.matches();
            AssertionUtils.assertThat(matched);
            return m.group(1) + "-" + m.group(2);
        }
    }
}
