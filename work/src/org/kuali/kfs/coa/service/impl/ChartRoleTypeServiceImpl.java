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
package org.kuali.kfs.coa.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

public class ChartRoleTypeServiceImpl extends KimRoleTypeServiceBase {

    @Override
    public boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        if (!qualification.containsKey(KimConstants.KIM_ATTRIB_CHART_CODE) || !roleQualifier.containsKey(KimConstants.KIM_ATTRIB_CHART_CODE)) {
            throw new RuntimeException("Chart of accounts code not found in qualifier.");
        }
        return StringUtils.equals(qualification.get(KimConstants.KIM_ATTRIB_CHART_CODE), roleQualifier.get(KimConstants.KIM_ATTRIB_CHART_CODE));
    }

}
