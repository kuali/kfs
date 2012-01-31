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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.cg.dto.AwardCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AwardParametersDTO;
import org.kuali.kfs.integration.cg.dto.ProposalCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.ProposalParametersDTO;
import org.kuali.kfs.integration.cg.service.AwardCreationService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardAccountDefaults;
import org.kuali.kfs.module.cg.businessobject.AwardAutoCreateDefaults;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.module.cg.businessobject.AwardProjectDirector;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.businessobject.ProposalOrganization;
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
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Service implemented for Award Creation.
 */
public class AwardCreationServiceImpl implements AwardCreationService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardCreationServiceImpl.class);

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;
    private ProposalCreationService proposalCreationService;
    private DateTimeService dateTimeService;
    private MaintenanceDocumentService maintenanceDocumentService;

    /**
     * @see org.kuali.kfs.integration.cg.service.AwardCreationService#createAward(org.kuali.kfs.integration.cg.dto.AwardParametersDTO)
     */
    public AwardCreationStatusDTO createAward(AwardParametersDTO awardParameters) {

        AwardCreationStatusDTO awardCreationStatus = new AwardCreationStatusDTO();
        awardCreationStatus.setErrorMessages(new ArrayList<String>());
        awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_SUCCESS);

        // create proposal first
        ProposalParametersDTO proposalParameters = getProposalCreationService().getProposalParametersDtoFromAwardParametersDto(awardParameters);
        ProposalCreationStatusDTO proposalCreationStatusDTO = getProposalCreationService().createProposal(proposalParameters);

        // check to see if the user has the permission to create award
        String principalId = awardParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            this.setFailStatus(awardCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[] { principalId }));
            return awardCreationStatus;
        }

        AwardAutoCreateDefaults awardAutoCreateDefaults = getAwardAutoCreateDefaults(awardParameters.getUnit());
        Proposal proposal = proposalCreationStatusDTO.getProposal();

        Award award = createAwardObject(awardParameters, awardAutoCreateDefaults, proposal);
        createAutomaticCGAwardMaintenanceDocument(award, awardCreationStatus);
        return awardCreationStatus;
    }

    /**
     * @see org.kuali.kfs.integration.cg.service.AwardCreationService#updateAward(org.kuali.kfs.integration.cg.dto.AwardParametersDTO)
     */
    public AwardCreationStatusDTO updateAward(AwardParametersDTO awardParameters) {
        AwardCreationStatusDTO awardCreationStatus = new AwardCreationStatusDTO();
        awardCreationStatus.setErrorMessages(new ArrayList<String>());
        awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_SUCCESS);

        String principalId = awardParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            this.setFailStatus(awardCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[] { principalId }));
            return awardCreationStatus;
        }

        AwardAutoCreateDefaults awardAutoCreateDefaults = getAwardAutoCreateDefaults(awardParameters.getUnit());
        updateRouteAutomaticCGAwardMaintenanceDocument(awardParameters, awardCreationStatus);

        return awardCreationStatus;
    }

    /**
     * @param awardParameters
     * @param awardCreationStatus
     */
    protected void updateRouteAutomaticCGAwardMaintenanceDocument(AwardParametersDTO awardParameters, AwardCreationStatusDTO awardCreationStatus) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("awardId", awardParameters.getAwardId());

        Collection<Award> oldAwards = (Collection<Award>) businessObjectService.findMatching(Award.class, criteria);
        Award oldAward = null;
        if (oldAwards != null && oldAwards.iterator().hasNext()) {
            oldAward = oldAwards.iterator().next();
            Award newAward = (Award) ObjectUtils.deepCopy(oldAward);

            updateAward(newAward, awardParameters);

            try {
                // set document header description...
                MaintenanceDocument maintenanceAwardDocument = (MaintenanceDocument) createCGAwardMaintenanceDocument(awardCreationStatus);
                maintenanceAwardDocument.getDocumentHeader().setDocumentDescription(ContractsAndGrantsConstants.AwardCreationService.AUTOMATIC_UPDATE_CG_AWARD_MAINTENANCE_DOCUMENT_DESCRIPTION);
                maintenanceAwardDocument.getOldMaintainableObject().setBusinessObject(oldAward);
                maintenanceAwardDocument.getNewMaintainableObject().setBusinessObject(newAward);
                maintenanceAwardDocument.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_EDIT_ACTION);
                boolean isDocumentLocked = checkForLockingDocument(maintenanceAwardDocument);
                if (!isDocumentLocked) {
                    documentService.routeDocument(maintenanceAwardDocument, null, null);
                }
                awardCreationStatus.setDocumentNumber(maintenanceAwardDocument.getDocumentNumber());
            }
            catch (Exception e) {
                LOG.error("Unable to update Agency Maintenance Document : " + e.getMessage());
                awardCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);

            }
        }
        else {
            // record not found, set fail
            setFailStatus(awardCreationStatus, ContractsAndGrantsConstants.AwardCreationService.ERROR_KC_DOCUMENT_RECORD_NOT_FOUND);
            awardCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            return;
        }
    }

    /**
     * @param newAward
     * @param awardParameters
     */
    protected void updateAward(Award newAward, AwardParametersDTO awardParameters) {
        Proposal proposal = newAward.getProposal();
        newAward.setAwardStatusCode(awardParameters.getAwardStatusCode());
        newAward.setAwardProjectTitle(awardParameters.getAwardProjectTitle());
        newAward.setAwardPurposeCode(awardParameters.getAwardPurposeCode());
        newAward.setProposalAwardTypeCode(awardParameters.getProposalAwardTypeCode());
        newAward.setAgencyNumber(awardParameters.getAgencyNumber());
        newAward.setAwardDocumentNumber(awardParameters.getAwardDocumentNumber());
        newAward.setAwardBeginningDate(new java.sql.Date(awardParameters.getProjectStartDate().getTime()));
        newAward.setAwardEndingDate(new java.sql.Date(awardParameters.getProjectEndDate().getTime()));

        // set Proposal Project Director
        AwardProjectDirector awardProjectDirector = new AwardProjectDirector();
        awardProjectDirector.setPrincipalId(awardParameters.getProposalPrimaryProjectDirectorId());
        awardProjectDirector.setProposalNumber(proposal.getProposalNumber());
        awardProjectDirector.setAwardPrimaryProjectDirectorIndicator(true);
        awardProjectDirector.setActive(true);

        List<AwardProjectDirector> awardProjectDirectors = new ArrayList<AwardProjectDirector>();
        awardProjectDirectors.add(awardProjectDirector);
        newAward.setAwardProjectDirectors(awardProjectDirectors);


        // create proposal organization from proposal values
        AwardOrganization awardOrganization = new AwardOrganization();

        if (proposal != null && proposal.getProposalOrganizations() != null) {
            ProposalOrganization proposalOrganization = proposal.getProposalOrganizations().get(0);
            awardOrganization.setChartOfAccountsCode(proposalOrganization.getChartOfAccountsCode());
            awardOrganization.setOrganizationCode(proposalOrganization.getOrganizationCode());
            awardOrganization.setActive(proposalOrganization.isActive());
            awardOrganization.setAwardPrimaryOrganizationIndicator(proposalOrganization.isProposalPrimaryOrganizationIndicator());

            List<AwardOrganization> awardOrganizations = new ArrayList<AwardOrganization>();
            awardOrganizations.add(awardOrganization);
            newAward.setAwardOrganizations(awardOrganizations);
        }
    }

    /**
     * This method check to see if the user can create the award maintenance document and set the user session
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
        if (documentAuthorizer.canInitiate(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Award.class), user)) {
            // set the user session so that the user name can be displayed in the saved document
            GlobalVariables.setUserSession(new UserSession(user.getPrincipalName()));
            return true;
        }

        LOG.error(KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[] { principalId }));

        return false;
    }

    /**
     * @param awardCreationStatus
     * @param message
     */
    protected void setFailStatus(AwardCreationStatusDTO awardCreationStatus, String message) {
        awardCreationStatus.getErrorMessages().add(message);
        awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
    }

    /**
     * @param awardParameters
     * @param defaults
     * @param proposal
     * @return
     */
    protected Award createAwardObject(AwardParametersDTO awardParameters, AwardAutoCreateDefaults defaults, Proposal proposal) {
        Award award = new Award();

        award.setProposalNumber(proposal.getProposalNumber());
        award.setAwardStatusCode(awardParameters.getAwardStatusCode());
        award.setAwardProjectTitle(awardParameters.getAwardProjectTitle());
        award.setAwardPurposeCode(awardParameters.getAwardPurposeCode());
        award.setProposalAwardTypeCode(awardParameters.getProposalAwardTypeCode());
        award.setAgencyNumber(awardParameters.getAgencyNumber());
        award.setAwardDocumentNumber(awardParameters.getAwardDocumentNumber());
        award.setAwardBeginningDate(new java.sql.Date(awardParameters.getProjectStartDate().getTime()));
        award.setAwardEndingDate(new java.sql.Date(awardParameters.getProjectEndDate().getTime()));
        award.setAwardId(awardParameters.getAwardId());

        // set Proposal Project Director
        AwardProjectDirector awardProjectDirector = new AwardProjectDirector();
        awardProjectDirector.setPrincipalId(proposal.getProposalProjectDirectors().get(0).getPrincipalId());
        awardProjectDirector.setProposalNumber(proposal.getProposalNumber());
        awardProjectDirector.setAwardPrimaryProjectDirectorIndicator(true);
        awardProjectDirector.setActive(true);

        List<AwardProjectDirector> awardProjectDirectors = new ArrayList<AwardProjectDirector>();
        awardProjectDirectors.add(awardProjectDirector);
        award.setAwardProjectDirectors(awardProjectDirectors);

        // set default Fund Manager
        AwardFundManager awardFundManager = new AwardFundManager();

        String defaultFundManagerName = KNSServiceLocator.getParameterService().getParameterValue(Award.class, CGConstants.PARAMETER_DEFAULT_FUND_MANAGER_ID);
        Person awardFundManagerPerson = KIMServiceLocator.getPersonService().getPersonByPrincipalName(defaultFundManagerName);

        awardFundManager.setFundManager(awardFundManagerPerson);
        awardFundManager.setAwardPrimaryFundManagerIndicator(true);
        awardFundManager.setActive(true);
        awardFundManager.setPrincipalId(awardFundManagerPerson.getPrincipalId());
        awardFundManager.setProposalNumber(proposal.getProposalNumber());

        List<AwardFundManager> awardFundManagers = new ArrayList<AwardFundManager>();
        awardFundManagers.add(awardFundManager);
        award.setAwardFundManagers(awardFundManagers);

        // create proposal organization from proposal values
        AwardOrganization awardOrganization = new AwardOrganization();

        if (proposal != null && proposal.getProposalOrganizations() != null) {
            ProposalOrganization proposalOrganization = proposal.getProposalOrganizations().get(0);
            awardOrganization.setChartOfAccountsCode(proposalOrganization.getChartOfAccountsCode());
            awardOrganization.setOrganizationCode(proposalOrganization.getOrganizationCode());
            awardOrganization.setActive(proposalOrganization.isActive());
            awardOrganization.setAwardPrimaryOrganizationIndicator(proposalOrganization.isProposalPrimaryOrganizationIndicator());

            List<AwardOrganization> awardOrganizations = new ArrayList<AwardOrganization>();
            awardOrganizations.add(awardOrganization);
            award.setAwardOrganizations(awardOrganizations);
        }

        // Set default values
        if (proposal != null) {
            award.setAwardDirectCostAmount(proposal.getProposalDirectCostAmount());
            award.setAwardIndirectCostAmount(proposal.getProposalDirectCostAmount());

            if (defaults != null) {
                // create AwardAccounts based off the defaults.
                List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
                for (AwardAccountDefaults awardAccountDefaults : defaults.getAwardAccountDefaults()) {
                    AwardAccount awardAccount = createAwardAccountFromDefaults(awardAccountDefaults, proposal.getProposalNumber());
                    awardAccounts.add(awardAccount);
                }
                award.setAwardAccounts(awardAccounts);
            }
        }

        award.setAwardEntryDate(getDateTimeService().getCurrentSqlDate()); // set entry date to current date
        return award;
    }

    /**
     * @param award
     * @param awardCreationStatus
     */
    protected void createAutomaticCGAwardMaintenanceDocument(Award award, AwardCreationStatusDTO awardCreationStatus) {

        // create a new maintenance document
        MaintenanceDocument maintenanceAwardDocument = (MaintenanceDocument) createCGAwardMaintenanceDocument(awardCreationStatus);

        if (ObjectUtils.isNotNull(maintenanceAwardDocument)) {
            // set document header description...
            maintenanceAwardDocument.getDocumentHeader().setDocumentDescription(ContractsAndGrantsConstants.AwardCreationService.AUTOMATIC_CREATE_CG_AWARD_MAINTENANCE_DOCUMENT_DESCRIPTION);

            // set the award object in the maintenance document.
            maintenanceAwardDocument.getNewMaintainableObject().setBusinessObject(award);
            maintenanceAwardDocument.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);

            // TODO if it requires a parameter to decide to save, submit or blanket approve, code must be written.
            // current action is to submit the document.
            try {
                getDocumentService().saveDocument(maintenanceAwardDocument);
                // set the document number in the status.
                awardCreationStatus.setDocumentNumber(maintenanceAwardDocument.getDocumentNumber());
            }
            catch (WorkflowException e) {
                LOG.error(KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS, null) + ": " + e.getMessage());
                awardCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            }
        }
        else {
            awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
        }

    }

    /**
     * This method will use the DocumentService to create a new document. The documentTypeName is gathered by using
     * MaintenanceDocumentDictionaryService which uses Award class to get the document type name.
     * 
     * @param AwardCreationStatusDTO
     * @return document returns a new document for the Award document type or null if there is an exception thrown.
     */
    public Document createCGAwardMaintenanceDocument(AwardCreationStatusDTO awardCreationStatus) {

        boolean internalUserSession = false;
        try {
            if (GlobalVariables.getUserSession() == null) {
                internalUserSession = true;
                GlobalVariables.setUserSession(new UserSession(KNSConstants.SYSTEM_USER));
                GlobalVariables.clear();
            }
            Document document = getDocumentService().getNewDocument(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Award.class));
            return document;

        }
        catch (Exception e) {
            awardCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
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
     * @param unitNumber
     * @return
     */
    protected AwardAutoCreateDefaults getAwardAutoCreateDefaults(String unitNumber) {
        AwardAutoCreateDefaults defaults = null;

        if (unitNumber == null || unitNumber.isEmpty()) {
            return null;
        }

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kcUnit", unitNumber);
        defaults = (AwardAutoCreateDefaults) businessObjectService.findByPrimaryKey(AwardAutoCreateDefaults.class, criteria);

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
                    defaults = (AwardAutoCreateDefaults) businessObjectService.findByPrimaryKey(AwardAutoCreateDefaults.class, criteria);
                    if (defaults != null)
                        break;
                }
            }
        }

        return defaults;

    }

    /**
     * @param defaults
     * @param proposalNumber
     * @return
     */
    protected AwardAccount createAwardAccountFromDefaults(AwardAccountDefaults defaults, Long proposalNumber) {
        AwardAccount awardAccount = new AwardAccount();

        awardAccount.setProposalNumber(proposalNumber);
        awardAccount.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
        awardAccount.setAccountNumber(defaults.getAccountNumber());
        awardAccount.setPrincipalId(defaults.getPrincipalId());
        awardAccount.setActive(defaults.isActive());

        return awardAccount;
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

    protected ProposalCreationService getProposalCreationService() {
        return proposalCreationService;
    }

    public void setProposalCreationService(ProposalCreationService proposalCreationService) {
        this.proposalCreationService = proposalCreationService;
    }


    /**
     * Retrieve the Date Time Service
     * 
     * @return Date Time Service
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Assign the Date Time Service
     * 
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Checks whether the Award is currently locked.
     * 
     * @param document The MaintenanceDocument containing the vendor.
     * @return boolean true if the Award is currently locked and false otherwise.
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
     * @param maintenanceDocumentService
     */
    public void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
        this.maintenanceDocumentService = maintenanceDocumentService;
    }
}
