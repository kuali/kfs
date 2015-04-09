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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.identity.RoleTestBase;
import org.kuali.rice.kim.api.KimConstants;

@ConfigureContext
public class PurapKFSApplicationRoleTest extends RoleTestBase {    
    public static final String PURAP_NAMESPACE = "KFS-PURAP";

    public static final String STRAIGHT_COMMODITY_USER = "season";
    public static final String WILDCARD_COMMODITY_USER = "cdbookma";
    public static final String BAD_WILDCARD_COMMODITY_USER = "fwillhit";
    public static final String COMMODITY_CAMPUS = "BL";
    public static final String COMMODITY_CODE = "1113";
    public static final String COMMODITY_REVIEWER_ROLE_NAME = "Commodity Reviewer";
    
    public void testCommodityReviewRoleTypeService() {
        Map<String,String> roleQualifiers = new HashMap<String,String>();
        roleQualifiers.put(KimConstants.AttributeConstants.CAMPUS_CODE, COMMODITY_CAMPUS);
        roleQualifiers.put(KfsKimAttributes.PURCHASING_COMMODITY_CODE, COMMODITY_CODE);
        
        assertUserIsRoleMember(getPrincipalIdByName(STRAIGHT_COMMODITY_USER), PURAP_NAMESPACE, COMMODITY_REVIEWER_ROLE_NAME, roleQualifiers);
        assertUserIsRoleMember(getPrincipalIdByName(WILDCARD_COMMODITY_USER), PURAP_NAMESPACE, COMMODITY_REVIEWER_ROLE_NAME, roleQualifiers);
        assertUserIsNotRoleMember(getPrincipalIdByName(BAD_WILDCARD_COMMODITY_USER), PURAP_NAMESPACE, COMMODITY_REVIEWER_ROLE_NAME, roleQualifiers);
    }
    
    public Map<String,String> buildRoleQualificationForSensitiveData(String sensitiveDataCode) {
        Map<String,String> roleQualification = new HashMap<String,String>();
        roleQualification.put(PurapKimAttributes.SENSITIVE_DATA_CODE, sensitiveDataCode);
        return roleQualification;
    }

    public static final String SENSITIVE_DATA_1 = "ANIM";
    public static final String SENSITIVE_DATA_2 = "RADI";
    public static final String SENSITIVE_DATA_3 = "ANIM;RADI";
    public static final String SENSITIVE_DATA_REVIEWER = "bhhallow";
    public static final String SENSITIVE_DATA_ROLE_NAME = "Sensitive Data Viewer";

    public void testSensitiveDataRoleTypeService() {
        assertUserIsRoleMember(getPrincipalIdByName(SENSITIVE_DATA_REVIEWER), PURAP_NAMESPACE, SENSITIVE_DATA_ROLE_NAME, buildRoleQualificationForSensitiveData(SENSITIVE_DATA_1));
        assertUserIsNotRoleMember(getPrincipalIdByName(SENSITIVE_DATA_REVIEWER), PURAP_NAMESPACE, SENSITIVE_DATA_ROLE_NAME, buildRoleQualificationForSensitiveData(SENSITIVE_DATA_2));
        assertUserIsRoleMember(getPrincipalIdByName(SENSITIVE_DATA_REVIEWER), PURAP_NAMESPACE, SENSITIVE_DATA_ROLE_NAME, buildRoleQualificationForSensitiveData(SENSITIVE_DATA_3));
    }
}
