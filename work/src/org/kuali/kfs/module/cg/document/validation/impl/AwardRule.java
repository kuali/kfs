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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.module.cg.businessobject.AwardInvoiceAccount;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.module.cg.businessobject.AwardProjectDirector;
import org.kuali.kfs.module.cg.businessobject.AwardSubcontractor;
import org.kuali.kfs.module.cg.businessobject.BillingFrequency;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Rules for the Award maintenance document.
 */
public class AwardRule extends CGMaintenanceDocumentRuleBase {

    protected static Logger LOG = org.apache.log4j.Logger.getLogger(AwardRule.class);
    protected Award newAwardCopy;
    protected Award oldAwardCopy;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering AwardRule.processCustomSaveDocumentBusinessRules");
        processCustomRouteDocumentBusinessRules(document);
        LOG.info("Leaving AwardRule.processCustomSaveDocumentBusinessRules");
        return true; // save despite error messages
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering AwardRule.processCustomRouteDocumentBusinessRules");
        boolean success = true;
        success &= checkProposal();
        success &= checkEndAfterBegin(newAwardCopy.getAwardBeginningDate(), newAwardCopy.getAwardEndingDate(), KFSPropertyConstants.AWARD_ENDING_DATE);
        success &= checkPrimary(newAwardCopy.getAwardOrganizations(), AwardOrganization.class, KFSPropertyConstants.AWARD_ORGRANIZATIONS, Award.class);
        success &= checkPrimary(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS, Award.class);
        success &= checkForDuplicateAccoutnts();
        success &= checkForDuplicateAwardProjectDirector();
        success &= checkForDuplicateAwardOrganization();
        success &= checkAccounts();
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS);
        success &= checkFundManagersExist(newAwardCopy.getAwardFundManagers(), KFSPropertyConstants.AWARD_FUND_MANAGERS);
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardAccounts(), AwardAccount.class, KFSPropertyConstants.AWARD_ACCOUNTS);
        success &= checkProjectDirectorsStatuses(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS);
        success &= checkFederalPassThrough();
        success &= checkExcludedFromInvoicing();
        success &= checkAgencyNotEqualToFederalPassThroughAgency(newAwardCopy.getAgency(), newAwardCopy.getFederalPassThroughAgency(), KFSPropertyConstants.AGENCY_NUMBER, KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER);
        success &= checkStopWorkReason();
        if(contractsGrantsBillingEnhancementActive){
            success &= checkPrimary(newAwardCopy.getAwardFundManagers(), AwardFundManager.class, KFSPropertyConstants.AWARD_FUND_MANAGERS, Award.class);
            success &= checkInvoicingOption();
            success &= checkNumberOfAccountsForBillingFrequency();
            success &= checkBillingFrequency();
        }
        LOG.info("Leaving AwardRule.processCustomRouteDocumentBusinessRules");
        return success;
    }

    /**
     * Checks whether the award is excluded from invoicing.
     *
     * @return
     */
    protected boolean checkExcludedFromInvoicing() {
        if (newAwardCopy.isExcludedFromInvoicing()) {
            if (ObjectUtils.isNotNull(newAwardCopy.getExcludedFromInvoicingReason())) {
                return true;
            }
            else {
                putFieldError(KFSPropertyConstants.EXCLUDED_FROM_INVOICING_REASON, KFSKeyConstants.ERROR_EXCLUDED_FROM_INVOICING_REASON_REQUIRED);
                return false;
            }
        }
        return true;
    }

    /**
     * checks to see if at least 1 award account exists
     *
     * @return true if the award contains at least 1 {@link AwardAccount}, false otherwise
     */
    protected boolean checkAccounts() {
        boolean success = true;
        Collection<AwardAccount> awardAccounts = newAwardCopy.getAwardAccounts();

        if (ObjectUtils.isNull(awardAccounts) || awardAccounts.isEmpty()) {
            String elementLabel = SpringContext.getBean(DataDictionaryService.class).getCollectionElementLabel(Award.class.getName(), KFSPropertyConstants.AWARD_ACCOUNTS, AwardAccount.class);
            putFieldError(KFSPropertyConstants.AWARD_ACCOUNTS, KFSKeyConstants.ERROR_ONE_REQUIRED, elementLabel);
            success = false;
        }

        return success;
    }

    /**
     * checks to see if:
     * <ol>
     * <li>a proposal has already been awarded
     * <li>a proposal is inactive
     * </ol>
     *
     * @return
     */
    protected boolean checkProposal() {
        boolean success = true;
        if (AwardRuleUtil.isProposalAwarded(newAwardCopy)) {
            putFieldError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_AWARD_PROPOSAL_AWARDED, newAwardCopy.getProposalNumber().toString());
            success = false;
        }
        // SEE KULCG-315 for details on why this code is commented out.
        // else if (AwardRuleUtil.isProposalInactive(newAwardCopy)) {
        // putFieldError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_AWARD_PROPOSAL_INACTIVE,
        // newAwardCopy.getProposalNumber().toString());
        // }

        return success;
    }

    /**
     * checks if the required federal pass through fields are filled in if the federal pass through indicator is yes
     *
     * @return
     */
    protected boolean checkFederalPassThrough() {
        boolean success = true;
        success = super.checkFederalPassThrough(newAwardCopy.getFederalPassThroughIndicator(), newAwardCopy.getAgency(), newAwardCopy.getFederalPassThroughAgencyNumber(), Award.class, KFSPropertyConstants.FEDERAL_PASS_THROUGH_INDICATOR);

        if (newAwardCopy.getFederalPassThroughIndicator()) {
            String indicatorLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(Award.class, KFSPropertyConstants.FEDERAL_PASS_THROUGH_INDICATOR);
            if (StringUtils.isBlank(newAwardCopy.getFederalPassThroughAgencyNumber())) {
                putFieldError(KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER, KFSKeyConstants.ERROR_FPT_AGENCY_NUMBER_REQUIRED);
                success = false;
            }
        }

        return success;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        newAwardCopy = (Award) super.getNewBo();
        oldAwardCopy = (Award) super.getOldBo();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;

        if (bo instanceof AwardProjectDirector) {
            AwardProjectDirector awardProjectDirector = (AwardProjectDirector) bo;
            success = this.checkAwardProjectDirector(awardProjectDirector);
        }
        else if (bo instanceof AwardFundManager) {
            AwardFundManager awardFundManager = (AwardFundManager) bo;
            success = this.checkAwardFundManager(awardFundManager);
        }
        else if (bo instanceof AwardAccount) {
            AwardAccount awardAccount = (AwardAccount) bo;
            success = this.checkAwardAccount(awardAccount);
        }
        else if (bo instanceof AwardSubcontractor) {
            AwardSubcontractor awardSubcontractor = (AwardSubcontractor) bo;
            success = this.checkAwardSubcontractor(awardSubcontractor);
        }
        else if (bo instanceof AwardOrganization) {
            AwardOrganization awardOrganization = (AwardOrganization) bo;
            success = this.checkAwardOrganization(awardOrganization);
        }

        return success;
    }


    /**
     * Overrides the method in MaintenanceDocumentRuleBase to give error message to the user when the user tries to add multiple
     * Primary Fund Managers. At most one Primary Fund Manager is allowed. contract.
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        if (line instanceof AwardFundManager) {
            AwardFundManager newAwardFundManager = (AwardFundManager) line;
            if (collectionName.equals(CGPropertyConstants.AWARD_FUND_MANAGERS)) {
                newAwardCopy = (Award) document.getNewMaintainableObject().getBusinessObject();
                List<AwardFundManager> awardFundManagers = newAwardCopy.getAwardFundManagers();

                // Check if there is already an Award Primary Fund Manager in the collection lines.
                int count = 0;
                for (AwardFundManager awardFundManager : awardFundManagers) {
                    if (awardFundManager.isPrimary()) {
                        count++;
                        if (newAwardFundManager.isPrimary()) {
                            String elementLabel = SpringContext.getBean(DataDictionaryService.class).getCollectionElementLabel(Award.class.getName(), collectionName, AwardFundManager.class);
                            putFieldError(collectionName, KFSKeyConstants.ERROR_MULTIPLE_PRIMARY, elementLabel);
                            return false;
                        }
                    }
                }

                if (count > 1) {
                    String elementLabel = SpringContext.getBean(DataDictionaryService.class).getCollectionElementLabel(Award.class.getName(), collectionName, AwardFundManager.class);
                    putFieldError(collectionName, KFSKeyConstants.ERROR_MULTIPLE_PRIMARY, elementLabel);
                    return false;
                }
            }
        }

        if (line instanceof AwardInvoiceAccount) {
            AwardInvoiceAccount newAwardInvoiceAccount = (AwardInvoiceAccount) line;
            if (collectionName.equals(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS)) {
                newAwardCopy = (Award) document.getNewMaintainableObject().getBusinessObject();
                List<AwardInvoiceAccount> awardInvoiceAccounts = newAwardCopy.getAwardInvoiceAccounts();

                // Check if there is already an Award Invoice Account of same type in the collection.
                int arCount = 0;
                int incomeCount = 0;
                for (AwardInvoiceAccount awardInvoiceAccount : awardInvoiceAccounts) {
                    if (awardInvoiceAccount.getAccountType().equals(CGConstants.AR_ACCOUNT)) {
                        if (awardInvoiceAccount.isActive()) {
                            arCount++;

                            if (newAwardInvoiceAccount.getAccountType().equals(CGConstants.AR_ACCOUNT)) {
                                if (newAwardInvoiceAccount.isActive()) {
                                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MULTIPLE_INV_ACCT, CGConstants.AR_ACCOUNT);
                                    return false;
                                }
                            }
                        }
                    }
                    else if (awardInvoiceAccount.getAccountType().equals(CGConstants.INCOME_ACCOUNT)) {
                        if (awardInvoiceAccount.isActive()) {
                            incomeCount++;

                            if (newAwardInvoiceAccount.getAccountType().equals(CGConstants.INCOME_ACCOUNT)) {
                                if (newAwardInvoiceAccount.isActive()) {
                                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MULTIPLE_INV_ACCT, CGConstants.INCOME_ACCOUNT);
                                    return false;
                                }
                            }
                        }
                    }
                }

                if (arCount > 1) {
                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MULTIPLE_INV_ACCT, CGConstants.AR_ACCOUNT);
                    return false;
                }
                if (incomeCount > 1) {
                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MULTIPLE_INV_ACCT, CGConstants.INCOME_ACCOUNT);
                    return false;
                }

            }
        }

        return super.processAddCollectionLineBusinessRules(document, collectionName, line);
    }

    /**
     * check if the given award organization exists
     *
     * @param awardOrganization
     * @return
     */
    protected boolean checkAwardOrganization(AwardOrganization awardOrganization) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.AWARD_ORGRANIZATIONS + ".";

        this.getDictionaryValidationService().validateBusinessObject(awardOrganization);
        if (StringUtils.isNotBlank(awardOrganization.getOrganizationCode()) && StringUtils.isNotBlank(awardOrganization.getChartOfAccountsCode())) {
            awardOrganization.refreshReferenceObject(KFSPropertyConstants.ORGANIZATION);

            if (ObjectUtils.isNull(awardOrganization.getOrganization())) {
                String label = this.getDataDictionaryService().getAttributeLabel(AwardOrganization.class, KFSPropertyConstants.ORGANIZATION_CODE);
                String message = label + "(" + awardOrganization.getOrganizationCode() + ")";

                putFieldError(errorPathPrefix + KFSPropertyConstants.ORGANIZATION_CODE, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;
    }

    /**
     * check if the given award subcontrator exists
     *
     * @param awardSubcontractor
     * @return
     */
    protected boolean checkAwardSubcontractor(AwardSubcontractor awardSubcontractor) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.AWARD_SUBCONTRACTORS + ".";

        this.getDictionaryValidationService().validateBusinessObject(awardSubcontractor);
        if (StringUtils.isNotBlank(awardSubcontractor.getSubcontractorNumber())) {
            awardSubcontractor.refreshReferenceObject("subcontractor");

            if (ObjectUtils.isNull(awardSubcontractor.getSubcontractor())) {
                String label = this.getDataDictionaryService().getAttributeLabel(AwardSubcontractor.class, KFSPropertyConstants.SUBCONTRACTOR_NUMBER);
                String message = label + "(" + awardSubcontractor.getSubcontractorNumber() + ")";

                putFieldError(errorPathPrefix + KFSPropertyConstants.SUBCONTRACTOR_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;
    }

    /**
     * check if the given award account exists
     *
     * @param awardAccount
     * @return
     */
    protected boolean checkAwardAccount(AwardAccount awardAccount) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getMessageMap().getErrorCount();
        String errorPathPrefix = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.AWARD_ACCOUNTS + ".";

        this.getDictionaryValidationService().validateBusinessObject(awardAccount);
        if (StringUtils.isNotBlank(awardAccount.getAccountNumber()) && StringUtils.isNotBlank(awardAccount.getChartOfAccountsCode())) {
            awardAccount.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);

            if (ObjectUtils.isNull(awardAccount.getAccount())) {
                String label = this.getDataDictionaryService().getAttributeLabel(AwardAccount.class, KFSPropertyConstants.ACCOUNT_NUMBER);
                String message = label + "(" + awardAccount.getChartOfAccountsCode() + "-" + awardAccount.getAccountNumber() + ")";

                putFieldError(errorPathPrefix + KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, message);
            }
        }

        Person projectDirector = awardAccount.getProjectDirector();
        if (StringUtils.isBlank(awardAccount.getPrincipalId()) || ObjectUtils.isNull(projectDirector)) {
            String label = this.getDataDictionaryService().getAttributeLabel(AwardAccount.class, "projectDirector.principalName");
            String message = label + "(" + awardAccount.getPrincipalId() + ")";

            putFieldError(errorPathPrefix + "projectDirector.principalName", KFSKeyConstants.ERROR_EXISTENCE, message);
        }

        success &= GlobalVariables.getMessageMap().getErrorCount() == originalErrorCount;

        return success;
    }

    /**
     * check if the given award project director exists
     *
     * @param awardProjectDirector
     * @return
     */
    protected boolean checkAwardProjectDirector(AwardProjectDirector awardProjectDirector) {
        boolean success = true;

        Person projectDirector = awardProjectDirector.getProjectDirector();
        if (StringUtils.isBlank(awardProjectDirector.getPrincipalId()) || ObjectUtils.isNull(projectDirector)) {
            String errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.AWARD_PROJECT_DIRECTORS + "." + "projectDirector.principalName";
            String label = this.getDataDictionaryService().getAttributeLabel(AwardProjectDirector.class, "projectDirector.principalName");
            String message = label + "(" + awardProjectDirector.getPrincipalId() + ")";

            putFieldError(errorPath, KFSKeyConstants.ERROR_EXISTENCE, message);

            success &= false;
        }
        return success;
    }

    protected boolean checkForDuplicateAccoutnts() {
        boolean success = true;
        String accountNumber;
        String accountChart;
        Collection<AwardAccount> awardAccounts = newAwardCopy.getAwardAccounts();
        HashSet<String> accountHash = new HashSet<String>();

        //validate if the newly entered award account is already on that award
        for(AwardAccount account: awardAccounts){
            if(account!=null && StringUtils.isNotEmpty(account.getAccountNumber())){
                 accountNumber = account.getAccountNumber();
                 accountChart  = account.getChartOfAccountsCode();
                 if (!accountHash.add(accountChart+accountNumber)){
                     putFieldError(KFSPropertyConstants.AWARD_ACCOUNTS, CGKeyConstants.ERROR_DUPLICATE_AWARD_ACCOUNT, accountChart + "-" + accountNumber);
                     return false;
                 }
             }
        }
        return success;
    }

    protected boolean checkForDuplicateAwardProjectDirector() {
        boolean success = true;
        String principalId;
        Collection<AwardProjectDirector> awardProjectDirectors = newAwardCopy.getAwardProjectDirectors();
        HashSet<String> principalIdHash = new HashSet<String>();

        //validate if the newly entered AwardProjectDirector is already on that award
        for(AwardProjectDirector projectDirector: awardProjectDirectors){
            if(projectDirector!=null && StringUtils.isNotEmpty(projectDirector.getPrincipalId())){
                principalId = projectDirector.getPrincipalId();
                if (!principalIdHash.add(principalId)){
                    putFieldError(KFSPropertyConstants.AWARD_PROJECT_DIRECTORS, CGKeyConstants.ERROR_DUPLICATE_AWARD_PROJECT_DIRECTOR, principalId);
                    return false;
                }
            }
        }
        return success;
    }

    protected boolean checkForDuplicateAwardOrganization() {
        boolean success = true;
        String organizationCode;
        String organizationChart;
        Collection<AwardOrganization> awardOrganizations = newAwardCopy.getAwardOrganizations();
        HashSet<String> orgaizationHash = new HashSet<String>();

        //validate if the newly entered awardOrganization is already on that award
        for(AwardOrganization awardOrganization: awardOrganizations){
            if(awardOrganization!=null && StringUtils.isNotEmpty(awardOrganization.getOrganizationCode())){
                organizationCode = awardOrganization.getOrganizationCode();
                organizationChart  = awardOrganization.getChartOfAccountsCode();
                if (!orgaizationHash.add(organizationChart+organizationCode)){
                    putFieldError(KFSPropertyConstants.AWARD_ORGRANIZATIONS, CGKeyConstants.ERROR_DUPLICATE_AWARD_ORGANIZATION, organizationChart + "-" + organizationCode);
                    return false;
                }
            }
        }
        return success;
    }

    /**
     * check if the given award fund manager exists
     *
     * @param awardFundManager
     * @return
     */
    protected boolean checkAwardFundManager(AwardFundManager awardFundManager) {
        boolean success = true;

        Person fundManager = awardFundManager.getFundManager();
        if(contractsGrantsBillingEnhancementActive){
            if (StringUtils.isBlank(awardFundManager.getPrincipalId()) || ObjectUtils.isNull(fundManager)) {
                String errorPath = KFSConstants.MAINTENANCE_ADD_PREFIX + KFSPropertyConstants.AWARD_FUND_MANAGERS + "." + "fundManager.principalName";
                String label = this.getDataDictionaryService().getAttributeLabel(AwardFundManager.class, "fundManager.principalName");
                String message = label + "(" + awardFundManager.getPrincipalId() + ")";

                putFieldError(errorPath, KFSKeyConstants.ERROR_EXISTENCE, message);

                success &= false;
            }
        }
        return success;
    }

    /**
     * This method checks the Contract Control account set for Award Account based on award's invoicing option.
     *
     * @return
     */
    protected boolean checkInvoicingOption() {
        boolean success = true;
        List<String> errorString = SpringContext.getBean(AccountsReceivableModuleBillingService.class).checkAwardContractControlAccounts(newAwardCopy);
        if (CollectionUtils.isNotEmpty(errorString) && errorString.size() > 1) {
            success = false;
            putFieldError(CGPropertyConstants.AWARD_INVOICING_OPTION_CODE, errorString.get(0), errorString.get(1));
        }
        return success;
    }

    /**
     * This method checks the number of active accounts set for the Award based on the billing frequency.
     * Awards with Predetermine or Milestone billing frequencies must have only one Award Account.
     *
     * @return true if the award has the correct number of accounts for the selected billing frequency
     */
    protected boolean checkNumberOfAccountsForBillingFrequency() {
        boolean success = true;
        int numberOfActiveAccounts = 0;

        // Determine billing frequency
        BillingFrequency billingFrequency = newAwardCopy.getBillingFrequency();
        if (ObjectUtils.isNotNull(billingFrequency)) {
            String billingFrequencyCode = billingFrequency.getFrequency();

            // Check for Predetermined and Milestone billing schedules
            if (ObjectUtils.isNotNull(billingFrequencyCode) &&
                    (CGConstants.MILESTONE_BILLING_SCHEDULE_CODE.equalsIgnoreCase(billingFrequencyCode) ||
                            CGConstants.PREDETERMINED_BILLING_SCHEDULE_CODE.equalsIgnoreCase(billingFrequencyCode))){

                // Get count of active accounts on Award
                Collection<AwardAccount> awardAccounts = newAwardCopy.getAwardAccounts();
                for (AwardAccount account : awardAccounts) {
                    if (account.isActive()) {
                        numberOfActiveAccounts++;
                    }

                    // if more than one account, add error and return out
                    if (numberOfActiveAccounts > 1) {
                        putFieldError(KFSPropertyConstants.AWARD_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MILESTONE_AND_PREDETERMINED_BILLING_FREQUENCY_MUST_HAVE_ONLY_ONE_AWARD_ACCOUNT);
                        return false;
                    }
                }
            }
        }

        return success;
    }

    /**
     * This method checks if the Stop Work Reason has been entered if the Stop Work flag has been checked.
     *
     * @return true if Stop Work flag hasn't been checked, or if it has been checked and the Stop Work Reason has been entered,
     * false otherwise
     */
    protected boolean checkStopWorkReason() {
        boolean success = true;
        if (newAwardCopy.isStopWorkIndicator()) {
            if (StringUtils.isBlank(newAwardCopy.getStopWorkReason())) {
                success = false;
                putFieldError(KFSPropertyConstants.STOP_WORK_REASON, KFSKeyConstants.ERROR_STOP_WORK_REASON_REQUIRED);
            }
        }
        return success;
    }

    /**
     * Checks if the user tries to change the billing frequency with active Milestones or Bills, and if so
     * returns an error.
     *
     * @return true if the billing frequency can be changed, false otherwise
     */
    protected boolean checkBillingFrequency() {
        boolean success = true;

        String newBillingFrequencyCode = newAwardCopy.getBillingFrequencyCode();
        String oldBillingFrequencyCode = oldAwardCopy.getBillingFrequencyCode();

        if (!StringUtils.equals(newBillingFrequencyCode, oldBillingFrequencyCode)) {
            if (StringUtils.equals(oldBillingFrequencyCode, CGConstants.MILESTONE_BILLING_SCHEDULE_CODE) &&
                    SpringContext.getBean(AccountsReceivableModuleBillingService.class).hasActiveMilestones(newAwardCopy.getProposalNumber())) {
                success = false;
                putFieldError(CGPropertyConstants.BILLING_FREQUENCY_CODE, CGKeyConstants.AwardConstants.ERROR_CG_ACTIVE_MILESTONES_EXIST, newAwardCopy.getBillingFrequency().getFrequencyDescription());
            } else if (StringUtils.equals(oldBillingFrequencyCode, CGConstants.PREDETERMINED_BILLING_SCHEDULE_CODE) &&
                    SpringContext.getBean(AccountsReceivableModuleBillingService.class).hasActiveBills(newAwardCopy.getProposalNumber())) {
                success = false;
                putFieldError(CGPropertyConstants.BILLING_FREQUENCY_CODE, CGKeyConstants.AwardConstants.ERROR_CG_ACTIVE_BILLS_EXIST, newAwardCopy.getBillingFrequency().getFrequencyDescription());
            }
        }

        return success;
    }

}
