/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley)
public class DelegateGlobalTest extends KualiTestBase {

    private static final String GLOBAL_DELEGATE_TYPENAME = "GDLG";

    private static final Log LOG = LogFactory.getLog(DelegateGlobalTest.class);

    private static final java.sql.Date START_DATE_1 = KfsDateUtils.newDate(2006, 6, 1);
    private static final java.sql.Date START_DATE_2 = KfsDateUtils.newDate(2006, 5, 1);
    private static final String DELEGATE_ID_1 = "4287701175"; // AFGORMAN BL-UDIV
    private static final String DELEGATE_ID_2 = "1571201547"; // BBURGER BL-VPIT
    private static final String DELEGATE_ID_3 = "1872708343"; // cswinson BL-VPGA
    private static final KualiDecimal FROM_AMOUNT_1 = KualiDecimal.ZERO;
    private static final KualiDecimal FROM_AMOUNT_2 = KualiDecimal.ZERO;
    private static final KualiDecimal FROM_AMOUNT_3 = new KualiDecimal(1000);
    private static final KualiDecimal TO_AMOUNT_1 = KualiDecimal.ZERO;
    private static final KualiDecimal TO_AMOUNT_2 = new KualiDecimal(10000);
    private static final KualiDecimal TO_AMOUNT_3 = new KualiDecimal(5000);
    private static final String DOC_TYPE_ALL = KFSConstants.ROOT_DOCUMENT_TYPE;
    private static final String DOC_TYPE_ACCT = "ACCT";
    private static final String DOC_TYPE_CR = "CR";
    private static final String DOC_TYPE_TF = "TF";
    private static final String COA1 = "BL";
    private static final String ACCOUNT1 = "1031400";
    private static final String ACCOUNT2 = "1031420";
    private static final String ACCOUNT3 = "1031467";
    private static final String ACCOUNT_BAD = "1031421";


    public void testApplyGlobalChanges_Empty() throws WorkflowException {

        MaintenanceDocument document;
        document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        document.getDocumentHeader().setDocumentDescription("blah");

        // get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        AccountDelegateGlobal bo = (AccountDelegateGlobal) newMaintainable.getBusinessObject();

        List<AccountDelegateGlobalDetail> changeDocuments = new ArrayList();
        bo.setDelegateGlobals(changeDocuments);

        List<AccountGlobalDetail> accountDetails = new ArrayList();
        bo.setAccountGlobalDetails(accountDetails);

        GlobalBusinessObject gbo = (GlobalBusinessObject) bo;
        List<PersistableBusinessObject> persistables = gbo.generateGlobalChangesToPersist();

        assertTrue("Global Changes returned should be an empty list.", persistables.isEmpty());
    }

    @SuppressWarnings("deprecation")
    public void testApplyGlobalChanges1() throws WorkflowException {

        MaintenanceDocument document;
        document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        document.getDocumentHeader().setDocumentDescription("blah");

        // get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        AccountDelegateGlobal bo = (AccountDelegateGlobal) newMaintainable.getBusinessObject();
        String finDocNumber = document.getDocumentHeader().getDocumentNumber();

        // create the lists
        List<AccountDelegateGlobalDetail> changeDocuments = new ArrayList();
        List<AccountGlobalDetail> accountDetails = new ArrayList();

        // add a delegate change document
        AccountDelegateGlobalDetail change = new AccountDelegateGlobalDetail();
        change.setDocumentNumber(finDocNumber);
        change.setAccountDelegatePrimaryRoutingIndicator(true);
        change.setAccountDelegateStartDate(START_DATE_1);
        change.setAccountDelegateUniversalId(DELEGATE_ID_1);
        change.setApprovalFromThisAmount(FROM_AMOUNT_1);
        change.setApprovalToThisAmount(TO_AMOUNT_1);
        change.setFinancialDocumentTypeCode(DOC_TYPE_ALL);
        changeDocuments.add(change);

        bo.setDelegateGlobals(changeDocuments);

        // add account change detail records
        AccountGlobalDetail account;
        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode(COA1);
        account.setAccountNumber(ACCOUNT1);
        accountDetails.add(account);

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode(COA1);
        account.setAccountNumber(ACCOUNT2);
        accountDetails.add(account);

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode(COA1);
        account.setAccountNumber(ACCOUNT3);
        accountDetails.add(account);

        bo.setAccountGlobalDetails(accountDetails);

        GlobalBusinessObject gbo = (GlobalBusinessObject) bo;
        List<PersistableBusinessObject> persistables = gbo.generateGlobalChangesToPersist();

        assertFalse("The list should not be empty.", persistables.isEmpty());
        assertEquals("There should be three output records.", 3, persistables.size());

    }

