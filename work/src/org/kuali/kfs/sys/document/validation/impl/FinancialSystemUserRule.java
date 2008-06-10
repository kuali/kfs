/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.rules;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.RiceKeyConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.format.PhoneNumberFormatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.authorization.FinancialSystemUserDocumentAuthorizer;
import org.kuali.kfs.bo.FinancialSystemUser;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;

public class FinancialSystemUserRule extends KfsMaintenanceDocumentRuleBase {

    protected FinancialSystemUser oldUser;
    protected FinancialSystemUser newUser;
    protected FinancialSystemUserDocumentAuthorizer documentAuthorizer;

    protected static final PhoneNumberFormatter phoneNumberFormatter = new PhoneNumberFormatter();
    
    protected static final String EMBEDDED_UU_PREFIX = "embeddedUniversalUser.";
    
    public boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects(document);

        // only check if the user can modify the universal user attributes
        if ( documentAuthorizer.canEditUniversalUserAttributes(GlobalVariables.getUserSession().getFinancialSystemUser()) ) {
            success &= checkUniversalUserRules(document);
        }

        // only check if the user can modify the KFS user attributes
        if ( documentAuthorizer.canEditFinancialSystemUserAttributes(GlobalVariables.getUserSession().getFinancialSystemUser()) ) {
            success &= checkFinancialSystemUserRules(document);
        }      
        
        return success;
    }

    public boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // just run all the same rules
        processCustomRouteDocumentBusinessRules(document);
        return true;
    }

    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    protected void setupConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldUser = (FinancialSystemUser) document.getOldMaintainableObject().getBusinessObject();
        if (oldUser == null) {
            oldUser = new FinancialSystemUser();
            oldUser.setEmbeddedUniversalUser(new UniversalUser());
        } else {
            oldUser.refreshEmbeddedUniversalUser();
        }
        //oldUser.refreshNonUpdateableReferences();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newUser = (FinancialSystemUser) document.getNewMaintainableObject().getBusinessObject();
        //newUser.refreshNonUpdateableReferences();

        DocumentAuthorizer tempAuth = documentAuthorizationService.getDocumentAuthorizer(document);
        if ( tempAuth instanceof FinancialSystemUserDocumentAuthorizer ) {
            documentAuthorizer = (FinancialSystemUserDocumentAuthorizer)tempAuth;
        }
    }

    protected boolean checkUniversalUserRules(MaintenanceDocument document) {
        boolean success = true;

        // KULCOA-1164: Validation phone number
        String phoneNumber = newUser.getPersonLocalPhoneNumber();
        try {
            newUser.setPersonLocalPhoneNumber((String) phoneNumberFormatter.convertFromPresentationFormat(phoneNumber));
        } catch (Exception e) {
            putFieldError( EMBEDDED_UU_PREFIX + KFSPropertyConstants.PERSON_LOCAL_PHONE_NUMBER, RiceKeyConstants.ERROR_INVALID_FORMAT, new String[] { "Local Phone Number", phoneNumber });
            success = false;
        }

        // KULCOA-1164: Check whether User Id is unique or not
        String userId = newUser.getPersonUserIdentifier();
        if (userId != null && (!StringUtils.equals( userId, oldUser.getPersonUserIdentifier() ) || KFSConstants.MAINTENANCE_COPY_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction()))) {
            if (userExists(KFSPropertyConstants.PERSON_USER_IDENTIFIER, userId)) {
                putFieldError(EMBEDDED_UU_PREFIX + KFSPropertyConstants.PERSON_USER_IDENTIFIER, RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_KEYS_ALREADY_EXIST_ON_CREATE_NEW, userId);
                success = false;
            }
        }

        // KULCOA-1164: Check whether Employee Id is unique or not
        String emplId = newUser.getPersonPayrollIdentifier();
        if (emplId != null && (!emplId.equals(oldUser.getPersonPayrollIdentifier()) || KFSConstants.MAINTENANCE_COPY_ACTION.equals(document.getNewMaintainableObject().getMaintenanceAction()))) {
            if (userExists(KFSPropertyConstants.PERSON_PAYROLL_IDENTIFIER, emplId)) {
                putFieldError(EMBEDDED_UU_PREFIX + KFSPropertyConstants.PERSON_PAYROLL_IDENTIFIER, RiceKeyConstants.ERROR_DOCUMENT_KUALIUSERMAINT_UNIQUE_EMPLID);
                success = false;
            }
        }


        return success;
    }

    protected boolean checkFinancialSystemUserRules( MaintenanceDocument document ) {
        boolean success = true;

        if (document.isEdit() && !newUser.isActiveFinancialSystemUser()) {
            success = checkUserGroups();
        }
        
        if (newUser.isActiveFinancialSystemUser()) {
            // user must not have an employee status in the invalid list in the business rules (KULRNE-10)
            if (!SpringContext.getBean(ParameterService.class).getParameterEvaluator(FinancialSystemUser.class, KFSConstants.CoreApcParms.ACTIVE_EMPLOYEE_STATUSES_PARM, 
                    newUser.getEmployeeStatusCode()).evaluationSucceeds()) {
                success = false;
                GlobalVariables.getErrorMap().putError("activeFinancialSystemUser", KFSKeyConstants.ERROR_DOCUMENT_KUALIUSERMAINT_INVALID_EMP_STATUS);
            }
        }
        
        return success;
    }
    
    
    protected boolean userExists(String field, String value) {
        Map searchMap = new HashMap();
        searchMap.put(field, value);

        return financialSystemUserService.findUniversalUsers(searchMap).size() > 0;
    }

    
    /**
     * This checks to make sure if a user is getting marked as inactive then they're responsibilities and groups need to be removed
     * first
     * 
     * @param document
     * @return false if user is still part of groups and has responsibilities and is being marked inactive
     */
    protected boolean checkUserGroups() {
        boolean success = true;
        // cannot mark a user inactive if they're a member of a workgroup, a review hierarchy, or account delegate. (nf_duser-254)
        // message to the user to tell them to remove the user from these groups before marking inactive
        // test is against 1 since all users belong to kualiUniversalGroup
        if (newUser.getGroups().size() > 1) {
            success = false;
        }
        // this retrieves fiscal officer and account delegate responsibilities through
        // AccountDaoOjb.getAccountsThatUserIsResponsibleFor(KualiUser kualiUser)
        if (newUser.getAccountResponsibilities().size() > 0) {
            success = false;
        }
        if (!success) {
            GlobalVariables.getErrorMap().putError("activeFinancialSystemUser", KFSKeyConstants.ERROR_DOCUMENT_KUALIUSERMAINT_CANNOT_MARK_INACTIVE);
        }

        return success;
    }
}
