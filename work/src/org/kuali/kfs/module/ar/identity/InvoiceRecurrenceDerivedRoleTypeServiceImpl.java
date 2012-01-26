/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

public class InvoiceRecurrenceDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Map<String,String> qualification) {
        List<RoleMembership> members = new ArrayList<RoleMembership>(1);
        if ((qualification != null && !qualification.isEmpty()) && StringUtils.isNotBlank(qualification.get(ArPropertyConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_INITIATOR_USER_ID))) {
            RoleMembership.Builder builder = RoleMembership.Builder.create(null, null, ArPropertyConstants.InvoiceRecurrenceFields.INVOICE_RECURRENCE_INITIATOR_USER_ID, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE, null);
            members.add(builder.build());
        }
        return members;
    }
}
