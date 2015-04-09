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

import org.kuali.rice.kim.api.role.RoleResponsibilityActionContract;
import org.kuali.rice.kim.api.role.RoleResponsibilityContract;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KfsKimRoleResponsibilityAction extends PersistableBusinessObjectBase implements RoleResponsibilityActionContract {

    protected String id;
    protected String roleResponsibilityId;
    protected String actionTypeCode;
    protected String actionPolicyCode;
    protected Integer priorityNumber;
    protected String roleMemberId;
    protected boolean forceAction;
    
    public KfsKimRoleResponsibilityAction() {}
    
    public KfsKimRoleResponsibilityAction( RoleResponsibilityActionContract b ) {
        id = b.getId();
        roleResponsibilityId = b.getRoleResponsibilityId();
        roleMemberId = b.getRoleMemberId();
        actionTypeCode = b.getActionTypeCode();
        actionPolicyCode = b.getActionPolicyCode();
        forceAction = b.isForceAction();
        priorityNumber = b.getPriorityNumber();
//        roleResponsibility = b.getRoleResponsibility();
    }
    
    

    @Override
    public RoleResponsibilityContract getRoleResponsibility() {
        throw new UnsupportedOperationException( "getRoleResponsibility needs to be implemented" );
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getRoleResponsibilityId() {
        return roleResponsibilityId;
    }


    public void setRoleResponsibilityId(String roleResponsibilityId) {
        this.roleResponsibilityId = roleResponsibilityId;
    }


    public String getActionTypeCode() {
        return actionTypeCode;
    }


    public void setActionTypeCode(String actionTypeCode) {
        this.actionTypeCode = actionTypeCode;
    }


    public String getActionPolicyCode() {
        return actionPolicyCode;
    }


    public void setActionPolicyCode(String actionPolicyCode) {
        this.actionPolicyCode = actionPolicyCode;
    }


    public Integer getPriorityNumber() {
        return priorityNumber;
    }


    public void setPriorityNumber(Integer priorityNumber) {
        this.priorityNumber = priorityNumber;
    }


    public String getRoleMemberId() {
        return roleMemberId;
    }


    public void setRoleMemberId(String roleMemberId) {
        this.roleMemberId = roleMemberId;
    }


    public boolean isForceAction() {
        return forceAction;
    }


    public void setForceAction(boolean forceAction) {
        this.forceAction = forceAction;
    }

}
