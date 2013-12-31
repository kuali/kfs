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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.batch.service.VerifyBillingFrequencyService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
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

    private AccountingPeriodService accountingPeriodService;
    private VerifyBillingFrequencyService verifyBillingFrequencyService;
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;
    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private ConfigurationService configService;
    private List<String> errLines = new ArrayList<String>();
    private KualiModuleService kualiModuleService;
    private ContractsAndGrantsModuleRetrieveService contractsAndGrantsModuleRetrieveService;
    public static final String REPORT_LINE_DIVIDER = "--------------------------------------------------------------------------------------------------------------";

    /**
     * The default implementation of this service retrieves a collection of qualified Awards and create Contracts Grants Invoice
     * Documents by Awards.
     *
     * @see org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceDocumentCreateService#createCGInvoiceDocumentsByAwards(java.lang.String)
     */
    @Override
    public boolean createCGInvoiceDocumentsByAwards(Collection<ContractsAndGrantsBillingAward> awards, String errOutputFileName) {
        ContractsGrantsInvoiceDocument cgInvoiceDocument;
        List<ContractsAndGrantsBillingAwardAccount> tmpAcctList1 = null;
        String coaCode = null, orgCode = null;


        if (ObjectUtils.isNotNull(awards)) {
            // iterate through awards and create cgInvoice documents
            tmpAcctList1 = new ArrayList();

            for (ContractsAndGrantsBillingAward awd : awards) {
                String invOpt = awd.getInvoicingOptions();

                // case 1: create CGIN by accounts
                if (invOpt.equals(ArPropertyConstants.INV_ACCOUNT)) {

                    for (ContractsAndGrantsBillingAwardAccount awardAccount : awd.getActiveAwardAccounts()) {
                        if (!awardAccount.isFinalBilledIndicator()) {
                            tmpAcctList1.clear();
                            // only one account is added into the list to create cgin
                            tmpAcctList1.add(awardAccount);
                            // coaCode = awardAccount.getAccount().getChartOfAccountsCode();
                            // orgCode = awardAccount.getAccount().getOrganizationCode();
                            coaCode = awd.getPrimaryAwardOrganization().getChartOfAccountsCode();
                            orgCode = awd.getPrimaryAwardOrganization().getOrganizationCode();
                            // To get valid award accounts of amounts > zero$ and pass it to the create invoices method
                            if (!getValidAwardAccounts(tmpAcctList1, awd).containsAll(tmpAcctList1)) {
                                errLines.add("The account#" + awardAccount.getAccountNumber() + " of the Award/Proposal# " + awd.getProposalNumber().toString() + " is not billable. It could have an invoice in progress or zero balances.");
                            }

                            cgInvoiceDocument = createCGInvoiceDocumentByAwardInfo(awd, getValidAwardAccounts(tmpAcctList1, awd), coaCode, orgCode);
                            if (ObjectUtils.isNotNull(cgInvoiceDocument)) {
                                // Saving the document
                                try {
                                    documentService.saveDocument(cgInvoiceDocument);
                                }
                                catch (WorkflowException ex) {
                                    LOG.error("Error creating cgin documents: " + ex.getMessage(), ex);
                                    throw new RuntimeException("Error creating cgin documents: " + ex.getMessage(), ex);
                                }
                            }
                        }
                    }
                }

                // case 2: create CGINs by contractControlAccounts
                else if (invOpt.equals(ArPropertyConstants.INV_CONTRACT_CONTROL_ACCOUNT)) {

                    List<Account> controlAccounts = new ArrayList<Account>();
                    List<Account> controlAccountsTemp = new ArrayList<Account>();
                    controlAccounts = (List<Account>) contractsGrantsInvoiceDocumentService.getContractControlAccounts(awd);
                    controlAccountsTemp = (List<Account>) contractsGrantsInvoiceDocumentService.getContractControlAccounts(awd);

                    if (controlAccounts == null || (controlAccounts.size() != awd.getActiveAwardAccounts().size())) {// to check if
                                                                                                                     // the number
                                                                                                                     // of contract
                                                                                                                     // control
                                                                                                                     // accounts is
                                                                                                                     // same as the
                                                                                                                     // number of
                                                                                                                     // accounts
                        errLines.add("Award/Proposal#" + awd.getProposalNumber().toString() + " is Bill By Contract Control Account, but no control account is found for some/all award accounts.");
                    }
                    else {
                        HashSet<Account> controlAccountSet = new HashSet<Account>();
                        for (int i = 0; i < controlAccountsTemp.size(); i++) {
                            if (ObjectUtils.isNotNull(controlAccountsTemp.get(i))) {
                                for (int j = i + 1; j < controlAccounts.size(); j++) {
                                    if (areTheSameAccounts(controlAccountsTemp.get(i), controlAccounts.get(j))) {
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
                                tmpAcctList1.clear();

                                for (ContractsAndGrantsBillingAwardAccount awardAccount : awd.getActiveAwardAccounts()) {
                                    if (!awardAccount.isFinalBilledIndicator()) {
                                        tmpCtrlAcct = awardAccount.getAccount().getContractControlAccount();
                                        if (tmpCtrlAcct.getChartOfAccountsCode().equals(controlAccount.getChartOfAccountsCode()) && tmpCtrlAcct.getAccountNumber().equals(controlAccount.getAccountNumber())) {
                                            tmpAcctList1.add(awardAccount);
                                        }
                                    }
                                }

                                coaCode = awd.getPrimaryAwardOrganization().getChartOfAccountsCode();
                                orgCode = awd.getPrimaryAwardOrganization().getOrganizationCode();
                                // To get valid award accounts of amounts > zero$ and pass it to the create invoices method
                                if (!getValidAwardAccounts(tmpAcctList1, awd).containsAll(tmpAcctList1)) {
                                    errLines.add("One or more accounts under the Contract Control Account#" + controlAccount.getAccountNumber() + " of the Award/Proposal# " + awd.getProposalNumber().toString() + " are not billable. They could have invoices in progress or zero balances.");
                                }
                                cgInvoiceDocument = createCGInvoiceDocumentByAwardInfo(awd, getValidAwardAccounts(tmpAcctList1, awd), coaCode, orgCode);
                                if (ObjectUtils.isNotNull(cgInvoiceDocument)) {
                                    // Saving the document
                                    try {
                                        documentService.saveDocument(cgInvoiceDocument);
                                    }
                                    catch (WorkflowException ex) {
                                        LOG.error("Error creating cgin documents: " + ex.getMessage(), ex);
                                        throw new RuntimeException("Error creating cgin documents: " + ex.getMessage(), ex);
                                    }
                                }


                            }
                        }
                        else {
                            errLines.add("Award/Proposal# " + awd.getProposalNumber().toString() + " is Bill By Contract Control Account, but no control account is found for award accounts.");
                        }
                    }
                }

                // case 3: create CGIN by award
                else if (invOpt.equals(ArPropertyConstants.INV_AWARD)) {
                    // Check if awardaccounts has the same control account
                    int accountNum = awd.getActiveAwardAccounts().size();
                    Collection<Account> controlAccounts = contractsGrantsInvoiceDocumentService.getContractControlAccounts(awd);
                    if (controlAccounts == null || controlAccounts.size() < accountNum) {
                        errLines.add("Award/Proposal# " + awd.getProposalNumber().toString() + " is Bill By Contract Control Account, but no control account is found for some/all award accounts.");
                    }
                    else {
                        // check if control accounts of awardaccounts are the same
                        boolean isValid = true;
                        if (accountNum != 1) {
                            Account tmpAcct1, tmpAcct2;

                            Object[] awardAccounts = awd.getActiveAwardAccounts().toArray();
                            for (int i = 0; i < awardAccounts.length - 1; i++) {
                                tmpAcct1 = ((ContractsAndGrantsBillingAwardAccount) awardAccounts[i]).getAccount().getContractControlAccount();
                                tmpAcct2 = ((ContractsAndGrantsBillingAwardAccount) awardAccounts[i + 1]).getAccount().getContractControlAccount();

                                if (ObjectUtils.isNull(tmpAcct1) || ObjectUtils.isNull(tmpAcct2) || !areTheSameAccounts(tmpAcct1, tmpAcct2)) {
                                    errLines.add("Award/Proposal# " + awd.getProposalNumber().toString() + " is billed by award, but it has different control accounts for award accounts.");
                                    isValid = false;
                                    break;
                                }
                            }
                        }

                        if (isValid) {
                            Account account;
                            for (ContractsAndGrantsBillingAwardAccount awardAccount : awd.getActiveAwardAccounts()) {
                                account = awardAccount.getAccount();
                                coaCode = awd.getPrimaryAwardOrganization().getChartOfAccountsCode();
                                orgCode = awd.getPrimaryAwardOrganization().getOrganizationCode();
                            }
                            // To get valid award accounts of amounts > zero$ and pass it to the create invoices method
                            // To get valid award accounts of amounts > zero$ and pass it to the create invoices method
                            if (!getValidAwardAccounts(awd.getActiveAwardAccounts(), awd).containsAll(awd.getActiveAwardAccounts())) {
                                errLines.add("One or more accounts of the Award/Proposal# " + awd.getProposalNumber().toString() + " are not billable. They could have invoices in progress or zero balances.");
                            }
                            cgInvoiceDocument = createCGInvoiceDocumentByAwardInfo(awd, getValidAwardAccounts(awd.getActiveAwardAccounts(), awd), coaCode, orgCode);
                            if (ObjectUtils.isNotNull(cgInvoiceDocument)) {
                                // Saving the document
                                try {
                                    documentService.saveDocument(cgInvoiceDocument);
                                }
                                catch (WorkflowException ex) {
                                    LOG.error("Error creating cgin documents: " + ex.getMessage(), ex);
                                    throw new RuntimeException("Error creating cgin documents: " + ex.getMessage(), ex);
                                }
                            }
                        }
                    }
                }
            }
        }
        else {
            errLines.add("No Award is found to create CINV");
        }

        // print out the invalid awards which has not been used to create CINV edoc
        if (!CollectionUtils.isEmpty(errLines)) {
            File errOutPutfile = new File(errOutputFileName);
            PrintStream outputFileStream = null;

            try {
                outputFileStream = new PrintStream(errOutPutfile);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (String line : errLines) {
                try {
                    writeErrorEntry(line, outputFileStream);
                }
                catch (IOException ioe) {
                    LOG.error("ContractsGrantsInvoiceDocumentCreateServiceImpl.createCGInvoiceDocumentsByAwards Stopped: " + ioe.getMessage());
                    throw new RuntimeException("ContractsGrantsInvoiceDocumentCreateServiceImpl.createCGInvoiceDocumentsByAwards Stopped: " + ioe.getMessage(), ioe);
                }


            }
            errLines.clear();
            outputFileStream.close();
        }

        return true;

    }


    /**
     * This method retrieves create a ContractsGrantsInvoiceDocument by Award * @param awd
     *
     * @return ContractsGrantsInvoiceDocument
     */
    @Override
    public ContractsGrantsInvoiceDocument createCGInvoiceDocumentByAwardInfo(ContractsAndGrantsBillingAward awd, List<ContractsAndGrantsBillingAwardAccount> accounts, String chartOfAccountsCode, String organizationCode) {
        ContractsGrantsInvoiceDocument cgInvoiceDocument;
        if (ObjectUtils.isNotNull(accounts) && !accounts.isEmpty() && !CollectionUtils.isEmpty(accounts)) {
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
                    List<String> procCodes = contractsGrantsInvoiceDocumentService.getProcessingFromBillingCodes(chartOfAccountsCode, organizationCode);

                    AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
                    accountsReceivableDocumentHeader.setDocumentNumber(cgInvoiceDocument.getDocumentNumber());
                    accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(procCodes.get(0));
                    accountsReceivableDocumentHeader.setProcessingOrganizationCode(procCodes.get(1));
                    cgInvoiceDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);


                    cgInvoiceDocument.setAward(awd);
                    contractsGrantsInvoiceDocumentService.populateInvoiceFromAward(awd, accounts,cgInvoiceDocument);
                    contractsGrantsInvoiceDocumentService.createSourceAccountingLinesAndGLPEs(cgInvoiceDocument);
                    if (ObjectUtils.isNotNull(cgInvoiceDocument.getAward())) {
                        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(cgInvoiceDocument);
                    }

                    LOG.info("Created Contracts and Grants invoice document " + cgInvoiceDocument.getDocumentNumber());
                }
                catch (WorkflowException ex) {
                    LOG.error("Error creating cgin documents: " + ex.getMessage(), ex);
                    throw new RuntimeException("Error creating cgin documents: " + ex.getMessage(), ex);
                }

                return cgInvoiceDocument;

            }
            else {
                // if chart of account code or organization code is not available, output the error
                errLines.add("Award/Proposal# " + awd.getProposalNumber().toString() + " has not set correctly organizaion code or chart of account code ");
            }
        }


        return null;
    }


    /**
     * @see org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService#retrieveAwards()
     */
    @Override
    public Collection<ContractsAndGrantsBillingAward> retrieveAwards() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.ACTIVE, true);
        return kualiModuleService.getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObjectsList(ContractsAndGrantsBillingAward.class, map);

    }


    /**
     * This method validates awards and output an error file including unqualified awards with reason stated.
     *
     * @param errOutputFile The name of the file recording unqualified awards with reason stated.
     * @return True if
     */
    @Override
    public Collection<ContractsAndGrantsBillingAward> validateAwards(Collection<ContractsAndGrantsBillingAward> awards, String errOutputFile) {
        boolean isInvalid;
        Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup = new HashMap<ContractsAndGrantsBillingAward, List<String>>();
        List<String> errorList = new ArrayList<String>();
        List<ContractsAndGrantsBillingAward> qualifiedAwards = new ArrayList<ContractsAndGrantsBillingAward>();
        List<ContractsAndGrantsBillingAward> tmpAwards = new ArrayList<ContractsAndGrantsBillingAward>();


        // validation for billing frequency for the collection of awards
        Collection<AccountingPeriod> accPeriods = accountingPeriodService.getAllAccountingPeriods();

        for (ContractsAndGrantsBillingAward award : awards) {
            errorList = new ArrayList<String>();
            if (award.getAwardBeginningDate() != null) {
                if (award.getPreferredBillingFrequency() != null && contractsGrantsInvoiceDocumentService.isValueOfPreferredBillingFrequencyValid(award)) {
                    tmpAwards.add(award);
                }
                else {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_BILLING_FREQUENCY_MISSING_ERROR));
                    invalidGroup.put(award, errorList);
                }
            }
            else {
                // 1.Award start date is missing
                errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_AWARD_START_DATE_MISSING_ERROR));
                invalidGroup.put(award, errorList);
            }

        }


        // To check for every award if its within the billing frequency.
        Collection<ContractsAndGrantsBillingAward> awardsToBill = new ArrayList<ContractsAndGrantsBillingAward>();
        boolean isValid = true;
        for (ContractsAndGrantsBillingAward awd : tmpAwards) {
            errorList = new ArrayList<String>();
            isValid = verifyBillingFrequencyService.validatBillingFrequency(awd);
            if (isValid) {
                awardsToBill.add(awd);
            }
            else {
                errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_AWARD_INVALID_BILLING_PERIOD));
                invalidGroup.put(awd, errorList);
            }

        }


        // filter out invalid awards from the awards collection
        if (!CollectionUtils.isEmpty(awardsToBill)) {
            String proposalId;
            for (ContractsAndGrantsBillingAward award : awardsToBill) {
                isInvalid = false;
                errorList = new ArrayList<String>();
                // use business rules to validate awards


                // 1. Award Invoicing suspended by user.
                if (contractsGrantsInvoiceDocumentService.isAwardInvoicingSuspendedByUser(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_USER_SUSPENDED_ERROR));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }
                // 2. Award is Inactive
                if (!award.isActive()) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_AWARD_INACTIVE_ERROR));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }

                // 4. Award invoicing option is missing
                if (contractsGrantsInvoiceDocumentService.isAwardInvoicingOptionMissing(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_INVOICING_OPTION_MISSING_ERROR));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }

                // 5. Award preferred billing frequency is not set correctly
                if (!contractsGrantsInvoiceDocumentService.isPreferredBillingFrequencySetCorrectly(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_SINGLE_ACCOUNT_ERROR));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }

                // 6. Award has no accounts assigned
                if (contractsGrantsInvoiceDocumentService.hasNoActiveAccountsAssigned(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_NO_ACCOUNT_ASSIGNED_ERROR));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }

                // 7. Award contains expired account or accounts
                Collection<Account> expAccounts = contractsGrantsInvoiceDocumentService.getExpiredAccountsOfAward(award);
                if (ObjectUtils.isNotNull(expAccounts) && !expAccounts.isEmpty()) {

                    StringBuilder line = new StringBuilder();
                    line.append(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_CONAINS_EXPIRED_ACCOUNTS_ERROR));

                    for (Account expAccount : expAccounts) {
                        line.append(" (expired account: " + expAccount.getAccountNumber() + " expiration date " + expAccount.getAccountExpirationDate() + ") ");
                    }
                    errorList.add(line.toString());
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }
                // 8. Award has final invoice Billed already
                if (contractsGrantsInvoiceDocumentService.isAwardFinalInvoiceAlreadyBuilt(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_AWARD_FINAL_BILLED_ERROR));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }


                // 9. Award has no valid milestones to invoice
                if (contractsGrantsInvoiceDocumentService.hasNoMilestonesToInvoice(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_AWARD_NO_VALID_MILESTONES));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }
                // 10. All has no valid bills to invoice
                if (contractsGrantsInvoiceDocumentService.hasNoBillsToInvoice(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_AWARD_NO_VALID_BILLS));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }
                // 11. Agency has no matching Customer record
                if (contractsGrantsInvoiceDocumentService.owningAgencyHasNoCustomerRecord(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_AWARD_AGENCY_NO_CUSTOMER_RECORD));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }

                // 12. All accounts of an Award have zero$ to invoice
                if (!CollectionUtils.isEmpty(award.getActiveAwardAccounts()) && CollectionUtils.isEmpty(getValidAwardAccounts(award.getActiveAwardAccounts(), award))) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_AWARD_NO_VALID_ACCOUNTS));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }
                // 13. Award does not have appropriate Contract Control Accounts set based on Invoicing Options
                List<String> errorString = contractsAndGrantsModuleRetrieveService.hasValidContractControlAccounts(award.getProposalNumber());
                if (!CollectionUtils.isEmpty(errorString)) {
                    errorList.add(configService.getPropertyValueAsString(errorString.get(0)).replace("{0}", errorString.get(1)));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }
                // 14. System Information and ORganization Accounting Default not setup.
                if (contractsGrantsInvoiceDocumentService.isChartAndOrgNotSetupForInvoicing(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_SYS_INFO_OADF_NOT_SETUP));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }

                // 16. If all accounts of award has invoices in progress.
                if ((award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) || award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) && contractsGrantsInvoiceDocumentService.isInvoiceInProgress(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_AWARD_INVOICES_IN_PROGRESS));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }

                // 17. Offset Definition is not available when the GLPE is 3.
                if (contractsGrantsInvoiceDocumentService.isOffsetDefNotSetupForInvoicing(award)) {
                    errorList.add(configService.getPropertyValueAsString(ArConstants.BatchFileSystem.CGINVOICE_CREATION_AWARD_OFFSET_DEF_NOT_SETUP));
                    invalidGroup.put(award, errorList);

                    isInvalid = true;
                }


                // if invalid is true, the award is unqualified.
                // records the unqualified award with failed reasons.
                if (!isInvalid) {
                    qualifiedAwards.add(award);

                }


            }


        }
        // print out all failed reasons if they are present
        if (!CollectionUtils.isEmpty(invalidGroup)) {

            writeErrorToFile(invalidGroup, errOutputFile);


        }

        return qualifiedAwards;
    }

    protected void writeErrorToFile(Map<ContractsAndGrantsBillingAward, List<String>> invalidGroup, String errOutputFile) {
        PrintStream outputFileStream = null;
        File errOutPutfile = new File(errOutputFile);
        try {
            outputFileStream = new PrintStream(errOutPutfile);
            writeReportHeader(outputFileStream);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (ContractsAndGrantsBillingAward award : invalidGroup.keySet()) {
            try {
                writeErrorEntryByAward(award, invalidGroup.get(award), outputFileStream);
            }
            catch (IOException ioe) {
                LOG.error("ContractsGrantsInvoiceDocumentCreateServiceImpl.validateAwards Stopped: " + ioe.getMessage());
                throw new RuntimeException("ContractsGrantsInvoiceDocumentCreateServiceImpl.validateAwards Stopped: " + ioe.getMessage(), ioe);
            }
        }
        // clean the error list for next iteration
        invalidGroup.clear();

        try {
            writeNewLines("", outputFileStream);
        }
        catch (IOException ex) {
            LOG.error("ContractsGrantsInvoiceDocumentCreateServiceImpl.writeErrorToFile Stopped: " + ex.getMessage());
        }
        outputFileStream.close();
    }


    /**
     * This method validate if two accounts present the same account by comparing their "account number" and
     * "chart of account code",which are primary key.
     *
     * @param obj1
     * @param obj2
     * @return True if these two accounts are the same
     */
    protected boolean areTheSameAccounts(Account obj1, Account obj2) {
        boolean isEqual = false;

        if (obj1 != null && obj2 != null) {
            if (StringUtils.equals(obj1.getChartOfAccountsCode(), obj2.getChartOfAccountsCode())) {
                if (StringUtils.equals(obj1.getAccountNumber(), obj2.getAccountNumber())) {
                    isEqual = true;
                }
            }
        }

        return isEqual;
    }


    /**
     * This method retrieves all the contracts grants invoice documents with a status of 'I' and routes them to the next step in the
     * routing path.
     *
     * @return True if the routing was performed successfully. A runtime exception will be thrown if any errors occur while routing.
     * @see org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceDocumentCreateService#routeContractsGrantsInvoiceDocuments()
     */
    @Override
    public boolean routeContractsGrantsInvoiceDocuments() {
        List<String> documentIdList = null;
        try {
            documentIdList = retrieveContractsGrantsInvoiceDocumentsToRoute(KewApiConstants.ROUTE_HEADER_SAVED_CD);
        }
        catch (WorkflowException e1) {
            LOG.error("Error retrieving cgin documents for routing: " + e1.getMessage(), e1);
            throw new RuntimeException(e1.getMessage(), e1);
        }
        catch (RemoteException re) {
            LOG.error("Error retrieving cgin documents for routing: " + re.getMessage(), re);
            throw new RuntimeException(re.getMessage(), re);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("CGinvoice to Route: " + documentIdList);
        }

        for (String cgInvoiceDocId : documentIdList) {
            try {
                ContractsGrantsInvoiceDocument cgInvoicDoc = (ContractsGrantsInvoiceDocument) documentService.getByDocumentHeaderId(cgInvoiceDocId);
                // To route documents only if the user in the session is same as the initiator.
                if (cgInvoicDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId().equals(GlobalVariables.getUserSession().getPerson().getPrincipalId())) {

                    if (LOG.isInfoEnabled()) {
                        LOG.info("Routing CGIN document # " + cgInvoiceDocId + ".");
                    }
                    documentService.prepareWorkflowDocument(cgInvoicDoc);

                    // calling workflow service to bypass business rule checks
                    workflowDocumentService.route(cgInvoicDoc.getDocumentHeader().getWorkflowDocument(), "", null);
                }
            }
            catch (WorkflowException e) {
                LOG.error("Error routing document # " + cgInvoiceDocId + " " + e.getMessage());
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return true;
    }


    /**
     * Returns a list of all initiated but not yet routed contracts grants invoice documents, using the KualiWorkflowInfo service.
     *
     * @return a list of contracts grants invoice documents to route
     */
    protected List<String> retrieveContractsGrantsInvoiceDocumentsToRoute(String statusCode) throws WorkflowException, RemoteException {
        List<String> documentIds = new ArrayList<String>();

        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(KFSConstants.ContractsGrantsModuleDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);
        criteria.setDocumentStatuses(Collections.singletonList(DocumentStatus.fromCode(statusCode)));
        DocumentSearchCriteria crit = criteria.build();

        int maxResults = SpringContext.getBean(FinancialSystemDocumentService.class).getMaxResultCap(crit);
        int iterations = SpringContext.getBean(FinancialSystemDocumentService.class).getFetchMoreIterationLimit();

        for (int i = 0; i < iterations; i++) {
            LOG.debug("Fetch Iteration: " + i);
            criteria.setStartAtIndex(maxResults * i);
            crit = criteria.build();
            LOG.debug("Max Results: " + criteria.getStartAtIndex());
            DocumentSearchResults results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(GlobalVariables.getUserSession().getPrincipalId(), crit);
            if (results.getSearchResults().isEmpty()) {
                break;
            }
            for (DocumentSearchResult resultRow : results.getSearchResults()) {
                documentIds.add(resultRow.getDocument().getDocumentId());
                LOG.debug(resultRow.getDocument().getDocumentId());
            }
        }
        return documentIds;
    }

    /**
     * Retrieves the document id out of the route document header
     *
     * @param routeDocHeader the String representing an HTML link to the document
     * @return the document id
     */
    protected String parseDocumentIdFromRouteDocHeader(String routeDocHeader) {
        int rightBound = routeDocHeader.indexOf('>') + 1;
        int leftBound = routeDocHeader.indexOf('<', rightBound);
        return routeDocHeader.substring(rightBound, leftBound);
    }


    protected void writeErrorEntry(String line, PrintStream printStream) throws IOException {
        try {
            printStream.printf("%s\n", line);
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    protected void writeErrorEntryByAward(ContractsAndGrantsBillingAward award, List<String> validationCategory, PrintStream printStream) throws IOException {
        // %15s %18s %20s %19s %15s %18s %23s %18s
        boolean firstLineFlag = true;
        String awardBeginningDate;
        String awardEndingDate;
        String awardTotalAmount;

        String proposalNumber = award.getProposalNumber().toString();
        Date beginningDate = award.getAwardBeginningDate();
        Date endingDate = award.getAwardEndingDate();
        KualiDecimal totalAmount = award.getAwardTotalAmount();

        if (ObjectUtils.isNotNull(beginningDate)) {
            awardBeginningDate = award.getAwardBeginningDate().toString();
        } else {
            awardBeginningDate = "null award beginning date";
        }

        if (ObjectUtils.isNotNull(beginningDate)) {
            awardEndingDate = award.getAwardEndingDate().toString();
        } else {
            awardEndingDate = "null award ending date";
        }

        if (ObjectUtils.isNotNull(totalAmount)) {
            awardTotalAmount = award.getAwardTotalAmount().toString();
        } else {
            awardTotalAmount = "null award total amount";
        }

        KualiDecimal cumulativeExpenses = KualiDecimal.ZERO;
        // calculate cumulativeExpenses
        for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {

            cumulativeExpenses = cumulativeExpenses.add(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount, ArPropertyConstants.ACTUAL_BALANCE_TYPE, award.getAwardBeginningDate()));

        }

        try {
            if (award.getActiveAwardAccounts().size() > 0) {
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
            else {
                if (award.getActiveAwardAccounts().size() == 0) {
                    writeToReport(proposalNumber, "", awardBeginningDate, awardEndingDate, awardTotalAmount, cumulativeExpenses.toString(), printStream);
                }
                else {
                    writeToReport(proposalNumber, award.getActiveAwardAccounts().get(0).getAccountNumber(), awardBeginningDate, awardEndingDate, awardTotalAmount, cumulativeExpenses.toString(), printStream);
                }
            }
            // To print all the errors from the validation category.
            for (String vCat : validationCategory) {
                printStream.printf("%s", "     " + vCat);
                writeNewLines("", printStream);
            }
            printStream.printf(REPORT_LINE_DIVIDER);
            writeNewLines("", printStream);

        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    protected void writeToReport(String proposalNumber, String accountNumber, String awardBeginningDate, String awardEndingDate, String awardTotalAmount, String cumulativeExpenses, PrintStream printStream) throws IOException {

        try {
            printStream.printf("%15s", proposalNumber);
            printStream.printf("%18s", accountNumber);
            printStream.printf("%20s", awardBeginningDate);
            printStream.printf("%19s", awardEndingDate);
            printStream.printf("%15s", awardTotalAmount);
            printStream.printf("%23s", cumulativeExpenses);
            writeNewLines("", printStream);
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * @param newline
     * @param printStream
     * @throws IOException
     */
    protected void writeNewLines(String newline, PrintStream printStream) throws IOException {
        try {
            printStream.printf("%s\r\n", newline);
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * @param printStream
     * @throws IOException
     */
    protected void writeReportHeader(PrintStream printStream) throws IOException {

        try {
            printStream.printf("%15s%18s%20s%19s%15s%23s\r\n", "Proposal Number", "Account Number", "Award Start Date", "Award Stop Date", "Award Total", "Cumulative Expenses");
            printStream.printf("%23s", "Validation Category");
            writeNewLines("", printStream);
            printStream.printf(REPORT_LINE_DIVIDER);
            writeNewLines("", printStream);
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }


    /**
     * This method would calulate the amounts to be invoiced for every award account and returns the valid award acccounts with
     * amounts > zero dollars
     *
     * @param awardAccounts
     * @return valid awardAccounts
     */
    protected List<ContractsAndGrantsBillingAwardAccount> getValidAwardAccounts(List<ContractsAndGrantsBillingAwardAccount> awardAccounts, ContractsAndGrantsBillingAward award) {
        List<ContractsAndGrantsBillingAwardAccount> awdAcctsToCheck = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        List<ContractsAndGrantsBillingAwardAccount> validAwardAccounts = new ArrayList<ContractsAndGrantsBillingAwardAccount>();
        if (!award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !award.getPreferredBillingFrequency().equalsIgnoreCase(ArPropertyConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
            // To set the amount to Draw for each award account. Then test if the amount to Draw is zero dollars. if zero dollars.
            // then the account is not valid.
            // To make sure that the LOC Review indicator is false so the amounts can be updated.
            for (ContractsAndGrantsBillingAwardAccount awdAcct : awardAccounts) {
                if (!awdAcct.isLetterOfCreditReviewIndicator()) {
                    awdAcctsToCheck.add(awdAcct);
                }
            }
            contractsGrantsInvoiceDocumentService.setAwardAccountToDraw(awdAcctsToCheck, award);
            // 1. To check the amounts for all accounts, even if they are set from LOC Review Document.
            // 2. TO check if there are invoices in progress.
            for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
                    if (StringUtils.isBlank(awardAccount.getInvoiceDocumentStatus()) || awardAccount.getInvoiceDocumentStatus().equalsIgnoreCase("FINAL") || awardAccount.getInvoiceDocumentStatus().equalsIgnoreCase("CANCELED") || awardAccount.getInvoiceDocumentStatus().equalsIgnoreCase("DISAPPROVED")) {
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

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    @NonTransactional
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }


    public ConfigurationService getConfigService() {
        return configService;
    }


    public void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }


    public ContractsAndGrantsModuleRetrieveService getContractsAndGrantsModuleRetrieveService() {
        return contractsAndGrantsModuleRetrieveService;
    }


    public void setContractsAndGrantsModuleRetrieveService(ContractsAndGrantsModuleRetrieveService contractsAndGrantsModuleRetrieveService) {
        this.contractsAndGrantsModuleRetrieveService = contractsAndGrantsModuleRetrieveService;
    }


}
