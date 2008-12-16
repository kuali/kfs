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
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

public class CampusRoleTypeServiceImpl extends KimRoleTypeServiceBase {

    protected List<String> requiredAttributes = new ArrayList<String>();
    {
        requiredAttributes.add(KimAttributes.CAMPUS_CODE);
    }
    
    @Override
    public boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        validateRequiredAttributesAgainstReceived(requiredAttributes, qualification, QUALIFICATION_RECEIVED_ATTIBUTES_NAME);
        validateRequiredAttributesAgainstReceived(requiredAttributes, roleQualifier, ROLE_QUALIFIERS_RECEIVED_ATTIBUTES_NAME);

        return StringUtils.equals(
                qualification.get(KimAttributes.CAMPUS_CODE), roleQualifier.get(KimAttributes.CAMPUS_CODE));
    }

}
