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
package org.kuali.kfs.module.cg.identity;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.identity.RoleTestBase;

public class CgKFSApplicationRoleTest extends RoleTestBase {

    public static final String ACCOUNT_DERIVED_AWARD_CHART = "BL";
    public static final String ACCOUNT_DERIVED_AWARD_ACCOUNT = "4831498";
    public static final String ACCOUNT_DERIVED_AWARD_PROJECT_DIRECTOR = "ahlers";

    public void testAccountDerivedRoleTypeService() {
        AccountService accountService = SpringContext.getBean(AccountService.class);
        Map<String,String> roleQualifications = new HashMap<String,String>();

        // 5. test award secondary director
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_AWARD_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_AWARD_ACCOUNT);

        assertUserIsRoleMember(getPrincipalIdByName(ACCOUNT_DERIVED_AWARD_PROJECT_DIRECTOR),
                KFSConstants.ParameterNamespaces.KFS,
                KFSConstants.SysKimApiConstants.AWARD_SECONDARY_DIRECTOR_KIM_ROLE_NAME, roleQualifications);
    }

}
