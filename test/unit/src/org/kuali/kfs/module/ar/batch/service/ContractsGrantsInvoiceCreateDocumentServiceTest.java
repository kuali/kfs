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
import static org.kuali.kfs.sys.fixture.UserNameFixture.wklykins;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.fixture.ARAgencyFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardAccountFixture;
import org.kuali.kfs.module.ar.fixture.ARAwardFixture;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.fixture.InvoiceAccountDetailFixture;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateTestBase;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.util.ErrorMessage;

@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceCreateDocumentServiceTest extends ContractsGrantsInvoiceCreateTestBase {

    protected ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        contractsGrantsInvoiceCreateDocumentService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
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

    @ConfigureContext(session = wklykins)
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

    @ConfigureContext(session = wklykins)
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

    @ConfigureContext(session = wklykins)
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

    @ConfigureContext(session = wklykins)
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

    @ConfigureContext(session = wklykins)
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



}
