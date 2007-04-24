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

import static org.kuali.PropertyConstants.DOCUMENT;
import static org.kuali.PropertyConstants.NEW_MAINTAINABLE_OBJECT;

import java.util.Collection;

import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
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
        success &= checkProposalAlreadyAwarded();
        success &= checkEndAfterBegin(newAwardCopy.getAwardBeginningDate(), newAwardCopy.getAwardEndingDate(), PropertyConstants.AWARD_ENDING_DATE);
        success &= checkPrimary(newAwardCopy.getAwardOrganizations(), AwardOrganization.class, PropertyConstants.AWARD_ORGRANIZATIONS, Award.class);
        success &= checkPrimary(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, PropertyConstants.AWARD_PROJECT_DIRECTORS, Award.class);
        success &= checkAccounts();
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, PropertyConstants.AWARD_PROJECT_DIRECTORS);
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardAccounts(), AwardAccount.class, PropertyConstants.AWARD_ACCOUNTS);
        return success;
    }

    private boolean checkAccounts() {
        boolean success = true;
        Collection<AwardAccount> awardAccounts = newAwardCopy.getAwardAccounts();

        if (ObjectUtils.isNull(awardAccounts) || awardAccounts.isEmpty()) {
            String elementLabel = SpringServiceLocator.getDataDictionaryService().getCollectionElementLabel(Award.class.getName(), PropertyConstants.AWARD_ACCOUNTS, AwardAccount.class);
            putFieldError(PropertyConstants.AWARD_ACCOUNTS, KeyConstants.ERROR_ONE_REQUIRED, elementLabel);
            success = false;
        }

        return success;
    }

    /**
     * checks to see if:
     * <ol>
     * <li> a proposal has already been awarded
     * </ol>
     * 
     * @return
     */
    private boolean checkProposalAlreadyAwarded() {
        boolean success = true;
        if (AwardRuleUtil.isProposalAwarded(newAwardCopy)) {
            putFieldError(PropertyConstants.PROPOSAL_NUMBER, KeyConstants.ERROR_AWARD_PROPOSAL_AWARDED, newAwardCopy.getProposalNumber().toString());
        }
        return success;
    }

    @Override
    public void setupConvenienceObjects() {
        // oldAward = (Award) super.getOldBo();
        newAwardCopy = (Award) super.getNewBo();
    }

}
