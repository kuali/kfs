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
