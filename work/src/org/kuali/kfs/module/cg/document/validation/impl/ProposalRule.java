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
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.BusinessObject;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.bo.ProposalOrganization;
import org.kuali.PropertyConstants;
import org.kuali.KeyConstants;

/**
 * Rules for the Proposal maintenance document.
 */
public class ProposalRule extends MaintenanceDocumentRuleBase {

    // private Proposal oldProposal;
    private Proposal newProposal;

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        processCustomRouteDocumentBusinessRules(document);
        return true; // save despite error messages
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        boolean success = true;
        success &= checkOrgs();
        return success;
    }

    private boolean checkOrgs() {
        boolean success = true;
        boolean foundPrimary = false;
        int n = 0;
        for (ProposalOrganization po : newProposal.getProposalOrganizations()) {
            String propertyName = PropertyConstants.PROPOSAL_ORGANIZATIONS + "[" + (n++) + "]." + PropertyConstants.ORGANIZATION_CODE;
            if (po.isProposalPrimaryOrganizationIndicator()) {
                if (foundPrimary) {
                    putFieldError(PropertyConstants.PROPOSAL_ORGANIZATIONS, KeyConstants.ERROR_MULTIPLE_PRIMARY_ORGS);
                    success = false;
                }
                foundPrimary = true;
            }
        }
        if (!foundPrimary) {
            putFieldError(PropertyConstants.PROPOSAL_ORGANIZATIONS, KeyConstants.ERROR_NO_PRIMARY_ORG);
            success = false;
        }
        return success;
    }

    public void setupConvenienceObjects() {
        // oldProposal = (Proposal) super.getOldBo();
        newProposal = (Proposal) super.getNewBo();
    }

    // todo: change the super method to accept var args
    @Override
    protected void putFieldError(String propertyName, String errorConstant, String... parameters) {
        super.putFieldError(propertyName, errorConstant, parameters);
    }
}
