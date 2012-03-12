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

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

public class ChartRoleTypeServiceImpl extends RoleTypeServiceBase {
    
    /***
     * @see org.kuali.rice.kim.service.support.impl.KimTypeInfoServiceBase#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        return StringUtils.equals(qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), 
                roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
    }

}
