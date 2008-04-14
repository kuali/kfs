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
package org.kuali.module.chart.globals;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.AccountGlobal;
import org.kuali.module.chart.bo.AccountGlobalDetail;
import org.kuali.module.chart.bo.DelegateGlobal;
import org.kuali.module.chart.bo.DelegateGlobalDetail;
import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.CrossSectionSuite;

@SuppressWarnings("deprecation")
@ConfigureContext(session = KHUNTLEY)
public class GlobalDocumentTest extends KualiTestBase {

    private static final Log LOG = LogFactory.getLog(GlobalDocumentTest.class);

    private static final String KNOWN_DOCUMENT_TYPENAME = "DelegateGlobalMaintenanceDocument";
    private static final String GLOBAL_DELEGATE_TYPENAME = "DelegateGlobalMaintenanceDocument";
    private static final String GLOBAL_ACCOUNT_TYPENAME = "AccountGlobalMaintenanceDocument";


    public void testGlobalDelegateMaintenanceDocumentCreation_goodDocTypeName() throws Exception {
        MaintenanceDocument doc = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(KNOWN_DOCUMENT_TYPENAME);
        assertNotNull(doc);
        assertNotNull(doc.getNewMaintainableObject());
        assertEquals("org.kuali.module.chart.bo.DelegateGlobal", doc.getNewMaintainableObject().getBoClass().getName());
    }

    public final void testGetNewDocument_globalDelegateMaintDoc() throws Exception {

        MaintenanceDocument document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(GLOBAL_DELEGATE_TYPENAME);

        // make sure the doc is setup
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());

        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());

        Maintainable newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be DelegateGlobal.", DelegateGlobal.class, newMaintainable.getBoClass());

        PersistableBusinessObject newBo = newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", newBo);
        assertEquals("New BO should be of the correct class.", DelegateGlobal.class, newBo.getClass());

    }

    public final void testGetNewDocument_globalAccountMaintDoc() throws Exception {

        MaintenanceDocument document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(GLOBAL_ACCOUNT_TYPENAME);

        // make sure the doc is setup
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());

        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());

        Maintainable newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be AccountGlobal.", AccountGlobal.class, newMaintainable.getBoClass());

        PersistableBusinessObject newBo = newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", newBo);
        assertEquals("New BO should be of the correct class.", AccountGlobal.class, newBo.getClass());
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = true)
    public final void testSaveDocument_globalDelegate() throws Exception {

        MaintenanceDocument document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(GLOBAL_DELEGATE_TYPENAME);

        // get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        DelegateGlobal bo = (DelegateGlobal) newMaintainable.getBusinessObject();
        String finDocNumber = document.getDocumentNumber();
        document.getDocumentHeader().setFinancialDocumentDescription("blah");

        System.err.println("DOC_NBR = " + finDocNumber);

        List<DelegateGlobalDetail> changes = new ArrayList();

        DelegateGlobalDetail change = new DelegateGlobalDetail();
        change.setAccountDelegatePrimaryRoutingIndicator(false);
        change.setAccountDelegateStartDate(DateUtils.newDate(2006, 6, 1));
        change.setAccountDelegateUniversalId("6137600107");
        change.setApprovalFromThisAmount(KualiDecimal.ZERO);
        change.setApprovalToThisAmount(KualiDecimal.ZERO);
        change.setFinancialDocumentTypeCode("ALL");
        changes.add(change);

        bo.setDelegateGlobals(changes);

        AccountGlobalDetail account;

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031400");
        bo.addAccount(account);

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031420");
        bo.addAccount(account);

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031467");
        bo.addAccount(account);
        SpringContext.getBean(DocumentService.class).saveDocument(document);

        // now that it worked, lets cancel the doc so it doesnt lock for others
        SpringContext.getBean(DocumentService.class).cancelDocument(document, "cancelling test document");

    }

    @AnnotationTestSuite(CrossSectionSuite.class)
    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = true)
    public final void testSaveAndLoadDocument_globalDelegate() throws Exception {

        MaintenanceDocument document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(GLOBAL_DELEGATE_TYPENAME);

        // get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        DelegateGlobal bo = (DelegateGlobal) newMaintainable.getBusinessObject();
        String finDocNumber = document.getDocumentNumber();
        document.getDocumentHeader().setFinancialDocumentDescription("blah");

        List<DelegateGlobalDetail> changes = new ArrayList();

        DelegateGlobalDetail change = new DelegateGlobalDetail();
        change.setAccountDelegatePrimaryRoutingIndicator(false);
        change.setAccountDelegateStartDate(DateUtils.newDate(2006, 6, 1));
        change.setAccountDelegateUniversalId("6137600107");
        change.setApprovalFromThisAmount(KualiDecimal.ZERO);
        change.setApprovalToThisAmount(KualiDecimal.ZERO);
        change.setFinancialDocumentTypeCode("ALL");
        changes.add(change);
        bo.setDelegateGlobals(changes);

        AccountGlobalDetail account;

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031400");
        bo.addAccount(account);

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031420");
        bo.addAccount(account);

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031467");
        bo.addAccount(account);

        SpringContext.getBean(DocumentService.class).saveDocument(document);

        // clear the document, and re-load it from the DB
        document = null;
        document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(finDocNumber);
        assertNotNull("Document should not be null after loaded from the DB.", document);
        assertNotNull("Document Header should not be null after loaded from the DB.", document.getDocumentHeader());
        assertNotNull("Document FinDocNumber should not be null after loaded from the DB.", document.getDocumentHeader().getDocumentNumber());

        // document should show up as a 'New' document
        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());

        // Maintainable should be populated and contain the right class
        newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be DelegateGlobal.", DelegateGlobal.class, newMaintainable.getBoClass());

        // BO should be non-null and the right class
        bo = (DelegateGlobal) newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", bo);
        assertEquals("New BO should be of the correct class.", DelegateGlobal.class, bo.getClass());

        // List should contain 3 elements
        assertNotNull("AccountDetail list should not be null.", bo.getAccountGlobalDetails());
        List accounts = bo.getAccountGlobalDetails();
        assertEquals("AccountDetail list should not be empty.", false, accounts.isEmpty());
        assertEquals("AccountDetail list should contain 3 elements.", 3, accounts.size());

        // make sure all the accounts are non-null and at least have the Chart populated
        for (Iterator iter = accounts.iterator(); iter.hasNext();) {
            AccountGlobalDetail accountDetail = (AccountGlobalDetail) iter.next();

            assertNotNull("AccountDetailChange should not be null.", accountDetail);
            assertNotNull("ChartOfAccountsCode", accountDetail.getChartOfAccountsCode());
            assertEquals("Account Chart should be known.", "BL", accountDetail.getChartOfAccountsCode());
        }

        // now that it worked, lets cancel the doc so it doesnt lock for others
        SpringContext.getBean(DocumentService.class).cancelDocument(document, "cancelling test document");

    }
}
