/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.batch.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAgencyFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFundManagerFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceAccountDetailFixture;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;

@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceCreateDocumentServiceTest extends KualiTestBase {

    protected ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected KualiModuleService kualiModuleService;
    private String errorOutputFile;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        contractsGrantsInvoiceCreateDocumentService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        documentService = SpringContext.getBean(DocumentService.class);
        kualiModuleService = SpringContext.getBean(KualiModuleService.class);

        ModuleConfiguration systemConfiguration = kualiModuleService.getModuleServiceByNamespaceCode(KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE).getModuleConfiguration();
        String destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);
        errorOutputFile = destinationFolderPath + "JUNIT TEST.log";
    }

    @Override
    public void tearDown() throws Exception {
        File errors = new File(errorOutputFile);

        if (errors.exists()) {
            errors.delete();
        }

        super.tearDown();
    }

    public void testCreateCGInvoiceDocumentByAwardInfo() {
        String coaCode = "BL";
        String orgCode = "UGCS";

        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument_1 = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        contractsGrantsInvoiceDocument_1.setBillByChartOfAccountCode(coaCode);
        contractsGrantsInvoiceDocument_1.setBilledByOrganizationCode(orgCode);

        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        accountsReceivableDocumentHeader.setDocumentNumber(contractsGrantsInvoiceDocument_1.getDocumentNumber());

        List<String> procCodes = contractsGrantsInvoiceDocumentService.getProcessingFromBillingCodes(coaCode, orgCode);
        accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(procCodes.get(0));
        accountsReceivableDocumentHeader.setProcessingOrganizationCode(procCodes.get(1));

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

        List<String> errLines = new ArrayList<String>();
        contractsGrantsInvoiceDocument_2 = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode, errLines);

        assertEquals(contractsGrantsInvoiceDocument_1.getProposalNumber(), contractsGrantsInvoiceDocument_2.getProposalNumber());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountDetails().get(0).getAccountNumber(), contractsGrantsInvoiceDocument_2.getAccountDetails().get(0).getAccountNumber());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountDetails().get(0).getChartOfAccountsCode(), contractsGrantsInvoiceDocument_2.getAccountDetails().get(0).getChartOfAccountsCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode(), contractsGrantsInvoiceDocument_2.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode(), contractsGrantsInvoiceDocument_2.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getBillByChartOfAccountCode(), contractsGrantsInvoiceDocument_2.getBillByChartOfAccountCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getBilledByOrganizationCode(), contractsGrantsInvoiceDocument_2.getBilledByOrganizationCode());
    }

    public void testValidateManualAwardsOneValidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs);

        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should be empty.", contractsGrantsInvoiceDocumentErrorLogs.size() == 0);
        assertTrue("validAwards should contain one award.", validAwards.size() == 1);
        assertTrue("validAwards should contain our initial award.", validAwards.contains(awards.get(0)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("no errors should be persisted", persistedErrors.size() == 0);
    }

    public void testValidateManualAwardsOneInvalidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setSuspendInvoicingIndicator(true);
        List<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs);

        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain one award.", contractsGrantsInvoiceDocumentErrorLogs.size() == 1);
        assertTrue("validAwards should be empty.", validAwards.size() == 0);
        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our initial award.", errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(0)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertFalse("batch should be false", persistedError.isBatch());
        }
    }

    public void testValidateManualAwardsTwoValidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        ((Award)awards2.get(0)).setProposalNumber(new Long(11));
        awards.addAll(awards2);
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs);

        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should be empty.", contractsGrantsInvoiceDocumentErrorLogs.size() == 0);
        assertTrue("validAwards should contain one award.", validAwards.size() == 2);
        assertTrue("validAwards should contain our first award.", validAwards.contains(awards.get(0)));
        assertTrue("validAwards should contain our second award.", validAwards.contains(awards.get(1)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("no errors should be persisted", persistedErrors.size() == 0);
    }

    public void testValidateManualAwardsTwoInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setSuspendInvoicingIndicator(true);
        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        ((Award)awards2.get(0)).setSuspendInvoicingIndicator(true);
        ((Award)awards2.get(0)).setProposalNumber(new Long(11));
        awards.addAll(awards2);
        List<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs);

        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain two awards.", contractsGrantsInvoiceDocumentErrorLogs.size() == 2);
        assertTrue("validAwards should be empty.", validAwards.size() == 0);
        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our first award.", errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(0)));
        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our second award.", errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(1)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("two errors should be persisted", persistedErrors.size() == 2);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertFalse("batch should be false", persistedError.isBatch());
        }
    }

    public void testValidateManualAwardsOneValidOneInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        ((Award)awards2.get(0)).setSuspendInvoicingIndicator(true);
        ((Award)awards2.get(0)).setProposalNumber(new Long(11));
        awards.addAll(awards2);
        List<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs);

        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain one award.", contractsGrantsInvoiceDocumentErrorLogs.size() == 1);
        assertTrue("validAwards should contain one award.", validAwards.size() == 1);
        assertTrue("validAwards should contain our first award.", validAwards.contains(awards.get(0)));
        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our second award.", errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(1)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertFalse("batch should be false", persistedError.isBatch());
        }
    }

    public void testValidateBatchAwardsOneValidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, errorOutputFile);

        assertTrue("validAwards should contain one award.", validAwards.size() == 1);
        assertTrue("validAwards should contain our initial award.", validAwards.contains(awards.get(0)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("no errors should be persisted", persistedErrors.size() == 0);

        File errors = new File(errorOutputFile);
        assertFalse("no errors should be written", errors.exists());
    }

    public void testValidateBatchAwardsOneInvalidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setSuspendInvoicingIndicator(true);

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, errorOutputFile);

        assertTrue("validAwards should be empty.", validAwards.size() == 0);

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("batch should be true", persistedError.isBatch());
        }

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
    }

    public void testValidateBatchAwardsTwoValidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        ((Award)awards2.get(0)).setProposalNumber(new Long(11));
        awards.addAll(awards2);

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, errorOutputFile);

        assertTrue("validAwards should contain one award.", validAwards.size() == 2);
        assertTrue("validAwards should contain our first award.", validAwards.contains(awards.get(0)));
        assertTrue("validAwards should contain our second award.", validAwards.contains(awards.get(1)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("no errors should be persisted", persistedErrors.size() == 0);

        File errors = new File(errorOutputFile);
        assertFalse("no errors should be written", errors.exists());
    }

    public void testValidateBatchAwardsTwoInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        ((Award)awards.get(0)).setSuspendInvoicingIndicator(true);
        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        ((Award)awards2.get(0)).setSuspendInvoicingIndicator(true);
        ((Award)awards2.get(0)).setProposalNumber(new Long(11));
        awards.addAll(awards2);

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, errorOutputFile);

        assertTrue("validAwards should be empty.", validAwards.size() == 0);

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("two errors should be persisted", persistedErrors.size() == 2);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("batch should be true", persistedError.isBatch());
        }

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
    }

    public void testValidateBatchAwardsOneValidOneInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        ((Award)awards2.get(0)).setSuspendInvoicingIndicator(true);
        ((Award)awards2.get(0)).setProposalNumber(new Long(11));
        awards.addAll(awards2);

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, errorOutputFile);

        assertTrue("validAwards should contain one award.", validAwards.size() == 1);
        assertTrue("validAwards should contain our first award.", validAwards.contains(awards.get(0)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("batch should be true", persistedError.isBatch());
        }

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
    }

    private boolean errorLogContainsAward(Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs, ContractsAndGrantsBillingAward award) {
        for (ContractsGrantsInvoiceDocumentErrorLog contractsGrantsInvoiceDocumentErrorLog: contractsGrantsInvoiceDocumentErrorLogs) {
            if (contractsGrantsInvoiceDocumentErrorLog.getProposalNumber().equals(award.getProposalNumber())) {
                return true;
            }
        }
        return false;
    }

    protected List<ContractsAndGrantsBillingAward> setupAwards() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        ContractsAndGrantsBillingAward award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.createAward();
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAgencyFromFixture((Award) award);
        award.getActiveAwardAccounts().clear();
        AwardAccount awardAccount_1 = ARAwardAccountFixture.AWD_ACCT_WITH_CCA_1.createAwardAccount();
        awardAccount_1.refreshReferenceObject("account");
        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        awardAccounts.add(awardAccount_1);
        ((Award)award).setAwardAccounts(awardAccounts);
        award = ARAwardFixture.CG_AWARD_MONTHLY_BILLED_DATE_VALID.setAwardOrganizationFromFixture((Award) award);
        AwardFundManager awardFundManager = ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager();
        ((Award)award).getAwardFundManagers().add(awardFundManager);
        ((Award)award).setAwardPrimaryFundManager(ARAwardFundManagerFixture.AWD_FND_MGR1.createAwardFundManager());
        awards.add(award);

        return awards;
    }

}
