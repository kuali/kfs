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

package org.kuali.kfs.module.ar.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.integration.cg.ContractAndGrantsProposal;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsCategories;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARProposalFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceAccountDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceGeneralDetailFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * This class tests the ContractsGrantsInvoiceDocument class
 */
@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceDocumentTest extends KualiTestBase {
    public ContractsAndGrantsCategories category;
    public ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument;

    protected void setUp() throws Exception {
        super.setUp();
        category = new ContractsAndGrantsCategories();
        category.setCategoryCode("testCode");
        category.setCategoryDescription("testDescription");
        category.setCategoryName("testName");
        category.setCategoryObjectCodes("5000, 6000-6011, 700*");
        contractsGrantsInvoiceDocument = new ContractsGrantsInvoiceDocument();

    }

    public void testGetObjectCodeArrayFromSingleCategory() {
        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        Set<String> resultSet =  contractsGrantsInvoiceDocumentService.getObjectCodeArrayFromSingleCategory (category,contractsGrantsInvoiceDocument);
        Set<String> expectedResult = new HashSet<String>();
        expectedResult.add("5000");

        expectedResult.add("6000");
        expectedResult.add("6001");
        expectedResult.add("6002");
        expectedResult.add("6003");
        expectedResult.add("6004");
        expectedResult.add("6005");
        expectedResult.add("6006");
        expectedResult.add("6007");
        expectedResult.add("6008");
        expectedResult.add("6009");
        expectedResult.add("600a");
        expectedResult.add("600b");
        expectedResult.add("600c");
        expectedResult.add("600d");
        expectedResult.add("600e");
        expectedResult.add("600f");
        expectedResult.add("600g");
        expectedResult.add("600h");
        expectedResult.add("600i");
        expectedResult.add("600j");
        expectedResult.add("600k");
        expectedResult.add("600l");
        expectedResult.add("600m");
        expectedResult.add("600n");
        expectedResult.add("600o");
        expectedResult.add("600p");
        expectedResult.add("600q");
        expectedResult.add("600r");
        expectedResult.add("600s");
        expectedResult.add("600t");
        expectedResult.add("600u");
        expectedResult.add("600v");
        expectedResult.add("600w");
        expectedResult.add("600x");
        expectedResult.add("600y");
        expectedResult.add("600z");
        expectedResult.add("6010");
        expectedResult.add("6011");

        expectedResult.add("7000");
        expectedResult.add("7001");
        expectedResult.add("7002");
        expectedResult.add("7003");
        expectedResult.add("7004");
        expectedResult.add("7005");
        expectedResult.add("7006");
        expectedResult.add("7007");
        expectedResult.add("7008");
        expectedResult.add("7009");
        expectedResult.add("700a");
        expectedResult.add("700b");
        expectedResult.add("700c");
        expectedResult.add("700d");
        expectedResult.add("700e");
        expectedResult.add("700f");
        expectedResult.add("700g");
        expectedResult.add("700h");
        expectedResult.add("700i");
        expectedResult.add("700j");
        expectedResult.add("700k");
        expectedResult.add("700l");
        expectedResult.add("700m");
        expectedResult.add("700n");
        expectedResult.add("700o");
        expectedResult.add("700p");
        expectedResult.add("700q");
        expectedResult.add("700r");
        expectedResult.add("700s");
        expectedResult.add("700t");
        expectedResult.add("700u");
        expectedResult.add("700v");
        expectedResult.add("700w");
        expectedResult.add("700x");
        expectedResult.add("700y");
        expectedResult.add("700z");

        assertTrue(expectedResult.containsAll(resultSet));
        assertTrue(resultSet.containsAll(expectedResult));
    }

    public void testUpdateLastBilledDate() {

        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument_1 = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument_2 = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        ContractAndGrantsProposal proposal = ARProposalFixture.CG_PRPSL1.createProposal();

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD1.createAward();

        award.getActiveAwardAccounts().clear();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        ContractsAndGrantsBillingAwardAccount awardAccount_2 = ARAwardAccountFixture.AWD_ACCT_2.createAwardAccount();

        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        awardAccounts.add(awardAccount_2);

        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setAwardAccountsToAward(award.getProposalNumber(), awardAccounts);

        contractsGrantsInvoiceDocument_1.setAward(award);
        contractsGrantsInvoiceDocument_2.setAward(award);


        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL3.createInvoiceAccountDetail();

        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument_1.setAccountDetails(accountDetails);
        accountDetails = new ArrayList<InvoiceAccountDetail>();
        InvoiceAccountDetail invoiceAccountDetail_2 = InvoiceAccountDetailFixture.INV_ACCT_DTL4.createInvoiceAccountDetail();
        accountDetails.add(invoiceAccountDetail_2);
        contractsGrantsInvoiceDocument_2.setAccountDetails(accountDetails);

        InvoiceGeneralDetail invoiceGeneralDetail_1 = InvoiceGeneralDetailFixture.INV_GNRL_DTL3.createInvoiceGeneralDetail();
        InvoiceGeneralDetail invoiceGeneralDetail_2 = InvoiceGeneralDetailFixture.INV_GNRL_DTL3.createInvoiceGeneralDetail();
        // To set last Billed Date to invoice and check if they are being set to award Accounts and award properly.
        Date lastBilledDate_1 = Date.valueOf("2011-10-26");
        Date lastBilledDate_2 = Date.valueOf("2011-10-27");
        invoiceGeneralDetail_1.setLastBilledDate(lastBilledDate_1);
        invoiceGeneralDetail_2.setLastBilledDate(lastBilledDate_2);
        contractsGrantsInvoiceDocument_1.setInvoiceGeneralDetail(invoiceGeneralDetail_1);
        contractsGrantsInvoiceDocument_2.setInvoiceGeneralDetail(invoiceGeneralDetail_2);
        // Now there are two invoices, to check combinations of invoices, when one by one go to final and corrected.


        String invoiceStatus = "FINAL";
        // 1. invoice 1 is set to final.
        contractsGrantsInvoiceDocumentService.updateLastBilledDate(invoiceStatus,contractsGrantsInvoiceDocument_1);
        // Now to retrieve the Award Account and check the values.

        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_1.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount_1.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount_1.getProposalNumber());
        ContractsAndGrantsBillingAwardAccount awdAcct_1 = (ContractsAndGrantsBillingAwardAccount) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_2.getAccountNumber());
        ContractsAndGrantsBillingAwardAccount awdAcct_2 = (ContractsAndGrantsBillingAwardAccount) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);


        assertEquals(lastBilledDate_1, awdAcct_1.getCurrentLastBilledDate());

        assertEquals(null, awdAcct_2.getCurrentLastBilledDate());
        assertEquals(null, award.getLastBilledDate());

        // 2. invoice 2 is set to final.
        contractsGrantsInvoiceDocumentService.updateLastBilledDate(invoiceStatus,contractsGrantsInvoiceDocument_2);
        mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_1.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount_1.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount_1.getProposalNumber());
        awdAcct_1 = (ContractsAndGrantsBillingAwardAccount) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_2.getAccountNumber());
        awdAcct_2 = (ContractsAndGrantsBillingAwardAccount) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);

        assertEquals(lastBilledDate_1, awdAcct_1.getCurrentLastBilledDate());
        assertEquals(lastBilledDate_2, awdAcct_2.getCurrentLastBilledDate());
        mapKey.clear();
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        award = (ContractsAndGrantsBillingAward) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, mapKey);
        assertEquals(lastBilledDate_1, award.getLastBilledDate());

        invoiceStatus = "CORRECTED";
        // 3. invoice 1 or 2 is corrected.
        contractsGrantsInvoiceDocumentService.updateLastBilledDate(invoiceStatus,contractsGrantsInvoiceDocument_1);
        mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_1.getAccountNumber());
        mapKey.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount_1.getChartOfAccountsCode());
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount_1.getProposalNumber());
        awdAcct_1 = (ContractsAndGrantsBillingAwardAccount) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);
        mapKey.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount_2.getAccountNumber());
        awdAcct_2 = (ContractsAndGrantsBillingAwardAccount) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAwardAccount.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAwardAccount.class, mapKey);

        assertEquals(null, awdAcct_1.getCurrentLastBilledDate());
        assertEquals(lastBilledDate_2, awdAcct_2.getCurrentLastBilledDate());
        mapKey.clear();
        mapKey.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        award = (ContractsAndGrantsBillingAward) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, mapKey);

        assertEquals(null, award.getLastBilledDate());


    }

}
