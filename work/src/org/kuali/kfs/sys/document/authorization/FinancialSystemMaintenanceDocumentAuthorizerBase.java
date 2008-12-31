/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.identity.KimAttributes;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;

/**
 * This class is the custom KFS maintenance document authorizer base class
 */
public class FinancialSystemMaintenanceDocumentAuthorizerBase extends MaintenanceDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(FinancialSystemMaintenanceDocumentAuthorizerBase.class);

    private static RoleService roleService;
// TODO fix for kim    
//    /**
//     * Adds settings for KFS maintenance-document-specific flags.
//     * 
//     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase#getDocumentActionFlags(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person)
//     */
//    @Override
//    public FinancialSystemDocumentActionFlags getDocumentActionFlags(Document document, Person user) {
//        if ( LOG.isDebugEnabled() ) {
//            LOG.debug("calling FinancialSystemMaintenanceDocumentAuthorizerBase.getDocumentActionFlags for document '" + document.getDocumentNumber() + "'. user '" + user.getPrincipalName() + "'");
//        }
//        FinancialSystemDocumentActionFlags flags =  new FinancialSystemDocumentActionFlags(super.getDocumentActionFlags(document, user));
//
//        // if document implements AmountTotaling interface, then we should display the total
//        if (document instanceof AmountTotaling) {
//            flags.setHasAmountTotal(true);
//        }
//        else {
//            flags.setHasAmountTotal(false);
//        }
//
//        return flags;
//    }

    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String,String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        Document document = (Document)businessObject;
        
        // get the KFS-SYS User qualifiers
        // get the namespace from the current qualifier set (set by the superclass)
        String namespaceCode = attributes.get(KimAttributes.NAMESPACE_CODE);
        Person initiator = getPersonService().getPersonByPrincipalName( document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId() );

        // build a new attribute set for checking the role service
        AttributeSet userRoleQualifiers = new AttributeSet();
        userRoleQualifiers.put(KimAttributes.NAMESPACE_CODE, namespaceCode);        
        List<AttributeSet> userRoleInfo = getRoleService().getRoleQualifiersForPrincipal(initiator.getPrincipalId(), 
                KFSConstants.ParameterNamespaces.KFS, KimConstants.KIM_ROLE_NAME_USER, userRoleQualifiers);

        if ( userRoleInfo != null && !userRoleInfo.isEmpty() ) {
            attributes.put(KimAttributes.CHART_OF_ACCOUNTS_CODE, userRoleInfo.get(0).get(KimAttributes.CHART_OF_ACCOUNTS_CODE));
            attributes.put(KimAttributes.ORGANIZATION_CODE, userRoleInfo.get(0).get(KimAttributes.ORGANIZATION_CODE));
        }
        attributes.put(KimAttributes.CAMPUS_CODE, initiator.getCampusCode());
        
    }

    public static RoleService getRoleService() {
        if ( roleService == null ) {
            roleService = KIMServiceLocator.getRoleManagementService();
        }
        return roleService;
    }

}

