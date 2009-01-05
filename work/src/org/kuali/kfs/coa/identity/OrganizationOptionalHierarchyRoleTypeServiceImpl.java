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

import org.kuali.kfs.sys.identity.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

public class OrganizationOptionalHierarchyRoleTypeServiceImpl extends OrganizationHierarchyAwareRoleTypeServiceBase {
    public static final String DESCEND_HIERARCHY_TRUE_VALUE = "Y";
    public static final String DESCEND_HIERARCHY_FALSE_VALUE = "N";
    private boolean qualificationDeterminesDescendHierarchy;
    {
        if (qualificationDeterminesDescendHierarchy) {
            qualificationRequiredAttributes.add(KimAttributes.DESCEND_HIERARCHY);
        }
        else {
            roleQualifierRequiredAttributes.add(KimAttributes.DESCEND_HIERARCHY);
        }
    }

    /***
     * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
     *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        validateRequiredAttributesAgainstReceived(qualificationRequiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        validateRequiredAttributesAgainstReceived(roleQualifierRequiredAttributes, roleQualifier, ROLE_QUALIFIERS_RECEIVED_ATTIBUTES_NAME);
        String descendHierarchy = null;
        if (qualificationDeterminesDescendHierarchy) {
            descendHierarchy = qualification.get(KimAttributes.DESCEND_HIERARCHY);
        }
        else {
            descendHierarchy = roleQualifier.get(KimAttributes.DESCEND_HIERARCHY);
        }
        return isParentOrg(qualification.get(KimAttributes.CHART_OF_ACCOUNTS_CODE), 
                qualification.get(KimAttributes.ORGANIZATION_CODE), 
                roleQualifier.get(KimAttributes.CHART_OF_ACCOUNTS_CODE), 
                roleQualifier.get(KimAttributes.ORGANIZATION_CODE), DESCEND_HIERARCHY_TRUE_VALUE.equals(descendHierarchy));
    }

    public void setQualificationDeterminesDescendHierarchy(boolean qualificationDeterminesDescendHierarchy) {
        this.qualificationDeterminesDescendHierarchy = qualificationDeterminesDescendHierarchy;
    }
}