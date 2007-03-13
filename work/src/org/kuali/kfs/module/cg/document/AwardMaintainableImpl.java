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

import static org.kuali.PropertyConstants.AWARD_ACCOUNTS;
import static org.kuali.PropertyConstants.AWARD_PROJECT_DIRECTORS;
import static org.kuali.PropertyConstants.AWARD_SUBCONTRACTORS;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.cg.bo.Award;
import org.kuali.module.cg.bo.AwardOrganization;
import org.kuali.module.cg.bo.AwardProjectDirector;
import org.kuali.module.cg.bo.AwardSubcontractor;
import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.bo.ProposalOrganization;
import org.kuali.module.cg.bo.ProposalProjectDirector;
import org.kuali.module.cg.bo.ProposalSubcontractor;

/**
 * Methods for the Award maintenance document UI.
 */
public class AwardMaintainableImpl extends KualiMaintainableImpl {
    public AwardMaintainableImpl() {
        super();
    }

    public AwardMaintainableImpl(Award award) {
        super(award);
        this.setBoClass(award.getClass());
    }


    /**
     * This method is called for refreshing the Agency before display to show the full name in case the agency number was changed by
     * hand before any submit that causes a redisplay.
     */
    @Override
    public void processAfterRetrieve() {
        refreshAward();
        super.processAfterRetrieve();
    }

    /**
     * This method is called for refreshing the Agency before a save to display the full name in case the agency number was changed
     * by hand just before the save.
     */
    @Override
    public void prepareForSave() {
        refreshAward();
        super.prepareForSave();
    }

    /**
     * This method is called for refreshing the Agency after a lookup to display its full name without AJAX.
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        refreshAward();
        super.refresh(refreshCaller, fieldValues, document);
        // copy over proposal values after refresh
        if (StringUtils.equals(PropertyConstants.PROPOSAL, (String) fieldValues.get(Constants.REFERENCES_TO_REFRESH))) {
            copyProposalToAward();
        }
    }

    /**
     * copies proposal fields to award
     */
    @SuppressWarnings("deprecation")
    private void copyProposalToAward() {
        Award award = getAward();
        Proposal proposal = award.getProposal();
        if (ObjectUtils.isNotNull(proposal)) {
            award.setAgencyNumber(proposal.getAgencyNumber());
            award.setAgency(proposal.getAgency());
            award.setAwardProjectTitle(proposal.getProposalProjectTitle());
            award.setAwardDirectCostAmount(proposal.getProposalDirectCostAmount());
            award.setAwardIndirectCostAmount(proposal.getProposalIndirectCostAmount());
            award.setProposalAwardTypeCode(proposal.getProposalAwardTypeCode());
            award.setProposalAwardType(proposal.getProposalAwardType());
            award.setFederalPassThroughIndicator(proposal.getProposalFederalPassThroughIndicator());
            award.setFederalPassThroughAgencyNumber(proposal.getFederalPassThroughAgencyNumber());
            award.setFederalPassThroughAgency(proposal.getFederalPassThroughAgency());
            award.setAwardPurposeCode(proposal.getProposalPurposeCode());
            award.setAwardPurpose(proposal.getProposalPurpose());

            // copy proposal organizations to award organizations
            for (ProposalOrganization pOrg : proposal.getProposalOrganizations()) {
                AwardOrganization awardOrg = new AwardOrganization();
                awardOrg.setProposalNumber(pOrg.getProposalNumber());
                awardOrg.setChartOfAccountsCode(pOrg.getChartOfAccountsCode());
                awardOrg.setChartOfAccounts(pOrg.getChartOfAccounts());
                awardOrg.setOrganizationCode(pOrg.getOrganizationCode());
                awardOrg.setOrganization(pOrg.getOrganization());
                awardOrg.setAwardPrimaryOrganizationIndicator(pOrg.isProposalPrimaryOrganizationIndicator());
                award.getAwardOrganizations().add(awardOrg);
            }

            // copy proposal subcontractors to award subcontractors
            for (ProposalSubcontractor pSubcontractor : proposal.getProposalSubcontractors()) {
                AwardSubcontractor awardSubcontractor = new AwardSubcontractor();
                awardSubcontractor.setProposalNumber(pSubcontractor.getProposalNumber());
                awardSubcontractor.setAwardSubcontractorNumber(pSubcontractor.getProposalSubcontractorNumber());
                awardSubcontractor.setSubcontractorAmount(pSubcontractor.getProposalSubcontractorAmount());
                awardSubcontractor.setAwardSubcontractorDescription(pSubcontractor.getProposalSubcontractorDescription());
                awardSubcontractor.setSubcontractor(pSubcontractor.getSubcontractor());
                award.getAwardSubcontractors().add(awardSubcontractor);
            }
            
            //copy proposal project directors to award propject directors
            for(ProposalProjectDirector pDirector:proposal.getProposalProjectDirectors()){
                AwardProjectDirector awardDirector= new AwardProjectDirector();
                awardDirector.setProposalNumber(pDirector.getProposalNumber());
                awardDirector.setAwardPrimaryProjectDirectorIndicator(pDirector.isProposalPrimaryProjectDirectorIndicator());
                awardDirector.setAwardProjectDirectorProjectTitle(pDirector.getProposalProjectDirectorProjectTitle());
                awardDirector.setPersonUniversalIdentifier(pDirector.getPersonUniversalIdentifier());
                award.getAwardProjectDirectors().add(awardDirector);
            }
        }
    }


    private void refreshAward() {
        Award award = getAward();
        award.refreshNonUpdateableReferences();

        getNewCollectionLine(AWARD_SUBCONTRACTORS).refreshNonUpdateableReferences();
        getNewCollectionLine(AWARD_PROJECT_DIRECTORS).refreshNonUpdateableReferences();
        getNewCollectionLine(AWARD_ACCOUNTS).refreshNonUpdateableReferences();

        // the org list doesn't need any refresh
        refreshNonUpdateableReferences(award.getAwardAccounts());
        refreshNonUpdateableReferences(award.getAwardSubcontractors());
        refreshNonUpdateableReferences(award.getAwardProjectDirectors());
    }

    // todo: move to ObjectUtils?
    private static void refreshNonUpdateableReferences(Collection<? extends PersistableBusinessObject> collection) {
        for (PersistableBusinessObject item : collection) {
            item.refreshNonUpdateableReferences();
        }
    }

    public Award getAward() {
        return (Award) getBusinessObject();
    }

    /**
     * called for refreshing the subcontractor on proposalSubcontractor before adding to the proposalSubcontractors collection on
     * the proposal. this is to ensure that the summary fields are show correctly. i.e. subcontractor name
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        refreshAward();
        super.addNewLineToCollection(collectionName);
    }
}
