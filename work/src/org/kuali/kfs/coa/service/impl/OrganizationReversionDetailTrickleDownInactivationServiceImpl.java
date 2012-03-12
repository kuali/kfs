/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.OrganizationReversion;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.coa.businessobject.OrganizationReversionDetail;
import org.kuali.kfs.coa.service.OrganizationReversionDetailTrickleDownInactivationService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentHeaderService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * The default implementation of the OrganizationReversionDetailTrickleDownService
 */
public class OrganizationReversionDetailTrickleDownInactivationServiceImpl implements OrganizationReversionDetailTrickleDownInactivationService {
    private static final Logger LOG = Logger.getLogger(OrganizationReversionDetailTrickleDownInactivationServiceImpl.class);
    protected NoteService noteService;
    protected ConfigurationService kualiConfigurationService;
    protected BusinessObjectService businessObjectService;
    protected DocumentHeaderService documentHeaderService;
    
    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionDetailTrickleDownInactivationService#trickleDownInactiveOrganizationReversionDetails(org.kuali.kfs.coa.businessobject.OrganizationReversion, java.lang.String)
     */
    public void trickleDownInactiveOrganizationReversionDetails(OrganizationReversion organizationReversion, String documentNumber) {
        organizationReversion.refreshReferenceObject("organizationReversionDetail");
        trickleDownInactivations(organizationReversion.getOrganizationReversionDetail(), documentNumber);
    }

    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionDetailTrickleDownInactivationService#trickleDownInactiveOrganizationReversionDetails(org.kuali.kfs.coa.businessobject.OrganizationReversionCategory, java.lang.String)
     */
    public void trickleDownInactiveOrganizationReversionDetails(OrganizationReversionCategory organizationReversionCategory, String documentNumber) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("organizationReversionCategoryCode", organizationReversionCategory.getOrganizationReversionCategoryCode());
        Collection orgReversionDetails = businessObjectService.findMatching(OrganizationReversionDetail.class, fieldValues);
        
