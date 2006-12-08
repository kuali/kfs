/*
 * Copyright (c) 2005, 2006 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.test.suite;

import junit.framework.TestSuite;

/**
 * The suite of all test classes or methods which {@link RelatesTo} a Kuali JIRA issue which is currently in an open or in-progress state.
 * IDEs or Ant can run this as JUnit tests.  It takes well over a minute to get the list of open issues from JIRA, so have patience.
 *
 * @see org.kuali.test.suite.RelatesTo
 */
public class OpenOrInProgressSuite extends JiraRelatedSuite {

    public static TestSuite suite()
        throws Exception
    {
        return new OpenOrInProgressSuite().getSuite(State.OPEN_OR_IN_PROGRESS);
    }

    /**
     * The suite of all test methods (including those within test class sub-suites)
     * which do not {@link RelatesTo} a JIRA issue which is currently in an open or in-progress state.
     * IDEs or Ant can run this as JUnit tests.  It takes well over a minute to get the list of open issues from JIRA, so have patience.
     */
    public static class Not extends JiraRelatedSuite {
        public static TestSuite suite()
            throws Exception
        {
            return new OpenOrInProgressSuite().getNegativeSuite(State.OPEN_OR_IN_PROGRESS);
        }
    }
}
