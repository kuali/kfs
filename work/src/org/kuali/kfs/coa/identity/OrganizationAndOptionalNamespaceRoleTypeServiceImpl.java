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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.config.KIMConfigurer;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

/**
 * This role type service was designed to be attached to the KFS-SYS User role.
 * This class...
 */
public class OrganizationAndOptionalNamespaceRoleTypeServiceImpl extends KimRoleTypeServiceBase {

    protected List<String> roleQualifierRequiredAttributes = new ArrayList<String>();
    protected List<String> qualificationRequiredAttributes = new ArrayList<String>();
    {
        roleQualifierRequiredAttributes.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        roleQualifierRequiredAttributes.add(KFSPropertyConstants.ORGANIZATION_CODE);        
        //roleQualifierRequiredAttributes.add(KfsCoreKimAttributes.NAMESPACE_CODE);

        qualificationRequiredAttributes.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        qualificationRequiredAttributes.add(KFSPropertyConstants.ORGANIZATION_CODE);        
        //qualificationRequiredAttributes.add(KfsCoreKimAttributes.NAMESPACE_CODE);
    }
    
    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        validateRequiredAttributesAgainstReceived(
                roleQualifierRequiredAttributes, roleQualifier, ROLE_QUALIFIERS_RECEIVED_ATTIBUTES_NAME);
        validateRequiredAttributesAgainstReceived(
                qualificationRequiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        
        String chart = qualification.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String orgCode = qualification.get(KFSPropertyConstants.ORGANIZATION_CODE);
        String namespace = qualification.get(KimAttributes.NAMESPACE_CODE);
        
        String roleChart = roleQualifier.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String roleOrgCode = roleQualifier.get(KFSPropertyConstants.ORGANIZATION_CODE);
        String roleNamespace = roleQualifier.get(KimAttributes.NAMESPACE_CODE);

        // check if the chart/org match
        // if so, do an optional namespace match - if is specified on both, then must match
        if ( StringUtils.equals(chart, roleChart) && StringUtils.equals(orgCode, roleOrgCode)) {
            if ( StringUtils.isBlank(namespace) || StringUtils.isBlank(roleNamespace) ) {
                return true;
            } else if ( StringUtils.equals(namespace, roleNamespace) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<RoleMembershipInfo> doRoleQualifiersMatchQualification(AttributeSet qualification, List<RoleMembershipInfo> roleMemberList) {
        // perform special matching to make a match that includes the namespace take priority
        RoleMembershipInfo namespaceMatch = null;
        RoleMembershipInfo nonNamespaceMatch = null;
        String chart = qualification.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String orgCode = qualification.get(KFSPropertyConstants.ORGANIZATION_CODE);
        String namespace = qualification.get(KimAttributes.NAMESPACE_CODE);
        // look for match
        for ( RoleMembershipInfo rmi : roleMemberList ) {
            String roleChart = rmi.getQualifier().get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            String roleOrgCode = rmi.getQualifier().get(KFSPropertyConstants.ORGANIZATION_CODE);
            String roleNamespace = rmi.getQualifier().get(KimAttributes.NAMESPACE_CODE);
            
            if ( StringUtils.equals(chart, roleChart) 
                    && StringUtils.equals(orgCode, roleOrgCode) ) {
                // check for an exact namespace match
                if ( StringUtils.equals(namespace, roleNamespace) ) {
                    namespaceMatch = rmi;
                    break; // namespace matches will take precedence, so skip further checks 
                // check for a blank namespace if the qualifier is not blank
                } else if ( StringUtils.isNotBlank(namespace) && StringUtils.isBlank(roleNamespace) ) {
                    nonNamespaceMatch = rmi;
                }
            }            
        }
        List<RoleMembershipInfo> matchingMemberships = new ArrayList<RoleMembershipInfo>( 1 );
        if ( nonNamespaceMatch != null || namespaceMatch != null ) {
            matchingMemberships.add((namespaceMatch!=null)?namespaceMatch:nonNamespaceMatch);
        }
        return matchingMemberships;
    }
    
}
