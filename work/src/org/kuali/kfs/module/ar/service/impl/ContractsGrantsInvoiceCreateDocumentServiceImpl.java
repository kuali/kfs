/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
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
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsOrganization;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArParameterKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.AwardAccountObjectCodeTotalBilled;
import org.kuali.kfs.module.ar.businessobject.Bill;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorMessage;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceAddressDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceBill;
import org.kuali.kfs.module.ar.businessobject.InvoiceDetailAccountObjectCode;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceMilestone;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.dataaccess.AwardAccountObjectCodeTotalBilledDao;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsBillingAwardVerificationService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.service.ContractsGrantsBillingUtilityService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.service.CostCategoryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.kfs.sys.service.OptionsService;
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
    protected ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected CostCategoryService costCategoryService;
    protected CustomerService customerService;
    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected FinancialSystemDocumentService financialSystemDocumentService;
    protected KualiModuleService kualiModuleService;
    protected ParameterService parameterService;
    protected VerifyBillingFrequencyService verifyBillingFrequencyService;
    protected WorkflowDocumentService workflowDocumentService;
    protected UniversityDateService universityDateService;
    protected OptionsService optionsService;

    public static final String REPORT_LINE_DIVIDER = "--------------------------------------------------------------------------------------------------------------";

    /**
     * @see org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService#createCGInvoiceDocumentsByAwards(java.util.Collection, org.kuali.kfs.module.ar.ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType)
     */
    @Override
    public List<ErrorMessage> createCGInvoiceDocumentsByAwards(Collection<ContractsAndGrantsBillingAward> awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType creationProcessTypeCode) {
        List<ErrorMessage> errorMessages = createInvoices(awards, null, null);

        if (!CollectionUtils.isEmpty(errorMessages)) {
            storeCreationErrors(errorMessages, creationProcessTypeCode.getCode());
        }

        return errorMessages;
    }

    /**
     * @see org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService#createCGInvoiceDocumentsByAwards(java.util.Collection, java.util.List, java.lang.String)
     */
    @Override
    public List<ErrorMessage> createCGInvoiceDocumentsByAwards(Collection<ContractsAndGrantsBillingAward> awards, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails, String locCreationType) {
        List<ErrorMessage> errorMessages = createInvoices(awards, accountDetails, locCreationType);

        if (!CollectionUtils.isEmpty(errorMessages)) {
            storeCreationErrors(errorMessages, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.LOC.getCode());
        }

        return errorMessages;
    }

    /**
     * This method iterates through awards and create cgInvoice documents
     * @param awards used to create cgInvoice documents
     * @param accountDetails letter of credit details if we're creating via loc
     * @param locCreationType letter of credit creation type if we're creating via loc
     * @return List of error messages (if any)
     */
    protected List<ErrorMessage> createInvoices(Collection<ContractsAndGrantsBillingAward> awards, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails, String locCreationType) {
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        if (ObjectUtils.isNotNull(awards) && awards.size() > 0) {
            for (ContractsAndGrantsBillingAward awd : awards) {
                String invOpt = awd.getInvoicingOptionCode();
                final ContractsAndGrantsOrganization awardOrganization = awd.getPrimaryAwardOrganization();
                if (ObjectUtils.isNull(awardOrganization)) {
                    final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NO_ORGANIZATION_ON_AWARD, awd.getProposalNumber().toString());
                    errorMessages.add(errorMessage);
                } else {
                    if (invOpt.equals(ArConstants.INV_ACCOUNT)) { // case 1: create Contracts & Grants Invoice by accounts
                        createInvoicesByAccounts(awd, errorMessages, accountDetails, locCreationType);
                    }
                    else if (invOpt.equals(ArConstants.INV_CONTRACT_CONTROL_ACCOUNT)) { // case 2: create Contracts & Grants Invoices by contractControlAccounts
                        createInvoicesByContractControlAccounts(awd, errorMessages, accountDetails, locCreationType);
                    }
                    // case 3: create Contracts & Grants Invoice by award
                    else if (invOpt.equals(ArConstants.INV_AWARD)) {
                        createInvoicesByAward(awd, errorMessages, accountDetails, locCreationType);
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
     * Generates and saves a single Contracts & Grants Invoice Document based on the given award
     * @param awd the award to generate a Contracts & Grants Invoice Document for
     * @param errLines a holder for error messages
     * @param accountDetails letter of credit details if we're creating via loc
     * @param locCreationType letter of credit creation type if we're creating via loc
     */
    protected void createInvoicesByAward(ContractsAndGrantsBillingAward awd, List<ErrorMessage> errorMessages, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails, String locCreationType) {
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
                if (!getValidAwardAccounts(awd.getActiveAwardAccounts(), awd).containsAll(awd.getActiveAwardAccounts())) {
                    final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NOT_ALL_BILLABLE_ACCOUNTS, awd.getProposalNumber().toString());
                    errorMessages.add(errorMessage);
                }
                generateAndSaveContractsAndGrantsInvoiceDocument(awd, getValidAwardAccounts(awd.getActiveAwardAccounts(), awd), coaCode, orgCode, errorMessages, accountDetails, locCreationType);
            }
        }
    }


    /**
     * Generates and saves Contracts & Grants Invoice Documents based on the given award's contract control accounts
     * @param awd the award with contract control accounts to build Contracts & Grants Invoice Documents from
     * @param errLines a holder for error messages
     * @param accountDetails letter of credit details if we're creating via loc
     * @param locCreationType letter of credit creation type if we're creating via loc
     */
    protected void createInvoicesByContractControlAccounts(ContractsAndGrantsBillingAward awd, List<ErrorMessage> errorMessages, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails, String locCreationType) {
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
                    tmpAcctList.clear();

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
                    generateAndSaveContractsAndGrantsInvoiceDocument(awd, getValidAwardAccounts(tmpAcctList, awd), awd.getPrimaryAwardOrganization().getChartOfAccountsCode(), awd.getPrimaryAwardOrganization().getOrganizationCode(), errorMessages, accountDetails, locCreationType);
                }
            }
            else {
                final ErrorMessage errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.BILL_BY_CONTRACT_LACKS_CONTROL_ACCOUNT, awd.getProposalNumber().toString());
                errorMessages.add(errorMessage);
            }
        }
    }

    /**
     * Generates and saves Contracts & Grants Invoice Documents based on the award accounts of the passed in award
     * @param awd the award to build Contracts & Grants Invoice Documents from the award accounts on
     * @param errLines a holder for error messages
     * @param accountDetails letter of credit details if we're creating via loc
     * @param locCreationType letter of credit creation type if we're creating via loc
     */
    protected void createInvoicesByAccounts(ContractsAndGrantsBillingAward awd, List<ErrorMessage> errorMessages, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails, String locCreationType) {
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

                generateAndSaveContractsAndGrantsInvoiceDocument(awd, getValidAwardAccounts(tmpAcctList, awd), awd.getPrimaryAwardOrganization().getChartOfAccountsCode(), awd.getPrimaryAwardOrganization().getOrganizationCode(), errorMessages, accountDetails, locCreationType);
            }
        }
    }

    /**
     * Generates and then saves a Contracts & Grants Invoice Document
     * @param awd the award for the document
     * @param validAwardAccounts the award accounts which should appear on the document
     * @param coaCode the chart code for the document
     * @param orgCode the organization code for the document
     * @param errLines a List of error messages, to be appended to if there are errors in document generation
     * @param accountDetails letter of credit details if we're creating via loc
     * @param locCreationType letter of credit creation type if we're creating via loc
     */
    protected void generateAndSaveContractsAndGrantsInvoiceDocument(ContractsAndGrantsBillingAward awd, List<ContractsAndGrantsBillingAwardAccount> validAwardAccounts, final String coaCode, final String orgCode, List<ErrorMessage> errorMessages, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails, String locCreationType) {
        ContractsGrantsInvoiceDocument cgInvoiceDocument = createCGInvoiceDocumentByAwardInfo(awd, validAwardAccounts, coaCode, orgCode, errorMessages, accountDetails, locCreationType);
        if (ObjectUtils.isNotNull(cgInvoiceDocument)) {
            if ( !KualiDecimal.ZERO.equals(cgInvoiceDocument.getTotalInvoiceAmount()) ||
                    StringUtils.equalsIgnoreCase(awd.getBillingFrequencyCode(), ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) ||
                    StringUtils.equalsIgnoreCase(awd.getBillingFrequencyCode(), ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
                // Saving the document
                try {
                    documentService.saveDocument(cgInvoiceDocument, DocumentSystemSaveEvent.class);
                } catch (WorkflowException ex) {
                    LOG.error("Error creating cgin documents: " + ex.getMessage(), ex);
                    throw new RuntimeException("Error creating cgin documents: " + ex.getMessage(), ex);
                }
            } else {
                ErrorMessage errorMessage;
                if (!awd.getActiveAwardAccounts().isEmpty()) {
                     errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NON_BILLABLE, awd.getActiveAwardAccounts().get(0).getAccountNumber(), awd.getProposalNumber().toString());
                } else {
                    errorMessage = new ErrorMessage(ArKeyConstants.ContractsGrantsInvoiceCreateDocumentConstants.NON_BILLABLE, null, awd.getProposalNumber().toString());

                }
                errorMessages.add(errorMessage);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService#createCGInvoiceDocumentByAwardInfo(org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward, java.util.List, java.lang.String, java.lang.String, java.util.List, java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsInvoiceDocument createCGInvoiceDocumentByAwardInfo(ContractsAndGrantsBillingAward awd, List<ContractsAndGrantsBillingAwardAccount> accounts, String chartOfAccountsCode, String organizationCode, List<ErrorMessage> errorMessages, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails, String locCreationType) {
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

                    populateInvoiceFromAward(awd, accounts, cgInvoiceDocument, accountDetails, locCreationType);
                    contractsGrantsInvoiceDocumentService.createSourceAccountingLines(cgInvoiceDocument, accounts);
                    if (ObjectUtils.isNotNull(cgInvoiceDocument.getInvoiceGeneralDetail().getAward())) {
                        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(cgInvoiceDocument);
                    }

                    LOG.info("Created Contracts & Grants Invoice Document " + cgInvoiceDocument.getDocumentNumber());
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
     * @param accountDetails letter of credit details if we're creating via loc
     * @param locCreationType letter of credit creation type if we're creating via loc
     */
    protected void populateInvoiceFromAward(ContractsAndGrantsBillingAward award, List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsGrantsInvoiceDocument document, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails, String locCreationType) {
        if (ObjectUtils.isNotNull(award)) {
            // Invoice General Detail section
            InvoiceGeneralDetail invoiceGeneralDetail = new InvoiceGeneralDetail();
            invoiceGeneralDetail.setDocumentNumber(document.getDocumentNumber());
            invoiceGeneralDetail.setProposalNumber(award.getProposalNumber());
            invoiceGeneralDetail.setAward(award);

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
                final List<InvoiceAddressDetail> invoiceAddressDetails = buildInvoiceAddressDetailsFromAgency(agency, document);
                document.getInvoiceAddressDetails().addAll(invoiceAddressDetails);
            }

            java.sql.Date invoiceDate = document.getInvoiceGeneralDetail().getLastBilledDate();
            if (document.getInvoiceGeneralDetail().getBillingFrequencyCode().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {// To check if award has milestones
                final List<Milestone> milestones = getContractsGrantsBillingUtilityService().getActiveMilestonesForProposalNumber(award.getProposalNumber());
                if (!CollectionUtils.isEmpty(milestones)) {
                    // copy award milestones to invoice milestones
                    document.getInvoiceMilestones().clear();
                    final List<InvoiceMilestone> invoiceMilestones = buildInvoiceMilestones(milestones, invoiceDate);
                    document.getInvoiceMilestones().addAll(invoiceMilestones);
                }
            }
            else if (document.getInvoiceGeneralDetail().getBillingFrequencyCode().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {// To check if award has bills
                final List<Bill> bills = getContractsGrantsBillingUtilityService().getActiveBillsForProposalNumber(award.getProposalNumber());
                if (!CollectionUtils.isEmpty(bills)) {
                    // copy award milestones to invoice milestones
                    document.getInvoiceBills().clear();
                    final List<InvoiceBill> invoiceBills = buildInvoiceBills(bills, invoiceDate);
                    document.getInvoiceBills().addAll(invoiceBills);
                }
            }

            // copy award's accounts to invoice account details
            document.getAccountDetails().clear();
            final List<InvoiceAccountDetail> invoiceAccountDetails = new ArrayList<>();
            List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectsCodes = new ArrayList<>();
            Map<String, KualiDecimal> budgetAmountsByCostCategory = new HashMap<>();

            Integer currentYear = getUniversityDateService().getCurrentFiscalYear();
            final boolean firstFiscalPeriod = isFirstFiscalPeriod();
            final Integer fiscalYear = firstFiscalPeriod && useTimeBasedBillingFrequency(document.getInvoiceGeneralDetail().getBillingFrequencyCode()) ? currentYear - 1 : currentYear;

            final SystemOptions systemOptions = optionsService.getOptions(fiscalYear);

            List<String> balanceTypeCodeList = new ArrayList<String>();
            balanceTypeCodeList.add(systemOptions.getBudgetCheckingBalanceTypeCd());
            balanceTypeCodeList.add(systemOptions.getActualFinancialBalanceTypeCd());
            for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
                InvoiceAccountDetail invoiceAccountDetail = buildInvoiceAccountDetailForAwardAccount(award, awardAccount, document.getDocumentNumber(), document.getInvoiceGeneralDetail());
                final ContractsGrantsLetterOfCreditReviewDetail locReviewDetail = retrieveMatchingLetterOfCreditReviewDetail(awardAccount, accountDetails);

                List<Balance> glBalances = retrieveBalances(fiscalYear, awardAccount.getChartOfAccountsCode(), awardAccount.getAccountNumber(), balanceTypeCodeList);
                KualiDecimal awardAccountBudgetAmount = KualiDecimal.ZERO;
                KualiDecimal balanceAmount = KualiDecimal.ZERO;
                KualiDecimal awardAccountCumulativeAmount = KualiDecimal.ZERO;
                for (Balance balance : glBalances) {
                    if (!isBalanceCostShare(balance)) {
                        if (balance.getBalanceTypeCode().equalsIgnoreCase(systemOptions.getBudgetCheckingBalanceTypeCd())) {
                            awardAccountBudgetAmount = addBalanceToAwardAccountBudgetAmount(balance, awardAccountBudgetAmount, firstFiscalPeriod);
                            updateCategoryBudgetAmountsByBalance(balance, budgetAmountsByCostCategory, firstFiscalPeriod);
                        }
                        else if (balance.getBalanceTypeCode().equalsIgnoreCase(systemOptions.getActualFinancialBalanceTypeCd())) {
                            awardAccountCumulativeAmount = addBalanceToAwardAccountCumulativeAmount(document, balance, award, awardAccountCumulativeAmount, firstFiscalPeriod);
                            updateCategoryActualAmountsByBalance(document, balance, award, invoiceDetailAccountObjectsCodes, firstFiscalPeriod);
                        }
                    }
                    invoiceAccountDetail.setTotalBudget(awardAccountBudgetAmount);
                    invoiceAccountDetail.setCumulativeExpenditures(awardAccountCumulativeAmount);
                }
                invoiceAccountDetails.add(invoiceAccountDetail);
                if (!ObjectUtils.isNull(locReviewDetail) && !locReviewDetail.getClaimOnCashBalance().negated().equals(locReviewDetail.getAmountToDraw()) && StringUtils.equalsIgnoreCase(award.getBillingFrequencyCode(), ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
                    distributeAmountAmongAllAccountObjectCodes(document, awardAccount, invoiceDetailAccountObjectsCodes, locReviewDetail);
                }
                else {
                    updateInvoiceDetailAccountObjectCodesByBilledAmount(awardAccount, invoiceDetailAccountObjectsCodes);
                }
            }
            document.getAccountDetails().addAll(invoiceAccountDetails);
            if (!document.getInvoiceGeneralDetail().getBillingFrequencyCode().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !document.getInvoiceGeneralDetail().getBillingFrequencyCode().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
                document.getInvoiceDetailAccountObjectCodes().addAll(invoiceDetailAccountObjectsCodes);
                List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilleds = getAwardAccountObjectCodeTotalBilledDao().getAwardAccountObjectCodeTotalBuildByProposalNumberAndAccount(awardAccounts);
                List<ContractsGrantsInvoiceDetail> invoiceDetails = generateValuesForCategories(document.getDocumentNumber(), document.getInvoiceDetailAccountObjectCodes(), budgetAmountsByCostCategory, awardAccountObjectCodeTotalBilleds);
                document.getInvoiceDetails().addAll(invoiceDetails);
            }
            // Set some basic values to invoice Document
            populateContractsGrantsInvoiceDocument(award, document, accountDetails, locCreationType);
        }
    }

    /**
     * Finds a letter of credit review detail which matches the given award account, or null if one could not be found
     * @param awardAccount the award account to find a matching contracts grants letter of credit review detail for
     * @param accountDetails a List of contracts grants letter of credit review details
     * @return the matching contracts grants review detail, or null if one could not be found
     */
    protected ContractsGrantsLetterOfCreditReviewDetail retrieveMatchingLetterOfCreditReviewDetail(ContractsAndGrantsBillingAwardAccount awardAccount, List<ContractsGrantsLetterOfCreditReviewDetail> accountDetails) {
        if (CollectionUtils.isEmpty(accountDetails)) {
            return null;
        }
        for (ContractsGrantsLetterOfCreditReviewDetail reviewDetail : accountDetails) {
            if (reviewDetail.getProposalNumber().equals(awardAccount.getProposalNumber()) && StringUtils.equals(reviewDetail.getChartOfAccountsCode(), awardAccount.getChartOfAccountsCode()) && StringUtils.equals(reviewDetail.getAccountNumber(), awardAccount.getAccountNumber())) {
                return reviewDetail;
            }
        }
        return null;
    }

    /**
     * Updates the appropriate amounts for the InvoiceDetailAccountObjectCode matching the given balance
     * @param document the CINV document we're generating
     * @param balance the balance to update amounts by
     * @param award the award on the CINV document we're generating
     * @param invoiceDetailAccountObjectsCodes the List of invoiceDetailObjectCodes to update one of
     * @param firstFiscalPeriod whether we're generating the CINV document in the fiscal fiscal period or not
     */
    protected void updateCategoryActualAmountsByBalance(ContractsGrantsInvoiceDocument document, Balance balance, ContractsAndGrantsBillingAward award, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes, boolean firstFiscalPeriod) {
        final CostCategory category = getCostCategoryService().getCostCategoryForObjectCode(balance.getUniversityFiscalYear(), balance.getChartOfAccountsCode(), balance.getObjectCode());
        if (!ObjectUtils.isNull(category)) {
            final InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = getInvoiceDetailAccountObjectCodeByBalanceAndCategory(invoiceDetailAccountObjectCodes, balance, document.getDocumentNumber(), document.getInvoiceGeneralDetail().getProposalNumber(), category);

            if (useTimeBasedBillingFrequency(document.getInvoiceGeneralDetail().getBillingFrequencyCode())) {
                if (firstFiscalPeriod) {
                    invoiceDetailAccountObjectCode.setCumulativeExpenditures(cleanAmount(invoiceDetailAccountObjectCode.getCumulativeExpenditures()).add(cleanAmount(balance.getContractsGrantsBeginningBalanceAmount())).add(cleanAmount(balance.getAccountLineAnnualBalanceAmount())));
                    if (!includePeriod13InPeriod01Calculations()) {
                        invoiceDetailAccountObjectCode.setCumulativeExpenditures(cleanAmount(invoiceDetailAccountObjectCode.getCumulativeExpenditures()).subtract(cleanAmount(balance.getMonth13Amount())));
                    }
                } else {
                    invoiceDetailAccountObjectCode.setCumulativeExpenditures(cleanAmount(invoiceDetailAccountObjectCode.getCumulativeExpenditures()).add(calculateBalanceAmountWithoutLastBilledPeriod(document.getInvoiceGeneralDetail().getLastBilledDate(), balance)));
                }
            }
            else {// For other billing frequencies
                KualiDecimal balanceAmount = cleanAmount(balance.getContractsGrantsBeginningBalanceAmount()).add(cleanAmount(balance.getAccountLineAnnualBalanceAmount()));
                invoiceDetailAccountObjectCode.setCumulativeExpenditures(cleanAmount(invoiceDetailAccountObjectCode.getCumulativeExpenditures()).add(cleanAmount(balance.getContractsGrantsBeginningBalanceAmount()).add(cleanAmount(balance.getAccountLineAnnualBalanceAmount()))));
            }
        }
    }

    /**
     * Sums the balance to the given awardAccountCumulativeAmount and returns that summed amount
     * @param document the CINV document we're generating
     * @param balance the balance to update amounts by
     * @param award the award on the CINV document we're generating
     * @param awardAccountCumulativeAmount the beginning cumulative expense amount for the award account of the balance
     * @param firstFiscalPeriod whether we're generating the CINV document in the fiscal fiscal period or not
     * @return the updated cumulative amount on the award account
     */
    protected KualiDecimal addBalanceToAwardAccountCumulativeAmount(ContractsGrantsInvoiceDocument document, Balance balance, ContractsAndGrantsBillingAward award, KualiDecimal awardAccountCumulativeAmount, boolean firstFiscalPeriod) {
        if (useTimeBasedBillingFrequency(document.getInvoiceGeneralDetail().getBillingFrequencyCode())) {
            if (firstFiscalPeriod) {
                KualiDecimal newAwardAccountCumulativeAmount = awardAccountCumulativeAmount.add(cleanAmount(balance.getContractsGrantsBeginningBalanceAmount())).add(cleanAmount(balance.getAccountLineAnnualBalanceAmount()));
                if (!includePeriod13InPeriod01Calculations()) {
                    newAwardAccountCumulativeAmount = awardAccountCumulativeAmount.subtract(balance.getMonth13Amount());
                }
                return newAwardAccountCumulativeAmount;
            } else {
                return awardAccountCumulativeAmount.add(calculateBalanceAmountWithoutLastBilledPeriod(award.getLastBilledDate(), balance));
            }
        }
        else {// For other billing frequencies
            KualiDecimal balanceAmount = cleanAmount(balance.getContractsGrantsBeginningBalanceAmount()).add(cleanAmount(balance.getAccountLineAnnualBalanceAmount()));
            return awardAccountCumulativeAmount.add(balanceAmount);
        }
    }

    /**
     * Updates the cost category budget amount (in the given Map, budgetAmountsByCostCategory) by the total amount of the balance
     * @param balance the balance to update the budget amounts by
     * @param budgetAmountsByCostCategory the Map of budget amounts sorted by cost category
     * @param firstFiscalPeriod whether this CINV is being generated in the first fiscal period or not
     * @return the updated award account budget amount
     */
    protected void updateCategoryBudgetAmountsByBalance(Balance balance, Map<String, KualiDecimal> budgetAmountsByCostCategory, boolean firstFiscalPeriod) {
        CostCategory category = getCostCategoryService().getCostCategoryForObjectCode(balance.getUniversityFiscalYear(), balance.getChartOfAccountsCode(), balance.getObjectCode());
        if (!ObjectUtils.isNull(category)) {
            final KualiDecimal balanceAmount = getBudgetBalanceAmount(balance, firstFiscalPeriod);
            KualiDecimal categoryBudgetAmount = budgetAmountsByCostCategory.get(category.getCategoryCode());
            if (categoryBudgetAmount == null) {
                categoryBudgetAmount = KualiDecimal.ZERO;
            }
            categoryBudgetAmount = categoryBudgetAmount.add(balanceAmount);
            budgetAmountsByCostCategory.put(category.getCategoryCode(), categoryBudgetAmount);
        } else {
            LOG.warn("Could not find cost category for balance: "+balance.getUniversityFiscalYear()+" "+balance.getChartOfAccountsCode()+" "+balance.getAccountNumber()+" "+balance.getSubAccountNumber()+" "+balance.getObjectCode()+" "+balance.getSubObjectCode()+" "+balance.getBalanceTypeCode());
        }
    }

    /**
     * Adds the budget balance to the award account budget amount
     * @param balance the balance to update the budget amounts by
     * @param awardAccountBudgetAmount the beginning award account budget amount
     * @param firstFiscalPeriod whether this CINV is being generated in the first fiscal period or not
     * @return the updated award account budget amount
     */
    protected KualiDecimal addBalanceToAwardAccountBudgetAmount(Balance balance, KualiDecimal awardAccountBudgetAmount, boolean firstFiscalPeriod) {
        final KualiDecimal balanceAmount = getBudgetBalanceAmount(balance, firstFiscalPeriod);
        return awardAccountBudgetAmount.add(balanceAmount);
    }

    /**
     * Determines the balance amount (cg + annual) from the given budget balance
     * @param balance balance to find amount from
     * @param firstFiscalPeriod whether the CINV is being created in the first fiscal period or not
     * @return the total amount from the balance
     */
    protected KualiDecimal getBudgetBalanceAmount(Balance balance, boolean firstFiscalPeriod) {
        KualiDecimal balanceAmount = balance.getContractsGrantsBeginningBalanceAmount().add(balance.getAccountLineAnnualBalanceAmount());
        if (firstFiscalPeriod && !includePeriod13InPeriod01Calculations()) {
            balanceAmount = balanceAmount.subtract(balance.getMonth13Amount()); // get rid of period 13 if we should not include in calculations
        }
        return balanceAmount;
    }

    /**
     * Builds a new invoice account detail for a given award account
     * @param award the award associated with the award account
     * @param awardAccount the award account to build the invoice account detail for
     * @param documentNumber the number of the document we're currently building
     * @param invoiceGeneralDetail the invoice general detail for the the document we're currently building
     * @return the built invoice account detail
     */
    protected InvoiceAccountDetail buildInvoiceAccountDetailForAwardAccount(ContractsAndGrantsBillingAward award, ContractsAndGrantsBillingAwardAccount awardAccount, final String documentNumber, InvoiceGeneralDetail invoiceGeneralDetail) {
        InvoiceAccountDetail invoiceAccountDetail = new InvoiceAccountDetail();
        invoiceAccountDetail.setDocumentNumber(documentNumber);

        invoiceAccountDetail.setAccountNumber(awardAccount.getAccountNumber());
        if (ObjectUtils.isNotNull(awardAccount.getAccount()) && StringUtils.isNotEmpty(awardAccount.getAccount().getContractControlAccountNumber())) {
            invoiceAccountDetail.setContractControlAccountNumber(awardAccount.getAccount().getContractControlAccountNumber());
        }
        invoiceAccountDetail.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
        invoiceAccountDetail.setProposalNumber(awardAccount.getProposalNumber());
        return invoiceAccountDetail;
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
                invBill.setBillNumber(awdBill.getBillNumber());
                invBill.setBillIdentifier(awdBill.getBillIdentifier());
                invBill.setBillDescription(awdBill.getBillDescription());
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
                invMilestone.setMilestoneNumber(awdMilestone.getMilestoneNumber());
                invMilestone.setMilestoneIdentifier(awdMilestone.getMilestoneIdentifier());
                invMilestone.setMilestoneDescription(awdMilestone.getMilestoneDescription());
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
    protected List<InvoiceAddressDetail> buildInvoiceAddressDetailsFromAgency(ContractsAndGrantsBillingAgency agency, ContractsGrantsInvoiceDocument document) {
        Map<String, Object> mapKey = new HashMap<String, Object>();
        mapKey.put(KFSPropertyConstants.CUSTOMER_NUMBER, agency.getCustomerNumber());
        final List<CustomerAddress> customerAddresses = (List<CustomerAddress>) businessObjectService.findMatching(CustomerAddress.class, mapKey);
        String documentNumber = document.getDocumentNumber();

        List<InvoiceAddressDetail> invoiceAddressDetails = new ArrayList<>();
        for (CustomerAddress customerAddress : customerAddresses) {
            if (StringUtils.equalsIgnoreCase(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY, customerAddress.getCustomerAddressTypeCode())) {
                document.setCustomerBillToAddressOnInvoice(customerAddress);
            }
            InvoiceAddressDetail invoiceAddressDetail = new InvoiceAddressDetail();
            invoiceAddressDetail.setCustomerNumber(customerAddress.getCustomerNumber());
            invoiceAddressDetail.setDocumentNumber(documentNumber);
            invoiceAddressDetail.setCustomerAddressIdentifier(customerAddress.getCustomerAddressIdentifier());
            invoiceAddressDetail.setCustomerAddressTypeCode(customerAddress.getCustomerAddressTypeCode());
            invoiceAddressDetail.setCustomerAddressName(customerAddress.getCustomerAddressName());
            invoiceAddressDetail.setInvoiceTransmissionMethodCode(customerAddress.getInvoiceTransmissionMethodCode());
            invoiceAddressDetail.setCustomerEmailAddress(customerAddress.getCustomerEmailAddress());
            invoiceAddressDetail.setCustomerLine1StreetAddress(customerAddress.getCustomerLine1StreetAddress());
            invoiceAddressDetail.setCustomerLine2StreetAddress(customerAddress.getCustomerLine2StreetAddress());
            invoiceAddressDetail.setCustomerCityName(customerAddress.getCustomerCityName());
            invoiceAddressDetail.setCustomerStateCode(customerAddress.getCustomerStateCode());
            invoiceAddressDetail.setCustomerZipCode(customerAddress.getCustomerZipCode());
            invoiceAddressDetail.setCustomerCountryCode(customerAddress.getCustomerCountryCode());
            invoiceAddressDetail.setCustomerInternationalMailCode(customerAddress.getCustomerInternationalMailCode());
            invoiceAddressDetail.setCustomerAddressInternationalProvinceName(customerAddress.getCustomerAddressInternationalProvinceName());
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
     * @param documentNumber the number of the document we want to add invoice details to
     * @param invoiceDetailAccountObjectCodes the List of InvoiceDetailAccountObjectCodes containing amounts to sum into our invoice details
     * @param budgetAmountsByCostCategory the budget amounts, sorted by cost category
     * @param awardAccountObjectCodeTotalBilleds the business objects containg what has been billed from the document's award accounts already
     */
    public List<ContractsGrantsInvoiceDetail> generateValuesForCategories(String documentNumber, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes, Map<String, KualiDecimal> budgetAmountsByCostCategory, List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilleds) {
        Collection<CostCategory> costCategories = retrieveAllBillingCategories();
        List<ContractsGrantsInvoiceDetail> invoiceDetails = new ArrayList<>();
        Map<String, List<InvoiceDetailAccountObjectCode>> invoiceDetailAccountObjectCodesMap = mapInvoiceDetailAccountObjectCodesByCategoryCode(invoiceDetailAccountObjectCodes);
        Map<String, List<AwardAccountObjectCodeTotalBilled>> billedsMap = mapAwardAccountObjectCodeTotalBilledsByCategoryCode(awardAccountObjectCodeTotalBilleds);

        for (CostCategory category : costCategories) {
            ContractsGrantsInvoiceDetail invDetail = new ContractsGrantsInvoiceDetail();
            invDetail.setDocumentNumber(documentNumber);
            invDetail.setCategoryCode(category.getCategoryCode());
            invDetail.setCostCategory(category);
            invDetail.setIndirectCostIndicator(category.isIndirectCostIndicator());
            // calculate total billed first
            invDetail.setCumulativeExpenditures(KualiDecimal.ZERO);
            invDetail.setInvoiceAmount(KualiDecimal.ZERO);
            invDetail.setTotalPreviouslyBilled(KualiDecimal.ZERO);

            List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodesForCategory = invoiceDetailAccountObjectCodesMap.get(category.getCategoryCode());
            if (!CollectionUtils.isEmpty(invoiceDetailAccountObjectCodesForCategory)) {
                for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodesForCategory) {
                    invDetail.setCumulativeExpenditures(invDetail.getCumulativeExpenditures().add(invoiceDetailAccountObjectCode.getCumulativeExpenditures()));
                    invDetail.setInvoiceAmount(invDetail.getInvoiceAmount().add(invoiceDetailAccountObjectCode.getCurrentExpenditures()));
                }
            }
            List<AwardAccountObjectCodeTotalBilled> billedForCategory = billedsMap.get(category.getCategoryCode());
            if (!CollectionUtils.isEmpty(billedForCategory)) {
                for (AwardAccountObjectCodeTotalBilled accountObjectCodeTotalBilled : billedForCategory) {
                    invDetail.setTotalPreviouslyBilled(invDetail.getTotalPreviouslyBilled().add(accountObjectCodeTotalBilled.getTotalBilled())); // this adds up all the total billed based on object code into categories; sum for this category.
                }
            }

            // calculate the rest using billed to date
            if (!ObjectUtils.isNull(budgetAmountsByCostCategory.get(category.getCategoryCode()))) {
                invDetail.setTotalBudget(budgetAmountsByCostCategory.get(category.getCategoryCode()));
            } else {
                invDetail.setTotalBudget(KualiDecimal.ZERO);
            }
            invoiceDetails.add(invDetail);
        }
        return invoiceDetails;
    }

    /**
     * Converts a List of InvoiceDetailAccountObjectCodes into a map where the key is the category code
     * @param invoiceDetailAccountObjectCodes a List of InvoiceDetailAccountObjectCodes
     * @return that List converted to a Map, keyed by category code
     */
    protected Map<String, List<InvoiceDetailAccountObjectCode>> mapInvoiceDetailAccountObjectCodesByCategoryCode(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        Map<String, List<InvoiceDetailAccountObjectCode>> invoiceDetailAccountObjectCodesMap = new HashMap<>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodesForCategory = invoiceDetailAccountObjectCodesMap.get(invoiceDetailAccountObjectCode.getCategoryCode());
            if (invoiceDetailAccountObjectCodesForCategory == null) {
                invoiceDetailAccountObjectCodesForCategory = new ArrayList<>();
            }
            invoiceDetailAccountObjectCodesForCategory.add(invoiceDetailAccountObjectCode);
            invoiceDetailAccountObjectCodesMap.put(invoiceDetailAccountObjectCode.getCategoryCode(), invoiceDetailAccountObjectCodesForCategory);
        }
        return invoiceDetailAccountObjectCodesMap;
    }

    /**
     * Converts a List of AwardAccountObjectCodeTotalBilled into a Map, keyed by the Cost Category which most closely matches them
     * @param awardAccountObjectCodeTotalBilleds the List of AwardAccountObjectCodeTotalBilled business objects to Map
     * @return the Mapped AwardAccountObjectCodeTotalBilled records
     */
    protected Map<String, List<AwardAccountObjectCodeTotalBilled>> mapAwardAccountObjectCodeTotalBilledsByCategoryCode( List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilleds) {
        Integer fiscalYear = getUniversityDateService().getCurrentFiscalYear();
        Map<String, List<AwardAccountObjectCodeTotalBilled>> billedsMap = new HashMap<>();
        for (AwardAccountObjectCodeTotalBilled billed : awardAccountObjectCodeTotalBilleds) {
            final CostCategory category = getCostCategoryService().getCostCategoryForObjectCode(fiscalYear, billed.getChartOfAccountsCode(), billed.getFinancialObjectCode());
            if (!ObjectUtils.isNull(category)) {
                List<AwardAccountObjectCodeTotalBilled> billedForCategory = billedsMap.get(category.getCategoryCode());
                if (billedForCategory == null) {
                    billedForCategory = new ArrayList<>();
                }
                billedForCategory.add(billed);
                billedsMap.put(category.getCategoryCode(), billedForCategory);
            } else {
                LOG.warn("Could not find cost category for AwardAccountObjectCodeTotalBilled, fiscal year = "+fiscalYear+" "+billed.getChartOfAccountsCode()+" "+billed.getFinancialObjectCode());
            }
        }
        return billedsMap;
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
        if (ObjectUtils.isNotNull(award.getBillingFrequencyCode())) {
            invoiceGeneralDetail.setBillingFrequencyCode(award.getBillingFrequencyCode());
        }
        if (ObjectUtils.isNotNull(award.getInstrumentTypeCode())) {
            invoiceGeneralDetail.setInstrumentTypeCode(award.getInstrumentTypeCode());
        }
        // To set Award Date range - this would be (Award Start Date to Award Stop Date)
        String awdDtRange = award.getAwardBeginningDate() + " to " + award.getAwardEndingDate();
        invoiceGeneralDetail.setAwardDateRange(awdDtRange);

        // set the billed to Date Field
        // To check if award has milestones
        if (StringUtils.equalsIgnoreCase(invoiceGeneralDetail.getBillingFrequencyCode(), ArConstants.MILESTONE_BILLING_SCHEDULE_CODE)) {
            invoiceGeneralDetail.setTotalPreviouslyBilled(contractsGrantsInvoiceDocumentService.getMilestonesBilledToDateAmount(award.getProposalNumber()));
        }
        else if (StringUtils.equalsIgnoreCase(invoiceGeneralDetail.getBillingFrequencyCode(),ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            invoiceGeneralDetail.setTotalPreviouslyBilled(contractsGrantsInvoiceDocumentService.getPredeterminedBillingBilledToDateAmount(award.getProposalNumber()));
        }
        else {
            invoiceGeneralDetail.setTotalPreviouslyBilled(contractsGrantsInvoiceDocumentService.getAwardBilledToDateAmountByProposalNumber(award.getProposalNumber()));
        }
    }

    /**
     * For letter of credit, this distributes the amount for matching LOC invoice detail account object codes (which is very probably all of invoice detail account object codes in the given list) evenly
     * @param document the CINV document we're creating
     * @param awdAcct the C&G Award Account
     * @param invoiceDetailAccountObjectsCodes the List of invoice detail account object codes we're attempting to generate
     * @param locReviewDetail the contracts grants letter of credit review detail which is related to the given award account
     */
    protected void distributeAmountAmongAllAccountObjectCodes(ContractsGrantsInvoiceDocument document, ContractsAndGrantsBillingAwardAccount awdAcct, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectsCodes, ContractsGrantsLetterOfCreditReviewDetail locReviewDetail) {
        final List<InvoiceDetailAccountObjectCode> locRedistributionInvoiceDetailAccountObjectCodes = filterInvoiceAccountObjectCodesByDocumentAndAccount(document, awdAcct, invoiceDetailAccountObjectsCodes);
        final Map<String, List<InvoiceDetailAccountObjectCode>> locRedistributionAccountObjectCodesByCategory = mapInvoiceDetailAccountObjectCodesByCategoryCode(locRedistributionInvoiceDetailAccountObjectCodes);
        final Map<String, BigDecimal> percentagesByCategory = calculatePercentagesByCategory(locRedistributionAccountObjectCodesByCategory, locReviewDetail.getClaimOnCashBalance().negated());
        final Map<String, KualiDecimal> amountsByCategory = calculateAmountsByCategory(percentagesByCategory, locReviewDetail.getAmountToDraw());
        redistributeAmountsToInvoiceAccountCategories(locRedistributionAccountObjectCodesByCategory, amountsByCategory);
        takeAPennyLeaveAPennyCGBStyle(locRedistributionInvoiceDetailAccountObjectCodes, locReviewDetail.getAmountToDraw());
    }

    /**
     * Filters the given list of invoice detail account object codes by the given document and account
     * @param document the document which owns the invoice detail account object codes
     * @param awdAcct the award account to find invoice detail account object codes for
     * @param invoiceDetailAccountObjectsCodes the original list of invoice detail account object codes
     * @return a list of invoice detail account object codes associated with the given document and the given account
     */
    protected List<InvoiceDetailAccountObjectCode> filterInvoiceAccountObjectCodesByDocumentAndAccount(ContractsGrantsInvoiceDocument document, ContractsAndGrantsBillingAwardAccount awdAcct, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectsCodes) {
        List<InvoiceDetailAccountObjectCode> locRedistributionInvoiceDetailAccountObjectCodes = new ArrayList<InvoiceDetailAccountObjectCode>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectsCodes) {
            if (StringUtils.equals(invoiceDetailAccountObjectCode.getDocumentNumber(), document.getDocumentNumber()) && invoiceDetailAccountObjectCode.getProposalNumber().equals(document.getInvoiceGeneralDetail().getProposalNumber()) && StringUtils.equals(invoiceDetailAccountObjectCode.getAccountNumber(), awdAcct.getAccountNumber()) && StringUtils.equals(invoiceDetailAccountObjectCode.getChartOfAccountsCode(), awdAcct.getChartOfAccountsCode())) {
                locRedistributionInvoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode);
            }
        }
        return locRedistributionInvoiceDetailAccountObjectCodes;
    }

    /**
     * Sums the current expenditures of the given invoice detail account object codes
     * @param invoiceDetailAccountObjectCodes invoice detail account object codes to total the current expenditures of
     * @return the total of the current expenditures
     */
    protected InvoiceDetailAccountObjectCode sumInvoiceDetailAccountObjectCodes(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        InvoiceDetailAccountObjectCode total = new InvoiceDetailAccountObjectCode();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            total.setCumulativeExpenditures(total.getCumulativeExpenditures().add(invoiceDetailAccountObjectCode.getCumulativeExpenditures()));
            total.setCurrentExpenditures(total.getCurrentExpenditures().add(invoiceDetailAccountObjectCode.getCurrentExpenditures()));
            total.setTotalBilled(total.getTotalBilled().add(invoiceDetailAccountObjectCode.getTotalBilled()));
        }
        return total;
    }

    /**
     * Calculates the percentage of the given total each list of invoice detail account object codes represents
     * @param invoiceDetailAccountObjectCodesByCategory a Map of invoice detail account object codes mapped by category
     * @param total the total of all of the invoice detail account object codes
     * @return A Map keyed by category where the value is the percentage of the total that category represents
     */
    protected Map<String, BigDecimal> calculatePercentagesByCategory(Map<String, List<InvoiceDetailAccountObjectCode>> invoiceDetailAccountObjectCodesByCategory, KualiDecimal total) {
        Map<String, BigDecimal> percentagesByCategory = new HashMap<>();
        for (String categoryCode : invoiceDetailAccountObjectCodesByCategory.keySet()) {
            if (total.equals(KualiDecimal.ZERO)) {
                percentagesByCategory.put(categoryCode, BigDecimal.ZERO);
            } else {
                percentagesByCategory.put(categoryCode, calculatePercentageByInvoiceDetailAccountObjectCodes(invoiceDetailAccountObjectCodesByCategory.get(categoryCode), total));
            }
        }
        return percentagesByCategory;
    }

    /**
     * Finds the percentage that the given total is of the sum of the current expenditures of the given invoiceDetailAccountObjectCodes
     * @param invoiceDetailAccountObjectCodes a List of invoice detail account object codes to sum
     * @param total the total of all of the invoice detail account object codes for that account
     * @return the percentage of the total of the given List of invoice detail account object code current expenditures are of the given total
     */
    protected BigDecimal calculatePercentageByInvoiceDetailAccountObjectCodes(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes, KualiDecimal total) {
        final KualiDecimal cumulativeExpenditureTotal = sumInvoiceDetailAccountObjectCodes(invoiceDetailAccountObjectCodes).getCumulativeExpenditures();
        return cumulativeExpenditureTotal.bigDecimalValue().divide(total.bigDecimalValue(), 10, RoundingMode.HALF_UP);
    }

    /**
     * Given a Map of category keys mapping percentage values and an amount, find what amount each percentage would be
     * @param percentagesByCategory a map of category code keys mapping percentage values
     * @param amount the amount to split by percentages
     * @return a Map of amounts keyed by category codes
     */
    protected Map<String, KualiDecimal> calculateAmountsByCategory(Map<String, BigDecimal> percentagesByCategory, KualiDecimal amount) {
        final BigDecimal bigDecimalAmount = amount.bigDecimalValue().setScale(2, RoundingMode.HALF_UP);
        Map<String, KualiDecimal> amountsByCategory = new HashMap<>();
        for (String categoryCode : percentagesByCategory.keySet()) {
            amountsByCategory.put(categoryCode, new KualiDecimal(bigDecimalAmount.multiply(percentagesByCategory.get(categoryCode))));
        }
        return amountsByCategory;
    }

    /**
     * Redistributes the given amounts mapped by category to each of the invoice detail account object codes mapped by category code
     * @param redistributionAccountObjectCodesByCategory invoice detail account object codes mapped by category code
     * @param amountsByCategory amounts mapped by category code
     */
    protected void redistributeAmountsToInvoiceAccountCategories(Map<String, List<InvoiceDetailAccountObjectCode>> redistributionAccountObjectCodesByCategory, Map<String, KualiDecimal> amountsByCategory) {
        for (String categoryCode : redistributionAccountObjectCodesByCategory.keySet()) {
            final List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes = redistributionAccountObjectCodesByCategory.get(categoryCode);
            if (invoiceDetailAccountObjectCodes.size() == 1) {
                invoiceDetailAccountObjectCodes.get(0).setCurrentExpenditures(amountsByCategory.get(categoryCode));
            } else {
                splitOutRedistribution(invoiceDetailAccountObjectCodes, amountsByCategory.get(categoryCode));
            }
        }
    }

    /**
     * If the total of current expenditures within the list of InvoiceDetailAccountObjectCode business objects does not meet the amount to target,
     * steal or give a penny from one of those business objects so that it does
     * @param invoiceDetailAccountObjectCodes a List of InvoiceDetailAccountObjectCode business objects
     * @param amountToTarget the amount which the sum of those objects should equal
     */
    protected void takeAPennyLeaveAPennyCGBStyle(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes, KualiDecimal amountToTarget) {
        if (!CollectionUtils.isEmpty(invoiceDetailAccountObjectCodes)) {
            final KualiDecimal currentExpenditureTotal = sumInvoiceDetailAccountObjectCodes(invoiceDetailAccountObjectCodes).getCurrentExpenditures();
            if (!currentExpenditureTotal.equals(amountToTarget)) {
                final KualiDecimal difference = currentExpenditureTotal.subtract(amountToTarget);
                InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = findFirstPositiveCurrentExpenditureInvoiceDetailAccountObjectCode(invoiceDetailAccountObjectCodes);
                if (invoiceDetailAccountObjectCode != null) {
                    invoiceDetailAccountObjectCode.setCurrentExpenditures(invoiceDetailAccountObjectCode.getCurrentExpenditures().subtract(difference));
                }
            }
        }
    }

    /**
     * Given a list of invoice detail account object codes, return the first one with a positive currentExpenditure field
     * @param invoiceDetailAccountObjectCodes the list of InvoiceDetailAccountObjectCodes to find the first one with a positive currentExpenditure
     * @return the first invoice detail account object code with a positive currentExpenditure or null if nothing could be found
     */
    protected InvoiceDetailAccountObjectCode findFirstPositiveCurrentExpenditureInvoiceDetailAccountObjectCode(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes) {
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            if (!ObjectUtils.isNull(invoiceDetailAccountObjectCode.getCurrentExpenditures()) && invoiceDetailAccountObjectCode.getCurrentExpenditures().isPositive()) {
                return invoiceDetailAccountObjectCode;
            }
        }
        return null;
    }

    /**
     * Splits an amount evenly over the given List of invoice detail account object codes
     * @param invoiceDetailAccountObjectCodes a List of invoice detail account object codes to divvy an amount equally among
     * @param amount the amount to divvy
     */
    protected void splitOutRedistribution(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes, KualiDecimal amount) {
        final KualiDecimal amountEach = new KualiDecimal(amount.bigDecimalValue().divide(new BigDecimal(invoiceDetailAccountObjectCodes.size()), 2, RoundingMode.HALF_UP));
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectCodes) {
            invoiceDetailAccountObjectCode.setCurrentExpenditures(amountEach);
        }
    }

    /**
     * Updates all of the given invoice detail object codes by the billed amount for the given award account (and updates the current expenditures accordingly)
     * @param awdAcct the award account to find billing information for
     * @param invoiceDetailAccountObjectsCodes the List of invoice detail account object code business objects to update
     */
    protected void updateInvoiceDetailAccountObjectCodesByBilledAmount(ContractsAndGrantsBillingAwardAccount awdAcct, List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectsCodes) {
        List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilledList = retrieveBillingInformationForAwardAccount(awdAcct);

        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectsCodes) {
            // since there may be multiple accounts represented in the Invoice Detail Account Object Codes, only process the ones that match
            if (StringUtils.equals(invoiceDetailAccountObjectCode.getChartOfAccountsCode(), awdAcct.getChartOfAccountsCode()) &&
                    StringUtils.equals(invoiceDetailAccountObjectCode.getAccountNumber(), awdAcct.getAccountNumber())) {
                if (!CollectionUtils.isEmpty(awardAccountObjectCodeTotalBilledList)) {
                    for (AwardAccountObjectCodeTotalBilled awardAccountObjectCodeTotalBilled : awardAccountObjectCodeTotalBilledList) {
                        if (invoiceDetailAccountObjectCode.getFinancialObjectCode().equalsIgnoreCase(awardAccountObjectCodeTotalBilled.getFinancialObjectCode())) {
                            invoiceDetailAccountObjectCode.setTotalBilled(awardAccountObjectCodeTotalBilled.getTotalBilled());
                        }
                    }
                }
                invoiceDetailAccountObjectCode.setCurrentExpenditures(invoiceDetailAccountObjectCode.getCumulativeExpenditures().subtract(invoiceDetailAccountObjectCode.getTotalBilled()));
            }
        }
    }

    /**
     * Retrieves all of the billing information performed against the given award account
     * @param awdAcct a C&G award account
     * @return the List of billing information
     */
    protected List<AwardAccountObjectCodeTotalBilled> retrieveBillingInformationForAwardAccount(ContractsAndGrantsBillingAwardAccount awdAcct) {
        Map<String, Object> totalBilledKeys = new HashMap<String, Object>();
        totalBilledKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, awdAcct.getProposalNumber());
        totalBilledKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awdAcct.getChartOfAccountsCode());
        totalBilledKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, awdAcct.getAccountNumber());

        List<AwardAccountObjectCodeTotalBilled> awardAccountObjectCodeTotalBilledList = (List<AwardAccountObjectCodeTotalBilled>) businessObjectService.findMatching(AwardAccountObjectCodeTotalBilled.class, totalBilledKeys);
        return awardAccountObjectCodeTotalBilledList;
    }

    /**
     * Determines if today, the document creation date, occurs within the first fiscal period
     * @return true if it is the first fiscal period, false otherwise
     */
    protected boolean isFirstFiscalPeriod() {
        final AccountingPeriod currentPeriod = accountingPeriodService.getByDate(getDateTimeService().getCurrentSqlDate());
        final boolean firstFiscalPeriod = StringUtils.equals(currentPeriod.getUniversityFiscalPeriodCode(), KFSConstants.MONTH1);
        return firstFiscalPeriod;
    }

    /**
     * @return a Collection of all active Contracts & Grants billing categories
     */
    protected Collection<CostCategory> retrieveAllBillingCategories() {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.ACTIVE, true);
        Collection<CostCategory> costCategories = businessObjectService.findMatching(CostCategory.class, criteria);
        return costCategories;
    }

    /**
     * Looks up or constructs an InvoiceDetailAccountObjectCode based on a given balance and billing category
     * @param invoiceDetailAccountObjectCodes the list of invoice detail account object codes to find a matching Invoice Detail Account Object Code in
     * @param bal the balance to get the account object code from
     * @param documentNumber the document number of the CINV doc being created
     * @param proposalNumber the proposal number associated with the award on the CINV document we're currently building
     * @param costCategory the cost category associated with the balance
     * @return the retrieved or constructed (if nothing was found in the database) InvoiceDetailAccountObjectCode object
     */
    protected InvoiceDetailAccountObjectCode getInvoiceDetailAccountObjectCodeByBalanceAndCategory(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectCodes, Balance bal, String documentNumber, final Long proposalNumber, CostCategory category) {
        // Check if there is an existing invoice detail account object code existing (if there are more than one fiscal years)
        InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode = lookupInvoiceDetailAccountObjectCode(invoiceDetailAccountObjectCodes, bal, proposalNumber);

        if (ObjectUtils.isNull(invoiceDetailAccountObjectCode)) {
            if (!ObjectUtils.isNull(category)) {
                invoiceDetailAccountObjectCode = new InvoiceDetailAccountObjectCode();
                invoiceDetailAccountObjectCode.setDocumentNumber(documentNumber);
                invoiceDetailAccountObjectCode.setProposalNumber(proposalNumber);
                invoiceDetailAccountObjectCode.setFinancialObjectCode(bal.getObjectCode());
                invoiceDetailAccountObjectCode.setCategoryCode(category.getCategoryCode());
                invoiceDetailAccountObjectCode.setAccountNumber(bal.getAccountNumber());
                invoiceDetailAccountObjectCode.setChartOfAccountsCode(bal.getChartOfAccountsCode());
                invoiceDetailAccountObjectCodes.add(invoiceDetailAccountObjectCode);
            } else {
                LOG.warn("Could not find cost category for balance: "+bal.getUniversityFiscalYear()+" "+bal.getChartOfAccountsCode()+" "+bal.getAccountNumber()+" "+bal.getSubAccountNumber()+" "+bal.getObjectCode()+" "+bal.getSubObjectCode()+" "+bal.getBalanceTypeCode());
            }
        }
        return invoiceDetailAccountObjectCode;
    }

    /**
     * Looks for a matching invoice detail account object code in the given list that matches the given balance and proposal number
     * @param invoiceDetailAccountObjectsCodes a List of invoice detail account object codes to look up values from
     * @param bal the balance to match
     * @param proposalNumber the proposal number to match
     * @return the matching invoice detail account object code record, or null if no matching record can be found
     */
    protected InvoiceDetailAccountObjectCode lookupInvoiceDetailAccountObjectCode(List<InvoiceDetailAccountObjectCode> invoiceDetailAccountObjectsCodes, Balance bal, final Long proposalNumber) {
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : invoiceDetailAccountObjectsCodes) {
            if (StringUtils.equals(bal.getChartOfAccountsCode(), invoiceDetailAccountObjectCode.getChartOfAccountsCode())
                    && StringUtils.equals(bal.getAccountNumber(), invoiceDetailAccountObjectCode.getAccountNumber())
                    && StringUtils.equals(bal.getObjectCode(), invoiceDetailAccountObjectCode.getFinancialObjectCode())
                    && org.apache.commons.lang.ObjectUtils.equals(proposalNumber, invoiceDetailAccountObjectCode.getProposalNumber())) {
                return invoiceDetailAccountObjectCode;
            }
        }
        return null;
    }

    /**
     * Determines if a balance represents a cost share or not
     * @param bal the balance to check
     * @return true if the balance is a cost share, false otherwise
     */
    protected boolean isBalanceCostShare(Balance bal) {
        return !ObjectUtils.isNull(bal.getSubAccount()) && !ObjectUtils.isNull(bal.getSubAccount().getA21SubAccount()) && StringUtils.equalsIgnoreCase(bal.getSubAccount().getA21SubAccount().getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE);
    }

    /**
     * Given the billing frequency code, determines if the billing is time-based: monthly, quarterly, bi-annual, or annual
     * @param billingFrequencyCode the billing frequency code
     * @return true if time-based billing is used, false if
     */
    protected boolean useTimeBasedBillingFrequency(String billingFrequencyCode) {
        return billingFrequencyCode.equalsIgnoreCase(ArConstants.MONTHLY_BILLING_SCHEDULE_CODE) || billingFrequencyCode.equalsIgnoreCase(ArConstants.QUATERLY_BILLING_SCHEDULE_CODE) || billingFrequencyCode.equalsIgnoreCase(ArConstants.SEMI_ANNUALLY_BILLING_SCHEDULE_CODE) || billingFrequencyCode.equalsIgnoreCase(ArConstants.ANNUALLY_BILLING_SCHEDULE_CODE);
    }

    /**
     * Retrieves balances used to populate amounts for an invoice account detail
     * @param fiscalYear the fiscal year of the balances to find
     * @param chartOfAccountsCode the chart of accounts code of balances to find
     * @param accountNumber the account number of balances to find
     * @param balanceTypeCodeList the balance type codes of balances to find
     * @return a List of retrieved balances
     */
    protected List<Balance> retrieveBalances(Integer fiscalYear, String chartOfAccountsCode, String accountNumber, List<String> balanceTypeCodeList) {
        final SystemOptions systemOptions = optionsService.getOptions(fiscalYear);
        Map<String, Object> balanceKeys = new HashMap<String, Object>();
        balanceKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        balanceKeys.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        balanceKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        balanceKeys.put(KFSPropertyConstants.OBJECT_TYPE_CODE, retrieveExpenseObjectTypes());
        balanceKeys.put(KFSPropertyConstants.BALANCE_TYPE_CODE,balanceTypeCodeList);
        return (List<Balance>)getBusinessObjectService().findMatching(Balance.class, balanceKeys);
    }

    /**
     * Determines if Period 13 should be included in Period 01 calculations for invoice details and invoice account details
     * @return true if period 13 should be included, false otherwise
     */
    protected boolean includePeriod13InPeriod01Calculations() {
        return getParameterService().getParameterValueAsBoolean(ContractsGrantsInvoiceDocument.class, ArParameterKeyConstants.INCLUDE_PERIOD_13_IN_BUDGET_AND_CURRENT_IND_PARM_NM, Boolean.FALSE);
    }

    /**
     * This method helps in setting up basic values for Contracts & Grants Invoice Document
     */
    protected void populateContractsGrantsInvoiceDocument(ContractsAndGrantsBillingAward award, ContractsGrantsInvoiceDocument document, List<ContractsGrantsLetterOfCreditReviewDetail> locReviewDetails, String locCreationType) {
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
        if (!StringUtils.isBlank(locCreationType)) {
            document.getInvoiceGeneralDetail().setLetterOfCreditCreationType(locCreationType);
        }
        // To set up values for Letter of Credit Fund and Fund Group irrespective of the LOC Creation type.
        if (StringUtils.isNotEmpty(award.getLetterOfCreditFundCode())) {
            document.getInvoiceGeneralDetail().setLetterOfCreditFundCode(award.getLetterOfCreditFundCode());
        }
        if (ObjectUtils.isNotNull(award.getLetterOfCreditFund())) {
            if (StringUtils.isNotEmpty(award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode())) {
                document.getInvoiceGeneralDetail().setLetterOfCreditFundGroupCode(award.getLetterOfCreditFund().getLetterOfCreditFundGroupCode());
            }
        }

        KualiDecimal totalAmountBilledToDate = KualiDecimal.ZERO;
        if (document.getInvoiceMilestones().size() > 0) {
            totalAmountBilledToDate = calculateMilestoneAmount(document);
        }
        else if (document.getInvoiceBills().size() > 0) {
            totalAmountBilledToDate = calculateTotalBillAmount(document);
        }
        else {
            totalAmountBilledToDate = calculateTotalExpenditureAmount(document, locReviewDetails).add(getContractsGrantsInvoiceDocumentService().getOtherTotalBilledForAwardPeriod(document));
        }
        document.getInvoiceGeneralDetail().setTotalAmountBilledToDate(totalAmountBilledToDate);
    }

    protected KualiDecimal calculateMilestoneAmount(ContractsGrantsInvoiceDocument document) {
        KualiDecimal totalMilestoneAmount = KualiDecimal.ZERO;
        // To calculate the total milestone amount.
        if (document.getInvoiceMilestones().size() > 0) {
            for (InvoiceMilestone milestone : document.getInvoiceMilestones()) {
                if (milestone.getMilestoneAmount() != null) {
                    totalMilestoneAmount = totalMilestoneAmount.add(milestone.getMilestoneAmount());
                }
            }
        }
        totalMilestoneAmount = totalMilestoneAmount.add(document.getInvoiceGeneralDetail().getTotalPreviouslyBilled());
        return totalMilestoneAmount;
    }

    protected KualiDecimal calculateTotalBillAmount(ContractsGrantsInvoiceDocument document) {
        KualiDecimal totalBillAmount = KualiDecimal.ZERO;
        // To calculate the total bill amount.
        if (document.getInvoiceBills().size() > 0) {
            for (InvoiceBill bill : document.getInvoiceBills()) {
                if (bill.getEstimatedAmount() != null) {
                    totalBillAmount = totalBillAmount.add(bill.getEstimatedAmount());
                }
            }
        }
        totalBillAmount = totalBillAmount.add(document.getInvoiceGeneralDetail().getTotalPreviouslyBilled());
        return totalBillAmount;
    }

    protected KualiDecimal calculateTotalExpenditureAmount(ContractsGrantsInvoiceDocument document, List<ContractsGrantsLetterOfCreditReviewDetail> locReviewDetails) {
        Map<String, KualiDecimal> totalBilledByAccountNumberMap = new HashMap<String, KualiDecimal>();
        for (InvoiceDetailAccountObjectCode invoiceDetailAccountObjectCode : document.getInvoiceDetailAccountObjectCodes()) {
            String key = invoiceDetailAccountObjectCode.getChartOfAccountsCode()+"-"+invoiceDetailAccountObjectCode.getAccountNumber();
            KualiDecimal totalBilled = cleanAmount(totalBilledByAccountNumberMap.get(key));
            totalBilled = totalBilled.add(invoiceDetailAccountObjectCode.getTotalBilled());
            totalBilledByAccountNumberMap.put(key, totalBilled);
        }

        KualiDecimal totalExpendituredAmount = KualiDecimal.ZERO;
        for (InvoiceAccountDetail invAcctD : document.getAccountDetails()) {
            KualiDecimal currentExpenditureAmount = KualiDecimal.ZERO;
            if (!ObjectUtils.isNull(totalBilledByAccountNumberMap.get(invAcctD.getChartOfAccountsCode()+"-"+invAcctD.getAccountNumber()))) {
                invAcctD.setTotalPreviouslyBilled(totalBilledByAccountNumberMap.get(invAcctD.getChartOfAccountsCode()+"-"+invAcctD.getAccountNumber()));
            } else {
                invAcctD.setTotalPreviouslyBilled(KualiDecimal.ZERO);
            }

            currentExpenditureAmount = invAcctD.getCumulativeExpenditures().subtract(invAcctD.getTotalPreviouslyBilled());
            invAcctD.setInvoiceAmount(currentExpenditureAmount);
            // overwriting account detail expenditure amount if locReview Indicator is true - and award belongs to LOC Billing
            if (!ObjectUtils.isNull(document.getInvoiceGeneralDetail())) {
                ContractsAndGrantsBillingAward award = document.getInvoiceGeneralDetail().getAward();
                if (ObjectUtils.isNotNull(award) && StringUtils.equalsIgnoreCase(award.getBillingFrequencyCode(), ArConstants.LOC_BILLING_SCHEDULE_CODE) && !CollectionUtils.isEmpty(locReviewDetails)) {
                    for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                        final ContractsGrantsLetterOfCreditReviewDetail locReviewDetail = retrieveMatchingLetterOfCreditReviewDetail(awardAccount, locReviewDetails);
                        if (!ObjectUtils.isNull(locReviewDetail) && StringUtils.equals(awardAccount.getChartOfAccountsCode(), invAcctD.getChartOfAccountsCode()) && StringUtils.equals(awardAccount.getAccountNumber(), invAcctD.getAccountNumber())) {
                            currentExpenditureAmount = locReviewDetail.getAmountToDraw();
                            invAcctD.setInvoiceAmount(currentExpenditureAmount);
                        }
                    }
                }
            }
            totalExpendituredAmount = totalExpendituredAmount.add(currentExpenditureAmount);
        }
        totalExpendituredAmount = totalExpendituredAmount.add(document.getInvoiceGeneralDetail().getTotalPreviouslyBilled());
        return totalExpendituredAmount;
    }

    /**
     * This method would make sure the amounts of the current period are not included. So it calculates the cumulative and
     * subtracts the current period values. This would be done for Billing Frequencies - Monthly, Quarterly, Semi-Annual and Annual.
     *
     * @param glBalance
     * @return balanceAmount
     */
    protected KualiDecimal calculateBalanceAmountWithoutLastBilledPeriod(java.sql.Date lastBilledDate, Balance glBalance) {
        Timestamp ts = new Timestamp(new java.util.Date().getTime());
        java.sql.Date today = new java.sql.Date(ts.getTime());
        AccountingPeriod currPeriod = accountingPeriodService.getByDate(today);
        String currentPeriodCode = currPeriod.getUniversityFiscalPeriodCode();

        KualiDecimal currentBalanceAmount = KualiDecimal.ZERO;
        switch (currentPeriodCode) {
        case KFSConstants.MONTH13:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth12Amount()));
            // notice - no break!!!! we want to fall through to pick up all the prior months amounts
        case KFSConstants.MONTH12:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth11Amount()));
        case KFSConstants.MONTH11:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth10Amount()));
        case KFSConstants.MONTH10:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth9Amount()));
        case KFSConstants.MONTH9:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth8Amount()));
        case KFSConstants.MONTH8:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth7Amount()));
        case KFSConstants.MONTH7:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth6Amount()));
        case KFSConstants.MONTH6:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth5Amount()));
        case KFSConstants.MONTH5:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth4Amount()));
        case KFSConstants.MONTH4:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth3Amount()));
        case KFSConstants.MONTH3:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth2Amount()));
        case KFSConstants.MONTH2:
            currentBalanceAmount = currentBalanceAmount.add(cleanAmount(glBalance.getMonth1Amount()));
        }

        return glBalance.getContractsGrantsBeginningBalanceAmount().add(currentBalanceAmount);
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
            if (StringUtils.equalsIgnoreCase(award.getBillingFrequencyCode(), ArConstants.LOC_BILLING_SCHEDULE_CODE)) {
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
                if (award.getBillingFrequencyCode() != null && getContractsGrantsBillingAwardVerificationService().isValueOfBillingFrequencyValid(award)) {
                    if (verifyBillingFrequencyService.validateBillingFrequency(award)) {
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
        // 1. Award is excluded from invoicing
        if (award.isExcludedFromInvoicing()) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_EXCLUDED_FROM_INVOICING));
        }

        // 2. Award is Inactive
        if (!award.isActive()) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_INACTIVE_ERROR));
        }

        // 4. Award invoicing option is missing
        if (StringUtils.isEmpty(award.getInvoicingOptionCode())) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_INVOICING_OPTION_MISSING_ERROR));
        }

        // 5. Award billing frequency is not set correctly
        if (!getContractsGrantsBillingAwardVerificationService().isBillingFrequencySetCorrectly(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_SINGLE_ACCOUNT_ERROR));
        }

        // 6. Award has no accounts assigned
        if (CollectionUtils.isEmpty(award.getActiveAwardAccounts())) {
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
        if (!getContractsGrantsBillingAwardVerificationService().owningAgencyHasCustomerRecord(award)) {
            errorList.add(configurationService.getPropertyValueAsString(ArKeyConstants.CGINVOICE_CREATION_AWARD_AGENCY_NO_CUSTOMER_RECORD));
        }

        // 12. All accounts of an Award have zero$ to invoice
        if (!hasBillableAccounts(award)) {
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
            LOG.error("Could not write errors in Contracts & Grants Invoice Document creation process to file" + ioe.getMessage());
            throw new RuntimeException("Could not write errors in Contracts & Grants Invoice Document creation process to file", ioe);
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
                final SystemOptions systemOptions = optionsService.getCurrentYearOptions();

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

                        cumulativeExpenses = cumulativeExpenses.add(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount, systemOptions.getActualFinancialBalanceTypeCd(), beginningDate));
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
     * This method retrieves all the Contracts & Grants Invoice Documents with a status of Saved and
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
                    LOG.info("Routing Contracts & Grants Invoice document # " + cgInvoiceDocId + ".");
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
     * Returns a list of all saved but not yet routed Contracts & Grants Invoice Documents, using the KualiWorkflowInfo service.
     *
     * @return a list of Contracts & Grants Invoice Documents to route
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
                final SystemOptions systemOptions = optionsService.getCurrentYearOptions();

                // calculate cumulativeExpenses
                for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    cumulativeExpenses = cumulativeExpenses.add(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount, systemOptions.getActualFinancialBalanceTypeCd(), award.getAwardBeginningDate()));
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

    protected boolean hasBillableAccounts(ContractsAndGrantsBillingAward award) {
        String billingFrequencyCode = award.getBillingFrequencyCode();

        if (StringUtils.equalsIgnoreCase(billingFrequencyCode, ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) ||
                StringUtils.equalsIgnoreCase(billingFrequencyCode, ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            return !getContractsGrantsBillingAwardVerificationService().isInvoiceInProgress(award);
        } else {
            return CollectionUtils.isEmpty(award.getActiveAwardAccounts()) || !CollectionUtils.isEmpty(getValidAwardAccounts(award.getActiveAwardAccounts(), award));
        }
    }

    /**
     * This method returns the valid award accounts based on evaluation of billing frequency and invoice document status
     *
     * @param awardAccounts
     * @return valid awardAccounts
     */
    protected List<ContractsAndGrantsBillingAwardAccount> getValidAwardAccounts(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsAndGrantsBillingAward award) {
        if (!award.getBillingFrequencyCode().equalsIgnoreCase(ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !award.getBillingFrequencyCode().equalsIgnoreCase(ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            List<ContractsAndGrantsBillingAwardAccount> validAwardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
            Set<Account> invalidAccounts = harvestAccountsFromContractsGrantsInvoices(getInProgressInvoicesForAward(award));

            for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
                if (!invalidAccounts.contains(awardAccount.getAccount())) {
                    if (verifyBillingFrequencyService.validateBillingFrequency(award, awardAccount)) {
                        validAwardAccounts.add(awardAccount);
                    }
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
        fieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER, award.getProposalNumber());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_STATUS_CODE, financialSystemDocumentService.getPendingDocumentStatuses());

        return businessObjectService.findMatching(ContractsGrantsInvoiceDocument.class, fieldValues);
    }

    /**
     * Retrieve expense object types by the basic accounting category for expenses
     * @see org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService#retrieveExpenseObjectTypes()
     */
    @Override
    public Collection<String> retrieveExpenseObjectTypes() {
        List<String> objectTypeCodes = new ArrayList<>();

        Map<String, Object> fieldValues = new HashMap<>();
        fieldValues.put(KFSPropertyConstants.BASIC_ACCOUNTING_CATEGORY_CODE, KFSConstants.BasicAccountingCategoryCodes.EXPENSES);

        final Collection<ObjectType> objectTypes = getBusinessObjectService().findMatching(ObjectType.class, fieldValues);
        for (ObjectType objectType : objectTypes) {
            objectTypeCodes.add(objectType.getCode());
        }

        return objectTypeCodes;
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

    public ContractsGrantsBillingUtilityService getContractsGrantsBillingUtilityService() {
        return contractsGrantsBillingUtilityService;
    }

    public void setContractsGrantsBillingUtilityService(ContractsGrantsBillingUtilityService contractsGrantsBillingUtilityService) {
        this.contractsGrantsBillingUtilityService = contractsGrantsBillingUtilityService;
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

    public CostCategoryService getCostCategoryService() {
        return costCategoryService;
    }

    public void setCostCategoryService(CostCategoryService costCategoryService) {
        this.costCategoryService = costCategoryService;
    }

    public OptionsService getOptionsService() {
        return optionsService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

}
