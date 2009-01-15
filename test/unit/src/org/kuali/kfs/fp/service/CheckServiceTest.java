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
package org.kuali.kfs.fp.service;

import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE18;
import static org.kuali.kfs.sys.fixture.UserNameFixture.mhkozlow;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class tests the Check service.
 */
@ConfigureContext(session = mhkozlow)
public class CheckServiceTest extends KualiTestBase {

    private Check check;

    private String documentNumber;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentNumber = createDocument();
        // setup check
        check = new CheckBase();
        check.setDocumentNumber(documentNumber);
        check.setAmount(new KualiDecimal("314.15"));
        check.setCheckDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        check.setCheckNumber("2112");
        check.setDescription("test check");
        check.setFinancialDocumentDepositLineNumber(new Integer(2001));
        check.setSequenceId(new Integer(2001));
        check.setFinancialDocumentTypeCode("CR");
        check.setCashieringRecordSource("R");

        // clean up remnants of earlier tests
        clearTestData();
    }

    @ConfigureContext(session = mhkozlow, shouldCommitTransactions = true)
    public void testLifecycle() throws Exception {
        boolean deleteSucceeded = false;
        List retrievedChecks = SpringContext.getBean(CheckService.class).getByDocumentHeaderId(documentNumber);
        assertTrue(retrievedChecks.size() == 0);

        Check savedCheck = null;
        Check retrievedCheck = null;
        try {
            // save a check
            SpringContext.getBean(BusinessObjectService.class).save(check);
            savedCheck = check;

            // retrieve it
            retrievedChecks = SpringContext.getBean(CheckService.class).getByDocumentHeaderId(documentNumber);
            assertTrue(retrievedChecks.size() > 0);
            retrievedCheck = (Check) retrievedChecks.get(0);

            // compare
            assertTrue(check.isLike(savedCheck));
            assertTrue(savedCheck.isLike(retrievedCheck));
        }
        finally {
            // delete it
            SpringContext.getBean(BusinessObjectService.class).delete(savedCheck);
        }

        // verify that the delete succeeded
        retrievedChecks = SpringContext.getBean(CheckService.class).getByDocumentHeaderId(documentNumber);
        assertTrue(retrievedChecks.size() == 0);

    }

    private void clearTestData() {
        List retrievedChecks = SpringContext.getBean(CheckService.class).getByDocumentHeaderId(documentNumber);
        if (retrievedChecks.size() > 0) {
            for (Iterator i = retrievedChecks.iterator(); i.hasNext();) {
                SpringContext.getBean(BusinessObjectService.class).delete((Check) i.next());
            }
        }
    }

    private String createDocument() throws Exception {
        CashReceiptDocument document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        LINE18.addAsSourceTo(document);
        SpringContext.getBean(DocumentService.class).saveDocument(document);
        return document.getDocumentNumber();
    }
}

