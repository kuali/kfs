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
package org.kuali.kfs.module.ar.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsOrganization;
import org.kuali.kfs.integration.cg.ContractsGrantsAwardInvoiceAccountInformation;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsCategory;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorMessage;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailTotal;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsBillingAwardVerificationService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


/**
 * This is the default implementation of the ContractsGrantsInvoiceDocumentCreateService interface.
 *
 * @see org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceDocumentCreateService
 */
@Transactional
public class ContractsGrantsInvoiceCreateDocumentServiceImpl implements ContractsGrantsInvoiceCreateDocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceCreateDocumentServiceImpl.class);

    protected AccountService accountService;
    protected AccountingPeriodService accountingPeriodService;
    protected AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    protected AwardAccountObjectCodeTotalBilledDao awardAccountObjectCodeTotalBilledDao;
    protected BusinessObjectService businessObjectService;
    protected ConfigurationService configurationService;
    protected ContractsGrantsBillingAwardVerificationService contractsGrantsBillingAwardVerificationService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected CustomerService customerService;
    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected KualiModuleService kualiModuleService;
    protected ParameterService parameterService;
    protected VerifyBillingFrequencyService verifyBillingFrequencyService;
    protected WorkflowDocumentService workflowDocumentService;
    protected UniversityDateService universityDateService;

    public static final String REPORT_LINE_DIVIDER = "--------------------------------------------------------------------------------------------------------------";

    @Override
    public List<ErrorMessage> createCGInvoiceDocumentsByAwards(Collection<ContractsAndGrantsBillingAward> awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType creationProcessTypeCode) {
        List<ErrorMessage> errorMessages = createInvoices(awards);

        if (!CollectionUtils.isEmpty(errorMessages)) {
            storeCreationErrors(errorMessages, creationProcessTypeCode.getCode());
        }

        return errorMessages;
    }

    /**
     * This method iterates through awards and create cgInvoice documents
     * @param awards used to create cgInvoice documents
     * @return List of error messages (if any)
     */
    protected List<ErrorMessage> createInvoices(Collection<ContractsAndGrantsBillingAward> awards) {
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        if (ObjectUtils.isNotNull(awards) && awards.size() > 0) {
            for (ContractsAndGrantsBillingAward awd : awards) {
                String invOpt = awd.getInvoicingOptions();
                final ContractsAndGrantsOrganization awardOrganization = awd.getPrimaryAwardOrganization();
                if (ObjectUtils.isNull(awardOrganization)) {
                    final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_ORGANIZATION_ON_AWARD, awd.getProposalNumber().toString());
                    errorMessages.add(errorMessage);
                } else {
                    if (invOpt.equals(ArPropertyConstants.INV_ACCOUNT)) { // case 1: create Contracts Grants Invoice by accounts
                        createInvoicesByAccounts(awd, errorMessages);
                    }
                    else if (invOpt.equals(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT)) { // case 2: create Contracts Grants Invoices by contractControlAccounts
                        createInvoicesByContractControlAccounts(awd, errorMessages);
                    }
                    // case 3: create Contracts Grants Invoice by award
                    else if (invOpt.equals(ArPropertyConstants.INV_AWARD)) {
                        createInvoicesByAward(awd, errorMessages);
                    }
                }
            }
        }  else {
            final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_AWARD);
            errorMessages.add(errorMessage);
        }
        return errorMessages;
    }

    /**
     * Generates and saves a single contracts and grants invoice document based on the given award
     * @param awd the award to generate a contracts and grants invoice document for
     * @param errLines a holder for error messages
     */
    protected void createInvoicesByAward(ContractsAndGrantsBillingAward awd, List<ErrorMessage> errorMessages) {
        // Check if awardaccounts has the same control account
        int accountNum = awd.getActiveAwardAccounts().size();
        Collection<Account> controlAccounts = getContractsGrantsInvoiceDocumentService().getContractControlAccounts(awd);
        if (controlAccounts == null || controlAccounts.size() < accountNum) {
            final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.BILL_BY_CONTRACT_VALID_ACCOUNTS, awd.getProposalNumber().toString());
            errorMessages.add(errorMessage);
        }
        else {
            // check if control accounts of awardaccounts are the same
            boolean isValid = true;
            if (accountNum != 1) {
                Set<Account> distinctAwardAccounts = new HashSet<>();
                for (ContractsAndGrantsBillingAwardAccount awardAccount : awd.getActiveAwardAccounts()) {
                    if (!ObjectUtils.isNull(awardAccount.getAccount().getContractControlAccount())) {
                        distinctAwardAccounts.add(awardAccount.getAccount().getContractControlAccount());
                    }
                }
                if (distinctAwardAccounts.size() > 1) {
                    final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.DIFFERING_CONTROL_ACCOUNTS, awd.getProposalNumber().toString());
                    errorMessages.add(errorMessage);
                    isValid = false;
                }
            }

            if (isValid) {
                String coaCode = null;
                String orgCode = null;
                for (ContractsAndGrantsBillingAwardAccount awardAccount : awd.getActiveAwardAccounts()) {
                    Account account = awardAccount.getAccount();
                    coaCode = awd.getPrimaryAwardOrganization().getChartOfAccountsCode();
                    orgCode = awd.getPrimaryAwardOrganization().getOrganizationCode();
                }
                // To get valid award accounts of amounts > zero$ and pass it to the create invoices method
                // To get valid award accounts of amounts > zero$ and pass it to the create invoices method
                if (!getValidAwardAccounts(awd.getActiveAwardAccounts(), awd).containsAll(awd.getActiveAwardAccounts())) {
                    final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NOT_ALL_BILLABLE_ACCOUNTS, awd.getProposalNumber().toString());
                    errorMessages.add(errorMessage);
                }
                generateAndSaveContractsAndGrantsInvoiceDocument(awd, getValidAwardAccounts(awd.getActiveAwardAccounts(), awd), coaCode, orgCode, errorMessages);
            }
        }
    }


    /**
     * Generates and saves contracts and grants invoice documents based on the given award's contract control accounts
     * @param awd the award with contract control accounts to build contracts and grants invoice documents from
     * @param errLines a holder for error messages
     */
    protected void createInvoicesByContractControlAccounts(ContractsAndGrantsBillingAward awd, List<ErrorMessage> errorMessages) {
        List<ContractsAndGrantsBillingAwardAccount> tmpAcctList = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        List<Account> controlAccounts = getContractsGrantsInvoiceDocumentService().getContractControlAccounts(awd);
        List<Account> controlAccountsTemp = getContractsGrantsInvoiceDocumentService().getContractControlAccounts(awd);

        if (controlAccounts == null || (controlAccounts.size() != awd.getActiveAwardAccounts().size())) {// to check if the number of contract control accounts is same as the number of accounts
            final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_CONTROL_ACCOUNT, awd.getProposalNumber().toString());
            errorMessages.add(errorMessage);
        }
        else {
            Set<Account> controlAccountSet = new HashSet<Account>();
            for (int i = 0; i < controlAccountsTemp.size(); i++) {
                if (ObjectUtils.isNotNull(controlAccountsTemp.get(i))) {
                    for (int j = i + 1; j < controlAccounts.size(); j++) {
                        if (controlAccountsTemp.get(i).equals(controlAccounts.get(j))) {
                            controlAccounts.set(j, null);
                        }
                    }
                }
                else {
                    break;
                }
            }
            for (Account ctrlAcct : controlAccounts) {
                if (ObjectUtils.isNotNull(ctrlAcct)) {
                    controlAccountSet.add(ctrlAcct);
                }

            }
            // control accounts are set correctly for award accounts

            if (controlAccountSet.size() != 0) {
                for (Account controlAccount : controlAccountSet) {
                    Account tmpCtrlAcct = null;

                    for (ContractsAndGrantsBillingAwardAccount awardAccount : awd.getActiveAwardAccounts()) {
                        if (!awardAccount.isFinalBilledIndicator()) {
                            tmpCtrlAcct = awardAccount.getAccount().getContractControlAccount();
                            if (tmpCtrlAcct.getChartOfAccountsCode().equals(controlAccount.getChartOfAccountsCode()) && tmpCtrlAcct.getAccountNumber().equals(controlAccount.getAccountNumber())) {
                                tmpAcctList.add(awardAccount);
                            }
                        }
                    }

                    // To get valid award accounts of amounts > zero$ and pass it to the create invoices method
                    if (!getValidAwardAccounts(tmpAcctList, awd).containsAll(tmpAcctList)) {
                        final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.CONTROL_ACCOUNT_NON_BILLABLE, controlAccount.getAccountNumber(), awd.getProposalNumber().toString());
                        errorMessages.add(errorMessage);
                    }
                    generateAndSaveContractsAndGrantsInvoiceDocument(awd, getValidAwardAccounts(tmpAcctList, awd), awd.getPrimaryAwardOrganization().getChartOfAccountsCode(), awd.getPrimaryAwardOrganization().getOrganizationCode(), errorMessages);
                }
            }
            else {
                final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.BILL_BY_CONTRACT_LACKS_CONTROL_ACCOUNT, awd.getProposalNumber().toString());
                errorMessages.add(errorMessage);
            }
        }
    }

    /**
     * Generates and saves contracts and grants invoice documens based on the award accounts of the passed in award
     * @param awd the award to build contracts and grants invoice documents from the award accounts on
     * @param errLines a holder for error messages
     */
    protected void createInvoicesByAccounts(ContractsAndGrantsBillingAward awd, List<ErrorMessage> errorMessages) {
        List<ContractsAndGrantsBillingAwardAccount> tmpAcctList = new ArrayList<ContractsAndGrantsBillingAwardAccount>();

        for (ContractsAndGrantsBillingAwardAccount awardAccount : awd.getActiveAwardAccounts()) {
            if (!awardAccount.isFinalBilledIndicator()) {
                // only one account is added into the list to create cgin
                tmpAcctList.clear();
                tmpAcctList.add(awardAccount);

                // To get valid award accounts of amounts > zero$ and pass it to the create invoices method
                if (!getValidAwardAccounts(tmpAcctList, awd).containsAll(tmpAcctList)) {
                    final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NON_BILLABLE, awardAccount.getAccountNumber(), awd.getProposalNumber().toString());
                    errorMessages.add(errorMessage);
                }

                generateAndSaveContractsAndGrantsInvoiceDocument(awd, getValidAwardAccounts(tmpAcctList, awd), awd.getPrimaryAwardOrganization().getChartOfAccountsCode(), awd.getPrimaryAwardOrganization().getOrganizationCode(), errorMessages);
            }
        }
    }

    /**
     * Generates and then saves a contracts and grants invoice document
     * @param awd the award for the document
     * @param validAwardAccounts the award accounts which should appear on the document
     * @param coaCode the chart code for the document
     * @param orgCode the organization code for the document
     * @param errLines a List of error messages, to be appended to if there are errors in document generation
     */
    protected void generateAndSaveContractsAndGrantsInvoiceDocument(ContractsAndGrantsBillingAward awd, List<ContractsAndGrantsBillingAwardAccount> validAwardAccounts, final String coaCode, final String orgCode, List<ErrorMessage> errorMessages) {
        ContractsGrantsInvoiceDocument cgInvoiceDocument = createCGInvoiceDocumentByAwardInfo(awd, validAwardAccounts, coaCode, orgCode, errorMessages);
        if (ObjectUtils.isNotNull(cgInvoiceDocument)) {
            // Saving the document
            try {
                documentService.saveDocument(cgInvoiceDocument, DocumentSystemSaveEvent.class);
            }
            catch (WorkflowException ex) {
                LOG.error("Error creating cgin documents: " + ex.getMessage(), ex);
                throw new RuntimeException("Error creating cgin documents: " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * This method retrieves create a ContractsGrantsInvoiceDocument by Award * @param awd
     *
     * @return ContractsGrantsInvoiceDocument
     */
    @Override
    public ContractsGrantsInvoiceDocument createCGInvoiceDocumentByAwardInfo(ContractsAndGrantsBillingAward awd, List<ContractsAndGrantsBillingAwardAccount> accounts, String chartOfAccountsCode, String organizationCode, List<ErrorMessage> errorMessages) {
        ContractsGrantsInvoiceDocument cgInvoiceDocument = null;
        if (ObjectUtils.isNotNull(accounts) && !accounts.isEmpty()) {
            if (chartOfAccountsCode != null && organizationCode != null) {
                try {
                    cgInvoiceDocument = (ContractsGrantsInvoiceDocument) documentService.getNewDocument(ContractsGrantsInvoiceDocument.class);
                    // Set description to the document created.
                    cgInvoiceDocument.getDocumentHeader().setDocumentDescription(ArConstants.BatchFileSystem.CGINVOICE_DOCUMENT_DESCRIPTION_OF_BATCH_PROCESS);
                    // setup several Default Values for CGInvoice document which extends from Customer Invoice Document

                    // a) set billing org and chart code
                    cgInvoiceDocument.setBillByChartOfAccountCode(chartOfAccountsCode);
                    cgInvoiceDocument.setBilledByOrganizationCode(organizationCode);

                    // b) set processing org and chart code
                    List<String> procCodes = getContractsGrantsInvoiceDocumentService().getProcessingFromBillingCodes(chartOfAccountsCode, organizationCode);

                    AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
                    accountsReceivableDocumentHeader.setDocumentNumber(cgInvoiceDocument.getDocumentNumber());

                    // Set processing chart and org codes
                    if (procCodes != null){
                        int procCodesSize = procCodes.size();

                        // Set processing chart
                        if (procCodesSize > 0){
                            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(procCodes.get(0));
                        }

                        // Set processing org code
                        if (procCodesSize > 1){
                            accountsReceivableDocumentHeader.setProcessingOrganizationCode(procCodes.get(1));
                        }
                    }

                    cgInvoiceDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

                    cgInvoiceDocument.setAward(awd);
                    populateInvoiceFromAward(awd, accounts,cgInvoiceDocument);
                    contractsGrantsInvoiceDocumentService.createSourceAccountingLines(cgInvoiceDocument);
                    if (ObjectUtils.isNotNull(cgInvoiceDocument.getAward())) {
                        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(cgInvoiceDocument);
                    }

                    LOG.info("Created Contracts and Grants invoice document " + cgInvoiceDocument.getDocumentNumber());
                }
                catch (WorkflowException ex) {
                    LOG.error("Error creating cgin documents: " + ex.getMessage(), ex);
                    throw new RuntimeException("Error creating cgin documents: " + ex.getMessage(), ex);
                }
            }
            else {
                // if chart of account code or organization code is not available, output the error
                final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_CHART_OR_ORG, awd.getProposalNumber().toString());
                errorMessages.add(errorMessage);
            }
        }

        return cgInvoiceDocument;
    }

    /**
     * This method takes all the applicable attributes from the associated award object and sets those attributes into their
     * corresponding invoice attributes.
     *
     * @param award The associated award that the invoice will be linked to.
     * @param awardAccounts
     * @param document
     */
    protected void populateInvoiceFromAward(ContractsAndGrantsBillingAward award, List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsGrantsInvoiceDocument document) {
        List<Milestone> milestones = new ArrayList<Milestone>();
        List<Bill> bills = new ArrayList<Bill>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        map.put(KFSPropertyConstants.ACTIVE, true);
        milestones = (List<Milestone>) businessObjectService.findMatching(Milestone.class, map);
        bills = (List<Bill>) businessObjectService.findMatching(Bill.class, map);

        if (ObjectUtils.isNotNull(award)) {

            // Invoice General Detail section
            document.setProposalNumber(award.getProposalNumber());
            InvoiceGeneralDetail invoiceGeneralDetail = new InvoiceGeneralDetail();
            invoiceGeneralDetail.setDocumentNumber(document.getDocumentNumber());

            // Set the last Billed Date and Billing Period
            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            java.sql.Date today = new java.sql.Date(ts.getTime());
            AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
            java.sql.Date[] pair = verifyBillingFrequencyService.getStartDateAndEndDateOfPreviousBillingPeriod(award, currPeriod);
            invoiceGeneralDetail.setBillingPeriod(pair[0] + " to " + pair[1]);
            invoiceGeneralDetail.setLastBilledDate(pair[1]);


            populateInvoiceDetailFromAward(invoiceGeneralDetail, award);
            document.setInvoiceGeneralDetail(invoiceGeneralDetail);
            // To set Bill by address identifier because it is a required field - set the value to 1 as it is never being used.
            document.setCustomerBillToAddressIdentifier(1);

            // Set Invoice due date to current date as it is required field and never used.
            document.setInvoiceDueDate(dateTimeService.getCurrentSqlDateMidnight());

            // copy award's customer address to invoice address details
            document.getInvoiceAddressDetails().clear();

            ContractsAndGrantsBillingAgency agency = award.getAgency();
            if (ObjectUtils.isNotNull(agency)) {
                final List<InvoiceAddressDetail> invoiceAddressDetails = buildInvoiceAddressDetailsFromAgency(agency, document.getDocumentNumber());
                document.getInvoiceAddressDetails().addAll(invoiceAddressDetails);
            }

            java.sql.Date invoiceDate = document.getInvoiceGeneralDetail().getLastBilledDate();
            if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !CollectionUtils.isEmpty(milestones)) {// To check if award has milestones
                // copy award milestones to invoice milestones
                document.getInvoiceMilestones().clear();
                final List<InvoiceMilestone> invoiceMilestones = buildInvoiceMilestones(milestones, invoiceDate);
                document.getInvoiceMilestones().addAll(invoiceMilestones);
            }
            else if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) && !CollectionUtils.isEmpty(bills)) {// To check if award has bills
                // copy award milestones to invoice milestones
                document.getInvoiceBills().clear();
                final List<InvoiceBill> invoiceBills = buildInvoiceBills(bills, invoiceDate);
                document.getInvoiceBills().addAll(invoiceBills);
            }
            else {

                // To set values for categories and populate invoice details section
                generateValuesForAccountObjectCodes(awardAccounts, award, document);
                generateValuesForCategories(awardAccounts, document);
            }

            // copy award's accounts to invoice account details
            document.getAccountDetails().clear();
            final List<InvoiceAccountDetail> invoiceAccountDetails = buildInvoiceAcccountDetails(award, awardAccounts, document.getDocumentNumber(), document.getInvoiceGeneralDetail());
            document.getAccountDetails().addAll(invoiceAccountDetails);
            // Set some basic values to invoice Document
            populateContractsGrantsInvoiceDocument(award, document);

        }
    }

    /**
     * Builds a List of InvoiceAccountDetails for a given Award to place on a generated Contracts & Grants Invoice document
     * @param award the award associated with the being-built CINV document
     * @param awardAccounts the valid-to-bill accounts on that award
     * @param documentNumber the document number of the CINV document in process of construction
     * @param invoiceGeneralDetail the general detail on the CINV document
     * @return a List of generated account details
     */
    protected List<InvoiceAccountDetail> buildInvoiceAcccountDetails(ContractsAndGrantsBillingAward award, List<ContractsAndGrantsBillingAwardAccount> awardAccounts, final String documentNumber, InvoiceGeneralDetail invoiceGeneralDetail) {
        List<InvoiceAccountDetail> invoiceAccountDetails = new ArrayList<>();
        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {

            InvoiceAccountDetail invoiceAccountDetail = new InvoiceAccountDetail();
            invoiceAccountDetail.setDocumentNumber(documentNumber);

            invoiceAccountDetail.setAccountNumber(awardAccount.getAccountNumber());
            if (ObjectUtils.isNotNull(awardAccount.getAccount()) && StringUtils.isNotEmpty(awardAccount.getAccount().getContractControlAccountNumber())) {
                invoiceAccountDetail.setContractControlAccountNumber(awardAccount.getAccount().getContractControlAccountNumber());
            }
            invoiceAccountDetail.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
            invoiceAccountDetail.setProposalNumber(award.getProposalNumber());
            updateBudgetsAndCumulativesForInvoiceAccountDetail(invoiceAccountDetail, invoiceGeneralDetail.getLastBilledDate(), invoiceGeneralDetail.getBillingFrequency(), award.getAwardBeginningDate());
            invoiceAccountDetails.add(invoiceAccountDetail);
        }
        return invoiceAccountDetails;
    }

    /**
     * Generates InvoiceBills for each of the given Bills
     * @param bills the bulls to associate with a contracts & grants billing invoice
     * @param invoiceDate the date of the invoice we're building
     * @return the List of generated InvoiceBill objects
     */
    protected List<InvoiceBill> buildInvoiceBills(List<Bill> bills, java.sql.Date invoiceDate) {
        List<InvoiceBill> invoiceBills = new ArrayList<>();
        for (Bill awdBill : bills) {
            // To check for null - Bill Completion date.
            // To consider the completed milestones only.
            if (awdBill.getBillDate() != null && !invoiceDate.before(awdBill.getBillDate()) && !awdBill.isBilled() && awdBill.getEstimatedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                InvoiceBill invBill = new InvoiceBill();
                invBill.setProposalNumber(awdBill.getProposalNumber());
                invBill.setBillNumber(awdBill.getBillNumber());
                invBill.setBillIdentifier(awdBill.getBillIdentifier());
                invBill.setBillDescription(awdBill.getBillDescription());
                invBill.setBilled(awdBill.isBilled());
                invBill.setBillDate(awdBill.getBillDate());
                invBill.setEstimatedAmount(awdBill.getEstimatedAmount());
                invoiceBills.add(invBill);
            }
        }
        return invoiceBills;
    }

    /**
     * Generates InvoiceMilestones for each of the given milestones
     * @param milestones the milestones to associate with a contracts & grants billing invoice
     * @param invoiceDate the date of the invoice we're building
     * @return the List of InvoiceMilestones
     */
    protected List<InvoiceMilestone> buildInvoiceMilestones(List<Milestone> milestones, java.sql.Date invoiceDate) {
        List<InvoiceMilestone> invoiceMilestones = new ArrayList<>();
        for (Milestone awdMilestone : milestones) {
            // To consider the completed milestones only.
            // To check for null - Milestone Completion date.

            if (awdMilestone.getMilestoneActualCompletionDate() != null && !invoiceDate.before(awdMilestone.getMilestoneActualCompletionDate()) && !awdMilestone.isBilled() && awdMilestone.getMilestoneAmount().isGreaterThan(KualiDecimal.ZERO)) {

                InvoiceMilestone invMilestone = new InvoiceMilestone();
                invMilestone.setProposalNumber(awdMilestone.getProposalNumber());
                invMilestone.setMilestoneNumber(awdMilestone.getMilestoneNumber());
                invMilestone.setMilestoneIdentifier(awdMilestone.getMilestoneIdentifier());
                invMilestone.setMilestoneDescription(awdMilestone.getMilestoneDescription());
                invMilestone.setBilled(awdMilestone.isBilled());
                invMilestone.setMilestoneActualCompletionDate(awdMilestone.getMilestoneActualCompletionDate());
                invMilestone.setMilestoneAmount(awdMilestone.getMilestoneAmount());
                invoiceMilestones.add(invMilestone);
            }
        }
        return invoiceMilestones;
    }

    /**
     * Builds a list of InvoiceAddressDetails based on the customer associated with an Agency
     * @param agency the agency associated with the proposal we're building a CINV document for
     * @param documentNumber the document number of the CINV document we're creating
     * @return a List of the generated invoice address details
     */
    protected List<InvoiceAddressDetail> buildInvoiceAddressDetailsFromAgency(ContractsAndGrantsBillingAgency agency, String documentNumber) {
        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.CUSTOMER_NUMBER, agency.getCustomerNumber());
        final List<CustomerAddress> customerAddresses = (List<CustomerAddress>) businessObjectService.findMatching(CustomerAddress.class, mapKey);

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        for (CustomerAddress customerAddress : customerAddresses) {
            InvoiceAddressDetail invoiceAddressDetail = new InvoiceAddressDetail();
            invoiceAddressDetail.setCustomerNumber(customerAddress.getCustomerNumber());
            invoiceAddressDetail.setDocumentNumber(documentNumber);
            invoiceAddressDetail.setCustomerAddressIdentifier(customerAddress.getCustomerAddressIdentifier());
            invoiceAddressDetail.setCustomerAddressTypeCode(customerAddress.getCustomerAddressTypeCode());
            invoiceAddressDetail.setCustomerAddressName(customerAddress.getCustomerAddressName());
            invoiceAddressDetail.setInvoiceTransmissionMethodCode(customerAddress.getInvoiceTransmissionMethodCode());
            invoiceAddressDetail.setCustomerEmailAddress(customerAddress.getCustomerEmailAddress());
            if (StringUtils.isNotBlank(customerAddress.getCustomerInvoiceTemplateCode())) {
                invoiceAddressDetail.setCustomerInvoiceTemplateCode(customerAddress.getCustomerInvoiceTemplateCode());
            } else {
                AccountsReceivableCustomer customer = agency.getCustomer();
                if (ObjectUtils.isNotNull(customer) && StringUtils.isNotBlank(customer.getCustomerInvoiceTemplateCode())) {
                    invoiceAddressDetail.setCustomerInvoiceTemplateCode(customer.getCustomerInvoiceTemplateCode());
                }
            }
            invoiceAddressDetails.add(invoiceAddressDetail);
        }
        return invoiceAddressDetails;
    }

    /**
     * 1. This method is responsible to populate categories column for the ContractsGrantsInvoice Document. 2. The categories are
     * retrieved from the Maintenance document as a collection and then a logic with conditions to handle ranges of Object Codes. 3.
     * Once the object codes are retrieved and categories are set the performAccountingCalculations method of InvoiceDetail BO will
     * do all the accounting calculations.
     */
    public void generateValuesForCategories(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsGrantsInvoiceDocument document) {

        Set<String> categoryArray = new HashSet<String>();

        // To get only the active categories.
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        Collection<ContractsAndGrantsCategory> contractsAndGrantsCategories = businessObjectService.findMatching(ContractsAndGrantsCategory.class, criteria);

        // query database for award account object code details. then divi them up into categories
        List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilleds = getAwardAccountObjectCodeTotalBilledDao().getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);

        for (ContractsAndGrantsCategory category : contractsAndGrantsCategories) {
            // To add all the values from Category Array to Invoice Details category only if they are retrieved well.

            ContractsGrantsInvoiceDetail invDetail = new ContractsGrantsInvoiceDetail();
            invDetail.setDocumentNumber(document.getDocumentHeader().getDocumentNumber());
            invDetail.setCategoryCode(category.getCategoryCode());
            invDetail.setCategoryName(category.getCategoryName());
            invDetail.setIndirectCostIndicator(category.isIndirectCostIndicator());
            // calculate total billed first
            Set<String> completeObjectCodeArrayForSingleCategory = contractsGrantsInvoiceDocumentService.getObjectCodeArrayFromSingleCategory(category, document);
            performBilledAndExpenditureCalculationForDetail(invDetail, awardAccountObjectCodeTotalBilleds, document.getInvoiceDetailAccountObjectCodes(), completeObjectCodeArrayForSingleCategory);

            // calculate the rest using billed to date
            performBudgetCalculationsOnInvoiceDetail(invDetail, awardAccounts, completeObjectCodeArrayForSingleCategory, document.getAward().getAwardBeginningDate());// accounting
                                                                                                                                                      // calculations
            // happening here
            document.getInvoiceDetails().add(invDetail);
        }

        // To calculate total values for Invoice Detail section.
        InvoiceDetailTotal directCostTotal = new InvoiceDetailTotal();
        InvoiceDetailTotal indirectCostTotal = new InvoiceDetailTotal();

        for (ContractsGrantsInvoiceDetail invD : document.getInvoiceDetailsWithIndirectCosts()) {
            // To sum up values for indirect Cost Invoice Details
            if (invD.isIndirectCostIndicator()) {
                indirectCostTotal.sumInvoiceDetail(invD);
            }
            else {
                directCostTotal.sumInvoiceDetail(invD);
            }
        }
        ContractsGrantsInvoiceDetail directCostInvDetail = new ContractsGrantsInvoiceDetail();
        directCostInvDetail.setDocumentNumber(document.getDocumentHeader().getDocumentNumber());

        directCostInvDetail.setCategoryCode(ArConstants.TOTAL_DIRECT_COST_CD);
        directCostInvDetail.setCategoryName(ArConstants.TOTAL_DIRECT_COST);
        directCostInvDetail.setBudget(directCostTotal.getBudget());
        directCostInvDetail.setExpenditures(directCostTotal.getExpenditures());
        directCostInvDetail.setCumulative(directCostTotal.getCumulative());
        directCostInvDetail.setBilled(directCostTotal.getBilled());
        document.getInvoiceDetails().add(directCostInvDetail);

        // To create a Total In Direct Cost invoice detail to add values for indirect cost invoice details.

        ContractsGrantsInvoiceDetail indInvDetail = new ContractsGrantsInvoiceDetail();
        indInvDetail.setDocumentNumber(document.getDocumentHeader().getDocumentNumber());
        indInvDetail.setIndirectCostIndicator(true);
        indInvDetail.setCategoryCode(ArConstants.TOTAL_IN_DIRECT_COST_CD);
        indInvDetail.setCategoryName(ArConstants.TOTAL_IN_DIRECT_COST);
        indInvDetail.setBudget(indirectCostTotal.getBudget());
        indInvDetail.setExpenditures(indirectCostTotal.getExpenditures());
        indInvDetail.setCumulative(indirectCostTotal.getCumulative());
        indInvDetail.setBilled(indirectCostTotal.getBilled());
        document.getInvoiceDetails().add(indInvDetail);

        // Sum up the direct cost and indirect cost invoice details.

        ContractsGrantsInvoiceDetail totalInvDetail = new ContractsGrantsInvoiceDetail();
        totalInvDetail.setDocumentNumber(document.getDocumentHeader().getDocumentNumber());
        totalInvDetail.setCategoryCode(ArConstants.TOTAL_COST_CD);
        totalInvDetail.setCategoryName(ArConstants.TOTAL_COST);

        ContractsGrantsInvoiceDetail totalDirectCostInvoiceDetail = document.getTotalDirectCostInvoiceDetail();
        if (ObjectUtils.isNotNull(totalDirectCostInvoiceDetail)) {
            totalInvDetail.setBudget(directCostTotal.getBudget().add(indirectCostTotal.getBudget()));
            totalInvDetail.setExpenditures(directCostTotal.getExpenditures().add(indirectCostTotal.getExpenditures()));
            totalInvDetail.setCumulative(directCostTotal.getCumulative().add(indirectCostTotal.getCumulative()));
            totalInvDetail.setBilled(directCostTotal.getBilled().add(indirectCostTotal.getBilled()));
        }

        document.getInvoiceDetails().add(totalInvDetail);
    }

    /**
     * This method takes all the applicable attributes from the associated award object and sets those attributes into their
     * corresponding invoice attributes.
     * @param invoiceGeneralDetail the invoice detail to populate
     * @param award The associated award that the invoice will be linked to.
     */
    protected void populateInvoiceDetailFromAward(InvoiceGeneralDetail invoiceGeneralDetail, ContractsAndGrantsBillingAward award) {
        // copy General details from award to the invoice
        invoiceGeneralDetail.setAwardTotal(award.getAwardTotalAmount());
        invoiceGeneralDetail.setAgencyNumber(award.getAgencyNumber());
        if (ObjectUtils.isNotNull(award.getPreferredBillingFrequency())) {
            invoiceGeneralDetail.setBillingFrequency(award.getPreferredBillingFrequency());
        }
        if (ObjectUtils.isNotNull(award.getInstrumentTypeCode())) {
            invoiceGeneralDetail.setInstrumentTypeCode(award.getInstrumentTypeCode());
        }
        // To set Award Date range - this would be (Award Start Date to Award Stop Date)
        String awdDtRange = award.getAwardBeginningDate() + " to " + award.getAwardEndingDate();
        invoiceGeneralDetail.setAwardDateRange(awdDtRange);

        // set the billed to Date Field
        // To check if award has milestones
        if (StringUtils.equalsIgnoreCase(invoiceGeneralDetail.getBillingFrequency(), ArConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
            invoiceGeneralDetail.setBilledToDateAmount(contractsGrantsInvoiceDocumentService.getMilestonesBilledToDateAmount(award.getProposalNumber()));
        }
        else if (StringUtils.equalsIgnoreCase(invoiceGeneralDetail.getBillingFrequency(),ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            invoiceGeneralDetail.setBilledToDateAmount(contractsGrantsInvoiceDocumentService.getPredeterminedBillingBilledToDateAmount(award.getProposalNumber()));
        }
        else {
            invoiceGeneralDetail.setBilledToDateAmount(contractsGrantsInvoiceDocumentService.getAwardBilledToDateAmountByProposalNumber(award.getProposalNumber()));
        }
    }

    /**
     * @param awardAccounts
     * @param award
     */
    protected void generateValuesForAccountObjectCodes(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsAndGrantsBillingAward award, ContractsGrantsInvoiceDocument document) {
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();
        Integer currentYear = universityDateService.getCurrentFiscalYear();
        Map<String, Set<String>> objectCodeFromCategoriesMap = new HashMap<String, Set<String>>();

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        Collection<ContractsAndGrantsCategory> contractsAndGrantsCategories = businessObjectService.findMatching(ContractsAndGrantsCategory.class, criteria);
        // get the categories and create a new arraylist for each one
        for (ContractsAndGrantsCategory category : contractsAndGrantsCategories) {
            // populate the category object code maps
            objectCodeFromCategoriesMap.put(category.getCategoryCode(), contractsGrantsInvoiceDocumentService.getObjectCodeArrayFromSingleCategory(category, document));

        }
        // Changes made to retrieve balances of previous years (useful when the award is billed for the first time and in case
        // of fiscal year change)
        final Integer awardBeginningYear = universityDateService.getFiscalYear(award.getAwardBeginningDate());
        final Set<String> awardObjectCodes = contractsGrantsInvoiceDocumentService.getObjectCodeArrayFromContractsAndGrantsCategories(document);

        final List<Balance> glBalances = retrieveBalancesForAwardAccounts(awardAccounts, awardBeginningYear, currentYear, awardObjectCodes);

        for (Balance bal : glBalances) {
            if (ObjectUtils.isNull(bal.getSubAccount()) || ObjectUtils.isNull(bal.getSubAccount().getA21SubAccount()) || !StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {

                for (ContractsAndGrantsCategory category : contractsAndGrantsCategories) {
                    Set<String> objectCodeFromCategoriesSet = objectCodeFromCategoriesMap.get(category.getCategoryCode());

                    // if the object code from this balance is in the list of object code retrieved from the category, then include in the detail
                    if (objectCodeFromCategoriesSet.contains(bal.getObjectCode())) {
                        final InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = getInvoiceDetailAccountObjectCodeByBalanceAndCategory(bal, category, document.getDocumentNumber(), document.getProposalNumber());

                        if (!document.getInvoiceDetailAccountObjectCodes().contains(invoiceDetailAccountObjectCode)) {
                            document.getInvoiceDetailAccountObjectCodes().add(invoiceDetailAccountObjectCode);
                        }

                        // Retrieve cumulative amounts based on the biling period.

                        if (document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.MONTHLY_BILLING_SCHEDULE_CODE) || document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.QUATERLY_BILLING_SCHEDULE_CODE) || document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE) || document.getInvoiceGeneralDetail().getBillingFrequency().equalsIgnoreCase(ArConstants.ANNUALLY_BILLING_SCHEDULE_CODE)) {
                            invoiceDetailAccountObjectCode.setCumulativeExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().add(calculateBalanceAmountWithoutLastBilledPeriod(document.getInvoiceGeneralDetail().getLastBilledDate(), bal)));
                        }
                        else {// This code should be removed. This is temporary - just to make sure the amounts are pulled up.
                            invoiceDetailAccountObjectCode.setCumulativeExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().add(bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount())));
                        }

                        // add this single account object code item to the list in the Map
                        businessObjectService.save(invoiceDetailAccountObjectCode);


                        break; // found a match into which category, we can stop and move on to next balance entry
                    }
                }
            }
        }

        // Modifying the code to set invoiceDetailaccountobject codes calculation here checking with loc review indicator and also
        // accounting more than one fiscal years.

        for (ContractsAndGrantsBillingAwardAccount awdAcct : awardAccounts) {
            if (awdAcct.isLetterOfCreditReviewIndicator() && award.getPreferredBillingFrequency().equalsIgnoreCase(ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
                KualiDecimal amountToDrawForObjectCodes = KualiDecimal.ZERO;

                List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodeList = new ArrayList<InvoiceDetailAccountObjectCode>();
                for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : document.getInvoiceDetailAccountObjectCodes()) {
                    if (invoiceDetailAccountObjectCode.getDocumentNumber().equals(document.getDocumentNumber()) && invoiceDetailAccountObjectCode.getProposalNumber().equals(document.getProposalNumber()) && invoiceDetailAccountObjectCode.getAccountNumber().equals(awdAcct.getAccountNumber()) && invoiceDetailAccountObjectCode.getChartOfAccountsCode().equals(awdAcct.getChartOfAccountsCode())) {
                        invoiceDetailAccountObjectCodeList.add(invoiceDetailAccountObjectCode);
                    }
                }
                amountToDrawForObjectCodes = awdAcct.getAmountToDraw().divide(new KualiDecimal(invoiceDetailAccountObjectCodeList.size()));

                // Now to set the divided value equally to all the object code rows.
                for (InvoiceDetailAccountObjectCode invDtllAcctOB : invoiceDetailAccountObjectCodeList) {
                    invDtllAcctOB.setCurrentExpenditures(amountToDrawForObjectCodes);
                }
            }
            else {
                // code to write values from award acct total billed amount to invoice detail account object code..

                Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
                totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, document.getProposalNumber());
                totalBilledKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awdAcct.getChartOfAccountsCode());
                totalBilledKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awdAcct.getAccountNumber());

                List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilledList = (List<AwardAccountObjectCodeTotalBilled>) businessObjectService.findMatching(AwardAccountObjectCodeTotalBilled.class, totalBilledKeys);

                for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : document.getInvoiceDetailAccountObjectCodes()) {
                    if (!CollectionUtils.isEmpty(awardAccountObjectCodeTotalBilledList)) {
                        for (AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled : awardAccountObjectCodeTotalBilledList) {
                            if (invoiceDetailAccountObjectCode.getFinancialObjectCode().equalsIgnoreCase(awardAccountObjectCodeTotalBilled.getFinancialObjectCode())) {
                                invoiceDetailAccountObjectCode.setTotalBilled(awardAccountObjectCodeTotalBilled.getTotalBilled());
                            }
                        }
                    }
                    // Set current expenditures
                    invoiceDetailAccountObjectCode.setCurrentExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().subtract(invoiceDetailAccountObjectCode.getTotalBilled()));

                }
            }

        }
    }

    /**
     * Retrieves or constructs an InvoiceDetailAccountObjectCode based on a given balance and billing category
     * @param bal the balance to get the account object code from
     * @param category the billing category the account object code should come from
     * @param documentNumber the number of the CINV document we are currently building
     * @param proposalNumber the proposal number associated with the award on the CINV document we're currently building
     * @return the retrieved or constructed (if nothing was found in the database) InvoiceDetailAccountObjectCode object
     */
    protected InvoiceDetailAccountObjectCode getInvoiceDetailAccountObjectCodeByBalanceAndCategory(Balance bal, ContractsAndGrantsCategory category, final String documentNumber, final Long proposalNumber) {
        // Check if there is an existing invoice detail account object code existing (if there are more than one fiscal years)
        Map<String, Object> invDtlKeys = new HashMap<String, Object>();
        invDtlKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        invDtlKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, bal.getChartOfAccountsCode());
        invDtlKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, bal.getAccountNumber());
        invDtlKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, bal.getObjectCode());
        invDtlKeys.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = businessObjectService.findByPrimaryKey(InvoiceDetailAccountObjectCode.class, invDtlKeys);

        if (ObjectUtils.isNull(invoiceDetailAccountObjectCode)) {
            invoiceDetailAccountObjectCode = new InvoiceDetailAccountObjectCode();
            invoiceDetailAccountObjectCode.setDocumentNumber(documentNumber);
            invoiceDetailAccountObjectCode.setProposalNumber(proposalNumber);
            invoiceDetailAccountObjectCode.setFinancialObjectCode(bal.getObjectCode());
            invoiceDetailAccountObjectCode.setCategoryCode(category.getCategoryCode());
            invoiceDetailAccountObjectCode.setAccountNumber(bal.getAccountNumber());
            invoiceDetailAccountObjectCode.setChartOfAccountsCode(bal.getChartOfAccountsCode());
        }
        return invoiceDetailAccountObjectCode;
    }

    /**
     * Retrieves all balances between the beginning of the award and the current fiscal year for the given award accounts
     * @param awardAccounts the award accounts to look up balances for
     * @param awardBeginningYear the first year of the award
     * @param currentYear the current year
     * @param awardObjectCodes the object codes which the award accounts need to retrieve associated balances for
     * @return the retrieved balances
     */
    protected List<Balance> retrieveBalancesForAwardAccounts(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, final Integer awardBeginningYear, Integer currentYear, final Set<String> awardObjectCodes) {
        List<Balance> glBalances = new ArrayList<Balance>();
        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
            for (int fy = awardBeginningYear; fy <= currentYear; fy++) {
                Map<String, Object> balanceKeys = new HashMap<String, Object>();
                balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
                balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
                balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fy);
                balanceKeys.put(KFSPropertyConstants.OBJECT_TYPE_CODE, ArPropertyConstants.EXPENSE_OBJECT_TYPE);
                balanceKeys.put(KFSPropertyConstants.BALANCE_TYPE_CODE, ArPropertyConstants.ACTUAL_BALANCE_TYPE);
                balanceKeys.put(KFSPropertyConstants.OBJECT_CODE, awardObjectCodes);
                glBalances.addAll(businessObjectService.findMatching(Balance.class, balanceKeys));
            }
        }
        return glBalances;
    }

    /**
     * This method will set Budgets and Cumulative Expenditure amounts for each invoice account detail.
     * @param invoiceAccountDetail the invoice account detail to update
     * @param lastBilledDate the last billed date on the award
     * @param billingFrequency the billing frequency on the award
     * @param awardBeginningDate the beginning date of the award
     */
    protected void updateBudgetsAndCumulativesForInvoiceAccountDetail(InvoiceAccountDetail invoiceAccountDetail, java.sql.Date lastBilledDate, String billingFrequency, java.sql.Date awardBeginningDate) {
        List<Balance> glBalances = new ArrayList<Balance>();
        Integer currentYear = getUniversityDateService().getCurrentFiscalYear();
        List<Integer> fiscalYears = new ArrayList<Integer>();
        Calendar c = Calendar.getInstance();

        Integer awardBeginningFiscalYear = getUniversityDateService().getFiscalYear(awardBeginningDate);

        for (Integer i = awardBeginningFiscalYear; i <= currentYear; i++) {
            fiscalYears.add(i);
        }
        List<String> balanceTypeCodeList = new ArrayList<String>();
        balanceTypeCodeList.add(ArPropertyConstants.BUDGET_BALANCE_TYPE);
        balanceTypeCodeList.add(ArPropertyConstants.ACTUAL_BALANCE_TYPE);
        for (Integer eachFiscalYr : fiscalYears) {
            Map<String, Object> balanceKeys = new HashMap<String, Object>();
            balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, invoiceAccountDetail.getChartOfAccountsCode());
            balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, invoiceAccountDetail.getAccountNumber());
            balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
            balanceKeys.put(KFSPropertyConstants.OBJECT_TYPE_CODE, ArPropertyConstants.EXPENSE_OBJECT_TYPE);
            balanceKeys.put(KFSPropertyConstants.BALANCE_TYPE_CODE,balanceTypeCodeList);
            glBalances.addAll(getBusinessObjectService().findMatching(Balance.class, balanceKeys));
        }
        KualiDecimal budAmt = KualiDecimal.ZERO;
        KualiDecimal balAmt = KualiDecimal.ZERO;
        KualiDecimal cumAmt = KualiDecimal.ZERO;

        for (Balance bal : glBalances) {
            if (ObjectUtils.isNull(bal.getSubAccount()) || ObjectUtils.isNull(bal.getSubAccount().getA21SubAccount()) || !StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
                if (bal.getBalanceTypeCode().equalsIgnoreCase(ArPropertyConstants.BUDGET_BALANCE_TYPE)) {
                    balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                    budAmt = budAmt.add(balAmt);
                }
                else if (bal.getBalanceTypeCode().equalsIgnoreCase(ArPropertyConstants.ACTUAL_BALANCE_TYPE)) {
                    if (billingFrequency.equalsIgnoreCase(ArConstants.MONTHLY_BILLING_SCHEDULE_CODE) || billingFrequency.equalsIgnoreCase(ArConstants.QUATERLY_BILLING_SCHEDULE_CODE) || billingFrequency.equalsIgnoreCase(ArConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE) || billingFrequency.equalsIgnoreCase(ArConstants.ANNUALLY_BILLING_SCHEDULE_CODE)) {

                        cumAmt = cumAmt.add(calculateBalanceAmountWithoutLastBilledPeriod(lastBilledDate, bal));
                    }
                    else {// For other billing frequencies
                        balAmt = bal.getContractsGrantsBeginningBalanceAmount().add(bal.getAccountLineAnnualBalanceAmount());
                        cumAmt = cumAmt.add(balAmt);
                    }
                }

            }
        }
        // To set Budgets and cumulative amounts
        invoiceAccountDetail.setBudgetAmount(budAmt);
        invoiceAccountDetail.setCumulativeAmount(cumAmt);
    }

    /**
     * This method helps in setting up basic values for Contracts Grants Invoice Document
     */
    protected void populateContractsGrantsInvoiceDocument(ContractsAndGrantsBillingAward award, ContractsGrantsInvoiceDocument document) {
        if (ObjectUtils.isNotNull(award.getAgency())) {
            if (ObjectUtils.isNotNull(document.getAccountsReceivableDocumentHeader())) {
                document.getAccountsReceivableDocumentHeader().setCustomerNumber(award.getAgency().getCustomerNumber());
            }
            Customer customer = getCustomerService().getByPrimaryKey(award.getAgency().getCustomerNumber());
            if (ObjectUtils.isNotNull(customer)) {
                document.setCustomerName(customer.getCustomerName());
            }
        }
        // To set open invoice indicator to true to help doing cash control for the invoice
        document.setOpenInvoiceIndicator(true);

        // To set LOC creation type and appropriate values from award.
        if (StringUtils.isNotEmpty(award.getLetterOfCreditCreationType())) {
            document.setLetterOfCreditCreationType(award.getLetterOfCreditCreationType());
        }
        // To set up values for Letter of Credit Fund and Fund Group irrespective of the LOC Creation type.
        if (StringUtils.isNotEmpty(award.getLetterOfCreditFundCode())) {
            document.setLetterOfCreditFundCode(award.getLetterOfCreditFundCode());
        }
        if (ObjectUtils.isNotNull(award.getLetterOfCreditFund())) {
            if (StringUtils.isNotEmpty(award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode())) {
                document.setLetterOfCreditFundGroupCode(award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode());
            }
        }

        // To set Account Receivable object code when the parameter is 3.

        String receivableOffsetOption = getParameterService().getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
        List<ContractsGrantsAwardInvoiceAccountInformation> awardInvoiceAccounts = new ArrayList<ContractsGrantsAwardInvoiceAccountInformation>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        map.put(KFSPropertyConstants.ACTIVE, true);
        awardInvoiceAccounts = kualiModuleService.getResponsibleModuleService(ContractsGrantsAwardInvoiceAccountInformation.class).getExternalizableBusinessObjectsList(ContractsGrantsAwardInvoiceAccountInformation.class, map);
        if (isUsingReceivableFAU) {
            if (!CollectionUtils.isEmpty(awardInvoiceAccounts)) {
                for (ContractsGrantsAwardInvoiceAccountInformation awardInvoiceAccount : awardInvoiceAccounts) {
                    if (awardInvoiceAccount.getAccountType().equals(ArPropertyConstants.AR_ACCOUNT)) {
                        if (awardInvoiceAccount.isActive()) {// consider the active invoice account only.
                            document.setPaymentChartOfAccountsCode(awardInvoiceAccount.getChartOfAccountsCode());
                            document.setPaymentAccountNumber(awardInvoiceAccount.getAccountNumber());
                            document.setPaymentSubAccountNumber(awardInvoiceAccount.getSubAccountNumber());
                            document.setPaymentFinancialObjectCode(awardInvoiceAccount.getObjectCode());
                            document.setPaymentFinancialSubObjectCode(awardInvoiceAccount.getSubObjectCode());
                        }
                    }
                }
            }
        }

        // set totalBilled by Account Number in Account Details
        Map<String, KualiDecimal> totalBilledByAccountNumberMap = new HashMap<String, KualiDecimal>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : document.getInvoiceDetailAccountObjectCodes()) {
            String accountNumber = invoiceDetailAccountObjectCode.getAccountNumber();
            KualiDecimal totalBilled = cleanAmount(totalBilledByAccountNumberMap.get(accountNumber));
            totalBilled = totalBilled.add(invoiceDetailAccountObjectCode.getTotalBilled());
            totalBilledByAccountNumberMap.put(accountNumber, totalBilled);
        }

        KualiDecimal totalExpendituredAmount = KualiDecimal.ZERO;
        for (InvoiceAccountDetail invAcctD : document.getAccountDetails()) {
            KualiDecimal currentExpenditureAmount = KualiDecimal.ZERO;
            if (totalBilledByAccountNumberMap.get(invAcctD.getAccountNumber()) != null) {
                invAcctD.setBilledAmount(totalBilledByAccountNumberMap.get(invAcctD.getAccountNumber()));
            }

            currentExpenditureAmount = invAcctD.getCumulativeAmount().subtract(invAcctD.getBilledAmount());
            invAcctD.setExpenditureAmount(currentExpenditureAmount);
            // overwriting account detail expenditure amount if locReview Indicator is true - and award belongs to LOC Billing
            for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                if (awardAccount.getAccountNumber().equals(invAcctD.getAccountNumber()) && awardAccount.isLetterOfCreditReviewIndicator() && award.getPreferredBillingFrequency().equalsIgnoreCase(ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
                    currentExpenditureAmount = awardAccount.getAmountToDraw();
                    invAcctD.setExpenditureAmount(currentExpenditureAmount);
                }
            }
            totalExpendituredAmount = totalExpendituredAmount.add(currentExpenditureAmount);
        }
        totalExpendituredAmount = totalExpendituredAmount.add(document.getInvoiceGeneralDetail().getBilledToDateAmount());


        KualiDecimal totalMilestoneAmount = KualiDecimal.ZERO;
        // To calculate the total milestone amount.
        if (document.getInvoiceMilestones().size() > 0) {
            for (InvoiceMilestone milestone : document.getInvoiceMilestones()) {
                if (milestone.getMilestoneAmount() != null) {
                    totalMilestoneAmount = totalMilestoneAmount.add(milestone.getMilestoneAmount());
                }
            }
        }
        totalMilestoneAmount = totalMilestoneAmount.add(document.getInvoiceGeneralDetail().getBilledToDateAmount());

        KualiDecimal totalBillAmount = KualiDecimal.ZERO;
        // To calculate the total bill amount.
        if (document.getInvoiceBills().size() > 0) {
            for (InvoiceBill bill : document.getInvoiceBills()) {
                if (bill.getEstimatedAmount() != null) {
                    totalBillAmount = totalBillAmount.add(bill.getEstimatedAmount());
                }
            }
        }
        totalBillAmount = totalBillAmount.add(document.getInvoiceGeneralDetail().getBilledToDateAmount());


        // To set the New Total Billed Amount.
        if (document.getInvoiceMilestones().size() > 0) {
            document.getInvoiceGeneralDetail().setNewTotalBilled(totalMilestoneAmount);
        }
        else if (document.getInvoiceBills().size() > 0) {
            document.getInvoiceGeneralDetail().setNewTotalBilled(totalBillAmount);
        }
        else {
            document.getInvoiceGeneralDetail().setNewTotalBilled(totalExpendituredAmount);
        }
    }

    /**
     * Given a List of AwardAccountObjectCodeTotalBilled objects and InvoiceDetailAccountObjectCode objects, looks for those objects within the list of object codes for category and sums the
     * relevant billed and expenditure amounts
     * @param invoiceDetail the detail to update
     * @param awardAccountObjectCodeTotalBilleds the award account object code billed totals
     * @param invoiceDetailAccountObjectCodes the invoice detail account object codes
     * @param objectCodesForCategory the object codes for the given category
     */
    protected void performBilledAndExpenditureCalculationForDetail(ContractsGrantsInvoiceDetail invoiceDetail, List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilleds, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes, Set<String> objectCodesForCategory) {
        invoiceDetail.setCumulative(KualiDecimal.ZERO);
        invoiceDetail.setExpenditures(KualiDecimal.ZERO);
        invoiceDetail.setBilled(KualiDecimal.ZERO);
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            if (objectCodesForCategory.contains(invoiceDetailAccountObjectCode.getFinancialObjectCode())) {
                invoiceDetail.setCumulative(invoiceDetail.getCumulative().add(invoiceDetailAccountObjectCode.getCumulativeExpenditures()));
                invoiceDetail.setExpenditures(invoiceDetail.getExpenditures().add(invoiceDetailAccountObjectCode.getCurrentExpenditures()));
            }
        }
        for (AwardAccountObjectCodeTotalBilled accountObjectCodeTotalBilled : awardAccountObjectCodeTotalBilleds) {
            if (objectCodesForCategory.contains(accountObjectCodeTotalBilled.getFinancialObjectCode())) {
                invoiceDetail.setBilled(invoiceDetail.getBilled().add(accountObjectCodeTotalBilled.getTotalBilled())); // this adds up all the total billed based on object code into categories; sum for this category.
            }
        }
    }

    /**
     * The budget calculations for individual Invoice Detail object are calculated here. Values are retrieved from GL Balance table
     * and manipulated.
     *
     * @param awardAccounts accounts for a particular award.
     * @param objectCodes set of object codes pertaining to a single category
     * @param awardBeginningDate the beginning date for the award
     */
    protected void performBudgetCalculationsOnInvoiceDetail(ContractsGrantsInvoiceDetail invoiceDetail, List<ContractsAndGrantsBillingAwardAccount> awardAccounts, Set<String> completeObjectCodeArrayForSingleCategory, Date awardBeginningDate) {
        KualiDecimal budAmt = KualiDecimal.ZERO;
        KualiDecimal balAmt = KualiDecimal.ZERO;
        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
            // To retrieve the complete set of object codes and then categorize them based on object codes and BalanceType
            List<Balance> glBalances = new ArrayList<Balance>();
            Integer currentYear = getUniversityDateService().getCurrentFiscalYear();
            List<Integer> fiscalYears = new ArrayList<Integer>();
            Calendar c = Calendar.getInstance();


            Integer fiscalYear = getUniversityDateService().getFiscalYear(awardBeginningDate);

            for (Integer i = fiscalYear; i <= currentYear; i++) {
                fiscalYears.add(i);
            }

            List<String> objectList = new ArrayList<String>(completeObjectCodeArrayForSingleCategory);
            for (Integer eachFiscalYr : fiscalYears) {
                Map<String, Object> balanceKeys = new HashMap<String, Object>();
                balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
                balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
                balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, eachFiscalYr);
                balanceKeys.put(KFSPropertyConstants.BALANCE_TYPE_CODE, ArPropertyConstants.BUDGET_BALANCE_TYPE);
                balanceKeys.put(KFSPropertyConstants.OBJECT_TYPE_CODE, ArPropertyConstants.EXPENSE_OBJECT_TYPE);
                balanceKeys.put(KFSPropertyConstants.OBJECT_CODE, objectList);
                glBalances.addAll(getBusinessObjectService().findMatching(Balance.class, balanceKeys));
            }
            for (Balance glBalance : glBalances) {
                if (ObjectUtils.isNull(glBalance.getSubAccount()) || ObjectUtils.isNull(glBalance.getSubAccount().getA21SubAccount()) || !StringUtils.equalsIgnoreCase(glBalance.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
                    balAmt = glBalance.getContractsGrantsBeginningBalanceAmount().add(glBalance.getAccountLineAnnualBalanceAmount());
                    budAmt = budAmt.add(balAmt);
                }
            }
        }

        invoiceDetail.setBudget(budAmt);// Setting current budget value here
    }

    /**
     * This method would make sure the amounts of the currrent period are not included. So it calculates the cumulative and
     * subtracts the current period values. This would be done for Billing Frequencies - Monthly, Quarterly, Semi-Annual and Annual.
     *
     * @param glBalance
     * @return balanceAmount
     */
    protected KualiDecimal calculateBalanceAmountWithoutLastBilledPeriod(java.sql.Date lastBilledDate, Balance glBalance) {
        // 1. calculate invoice period
        AccountingPeriod invoicePeriod = accountingPeriodService.getByDate(lastBilledDate);
        String invoicePeriodCode = invoicePeriod.getUniversityFiscalPeriodCode();

        // 2. Get the current Period Code
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        java.sql.Date today = new java.sql.Date(ts.getTime());
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
        String currentPeriodCode = currPeriod.getUniversityFiscalPeriodCode();

        KualiDecimal currentBalanceAmount =
                cleanAmount(glBalance.getMonth1Amount())
                .add(cleanAmount(glBalance.getMonth2Amount()))
                .add(cleanAmount(glBalance.getMonth3Amount()))
                .add(cleanAmount(glBalance.getMonth4Amount()))
                .add(cleanAmount(glBalance.getMonth5Amount()))
                .add(cleanAmount(glBalance.getMonth6Amount()))
                .add(cleanAmount(glBalance.getMonth7Amount()))
                .add(cleanAmount(glBalance.getMonth8Amount()))
                .add(cleanAmount(glBalance.getMonth9Amount()))
                .add(cleanAmount(glBalance.getMonth10Amount()))
                .add(cleanAmount(glBalance.getMonth11Amount()))
                .add(cleanAmount(glBalance.getMonth12Amount()))
                .add(cleanAmount(glBalance.getMonth13Amount()));

        KualiDecimal balAmt = glBalance.getContractsGrantsBeginningBalanceAmount().add(glBalance.getAccountLineAnnualBalanceAmount());
        KualiDecimal accurateBalanceAmount = balAmt.subtract(currentBalanceAmount);

        return accurateBalanceAmount;
    }

    /**
     * Null protects the addition in retrieveAccurateBalanceAmount
     * @param amount the amount to return
     * @return zero if the amount was null, the given amount otherwise
     */
    protected KualiDecimal cleanAmount(KualiDecimal amount) {
        return amount == null ? KualiDecimal.ZERO : amount;
    }

    /**
     * @see org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService#retrieveNonLOCAwards()
     */
    @Override
    public Collection<ContractsAndGrantsBillingAward> retrieveNonLOCAwards() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.ACTIVE, true);
        // It would be nice not to have to manually remove LOC awards, maybe when we convert to KRAD
        // we could leverage the KRAD-DATA Criteria framework to avoid this
        Collection<ContractsAndGrantsBillingAward> awards = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingAward.class, map);
        Iterator<ContractsAndGrantsBillingAward> it = awards.iterator();
        while (it.hasNext()) {
            ContractsAndGrantsBillingAward award = it.next();
            if (StringUtils.equalsIgnoreCase(award.getPreferredBillingFrequency(), ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
                it.remove();
            }
        }
        return awards;
    }

    /**
     * @see org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService#validateAwards(java.util.Collection, java.util.Collection)
     */
    @Override
    public Collection<ContractsAndGrantsBillingAward> validateAwards(Collection<ContractsAndGrantsBillingAward> awards, Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs, String errOutputFile, String creationProcessTypeCode) {
        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();

        if (ObjectUtils.isNull(contractsGrantsInvoiceDocumentErrorLogs)) {
            contractsGrantsInvoiceDocumentErrorLogs = new ArrayList<ContractsGrantsInvoiceDocumentErrorLog>();
        }

        performAwardValidation(awards, invalidGroup, qualifiedAwards);

        if (!CollectionUtils.isEmpty(invalidGroup)) {
            if (StringUtils.isNotBlank(errOutputFile)) {
                writeErrorToFile(invalidGroup, errOutputFile);
            }
            storeValidationErrors(invalidGroup, contractsGrantsInvoiceDocumentErrorLogs, creationProcessTypeCode);
        }

        return qualifiedAwards;
    }

    /**
     * Perform all validation checks on the awards passed in to determine if CGB Invoice documents can be
     * created for the given awards.
     *
     * @param awards to be validated
     * @param invalidGroup Map of errors per award that failed validation
     * @param qualifiedAwards List of awards that are valid to create CGB Invoice docs from
     */
    protected void performAwardValidation(Collection<ContractsAndGrantsBillingAward> awards, Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup, List<ContractsAndGrantsBillingAward> qualifiedAwards) {

        for (ContractsAndGrantsBillingAward award : awards) {
            List<String> errorList = new ArrayList<String>();

            if (award.getAwardBeginningDate() != null) {
                if (award.getPreferredBillingFrequency() != null && getContractsGrantsBillingAwardVerificationService().isValueOfPreferredBillingFrequencyValid(award)) {
                    if (verifyBillingFrequencyService.validatBillingFrequency(award)) {
                        validateAward(errorList, award);
                    } else {
                        errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_INVALID_BILLING_PERIOD));
                    }
                } else {
                    errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_BILLING_FREQUENCY_MISSING_ERROR));
                }
            } else {
                errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_START_DATE_MISSING_ERROR));
            }

            if (errorList.size() > 0) {
                invalidGroup.put(award, errorList);
            } else {
                qualifiedAwards.add(award);
            }

        }
    }


    /**
     * Perform validation for an award to determine if a CGB Invoice document can be created for the award.
     *
     * @param errorList list of validation errors per award
     * @param award to perform validation upon
     */
    protected void validateAward(List<String> errorList, ContractsAndGrantsBillingAward award) {
        // 1. Award Invoicing suspended by user.
        if (award.isSuspendInvoicingIndicator()) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_USER_SUSPENDED_ERROR));
        }

        // 2. Award is Inactive
        if (!award.isActive()) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_INACTIVE_ERROR));
        }

        // 4. Award invoicing option is missing
        if (StringUtils.isEmpty(award.getInvoicingOptions())) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_INVOICING_OPTION_MISSING_ERROR));
        }

        // 5. Award preferred billing frequency is not set correctly
        if (!getContractsGrantsBillingAwardVerificationService().isPreferredBillingFrequencySetCorrectly(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_SINGLE_ACCOUNT_ERROR));
        }

        // 6. Award has no accounts assigned
        if (getContractsGrantsInvoiceDocumentService().hasNoActiveAccountsAssigned(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_NO_ACTIVE_ACCOUNTS_ASSIGNED_ERROR));
        }

        // 7. Award contains expired account or accounts
        Collection<Account> expAccounts = getContractsGrantsInvoiceDocumentService().getExpiredAccountsOfAward(award);
        if (ObjectUtils.isNotNull(expAccounts) && !expAccounts.isEmpty()) {
            StringBuilder line = new StringBuilder();
            line.append(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_CONAINS_EXPIRED_ACCOUNTS_ERROR));

            for (Account expAccount : expAccounts) {
                line.append(" (expired account: " + expAccount.getAccountNumber() + " expiration date " + expAccount.getAccountExpirationDate() + ") ");
            }
            errorList.add(line.toString());
        }
        // 8. Award has final invoice Billed already
        if (getContractsGrantsBillingAwardVerificationService().isAwardFinalInvoiceAlreadyBuilt(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_FINAL_BILLED_ERROR));
        }

        // 9. Award has no valid milestones to invoice
        if (!getContractsGrantsBillingAwardVerificationService().hasMilestonesToInvoice(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_MILESTONES));
        }

        // 10. All has no valid bills to invoice
        if (!getContractsGrantsBillingAwardVerificationService().hasBillsToInvoice(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_BILLS));
        }

        // 11. Agency has no matching Customer record
        if (getContractsGrantsBillingAwardVerificationService().owningAgencyHasNoCustomerRecord(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_AGENCY_NO_CUSTOMER_RECORD));
        }

        // 12. All accounts of an Award have zero$ to invoice
        if (!CollectionUtils.isEmpty(award.getActiveAwardAccounts()) && CollectionUtils.isEmpty(getValidAwardAccounts(award.getActiveAwardAccounts(), award))) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_VALID_ACCOUNTS));
        }

        // 13. Award does not have appropriate Contract Control Accounts set based on Invoicing Options
        List<String> errorString = contractsGrantsInvoiceDocumentService.checkAwardContractControlAccounts(award);
        if (!CollectionUtils.isEmpty(errorString) && errorString.size() > 1) {
            errorList.add(configurationService.getPropertyValueAsString(errorString.get(0)).replace("{0}", errorString.get(1)));
        }

        // 14. System Information and ORganization Accounting Default not setup.
        if (!getContractsGrantsBillingAwardVerificationService().isChartAndOrgSetupForInvoicing(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_SYS_INFO_OADF_NOT_SETUP));
        }

        // 15. if there is no AR Invoice Account present when the GLPE is 3.
        if (!getContractsGrantsBillingAwardVerificationService().hasARInvoiceAccountAssigned(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_NO_AR_INV_ACCOUNT));
        }

        // 16. If all accounts of award has invoices in progress.
        if ((award.getPreferredBillingFrequency().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) || award.getPreferredBillingFrequency().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) && getContractsGrantsBillingAwardVerificationService().isInvoiceInProgress(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_INVOICES_IN_PROGRESS));
        }

        // 17. Offset Definition is not available when the GLPE is 3.
        if (!getContractsGrantsBillingAwardVerificationService().isOffsetDefinitionSetupForInvoicing(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_OFFSET_DEF_NOT_SETUP));
        }
    }

    protected void writeErrorToFile(Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup, String errOutputFile) {
        PrintStream outputFileStream = null;
        File errOutPutfile = new File(errOutputFile);
        try {
            outputFileStream = new PrintStream(errOutPutfile);
            writeReportHeader(outputFileStream);

            for (ContractsAndGrantsBillingAward award : invalidGroup.keySet()) {
                writeErrorEntryByAward(award, invalidGroup.get(award), outputFileStream);
            }

            outputFileStream.printf("\r\n");
        } catch (IOException ioe) {
            LOG.error("Could not write errors in contracts and grants invoice document creation process to file" + ioe.getMessage());
            throw new RuntimeException("Could not write errors in contracts and grants invoice document creation process to file", ioe);
        } finally {
            if (outputFileStream != null) {
                outputFileStream.close();
            }
        }
    }

    protected void storeValidationErrors(Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup, Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs, String creationProcessTypeCode) {
        for (ContractsAndGrantsBillingAward award : invalidGroup.keySet()) {
            KualiDecimal cumulativeExpenses = KualiDecimal.ZERO;
            ContractsGrantsInvoiceDocumentErrorLog contractsGrantsInvoiceDocumentErrorLog = new ContractsGrantsInvoiceDocumentErrorLog();

            if (ObjectUtils.isNotNull(award)){
                String proposalNumber = award.getProposalNumber().toString();
                Date beginningDate = award.getAwardBeginningDate();
                Date endingDate = award.getAwardEndingDate();
                KualiDecimal totalAmount = award.getAwardTotalAmount();

                contractsGrantsInvoiceDocumentErrorLog.setProposalNumber(award.getProposalNumber());
                contractsGrantsInvoiceDocumentErrorLog.setAwardBeginningDate(beginningDate);
                contractsGrantsInvoiceDocumentErrorLog.setAwardEndingDate(endingDate);
                contractsGrantsInvoiceDocumentErrorLog.setAwardTotalAmount(award.getAwardTotalAmount().bigDecimalValue());
                if (ObjectUtils.isNotNull(award.getAwardPrimaryFundManager())) {
                    contractsGrantsInvoiceDocumentErrorLog.setPrimaryFundManagerPrincipalId(award.getAwardPrimaryFundManager().getPrincipalId());
                }
                if (!CollectionUtils.isEmpty(award.getActiveAwardAccounts())) {
                    boolean firstLineFlag = true;

                    for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                        cumulativeExpenses = cumulativeExpenses.add(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount, ArPropertyConstants.ACTUAL_BALANCE_TYPE, beginningDate));
                        if (firstLineFlag) {
                            firstLineFlag = false;
                            contractsGrantsInvoiceDocumentErrorLog.setAccounts(awardAccount.getAccountNumber());
                        }
                        else {
                            contractsGrantsInvoiceDocumentErrorLog.setAccounts(contractsGrantsInvoiceDocumentErrorLog.getAccounts() + ";" + awardAccount.getAccountNumber());
                        }
                    }
                }
                contractsGrantsInvoiceDocumentErrorLog.setCumulativeExpensesAmount(cumulativeExpenses.bigDecimalValue());
            }

            for (String vCat : invalidGroup.get(award)) {
                ContractsGrantsInvoiceDocumentErrorMessage contractsGrantsInvoiceDocumentErrorCategory = new ContractsGrantsInvoiceDocumentErrorMessage();
                contractsGrantsInvoiceDocumentErrorCategory.setErrorMessageText(vCat);
                contractsGrantsInvoiceDocumentErrorLog.getErrorMessages().add(contractsGrantsInvoiceDocumentErrorCategory);
            }

            contractsGrantsInvoiceDocumentErrorLog.setErrorDate(dateTimeService.getCurrentTimestamp());
            contractsGrantsInvoiceDocumentErrorLog.setCreationProcessTypeCode(creationProcessTypeCode);
            businessObjectService.save(contractsGrantsInvoiceDocumentErrorLog);
            contractsGrantsInvoiceDocumentErrorLogs.add(contractsGrantsInvoiceDocumentErrorLog);
        }
    }

    protected void storeCreationErrors(List<ErrorMessage> errorMessages, String creationProcessTypeCode) {
        for (ErrorMessage errorMessage : errorMessages) {
            ContractsGrantsInvoiceDocumentErrorLog contractsGrantsInvoiceDocumentErrorLog = new ContractsGrantsInvoiceDocumentErrorLog();

            ContractsGrantsInvoiceDocumentErrorMessage contractsGrantsInvoiceDocumentErrorCategory = new ContractsGrantsInvoiceDocumentErrorMessage();
            contractsGrantsInvoiceDocumentErrorCategory.setErrorMessageText(MessageFormat.format(configurationService.getPropertyValueAsString(errorMessage.getErrorKey()), (Object[])errorMessage.getMessageParameters()));
            contractsGrantsInvoiceDocumentErrorLog.getErrorMessages().add(contractsGrantsInvoiceDocumentErrorCategory);

            contractsGrantsInvoiceDocumentErrorLog.setErrorDate(dateTimeService.getCurrentTimestamp());
            contractsGrantsInvoiceDocumentErrorLog.setCreationProcessTypeCode(creationProcessTypeCode);
            businessObjectService.save(contractsGrantsInvoiceDocumentErrorLog);
        }
    }

    /**
     * This method retrieves all the contracts grants invoice documents with a status of Saved and
     * routes them to the next step in the routing path.
     *
     * @return True if the routing was performed successfully. A runtime exception will be thrown if any errors occur while routing.
     * @see org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceDocumentCreateService#routeContractsGrantsInvoiceDocuments()
     */
    @Override
    public void routeContractsGrantsInvoiceDocuments() {
        final String currentUserPrincipalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
        List<String> documentIdList = retrieveContractsGrantsInvoiceDocumentsToRoute(DocumentStatus.SAVED, currentUserPrincipalId);

        if (LOG.isInfoEnabled()) {
            LOG.info("CGinvoice to Route: " + documentIdList);
        }

        for (String cgInvoiceDocId : documentIdList) {
            try {
                ContractsGrantsInvoiceDocument cgInvoicDoc = (ContractsGrantsInvoiceDocument) documentService.getByDocumentHeaderId(cgInvoiceDocId);
                // To route documents only if the user in the session is same as the initiator.
                if (LOG.isInfoEnabled()) {
                    LOG.info("Routing Contracts Grants Invoice document # " + cgInvoiceDocId + ".");
                }
                documentService.prepareWorkflowDocument(cgInvoicDoc);

                // calling workflow service to bypass business rule checks
                workflowDocumentService.route(cgInvoicDoc.getDocumentHeader().getWorkflowDocument(), "", null);
            } catch (WorkflowException e) {
                LOG.error("Error routing document # " + cgInvoiceDocId + " " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    /**
     * Returns a list of all saved but not yet routed contracts grants invoice documents, using the KualiWorkflowInfo service.
     *
     * @return a list of contracts grants invoice documents to route
     */
    protected List<String> retrieveContractsGrantsInvoiceDocumentsToRoute(DocumentStatus statusCode, String initiatorPrincipalId) {
        List<String> documentIds = new ArrayList<String>();

        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);
        fieldValues.put(KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, statusCode.getCode());
        fieldValues.put(KFSPropertyConstants.INITIATOR_PRINCIPAL_ID, initiatorPrincipalId);
        Collection<FinancialSystemDocumentHeader> docHeaders = businessObjectService.findMatching(FinancialSystemDocumentHeader.class, fieldValues);

        for (FinancialSystemDocumentHeader docHeader : docHeaders) {
            documentIds.add(docHeader.getDocumentNumber());
        }
        return documentIds;
    }

    protected void writeErrorEntryByAward(ContractsAndGrantsBillingAward award, List<String> validationCategory, PrintStream printStream) throws IOException {
        // %15s %18s %20s %19s %15s %18s %23s %18s
        if (ObjectUtils.isNotNull(award)){
            KualiDecimal cumulativeExpenses = KualiDecimal.ZERO;
            String awardBeginningDate;
            String awardEndingDate;
            String awardTotalAmount;

            String proposalNumber = award.getProposalNumber().toString();
            Date beginningDate = award.getAwardBeginningDate();
            Date endingDate = award.getAwardEndingDate();
            KualiDecimal totalAmount = award.getAwardTotalAmount();

            if (ObjectUtils.isNotNull(beginningDate)) {
                awardBeginningDate = beginningDate.toString();
            } else {
                awardBeginningDate = "null award beginning date";
            }

            if (ObjectUtils.isNotNull(endingDate)) {
                awardEndingDate = endingDate.toString();
            } else {
                awardEndingDate = "null award ending date";
            }

            if (ObjectUtils.isNotNull(totalAmount) && ObjectUtils.isNotNull(totalAmount.bigDecimalValue())) {
                awardTotalAmount = totalAmount.toString();
            } else {
                awardTotalAmount = "null award total amount";
            }

            if (CollectionUtils.isEmpty(award.getActiveAwardAccounts())) {
                writeToReport(proposalNumber, "", awardBeginningDate, awardEndingDate, awardTotalAmount, cumulativeExpenses.toString(), printStream);
            }
            else {
                // calculate cumulativeExpenses
                for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    cumulativeExpenses = cumulativeExpenses.add(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount, ArPropertyConstants.ACTUAL_BALANCE_TYPE, award.getAwardBeginningDate()));
                }
                boolean firstLineFlag = true;

                for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    if (firstLineFlag) {
                        writeToReport(proposalNumber, awardAccount.getAccountNumber(), awardBeginningDate, awardEndingDate, awardTotalAmount, cumulativeExpenses.toString(), printStream);
                        firstLineFlag = false;
                    }
                    else {
                        writeToReport("", awardAccount.getAccountNumber(), "", "", "", "", printStream);
                    }
                }
            }
        }
        // To print all the errors from the validation category.
        for (String vCat : validationCategory) {
            printStream.printf("%s", "     " + vCat);
            printStream.printf("\r\n");
        }
        printStream.printf(REPORT_LINE_DIVIDER);
        printStream.printf("\r\n");
    }

    protected void writeToReport(String proposalNumber, String accountNumber, String awardBeginningDate, String awardEndingDate, String awardTotalAmount, String cumulativeExpenses, PrintStream printStream) throws IOException {
        printStream.printf("%15s", proposalNumber);
        printStream.printf("%18s", accountNumber);
        printStream.printf("%20s", awardBeginningDate);
        printStream.printf("%19s", awardEndingDate);
        printStream.printf("%15s", awardTotalAmount);
        printStream.printf("%23s", cumulativeExpenses);
        printStream.printf("\r\n");
    }

    /**
     * @param printStream
     * @throws IOException
     */
    protected void writeReportHeader(PrintStream printStream) throws IOException {
        printStream.printf("%15s%18s%20s%19s%15s%23s\r\n", "Proposal Number", "Account Number", "Award Start Date", "Award Stop Date", "Award Total", "Cumulative Expenses");
        printStream.printf("%23s", "Validation Category");
        printStream.printf("\r\n");
        printStream.printf(REPORT_LINE_DIVIDER);
        printStream.printf("\r\n");
    }


    /**
     * This method returns the valid award accounts based on evaluation of preferred billing frequency and invoice document status
     *
     * @param awardAccounts
     * @return valid awardAccounts
     */
    protected List<ContractsAndGrantsBillingAwardAccount> getValidAwardAccounts(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsAndGrantsBillingAward award) {
        if (!award.getPreferredBillingFrequency().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !award.getPreferredBillingFrequency().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            List<ContractsAndGrantsBillingAwardAccount> validAwardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
            final Set<Account> invalidAccounts = harvestAccountsFromContractsGrantsInvoices(getInProgressInvoicesForAward(award));
            for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
                if (!invalidAccounts.contains(awardAccount.getAccount())) {
                    validAwardAccounts.add(awardAccount);
                }
            }

            return validAwardAccounts;
        }
        else {
            return awardAccounts;
        }

    }

    /**
     * Pulls all the unique accounts from the source accounting lines on the given ContractsGrantsInvoiceDocument
     * @param contractsGrantsInvoices the invoices to pull unique accounts from
     * @return a Set of the unique accounts
     */
    protected Set<Account> harvestAccountsFromContractsGrantsInvoices(Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoices) {
       Set<Account> accounts = new HashSet<Account>();
       for (ContractsGrantsInvoiceDocument invoice : contractsGrantsInvoices) {
           for (InvoiceAccountDetail invoiceAccountDetail : invoice.getAccountDetails()) {
               final Account account = getAccountService().getByPrimaryId(invoiceAccountDetail.getChartOfAccountsCode(), invoiceAccountDetail.getAccountNumber());
               if (!ObjectUtils.isNull(account)) {
                   accounts.add(account);
               }
           }
       }
       return accounts;
    }

    /**
     * Looks up all the in progress contracts & grants invoices for the award
     * @param award the award to look up contracts & grants invoices for
     * @return a Collection matching in progress/pending Contracts & Grants Invoice documents
     */
    protected Collection<ContractsGrantsInvoiceDocument> getInProgressInvoicesForAward(ContractsAndGrantsBillingAward award) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, financialSystemDocumentService.getPendingDocumentStatuses());

        return businessObjectService.findMatching(ContractsGrantsInvoiceDocument.class, fieldValues);
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Sets the accountingPeriodService attribute value.
     *
     * @param accountingPeriodService The accountingPeriodService to set.
     */
    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }


    /**
     * Sets the verifyBillingFrequencyService attribute value.
     *
     * @param verifyBillingFrequencyService The verifyBillingFrequencyService to set.
     */
    public void setVerifyBillingFrequencyService(VerifyBillingFrequencyService verifyBillingFrequencyService) {
        this.verifyBillingFrequencyService = verifyBillingFrequencyService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     *
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }


    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }


    /**
     * Sets the accountsReceivableDocumentHeaderService attribute value.
     *
     * @param accountsReceivableDocumentHeaderService The accountsReceivableDocumentHeaderService to set.
     */
    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }

    /**
     * Sets the contractsGrantsInvoiceDocumentService attribute value.
     *
     * @param contractsGrantsInvoiceDocumentService The contractsGrantsInvoiceDocumentService to set.
     */
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }


    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }


    public ConfigurationService getConfigurationService() {
        return configurationService;
    }


    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }


    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }


    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }


    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }


    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public AwardAccountObjectCodeTotalBilledDao getAwardAccountObjectCodeTotalBilledDao() {
        return awardAccountObjectCodeTotalBilledDao;
    }

    public void setAwardAccountObjectCodeTotalBilledDao(AwardAccountObjectCodeTotalBilledDao awardAccountObjectCodeTotalBilledDao) {
        this.awardAccountObjectCodeTotalBilledDao = awardAccountObjectCodeTotalBilledDao;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public ContractsGrantsBillingAwardVerificationService getContractsGrantsBillingAwardVerificationService() {
        return contractsGrantsBillingAwardVerificationService;
    }

    public void setContractsGrantsBillingAwardVerificationService(ContractsGrantsBillingAwardVerificationService contractsGrantsBillingAwardVerificationService) {
        this.contractsGrantsBillingAwardVerificationService = contractsGrantsBillingAwardVerificationService;
    }
}