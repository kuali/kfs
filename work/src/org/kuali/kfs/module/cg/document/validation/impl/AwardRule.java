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
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.module.cg.businessobject.AwardProjectDirector;
import org.kuali.kfs.module.cg.businessobject.AwardSubcontractor;
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

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering AwardRule.processCustomSaveDocumentBusinessRules");
        processCustomRouteDocumentBusinessRules(document);
        LOG.info("Leaving AwardRule.processCustomSaveDocumentBusinessRules");
        return true; // save despite error messages
    }

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
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardAccounts(), AwardAccount.class, KFSPropertyConstants.AWARD_ACCOUNTS);
        success &= checkProjectDirectorsStatuses(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS);
        success &= checkFederalPassThrough();
        success &= checkAgencyNotEqualToFederalPassThroughAgency(newAwardCopy.getAgency(), newAwardCopy.getFederalPassThroughAgency(), KFSPropertyConstants.AGENCY_NUMBER, KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER);
        LOG.info("Leaving AwardRule.processCustomRouteDocumentBusinessRules");
        return success;
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
        // oldAward = (Award) super.getOldBo();
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

    // check if the given award organization exists
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

    // check if the given award subcontrator exists
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

    // check if the given award account exists
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

    // check if the given award project director exists
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
}
