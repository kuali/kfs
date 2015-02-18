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
