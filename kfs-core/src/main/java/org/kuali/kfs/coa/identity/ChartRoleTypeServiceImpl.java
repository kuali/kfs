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
