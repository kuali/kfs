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
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.PredeterminedBillingSchedule;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAgencyFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFundManagerFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceAccountDetailFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceBillFixture;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ErrorMessage;

@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceCreateDocumentServiceTest extends KualiTestBase {

    protected ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected BusinessObjectService businessObjectService;
    protected ConfigurationService configurationService;
    protected DocumentService documentService;
    protected KualiModuleService kualiModuleService;
    protected AccountingPeriodService accountingPeriodService;
    protected VerifyBillingFrequencyService verifyBillingFrequencyService;

    private String errorOutputFile;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        contractsGrantsInvoiceCreateDocumentService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        documentService = SpringContext.getBean(DocumentService.class);
        kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        configurationService = SpringContext.getBean(ConfigurationService.class);
        accountingPeriodService = SpringContext.getBean(AccountingPeriodService.class);
        verifyBillingFrequencyService = SpringContext.getBean(VerifyBillingFrequencyService.class);

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

    public void testValidateManualAwardsOneValidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

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

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain one award.", contractsGrantsInvoiceDocumentErrorLogs.size() == 1);
        assertTrue("validAwards should be empty.", validAwards.size() == 0);
        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our initial award.", errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(0)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
        }
    }

    public void testValidateManualAwardsTwoValidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        ((Award)awards2.get(0)).setProposalNumber(new Long(11));
        awards.addAll(awards2);
        Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

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

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain two awards.", contractsGrantsInvoiceDocumentErrorLogs.size() == 2);
        assertTrue("validAwards should be empty.", validAwards.size() == 0);
        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our first award.", errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(0)));
        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our second award.", errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(1)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("two errors should be persisted", persistedErrors.size() == 2);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
        }
    }

    public void testValidateManualAwardsOneValidOneInvalidAwards() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        ((Award)awards2.get(0)).setSuspendInvoicingIndicator(true);
        ((Award)awards2.get(0)).setProposalNumber(new Long(11));
        awards.addAll(awards2);
        List<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());

        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain one award.", contractsGrantsInvoiceDocumentErrorLogs.size() == 1);
        assertTrue("validAwards should contain one award.", validAwards.size() == 1);
        assertTrue("validAwards should contain our first award.", validAwards.contains(awards.get(0)));
        assertTrue("contractsGrantsInvoiceDocumentErrorLogs should contain our second award.", errorLogContainsAward(contractsGrantsInvoiceDocumentErrorLogs, awards.get(1)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
        }
    }

    public void testValidateBatchAwardsOneValidAward() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, null, errorOutputFile, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

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

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, null, errorOutputFile, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

        assertTrue("validAwards should be empty.", validAwards.size() == 0);

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
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

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, null, errorOutputFile, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

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

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, null, errorOutputFile, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

        assertTrue("validAwards should be empty.", validAwards.size() == 0);

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("two errors should be persisted", persistedErrors.size() == 2);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
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

        Collection<ContractsAndGrantsBillingAward> validAwards = contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, null, errorOutputFile, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode());

        assertTrue("validAwards should contain one award.", validAwards.size() == 1);
        assertTrue("validAwards should contain our first award.", validAwards.contains(awards.get(0)));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
        }

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
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

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        contractsGrantsInvoiceDocument_2 = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentByAwardInfo(award, awardAccounts, coaCode, orgCode, errorMessages);

        assertEquals(contractsGrantsInvoiceDocument_1.getProposalNumber(), contractsGrantsInvoiceDocument_2.getProposalNumber());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountDetails().get(0).getAccountNumber(), contractsGrantsInvoiceDocument_2.getAccountDetails().get(0).getAccountNumber());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountDetails().get(0).getChartOfAccountsCode(), contractsGrantsInvoiceDocument_2.getAccountDetails().get(0).getChartOfAccountsCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode(), contractsGrantsInvoiceDocument_2.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode(), contractsGrantsInvoiceDocument_2.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getBillByChartOfAccountCode(), contractsGrantsInvoiceDocument_2.getBillByChartOfAccountCode());
        assertEquals(contractsGrantsInvoiceDocument_1.getBilledByOrganizationCode(), contractsGrantsInvoiceDocument_2.getBilledByOrganizationCode());
    }

    public void testManualCreateCGInvoiceDocumentsByAwardsOneValid() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();

        List<ErrorMessage> errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        assertTrue("errorMessages should be empty.", errorMessages.size() == 0);

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("no errors should be persisted", persistedErrors.size() == 0);
    }

    public void testManualCreateCGInvoiceDocumentsByAwardsEmptyAwardsList() {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        List<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();
        contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode());
        assertTrue(contractsGrantsInvoiceDocumentErrorLogs.size() == 0);

        List<ErrorMessage> errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_AWARD);

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testManualCreateCGInvoiceDocumentsByAwardsNullAwardsList() {
        List<ContractsAndGrantsBillingAward> awards = null;

        List<ErrorMessage> errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_AWARD);

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testManualCreateCGInvoiceDocumentsByAwardsNoOrg() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = (Award)awards.get(0);
        award.setAwardOrganizations(new ArrayList<AwardOrganization>());

        List<ErrorMessage> errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_ORGANIZATION_ON_AWARD);
        errorMessage = MessageFormat.format(errorMessage, award.getProposalNumber().toString());

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testManualCreateCGInvoiceDocumentsByAccountNonBillable() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setPreferredBillingFrequency(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award)awards2.get(0);
        award2.setInvoicingOptions(ArPropertyConstants.INV_ACCOUNT);

        errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NON_BILLABLE);
        errorMessage = MessageFormat.format(errorMessage, award2.getActiveAwardAccounts().get(0).getAccountNumber(), award.getProposalNumber().toString());

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testManualCreateCGInvoiceDocumentsByCCAContractAccountNotBillable() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setPreferredBillingFrequency(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award)awards2.get(0);
        award2.setInvoicingOptions(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT);

        errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.CONTROL_ACCOUNT_NON_BILLABLE);
        errorMessage = MessageFormat.format(errorMessage, award2.getActiveAwardAccounts().get(0).getAccount().getContractControlAccount().getAccountNumber(), award.getProposalNumber().toString());

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testManualCreateCGInvoiceDocumentsByAwardNotAllBillableAccounts() throws WorkflowException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setPreferredBillingFrequency(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award)awards2.get(0);
        award2.setInvoicingOptions(ArPropertyConstants.INV_AWARD);

        errorMessages = contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NOT_ALL_BILLABLE_ACCOUNTS);
        errorMessage = MessageFormat.format(errorMessage, award2.getProposalNumber().toString());

        assertTrue("errorMessages should not be empty.", errorMessages.size() == 1);
        assertTrue("errorMessages should contain the error we're expecting.", messagesContainsExpectedError(errorMessages, errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be manual", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.MANUAL.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardsOneValid() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();

        contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

        File errors = new File(errorOutputFile);
        assertFalse("errors should not be written", errors.exists());

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("no errors should be persisted", persistedErrors.size() == 0);
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardsEmptyAwardsList() throws IOException {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_AWARD);

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardsNullAwardsList() throws IOException {
        List<ContractsAndGrantsBillingAward> awards = null;

        contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_AWARD);

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardsNoOrg() throws IOException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = (Award)awards.get(0);
        award.setAwardOrganizations(new ArrayList<AwardOrganization>());

        contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_ORGANIZATION_ON_AWARD);
        errorMessage = MessageFormat.format(errorMessage, award.getProposalNumber().toString());

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByAccountNonBillable() throws WorkflowException, IOException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setPreferredBillingFrequency(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award)awards2.get(0);
        award2.setInvoicingOptions(ArPropertyConstants.INV_ACCOUNT);

        contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NON_BILLABLE);
        errorMessage = MessageFormat.format(errorMessage, award2.getActiveAwardAccounts().get(0).getAccountNumber(), award.getProposalNumber().toString());

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByCCAContractAccountNotBillable() throws WorkflowException, IOException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setPreferredBillingFrequency(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award)awards2.get(0);
        award2.setInvoicingOptions(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT);

        contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.CONTROL_ACCOUNT_NON_BILLABLE);
        errorMessage = MessageFormat.format(errorMessage, award2.getActiveAwardAccounts().get(0).getAccount().getContractControlAccount().getAccountNumber(), award.getProposalNumber().toString());

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardNotAllBillableAccounts() throws WorkflowException, IOException {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();
        Award award = ((Award)awards.get(0));
        award.setPreferredBillingFrequency(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE);
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        ContractsGrantsInvoiceDocument cgInvoice = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class).createCGInvoiceDocumentByAwardInfo(award, award.getActiveAwardAccounts(), "BL", "PSY", errorMessages);
        documentService.saveDocument(cgInvoice);
        setupBills(cgInvoice);
        documentService.saveDocument(cgInvoice);

        List<ContractsAndGrantsBillingAward> awards2 = setupAwards();
        Award award2 = (Award)awards2.get(0);
        award2.setInvoicingOptions(ArPropertyConstants.INV_AWARD);

        contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, errorOutputFile);

        String errorMessage = configurationService.getPropertyValueAsString(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NOT_ALL_BILLABLE_ACCOUNTS);
        errorMessage = MessageFormat.format(errorMessage, award2.getProposalNumber().toString());

        File errors = new File(errorOutputFile);
        assertTrue("errors should be written", errors.exists());
        assertTrue("errorOutputFile should not be empty", errors.length() > 0);
        assertTrue("error file should contain expected error", FileUtils.readFileToString(errors).contains(errorMessage));

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("one error should be persisted", persistedErrors.size() == 1);
        for (ContractsGrantsInvoiceDocumentErrorLog persistedError: persistedErrors) {
            assertTrue("process type should be batch", persistedError.getCreationProcessTypeCode().equals(ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.BATCH.getCode()));
            assertTrue("error message text should match", persistedError.getErrorMessages().get(0).getErrorMessageText().equals(errorMessage));
        }
    }

    private boolean messagesContainsExpectedError(List<ErrorMessage> errorMessages, String expectedErrorMessage) {
        ErrorMessage errorMessage = errorMessages.get(0);
        String errorMessageString = MessageFormat.format(configurationService.getPropertyValueAsString(errorMessage.getErrorKey()), (Object[])errorMessage.getMessageParameters());
        return StringUtils.equals(errorMessageString, expectedErrorMessage);
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

    protected void setupBills(ContractsGrantsInvoiceDocument document) {
        List<InvoiceBill> invoiceBills = new ArrayList<InvoiceBill>();
        InvoiceBill invBill_1 = InvoiceBillFixture.INV_BILL_2.createInvoiceBill();
        invBill_1.setDocumentNumber(document.getDocumentNumber());
        invBill_1.setProposalNumber(document.getProposalNumber());
        invBill_1.setBilledIndicator(false);

        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        java.sql.Date today = new java.sql.Date(ts.getTime());
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
        Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(document.getAward(), currPeriod);
        Date invoiceDate = pair[1];
        Date billDate = new Date(new DateTime(invoiceDate.getTime()).minusDays(1).toDate().getTime());

        invBill_1.setBillDate(billDate);

        businessObjectService.save(invBill_1);
        invoiceBills.add(invBill_1);
        document.setInvoiceBills(invoiceBills);

        Bill bill = new Bill();
        bill.setProposalNumber(invBill_1.getProposalNumber());
        bill.setBillNumber(invBill_1.getBillNumber());
        bill.setBillDescription(invBill_1.getBillDescription());
        bill.setBillIdentifier(invBill_1.getBillIdentifier());
        bill.setBillDate(invBill_1.getBillDate());
        bill.setEstimatedAmount(invBill_1.getEstimatedAmount());
        bill.setBilledIndicator(invBill_1.isBilledIndicator());
        bill.setAward(document.getAward());
        bill.setActive(true);

        PredeterminedBillingSchedule predeterminedBillingSchedule = new PredeterminedBillingSchedule();
        predeterminedBillingSchedule.setProposalNumber(document.getProposalNumber());
        List<Bill> bills = new ArrayList<Bill>();
        bills.add(bill);
        predeterminedBillingSchedule.setBills(bills);
        businessObjectService.save(predeterminedBillingSchedule);
        businessObjectService.save(bill);
    }

}
