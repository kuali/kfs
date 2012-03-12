/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.coa.document.authorization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Document Authorizer for the Organization document.
 */
public class OrganizationDocumentAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationDocumentAuthorizer.class);
    
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActions) {
        Set<String> myDocumentActions = super.getDocumentActions(document, user, documentActions);

        if (checkPlantAttributes(document)) {
            myDocumentActions.remove(KRADConstants.KUALI_ACTION_CAN_BLANKET_APPROVE);
        }

        return myDocumentActions;
    }

    /**
     * This checks to see if a user is authorized for plant fields modification. If not then it returns true (without activating
     * fields). If the org does not have to report to itself then it checks to see if the plant fields have been filled out
     * correctly and fails if they haven't
     * 
     * @return false if user can edit plant fields but they have not been filled out correctly
     */
    protected boolean checkPlantAttributes(Document document) {
        // get user
        Person user = GlobalVariables.getUserSession().getPerson();

        // if not authorized to edit plant fields, exit with true
        if (isPlantAuthorized(user, document) == false) {
            return true;
        }

        return false;
    }

    /**
     * This method tests whether the specified user is part of the group that grants authorization to the Plant fields.
     * 
     * @param user - the user to test, document to get plant fund account
     * @return true if user is part of the group, false otherwise
     */
    protected boolean isPlantAuthorized(Person user, Document document) {
        String principalId = user.getPrincipalId();
        String namespaceCode = KFSConstants.ParameterNamespaces.KNS;
        String permissionTemplateName = KimConstants.PermissionTemplateNames.MODIFY_FIELD;
        
        Map<String,String> roleQualifiers = new HashMap<String,String>();

        Map<String,String> permissionDetails = new HashMap<String,String>();
        permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, Organization.class.getSimpleName());
        permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, KFSPropertyConstants.ORGANIZATION_PLANT_ACCOUNT_NUMBER);

        IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        Boolean isAuthorized = identityManagementService.isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, roleQualifiers);
        if (!isAuthorized) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("User '" + user.getPrincipalName() + "' has no access to the Plant Chart.");
            }
        }
        else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("User '" + user.getPrincipalName() + "' has access to the Plant fields.");
            }
        }

        return isAuthorized;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected void addRoleQualification(Object dataObject, Map<String, String> attributes) {
        super.addRoleQualification(dataObject, attributes);

        if (dataObject instanceof MaintenanceDocument) {
            MaintenanceDocument maintDoc = (MaintenanceDocument)dataObject;
            if ( maintDoc.getNewMaintainableObject() != null ) {
                Organization newOrg = (Organization) maintDoc.getNewMaintainableObject().getBusinessObject();
                if (!StringUtils.isBlank(newOrg.getChartOfAccountsCode())) {
                    attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, newOrg.getChartOfAccountsCode());
                }
            }
        }
        else if (dataObject instanceof Organization) {
            Organization newOrg = (Organization) dataObject;
            if (!StringUtils.isBlank(newOrg.getChartOfAccountsCode())) {
                attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, newOrg.getChartOfAccountsCode());
            }
        }  
    } 
}
