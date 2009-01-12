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
package org.kuali.kfs.module.cg.document;

import static org.kuali.kfs.sys.KFSPropertyConstants.PROPOSAL_PROJECT_DIRECTORS;
import static org.kuali.kfs.sys.KFSPropertyConstants.PROPOSAL_SUBCONTRACTORS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.ProjectDirector;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.businessobject.ProposalProjectDirector;
import org.kuali.kfs.module.cg.businessobject.ProposalResearchRisk;
import org.kuali.kfs.module.cg.businessobject.ResearchRiskType;
import org.kuali.kfs.module.cg.businessobject.defaultvalue.NextProposalNumberFinder;
import org.kuali.kfs.module.cg.document.service.RoutingFormResearchRiskService;
import org.kuali.kfs.module.cg.service.ProjectDirectorService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.util.AssertionUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.Section;

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
    public void setGenerateDefaultValues(boolean generateDefaultValues) {
        if (generateDefaultValues) {
            initResearchRiskTypes();
        }
        super.setGenerateDefaultValues(generateDefaultValues);
    }

    /**
     * 
     */
    private void initResearchRiskTypes() {
        List<ProposalResearchRisk> risks = getProposal().getProposalResearchRisks();
        AssertionUtils.assertThat(risks.isEmpty());
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

        getNewCollectionLine(PROPOSAL_SUBCONTRACTORS).refreshNonUpdateableReferences();

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
            getNewCollectionLine(PROPOSAL_PROJECT_DIRECTORS).refreshNonUpdateableReferences();
            refreshNonUpdateableReferences(getProposal().getProposalProjectDirectors());
        }
        else {
            refreshWithSecondaryKey((ProposalProjectDirector) getNewCollectionLine(PROPOSAL_PROJECT_DIRECTORS));
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
     * principalName) so we can redisplay it to the user for correction. If it only has a primary key then use that, because
     * it may be coming from the database, without any user input.
     * 
     * @param ppd the ProposalProjectDirector to refresh
     */
    private static void refreshWithSecondaryKey(ProposalProjectDirector ppd) {
        String secondaryKey = null;
        ppd.refreshReferenceObject("projectDirector");
        if (ObjectUtils.isNotNull(ppd.getProjectDirector())) {
            secondaryKey = ppd.getProjectDirector().getPrincipalName();
        }
        if (StringUtils.isNotBlank(secondaryKey)) {
            ProjectDirector dir = SpringContext.getBean(ProjectDirectorService.class).getByPersonUserIdentifier(secondaryKey);
            ppd.setPrincipalId(dir == null ? null : dir.getPrincipalId());
        }
        if (StringUtils.isNotBlank(ppd.getPrincipalId()) && SpringContext.getBean(ProjectDirectorService.class).primaryIdExists(ppd.getPrincipalId())) {
            ppd.refreshNonUpdateableReferences();
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

    /**
     * Allows customizing the maintenance document interface to hide research risks to unprivileged users.
     * 
     * @param oldMaintainable
     * @return
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = new ArrayList<Section>();

        List<Section> coreSections = getCoreSections(document, oldMaintainable);

        String preAwardWorkgroupName = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.PRE_AWARD_GROUP);
        String postAwardWorkgroupName = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.POST_AWARD_GROUP);

        Person user = GlobalVariables.getUserSession().getPerson();
        if (!KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, preAwardWorkgroupName) && !KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, postAwardWorkgroupName)) {
            for (Section section : coreSections) {
                if (!section.getSectionTitle().equalsIgnoreCase("Research Risks")) {
                    sections.add(section);
                }
                else {
                    // Do nothing
                }
            }
        }
        else {
            sections.addAll(coreSections);
        }
        return sections;
    }
}

