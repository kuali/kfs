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

import java.sql.Date;
import java.util.List;

import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.bo.ProposalOrganization;
import org.kuali.module.cg.bo.ProposalProjectDirector;
import org.kuali.module.cg.bo.ProjectDirector;
import org.apache.commons.lang.StringUtils;

/**
 * Rules for the Proposal maintenance document.
 */
public class ProposalRule extends CGMaintenanceDocumentRuleBase {

    // private Proposal oldProposalCopy;
    private Proposal newProposalCopy;

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument documentCopy) {
        processCustomRouteDocumentBusinessRules(documentCopy); // chain call but ignore success
        return true; // save despite error messages
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument documentCopy) {
        boolean success = true;
        success &= checkEndAfterBegin(newProposalCopy.getProposalBeginningDate(),newProposalCopy.getProposalEndingDate(),PropertyConstants.PROPOSAL_ENDING_DATE);
        success &= checkPrimary(newProposalCopy.getProposalOrganizations(), ProposalOrganization.class, PropertyConstants.PROPOSAL_ORGANIZATIONS, Proposal.class);
        success &= checkPrimary(newProposalCopy.getProposalProjectDirectors(), ProposalProjectDirector.class, PropertyConstants.PROPOSAL_PROJECT_DIRECTORS, Proposal.class);
        success &= checkProjectDirectorsExist(newProposalCopy.getProposalProjectDirectors());
        return success;
    }

    private boolean checkProjectDirectorsExist(List<ProposalProjectDirector> proposalProjectDirectors) {
        boolean success = true;
        final String personUserPropertyName = PropertyConstants.PROJECT_DIRECTOR + "." + PropertyConstants.PERSON_USER_IDENTIFIER;
        String label = SpringServiceLocator.getDataDictionaryService().getAttributeLabel(ProposalProjectDirector.class, personUserPropertyName);
        int i = 0;
        for (ProposalProjectDirector ppd : proposalProjectDirectors) {
            String propertyName = PropertyConstants.PROPOSAL_PROJECT_DIRECTORS + "[" + (i++) + "]." + personUserPropertyName;
            String id = ppd.getPersonUniversalIdentifier();
            if (StringUtils.isBlank(id) || !SpringServiceLocator.getProjectDirectorService().primaryIdExists(id)) {
                putFieldError(propertyName, KeyConstants.ERROR_EXISTENCE, label);
                success = false;
            }
        }
        return success;
    }

    /**
     * Performs convenience cast for Maintenance framework. Note that the MaintenanceDocumentRule events provide only a deep copy of
     * the document (from KualiDocumentEventBase), so these BOs are a copy too. The framework does this to prevent these rules from
     * changing any data.
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        // oldProposalCopy = (Proposal) super.getOldBo();
        newProposalCopy = (Proposal) super.getNewBo();
    }
}
