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
import java.util.List;
import java.util.Map;

import static org.kuali.PropertyConstants.PROPOSAL_PROJECT_DIRECTORS;
import static org.kuali.PropertyConstants.PROPOSAL_SUBCONTRACTORS;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.util.AssertionUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.bo.ProposalResearchRisk;
import org.kuali.module.cg.lookup.valuefinder.NextProposalNumberFinder;
import org.kuali.module.kra.routingform.bo.ResearchRiskType;

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
     * Use a new proposal number when creating a copy.
     */
    @Override
    public void processAfterCopy() {
        getProposal().setProposalNumber(NextProposalNumberFinder.getLongValue());
        super.processAfterCopy();
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
        refreshProposal();
        super.refresh(refreshCaller, fieldValues, document);
    }

    /**
     * This is a hook for initializing the BO from the maintenance framework.
     * @param generateDefaultValues true for initialization
     */
    @Override
    public void setGenerateDefaultValues(boolean generateDefaultValues) {
        if (generateDefaultValues) {
            initResearchRiskTypes();
        }
        super.setGenerateDefaultValues(generateDefaultValues);
    }
    
    private void initResearchRiskTypes() {
        List<ProposalResearchRisk> risks = getProposal().getProposalResearchRisks();
        AssertionUtils.assertThat(risks.isEmpty());
        // no requirement to exclude any risk types (except inactive ones, which the service excludes anyway)
        final String[] riskTypeCodesToExclude = new String[0];
        List<ResearchRiskType> researchRiskTypes = SpringServiceLocator.getRoutingFormResearchRiskService().getResearchRiskTypes(riskTypeCodesToExclude);
        for (ResearchRiskType type : researchRiskTypes) {
            ProposalResearchRisk ppr = new ProposalResearchRisk();
            ppr.setResearchRiskTypeCode(type.getResearchRiskTypeCode());
            ppr.setResearchRiskType(type);  // one less refresh
            risks.add(ppr);
        }
    }

    private void refreshProposal() {
        Proposal p = getProposal();
        p.refreshNonUpdateableReferences();

        getNewCollectionLine(PROPOSAL_SUBCONTRACTORS).refreshNonUpdateableReferences();
        getNewCollectionLine(PROPOSAL_PROJECT_DIRECTORS).refreshNonUpdateableReferences();
        
        // the org list doesn't need any refresh
        refreshNonUpdateableReferences(p.getProposalProjectDirectors());
        refreshNonUpdateableReferences(p.getProposalSubcontractors());
        refreshNonUpdateableReferences(p.getProposalResearchRisks());
    }
    
    // todo: move to ObjectUtils?
    private static void refreshNonUpdateableReferences(Collection<? extends PersistableBusinessObject> collection) {
        for (PersistableBusinessObject item : collection) {
            item.refreshNonUpdateableReferences();
        }
    }

    public Proposal getProposal() {
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
        refreshProposal();
        super.addNewLineToCollection(collectionName);
    }
}
