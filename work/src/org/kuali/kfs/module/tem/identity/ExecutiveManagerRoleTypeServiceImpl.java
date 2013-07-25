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
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

@SuppressWarnings("deprecation")
public class ExecutiveManagerRoleTypeServiceImpl extends RoleTypeServiceBase{

    /**
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#performMatch(java.util.Map, java.util.Map)
     */
    @Override
    protected boolean performMatch(Map<String, String> inputAttributes, Map<String, String> storedAttributes) {
        final String jobClassificationFromCheckInput = inputAttributes.get(TemKimAttributes.JOB_CLASSIFICATION_CODE);
        final String jobClassificationFromRoleQualification = storedAttributes.get(TemKimAttributes.JOB_CLASSIFICATION_CODE);
        if (StringUtils.isBlank(jobClassificationFromRoleQualification)) {
            return true; // members with blank role qualifications always match
        } else if (jobClassificationFromRoleQualification.equals(jobClassificationFromCheckInput)){
            return true; // the classification codes matched
        }
        return false;
    }
}
