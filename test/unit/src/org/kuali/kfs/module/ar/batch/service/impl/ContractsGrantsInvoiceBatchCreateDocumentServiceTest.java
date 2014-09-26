/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceBatchCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateTestBase;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.util.ErrorMessage;

/**
 * Tests for the ContractsGrantsInvoiceBatchCreateDocumentService
 */
@ConfigureContext(session = kfs)
public class ContractsGrantsInvoiceBatchCreateDocumentServiceTest extends ContractsGrantsInvoiceCreateTestBase {
    protected ContractsGrantsInvoiceBatchCreateDocumentService contractsGrantsInvoiceBatchCreateDocumentService;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        contractsGrantsInvoiceBatchCreateDocumentService = SpringContext.getBean(ContractsGrantsInvoiceBatchCreateDocumentService.class);
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardsOneValid() {
        List<ContractsAndGrantsBillingAward> awards = setupAwards();

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

        File errors = new File(errorOutputFile);
        assertFalse("errors should not be written", errors.exists());

        Collection<ContractsGrantsInvoiceDocumentErrorLog> persistedErrors = businessObjectService.findAll(ContractsGrantsInvoiceDocumentErrorLog.class);
        assertTrue("no errors should be persisted", persistedErrors.size() == 0);
    }

    public void testBatchCreateCGInvoiceDocumentsByAwardsEmptyAwardsList() throws IOException {
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

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

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

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

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errorOutputFile);

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
        award2.setInvoicingOptions(ArConstants.INV_ACCOUNT);

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, errorOutputFile);

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
        award2.setInvoicingOptions(ArConstants.INV_CONTRACT_CONTROL_ACCOUNT);

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, errorOutputFile);

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
        award2.setInvoicingOptions(ArConstants.INV_AWARD);

        contractsGrantsInvoiceBatchCreateDocumentService.createCGInvoiceDocumentsByAwards(awards2, errorOutputFile);

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
}
