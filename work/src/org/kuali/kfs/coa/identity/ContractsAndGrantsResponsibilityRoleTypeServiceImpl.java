/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

public class ContractsAndGrantsResponsibilityRoleTypeServiceImpl extends RoleTypeServiceBase {
    public static final String AWARD_ROUTE_NODE_NAME = "Award";

    @Override
    public List<RoleMembership> getMatchingRoleMemberships(Map<String,String> qualification, List<RoleMembership> roleMemberList) {
        // special handling for where the code is being called for a particular route node
        // and the code is blank
        if ( qualification != null ) {
            // this logic is here for Account docs, not ST docs where it interferes with the ability of C&G Processors
            // to edit object codes on target accounting lines
            if ( !StringUtils.equals( qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME), KFSConstants.FinancialDocumentTypeCodes.SALARY_EXPENSE_TRANSFER) ) {
                if ( StringUtils.equals( qualification.get(KimConstants.AttributeConstants.ROUTE_NODE_NAME), AWARD_ROUTE_NODE_NAME) ) {
                    if ( StringUtils.isBlank(qualification.get(KfsKimAttributes.CONTRACTS_AND_GRANTS_ACCOUNT_RESPONSIBILITY_ID)) ) {
                        return new ArrayList<RoleMembership>(0);
                    }
                }
            }
        }
        // otherwise, default to the normal behavior
        return super.getMatchingRoleMemberships(qualification, roleMemberList);
    }
}
