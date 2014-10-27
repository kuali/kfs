/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsLetterOfCreditReviewDocumentService;
import org.kuali.kfs.module.ar.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Contracts Grants LOC Review Document.
 */
public class ContractsGrantsLetterOfCreditReviewDocument extends FinancialSystemTransactionalDocumentBase {
    private static final Logger LOG = Logger.getLogger(ContractsGrantsLetterOfCreditReviewDocument.class);

    private String letterOfCreditFundCode;
    private ContractsAndGrantsLetterOfCreditFund letterOfCreditFund;
    private String letterOfCreditFundGroupCode;
    private ContractsAndGrantsLetterOfCreditFundGroup letterOfCreditFundGroup;
    private List<ContractsGrantsLetterOfCreditReviewDetail> headerReviewDetails;
    private List<ContractsGrantsLetterOfCreditReviewDetail> accountReviewDetails;

    private transient static volatile ContractsGrantsLetterOfCreditReviewDocumentService contractsGrantsLetterOfCreditReviewDocumentService;
    private transient static volatile OptionsService optionsService;

    public ContractsGrantsLetterOfCreditReviewDocument() {
        headerReviewDetails = new ArrayList<ContractsGrantsLetterOfCreditReviewDetail>();
        accountReviewDetails = new ArrayList<ContractsGrantsLetterOfCreditReviewDetail>();
    }

    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     *
     * @return Returns the letterOfCreditFundGroupCode.
     */
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }


    /**
     * Sets the letterOfCreditFundGroupCode attribute value.
     *
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }


    /**
     * Gets the letterOfCreditFundCode attribute.
     *
     * @return Returns the letterOfCreditFundCode.
     */
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    /**
     * Sets the letterOfCreditFundCode attribute value.
     *
     * @param letterOfCreditFundCode The letterOfCreditFundCode to set.
     */
    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }


    /**
     * Gets the letterOfCreditFund attribute.
     *
     * @return Returns the letterOfCreditFund.
     */
    public ContractsAndGrantsLetterOfCreditFund getLetterOfCreditFund() {
        return letterOfCreditFund = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsLetterOfCreditFund.class).retrieveExternalizableBusinessObjectIfNecessary(this, letterOfCreditFund, ArPropertyConstants.LETTER_OF_CREDIT_FUND);
    }

    /**
     * Sets the letterOfCreditFund attribute value.
     *
     * @param letterOfCreditFund The letterOfCreditFund to set.
     */
    public void setLetterOfCreditFund(ContractsAndGrantsLetterOfCreditFund letterOfCreditFund) {
        this.letterOfCreditFund = letterOfCreditFund;
    }

    /**
     * Gets the letterOfCreditFundGroup attribute.
     *
     * @return Returns the letterOfCreditFundGroup.
     */
    public ContractsAndGrantsLetterOfCreditFundGroup getLetterOfCreditFundGroup() {
        return letterOfCreditFundGroup = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsLetterOfCreditFundGroup.class).retrieveExternalizableBusinessObjectIfNecessary(this, letterOfCreditFundGroup, ArPropertyConstants.LETTER_OF_CREDIT_FUND_GROUP);
    }

    /**
     * Sets the letterOfCreditFundGroup attribute value.
     *
     * @param letterOfCreditFundGroup The letterOfCreditFundGroup to set.
     */
    public void setLetterOfCreditFundGroup(ContractsAndGrantsLetterOfCreditFundGroup letterOfCreditFundGroup) {
        this.letterOfCreditFundGroup = letterOfCreditFundGroup;
    }

    /**
     * Gets the CcaReviewDetails attribute.
     *
     * @return Returns the CcaReviewDetails.
     */
    public List<ContractsGrantsLetterOfCreditReviewDetail> getHeaderReviewDetails() {
        // To get the list of invoice Details for total cost
        List<ContractsGrantsLetterOfCreditReviewDetail> hdrDetails = new ArrayList<ContractsGrantsLetterOfCreditReviewDetail>();
        for (ContractsGrantsLetterOfCreditReviewDetail hdrD : headerReviewDetails) {
            if (ObjectUtils.isNull(hdrD.getAccountDescription())) {
                hdrDetails.add(hdrD);
            }
        }

        return hdrDetails;
    }

    /**
     * Sets the CcaReviewDetails attribute value.
     *
     * @param CcaReviewDetails The CcaReviewDetails to set.
     */
    public void setHeaderReviewDetails(List<ContractsGrantsLetterOfCreditReviewDetail> headerReviewDetails) {
        this.headerReviewDetails = headerReviewDetails;
    }

    /**
     * Gets the AccountReviewDetails attribute.
     *
     * @return Returns the AccountReviewDetails.
     */
    public List<ContractsGrantsLetterOfCreditReviewDetail> getAccountReviewDetails() {
        // To get the list of invoice Details for total cost
        List<ContractsGrantsLetterOfCreditReviewDetail> acctDetails = new ArrayList<ContractsGrantsLetterOfCreditReviewDetail>();
        for (ContractsGrantsLetterOfCreditReviewDetail acctD : accountReviewDetails) {
            if (ObjectUtils.isNotNull(acctD.getAccountDescription())) {
                if (acctD.getAccountDescription().equalsIgnoreCase(ArConstants.ACCOUNT) || acctD.getAccountDescription().equalsIgnoreCase(ArConstants.CONTRACT_CONTROL_ACCOUNT)) {
                    acctDetails.add(acctD);
                }
            }
        }

        return acctDetails;
    }

    /**
     * Sets the AccountReviewDetails attribute value.
     *
     * @param AccountReviewDetails The AccountReviewDetails to set.
     */
    public void setAccountReviewDetails(List<ContractsGrantsLetterOfCreditReviewDetail> accountReviewDetails) {
        this.accountReviewDetails = accountReviewDetails;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND_CODE, letterOfCreditFundCode);
        m.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND, letterOfCreditFund);
        m.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND_GROUP_CODE, letterOfCreditFundGroupCode);
        m.put(ArPropertyConstants.LETTER_OF_CREDIT_FUND_GROUP, letterOfCreditFundGroup);
        m.put(ArPropertyConstants.HEADER_REVIEW_DETAILS, headerReviewDetails);
        m.put(ArPropertyConstants.ACCOUNT_REVIEW_DETAILS, accountReviewDetails);
        return m;
    }

    /**
     * Clear out the initially populated fields.
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");

        // Clearing document Init fields
        setLetterOfCreditFundGroupCode(null);
        setLetterOfCreditFundCode(null);
    }

    /**
     * populate CGB LOC Review details based on the invoice info
     *
     * @param contractsGrantsInvoiceDocumentErrorLogs Collection used to hold any validation errors from the awards processed
     * @return true if process succeeded, and there is at least one valid award for the LOC doc, false otherwise
     */
    public boolean populateContractsGrantsLOCReviewDetails(Collection<ContractsGrantsInvoiceDocumentErrorLog> contractsGrantsInvoiceDocumentErrorLogs) {
        boolean valid = true;

        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        ContractsGrantsLetterOfCreditReviewDetail locReviewDtl;
        Map<String, Object> criteria = new HashMap<String, Object>();
        if (ObjectUtils.isNotNull(this.getLetterOfCreditFundGroupCode())) {
            criteria.put("letterOfCreditFund.letterOfCreditFundGroupCode", this.getLetterOfCreditFundGroupCode());
        }
        if (ObjectUtils.isNotNull(this.getLetterOfCreditFundCode())) {
            criteria.put("letterOfCreditFundCode", this.getLetterOfCreditFundCode());
        }
        // To exclude awards with milestones and predetermined schedule.
        criteria.put(ArPropertyConstants.BILLING_FREQUENCY_CODE, ArConstants.LOC_BILLING_SCHEDULE_CODE);

        List<ContractsAndGrantsBillingAward> awards = getContractsGrantsLetterOfCreditReviewDocumentService().getActiveAwardsByCriteria(criteria);

        if (CollectionUtils.isEmpty(awards)) {
            GlobalVariables.getMessageMap().putErrorForSectionId("Contracts Grants LOC Review Initiation", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_NO_AWARDS_RETRIEVED);
        }
        else {

            List<ContractsAndGrantsBillingAward> validAwards = new ArrayList<ContractsAndGrantsBillingAward>();

            validAwards = (List<ContractsAndGrantsBillingAward>) contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.LOC.getCode());

            if (CollectionUtils.isEmpty(validAwards)) {
                GlobalVariables.getMessageMap().putWarningForSectionId("Contracts Grants LOC Review Initiation", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_AWARDS_INVALID);
                valid = false;
            }
            else {
                for (ContractsAndGrantsBillingAward award : validAwards) {

                    // To set the amount to draw for the award accounts as a whole.
                    getContractsGrantsLetterOfCreditReviewDocumentService().setAwardAccountToDraw(award.getActiveAwardAccounts(), award);

                    KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
                    KualiDecimal amountAvailableToDraw = KualiDecimal.ZERO;
                    KualiDecimal totalClaimOnCashBalance = KualiDecimal.ZERO;
                    KualiDecimal totalAwardBudgetAmount = KualiDecimal.ZERO;
                    // Creating the header row here.
                    locReviewDtl = new ContractsGrantsLetterOfCreditReviewDetail();
                    locReviewDtl.setDocumentNumber(this.documentNumber);
                    locReviewDtl.setProposalNumber(award.getProposalNumber());
                    locReviewDtl.setAwardDocumentNumber(award.getAwardDocumentNumber());
                    locReviewDtl.setAgencyNumber(award.getAgencyNumber());
                    locReviewDtl.setCustomerNumber(award.getAgency().getCustomerNumber());
                    locReviewDtl.setAwardBeginningDate(award.getAwardBeginningDate());
                    locReviewDtl.setAwardEndingDate(award.getAwardEndingDate());
                    amountAvailableToDraw = getContractsGrantsLetterOfCreditReviewDocumentService().getAmountAvailableToDraw(award.getAwardTotalAmount(), award.getActiveAwardAccounts());
                    locReviewDtl.setAmountAvailableToDraw(amountAvailableToDraw);
                    if (ObjectUtils.isNotNull(award.getLetterOfCreditFund())) {
                        locReviewDtl.setLetterOfCreditAmount(award.getLetterOfCreditFund().getLetterOfCreditFundAmount());
                    }


                    headerReviewDetails.add(locReviewDtl);
                    final SystemOptions systemOption = getOptionsService().getCurrentYearOptions();

                    // Creating sub rows for the individual accounts.
                    for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                        locReviewDtl = new ContractsGrantsLetterOfCreditReviewDetail();
                        locReviewDtl.setDocumentNumber(this.documentNumber);
                        locReviewDtl.setProposalNumber(award.getProposalNumber());
                        locReviewDtl.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
                        locReviewDtl.setAccountNumber(awardAccount.getAccountNumber());
                        locReviewDtl.setAccountExpirationDate(awardAccount.getAccount().getAccountExpirationDate());
                        locReviewDtl.setClaimOnCashBalance(getContractsGrantsLetterOfCreditReviewDocumentService().getClaimOnCashforAwardAccount(awardAccount, award.getAwardBeginningDate()));
                        totalClaimOnCashBalance = totalClaimOnCashBalance.add(locReviewDtl.getClaimOnCashBalance());
                        locReviewDtl.setAwardBudgetAmount(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount, systemOption.getBudgetCheckingBalanceTypeCd(), award.getAwardBeginningDate()));
                        totalAwardBudgetAmount = totalAwardBudgetAmount.add(locReviewDtl.getAwardBudgetAmount());
                        if (ObjectUtils.isNotNull(awardAccount.getAccount().getContractControlAccountNumber()) && awardAccount.getAccountNumber().equalsIgnoreCase(awardAccount.getAccount().getContractControlAccountNumber())) {
                            locReviewDtl.setAccountDescription(ArConstants.CONTRACT_CONTROL_ACCOUNT);
                        }
                        else {
                            locReviewDtl.setAccountDescription(ArConstants.ACCOUNT);
                        }
                        locReviewDtl.setAmountToDraw(awardAccount.getAmountToDraw());
                        locReviewDtl.setHiddenAmountToDraw(awardAccount.getAmountToDraw());
                        totalAmountToDraw = totalAmountToDraw.add(locReviewDtl.getAmountToDraw());
                        accountReviewDetails.add(locReviewDtl);

                    }
                    // Amount to Draw for Header = Sum(Amount of Draw for individual accounts)
                    for (ContractsGrantsLetterOfCreditReviewDetail detail : getHeaderReviewDetails()) {// To identify the header row
                        if (ObjectUtils.isNotNull(detail.getAgencyNumber()) && ObjectUtils.isNull(detail.getAccountDescription()) && detail.getProposalNumber().equals(award.getProposalNumber())) {
                            detail.setAmountToDraw(totalAmountToDraw);
                            detail.setHiddenAmountToDraw(totalAmountToDraw);
                            detail.setClaimOnCashBalance(totalClaimOnCashBalance);
                            detail.setAwardBudgetAmount(totalAwardBudgetAmount);

                        }
                    }

                }
            }

        }

        return valid;
    }


    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#prepareForSave() To check if the amount to Draw
     *      field has been changed and to set the award locReviewIndicator to true.
     */
    @Override
    public void prepareForSave() {


        super.prepareForSave();

        // 1. compare the hiddenamountodraw and amount to draw field.
        for (ContractsGrantsLetterOfCreditReviewDetail detail : getAccountReviewDetails()) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.PROPOSAL_NUMBER, detail.getProposalNumber());
            ContractsAndGrantsBillingAward award = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);
            // to set funds Not Drawn as a difference between amountToDraw and hiddenAmountToDraw.


            // To set amount to Draw to 0 if there are blank values, to avoid exceptions.
            if (ObjectUtils.isNull(detail.getAmountToDraw()) ) {
                detail.setAmountToDraw(KualiDecimal.ZERO);
            }
            detail.setFundsNotDrawn(detail.getHiddenAmountToDraw().subtract(detail.getAmountToDraw()));

            if (detail.getFundsNotDrawn().isNegative()) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.FUNDS_NOT_DRAWN, ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_DOCUMENT_AMOUNT_TO_DRAW_INVALID);
                detail.setFundsNotDrawn(KualiDecimal.ZERO);
                detail.setAmountToDraw(detail.getHiddenAmountToDraw().subtract(detail.getFundsNotDrawn()));
            }

            if (detail.getHiddenAmountToDraw().compareTo(detail.getAmountToDraw()) != 0) {
                // This means the amount to Draw field has been changed,so

                // a. set locreview indicator to yes in award account
                // b.then set amounts to draw in award account
                for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    // set the amount to Draw in the award Account
                    Map<String, Object> criteria = new HashMap<String, Object>();
                    criteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
                    criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount.getProposalNumber());
                    if (detail.getAccountNumber().equals(awardAccount.getAccountNumber())) {
                        SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).setAmountToDrawToAwardAccount(criteria, detail.getAmountToDraw());
                        SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).setLOCReviewIndicatorToAwardAccount(criteria, true);
                    }
                }
            }
        }

        // To sum up amount to draw values.
        Set<Long> proposalNumberSet = new HashSet<Long>();
        for (ContractsGrantsLetterOfCreditReviewDetail detail : getHeaderReviewDetails()) {
            // Adding the awards to a set, to get unique values.
            proposalNumberSet.add(detail.getProposalNumber());

        }

        // 2. create invoices. - independent whether the amounts were changed or not.

        // To get the list of awards from the proposal Number set.

        Iterator<Long> iterator = proposalNumberSet.iterator();

        while (iterator.hasNext()) {
            KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
            Long proposalNumber = iterator.next();
            for (ContractsGrantsLetterOfCreditReviewDetail detail : getAccountReviewDetails()) {// To identify the header row
                if (ObjectUtils.isNotNull(detail.getAccountDescription()) && detail.getProposalNumber().equals(proposalNumber)) {
                    totalAmountToDraw = totalAmountToDraw.add(detail.getAmountToDraw());

                }
            }


            for (ContractsGrantsLetterOfCreditReviewDetail detail : getHeaderReviewDetails()) {// To identify the header row
                if (ObjectUtils.isNotNull(detail.getAgencyNumber()) && ObjectUtils.isNull(detail.getAccountDescription()) && detail.getProposalNumber().equals(proposalNumber)) {
                    detail.setAmountToDraw(totalAmountToDraw);

                }
            }
        }

    }


    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        List<ContractsAndGrantsBillingAward> awards = new ArrayList<ContractsAndGrantsBillingAward>();


        Set<Long> proposalNumberSet = new HashSet<Long>();

        super.doRouteStatusChange(statusChangeEvent);
        // performed only when document is in final state
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {

            // 1. compare the hiddenamountodraw and amount to draw field.
            for (ContractsGrantsLetterOfCreditReviewDetail detail : getHeaderReviewDetails()) {
                // Adding the awards to a set, to get unique values.
                proposalNumberSet.add(detail.getProposalNumber());

            }

            // 2. create invoices. - independent whether the amounts were changed or not.

            // To get the list of awards from the proposal Number set.

            Iterator<Long> iterator = proposalNumberSet.iterator();

            while (iterator.hasNext()) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put(KFSPropertyConstants.PROPOSAL_NUMBER, iterator.next());
                ContractsAndGrantsBillingAward awd = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsBillingAward.class).getExternalizableBusinessObject(ContractsAndGrantsBillingAward.class, map);
                awards.add(awd);
            }
            // To set the loc Creation Type to award based on the LOC Review document being retrieved.

            for (ContractsAndGrantsBillingAward award : awards) {
                if (ObjectUtils.isNotNull(this.getLetterOfCreditFundCode())) {

                    SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).setLOCCreationTypeToAward(award.getProposalNumber(), ArConstants.LOC_BY_LOC_FUND);
                }
                else {

                    SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).setLOCCreationTypeToAward(award.getProposalNumber(), ArConstants.LOC_BY_LOC_FUND_GRP);
                }

            }
            contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.LOC);

            //To route the invoices automatically as the initator would be system user after a wait time.
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            contractsGrantsInvoiceCreateDocumentService.routeContractsGrantsInvoiceDocuments();

            // The next important step is to set the locReviewIndicator to false and amount to Draw fields in award Account to zero.
            // This should not affect any further invoicing.
            for (ContractsAndGrantsBillingAward award : awards) {
                for (ContractsAndGrantsBillingAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    Map<String, Object> criteria = new HashMap<String, Object>();
                    criteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, awardAccount.getAccountNumber());
                    criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, awardAccount.getChartOfAccountsCode());
                    criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, awardAccount.getProposalNumber());
                    SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).setAmountToDrawToAwardAccount(criteria, KualiDecimal.ZERO);
                    SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).setLOCReviewIndicatorToAwardAccount(criteria, false);
                }
                SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).setLOCCreationTypeToAward(award.getProposalNumber(), null);
            }
        }
    }

    public static ContractsGrantsLetterOfCreditReviewDocumentService getContractsGrantsLetterOfCreditReviewDocumentService() {
        if (contractsGrantsLetterOfCreditReviewDocumentService == null) {
            contractsGrantsLetterOfCreditReviewDocumentService = SpringContext.getBean(ContractsGrantsLetterOfCreditReviewDocumentService.class);
        }
        return contractsGrantsLetterOfCreditReviewDocumentService;
    }

    public static OptionsService getOptionsService() {
        if (optionsService == null) {
            optionsService = SpringContext.getBean(OptionsService.class);
        }
        return optionsService;
    }
}
