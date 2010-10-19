/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.identity.RoleTestBase;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;

public class CgKFSApplicationRoleTest extends RoleTestBase {

    public static final String ACCOUNT_DERIVED_AWARD_CHART = "BL";
    public static final String ACCOUNT_DERIVED_AWARD_ACCOUNT = "4831498";
    public static final String ACCOUNT_DERIVED_AWARD_PROJECT_DIRECTOR = "ahlers";
    
    public void testAccountDerivedRoleTypeService() {
        AccountService accountService = SpringContext.getBean(AccountService.class);
        AttributeSet roleQualifications = new AttributeSet();

        // 5. test award secondary director
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_AWARD_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_AWARD_ACCOUNT);
        
        assertUserIsRoleMember(getPrincipalIdByName(ACCOUNT_DERIVED_AWARD_PROJECT_DIRECTOR), 
                KFSConstants.ParameterNamespaces.KFS, 
                KFSConstants.SysKimConstants.AWARD_SECONDARY_DIRECTOR_KIM_ROLE_NAME, roleQualifications);
    }
    
}
