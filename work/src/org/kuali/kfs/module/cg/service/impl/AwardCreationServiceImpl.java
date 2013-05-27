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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.dto.AwardCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AwardParametersDTO;
import org.kuali.kfs.integration.cg.dto.ProposalCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.ProposalParametersDTO;
import org.kuali.kfs.integration.cg.service.AwardCreationService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.AgencyType;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.module.cg.businessobject.AwardProjectDirector;
import org.kuali.kfs.module.cg.businessobject.AwardStatus;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.businessobject.ProposalAwardType;
import org.kuali.kfs.module.cg.businessobject.ProposalOrganization;
import org.kuali.kfs.module.cg.businessobject.ProposalPurpose;
import org.kuali.kfs.module.cg.service.ProposalCreationService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.util.KcUtils;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.kim.api.identity.PersonService; import org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.api.document.search.DocumentSearchResults;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.kim.api.identity.PersonService; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.service.MaintenanceDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

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
    private AccountService accountService;

    /**
     * @see org.kuali.kfs.integration.cg.service.AwardCreationService#createAward(org.kuali.kfs.integration.cg.dto.AwardParametersDTO)
     */
    @Override
    public AwardCreationStatusDTO createAward(AwardParametersDTO awardParameters) {

        AwardCreationStatusDTO awardCreationStatus = new AwardCreationStatusDTO();
        awardCreationStatus.setErrorMessages(new ArrayList<String>());
        awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_SUCCESS);


        // check to see if the user has the permission to create award
        String principalId = awardParameters.getPrincipalId();
        if(!isValidUser(principalId)) {
            this.setFailStatus(awardCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[] {principalId} ));
            return awardCreationStatus;
        }

        // check to see if agency exist
        String sponsorCode = awardParameters.getSponsorCode();
        if(!isValidSponsorCode(sponsorCode)){
            this.setFailStatus(awardCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.ERROR_SPONSOR_CODE_DOES_NOT_EXIST, new String[] {sponsorCode} ));
            return awardCreationStatus;
        }

        String accountNumber = awardParameters.getAccountNumber();
        String chartCode = awardParameters.getChartCode();

        // if accountNumber is not provided or does not exist in KFS then return with fail message
        if(StringUtils.isBlank(accountNumber) || StringUtils.isBlank(chartCode)){
            this.setFailStatus(awardCreationStatus, ContractsAndGrantsConstants.AwardCreationService.ACCOUNT_NUMBER_AND_CHART_CODE_CANNOT_BE_BLANK);
            return awardCreationStatus;
        }

        // if account does not exist, return with fail message
        Account account = accountService.getByPrimaryId(chartCode, accountNumber);
        if( account==null ) {
            this.setFailStatus(awardCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.ACCOUNT_NUMBER_AND_CHART_CODE_CANNOT_DOES_NOT_EXIST, new String[]{accountNumber, chartCode}));
            return awardCreationStatus;
        }

        // if award exists and is not finalize, we cannot edit the award,
        try {
            String unfinalizedDocumentNumber = "";
            unfinalizedDocumentNumber = getAwardDocumentNumberNotFinal(awardParameters.getAwardId());

            if(StringUtils.isNotBlank(unfinalizedDocumentNumber)) {
                this.setFailStatus(awardCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.ERROR_CG_AWARD_TO_UPDATE_NOT_YET_FINAL, new String[]{unfinalizedDocumentNumber} ));
                return awardCreationStatus;
            }
        }
        catch (WorkflowException ex) {
            LOG.error("Error while retrieving results from document search.");
            ex.printStackTrace();
        }

        // if awardId already exist, then update award instead.
        if(isAwardExists(awardParameters.getAwardId())) {
            return updateAward(awardParameters);
       } else {
            // create proposal first
            ProposalParametersDTO proposalParameters = getProposalCreationService().getProposalParametersDtoFromAwardParametersDto(awardParameters, getAgencyNumberFromSponsorCode(sponsorCode));
            ProposalCreationStatusDTO proposalCreationStatusDTO = getProposalCreationService().createProposal(proposalParameters);
            Proposal proposal = (Proposal) proposalCreationStatusDTO.getProposal();

            if(proposal==null){
                this.setFailStatus(awardCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.ERROR_CG_PROPOSAL_CREATION_FAILED, null));
                return awardCreationStatus;
            }

            Award award = createAwardObject(awardParameters, proposal, account);
            createAutomaticCGAwardMaintenanceDocument(award, awardCreationStatus);
            return awardCreationStatus;
       }
    }

    /**
     * @see org.kuali.kfs.integration.cg.service.AwardCreationService#updateAward(org.kuali.kfs.integration.cg.dto.AwardParametersDTO)
     */
    @Override
    public AwardCreationStatusDTO updateAward(AwardParametersDTO awardParameters) {
        AwardCreationStatusDTO awardCreationStatus = new AwardCreationStatusDTO();
        awardCreationStatus.setErrorMessages(new ArrayList<String>());
        awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_SUCCESS);

        String principalId = awardParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            this.setFailStatus(awardCreationStatus, KcUtils.getErrorMessage(ContractsAndGrantsConstants.AwardCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[] { principalId }));
            return awardCreationStatus;
        }

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

        Collection<Award> oldAwards = businessObjectService.findMatching(Award.class, criteria);
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
                maintenanceAwardDocument.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_EDIT_ACTION);
                boolean isDocumentLocked = checkForLockingDocument(maintenanceAwardDocument);
                if (!isDocumentLocked) {
                    getDocumentService().saveDocument(maintenanceAwardDocument);
                    awardCreationStatus.setDocumentNumber(maintenanceAwardDocument.getDocumentNumber());
                }
            }
            catch (Exception e) {
                LOG.error("Unable to update Agency Maintenance Document : " + e.getMessage());
                awardCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                awardCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            }
        }
        else {
            // record not found, set fail
            setFailStatus(awardCreationStatus, ContractsAndGrantsConstants.AwardCreationService.ERROR_UNABLE_TO_UPDATE_AWARD_RECORD_NOT_FOUND);
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
        newAward.setAgencyNumber(getAgencyNumberFromSponsorCode(awardParameters.getSponsorCode()));
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

        PersonService personService = SpringContext.getBean(PersonService.class);
        if (principalId == null) {
            return false;
        }
        Person user = personService.getPerson(principalId);
        if (user == null) {
            return false;
        }
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
    protected Award createAwardObject(AwardParametersDTO awardParameters, Proposal proposal, Account account) {
        Award award = new Award();

        award.setProposalNumber(proposal.getProposalNumber());
        award.setAwardProjectTitle(awardParameters.getAwardProjectTitle());

        // The following 3 fields have type mismatch between KC & KFS.  If these codes do not exist in KFS, then we will leave it blank.
        if( isAwardStatusCodeExist(awardParameters.getAwardStatusCode())){
            award.setAwardStatusCode(awardParameters.getAwardStatusCode());
        }

        if(isProposalPurposeCodeExist(awardParameters.getAwardPurposeCode())){
            award.setAwardPurposeCode(awardParameters.getAwardPurposeCode());
        }

        if(isProposalAwardTypeCodeExist(awardParameters.getProposalAwardTypeCode())){
            award.setProposalAwardTypeCode(awardParameters.getProposalAwardTypeCode());
        }

        award.setAgencyNumber(getAgencyNumberFromSponsorCode(awardParameters.getSponsorCode()));
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

        String defaultFundManagerName = SpringContext.getBean(ParameterService.class).getParameterValueAsString(Award.class, CGConstants.PARAMETER_DEFAULT_FUND_MANAGER_ID);
        Person awardFundManagerPerson = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(defaultFundManagerName);

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

        // set Accounts
        List<AwardAccount> awardAccounts = new ArrayList<AwardAccount>();
        AwardAccount awardAccount = createAwardAccountFromAccount(account, proposal.getProposalNumber());
        awardAccounts.add(awardAccount);
        award.setAwardAccounts(awardAccounts);

        // Set amounts from proposal
        if (proposal != null) {
            award.setAwardDirectCostAmount(proposal.getProposalDirectCostAmount());
            award.setAwardIndirectCostAmount(proposal.getProposalDirectCostAmount());
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
            maintenanceAwardDocument.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_NEW_ACTION);

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
                GlobalVariables.setUserSession(new UserSession(KRADConstants.SYSTEM_USER));
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


    protected AwardAccount createAwardAccountFromAccount(Account account, Long proposalNumber) {
        AwardAccount awardAccount = new AwardAccount();

        awardAccount.setProposalNumber(proposalNumber);
        awardAccount.setChartOfAccountsCode(account.getChartOfAccountsCode());
        awardAccount.setAccountNumber(account.getAccountNumber());
        awardAccount.setPrincipalId(account.getAccountFiscalOfficerUser().getPrincipalId());
        awardAccount.setActive(true);

        return awardAccount;
    }

    private boolean isAwardExists(String awardId){
        Map<String, String> map = new HashMap<String, String>();
        map.put("awardId", awardId);
        Collection<Award> awards = SpringContext.getBean(BusinessObjectService.class).findMatching(Award.class, map);
        if(awards!=null && !awards.isEmpty()){
            return true;
        }
        else {
            return false;
        }
    }

    private String getAwardDocumentNumberNotFinal(String awardId) throws WorkflowException{

        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        criteria.setSaveName(CGConstants.AWARD);
        criteria.setDocumentStatuses(Collections.singletonList(DocumentStatus.SAVED));

        // find documents in saved status
        DocumentSearchResults results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(GlobalVariables.getUserSession().getPrincipalId(), criteria.build());

        String documentNumber = getDocumentNumberByMatchingAwardId(results, awardId);

        if(!documentNumber.isEmpty()){
            return documentNumber;
        }

        // find documents still enroute
        criteria.setDocumentStatuses(Collections.singletonList(DocumentStatus.ENROUTE));
        results = KewApiServiceLocator.getWorkflowDocumentService().documentSearch(GlobalVariables.getUserSession().getPrincipalId(), criteria);

        return getDocumentNumberByMatchingAwardId(results, awardId);
    }

    private String getDocumentNumberByMatchingAwardId(DocumentSearchResults results, String awardId) throws WorkflowException{
        for (DocumentSearchResult resultRow: results.getSearchResults()) {
            boolean isExist = false;
            String documentNumber = "";
            for (KeyValue field : resultRow.getFieldValues()) {
                if(field.getKey() == "routeHeaderId"){
                    documentNumber = parseDocumentIdFromRouteDocHeader(field.getValue());
                    MaintenanceDocument maintenanceDocument = (MaintenanceDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
                    Award award = (Award)maintenanceDocument.getDocumentBusinessObject();
                    if(awardId.equals(award.getAwardId())){
                        return documentNumber;
                    }
                }
            }
        }
        return "";
    }

    private String getAgencyNumberFromSponsorCode(String sponsorCode) {
        String agencyNumber = "";
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("sponsorCode", sponsorCode);

        List<Agency> agencies = (List<Agency>)SpringContext.getBean(BusinessObjectService.class).findMatching(Agency.class, criteria);
        if(agencies!=null && !agencies.isEmpty()){
            agencyNumber = agencies.get(0).getAgencyNumber();
        }

        return agencyNumber;
    }

    /**
     * Retrieves the document id out of the route document header
     * @param routeDocHeader the String representing an HTML link to the document
     * @return the document id
     */
    protected String parseDocumentIdFromRouteDocHeader(String routeDocHeader) {
        int rightBound = routeDocHeader.indexOf('>') + 1;
        int leftBound = routeDocHeader.indexOf('<', rightBound);
        return routeDocHeader.substring(rightBound, leftBound);
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
        return (StringUtils.isNotBlank(blockingDocId));
    }

    protected boolean isAwardStatusCodeExist(String awardStatusCode){
        AwardStatus awardStatus = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(AwardStatus.class, awardStatusCode);
        return (ObjectUtils.isNotNull(awardStatus));
    }

    protected boolean isProposalPurposeCodeExist(String proposalPurposeCode){
        ProposalPurpose proposalPurpose = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(ProposalPurpose.class, proposalPurposeCode);
        return (ObjectUtils.isNotNull(proposalPurpose));
    }

    protected boolean isProposalAwardTypeCodeExist(String proposalAwardTypeCode){
        ProposalAwardType proposalAwardType = SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(ProposalAwardType.class, proposalAwardTypeCode);
        return (ObjectUtils.isNotNull(proposalAwardType));
    }

    protected boolean isValidSponsorCode(String sponsorCode){
        return (StringUtils.isNotBlank(getAgencyNumberFromSponsorCode(sponsorCode)));
    }

    /**
     * @param maintenanceDocumentService
     */
    public void setMaintenanceDocumentService(MaintenanceDocumentService maintenanceDocumentService) {
        this.maintenanceDocumentService = maintenanceDocumentService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }


}
