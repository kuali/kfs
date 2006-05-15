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
package org.kuali.module.chart.globals;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountChangeDetail;
import org.kuali.module.chart.bo.AccountChangeDocument;
import org.kuali.module.chart.bo.DelegateChangeDocument;
import org.kuali.module.chart.bo.GlobalAccountChanges;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.KualiTestBaseWithSpring;

import edu.iu.uis.eden.exception.WorkflowException;

public class GlobalDocumentTest extends KualiTestBaseWithFixtures {
    
    private static final Log LOG = LogFactory.getLog(GlobalDocumentTest.class);

    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiDelegateChangeDocument";
    private static final String GLOBAL_DELEGATE_TYPENAME = "KualiDelegateChangeDocument";
    private static final String GLOBAL_ACCOUNT_TYPENAME = "KualiAccountChangeDocument";
    
    private boolean rollback = false;
    private DocumentService docService;
    
    public GlobalDocumentTest() {
        super();
    }
    
    public void setUp() throws Exception {
        setRollback(rollback);
        super.setUp();
        docService = SpringServiceLocator.getDocumentService();
    }
    
    /**
     * Overrides base transaction wrapping, to prevent transactions from 
     * happening.
     * 
     * @see org.kuali.test.KualiTestBaseWithSpring#needsTestTransaction()
     */
    protected boolean needsTestTransaction() {
        return false;
    }
    
    public void testGlobalDelegateMaintenanceDocumentCreation_goodDocTypeName() throws Exception {
        MaintenanceDocument doc = (MaintenanceDocument) docService.getNewDocument(KNOWN_DOCUMENT_TYPENAME);
        assertNotNull(doc);
        assertNotNull(doc.getNewMaintainableObject());
        assertEquals("org.kuali.module.chart.bo.DelegateChangeDocument", doc.getNewMaintainableObject().getBoClass().getName());
    }

    public final void testGetNewDocument_globalDelegateMaintDoc() throws Exception {
        
        MaintenanceDocument document = (MaintenanceDocument) docService.getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        
        //  make sure the doc is setup
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getFinancialDocumentNumber());
        
        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());
        
        Maintainable newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be DelegateChangeDocument.", DelegateChangeDocument.class, newMaintainable.getBoClass());

        BusinessObject newBo = newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", newBo);
        assertEquals("New BO should be of the correct class.", DelegateChangeDocument.class, newBo.getClass());
        
    }
    
    public final void testGetNewDocument_globalAccountMaintDoc() throws Exception {
        
        MaintenanceDocument document = (MaintenanceDocument) docService.getNewDocument(GLOBAL_ACCOUNT_TYPENAME);
        
        //  make sure the doc is setup
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getFinancialDocumentNumber());
        
        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());
        
        Maintainable newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be AccountChangeDocument.", GlobalAccountChanges.class, newMaintainable.getBoClass());

        BusinessObject newBo = newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", newBo);
        assertEquals("New BO should be of the correct class.", GlobalAccountChanges.class, newBo.getClass());
    }
    
    //  These following tests always fails because (apparently) in the DocumentService save method, it calls 
    // the workflow saveDocument.  This in turn calls back to the Kuali PostProcessor for a 
    // handleDocumentRouteStatusChangeEvent(DocumentStatusChange).  This in turn attempts to load 
    // the document and update the status.  Unfortunately, because (I believe) workflow is running 
    // outside the transaction, it has no visibility into those records until the transaction is 
    // committed.  Short of disabling transaction wrapping for the whole app, I cant find a way out 
    // of this.
