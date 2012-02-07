/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMemberContract;
import org.kuali.rice.kim.api.role.RoleResponsibilityActionContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KfsKimDocRoleMember extends PersistableBusinessObjectBase implements RoleMemberContract {

    protected String id;
    protected String memberId;
    protected String memberName;
    protected String memberNamespaceCode;
    protected String roleId;
    protected MemberType type;
    protected Map<String,String> attributes = new HashMap<String, String>();
    protected boolean active = true;
    protected DateTime activeFromDate;
    protected DateTime activeToDate;
    protected List<KfsKimRoleResponsibilityAction> roleRspActions = new ArrayList<KfsKimRoleResponsibilityAction>();

    public KfsKimDocRoleMember() {}

    public KfsKimDocRoleMember( RoleMemberContract b ) {
        id = b.getId();
        roleId = b.getRoleId();
        attributes = b.getAttributes();

        if (!CollectionUtils.isEmpty(b.getRoleRspActions())) {
            for (RoleResponsibilityActionContract rra : b.getRoleRspActions()) {
                roleRspActions.add( new KfsKimRoleResponsibilityAction(rra) );
            }
        }

        memberId = b.getMemberId();
        type = b.getType();
        activeFromDate = b.getActiveFromDate();
        activeToDate = b.getActiveToDate();
        versionNumber = b.getVersionNumber();
    }

    public KfsKimDocRoleMember( String roleId, MemberType type) {
        this();
        this.roleId = roleId;
        this.type = type;
    }

    public KfsKimDocRoleMember(String roleId, MemberType type, String memberId) {
        this( roleId, type );
        this.memberId = memberId;
    }

    @Override
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    @Override
    public String getMemberId() {
        return memberId;
    }
    public void setMemberId(String memberId) {
        memberName = null;
        memberNamespaceCode = null;
        this.memberId = memberId;
    }

    protected void loadMemberInfo() {
        if ( MemberType.ROLE.equals( type ) ) {
            Role role = KimApiServiceLocator.getRoleService().getRole(memberId);
            if ( role != null ) {
                memberName = role.getName();
                memberNamespaceCode = role.getNamespaceCode();
            }
        } else if ( MemberType.GROUP.equals( type ) ) {
            Group group = KimApiServiceLocator.getGroupService().getGroup(memberId);
            if ( group != null ) {
                memberName = group.getName();
                memberNamespaceCode = group.getNamespaceCode();
            }
        } else {
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipal(memberId);
            if ( principal != null ) {
                memberName = principal.getPrincipalName();
                memberNamespaceCode = "";
            }
        }
    }

    @Override
    public String getMemberName() {
        if ( memberName == null && memberId != null ) {
            loadMemberInfo();
        }
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    @Override
    public String getMemberNamespaceCode() {
        if ( memberNamespaceCode == null && memberId != null ) {
            loadMemberInfo();
        }
        return memberNamespaceCode;
    }
    public void setMemberNamespaceCode(String memberNamespaceCode) {
        this.memberNamespaceCode = memberNamespaceCode;
    }
    @Override
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    @Override
    public Map<String, String> getAttributes() {
        return attributes;
    }
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    @Override
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    @Override
    public DateTime getActiveFromDate() {
        return activeFromDate;
    }
    public void setActiveFromDate(DateTime activeFromDate) {
        this.activeFromDate = activeFromDate;
    }
    @Override
    public DateTime getActiveToDate() {
        return activeToDate;
    }
    public void setActiveToDate(DateTime activeToDate) {
        this.activeToDate = activeToDate;
    }
    @Override
    public List<KfsKimRoleResponsibilityAction> getRoleRspActions() {
        return roleRspActions;
    }
    public void setRoleRspActions(List<KfsKimRoleResponsibilityAction> roleRspActions) {
        this.roleRspActions = roleRspActions;
    }
    @Override
    public MemberType getType() {
        return type;
    }
    public void setType(MemberType type) {
        this.type = type;
    }
    @Override
    public boolean isActive(DateTime activeAsOfDate) {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, null);
    }

}
