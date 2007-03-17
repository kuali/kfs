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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.KualiDecimal;

import static org.kuali.kfs.util.SpringServiceLocator.getDocumentService;

import org.kuali.module.chart.bo.AccountChangeDetail;
import org.kuali.module.chart.bo.AccountChangeDocument;
import org.kuali.module.chart.bo.DelegateChangeContainer;
import org.kuali.module.chart.bo.DelegateChangeDocument;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.CrossSectionSuite;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import edu.iu.uis.eden.exception.WorkflowException;

@SuppressWarnings("deprecation")
@WithTestSpringContext(session = KHUNTLEY)
public class GlobalDocumentTest extends KualiTestBase {

    private static final Log LOG = LogFactory.getLog(GlobalDocumentTest.class);

    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiDelegateChangeDocument";
    private static final String GLOBAL_DELEGATE_TYPENAME = "KualiDelegateChangeDocument";
    private static final String GLOBAL_ACCOUNT_TYPENAME = "KualiAccountChangeDocument";


    public void testGlobalDelegateMaintenanceDocumentCreation_goodDocTypeName() throws Exception {
        MaintenanceDocument doc = (MaintenanceDocument) getDocumentService().getNewDocument(KNOWN_DOCUMENT_TYPENAME);
        assertNotNull(doc);
        assertNotNull(doc.getNewMaintainableObject());
        assertEquals("org.kuali.module.chart.bo.DelegateChangeContainer", doc.getNewMaintainableObject().getBoClass().getName());
    }

