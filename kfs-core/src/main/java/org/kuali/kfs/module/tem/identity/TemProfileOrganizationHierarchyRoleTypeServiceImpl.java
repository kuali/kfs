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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.identity.KfsKimAttributes;

@SuppressWarnings("deprecation")
public class TemProfileOrganizationHierarchyRoleTypeServiceImpl extends TemOrganizationHierarchyRoleTypeService {

    public static final String DESCEND_HIERARCHY_TRUE_VALUE = "Y";
    public static final String DESCEND_HIERARCHY_FALSE_VALUE = "N";

    /**
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#getRequiredAttributes()
     */
    @Override
    protected List<String> getRequiredAttributes() {
        final List<String> attrs = new ArrayList<String>(super.getRequiredAttributes());
        attrs.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        attrs.add(KfsKimAttributes.DESCEND_HIERARCHY);
        attrs.add(KfsKimAttributes.ORGANIZATION_CODE);
        return Collections.unmodifiableList(attrs);
    }

    /**
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#performMatch(java.util.Map, java.util.Map)
     */
    @Override
    protected boolean performMatch(Map<String, String> inputAttributes, Map<String, String> storedAttributes) {

        if (inputAttributes == null  || (inputAttributes.containsKey(PERFORM_QUALIFIER_MATCH) && !Boolean.parseBoolean(inputAttributes.get(PERFORM_QUALIFIER_MATCH)) )) {
            return true;
        }
        String orgChartOfAccountsCode = inputAttributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = inputAttributes.get(KfsKimAttributes.ORGANIZATION_CODE);
        String roleChartOfAccountsCode = storedAttributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String roleOrganizationCode = storedAttributes.get(KfsKimAttributes.ORGANIZATION_CODE);
        boolean descendHierarchy = StringUtils.equalsIgnoreCase(storedAttributes.get(KfsKimAttributes.DESCEND_HIERARCHY), DESCEND_HIERARCHY_TRUE_VALUE);

        final boolean parentOrg = isParentOrg(orgChartOfAccountsCode, organizationCode, roleChartOfAccountsCode, roleOrganizationCode, descendHierarchy);
        return parentOrg;
    }
}
