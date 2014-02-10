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