        List<OrganizationReversionDetail> organizationReversionDetailList = new ArrayList<OrganizationReversionDetail>();
        for (Object orgRevDetailAsObject : orgReversionDetails) {
            organizationReversionDetailList.add((OrganizationReversionDetail)orgRevDetailAsObject);
        }
        trickleDownInactivations(organizationReversionDetailList, documentNumber);
    }
    
    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionDetailTrickleDownInactivationService#trickleDownActiveOrganizationReversionDetails(org.kuali.kfs.coa.businessobject.OrganizationReversion, java.lang.String)
     */
    public void trickleDownActiveOrganizationReversionDetails(OrganizationReversion organizationReversion, String documentNumber) {
        organizationReversion.refreshReferenceObject("organizationReversionDetail");
        trickleDownActivations(organizationReversion.getOrganizationReversionDetail(), documentNumber);
    }

    /**
     * @see org.kuali.kfs.coa.service.OrganizationReversionDetailTrickleDownInactivationService#trickleDownActiveOrganizationReversionDetails(org.kuali.kfs.coa.businessobject.OrganizationReversionCategory, java.lang.String)
     */
    public void trickleDownActiveOrganizationReversionDetails(OrganizationReversionCategory organizationReversionCategory, String documentNumber) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("organizationReversionCategoryCode", organizationReversionCategory.getOrganizationReversionCategoryCode());
        Collection orgReversionDetails = businessObjectService.findMatching(OrganizationReversionDetail.class, fieldValues);
        
        List<OrganizationReversionDetail> organizationReversionDetailList = new ArrayList<OrganizationReversionDetail>();
        for (Object orgRevDetailAsObject : orgReversionDetails) {
            organizationReversionDetailList.add((OrganizationReversionDetail)orgRevDetailAsObject);
        }
        trickleDownActivations(organizationReversionDetailList, documentNumber);
    }

    /**
     * The method which actually does the work of inactivating the details
     * @param organizationReversionDetails the details to inactivate
     * @param documentNumber the document number which has the inactivations as part of it
     * @return an inactivation status object which will help us save notes
     */
    protected void trickleDownInactivations(List<OrganizationReversionDetail> organizationReversionDetails, String documentNumber) {
        TrickleDownStatus status = new TrickleDownStatus(KFSKeyConstants.ORGANIZATION_REVERSION_DETAIL_TRICKLE_DOWN_INACTIVATION, KFSKeyConstants.ORGANIZATION_REVERSION_DETAIL_TRICKLE_DOWN_INACTIVATION_ERROR_DURING_PERSISTENCE);

        if (!ObjectUtils.isNull(organizationReversionDetails) && !organizationReversionDetails.isEmpty()) {
            for (OrganizationReversionDetail detail : organizationReversionDetails) {
                if (detail.isActive()) {
                    detail.setActive(false);
                    try {
                        businessObjectService.save(detail);
                        status.addOrganizationReversionDetail(detail);
                    }
                    catch (RuntimeException re) {
                        LOG.error("Unable to trickle-down inactivate sub-account " + detail.toString(), re);
                        status.addErrorPersistingOrganizationReversionDetail(detail);
                    }
                }
            }
        }
        
        status.saveSuccesfullyChangedNotes(documentNumber);
        status.saveErrorNotes(documentNumber);
    }
    
    /**
     * The method which actually does the work of activating the details
     * @param organizationReversionDetails the details to inactivate
     * @param documentNumber the document number which has the inactivations as part of it
     * @return an inactivation status object which will help us save notes
     */
    protected void trickleDownActivations(List<OrganizationReversionDetail> organizationReversionDetails, String documentNumber) {
        TrickleDownStatus status = new TrickleDownStatus(KFSKeyConstants.ORGANIZATION_REVERSION_DETAIL_TRICKLE_DOWN_ACTIVATION, KFSKeyConstants.ORGANIZATION_REVERSION_DETAIL_TRICKLE_DOWN_ACTIVATION_ERROR_DURING_PERSISTENCE);
                
        if (!ObjectUtils.isNull(organizationReversionDetails) && !organizationReversionDetails.isEmpty()) {
            for (OrganizationReversionDetail detail : organizationReversionDetails) {
                if (!detail.isActive() && allowActivation(detail)) {
                    detail.setActive(true);
                    try {
                        businessObjectService.save(detail);
                        status.addOrganizationReversionDetail(detail);
                    }
                    catch (RuntimeException re) {
                        LOG.error("Unable to trickle-down inactivate sub-account " + detail.toString(), re);
                        status.addErrorPersistingOrganizationReversionDetail(detail);
                    }
                }
            }
        }
        
        status.saveSuccesfullyChangedNotes(documentNumber);
        status.saveErrorNotes(documentNumber);
    }
    
    /**
     * Determines whether the given organization reversion detail can be activated: ie, that both its owning OrganizationReversion and its related
     * OrganizationReversionCategory are both active
     * @param detail the detail to check
     * @return true if the detail can be activated, false otherwise
     */
    protected boolean allowActivation(OrganizationReversionDetail detail) {
        boolean result = true;
        if (!ObjectUtils.isNull(detail.getOrganizationReversion())) {
            result &= detail.getOrganizationReversion().isActive();
        }
        if (!ObjectUtils.isNull(detail.getOrganizationReversionCategory())) {
            result &= detail.getOrganizationReversionCategory().isActive();
        }
        return result;
    }

    /**
     * Inner class to keep track of what organization reversions were inactivated and which
     * had errors when the persisting of the inactivation was attempted
     */
    protected class TrickleDownStatus {
        private List<OrganizationReversionDetail> organizationReversionDetails;
        private List<OrganizationReversionDetail> errorPersistingOrganizationReversionDetails;
        private String successfullyChangedOrganizationReversionDetailsMessageKey;
        private String erroredOutOrganizationReversionDetailsMessageKey;
        
        /**
         * Constructs a OrganizationReversionDetailTrickleDownInactivationServiceImpl
         */
        public TrickleDownStatus(String successfullyChangedOrganizationReversionDetailsMessageKey, String erroredOutOrganizationReversionDetailsMessageKey) {
            organizationReversionDetails = new ArrayList<OrganizationReversionDetail>();
            errorPersistingOrganizationReversionDetails = new ArrayList<OrganizationReversionDetail>();
            this.successfullyChangedOrganizationReversionDetailsMessageKey = successfullyChangedOrganizationReversionDetailsMessageKey;
            this.erroredOutOrganizationReversionDetailsMessageKey = erroredOutOrganizationReversionDetailsMessageKey;
        }
        
        /**
         * Adds an organization reversion detail which had a successfully persisted activation to the message list
         * @param organizationReversionDetail the detail to add to the list
         */
        public void addOrganizationReversionDetail(OrganizationReversionDetail organizationReversionDetail) {
            organizationReversionDetails.add(organizationReversionDetail);
        }
        
        /**
         * Adds an organization reversion detail which could not successful persist its activation to the error message list
         * @param organizationReversionDetail the detail to add to the list
         */
        public void addErrorPersistingOrganizationReversionDetail(OrganizationReversionDetail organizationReversionDetail) {
            errorPersistingOrganizationReversionDetails.add(organizationReversionDetail);
        }
        
        /**
         * @return the number of details we want per note
         */
        protected int getDetailsPerNote() {
            return 20;
        }
        
        /**
         * Builds a List of Notes out of a list of OrganizationReversionDescriptions
         * @param messageKey the key of the note text in ApplicationResources.properties
         * @param noteParent the thing to stick the note on
         * @param organizationReversionDetails the List of OrganizationReversionDetails to make notes about
         * @return a List of Notes
         */
        protected List<Note> generateNotes(String messageKey, PersistableBusinessObject noteParent, List<OrganizationReversionDetail> organizationReversionDetails) {
            List<Note> notes = new ArrayList<Note>();
            List<String> organizationReversionDetailsDescriptions = generateOrganizationReversionDetailsForNotes(organizationReversionDetails);
            Note noteTemplate = new Note();
            for (String description : organizationReversionDetailsDescriptions) {
                if (!StringUtils.isBlank(description)) {
                    notes.add(buildNote(description, messageKey, noteTemplate, noteParent));
                }
            }
            return notes;
        }
        
        /**
         * Builds a note
         * @param description a description to put into the message of the note
         * @param messageKey the key of the note text in ApplicationResources.properties
         * @param noteTemplate the template for the note
         * @param noteParent the thing to stick the note on
         * @return the built note
         */
        protected Note buildNote(String description, String messageKey, Note noteTemplate, PersistableBusinessObject noteParent) {
            Note note = null;
            try {
                final String noteTextTemplate = kualiConfigurationService.getPropertyValueAsString(messageKey);
                final String noteText = MessageFormat.format(noteTextTemplate, description);
                note = noteService.createNote(noteTemplate, noteParent, GlobalVariables.getUserSession().getPrincipalId());
                note.setNoteText(noteText);
            }
            catch (Exception e) {
                // noteService.createNote throws *Exception*???
                // weak!!
                throw new RuntimeException("Cannot create note", e);
            }
            return note;
        }
        
        /**
         * Builds organization reverion detail descriptions to populate notes
         * @param organizationReversionDetails the list of details to convert to notes
         * @return a List of notes
         */
        protected List<String> generateOrganizationReversionDetailsForNotes(List<OrganizationReversionDetail> organizationReversionDetails) {
            List<String> orgRevDetailDescriptions = new ArrayList<String>();
            
            if (organizationReversionDetails.size() > 0) {
                StringBuilder description = new StringBuilder();
                description.append(getOrganizationReversionDetailDescription(organizationReversionDetails.get(0)));
                
                int count = 1;
                while (count < organizationReversionDetails.size()) {
                    if (count % getDetailsPerNote() == 0) { // time for a new note
                        orgRevDetailDescriptions.add(description.toString());
                        description = new StringBuilder();
                    } else {
                        description.append(", ");
                    }
                    description.append(getOrganizationReversionDetailDescription(organizationReversionDetails.get(count)));
                    count += 1;
                }
                
                // add the last description
                orgRevDetailDescriptions.add(description.toString());
            }
            
            return orgRevDetailDescriptions;
        }
        
        /**
         * Beautifully and eloquently describes an organization reversion detail
         * @param organizationReversionDetail the organization reversion detail to describe
         * @return the funny, heart-breaking, and ultimately inspiring resultant description
         */
        protected String getOrganizationReversionDetailDescription(OrganizationReversionDetail organizationReversionDetail) {
            return organizationReversionDetail.getChartOfAccountsCode() + " - " + organizationReversionDetail.getOrganizationCode() + " Category: " + organizationReversionDetail.getOrganizationReversionCategoryCode();
        }
        
        /**
         * Saves notes to a document
         * @param organizationReversionDetails the details to make notes about
         * @param messageKey the message key of the text of the note
         * @param documentNumber the document number to write to
         */
        protected void saveAllNotes(List<OrganizationReversionDetail> organizationReversionDetails, String messageKey, String documentNumber) {
            DocumentHeader noteParent = documentHeaderService.getDocumentHeaderById(documentNumber);
            List<Note> notes = generateNotes(messageKey, noteParent, organizationReversionDetails);
            noteService.saveNoteList(notes);
        }
        
        /**
         * Adds all the notes about successful inactivations
         * @param documentNumber document number to save them to
         */
        public void saveSuccesfullyChangedNotes(String documentNumber) {
            saveAllNotes(organizationReversionDetails, successfullyChangedOrganizationReversionDetailsMessageKey, documentNumber);
        }
        
        /**
         * Adds all the notes about inactivations which couldn't be saved
         * @param documentNumber the document number to save them to
         */
        public void saveErrorNotes(String documentNumber) {
            saveAllNotes(errorPersistingOrganizationReversionDetails, erroredOutOrganizationReversionDetailsMessageKey, documentNumber);
        }

        /**
         * Sets the erroredOutOrganizationReversionDetailsMessageKey attribute value.
         * @param erroredOutOrganizationReversionDetailsMessageKey The erroredOutOrganizationReversionDetailsMessageKey to set.
         */
        public void setErroredOutOrganizationReversionDetailsMessageKey(String erroredOutOrganizationReversionDetailsMessageKey) {
            this.erroredOutOrganizationReversionDetailsMessageKey = erroredOutOrganizationReversionDetailsMessageKey;
        }

        /**
         * Sets the successfullyChangedOrganizationReversionDetailsMessageKey attribute value.
         * @param successfullyChangedOrganizationReversionDetailsMessageKey The successfullyChangedOrganizationReversionDetailsMessageKey to set.
         */
        public void setSuccessfullyChangedOrganizationReversionDetailsMessageKey(String successfullyChangedOrganizationReversionDetailsMessageKey) {
            this.successfullyChangedOrganizationReversionDetailsMessageKey = successfullyChangedOrganizationReversionDetailsMessageKey;
        }
    }

    /**
     * Gets the kualiConfigurationService attribute. 
     * @return Returns the kualiConfigurationService.
     */
    public ConfigurationService getConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * Gets the noteService attribute. 
     * @return Returns the noteService.
     */
    public NoteService getNoteService() {
        return noteService;
    }

    /**
     * Sets the noteService attribute value.
     * @param noteService The noteService to set.
     */
    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the documentHeaderService attribute. 
     * @return Returns the documentHeaderService.
     */
    public DocumentHeaderService getDocumentHeaderService() {
        return documentHeaderService;
    }

    /**
     * Sets the documentHeaderService attribute value.
     * @param documentHeaderService The documentHeaderService to set.
     */
    public void setDocumentHeaderService(DocumentHeaderService documentHeaderService) {
        this.documentHeaderService = documentHeaderService;
    }
}
