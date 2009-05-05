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
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;


/**
 * This role type service was designed to be attached to the KFS-SYS User role
 */
public class FinancialSystemUserRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    public static final String FINANCIAL_SYSTEM_USER_ROLE_NAME = "User";
    public static final String PERFORM_QUALIFIER_MATCH = "performQualifierMatch";

    protected List<String> roleQualifierRequiredAttributes = new ArrayList<String>();
    protected List<String> qualificationRequiredAttributes = new ArrayList<String>();
    {
        roleQualifierRequiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        roleQualifierRequiredAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);
        // roleQualifierRequiredAttributes.add(KfsCoreKimAttributes.NAMESPACE_CODE);

        qualificationRequiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        qualificationRequiredAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);
        // qualificationRequiredAttributes.add(KfsCoreKimAttributes.NAMESPACE_CODE);
    }

    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        validateRequiredAttributesAgainstReceived(roleQualifierRequiredAttributes, roleQualifier, ROLE_QUALIFIERS_RECEIVED_ATTIBUTES_NAME);
        validateRequiredAttributesAgainstReceived(qualificationRequiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);

        // if we can not find the qualifier which tells us to perform the match, just return true
        if (qualification == null || !Boolean.parseBoolean(qualification.get(PERFORM_QUALIFIER_MATCH))) {
            return true;
        }
        // if chart and org aren't on the assignment we don't care, since we are only matching to get the default chart/org for a
        // user in FinancialSystemUserServiceImpl
        if (StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) || StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE))) {
            return false;
        }
        // exact match on namespace or qualifier namespace of kfs-sys works
        if (StringUtils.equals(roleQualifier.get(KfsKimAttributes.NAMESPACE_CODE), qualification.get(KfsKimAttributes.NAMESPACE_CODE)) || StringUtils.equals(KFSConstants.ParameterNamespaces.KFS, roleQualifier.get(KfsKimAttributes.NAMESPACE_CODE))) {
            return true;
        }
        return false;
    }

    @Override
    public List<RoleMembershipInfo> doRoleQualifiersMatchQualification(AttributeSet qualification, List<RoleMembershipInfo> roleMemberList) {
        // if we can not find the qualifier which tells us to perform the match, just return all rows
        if (qualification == null || !Boolean.parseBoolean(qualification.get(PERFORM_QUALIFIER_MATCH))) {
            return roleMemberList;
        }
        // perform special matching to make a match that includes the namespace take priority
        RoleMembershipInfo namespaceMatch = null;
        RoleMembershipInfo nonNamespaceMatch = null;
        // look for match
        for (RoleMembershipInfo rmi : roleMemberList) {
            // if chart and org aren't on the assignment we don't care, since we are only matching to get the default chart/org for
            // a user in FinancialSystemUserServiceImpl
            if (!(StringUtils.isBlank(rmi.getQualifier().get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) || StringUtils.isBlank(rmi.getQualifier().get(KfsKimAttributes.ORGANIZATION_CODE)))) {
                if (StringUtils.equals(qualification.get(KfsKimAttributes.NAMESPACE_CODE), rmi.getQualifier().get(KfsKimAttributes.NAMESPACE_CODE))) {
                    namespaceMatch = rmi;
                }
                else if (StringUtils.equals(KFSConstants.ParameterNamespaces.KFS, rmi.getQualifier().get(KfsKimAttributes.NAMESPACE_CODE))) {
                    nonNamespaceMatch = rmi;
                }
            }
        }
        List<RoleMembershipInfo> matchingMemberships = new ArrayList<RoleMembershipInfo>(1);
        if (nonNamespaceMatch != null || namespaceMatch != null) {
            matchingMemberships.add((namespaceMatch != null) ? namespaceMatch : nonNamespaceMatch);
        }
        return matchingMemberships;
    }
}
