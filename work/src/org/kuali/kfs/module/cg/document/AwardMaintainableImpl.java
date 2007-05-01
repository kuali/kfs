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

import static org.kuali.kfs.KFSPropertyConstants.AWARD_ACCOUNTS;
import static org.kuali.kfs.KFSPropertyConstants.AWARD_PROJECT_DIRECTORS;
import static org.kuali.kfs.KFSPropertyConstants.AWARD_SUBCONTRACTORS;
import static org.kuali.kfs.KFSPropertyConstants.DOCUMENT;
import static org.kuali.kfs.KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.cg.bo.Award;
import org.kuali.module.cg.bo.AwardAccount;
import org.kuali.module.cg.bo.AwardOrganization;
import org.kuali.module.cg.bo.AwardProjectDirector;
import org.kuali.module.cg.bo.CGProjectDirector;
import org.kuali.module.cg.bo.ProjectDirector;
import org.kuali.module.cg.rules.AwardRuleUtil;
import org.kuali.rice.KNSServiceLocator;

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
        refreshAward(false);
        super.processAfterRetrieve();
    }

    /**
     * This method is called for refreshing the Agency before a save to display the full name in case the agency number was changed
     * by hand just before the save.
     */
    @Override
    public void prepareForSave() {
        refreshAward(false);
        List<AwardProjectDirector> directors = getAward().getAwardProjectDirectors();
        if (directors.size() == 1) {
            directors.get(0).setAwardPrimaryProjectDirectorIndicator(true);
        }
        List<AwardOrganization> organizations = getAward().getAwardOrganizations();
        if (organizations.size() == 1) {
            organizations.get(0).setAwardPrimaryOrganizationIndicator(true);
        }

        super.prepareForSave();
    }

    /**
     * This method is called for refreshing the Agency after a lookup to display its full name without AJAX.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        
        if (StringUtils.equals(KFSPropertyConstants.PROPOSAL, (String) fieldValues.get(KFSConstants.REFERENCES_TO_REFRESH))) {
            String pathToMaintainable = DOCUMENT + "." + NEW_MAINTAINABLE_OBJECT;
            GlobalVariables.getErrorMap().addToErrorPath(pathToMaintainable);

            boolean awarded = AwardRuleUtil.isProposalAwarded(getAward());
            if (awarded) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_AWARD_PROPOSAL_AWARDED, new String[] { getAward().getProposalNumber().toString() });
            }
            if (AwardRuleUtil.isProposalInactive(getAward())) {
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_AWARD_PROPOSAL_INACTIVE, new String[] { getAward().getProposalNumber().toString() });
            }
            GlobalVariables.getErrorMap().removeFromErrorPath(pathToMaintainable);

            // copy over proposal values after refresh
            if (!awarded) {
                refreshAward(KNSServiceLocator.KUALI_LOOKUPABLE.equals(fieldValues.get(KFSConstants.REFRESH_CALLER)));
                super.refresh(refreshCaller, fieldValues, document);
                Award award = getAward();
                award.populateFromProposal(award.getProposal());
                refreshAward(KNSServiceLocator.KUALI_LOOKUPABLE.equals(fieldValues.get(KFSConstants.REFRESH_CALLER)));
            }
        }else{
            refreshAward(KNSServiceLocator.KUALI_LOOKUPABLE.equals(fieldValues.get(KFSConstants.REFRESH_CALLER)));
            super.refresh(refreshCaller, fieldValues, document);
        }

    }

    private void refreshAward(boolean refreshFromLookup) {
        Award award = getAward();
        award.refreshNonUpdateableReferences();

        getNewCollectionLine(AWARD_SUBCONTRACTORS).refreshNonUpdateableReferences();
        getNewCollectionLine(AWARD_PROJECT_DIRECTORS).refreshNonUpdateableReferences();
        getNewCollectionLine(AWARD_ACCOUNTS).refreshNonUpdateableReferences();

        // the org list doesn't need any refresh
        refreshNonUpdateableReferences(award.getAwardOrganizations());
        refreshNonUpdateableReferences(award.getAwardAccounts());
        refreshNonUpdateableReferences(award.getAwardSubcontractors());
        refreshAwardProjectDirectors(refreshFromLookup);
    }

    /**
     * Refreshs this maintainable's AwardProjectDirectors.
     * 
     * @param refreshFromLookup a lookup returns only the primary key, so ignore the secondary key when true
     */
    private void refreshAwardProjectDirectors(boolean refreshFromLookup) {
        if (refreshFromLookup) {
            getNewCollectionLine(AWARD_PROJECT_DIRECTORS).refreshNonUpdateableReferences();
            refreshNonUpdateableReferences(getAward().getAwardProjectDirectors());


            getNewCollectionLine(AWARD_ACCOUNTS).refreshNonUpdateableReferences();
            refreshNonUpdateableReferences(getAward().getAwardAccounts());
        }
        else {
            refreshWithSecondaryKey((AwardProjectDirector) getNewCollectionLine(AWARD_PROJECT_DIRECTORS));
            for (AwardProjectDirector projectDirector : getAward().getAwardProjectDirectors()) {
                refreshWithSecondaryKey(projectDirector);
            }

            refreshWithSecondaryKey((AwardAccount) getNewCollectionLine(AWARD_ACCOUNTS));
            for (AwardAccount account : getAward().getAwardAccounts()) {
                refreshWithSecondaryKey(account);
            }
        }
    }

    // todo: move to ObjectUtils?
    private static void refreshNonUpdateableReferences(Collection<? extends PersistableBusinessObject> collection) {
        for (PersistableBusinessObject item : collection) {
            item.refreshNonUpdateableReferences();
        }
    }

    /**
     * Refreshes the reference to ProjectDirector, giving priority to its secondary key. Any secondary key that it has may be user
     * input, so that overrides the primary key, setting the primary key. If its primary key is blank or nonexistent, then leave the
     * current reference as it is, because it may be a nonexistent instance which is holding the secondary key (the username, i.e.,
     * personUserIdentifier) so we can redisplay it to the user for correction. If it only has a primary key then use that, because
     * it may be coming from the database, without any user input.
     * 
     * @param director the ProjectDirector to refresh
     */
    private static void refreshWithSecondaryKey(CGProjectDirector director) {
        if (ObjectUtils.isNotNull(director.getProjectDirector())) {
            String secondaryKey = director.getProjectDirector().getPersonUserIdentifier();
            if (StringUtils.isNotBlank(secondaryKey)) {
                ProjectDirector dir = SpringServiceLocator.getProjectDirectorService().getByPersonUserIdentifier(secondaryKey);
                director.setPersonUniversalIdentifier(dir == null ? null : dir.getPersonUniversalIdentifier());
            }
            if (StringUtils.isNotBlank(director.getPersonUniversalIdentifier()) && SpringServiceLocator.getProjectDirectorService().primaryIdExists(director.getPersonUniversalIdentifier())) {
                ((PersistableBusinessObject) director).refreshNonUpdateableReferences();
            }
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
        refreshAward(false);
        super.addNewLineToCollection(collectionName);
    }
}
