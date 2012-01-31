/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.dto.AgencyCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AgencyParametersDTO;
import org.kuali.kfs.integration.cg.service.AgencyCreationService;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.util.KcUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Services Implemented for Agency creation.
 */
public class AgencyCreationServiceImpl implements AgencyCreationService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AgencyCreationServiceImpl.class);

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private MaintenanceDocumentService maintenanceDocumentService;

    /**
     * @see org.kuali.kfs.integration.cg.service.AgencyCreationService#createAgency(org.kuali.kfs.integration.cg.dto.AgencyParametersDTO)
     */
    public AgencyCreationStatusDTO createAgency(AgencyParametersDTO agencyParameters) {

        AgencyCreationStatusDTO agencyCreationStatus = new AgencyCreationStatusDTO();
        agencyCreationStatus.setErrorMessages(new ArrayList<String>());
        agencyCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_SUCCESS);

        // check to see if the user has the permission to create account
        String principalId = agencyParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            this.setFailStatus(agencyCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[] { principalId }));
            return agencyCreationStatus;
        }

        Agency agency = createAgencyObject(agencyParameters);
        createRouteAutomaticCGAgencyMaintenanceDocument(agency, agencyCreationStatus);
        return agencyCreationStatus;
    }

    /**
     * @see org.kuali.kfs.integration.cg.service.AgencyCreationService#updateAgency(org.kuali.kfs.integration.cg.dto.AgencyParametersDTO)
     */
    public AgencyCreationStatusDTO updateAgency(AgencyParametersDTO agencyParameters) {
        AgencyCreationStatusDTO agencyCreationStatus = new AgencyCreationStatusDTO();
        agencyCreationStatus.setErrorMessages(new ArrayList<String>());
        agencyCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_SUCCESS);

        // check to see if the user has the permission to create account
        String principalId = agencyParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            this.setFailStatus(agencyCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[] { principalId }));
            return agencyCreationStatus;
        }

        updateRouteAutomaticCGAgencyMaintenanceDocument(agencyParameters, agencyCreationStatus);

        return agencyCreationStatus;
    }

    /**
     * This method check to see if the user can create the agency maintenance document and set the user session
     * 
     * @param String principalId
     * @return boolean
     */
    protected boolean isValidUser(String principalId) {

        PersonService<Person> personService = KIMServiceLocator.getPersonService();
        if (principalId == null)
            return false;
        Person user = personService.getPerson(principalId);
        if (user == null)
            return false;
        DocumentAuthorizer documentAuthorizer = new MaintenanceDocumentAuthorizerBase();
        if (documentAuthorizer.canInitiate(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Agency.class), user)) {
            // set the user session so that the user name can be displayed in the saved document
            GlobalVariables.setUserSession(new UserSession(user.getPrincipalName()));
            return true;
        }

        LOG.error(KcUtils.getErrorMessage(ContractsAndGrantsConstants.AgencyCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[] { principalId }));

        return false;
    }

    /**
     * @param agencyCreationStatus
     * @param message
     */
    protected void setFailStatus(AgencyCreationStatusDTO agencyCreationStatus, String message) {
        agencyCreationStatus.getErrorMessages().add(message);
        agencyCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
    }

    /**
     * Created Agency object from the parameters.
     * 
     * @param agencyParameters
     * @return
     */
    protected Agency createAgencyObject(AgencyParametersDTO agencyParameters) {
        Agency agency = new Agency();

        agency.setAgencyNumber(agencyParameters.getAgencyNumber());
        agency.setReportingName(agencyParameters.getReportingName());
        agency.setFullName(agencyParameters.getFullName());
        agency.setAgencyTypeCode(agencyParameters.getAgencyTypeCode());
        agency.setActive(agencyParameters.isActive());
        agency.setInStateIndicator(agencyParameters.isInState());

        return agency;
    }

    /**
     * @param agency
     * @param agencyCreationStatus
     */
    protected void createRouteAutomaticCGAgencyMaintenanceDocument(Agency agency, AgencyCreationStatusDTO agencyCreationStatus) {

        // create a new maintenance document
        MaintenanceDocument maintenanceAgencyDocument = (MaintenanceDocument) createCGAgencyMaintenanceDocument(agencyCreationStatus);

        if (ObjectUtils.isNotNull(maintenanceAgencyDocument)) {
            // set document header description...
            maintenanceAgencyDocument.getDocumentHeader().setDocumentDescription(ContractsAndGrantsConstants.AgencyCreationService.AUTOMATIC_CREATE_CG_AGENCY_MAINTENANCE_DOCUMENT_DESCRIPTION);


            // set the agency object in the maintenance document.
            maintenanceAgencyDocument.getNewMaintainableObject().setBusinessObject(agency);
            maintenanceAgencyDocument.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);

            // TODO if it requires a parameter to decide to save, submit or blanket approve, code must be written.
            // current action is to submit the document.
            try {
                getDocumentService().saveDocument(maintenanceAgencyDocument);
                // set the document number in the status.
                agencyCreationStatus.setDocumentNumber(maintenanceAgencyDocument.getDocumentNumber());
            }
            catch (WorkflowException e) {
                LOG.error(KcUtils.getErrorMessage(ContractsAndGrantsConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS, null) + ": " + e.getMessage());
                agencyCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                agencyCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            }
        }
        else {
            agencyCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
        }

    }

    /**
     * This method will use the DocumentService to create a new document. The documentTypeName is gathered by using
     * MaintenanceDocumentDictionaryService which uses Agency class to get the document type name.
     * 
     * @param AgencyCreationStatusDTO
     * @return document returns a new document for the account document type or null if there is an exception thrown.
     */
    public Document createCGAgencyMaintenanceDocument(AgencyCreationStatusDTO agencyCreationStatus) {

        boolean internalUserSession = false;
        try {
            if (GlobalVariables.getUserSession() == null) {
                internalUserSession = true;
                GlobalVariables.setUserSession(new UserSession(KNSConstants.SYSTEM_USER));
                GlobalVariables.clear();
            }

            Document document = getDocumentService().getNewDocument(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Agency.class));
            return document;

        }
        catch (Exception e) {
            agencyCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            agencyCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            return null;
        }
        finally {
            // if a user session was established for this call, clear it our
            if (internalUserSession) {
                GlobalVariables.clear();
                GlobalVariables.setUserSession(null);
            }
        }
    }

    /**
     * @param agencyParameters
     * @param agencyCreationStatus
     */
    protected void updateRouteAutomaticCGAgencyMaintenanceDocument(AgencyParametersDTO agencyParameters, AgencyCreationStatusDTO agencyCreationStatus) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("agencyNumber", agencyParameters.getAgencyNumber());

        Agency oldAgency = (Agency) businessObjectService.findByPrimaryKey(Agency.class, criteria);
        Agency newAgency = (Agency) ObjectUtils.deepCopy(oldAgency);
        updateAgency(newAgency, agencyParameters);

        try {
            // set document header description...
            MaintenanceDocument maintenanceAgencyDocument = (MaintenanceDocument) createCGAgencyMaintenanceDocument(agencyCreationStatus);
            maintenanceAgencyDocument.getDocumentHeader().setDocumentDescription(ContractsAndGrantsConstants.AgencyCreationService.AUTOMATIC_UPDATE_CG_AGENCY_MAINTENANCE_DOCUMENT_DESCRIPTION);
            maintenanceAgencyDocument.getOldMaintainableObject().setBusinessObject(oldAgency);
            maintenanceAgencyDocument.getNewMaintainableObject().setBusinessObject(newAgency);
            maintenanceAgencyDocument.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_EDIT_ACTION);
            boolean isDocumentLocked = checkForLockingDocument(maintenanceAgencyDocument);
            if (!isDocumentLocked) {
                documentService.routeDocument(maintenanceAgencyDocument, null, null);
            }
            agencyCreationStatus.setDocumentNumber(maintenanceAgencyDocument.getDocumentNumber());
        }
        catch (Exception e) {
            LOG.error("Unable to update Agency Maintenance Document : " + e.getMessage());
            agencyCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            agencyCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);

        }
    }

    /**
     * @param agency
     * @param agencyParameters
     */
    protected void updateAgency(Agency agency, AgencyParametersDTO agencyParameters) {
        agency.setReportingName(agencyParameters.getReportingName());
        agency.setFullName(agencyParameters.getFullName());
        agency.setAgencyTypeCode(agencyParameters.getAgencyTypeCode());
        agency.setActive(agencyParameters.isActive());
        agency.setInStateIndicator(agencyParameters.isInState());
    }


    /**
     * Checks whether the Agency is currently locked.
     * 
     * @param document The MaintenanceDocument containing the vendor.
     * @return boolean true if the Agency is currently locked and false otherwise.
     */
    protected boolean checkForLockingDocument(MaintenanceDocument document) {
        String blockingDocId = maintenanceDocumentService.getLockingDocumentId(document);
        if (StringUtils.isBlank(blockingDocId)) {
            return false;
        }
        else {
            return true;
        }
    }


    /**
     * Gets the documentService attribute.
     * 
     * @return Current value of documentService.
     */
    protected DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
        this.maintenanceDocumentService = maintenanceDocumentService;
    }

}
