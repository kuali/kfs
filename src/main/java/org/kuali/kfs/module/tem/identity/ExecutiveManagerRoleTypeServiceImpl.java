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
