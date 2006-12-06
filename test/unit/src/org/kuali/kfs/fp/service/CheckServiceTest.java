/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.financial.service;

import static org.kuali.core.util.SpringServiceLocator.getCheckService;
import static org.kuali.core.util.SpringServiceLocator.getDateTimeService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE18;
import static org.kuali.test.fixtures.UserNameFixture.MHKOZLOW;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.bo.CheckBase;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
/**
 * This class tests the Check service.
 * 
 * 
 */
@WithTestSpringContext(session = MHKOZLOW)
public class CheckServiceTest extends KualiTestBase {

    private Check check;

    private String documentNumber;;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentNumber=createDocument();
        // setup check
        check = new CheckBase();
        check.setDocumentNumber(documentNumber);
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
        List retrievedChecks = getCheckService().getByDocumentHeaderId(documentNumber);
        assertTrue(retrievedChecks.size() == 0);

        Check savedCheck = null;
        Check retrievedCheck = null;
        try {
            // save a check
            savedCheck = getCheckService().save(check);

            // retrieve it
            retrievedChecks = getCheckService().getByDocumentHeaderId(documentNumber);
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
        retrievedChecks = getCheckService().getByDocumentHeaderId(documentNumber);
        assertTrue(retrievedChecks.size() == 0);

    }

    private void clearTestData() {
        List retrievedChecks = getCheckService().getByDocumentHeaderId(documentNumber);
        if (retrievedChecks.size() > 0) {
            for (Iterator i = retrievedChecks.iterator(); i.hasNext();) {
                getCheckService().deleteCheck((Check) i.next());
            }
        }
    }
    private String createDocument() throws Exception{
        CashReceiptDocument document = DocumentTestUtils.createDocument(getDocumentService(), CashReceiptDocument.class);
        LINE18.addAsSourceTo(document);
        getDocumentService().saveDocument(document, null, null);
        return document.getDocumentNumber();
    }
}
