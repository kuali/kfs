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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
 * Contracts & Grants LOC Review Document.
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
            GlobalVariables.getMessageMap().putErrorForSectionId("Contracts & Grants LOC Review Initiation", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_NO_AWARDS_RETRIEVED);
        }
        else {

            List<ContractsAndGrantsBillingAward> validAwards = new ArrayList<ContractsAndGrantsBillingAward>();

            validAwards = (List<ContractsAndGrantsBillingAward>) contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, contractsGrantsInvoiceDocumentErrorLogs, null, ArConstants.ContractsAndGrantsInvoiceDocumentCreationProcessType.LOC.getCode());

            if (CollectionUtils.isEmpty(validAwards)) {
                GlobalVariables.getMessageMap().putWarningForSectionId("Contracts & Grants LOC Review Initiation", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_AWARDS_INVALID);
                valid = false;
            }
            else {
                for (ContractsAndGrantsBillingAward award : validAwards) {

                    // To set the amount to draw for the award accounts as a whole.
                    final Map<String, KualiDecimal> awardAccountAmountsToDraw = getContractsGrantsLetterOfCreditReviewDocumentService().calculateAwardAccountAmountsToDraw(award, award.getActiveAwardAccounts());

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
                        final String awardAccountKey = getContractsGrantsLetterOfCreditReviewDocumentService().getAwardAccountKey(awardAccount);
                        locReviewDtl = new ContractsGrantsLetterOfCreditReviewDetail();
                        locReviewDtl.setDocumentNumber(this.documentNumber);
                        locReviewDtl.setProposalNumber(award.getProposalNumber());
                        locReviewDtl.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
                        locReviewDtl.setAccountNumber(awardAccount.getAccountNumber());
                        locReviewDtl.setAccountExpirationDate(awardAccount.getAccount().getAccountExpirationDate());
                        locReviewDtl.setClaimOnCashBalance(awardAccountAmountsToDraw.get(awardAccountKey).negated());
                        totalClaimOnCashBalance = totalClaimOnCashBalance.add(locReviewDtl.getClaimOnCashBalance());
                        locReviewDtl.setAwardBudgetAmount(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount, systemOption.getBudgetCheckingBalanceTypeCd(), award.getAwardBeginningDate()));
                        totalAwardBudgetAmount = totalAwardBudgetAmount.add(locReviewDtl.getAwardBudgetAmount());
                        if (ObjectUtils.isNotNull(awardAccount.getAccount().getContractControlAccountNumber()) && awardAccount.getAccountNumber().equalsIgnoreCase(awardAccount.getAccount().getContractControlAccountNumber())) {
                            locReviewDtl.setAccountDescription(ArConstants.CONTRACT_CONTROL_ACCOUNT);
                        }
                        else {
                            locReviewDtl.setAccountDescription(ArConstants.ACCOUNT);
                        }
                        locReviewDtl.setAmountToDraw(awardAccountAmountsToDraw.get(awardAccountKey));
                        locReviewDtl.setHiddenAmountToDraw(awardAccountAmountsToDraw.get(awardAccountKey));
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
        }

        // To sum up amount to draw values.
        Set<Long> proposalNumberSet = new HashSet<Long>();
        for (ContractsGrantsLetterOfCreditReviewDetail detail : getHeaderReviewDetails()) {
            // Adding the awards to a set, to get unique values.
            proposalNumberSet.add(detail.getProposalNumber());

        }

        // 2. create invoices. - independent whether the amounts were changed or not.

        // To get the list of awards from the proposal Number set.
        for (Long proposalNumber : proposalNumberSet) {
            KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
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
        super.doRouteStatusChange(statusChangeEvent);
        // performed only when document is in processed state
        if (getDocumentHeader().getWorkflowDocument().isProcessed()) {
            getContractsGrantsLetterOfCreditReviewDocumentService().generateContractsGrantsInvoiceDocuments(this);
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
