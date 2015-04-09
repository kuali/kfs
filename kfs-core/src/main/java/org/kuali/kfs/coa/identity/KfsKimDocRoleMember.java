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
