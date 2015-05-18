/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.kim.role;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.role.RoleMembership;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This is the base class for all derived role type services 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated A krad integrated type service base class will be provided in the future.
 */
@Deprecated
public class DerivedRoleTypeServiceBase extends RoleTypeServiceBase {

	@Override
	public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

        if (roleName == null) {
            throw new RiceIllegalArgumentException("roleName was null");
        }

        return Collections.emptyList();
	}

	/**
	 * @see RoleTypeServiceBase#isDerivedRoleType()
	 */
	@Override
	public boolean isDerivedRoleType() {
		return true;
	}

}
