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
package org.kuali.kfs.sys.document.authorization;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.authorization.UniversalUserAuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.businessobject.FinancialSystemUserOrganizationSecurity;
import org.kuali.kfs.sys.businessobject.FinancialSystemUserPrimaryOrganization;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KNSPropertyConstants;

/**
 * Universal User specific authorization rules.
 * 
 * 
 */
public class FinancialSystemUserDocumentAuthorizer extends MaintenanceDocumentAuthorizerBase {

    protected transient static KualiConfigurationService configService;
    protected transient static ParameterService parameterService;
    protected transient static String universalUserEditWorkgroupName;
    protected transient static String financialSystemUserEditWorkgroupName;
    protected transient static boolean universalUsersMaintainedByKuali = true;
    protected transient static boolean financialSystemUsersMaintainedByKuali = true;
    protected transient static boolean passwordEditingEnabled = false;
    
    protected static final String EMBEDDED_UU_PREFIX = "embeddedUniversalUser.";
    
    /**
     * Constructs a UniversalUserDocumentAuthorizer.
     */
    public FinancialSystemUserDocumentAuthorizer() {
        super();
        initStatics();
    }

    /**
     * @see org.kuali.core.document.MaintenanceDocumentAuthorizerBase#getEditMode(org.kuali.core.document.Document, org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user) {
        Map editModes = new HashMap();
        if (!(document.getDocumentHeader().getWorkflowDocument().stateIsInitiated() || document.getDocumentHeader().getWorkflowDocument().stateIsSaved())) {
            editModes.put(UniversalUserAuthorizationConstants.MaintenanceEditMode.VIEW_ONLY, "TRUE");
        } else {
            editModes = super.getEditMode(document, user);            
        }
        //initStatics();
        
        // check for ssn edit mode
        if (user.isMember( universalUserEditWorkgroupName ) ) {
            editModes.put(UniversalUserAuthorizationConstants.MaintenanceEditMode.SSN_EDIT_ENTRY, "TRUE");
        }
        
        return editModes;
    }

    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, UniversalUser user) {
        MaintenanceDocumentAuthorizations auths = new MaintenanceDocumentAuthorizations();
        //initStatics();

        // prevent users not in the UU edit group from changing base UU properties
        if ( !canEditUniversalUserAttributes(user) ) {
            auths.addReadonlyAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.PERSON_USER_IDENTIFIER );
            auths.addReadonlyAuthField( KNSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + "personTaxIdentifier" );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + "personTaxIdentifierTypeCode" );
            auths.addReadonlyAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.PERSON_NAME );
            auths.addReadonlyAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.CAMPUS_CODE );
            auths.addReadonlyAuthField( EMBEDDED_UU_PREFIX + "primaryDepartmentCode" );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + "personPayrollIdentifier" );
            auths.addReadonlyAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.EMPLOYEE_STATUS_CODE );
            auths.addReadonlyAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.EMPLOYEE_TYPE_CODE );
            auths.addReadonlyAuthField( EMBEDDED_UU_PREFIX + "student" );
            auths.addReadonlyAuthField( EMBEDDED_UU_PREFIX + "staff" );
            auths.addReadonlyAuthField( EMBEDDED_UU_PREFIX + "faculty" );
            auths.addReadonlyAuthField( EMBEDDED_UU_PREFIX + "affiliate" );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.PERSON_FIRST_NAME );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.PERSON_LAST_NAME );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + "personMiddleName" );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.PERSON_LOCAL_PHONE_NUMBER );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.PERSON_CAMPUS_ADDRESS );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.PERSON_EMAIL_ADDRESS );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + KNSPropertyConstants.PERSON_BASE_SALARY_AMOUNT );
            auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + "financialSystemsEncryptedPasswordText" );
        } else {
            if ( !passwordEditingEnabled ) {
                auths.addHiddenAuthField( EMBEDDED_UU_PREFIX + "financialSystemsEncryptedPasswordText" );
            }
        }
        
        if ( !canEditFinancialSystemUserAttributes( user ) ) {
            auths.addReadonlyAuthField( "activeFinancialSystemUser" );
            auths.addReadonlyAuthField( "chartOfAccountsCode" );
            auths.addReadonlyAuthField( "organizationCode" );
            
            // iterate over the default organizations and security organizations to mark as read-only
            int index = 0;
            for ( FinancialSystemUserPrimaryOrganization item : ((FinancialSystemUser)document.getNewMaintainableObject().getBusinessObject()).getPrimaryOrganizations() ) {
                auths.addReadonlyAuthField( "primaryOrganizations[" + index + "].moduleId" );
                auths.addReadonlyAuthField( "primaryOrganizations[" + index + "].chartOfAccountsCode" );
                auths.addReadonlyAuthField( "primaryOrganizations[" + index + "].organizationCode" );
                index++;
            }
            index = 0;
            for ( FinancialSystemUserOrganizationSecurity item : ((FinancialSystemUser)document.getNewMaintainableObject().getBusinessObject()).getOrganizationSecurity() ) {
                auths.addReadonlyAuthField( "organizationSecurity[" + index + "].moduleId" );
                auths.addReadonlyAuthField( "organizationSecurity[" + index + "].chartOfAccountsCode" );
                auths.addReadonlyAuthField( "organizationSecurity[" + index + "].organizationCode" );
                auths.addReadonlyAuthField( "organizationSecurity[" + index + "].descendOrgHierarchy" );
                index++;
            }
        }

        return auths;
    }
   
    protected void initStatics() {
        if ( configService == null ) {
            parameterService = SpringContext.getBean(ParameterService.class);
            configService = KNSServiceLocator.getKualiConfigurationService();
        }
        if ( financialSystemUserEditWorkgroupName == null ) {
            // check whether users are editable within KFS
            universalUsersMaintainedByKuali = configService.getPropertyAsBoolean( KFSConstants.MAINTAIN_USERS_LOCALLY_KEY );
            // check whether KFS user data is editable within KFS
            financialSystemUsersMaintainedByKuali = configService.getPropertyAsBoolean( KFSConstants.MAINTAIN_KFS_USERS_LOCALLY_KEY );
            // check whether local CAS is in use
            passwordEditingEnabled = KNSServiceLocator.getWebAuthenticationService().isValidatePassword();
            // get the group name that we need here
            universalUserEditWorkgroupName = configService.getParameterValue(KNSConstants.KNS_NAMESPACE, KNSConstants.DetailTypes.UNIVERSAL_USER_DETAIL_TYPE, KFSConstants.CoreApcParms.UNIVERSAL_USER_EDIT_WORKGROUP_PARM);
            financialSystemUserEditWorkgroupName = parameterService.getParameterValue(FinancialSystemUser.class, KFSConstants.CoreApcParms.FINANCIAL_SYSTEM_USER_EDIT_WORKGROUP_PARM);
        }
    }
    
    public boolean canEditFinancialSystemUserAttributes( UniversalUser user ) {
        return financialSystemUsersMaintainedByKuali && user.isMember( financialSystemUserEditWorkgroupName );
    }

    public boolean canEditUniversalUserAttributes( UniversalUser user ) {
        return universalUsersMaintainedByKuali && user.isMember( universalUserEditWorkgroupName );
    }
    
}
