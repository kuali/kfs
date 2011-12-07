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

import org.joda.time.DateTime;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.role.RoleMemberContract;
import org.kuali.rice.kim.api.role.RoleResponsibilityActionContract;
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
    
    public KfsKimDocRoleMember( String roleId, MemberType type) {
        this.roleId = roleId;
        this.type = type;
    }    
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getMemberId() {
        return memberId;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    public String getMemberName() {
        // RICE20 - TODO: dynamically load this information upon request
        return memberName;
    }
    public void setMemberName(String memberName) {
        // RICE20 - TODO: dynamically load this information upon request
        this.memberName = memberName;
    }
    public String getMemberNamespaceCode() {
        // RICE20 - TODO: dynamically load this information upon request
        return memberNamespaceCode;
    }
    public void setMemberNamespaceCode(String memberNamespaceCode) {
        // RICE20 - TODO: dynamically load this information upon request
        this.memberNamespaceCode = memberNamespaceCode;
    }
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    public Map<String, String> getAttributes() {
        return attributes;
    }
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public DateTime getActiveFromDate() {
        return activeFromDate;
    }
    public void setActiveFromDate(DateTime activeFromDate) {
        this.activeFromDate = activeFromDate;
    }
    public DateTime getActiveToDate() {
        return activeToDate;
    }
    public void setActiveToDate(DateTime activeToDate) {
        this.activeToDate = activeToDate;
    }
    public List<KfsKimRoleResponsibilityAction> getRoleRspActions() {
        return roleRspActions;
    }
    public void setRoleRspActions(List<KfsKimRoleResponsibilityAction> roleRspActions) {
        this.roleRspActions = roleRspActions;
    }
    public MemberType getType() {
        return type;
    }
    public void setType(MemberType type) {
        this.type = type;
    }
    @Override
    public boolean isActive(DateTime activeAsOfDate) {
        // RICE20 - TODO: add implementation of this method
        return false;
    }

}
