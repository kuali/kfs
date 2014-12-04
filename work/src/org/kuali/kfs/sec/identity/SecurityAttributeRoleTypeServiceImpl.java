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
