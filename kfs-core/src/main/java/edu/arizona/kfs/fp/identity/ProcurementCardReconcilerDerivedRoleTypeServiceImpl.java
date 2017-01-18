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
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

@SuppressWarnings("deprecation")
public class ProcurementCardReconcilerDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {
    public static final String PROCUREMENT_CARD_RECONCILER_GROUP_ID = "cardGroupId";
    public static final String PROCUREMENT_CARDHOLDER_USER_ID = "cardHolderSystemId";
    
    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        String cardGroupId = qualification.get(PROCUREMENT_CARD_RECONCILER_GROUP_ID);
        String cardHolderSystemId = qualification.get(PROCUREMENT_CARDHOLDER_USER_ID);        
        List<String> groupMembers = new ArrayList<String>();
        String principalId = qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID);        
        List<RoleMembership> members = new ArrayList<RoleMembership>();      
        if ((qualification != null) && StringUtils.isNotBlank(cardGroupId)) { 
            groupMembers = SpringContext.getBean(GroupService.class).getMemberPrincipalIds(cardGroupId);
            for (String groupMember : groupMembers) {
                if (StringUtils.isNotBlank(cardHolderSystemId) && !groupMember.equals(cardHolderSystemId)) {
                    //card holder cannot reconcile their own pcard transactions 
                    members.add(RoleMembership.Builder.create(null, null, groupMember, MemberType.PRINCIPAL, null).build());
                }
            }            
        }
        else if ((qualification != null) && StringUtils.isNotBlank(principalId)) {
            members.add(RoleMembership.Builder.create(null, null, principalId, MemberType.PRINCIPAL, null).build());                        
        }
        return members;
    }
}