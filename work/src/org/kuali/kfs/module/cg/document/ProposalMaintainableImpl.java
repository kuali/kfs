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
package org.kuali.kfs.module.cg.document;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.businessobject.ProposalProjectDirector;
import org.kuali.kfs.module.cg.businessobject.ProposalResearchRisk;
import org.kuali.kfs.module.cg.businessobject.ProposalSubcontractor;
import org.kuali.kfs.module.cg.businessobject.ResearchRiskType;
import org.kuali.kfs.module.cg.businessobject.defaultvalue.NextProposalNumberFinder;
import org.kuali.kfs.module.cg.document.service.RoutingFormResearchRiskService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Methods for the Proposal maintenance document UI.
 */
public class ProposalMaintainableImpl extends FinancialSystemMaintainable {
    public ProposalMaintainableImpl() {
        super();
    }

    /**
     * Constructs a new ProposalMaintainableImpl from an existing {@link Proposal}.
     *
     * @param proposal
     */
    public ProposalMaintainableImpl(Proposal proposal) {
        super(proposal);
        this.setBoClass(proposal.getClass());
    }

    /**
     * Use a new proposal number when creating a copy.
     */
    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        getProposal().setProposalNumber(NextProposalNumberFinder.getLongValue());
        getProposal().setProposalClosingDate(null);
        super.processAfterCopy(document, parameters);
    }

    /**
     * This method is called for refreshing the {@link Agency} before display to show the full name in case the agency number was
     * changed by hand before any submit that causes a redisplay.
     */
    @Override
    public void processAfterRetrieve() {
        refreshProposal(false);
        super.processAfterRetrieve();
    }

    /**
     * <p>
     * This method is called for refreshing the {@link Agency} before a save to display the full name in case the agency number was
     * changed by hand just before the save. Also, if there is only one {@link ProjectDirector}, then this method defaults it to be
     * primary. This method can change data, unlike the rules. It is run before the rules.<p/> This default primary is limited to
     * save actions (including route, etc) so that when the user adds multiple {@link ProjectDirectors} the first one added doesn't
     * default to primary (so the user must choose).
     */
    @Override
    public void prepareForSave() {
        refreshProposal(false);
        List<ProposalProjectDirector> directors = getProposal().getProposalProjectDirectors();
        if (directors.size() == 1) {
            directors.get(0).setProposalPrimaryProjectDirectorIndicator(true);
        }
        List<ProposalSubcontractor> proposalSubcontractors = getProposal().getProposalSubcontractors();
        if (proposalSubcontractors != null && !proposalSubcontractors.isEmpty()) {
            int i = 0;
            for (ProposalSubcontractor proposalSubcontractor : proposalSubcontractors) {
                i++;
                if (StringUtils.isBlank(proposalSubcontractor.getProposalSubcontractorNumber())) {
                    proposalSubcontractor.setProposalSubcontractorNumber("" + i);
                }
            }
        }
        super.prepareForSave();
    }

    /**
     * This method is called for refreshing the {@link Agency} and other related BOs after a lookup, to display their full name &
     * etc without AJAX.
     *
     * @param refreshCaller
     * @param fieldValues
     * @param document
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        refreshProposal(KFSConstants.KUALI_LOOKUPABLE_IMPL.equals(fieldValues.get(KFSConstants.REFRESH_CALLER)));
        super.refresh(refreshCaller, fieldValues, document);
    }

    /**
     * This is a hook for initializing the BO from the maintenance framework. It initializes the {@link ResearchRiskType}s
     * collection.
     *
     * @param generateDefaultValues true for initialization
     */
    @Override
    public void setGenerateDefaultValues(String docTypeName) {
        // if (generateDefaultValues) {
        initResearchRiskTypes();
        // }
        super.setGenerateDefaultValues(docTypeName);
    }

    /**
     *
     */
    private void initResearchRiskTypes() {
        List<ProposalResearchRisk> risks = getProposal().getProposalResearchRisks();
        // no requirement to exclude any risk types (except inactive ones, which the service excludes anyway)
        final String[] riskTypeCodesToExclude = new String[0];
        List<ResearchRiskType> researchRiskTypes = SpringContext.getBean(RoutingFormResearchRiskService.class).getResearchRiskTypes(riskTypeCodesToExclude);
        for (ResearchRiskType type : researchRiskTypes) {
            ProposalResearchRisk ppr = new ProposalResearchRisk();
            ppr.setResearchRiskTypeCode(type.getResearchRiskTypeCode());
            ppr.setResearchRiskType(type); // one less refresh
            risks.add(ppr);
        }
    }

    /**
     * @param refreshFromLookup
     */
    private void refreshProposal(boolean refreshFromLookup) {
        getProposal().refreshNonUpdateableReferences();

        getNewCollectionLine(KFSPropertyConstants.PROPOSAL_SUBCONTRACTORS).refreshNonUpdateableReferences();

        refreshNonUpdateableReferences(getProposal().getProposalOrganizations());
        refreshNonUpdateableReferences(getProposal().getProposalSubcontractors());
        refreshNonUpdateableReferences(getProposal().getProposalResearchRisks());

        refreshProposalProjectDirectors(refreshFromLookup);
    }

    /**
     * Refreshes this maintainable's ProposalProjectDirectors.
     *
     * @param refreshFromLookup a lookup returns only the primary key, so ignore the secondary key when true
     */
    private void refreshProposalProjectDirectors(boolean refreshFromLookup) {
        if (refreshFromLookup) {
            getNewCollectionLine(KFSPropertyConstants.PROPOSAL_PROJECT_DIRECTORS).refreshNonUpdateableReferences();
            refreshNonUpdateableReferences(getProposal().getProposalProjectDirectors());
        }
        else {
            refreshWithSecondaryKey((ProposalProjectDirector) getNewCollectionLine(KFSPropertyConstants.PROPOSAL_PROJECT_DIRECTORS));
            for (ProposalProjectDirector ppd : getProposal().getProposalProjectDirectors()) {
                refreshWithSecondaryKey(ppd);
            }
        }
    }

    /**
     * @param collection
     */
    private static void refreshNonUpdateableReferences(Collection<? extends PersistableBusinessObject> collection) {
        for (PersistableBusinessObject item : collection) {
            item.refreshNonUpdateableReferences();
        }
    }

    /**
     * Refreshes the reference to ProjectDirector, giving priority to its secondary key. Any secondary key that it has may be user
     * input, so that overrides the primary key, setting the primary key. If its primary key is blank or nonexistent, then leave the
     * current reference as it is, because it may be a nonexistent instance which is holding the secondary key (the username, i.e.,
     * principalName) so we can redisplay it to the user for correction. If it only has a primary key then use that, because it may
     * be coming from the database, without any user input.
     *
     * @param ppd the ProposalProjectDirector to refresh
     */
    private static void refreshWithSecondaryKey(ProposalProjectDirector ppd) {
        String secondaryKey = null;
        if (ObjectUtils.isNotNull(ppd.getProjectDirector())) {
            secondaryKey = ppd.getProjectDirector().getPrincipalName();
        }
        if (StringUtils.isNotBlank(secondaryKey)) {
            Principal dir = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(secondaryKey);
            ppd.setPrincipalId(dir == null ? null : dir.getPrincipalId());
        }
        if (StringUtils.isNotBlank(ppd.getPrincipalId())) {
            Principal person = KimApiServiceLocator.getIdentityService().getPrincipal(ppd.getPrincipalId());

            if (person != null) {
                ppd.refreshNonUpdateableReferences();
            }
        }
    }

    /**
     * Gets the {@link Proposal}
     *
     * @return
     */
    public Proposal getProposal() {
        return (Proposal) getBusinessObject();
    }

    /**
     * called for refreshing the {@link Subcontractor} on {@link ProposalSubcontractor} before adding to the
     * {@link ProposalSubcontractor}s collection on the proposal. this is to ensure that the summary fields are shown correctly.
     * i.e. {@link Subcontractor} name
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        refreshProposal(false);
        super.addNewLineToCollection(collectionName);
    }
}
