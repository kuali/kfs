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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.businessobject.ProposalOrganization;
import org.kuali.kfs.module.cg.businessobject.ProposalProjectDirector;
import org.kuali.kfs.module.cg.businessobject.ProposalSubcontractor;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Rules for the Proposal maintenance document.
 */
public class ProposalRule extends CGMaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(ProposalRule.class);

    protected Proposal newProposalCopy;

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument documentCopy) {
        LOG.debug("Entering ProposalRule.processCustomSaveDocumentBusinessRules");
        processCustomRouteDocumentBusinessRules(documentCopy); // chain call but ignore success
        LOG.info("Leaving ProposalRule.processCustomSaveDocumentBusinessRules");
        return true; // save despite error messages
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument documentCopy) {
        LOG.debug("Entering ProposalRule.processCustomRouteDocumentBusinessRules");
        boolean success = true;
        success &= checkEndAfterBegin(newProposalCopy.getProposalBeginningDate(), newProposalCopy.getProposalEndingDate(), KFSPropertyConstants.PROPOSAL_ENDING_DATE);
        success &= checkPrimary(newProposalCopy.getProposalOrganizations(), ProposalOrganization.class, KFSPropertyConstants.PROPOSAL_ORGANIZATIONS, Proposal.class);
        success &= checkPrimary(newProposalCopy.getProposalProjectDirectors(), ProposalProjectDirector.class, KFSPropertyConstants.PROPOSAL_PROJECT_DIRECTORS, Proposal.class);
        success &= checkProjectDirectorsAreDirectors(newProposalCopy.getProposalProjectDirectors(), ProposalProjectDirector.class, KFSPropertyConstants.PROPOSAL_PROJECT_DIRECTORS);
        success &= checkProjectDirectorsExist(newProposalCopy.getProposalProjectDirectors(), ProposalProjectDirector.class, KFSPropertyConstants.PROPOSAL_PROJECT_DIRECTORS);
        success &= checkProjectDirectorsStatuses(newProposalCopy.getProposalProjectDirectors(), ProposalProjectDirector.class, KFSPropertyConstants.PROPOSAL_PROJECT_DIRECTORS);
        success &= checkFederalPassThrough(newProposalCopy.getProposalFederalPassThroughIndicator(), newProposalCopy.getAgency(), newProposalCopy.getFederalPassThroughAgencyNumber(), Proposal.class, KFSPropertyConstants.PROPOSAL_FEDERAL_PASS_THROUGH_INDICATOR);
        success &= checkAgencyNotEqualToFederalPassThroughAgency(newProposalCopy.getAgency(), newProposalCopy.getFederalPassThroughAgency(), KFSPropertyConstants.AGENCY_NUMBER, KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER);
        LOG.info("Leaving ProposalRule.processCustomRouteDocumentBusinessRules");
        return success;
    }

    /**
     * @return
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering ProposalRule.processCustomAddNewCollectionLineBusinessRules( " + collectionName + " )");
        }
        boolean success = true;
        success &= validateAddSubcontractor(line);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Leaving ProposalRule.processCustomAddNewCollectionLineBusinessRules( " + collectionName + " )");
        }
        return success;
    }

    /**
     * This method takes a look at the new line being added and applies appropriate validation checks if the line is a new line for
     * the {@link Subcontractor} collection. If the validation checks fail, an appropriate error message will be added to the global
     * error map and the method will return a value of false.
     * 
     * @param addLine New business object values being added.
     * @return True is the value being added passed all applicable validation rules.
     */
    protected boolean validateAddSubcontractor(PersistableBusinessObject addLine) {
        boolean success = true;
        if (addLine.getClass().isAssignableFrom(ProposalSubcontractor.class)) {
            ProposalSubcontractor subc = (ProposalSubcontractor) addLine;
            if (StringUtils.isBlank(subc.getSubcontractorNumber())) {
                String propertyName = KFSPropertyConstants.SUBCONTRACTOR_NUMBER;
                String errorKey = KFSKeyConstants.ERROR_PROPOSAL_SUBCONTRACTOR_NUMBER_REQUIRED_FOR_ADD;
                GlobalVariables.getMessageMap().putError(propertyName, errorKey);
                success = false;
            }
        }
        return success;
    }

    /**
     * Performs convenience cast for Maintenance framework. Note that the {@link MaintenanceDocumentRule} events provide only a deep
     * copy of the document (from KualiDocumentEventBase), so these BOs are a copy too. The framework does this to prevent these
     * rules from changing any data.
     * 
     * @see org.kuali.rice.kns.rules.MaintenanceDocumentRule#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        // oldProposalCopy = (Proposal) super.getOldBo();
        newProposalCopy = (Proposal) super.getNewBo();
    }
    
    
}
