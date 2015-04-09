/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.kfs.module.tem.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TravelCardType;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.datadictionary.validation.charlevel.RegexValidationPattern;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Travel Service Implementation
 */
@Transactional
public class TravelServiceImpl implements TravelService {

    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private AccountsReceivableModuleService accountsReceivableModuleService;
    private TemRoleService temRoleService;
    private TravelDocumentDao travelDocumentDao;

    /**
     * Validate a phone number
     *
     * @param phoneNumber to validate
     */
    @Override
    public String validatePhoneNumber(final String phoneNumber, String error){
        return validatePhoneNumber("", phoneNumber, error);
    }

    /**
     * Validate a phone number
     *
     * @param countryCode to consider for validation
     * @param phoneNumber to validate
     */
    @Override
    public String validatePhoneNumber(final String countryCode, final String phoneNumber, String error){

        //Determine if the US phone format should be used or a very lax international format.
        if (StringUtils.isBlank(countryCode) || (!countryCode.equals("US"))){
            RegexValidationPattern pattern = new RegexValidationPattern();
            pattern.setPattern(TemConstants.INT_PHONE_PATTERN);
            if (phoneNumber != null && pattern.matches(phoneNumber)){
                return "";
            }
            else{
                return error;
            }
        }
        else{
            RegexValidationPattern pattern = new RegexValidationPattern();
            pattern.setPattern(TemConstants.US_PHONE_PATTERN);
            if (pattern.matches(phoneNumber)){
                return "";
            }
            else{
                return error;
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelService#findTemProfileByPrincipalId(java.lang.String)
     */
    @Override
    public TemProfile findTemProfileByPrincipalId(String principalId) {
        return SpringContext.getBean(TemProfileServiceImpl.class).findTemProfileByPrincipalId(principalId);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelService#isUserInitiatorOrArranger(org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean isUserInitiatorOrArranger(TravelDocument document, Person user){
        boolean isUser = false;

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        String initiator = workflowDocument.getInitiatorPrincipalId();
        String docType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

        if(initiator.equals(user.getPrincipalId()) || temRoleService.isTravelDocumentArrangerForProfile(docType, user.getPrincipalId(), document.getProfileId()) ) {
            isUser = true;
        }
        return isUser;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TravelService#getTravelCardTypes()
     */
    @Override
    public List<String> getTravelCardTypes() {
        List<String> travelCardTypes = new ArrayList<String>();
        for (TravelCardType cardType : (List<TravelCardType>)businessObjectService.findAll(TravelCardType.class)){
            travelCardTypes.add(cardType.getCode());
        }
        return travelCardTypes;
    }

    /**
     * Retrieves the parent document type names - up to "TT" - for the document type
     * @param documentTypeName the document type to find the ancestry of
     * @return the document type names, including TT and the given document type
     */
    @Override
    public Set<String> getParentDocumentTypeNames(String documentTypeName) {
        // hard code for now until we can build the actual branch
        Set<String> docTypes = new HashSet<String>();
        docTypes.add(TemConstants.TravelDocTypes.TEM_TRANSACTIONAL_DOCUMENT);
        if (TemConstants.TravelDocTypes.getAuthorizationDocTypes().contains(documentTypeName)) {
            docTypes.add(TemConstants.TravelDocTypes.TRAVEL_TRANSACTIONAL_DOCUMENT);
            docTypes.add(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
        } else if (TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT.equals(documentTypeName)) {
            docTypes.add(TemConstants.TravelDocTypes.TRAVEL_TRANSACTIONAL_DOCUMENT);
            docTypes.add(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT);
        } else {
            docTypes.add(documentTypeName);
        }
        return docTypes;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setParameterService(final ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setAccountsReceivableModuleService(AccountsReceivableModuleService accountsReceivableModuleService) {
        this.accountsReceivableModuleService = accountsReceivableModuleService;
    }

    public void setTravelDocumentDao(TravelDocumentDao travelDocumentDao) {
        this.travelDocumentDao = travelDocumentDao;
    }

    public void setTemRoleService(TemRoleService temRoleService){
        this.temRoleService = temRoleService;
    }

}
