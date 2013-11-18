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
