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

import java.util.Map;

import org.joda.time.DateTime;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.kim.api.common.delegate.DelegateMemberContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KfsKimDocDelegateMember extends PersistableBusinessObjectBase implements DelegateMemberContract {

    protected String delegationMemberId;
    protected String delegationId;
    protected String memberId;
    protected String roleMemberId;
    protected Map<String, String> attributes;
    protected DateTime activeFromDate;
    protected DateTime activeToDate;
    protected DelegationType delegationType;
    protected MemberType type;
    protected String memberName;
    protected String memberNamespaceCode;
    
    public KfsKimDocDelegateMember() {}
    
    public KfsKimDocDelegateMember( String roleMemberId, MemberType type ) {
        this.roleMemberId = roleMemberId;
        this.type = type;
    }
    
    public KfsKimDocDelegateMember( DelegateMemberContract delegateMember ) {
        this.delegationMemberId = delegateMember.getDelegationMemberId();
        this.delegationId = delegateMember.getDelegationId();
        this.memberId = delegateMember.getMemberId();
        this.roleMemberId = delegateMember.getRoleMemberId();
        this.type = delegateMember.getType();
        this.activeFromDate = delegateMember.getActiveFromDate();
        this.activeToDate = delegateMember.getActiveToDate();
        this.attributes = delegateMember.getAttributes();        
    }
    
    public String getDelegationMemberId() {
        return delegationMemberId;
    }
    public void setDelegationMemberId(String delegationMemberId) {
        this.delegationMemberId = delegationMemberId;
    }
    public String getDelegationId() {
        return delegationId;
    }
    public void setDelegationId(String delegationId) {
        this.delegationId = delegationId;
    }
    public String getMemberId() {
        return memberId;
    }
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
    public String getRoleMemberId() {
        return roleMemberId;
    }
    public void setRoleMemberId(String roleMemberId) {
        this.roleMemberId = roleMemberId;
    }
    public Map<String, String> getAttributes() {
        return attributes;
    }
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
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

    @Override
    public boolean isActive(DateTime activeAsOfDate) {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOfDate);
    }

    @Override
    public boolean isActive() {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, null);
    }

    @Override
    public MemberType getType() {
        return type;
    }

    public DelegationType getDelegationType() {
        return delegationType;
    }

    public void setDelegationType(DelegationType delegationType) {
        this.delegationType = delegationType;
    }

    public void setType(MemberType type) {
        this.type = type;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberNamespaceCode() {
        return memberNamespaceCode;
    }

    public void setMemberNamespaceCode(String memberNamespaceCode) {
        this.memberNamespaceCode = memberNamespaceCode;
    }
    
    

}
