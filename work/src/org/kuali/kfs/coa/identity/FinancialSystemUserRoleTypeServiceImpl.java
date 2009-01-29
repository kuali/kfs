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
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;


/**
 * This role type service was designed to be attached to the KFS-SYS User role.
 * This class...
 */
public class FinancialSystemUserRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    public static final String FINANCIAL_SYSTEM_USER_ROLE_NAME = "User";
    public static final String PERFORM_QUALIFIER_MATCH = "performQualifierMatch";

    protected List<String> roleQualifierRequiredAttributes = new ArrayList<String>();
    protected List<String> qualificationRequiredAttributes = new ArrayList<String>();
    {
        roleQualifierRequiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        roleQualifierRequiredAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);        
        //roleQualifierRequiredAttributes.add(KfsCoreKimAttributes.NAMESPACE_CODE);

        qualificationRequiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        qualificationRequiredAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);        
        //qualificationRequiredAttributes.add(KfsCoreKimAttributes.NAMESPACE_CODE);
    }
    
    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        validateRequiredAttributesAgainstReceived(
                roleQualifierRequiredAttributes, roleQualifier, ROLE_QUALIFIERS_RECEIVED_ATTIBUTES_NAME);
        validateRequiredAttributesAgainstReceived(
                qualificationRequiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        
        // if we can not find the qualifier which tells us to perform the match, just return true
        if ( qualification == null || 
                !Boolean.parseBoolean( qualification.get(PERFORM_QUALIFIER_MATCH) ) ) {
            return true;
        }
        String chart = "";
        String orgCode = "";
        String namespace = "";
        if ( qualification != null ) {
            chart = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            orgCode = qualification.get(KfsKimAttributes.ORGANIZATION_CODE);
            namespace = qualification.get(KfsKimAttributes.NAMESPACE_CODE);
        }
        
        String roleChart = roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String roleOrgCode = roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE);
        String roleNamespace = roleQualifier.get(KfsKimAttributes.NAMESPACE_CODE);

        // check if the chart/org match
        // if so, do an optional namespace match - if is specified on both, then must match
        if ( StringUtils.equals(chart, roleChart) && StringUtils.equals(orgCode, roleOrgCode) || (StringUtils.isBlank(chart) && StringUtils.isBlank(orgCode)) ) {
            if ( StringUtils.isBlank(namespace) || StringUtils.isBlank(roleNamespace) ) {
                return true;
            } else if ( StringUtils.equals(namespace, roleNamespace) || (StringUtils.isBlank(roleNamespace) && StringUtils.isBlank(namespace)) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<RoleMembershipInfo> doRoleQualifiersMatchQualification(AttributeSet qualification, List<RoleMembershipInfo> roleMemberList) {
        // if we can not find the qualifier which tells us to perform the match, just return all rows
        if ( qualification == null || 
                !Boolean.parseBoolean( qualification.get(PERFORM_QUALIFIER_MATCH) ) ) {
            return roleMemberList;
        }
        // perform special matching to make a match that includes the namespace take priority
        RoleMembershipInfo namespaceMatch = null;
        RoleMembershipInfo nonNamespaceMatch = null;
        String chart = "";
        String orgCode = "";
        String namespace = "";
        if ( qualification != null ) {
            chart = qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            orgCode = qualification.get(KfsKimAttributes.ORGANIZATION_CODE);
            namespace = qualification.get(KfsKimAttributes.NAMESPACE_CODE);
        }
        // look for match
        for ( RoleMembershipInfo rmi : roleMemberList ) {
            String roleChart = rmi.getQualifier().get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            String roleOrgCode = rmi.getQualifier().get(KfsKimAttributes.ORGANIZATION_CODE);
            String roleNamespace = rmi.getQualifier().get(KfsKimAttributes.NAMESPACE_CODE);
            if ( StringUtils.equals(namespace, roleNamespace) || (StringUtils.isBlank(roleNamespace) && StringUtils.isBlank(namespace)) ) {
                // check chart org for exact namespace;
                if ( (StringUtils.equals(chart, roleChart) 
                        && StringUtils.equals(orgCode, roleOrgCode))
                        || (StringUtils.isBlank(chart) && StringUtils.isBlank(orgCode)) ) {
                    namespaceMatch = rmi;
                }             
                nonNamespaceMatch = null;
                break;
            } else if ( StringUtils.isNotBlank(namespace) && StringUtils.isBlank(roleNamespace) ) {
                if ( (StringUtils.equals(chart, roleChart) 
                        && StringUtils.equals(orgCode, roleOrgCode))
                        || (StringUtils.isBlank(chart) && StringUtils.isBlank(orgCode)) ) {
                    nonNamespaceMatch = rmi;
                }                
            }
            // compare the chart/org, but also match if none were passed
//            if ( (StringUtils.equals(chart, roleChart) 
//                    && StringUtils.equals(orgCode, roleOrgCode))
//                    || (StringUtils.isBlank(chart) && StringUtils.isBlank(orgCode)) ) {
//                // check for an exact namespace match (also match null and empty strings)
//                if ( StringUtils.equals(namespace, roleNamespace) || (StringUtils.isBlank(roleNamespace) && StringUtils.isBlank(namespace)) ) {
//                    namespaceMatch = rmi;
//                    break; // namespace matches will take precedence, so skip further checks 
//                // check for a blank namespace if the qualifier is not blank
//                } else if ( StringUtils.isNotBlank(namespace) && StringUtils.isBlank(roleNamespace) ) {
//                    nonNamespaceMatch = rmi;
//                }
//            }            
        }
        List<RoleMembershipInfo> matchingMemberships = new ArrayList<RoleMembershipInfo>( 1 );
        if ( nonNamespaceMatch != null || namespaceMatch != null ) {
            matchingMemberships.add((namespaceMatch!=null)?namespaceMatch:nonNamespaceMatch);
        }
        return matchingMemberships;
    }
    
}
