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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;

import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
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
import org.kuali.kfs.module.cg.service.AwardService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Rules for the Award maintenance document.
 */
public class AwardRule extends CGMaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(AwardRule.class);
    protected boolean contractsGrantsBillingEnhancementsInd;
    protected Award newAwardCopy;


    /**
     * Default constructor.
     */
    public AwardRule() {
        super();
        contractsGrantsBillingEnhancementsInd = SpringContext.getBean(ContractsAndGrantsModuleRetrieveService.class).isContractsGrantsBillingEnhancementsActive();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("Entering AwardRule.processCustomSaveDocumentBusinessRules");
        processCustomRouteDocumentBusinessRules(document);
        LOG.info("Leaving AwardRule.processCustomSaveDocumentBusinessRules");
        return true; // save despite error messages
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("Entering AwardRule.processCustomRouteDocumentBusinessRules");
        boolean success = true;
        success &= checkProposal();
        success &= checkEndAfterBegin(newAwardCopy.getAwardBeginningDate(), newAwardCopy.getAwardEndingDate(), KFSPropertyConstants.AWARD_ENDING_DATE);
        success &= checkPrimary(newAwardCopy.getAwardOrganizations(), AwardOrganization.class, KFSPropertyConstants.AWARD_ORGRANIZATIONS, Award.class);
        success &= checkPrimary(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS, Award.class);
        if(contractsGrantsBillingEnhancementsInd){
        success &= checkPrimary(newAwardCopy.getAwardFundManagers(), AwardFundManager.class, KFSPropertyConstants.AWARD_FUND_MANAGERS, Award.class);
        }
        success &= checkAccounts();
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS);
        success &= checkFundManagersExist(newAwardCopy.getAwardFundManagers(), AwardFundManager.class, KFSPropertyConstants.AWARD_FUND_MANAGERS);
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardAccounts(), AwardAccount.class, KFSPropertyConstants.AWARD_ACCOUNTS);
        success &= checkProjectDirectorsStatuses(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS);
        success &= checkFederalPassThrough();
        success &= checkSuspendedAwardInvoicing();
        success &= checkAgencyNotEqualToFederalPassThroughAgency(newAwardCopy.getAgency(), newAwardCopy.getFederalPassThroughAgency(), KFSPropertyConstants.AGENCY_NUMBER, KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER);
        success &= checkInvoicingOptions();
        success &= checkAwardInvoiceAccounts();
        LOG.info("Leaving AwardRule.processCustomRouteDocumentBusinessRules");
        return success;
    }

    /**
     * Checks whether the award Invoicing is Suspended.
     *
     * @return
     */
    protected boolean checkSuspendedAwardInvoicing() {
        if (newAwardCopy.isSuspendInvoicingIndicator()) {
            if (ObjectUtils.isNotNull(newAwardCopy.getSuspensionReason())) {
                return true;
            }
            else {
                putFieldError(KFSPropertyConstants.SUSPENSION_REASON, KFSKeyConstants.ERROR_SUSPENSION_REASON_REQUIRED);
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
                    if (awardInvoiceAccount.getAccountType().equals(CGPropertyConstants.AR_ACCOUNT)) {
                        if (awardInvoiceAccount.isActive()) {
                            arCount++;

                            if (newAwardInvoiceAccount.getAccountType().equals(CGPropertyConstants.AR_ACCOUNT)) {
                                if (newAwardInvoiceAccount.isActive()) {
                                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MULTIPLE_INV_ACCT, CGPropertyConstants.AR_ACCOUNT);
                                    return false;
                                }
                            }
                        }
                    }
                    else if (awardInvoiceAccount.getAccountType().equals(CGPropertyConstants.INCOME_ACCOUNT)) {
                        if (awardInvoiceAccount.isActive()) {
                            incomeCount++;

                            if (newAwardInvoiceAccount.getAccountType().equals(CGPropertyConstants.INCOME_ACCOUNT)) {
                                if (newAwardInvoiceAccount.isActive()) {
                                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MULTIPLE_INV_ACCT, CGPropertyConstants.INCOME_ACCOUNT);
                                    return false;
                                }
                            }
                        }
                    }
                }

                if (arCount > 1) {
                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MULTIPLE_INV_ACCT, CGPropertyConstants.AR_ACCOUNT);
                    return false;
                }
                if (incomeCount > 1) {
                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MULTIPLE_INV_ACCT, CGPropertyConstants.INCOME_ACCOUNT);
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

    /**
     * check if the given award fund manager exists
     *
     * @param awardFundManager
     * @return
     */
    protected boolean checkAwardFundManager(AwardFundManager awardFundManager) {
        boolean success = true;

        Person fundManager = awardFundManager.getFundManager();
        if(contractsGrantsBillingEnhancementsInd){
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
    protected boolean checkInvoicingOptions() {
        boolean success = true;
        AwardService awardService = SpringContext.getBean(AwardService.class);
        List<String> errorString = awardService.hasValidContractControlAccounts(newAwardCopy);
        if (CollectionUtils.isNotEmpty(errorString)) {
            success = false;
            putFieldError(CGPropertyConstants.AWARD_INVOICING_OPTIONS, errorString.get(0), errorString.get(1));
        }
        return success;
    }


    /**
     * checks to see if at least and atmost ACTIVE 1 award invoice account of AR type exists when the GLPE parameter is set to 3.
     *
     * @return true if the award contains at least and atmost 1 ACTIVE {@link AwardInvoiceAccount}, false otherwise
     */
    protected boolean checkAwardInvoiceAccounts() {
        boolean success = true;
        Collection<AwardInvoiceAccount> awardInvoiceAccounts = newAwardCopy.getAwardInvoiceAccounts();
        // To get parameter Value of GLPE Recievable offset generation method.
        String receivableOffsetOption = SpringContext.getBean(AccountsReceivableModuleService.class).retrieveGLPEReceivableParameterValue();

        boolean isUsingReceivableFAU = receivableOffsetOption.equals("3");
        //This condition is validated only if GLPE is 3 and CG enhancements is ON
        if (isUsingReceivableFAU && contractsGrantsBillingEnhancementsInd) {
            if (!ObjectUtils.isNull(awardInvoiceAccounts) || !awardInvoiceAccounts.isEmpty()) {
                int arCount = 0;
                int incomeCount = 0;
                for (AwardInvoiceAccount awardInvoiceAccount : awardInvoiceAccounts) {
                    if (awardInvoiceAccount.getAccountType().equals(CGPropertyConstants.AR_ACCOUNT)) {
                        if (awardInvoiceAccount.isActive()) {
                            arCount++;
                        }
                    }
                    else if (awardInvoiceAccount.getAccountType().equals(CGPropertyConstants.INCOME_ACCOUNT)) {
                        if (awardInvoiceAccount.isActive()) {
                            incomeCount++;
                        }
                    }
                }

                if (arCount == 0) {

                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_ONE_AR_INV_ACCT_REQD);
                    success = false;
                }
                else if (arCount > 1) {
                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MULTIPLE_INV_ACCT, CGPropertyConstants.AR_ACCOUNT);
                    return false;
                }
                if (incomeCount > 1) {
                    putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_MULTIPLE_INV_ACCT, CGPropertyConstants.INCOME_ACCOUNT);
                    return false;
                }
            }
            else {

                putFieldError(CGPropertyConstants.AWARD_INVOICE_ACCOUNTS, CGKeyConstants.AwardConstants.ERROR_ONE_AR_INV_ACCT_REQD);
                success = false;
            }
        }
        return success;
    }

}
