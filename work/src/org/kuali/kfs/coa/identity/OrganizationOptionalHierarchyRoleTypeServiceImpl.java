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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.identity.KfsKimAttributes;

public class OrganizationOptionalHierarchyRoleTypeServiceImpl extends OrganizationHierarchyAwareRoleTypeServiceBase {
    public static final String DESCEND_HIERARCHY_TRUE_VALUE = "Y";
    public static final String DESCEND_HIERARCHY_FALSE_VALUE = "N";
    public static final List<String> TRUE_VALUES = Arrays.asList(new String[] { "yes", "y", "true", "t", "on", "1", "enabled" });
    public static final List<String> FALSE_VALUES = Arrays.asList(new String[] { "no", "n", "false", "f", "off", "0", "disabled" });
    
    private boolean qualificationDeterminesDescendHierarchy;

    /***
     * @see org.kuali.rice.kim.service.support.impl.KimTypeInfoServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
     *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    protected boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        // if no qualification is passed, then we have no basis to reject this
        // (if a null is let through, then we get an NPE below)
        if ( qualification == null || qualification.isEmpty() || roleQualifier == null || roleQualifier.isEmpty() ) {
            return true;
        }
        String descendHierarchy = null;
        if (qualificationDeterminesDescendHierarchy) {
            descendHierarchy = qualification.get(KfsKimAttributes.DESCEND_HIERARCHY);
        }
        else {
            descendHierarchy = roleQualifier.get(KfsKimAttributes.DESCEND_HIERARCHY);
        }
        descendHierarchy = descendHierarchy == null ? DESCEND_HIERARCHY_FALSE_VALUE : descendHierarchy;
        
        return isParentOrg(qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE),
                qualification.get(KfsKimAttributes.ORGANIZATION_CODE),
                roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE),
                roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE), TRUE_VALUES.contains(descendHierarchy.toLowerCase()));
    }

    public void setQualificationDeterminesDescendHierarchy(boolean qualificationDeterminesDescendHierarchy) {
        this.qualificationDeterminesDescendHierarchy = qualificationDeterminesDescendHierarchy;
    }


}
