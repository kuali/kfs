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

import java.util.List;

import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.kim.api.common.delegate.DelegateTypeContract;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KfsKimDocDelegateType extends PersistableBusinessObjectBase implements DelegateTypeContract {

    protected String roleId;
    protected String delegationId;
    protected String kimTypeId;
    protected DelegationType delegationType;
    protected List<KfsKimDocDelegateMember> members;

    public KfsKimDocDelegateType() {}

    public KfsKimDocDelegateType( String roleId, String kimTypeId ) {
        this.roleId = roleId;
        this.kimTypeId = kimTypeId;
    }
    
    public KfsKimDocDelegateType( Role role ) {
        this( role.getId(), role.getKimTypeId() );
    }
    

    @Override
    public String getRoleId() {
        return roleId;
    }
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }
    @Override
    public String getDelegationId() {
        return delegationId;
    }
    public void setDelegationId(String delegationId) {
        this.delegationId = delegationId;
    }
    @Override
    public String getKimTypeId() {
        return kimTypeId;
    }
    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }
    @Override
    public DelegationType getDelegationType() {
        return delegationType;
    }
    public void setDelegationType(DelegationType delegationType) {
        this.delegationType = delegationType;
    }
    @Override
    public List<KfsKimDocDelegateMember> getMembers() {
        return members;
    }
    public void setMembers(List<KfsKimDocDelegateMember> members) {
        this.members = members;
    }

    @Override
    public boolean isActive() {
        return true;
    }

}
