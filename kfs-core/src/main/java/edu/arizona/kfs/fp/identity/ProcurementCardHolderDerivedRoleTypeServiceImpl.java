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
package edu.arizona.kfs.fp.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

public class ProcurementCardHolderDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardHolderDerivedRoleTypeServiceImpl.class);
	
    public static final String PROCUREMENT_CARDHOLDER_USER_ID = "cardHolderSystemId";
    
    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting role member from derived role. Namespace code: " + namespaceCode + " , Role Name: "
					+ roleName);
		}
        List<RoleMembership> members = new ArrayList<RoleMembership>(1);
        if ((qualification != null) && StringUtils.isNotBlank(qualification.get(PROCUREMENT_CARDHOLDER_USER_ID))) {
            members.add(RoleMembership.Builder.create(null, null, qualification.get(PROCUREMENT_CARDHOLDER_USER_ID), MemberType.PRINCIPAL, null).build());
        }
        return members;
    }
}