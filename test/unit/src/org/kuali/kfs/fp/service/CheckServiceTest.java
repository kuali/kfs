/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
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
package org.kuali.module.financial.service;

import static org.kuali.core.util.SpringServiceLocator.getCheckService;
import static org.kuali.core.util.SpringServiceLocator.getDateTimeService;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CheckBase;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the Check service.
 * 
 * 
 */
@WithTestSpringContext
public class CheckServiceTest extends KualiTestBase {

    private Check check;

    private final String DOCUMENT_HEADER_ID = "-1";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // setup check
        check = new CheckBase();
        check.setFinancialDocumentNumber(DOCUMENT_HEADER_ID);
        check.setAmount(new KualiDecimal("314.15"));
        check.setCheckDate(getDateTimeService().getCurrentSqlDate());
        check.setCheckNumber("2112");
        check.setDescription("test check");
        check.setInterimDepositAmount(true);
        check.setSequenceId(new Integer(2001));

        // clean up remnants of earlier tests
        clearTestData();
    }

    @TestsWorkflowViaDatabase
    public void testLifecycle() throws Exception {
        boolean deleteSucceeded = false;

        List retrievedChecks = getCheckService().getByDocumentHeaderId(DOCUMENT_HEADER_ID);
        assertTrue(retrievedChecks.size() == 0);

        Check savedCheck = null;
        Check retrievedCheck = null;
        try {
            // save a check
            savedCheck = getCheckService().save(check);

            // retrieve it
            retrievedChecks = getCheckService().getByDocumentHeaderId(DOCUMENT_HEADER_ID);
            assertTrue(retrievedChecks.size() > 0);
            retrievedCheck = (Check) retrievedChecks.get(0);

            // compare
            assertTrue(check.isLike(savedCheck));
            assertTrue(savedCheck.isLike(retrievedCheck));
        }
        finally {
            // delete it
            getCheckService().deleteCheck(savedCheck);
        }

        // verify that the delete succeeded
        retrievedChecks = getCheckService().getByDocumentHeaderId(DOCUMENT_HEADER_ID);
        assertTrue(retrievedChecks.size() == 0);

    }


    private void clearTestData() {
        List retrievedChecks = getCheckService().getByDocumentHeaderId(DOCUMENT_HEADER_ID);
        if (retrievedChecks.size() > 0) {
            for (Iterator i = retrievedChecks.iterator(); i.hasNext();) {
                getCheckService().deleteCheck((Check) i.next());
            }
        }
    }
}
