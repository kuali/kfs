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
