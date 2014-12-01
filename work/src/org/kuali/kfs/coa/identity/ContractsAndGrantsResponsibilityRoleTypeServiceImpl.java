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
