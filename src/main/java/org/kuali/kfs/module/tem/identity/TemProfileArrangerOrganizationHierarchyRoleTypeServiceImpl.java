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
package org.kuali.kfs.module.tem.identity;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.identity.KfsKimAttributes;


public class TemProfileArrangerOrganizationHierarchyRoleTypeServiceImpl extends TemProfileOrganizationHierarchyRoleTypeServiceImpl {

    /**
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#performMatch(java.util.Map, java.util.Map)
     */
    @Override
    protected boolean performMatch(Map<String, String> inputAttributes, Map<String, String> storedAttributes) {

        if (inputAttributes == null || inputAttributes.isEmpty() || (!inputAttributes.containsKey(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) && !inputAttributes.containsKey(KfsKimAttributes.ORGANIZATION_CODE))) {
            return true;
        }
        String orgChartOfAccountsCode = inputAttributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = inputAttributes.get(KfsKimAttributes.ORGANIZATION_CODE);
        String roleChartOfAccountsCode = storedAttributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String roleOrganizationCode = storedAttributes.get(KfsKimAttributes.ORGANIZATION_CODE);
        boolean descendHierarchy = StringUtils.equalsIgnoreCase(storedAttributes.get(KfsKimAttributes.DESCEND_HIERARCHY), DESCEND_HIERARCHY_TRUE_VALUE);

        return isParentOrg(orgChartOfAccountsCode, organizationCode, roleChartOfAccountsCode, roleOrganizationCode, descendHierarchy);
    }
}
