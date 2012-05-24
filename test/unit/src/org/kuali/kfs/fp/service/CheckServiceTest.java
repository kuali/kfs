/*
 * Copyright 2006 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CheckBase;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.identity.CashReceiptInitiatorDerivedRoleTypeServiceImpl;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.impl.permission.PermissionServiceImpl;
import org.kuali.rice.kim.impl.role.RoleServiceImpl;
import org.kuali.rice.kim.service.impl.IdentityManagementServiceImpl;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

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
        Logger.getLogger(PermissionServiceImpl.class).setLevel(Level.DEBUG);
        Logger.getLogger(RoleServiceImpl.class).setLevel(Level.DEBUG);
        Logger.getLogger(IdentityManagementServiceImpl.class).setLevel(Level.DEBUG);
        Logger.getLogger(CashReceiptInitiatorDerivedRoleTypeServiceImpl.class).setLevel(Level.DEBUG);

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
        check.setCashieringStatus("C");//change

        // clean up remnants of earlier tests
        clearTestData();
    }

    @ConfigureContext(session = mhkozlow, shouldCommitTransactions = true)
    public void testLifecycle() throws Exception {
        boolean deleteSucceeded = false;
        List retrievedChecks = new ArrayList(SpringContext.getBean(CheckService.class).getByDocumentHeaderId(documentNumber));
        assertTrue(retrievedChecks.size() == 0);

        Check savedCheck = null;
        Check retrievedCheck = null;
        try {
            // save a check
            SpringContext.getBean(BusinessObjectService.class).save(check);
            savedCheck = check;

            // retrieve it
            retrievedChecks = new ArrayList(SpringContext.getBean(CheckService.class).getByDocumentHeaderId(documentNumber));
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
        retrievedChecks = new ArrayList(SpringContext.getBean(CheckService.class).getByDocumentHeaderId(documentNumber));
        assertTrue(retrievedChecks.size() == 0);

    }

    private void clearTestData() {
        List retrievedChecks = new ArrayList(SpringContext.getBean(CheckService.class).getByDocumentHeaderId(documentNumber));
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

