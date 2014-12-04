/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