    @SuppressWarnings("deprecation")
    public void testApplyGlobalChanges2() throws WorkflowException {

        MaintenanceDocument document;
        document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        document.getDocumentHeader().setDocumentDescription("blah");

        // get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        AccountDelegateGlobal bo = (AccountDelegateGlobal) newMaintainable.getBusinessObject();
        String finDocNumber = document.getDocumentHeader().getDocumentNumber();

        // create the lists
        List<AccountDelegateGlobalDetail> changeDocuments = new ArrayList();
        List<AccountGlobalDetail> accountDetails = new ArrayList();

        // add a delegate change document
        AccountDelegateGlobalDetail change;
        change = new AccountDelegateGlobalDetail();
        change.setDocumentNumber(finDocNumber);
        change.setAccountDelegatePrimaryRoutingIndicator(true);
        change.setAccountDelegateStartDate(START_DATE_1);
        change.setAccountDelegateUniversalId(DELEGATE_ID_1);
        change.setApprovalFromThisAmount(FROM_AMOUNT_1);
        change.setApprovalToThisAmount(TO_AMOUNT_1);
        change.setFinancialDocumentTypeCode(DOC_TYPE_ALL);
        changeDocuments.add(change);

        change = new AccountDelegateGlobalDetail();
        change.setDocumentNumber(finDocNumber);
        change.setAccountDelegatePrimaryRoutingIndicator(true);
        change.setAccountDelegateStartDate(START_DATE_2);
        change.setAccountDelegateUniversalId(DELEGATE_ID_2);
        change.setApprovalFromThisAmount(FROM_AMOUNT_3);
        change.setApprovalToThisAmount(TO_AMOUNT_3);
        change.setFinancialDocumentTypeCode(DOC_TYPE_ACCT);
        changeDocuments.add(change);

        bo.setDelegateGlobals(changeDocuments);

        // add account change detail records
        AccountGlobalDetail account;
        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode(COA1);
        account.setAccountNumber(ACCOUNT1);
        accountDetails.add(account);

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode(COA1);
        account.setAccountNumber(ACCOUNT2);
        accountDetails.add(account);

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode(COA1);
        account.setAccountNumber(ACCOUNT3);
        accountDetails.add(account);

        bo.setAccountGlobalDetails(accountDetails);

        GlobalBusinessObject gbo = (GlobalBusinessObject) bo;
        List<PersistableBusinessObject> persistables = gbo.generateGlobalChangesToPersist();

        assertFalse("The list should not be empty.", persistables.isEmpty());
        assertEquals("There should be six output records.", 6, persistables.size());

    }

    @SuppressWarnings("deprecation")
    public void testApplyGlobalChanges3() throws WorkflowException {

        MaintenanceDocument document;
        document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(GLOBAL_DELEGATE_TYPENAME);
        document.getDocumentHeader().setDocumentDescription("blah");

        // get local references to the Maintainable and the BO
        Maintainable newMaintainable = document.getNewMaintainableObject();
        AccountDelegateGlobal bo = (AccountDelegateGlobal) newMaintainable.getBusinessObject();
        String finDocNumber = document.getDocumentHeader().getDocumentNumber();

        // create the lists
        List<AccountDelegateGlobalDetail> changeDocuments = new ArrayList();
        List<AccountGlobalDetail> accountDetails = new ArrayList();

        // add a delegate change document
        AccountDelegateGlobalDetail change = new AccountDelegateGlobalDetail();
        change.setDocumentNumber(finDocNumber);
        change.setAccountDelegatePrimaryRoutingIndicator(true);
        change.setAccountDelegateStartDate(START_DATE_1);
        change.setAccountDelegateUniversalId(DELEGATE_ID_1);
        change.setApprovalFromThisAmount(FROM_AMOUNT_1);
        change.setApprovalToThisAmount(TO_AMOUNT_1);
        change.setFinancialDocumentTypeCode(DOC_TYPE_ALL);
        changeDocuments.add(change);

        bo.setDelegateGlobals(changeDocuments);

        // add account change detail records
        AccountGlobalDetail account;
        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode(COA1);
        account.setAccountNumber(ACCOUNT1);
        accountDetails.add(account);

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode(COA1);
        account.setAccountNumber(ACCOUNT2);
        accountDetails.add(account);

        account = new AccountGlobalDetail();
        account.setDocumentNumber(finDocNumber);
        account.setChartOfAccountsCode(COA1);
        account.setAccountNumber(ACCOUNT_BAD);
        accountDetails.add(account);

        bo.setAccountGlobalDetails(accountDetails);

        GlobalBusinessObject gbo = (GlobalBusinessObject) bo;

        boolean errorHappened = false;
        try {
            List<PersistableBusinessObject> persistables = gbo.generateGlobalChangesToPersist();
        }
        catch (RuntimeException e) {
            errorHappened = true;
        }

        assertTrue("The expected error should have occurred.", errorHappened);
    }

}

