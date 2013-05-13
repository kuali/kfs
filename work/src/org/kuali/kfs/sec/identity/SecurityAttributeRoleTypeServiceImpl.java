/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.identity;

import java.util.Map;

import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

/**
 * Type service for roles created for security definitions. Each definition has a corresponding role which is granted the definition permissions
 */
public class SecurityAttributeRoleTypeServiceImpl extends RoleTypeServiceBase {





    /**
     * Any qualifier is allowed so that later on the qualification can be evaluated in the context of all qualifications for the user across all roles
     * 
     * @see org.kuali.rice.kim.service.support.impl.KimTypeInfoServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    protected boolean performMatch(Map<String,String> inputAttributeSet, Map<String,String> storedAttributeSet) {
        return true;
    }

}
