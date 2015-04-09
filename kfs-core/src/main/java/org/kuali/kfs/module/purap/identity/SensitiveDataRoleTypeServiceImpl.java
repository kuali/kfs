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
package org.kuali.kfs.module.purap.identity;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;


public class SensitiveDataRoleTypeServiceImpl extends RoleTypeServiceBase {
    





    protected boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        if ( qualification == null ) {
            return false;
        }
        String[] codes = StringUtils.split(qualification.get(PurapKimAttributes.SENSITIVE_DATA_CODE),';');
        if ( codes == null ) {
            return false;
        }
        return Arrays.asList( codes ).contains(roleQualifier.get(PurapKimAttributes.SENSITIVE_DATA_CODE));
    }
}
