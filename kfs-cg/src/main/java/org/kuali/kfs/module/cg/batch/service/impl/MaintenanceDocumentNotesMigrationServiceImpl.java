/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2015 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cg.batch.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.batch.service.MaintenanceDocumentNotesMigrationService;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.NoteService;

/**
 * Default implementation of MaintenanceDocumentNotesMigrationService
 */
public class MaintenanceDocumentNotesMigrationServiceImpl implements MaintenanceDocumentNotesMigrationService {
    protected BusinessObjectService businessObjectService;
    protected IdentityService identityService;
    protected NoteService noteService;

    /**
     * Migrates agency notes
     * @see org.kuali.kfs.module.cg.batch.service.MaintenanceDocumentNotesMigrationService#moveAgencyMaintenanceDocumentNotesToBusinessObjects()
     */
    @Override
    public void moveAgencyMaintenanceDocumentNotesToBusinessObjects() {
        final Principal systemUser = getSystemUser();
        final Collection<Agency> agencies = getBusinessObjectService().findAll(Agency.class);
        for (Agency agency: agencies) {
            migrateNotesForAgency(agency, systemUser);
        }
    }

    /**
     * Migrates notes for a given agency
     * @param agency the agency to migrate notes to
     * @param systemUser the principal info about the system user
     */
    protected void migrateNotesForAgency(Agency agency, Principal systemUser) {
        final List<DocumentHeader> documentHeaders = lookupDocumentHeaders("AGCY", agency.getAgencyNumber(), CGPropertyConstants.AgencyFields.AGENCY_NUMBER, systemUser);
        for (DocumentHeader documentHeader : documentHeaders) {
            migrateNotes(documentHeader, agency);
        }
    }

    /**
     * Migrates award notes
     * @see org.kuali.kfs.module.cg.batch.service.MaintenanceDocumentNotesMigrationService#moveAwardMaintenanceDocumentNotesToBusinessObjects()
     */
    @Override
    public void moveAwardMaintenanceDocumentNotesToBusinessObjects() {
        final Principal systemUser = getSystemUser();
        final Collection<Award> awards = getBusinessObjectService().findAll(Award.class);
        for (Award award : awards) {
            migrateNotesForAward(award, systemUser);
        }
    }

    /**
     * Migrates notes for a given award
     * @param award the award to migrate notes to
     * @param systemUser the principal info about the system user
     */
    protected void migrateNotesForAward(Award award, Principal systemUser) {
        final List<DocumentHeader> documentHeaders = lookupDocumentHeaders("AWRD", award.getProposalNumber().toString(), KFSPropertyConstants.PROPOSAL_NUMBER, systemUser);
        for (DocumentHeader documentHeader : documentHeaders) {
            migrateNotes(documentHeader, award);
        }
    }

    /**
     * Searches for all maintenance document headers associated with the given document values
     * @param maintenanceDocumentType the document type of the maint doc
     * @param primaryKey the primary key value
     * @param primaryKeyField the name of the primary key field
     * @param systemUser the principal info of the system user
     * @return a List of document headers
     */
    protected List<DocumentHeader> lookupDocumentHeaders(String maintenanceDocumentType, String primaryKey, String primaryKeyField, Principal systemUser) {
        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentTypeName(maintenanceDocumentType);
        criteria.addDocumentAttributeValue(primaryKeyField, primaryKey);
        DocumentSearchCriteria crit = criteria.build();
        return retrieveDocuments(crit, systemUser);
    }

    /**
     * Searches for all document headers which match the given document search criteria
     * @param criteria a set of document search criteria
     * @param systemUser the principal information about the system user
     * @return all of the document headers for the given criteria
     */
    protected List<DocumentHeader> retrieveDocuments(DocumentSearchCriteria criteria, Principal systemUser) {
        final DocumentSearchResults results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(systemUser.getPrincipalId(), criteria);
        List<DocumentHeader> documentHeaders = new ArrayList<>();
        for (DocumentSearchResult result : results.getSearchResults()) {
            final DocumentHeader docHeader = getBusinessObjectService().findBySinglePrimaryKey(DocumentHeader.class, result.getDocument().getDocumentId());
            documentHeaders.add(docHeader);
        }
        return documentHeaders;
    }

    /**
     * Migrates any notes associated with the given document header to the given business object
     * @param documentHeader the document header to migrate notes for
     * @param businessObject the business object to migrate notes to
     */
    protected void migrateNotes(DocumentHeader documentHeader, PersistableBusinessObject businessObject) {
        final List<Note> notes = getNoteService().getByRemoteObjectId(documentHeader.getObjectId());
        if (!CollectionUtils.isEmpty(notes)) {
            for (Note note: notes) {
                note.setRemoteObjectIdentifier(businessObject.getObjectId());
                getNoteService().save(note);
            }
        }
    }

    /**
     * @return the principal for the system user
     */
    protected Principal getSystemUser() {
        return getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public NoteService getNoteService() {
        return noteService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }
}
