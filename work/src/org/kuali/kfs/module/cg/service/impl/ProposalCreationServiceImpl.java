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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.cg.dto.AwardParametersDTO;
import org.kuali.kfs.integration.cg.dto.ProposalCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.ProposalParametersDTO;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.businessobject.ProposalAutoCreateDefaults;
import org.kuali.kfs.module.cg.businessobject.ProposalOrganization;
import org.kuali.kfs.module.cg.businessobject.ProposalProjectDirector;
import org.kuali.kfs.module.cg.businessobject.defaultvalue.NextProposalNumberFinder;
import org.kuali.kfs.module.cg.service.ProposalCreationService;
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
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Services Implemented for Proposal creation.
 */
public class ProposalCreationServiceImpl implements ProposalCreationService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProposalCreationServiceImpl.class);

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cg.service.ProposalCreationService#createProposal(org.kuali.kfs.integration.cg.dto.ProposalParametersDTO)
     */
    public ProposalCreationStatusDTO createProposal(ProposalParametersDTO proposalParameters) {

        ProposalCreationStatusDTO proposalCreationStatus = new ProposalCreationStatusDTO();
        proposalCreationStatus.setErrorMessages(new ArrayList<String>());
        proposalCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_SUCCESS);

        // check to see if the user has the permission to create account
        String principalId = proposalParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            this.setFailStatus(proposalCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.ProposalCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[] { principalId }));
            return proposalCreationStatus;
        }

        ProposalAutoCreateDefaults defaults = getProposalDefaults(proposalParameters.getUnit());
        Proposal proposal = createProposalObject(proposalParameters, defaults);
        createAutomaticCGProposalMaintenanceDocument(proposal, proposalCreationStatus);

        for (ProposalOrganization proposalOrganization : proposal.getProposalOrganizations()) {
            proposalOrganization.setProposalNumber(proposal.getProposalNumber());
        }

        proposalCreationStatus.setProposalNumber(proposal.getProposalNumber());
        proposalCreationStatus.setProposal(proposal);

        return proposalCreationStatus;
    }

    /**
     * @param unitNumber
     * @return
     */
    protected ProposalAutoCreateDefaults getProposalDefaults(String unitNumber) {

        ProposalAutoCreateDefaults defaults = null;

        if (unitNumber == null || unitNumber.isEmpty()) {
            return null;
        }

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kcUnit", unitNumber);
        defaults = (ProposalAutoCreateDefaults) businessObjectService.findByPrimaryKey(ProposalAutoCreateDefaults.class, criteria);

        // if the matching defaults is null, try the parents in the hierarchy
        if (defaults == null) {

            List<String> parentUnits = null;
            try {
                parentUnits = SpringContext.getBean(ContractsAndGrantsModuleService.class).getParentUnits(unitNumber);
            }
            catch (Exception ex) {
                LOG.error(KcUtils.getErrorMessage(ContractsAndGrantsConstants.ProposalCreationService.ERROR_KC_PROPOSAL_PARAMS_UNIT_NOTFOUND, null) + ": " + ex.getMessage());

                GlobalVariables.getMessageMap().putError(ContractsAndGrantsConstants.ProposalCreationService.ERROR_KC_PROPOSAL_PARAMS_UNIT_NOTFOUND, "unit", ex.getMessage());
            }

            if (parentUnits != null) {
                for (String unit : parentUnits) {
                    criteria.put("kcUnit", unit);
                    defaults = (ProposalAutoCreateDefaults) businessObjectService.findByPrimaryKey(ProposalAutoCreateDefaults.class, criteria);
                    if (defaults != null)
                        break;
                }
            }
        }

        return defaults;
    }

    /**
     * This method check to see if the user can create the proposal maintenance document and set the user session
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
        if (documentAuthorizer.canInitiate(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Proposal.class), user)) {
            // set the user session so that the user name can be displayed in the saved document
            GlobalVariables.setUserSession(new UserSession(user.getPrincipalName()));
            return true;
        }

        LOG.error(KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[] { principalId }));

        return false;
    }

    /**
     * @param proposalCreationStatus
     * @param message
     */
    protected void setFailStatus(ProposalCreationStatusDTO proposalCreationStatus, String message) {
        proposalCreationStatus.getErrorMessages().add(message);
        proposalCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
    }

    /**
     * This method created the proposal Object.
     * 
     * @param proposalParameters
     * @param defaults
     * @return
     */
    protected Proposal createProposalObject(ProposalParametersDTO proposalParameters, ProposalAutoCreateDefaults defaults) {
        Proposal proposal = new Proposal();

        proposal.setProposalNumber(NextProposalNumberFinder.getLongValue());

        proposal.setProposalStatusCode(proposalParameters.getProposalStatusCode());
        proposal.setAgencyNumber(proposalParameters.getAgencyNumber());
        proposal.setProposalProjectTitle(proposalParameters.getProposalProjectTitle());
        proposal.setProposalBeginningDate(new java.sql.Date(proposalParameters.getProposalBeginningDate().getTime()));
        proposal.setProposalEndingDate(new java.sql.Date(proposalParameters.getProposalEndingDate().getTime()));
        proposal.setProposalDirectCostAmount(new KualiDecimal(proposalParameters.getProposalDirectCostAmount()));

        // this value may be null, if so, then we will set it to default value below
        proposal.setProposalIndirectCostAmount(new KualiDecimal(proposalParameters.getProposalIndirectCostAmount()));

        proposal.setProposalSubmissionDate(new java.sql.Date(proposalParameters.getProposalSubmissionDate().getTime()));
        proposal.setProposalAwardTypeCode(proposalParameters.getProposalAwardTypeCode());
        proposal.setProposalPurposeCode(proposalParameters.getProposalPurposeCode());
        proposal.setActive(true);

        // set primary Project Director if specified in parameters
        if (!StringUtils.isBlank(proposalParameters.getProposalPrimaryProjectDirectorId())) {
            ProposalProjectDirector proposalProjectDirector = new ProposalProjectDirector();
            proposalProjectDirector.setPrincipalId(proposalParameters.getProposalPrimaryProjectDirectorId());
            proposalProjectDirector.setProposalNumber(proposal.getProposalNumber());
            proposalProjectDirector.setActive(true);
            proposalProjectDirector.setProposalPrimaryProjectDirectorIndicator(true);

            List<ProposalProjectDirector> proposalProjectDirectors = new ArrayList<ProposalProjectDirector>();
            proposalProjectDirectors.add(proposalProjectDirector);

            proposal.setProposalProjectDirectors(proposalProjectDirectors);
        }

        // set from default values
        if (defaults != null) {
            if (proposal.getProposalProjectDirectors().isEmpty()) {
                // set primary Project Director if not already set from paramaters
                ProposalProjectDirector proposalProjectDirector = new ProposalProjectDirector();
                proposalProjectDirector.setPrincipalId(defaults.getPrincipalId());
                proposalProjectDirector.setProposalNumber(proposal.getProposalNumber());
                proposalProjectDirector.setActive(true);
                proposalProjectDirector.setProposalPrimaryProjectDirectorIndicator(true);

                List<ProposalProjectDirector> proposalProjectDirectors = new ArrayList<ProposalProjectDirector>();
                proposalProjectDirectors.add(proposalProjectDirector);

                proposal.setProposalProjectDirectors(proposalProjectDirectors);
            }

            if (StringUtils.isBlank(proposalParameters.getProposalIndirectCostAmount())) {
                proposal.setProposalIndirectCostAmount(defaults.getProposalIndirectCostAmount());
            }

            // create proposal organization from default values
            ProposalOrganization proposalOrganization = new ProposalOrganization();
            proposalOrganization.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
            proposalOrganization.setOrganizationCode(defaults.getOrganizationCode());
            proposalOrganization.setActive(true);
            proposalOrganization.setProposalPrimaryOrganizationIndicator(true);

            List<ProposalOrganization> proposalOrganizations = new ArrayList<ProposalOrganization>();
            proposalOrganizations.add(proposalOrganization);
            proposal.setProposalOrganizations(proposalOrganizations);

            // have to set it directly in the object as well.
            proposal.setPrimaryProposalOrganization(proposalOrganization);
        }
        else {
            LOG.warn("No default values found for Proposal Creation with unit: " + proposalParameters.getUnit());
        }

        return proposal;
    }

    /**
     * @param proposal
     * @param proposalCreationStatus
     */
    protected void createAutomaticCGProposalMaintenanceDocument(Proposal proposal, ProposalCreationStatusDTO proposalCreationStatus) {

        // create a new maintenance document
        MaintenanceDocument maintenanceProposalDocument = (MaintenanceDocument) createCGProposalMaintenanceDocument(proposalCreationStatus);

        if (ObjectUtils.isNotNull(maintenanceProposalDocument)) {
            // set document header description...
            maintenanceProposalDocument.getDocumentHeader().setDocumentDescription(ContractsAndGrantsConstants.ProposalCreationService.AUTOMATIC_CREATE_CG_PROPOSAL_MAINTENANCE_DOCUMENT_DESCRIPTION);

            // set the award object in the maintenance document.
            maintenanceProposalDocument.getNewMaintainableObject().setBusinessObject(proposal);
            maintenanceProposalDocument.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);

            // TODO if it requires a parameter to decide to save, submit or blanket approve, code must be written.
            // current action is to submit the document.
            try {
                getDocumentService().blanketApproveDocument(maintenanceProposalDocument, "", null);
                // set the document number in the status.
                proposalCreationStatus.setDocumentNumber(maintenanceProposalDocument.getDocumentNumber());
            }
            catch (WorkflowException e) {
                LOG.error(KcUtils.getErrorMessage(ContractsAndGrantsConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS, null) + ": " + e.getMessage());
                proposalCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                proposalCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            }
        }
        else {
            proposalCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
        }

    }

    /**
     * This method will use the DocumentService to create a new document. The documentTypeName is gathered by using
     * MaintenanceDocumentDictionaryService which uses Proposal class to get the document type name.
     * 
     * @param ProposalCreationStatusDTO
     * @return document returns a new document for the account document type or null if there is an exception thrown.
     */
    public Document createCGProposalMaintenanceDocument(ProposalCreationStatusDTO proposalCreationStatus) {

        boolean internalUserSession = false;
        try {
            if (GlobalVariables.getUserSession() == null) {
                internalUserSession = true;
                GlobalVariables.setUserSession(new UserSession(KNSConstants.SYSTEM_USER));
                GlobalVariables.clear();
            }
            Document document = getDocumentService().getNewDocument(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Proposal.class));
            return document;

        }
        catch (Exception e) {
            proposalCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            proposalCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
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
     * This method takes an AwardParametersDTO and copies it into the ProposalParametersDTO.
     * 
     * @param awardParameters
     * @return
     */
    public ProposalParametersDTO getProposalParametersDtoFromAwardParametersDto(AwardParametersDTO awardParameters) {
        ProposalParametersDTO proposalParametersDTO = new ProposalParametersDTO();
        proposalParametersDTO.setAgencyNumber(awardParameters.getAgencyNumber());
        proposalParametersDTO.setPrincipalId(awardParameters.getPrincipalId());
        proposalParametersDTO.setProposalAwardTypeCode(awardParameters.getProposalAwardTypeCode());
        proposalParametersDTO.setProposalBeginningDate(awardParameters.getProjectStartDate());
        proposalParametersDTO.setProposalEndingDate(awardParameters.getProjectEndDate());
        proposalParametersDTO.setProposalSubmissionDate(awardParameters.getProposalSubmissionDate());
        proposalParametersDTO.setProposalDirectCostAmount(awardParameters.getProposalDirectCostAmount());
        proposalParametersDTO.setProposalIndirectCostAmount(awardParameters.getProposalIndirectCostAmount());
        proposalParametersDTO.setProposalPrimaryProjectDirectorId(awardParameters.getProposalPrimaryProjectDirectorId());
        proposalParametersDTO.setProposalProjectTitle(awardParameters.getAwardProjectTitle());
        proposalParametersDTO.setProposalPurposeCode(awardParameters.getAwardPurposeCode());
        proposalParametersDTO.setProposalStatusCode(awardParameters.getAwardStatusCode());
        proposalParametersDTO.setUnit(awardParameters.getUnit());

        return proposalParametersDTO;
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

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

}
