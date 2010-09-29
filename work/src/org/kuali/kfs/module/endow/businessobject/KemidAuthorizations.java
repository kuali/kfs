/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiInteger;

public class KemidAuthorizations extends PersistableBusinessObjectBase {

    private String kemid;
    private KualiInteger roleSequenceNumber;
    private String roleId;
    private String rolePrincipalId;
    private Date roleTerminationDate;
    private boolean active;

    private KEMID kemidObjRef;
    private Role role;
    private Person rolePrincipal;

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        m.put(EndowPropertyConstants.KEMID_AUTHORIZATIONS_ROLE_SEQ_NBR, String.valueOf(this.roleSequenceNumber));
        m.put(EndowPropertyConstants.KEMID_AUTHORIZATIONS_ROLE_ID, this.roleId);
        m.put(EndowPropertyConstants.KEMID_AUTHORIZATIONS_ROLE_PRNCPL_ID, rolePrincipalId);
        return m;
    }

    /**
     * Gets the kemid.
     * 
     * @return kemid
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid.
     * 
     * @param kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the roleSequenceNumber.
     * 
     * @return roleSequenceNumber
     */
    public KualiInteger getRoleSequenceNumber() {
        return roleSequenceNumber;
    }

    /**
     * Sets the roleSequenceNumber.
     * 
     * @param roleSequenceNumber
     */
    public void setRoleSequenceNumber(KualiInteger roleSequenceNumber) {
        this.roleSequenceNumber = roleSequenceNumber;
    }

    /**
     * Gets the roleId.
     * 
     * @return roleId
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * Sets the roleId.
     * 
     * @param roleId
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets the rolePrincipalId.
     * 
     * @return rolePrincipalId
     */
    public String getRolePrincipalId() {
        return rolePrincipalId;
    }

    /**
     * Sets the rolePrincipalId.
     * 
     * @param rolePrincipalId
     */
    public void setRolePrincipalId(String rolePrincipalId) {
        this.rolePrincipalId = rolePrincipalId;
    }

    /**
     * Gets the roleTerminationDate.
     * 
     * @return roleTerminationDate
     */
    public Date getRoleTerminationDate() {
        return roleTerminationDate;
    }

    /**
     * Sets the roleTerminationDate.
     * 
     * @param roleTerminationDate
     */
    public void setRoleTerminationDate(Date roleTerminationDate) {
        this.roleTerminationDate = roleTerminationDate;
    }

    /**
     * Gets the active indicator.
     * 
     * @return active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active indicator.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the kemidObjRef.
     * 
     * @return kemidObjRef
     */
    public KEMID getKemidObjRef() {
        return kemidObjRef;
    }

    /**
     * Sets the kemidObjRef.
     * 
     * @param kemidObjRef
     */
    public void setKemidObjRef(KEMID kemidObjRef) {
        this.kemidObjRef = kemidObjRef;
    }

    /**
     * Gets the role.
     * 
     * @return role
     */
    public Role getRole() {
        role = KIMServiceLocator.getRoleManagementService().getRole(roleId);
        return role;
    }

    /**
     * Sets the role.
     * 
     * @param role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Gets the rolePrincipal.
     * 
     * @return rolePrincipal
     */
    public Person getRolePrincipal() {
        rolePrincipal = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(rolePrincipalId, rolePrincipal);
        return rolePrincipal;
    }

    /**
     * Sets the rolePrincipal.
     * 
     * @param rolePrincipal
     */
    public void setRolePrincipal(Person rolePrincipal) {
        this.rolePrincipal = rolePrincipal;
    }

    /**
     * @return Returns the rolePrincipalId.
     */
    public String getRolePrincipalIdForSearching() {
        return getRolePrincipalId();
    }

}
