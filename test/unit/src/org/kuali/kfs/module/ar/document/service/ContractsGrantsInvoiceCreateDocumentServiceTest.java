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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ARAgencyFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceAccountDetailFixture;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * This class tests the ContractsGrantsInvoiceCreateDocumentService
 */
@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceCreateDocumentServiceTest extends KualiTestBase {


    @Override
    protected void setUp() throws Exception {

        super.setUp();
    }

    @SuppressWarnings("deprecation")
    public void testCreateCGInvoiceDocumentByAwardInfo() {

        String coaCode = "BL";
        String orgCode = "SRS";

        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument_1 = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        contractsGrantsInvoiceDocument_1.setBillByChartOfAccountCode(coaCode);
        contractsGrantsInvoiceDocument_1.setBilledByOrganizationCode(orgCode);

        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        accountsReceivableDocumentHeader.setDocumentNumber(contractsGrantsInvoiceDocument_1.getDocumentNumber());
        accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(coaCode);
        accountsReceivableDocumentHeader.setProcessingOrganizationCode(orgCode);

        contractsGrantsInvoiceDocument_1.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.createAward();
        ContractsAndGrantsBillingAgency agency = ARAgencyFixture.CG_AGENCY1.createAgency();
        ContractsAndGrantsBillingAwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_1.createAwardAccount();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        awardAccounts.add(awardAccount_1);
        award.getActiveAwardAccounts().clear();

        award.getActiveAwardAccounts().add(awardAccount_1);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_NULL.setAgencyFromFixture((Award) award);


        contractsGrantsInvoiceDocument_1.setAward(award);
        InvoiceAccountDetail invoiceAccountDetail_1 = InvoiceAccountDetailFixture.INV_ACCT_DTL3.createInvoiceAccountDetail();

        List<InvoiceAccountDetail> accountDetails = new ArrayList<InvoiceAccountDetail>();
        accountDetails.add(invoiceAccountDetail_1);
        contractsGrantsInvoiceDocument_1.setAccountDetails(accountDetails);

        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument_2;


        contractsGrantsInvoiceDocument_2 = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode);

        assertEquals(contractsGrantsInvoiceDocument_1.getProposalNumber(), contractsGrantsInvoiceDocument_2.getProposalNumber());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountDetails().get(0).getAccountNumber(), contractsGrantsInvoiceDocument_2.getAccountDetails().get(0).getAccountNumber());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountDetails().get(0).getChartOfAccountsCode(), contractsGrantsInvoiceDocument_2.getAccountDetails().get(0).getChartOfAccountsCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode(), contractsGrantsInvoiceDocument_2.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode(), contractsGrantsInvoiceDocument_2.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getBillByChartOfAccountCode(), contractsGrantsInvoiceDocument_2.getBillByChartOfAccountCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getBilledByOrganizationCode(), contractsGrantsInvoiceDocument_2.getBilledByOrganizationCode());


    }

    public void testValidateAwards(){

        ContractsAndGrantsBillingAward invalidAward = ARAwardFixture.CG_AWARD2.createAward();
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();
        awards.add(invalidAward);
        // To retrieve the batch file directory name as "reports/ar"
        ModuleConfiguration systemConfiguration = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode("KFS-AR").getModuleConfiguration();

        String destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);


        String errorOutputFile = destinationFolderPath + "JUNIT TEST.log";

        Collection<ContractsAndGrantsBillingAward> validAwards = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).validateAwards(awards, errorOutputFile);

        assertTrue(CollectionUtils.isEmpty(validAwards));


    }

}
