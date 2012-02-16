/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.identity;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

public class TemProfileArrangerOrganizationHierarchyRoleTypeServiceImpl extends TemOrganizationHierarchyRoleTypeService {

    public static final String DESCEND_HIERARCHY_TRUE_VALUE = "Y";
    public static final String DESCEND_HIERARCHY_FALSE_VALUE = "N";

    {
        requiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        requiredAttributes.add(KfsKimAttributes.DESCEND_HIERARCHY);
        requiredAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);
        checkRequiredAttributes = false;
    }

    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        
//        // if no qualification given but the user is assigned an organization then return they have the role
//        if ((ObjectUtils.isNull(qualification) || qualification.isEmpty()) && 
//             ObjectUtils.isNotNull(roleQualifier) && !roleQualifier.isEmpty()) {
//            return true;
//        }
//
//        // if they don't have a qualification then return false for the role
//        if (qualification == null || qualification.isEmpty() || roleQualifier == null || roleQualifier.isEmpty()) {
//            return false;
//        }

     // if we can not find the qualifier which tells us to perform the match, just return true
        if (qualification == null || !Boolean.parseBoolean(qualification.get(PERFORM_QUALIFIER_MATCH))) {
            return true;
        }
        String orgChartOfAccountsCode = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = qualification.get(KfsKimAttributes.ORGANIZATION_CODE);
        String roleChartOfAccountsCode = roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String roleOrganizationCode = roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE);
        boolean descendHierarchy = StringUtils.equalsIgnoreCase(roleQualifier.get(KfsKimAttributes.DESCEND_HIERARCHY), DESCEND_HIERARCHY_TRUE_VALUE);
        
        return isParentOrg(orgChartOfAccountsCode, organizationCode, roleChartOfAccountsCode, roleOrganizationCode, descendHierarchy);        
    }
    

    @Override
    public AttributeDefinitionMap getAttributeDefinitions(String arg0) {
        // TODO Auto-generated method stub
        return super.getAttributeDefinitions(arg0);
    }


}
