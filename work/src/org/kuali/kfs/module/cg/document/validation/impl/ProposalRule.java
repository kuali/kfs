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
import java.util.List;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.bo.BusinessObject;
import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.bo.ProposalOrganization;
import org.kuali.module.cg.bo.Primaryable;
import org.kuali.module.cg.bo.ProposalProjectDirector;
import org.kuali.PropertyConstants;
import org.kuali.KeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;

/**
 * Rules for the Proposal maintenance document.
 */
public class ProposalRule extends MaintenanceDocumentRuleBase {

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
        success &= checkPrimary(newProposalCopy.getProposalOrganizations(), ProposalOrganization.class, PropertyConstants.PROPOSAL_ORGANIZATIONS, Proposal.class);
        success &= checkPrimary(newProposalCopy.getProposalProjectDirectors(), ProposalProjectDirector.class, PropertyConstants.PROPOSAL_PROJECT_DIRECTORS, Proposal.class);
        return success;
    }

    private <E extends Primaryable> boolean checkPrimary(Collection<E> primaryables, Class<E> elementClass, String collectionName, Class<? extends BusinessObject> boClass) {
        boolean success = true;
        int count = 0;
        for (Primaryable p : primaryables) {
            if (p.isPrimary()) {
                count++;
            }
        }
        if (count != 1) {
            success = false;
            String elementLabel = SpringServiceLocator.getDataDictionaryService().getCollectionElementLabel(boClass.getName(), collectionName, elementClass);
            switch(count) {
                case 0:
                    putFieldError(collectionName, KeyConstants.ERROR_NO_PRIMARY, elementLabel);
                    break;
                default:
                    putFieldError(collectionName, KeyConstants.ERROR_MULTIPLE_PRIMARY, elementLabel);
            }
        }
        return success;
    }

    /**
     * Performs convenience cast for Maintenance framework.
     * Note that the MaintenanceDocumentRule events provide only a deep copy of the document (from KualiDocumentEventBase),
     * so these BOs are a copy too.  The framework does this to prevent these rules from changing any data.
     *
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRule#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        // oldProposalCopy = (Proposal) super.getOldBo();
        newProposalCopy = (Proposal) super.getNewBo();
    }

    // todo: change the super method to accept var args
    @Override
    protected void putFieldError(String propertyName, String errorConstant, String... parameters) {
        super.putFieldError(propertyName, errorConstant, parameters);
    }
}