    public final void testGetNewDocument_globalDelegateMaintDoc() throws Exception {

        MaintenanceDocument document = (MaintenanceDocument) getDocumentService().getNewDocument(GLOBAL_DELEGATE_TYPENAME);

        // make sure the doc is setup
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());

        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());

        Maintainable newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be DelegateChangeContainer.", DelegateChangeContainer.class, newMaintainable.getBoClass());

        PersistableBusinessObject newBo = newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", newBo);
        assertEquals("New BO should be of the correct class.", DelegateChangeContainer.class, newBo.getClass());

    }

    public final void testGetNewDocument_globalAccountMaintDoc() throws Exception {

        MaintenanceDocument document = (MaintenanceDocument) getDocumentService().getNewDocument(GLOBAL_ACCOUNT_TYPENAME);

        // make sure the doc is setup
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());

        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());

        Maintainable newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be AccountChangeDocument.", AccountChangeDocument.class, newMaintainable.getBoClass());

        PersistableBusinessObject newBo = newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", newBo);
        assertEquals("New BO should be of the correct class.", AccountChangeDocument.class, newBo.getClass());
    }

    @TestsWorkflowViaDatabase
    public final void testSaveDocument_globalDelegate() throws Exception {

        MaintenanceDocument document = (MaintenanceDocument) getDocumentService().getNewDocument(GLOBAL_DELEGATE_TYPENAME);

        // get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        DelegateChangeContainer bo = (DelegateChangeContainer) newMaintainable.getBusinessObject();
        String finDocNumber = document.getDocumentNumber();
        document.getDocumentHeader().setFinancialDocumentDescription("blah");

        System.err.println("DOC_NBR = " + finDocNumber);

        List<DelegateChangeDocument> changes = new ArrayList();

        DelegateChangeDocument change = new DelegateChangeDocument();
        change.setAccountDelegatePrimaryRoutingIndicator(false);
        change.setAccountDelegateStartDate(DateUtils.newDate(2006, 6, 1));
        change.setAccountDelegateUniversalId("6137600107");
        change.setApprovalFromThisAmount(new KualiDecimal(0));
        change.setApprovalToThisAmount(new KualiDecimal(0));
        change.setFinancialDocumentTypeCode("ALL");
        changes.add(change);

        bo.setDelegateChanges(changes);

        AccountChangeDetail account;

        account = new AccountChangeDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031400");
        bo.addAccount(account);

        account = new AccountChangeDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031420");
        bo.addAccount(account);

        account = new AccountChangeDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031467");
        bo.addAccount(account);
        getDocumentService().saveDocument(document);

        // now that it worked, lets cancel the doc so it doesnt lock for others
        getDocumentService().cancelDocument(document, "cancelling test document");

    }

    @AnnotationTestSuite(CrossSectionSuite.class)
    @TestsWorkflowViaDatabase
    public final void testSaveAndLoadDocument_globalDelegate() throws Exception {

        MaintenanceDocument document = (MaintenanceDocument) getDocumentService().getNewDocument(GLOBAL_DELEGATE_TYPENAME);

        // get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        DelegateChangeContainer bo = (DelegateChangeContainer) newMaintainable.getBusinessObject();
        String finDocNumber = document.getDocumentNumber();
        document.getDocumentHeader().setFinancialDocumentDescription("blah");

        List<DelegateChangeDocument> changes = new ArrayList();

        DelegateChangeDocument change = new DelegateChangeDocument();
        change.setAccountDelegatePrimaryRoutingIndicator(false);
        change.setAccountDelegateStartDate(DateUtils.newDate(2006, 6, 1));
        change.setAccountDelegateUniversalId("6137600107");
        change.setApprovalFromThisAmount(new KualiDecimal(0));
        change.setApprovalToThisAmount(new KualiDecimal(0));
        change.setFinancialDocumentTypeCode("ALL");
        changes.add(change);
        bo.setDelegateChanges(changes);

        AccountChangeDetail account;

        account = new AccountChangeDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031400");
        bo.addAccount(account);

        account = new AccountChangeDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031420");
        bo.addAccount(account);

        account = new AccountChangeDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031467");
        bo.addAccount(account);

        getDocumentService().saveDocument(document);

        // clear the document, and re-load it from the DB
        document = null;
        document = (MaintenanceDocument) getDocumentService().getByDocumentHeaderId(finDocNumber);
        assertNotNull("Document should not be null after loaded from the DB.", document);
        assertNotNull("Document Header should not be null after loaded from the DB.", document.getDocumentHeader());
        assertNotNull("Document FinDocNumber should not be null after loaded from the DB.", document.getDocumentHeader().getDocumentNumber());

        // document should show up as a 'New' document
        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());

        // Maintainable should be populated and contain the right class
        newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be DelegateChangeContainer.", DelegateChangeContainer.class, newMaintainable.getBoClass());

        // BO should be non-null and the right class
        bo = (DelegateChangeContainer) newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", bo);
        assertEquals("New BO should be of the correct class.", DelegateChangeContainer.class, bo.getClass());

        // List should contain 3 elements
        assertNotNull("AccountDetail list should not be null.", bo.getAccountChangeDetails());
        List accounts = bo.getAccountChangeDetails();
        assertEquals("AccountDetail list should not be empty.", false, accounts.isEmpty());
        assertEquals("AccountDetail list should contain 3 elements.", 3, accounts.size());

        // make sure all the accounts are non-null and at least have the Chart populated
        for (Iterator iter = accounts.iterator(); iter.hasNext();) {
            AccountChangeDetail accountDetail = (AccountChangeDetail) iter.next();

            assertNotNull("AccountDetailChange should not be null.", accountDetail);
            assertNotNull("ChartOfAccountsCode", accountDetail.getChartOfAccountsCode());
            assertEquals("Account Chart should be known.", "BL", accountDetail.getChartOfAccountsCode());
        }

        // now that it worked, lets cancel the doc so it doesnt lock for others
        getDocumentService().cancelDocument(document, "cancelling test document");

    }

    @TestsWorkflowViaDatabase
    public void testLocking_Delegate_NoLocks() throws WorkflowException {

        MaintenanceDocument document = (MaintenanceDocument) getDocumentService().getNewDocument(GLOBAL_DELEGATE_TYPENAME);

        // get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        DelegateChangeContainer bo = (DelegateChangeContainer) newMaintainable.getBusinessObject();
        String finDocNumber = document.getDocumentNumber();
        document.getDocumentHeader().setFinancialDocumentDescription("blah");

        List<DelegateChangeDocument> changes = new ArrayList();

        DelegateChangeDocument change = new DelegateChangeDocument();
        change.setAccountDelegatePrimaryRoutingIndicator(false);
        change.setAccountDelegateStartDate(DateUtils.newDate(2006, 6, 1));
        change.setAccountDelegateUniversalId("6137600107");
        change.setApprovalFromThisAmount(new KualiDecimal(0));
        change.setApprovalToThisAmount(new KualiDecimal(0));
        change.setFinancialDocumentTypeCode("ALL");
        changes.add(change);
        bo.setDelegateChanges(changes);

        AccountChangeDetail account;

        account = new AccountChangeDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031400");
        bo.addAccount(account);

        account = new AccountChangeDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031420");
        bo.addAccount(account);

        account = new AccountChangeDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031467");
        bo.addAccount(account);

        getDocumentService().saveDocument(document);

        // clear the document, and re-load it from the DB
        document = null;
        document = (MaintenanceDocument) getDocumentService().getByDocumentHeaderId(finDocNumber);
        assertNotNull("Document should not be null after loaded from the DB.", document);
        assertNotNull("Document Header should not be null after loaded from the DB.", document.getDocumentHeader());
        assertNotNull("Document FinDocNumber should not be null after loaded from the DB.", document.getDocumentHeader().getDocumentNumber());

        // document should show up as a 'New' document
        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());

        // Maintainable should be populated and contain the right class
        newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be DelegateChangeContainer.", DelegateChangeContainer.class, newMaintainable.getBoClass());

        // BO should be non-null and the right class
        bo = (DelegateChangeContainer) newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", bo);
        assertEquals("New BO should be of the correct class.", DelegateChangeContainer.class, bo.getClass());

        // List should contain 3 elements
        assertNotNull("AccountDetail list should not be null.", bo.getAccountChangeDetails());
        List accounts = bo.getAccountChangeDetails();
        assertEquals("AccountDetail list should not be empty.", false, accounts.isEmpty());
        assertEquals("AccountDetail list should contain 3 elements.", 3, accounts.size());

        // make sure all the accounts are non-null and at least have the Chart populated
        for (Iterator iter = accounts.iterator(); iter.hasNext();) {
            AccountChangeDetail accountDetail = (AccountChangeDetail) iter.next();

            assertNotNull("AccountDetailChange should not be null.", accountDetail);
            assertNotNull("ChartOfAccountsCode", accountDetail.getChartOfAccountsCode());
            assertEquals("Account Chart should be known.", "BL", accountDetail.getChartOfAccountsCode());
        }

        // now that it worked, lets cancel the doc so it doesnt lock for others
        getDocumentService().cancelDocument(document, "cancelling test document");

    }

}