/*
    public final void testSaveDocument_globalDelegate() throws Exception {
     
        MaintenanceDocument document = (MaintenanceDocument) docService.getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        
        //  get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        DelegateChangeDocument bo = (DelegateChangeDocument) newMaintainable.getBusinessObject();
        String finDocNumber = document.getFinancialDocumentNumber();
        System.err.println("DOC_NBR = " + finDocNumber);
        
        bo.setAccountDelegatePrimaryRoutingCode("X");
        bo.setAccountDelegateStartDate(new java.sql.Date(2006, 6, 1));
        bo.setAccountDelegateUniversalId("20000000");
        bo.setApprovalFromThisAmount(new KualiDecimal(0));
        bo.setApprovalToThisAmount(new KualiDecimal(0));
        bo.setFinancialDocumentTypeCode("ALL");

        AccountChangeDetail account;
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031400");
        bo.addAccount(account);
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031420");
        bo.addAccount(account);
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031467");
        bo.addAccount(account);
        
        docService.save(document, null, null);
        
    }
    
    public final void testSaveAndLoadDocument_globalDelegate() throws Exception {
        
        MaintenanceDocument document = (MaintenanceDocument) docService.getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        
        //  get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        DelegateChangeDocument bo = (DelegateChangeDocument) newMaintainable.getBusinessObject();
        String finDocNumber = document.getFinancialDocumentNumber();
        System.err.println("DOC_NBR = " + finDocNumber);
        
        bo.setAccountDelegatePrimaryRoutingCode("X");
        bo.setAccountDelegateStartDate(new java.sql.Date(2006, 6, 1));
        bo.setAccountDelegateUniversalId("20000000");
        bo.setApprovalFromThisAmount(new KualiDecimal(0));
        bo.setApprovalToThisAmount(new KualiDecimal(0));
        bo.setFinancialDocumentTypeCode("ALL");

        AccountChangeDetail account;
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031400");
        bo.addAccount(account);
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031420");
        bo.addAccount(account);
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031467");
        bo.addAccount(account);
        
        docService.save(document, null, null);
        
        //  clear the document, and re-load it from the DB
        document = null;
        document = (MaintenanceDocument) docService.getByDocumentHeaderId(finDocNumber);
        assertNotNull("Document should not be null after loaded from the DB.", document);
        assertNotNull("Document Header should not be null after loaded from the DB.", document.getDocumentHeader());
        assertNotNull("Document FinDocNumber should not be null after loaded from the DB.", document.getDocumentHeader().getFinancialDocumentNumber());
        
        //  document should show up as a 'New' document
        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());
        
        //  Maintainable should be populated and contain the right class
        newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be DelegateChangeDocument.", DelegateChangeDocument.class, newMaintainable.getBoClass());

        //  BO should be non-null and the right class
        bo = (DelegateChangeDocument) newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", bo);
        assertEquals("New BO should be of the correct class.", DelegateChangeDocument.class, bo.getClass());
        
        //  List should contain 3 elements
        assertNotNull("AccountDetail list should not be null.", bo.getAccountChangeDetails());
        List accounts = bo.getAccountChangeDetails();
        assertEquals("AccountDetail list should not be empty.", false, accounts.isEmpty());
        assertEquals("AccountDetail list should contain 3 elements.", 3, accounts.size());
        
        //  make sure all the accounts are non-null and at least have the Chart populated
        for (Iterator iter = accounts.iterator(); iter.hasNext();) {
            AccountChangeDetail accountDetail = (AccountChangeDetail) iter.next();
            
            assertNotNull("AccountDetailChange should not be null.", accountDetail);
            assertNotNull("ChartOfAccountsCode", accountDetail.getChartOfAccountsCode());
            assertEquals("Account Chart should be known.", "BL", accountDetail.getChartOfAccountsCode());
        }
    }
    
    public void testLocking_Delegate_NoLocks() throws WorkflowException {
        
        MaintenanceDocument document = (MaintenanceDocument) docService.getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        
        //  get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        DelegateChangeDocument bo = (DelegateChangeDocument) newMaintainable.getBusinessObject();
        String finDocNumber = document.getFinancialDocumentNumber();
        
        bo.setAccountDelegatePrimaryRoutingCode("");
        bo.setAccountDelegateStartDate(new java.sql.Date(2006, 6, 1));
        bo.setAccountDelegateUniversalId("019283749");
        bo.setApprovalFromThisAmount(new KualiDecimal(0));
        bo.setApprovalToThisAmount(new KualiDecimal(0));
        bo.setFinancialDocumentTypeCode("ALL");

        AccountChangeDetail account;
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031400");
        bo.addAccount(account);
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031420");
        bo.addAccount(account);
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031467");
        bo.addAccount(account);
        
        docService.save(document, null, null);
        
        //  clear the document, and re-load it from the DB
        document = null;
        document = (MaintenanceDocument) docService.getByDocumentHeaderId(finDocNumber);
        assertNotNull("Document should not be null after loaded from the DB.", document);
        assertNotNull("Document Header should not be null after loaded from the DB.", document.getDocumentHeader());
        assertNotNull("Document FinDocNumber should not be null after loaded from the DB.", document.getDocumentHeader().getFinancialDocumentNumber());
        
        //  document should show up as a 'New' document
        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());
        
        //  Maintainable should be populated and contain the right class
        newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be DelegateChangeDocument.", DelegateChangeDocument.class, newMaintainable.getBoClass());

        //  BO should be non-null and the right class
        bo = (DelegateChangeDocument) newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", bo);
        assertEquals("New BO should be of the correct class.", DelegateChangeDocument.class, bo.getClass());
        
        //  List should contain 3 elements
        assertNotNull("AccountDetail list should not be null.", bo.getAccountChangeDetails());
        List accounts = bo.getAccountChangeDetails();
        assertEquals("AccountDetail list should not be empty.", false, accounts.isEmpty());
        assertEquals("AccountDetail list should contain 3 elements.", 3, accounts.size());
        
        //  make sure all the accounts are non-null and at least have the Chart populated
        for (Iterator iter = accounts.iterator(); iter.hasNext();) {
            AccountChangeDetail accountDetail = (AccountChangeDetail) iter.next();
            
            assertNotNull("AccountDetailChange should not be null.", accountDetail);
            assertNotNull("ChartOfAccountsCode", accountDetail.getChartOfAccountsCode());
            assertEquals("Account Chart should be known.", "BL", accountDetail.getChartOfAccountsCode());
        }
    }

    public void testLocking_Delegate_GlobalLocks() throws WorkflowException {
        
        MaintenanceDocument document = (MaintenanceDocument) docService.getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        
        //  get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        DelegateChangeDocument bo = (DelegateChangeDocument) newMaintainable.getBusinessObject();
        String finDocNumber = document.getFinancialDocumentNumber();
        
        bo.setAccountDelegatePrimaryRoutingCode("X");
        bo.setAccountDelegateStartDate(new java.sql.Date(2006, 6, 1));
        bo.setAccountDelegateUniversalId("20000000");
        bo.setApprovalFromThisAmount(new KualiDecimal(0));
        bo.setApprovalToThisAmount(new KualiDecimal(0));
        bo.setFinancialDocumentTypeCode("ALL");

        AccountChangeDetail account;
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031400");
        bo.addAccount(account);
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031420");
        bo.addAccount(account);
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031467");
        bo.addAccount(account);
        
        docService.save(document, null, null);
        
        //  clear the document, and re-load it from the DB
        document = null;
        document = (MaintenanceDocument) docService.getByDocumentHeaderId(finDocNumber);
        assertNotNull("Document should not be null after loaded from the DB.", document);
        assertNotNull("Document Header should not be null after loaded from the DB.", document.getDocumentHeader());
        assertNotNull("Document FinDocNumber should not be null after loaded from the DB.", document.getDocumentHeader().getFinancialDocumentNumber());
        
        //  document should show up as a 'New' document
        assertEquals("Global document should always appear as a New.", true, document.isNew());
        assertEquals("Global document should never appear as an edit.", false, document.isEdit());
        
        //  Maintainable should be populated and contain the right class
        newMaintainable = document.getNewMaintainableObject();
        assertNotNull("New Maintainable should never be null.", newMaintainable);
        assertEquals("BO Class should be DelegateChangeDocument.", DelegateChangeDocument.class, newMaintainable.getBoClass());

        //  BO should be non-null and the right class
        bo = (DelegateChangeDocument) newMaintainable.getBusinessObject();
        assertNotNull("New BO should never be null.", bo);
        assertEquals("New BO should be of the correct class.", DelegateChangeDocument.class, bo.getClass());
        
        //  List should contain 3 elements
        assertNotNull("AccountDetail list should not be null.", bo.getAccountChangeDetails());
        List accounts = bo.getAccountChangeDetails();
        assertEquals("AccountDetail list should not be empty.", false, accounts.isEmpty());
        assertEquals("AccountDetail list should contain 3 elements.", 3, accounts.size());
        
        //  make sure all the accounts are non-null and at least have the Chart populated
        for (Iterator iter = accounts.iterator(); iter.hasNext();) {
            AccountChangeDetail accountDetail = (AccountChangeDetail) iter.next();
            
            assertNotNull("AccountDetailChange should not be null.", accountDetail);
            assertNotNull("ChartOfAccountsCode", accountDetail.getChartOfAccountsCode());
            assertEquals("Account Chart should be known.", "BL", accountDetail.getChartOfAccountsCode());
        }

        document = (MaintenanceDocument) docService.getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        
        //  get local references to the Maintainable and the BO
        newMaintainable = document.getNewMaintainableObject();
        bo = (DelegateChangeDocument) newMaintainable.getBusinessObject();
        finDocNumber = document.getFinancialDocumentNumber();
        
        bo.setAccountDelegatePrimaryRoutingCode("X");
        bo.setAccountDelegateStartDate(new java.sql.Date(2006, 6, 1));
        bo.setAccountDelegateUniversalId("3000000");
        bo.setApprovalFromThisAmount(new KualiDecimal(0));
        bo.setApprovalToThisAmount(new KualiDecimal(0));
        bo.setFinancialDocumentTypeCode("ALL");

        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031400");
        bo.addAccount(account);
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031420");
        bo.addAccount(account);
        
        account = new AccountChangeDetail();
        account.setFinancialDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode("BL");
        account.setAccountNumber("1031467");
        bo.addAccount(account);
        
        docService.save(document, null, null);
        
    }
*/  
    
}
