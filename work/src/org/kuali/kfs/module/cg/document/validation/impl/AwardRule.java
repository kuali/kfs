/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.cg.rules;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.cg.bo.Award;
import org.kuali.module.cg.bo.AwardAccount;
import org.kuali.module.cg.bo.AwardOrganization;
import org.kuali.module.cg.bo.AwardProjectDirector;

/**
 * Rules for the Award maintenance document.
 */
public class AwardRule extends CGMaintenanceDocumentRuleBase {

    // private Award oldAward;
    private Award newAwardCopy;

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        processCustomRouteDocumentBusinessRules(document);

        return true; // save despite error messages
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        success &= checkProposal();
        success &= checkEndAfterBegin(newAwardCopy.getAwardBeginningDate(), newAwardCopy.getAwardEndingDate(), KFSPropertyConstants.AWARD_ENDING_DATE);
        success &= checkPrimary(newAwardCopy.getAwardOrganizations(), AwardOrganization.class, KFSPropertyConstants.AWARD_ORGRANIZATIONS, Award.class);
        success &= checkPrimary(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS, Award.class);
        success &= checkAccounts();
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS);
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardAccounts(), AwardAccount.class, KFSPropertyConstants.AWARD_ACCOUNTS);
        success &= checkFederalPassThrough();
        return success;
    }

    /**
     * checks to see if at least 1 award account exists
     * 
     * @return true if the award contains at least 1 {@link AwardAccount}, false otherwise
     */
    private boolean checkAccounts() {
        boolean success = true;
        Collection<AwardAccount> awardAccounts = newAwardCopy.getAwardAccounts();

        if (ObjectUtils.isNull(awardAccounts) || awardAccounts.isEmpty()) {
            String elementLabel = SpringServiceLocator.getDataDictionaryService().getCollectionElementLabel(Award.class.getName(), KFSPropertyConstants.AWARD_ACCOUNTS, AwardAccount.class);
            putFieldError(KFSPropertyConstants.AWARD_ACCOUNTS, KFSKeyConstants.ERROR_ONE_REQUIRED, elementLabel);
            success = false;
        }

        return success;
    }

    /**
     * checks to see if:
     * <ol>
     * <li> a proposal has already been awarded
     * <li> a proposal is inactive
     * </ol>
     * 
     * @return
     */
    private boolean checkProposal() {
        boolean success = true;
        if (AwardRuleUtil.isProposalAwarded(newAwardCopy)) {
            putFieldError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_AWARD_PROPOSAL_AWARDED, newAwardCopy.getProposalNumber().toString());
            success = false;
        }
        else if (AwardRuleUtil.isProposalInactive(newAwardCopy)) {
            putFieldError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_AWARD_PROPOSAL_INACTIVE, newAwardCopy.getProposalNumber().toString());
        }

        return success;
    }

    /**
     * checks if the required federal pass through fields are filled in if the federal pass through indicator is yes
     * 
     * @return
     */
    private boolean checkFederalPassThrough() {
        boolean success = true;
        if (newAwardCopy.getFederalPassThroughIndicator()) {

            String indicatorLabel = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(Award.class, KFSPropertyConstants.FEDERAL_PASS_THROUGH_INDICATOR);
            if (null == newAwardCopy.getFederalPassThroughFundedAmount()) {
                String amountLabel = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(Award.class, KFSPropertyConstants.FEDERAL_PASS_THROUGH_FUNDED_AMOUNT);
                putFieldError(KFSPropertyConstants.FEDERAL_PASS_THROUGH_FUNDED_AMOUNT, KFSKeyConstants.ERROR_AWARD_FEDERAL_PASS_THROUGH_INDICATOR_DEPENDENCY_REQUIRED, new String[] { amountLabel, indicatorLabel });
                success = false;
            }
            if (StringUtils.isBlank(newAwardCopy.getFederalPassThroughAgencyNumber())) {
                String agencyLabel = SpringServiceLocator.getDataDictionaryService().getAttributeErrorLabel(Award.class, KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER);
                putFieldError(KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER, KFSKeyConstants.ERROR_AWARD_FEDERAL_PASS_THROUGH_INDICATOR_DEPENDENCY_REQUIRED, new String[] { agencyLabel, indicatorLabel });
                success = false;
            }
        }
        return success;
    }

    @Override
    public void setupConvenienceObjects() {
        // oldAward = (Award) super.getOldBo();
        newAwardCopy = (Award) super.getNewBo();
    }

}
