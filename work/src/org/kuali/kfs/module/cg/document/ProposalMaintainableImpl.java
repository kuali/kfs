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
package org.kuali.module.cg.maintenance;

import java.util.Collection;
import java.util.Map;

import static org.kuali.PropertyConstants.PROPOSAL_SUBCONTRACTORS;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.bo.ProposalSubcontractor;

/**
 * Methods for the Proposal maintenance document UI.
 */
public class ProposalMaintainableImpl extends KualiMaintainableImpl {

    public ProposalMaintainableImpl() {
        super();
    }

    public ProposalMaintainableImpl(Proposal proposal) {
        super(proposal);
        this.setBoClass(proposal.getClass());
    }

    /**
     * This method is called for refreshing the Agency before display to show the full name in case the agency number was changed by
     * hand before any submit that causes a redisplay.
     */
    @Override
    public void processAfterRetrieve() {
        refreshProposal();
        super.processAfterRetrieve();
    }

    /**
     * This method is called for refreshing the Agency before a save to display the full name in case the agency number was changed
     * by hand just before the save.
     */
    @Override
    public void prepareForSave() {
        refreshProposal();
        super.prepareForSave();
    }

    /**
     * This method is called for refreshing the Agency after a lookup to display its full name without AJAX.
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        ((ProposalSubcontractor) document.getNewMaintainableObject().getNewCollectionLine(PROPOSAL_SUBCONTRACTORS)).refreshNonUpdateableReferences();
        ((ProposalSubcontractor) document.getOldMaintainableObject().getNewCollectionLine(PROPOSAL_SUBCONTRACTORS)).refreshNonUpdateableReferences();
        refreshProposal();
        super.refresh(refreshCaller, fieldValues, document);
    }

    private void refreshProposal() {
        getProposal().refreshNonUpdateableReferences();
        // refresh subcontractors
        for (ProposalSubcontractor proposalSubcontractor : getProposal().getProposalSubcontractors()) {
            proposalSubcontractor.refreshNonUpdateableReferences();
        }

    }

    private Proposal getProposal() {
        return (Proposal) getBusinessObject();
    }

    /**
     * called for refreshing the subcontractor on proposalSubcontractor before adding to the proposalSubcontractors collection on
     * the proposal. this is to ensure that the summary fields are show correctly. i.e. subcontractor name
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        if (collectionName.equals(PROPOSAL_SUBCONTRACTORS)) {
            PersistableBusinessObject itemToBeAdded = newCollectionLines.get(collectionName);
            itemToBeAdded.refreshNonUpdateableReferences();
        }
        super.addNewLineToCollection(collectionName);

    }
}
