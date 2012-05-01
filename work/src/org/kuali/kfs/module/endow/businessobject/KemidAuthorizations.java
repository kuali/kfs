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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.framework.role.RoleEbo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.campus.CampusEbo;

public class KemidAuthorizations extends PersistableBusinessObjectBase {

    protected String kemid;
    protected KualiInteger roleSequenceNumber;
    protected String roleId;
    protected String rolePrincipalId;
    protected Date roleTerminationDate;
    protected boolean active;

    protected KEMID kemidObjRef;
    protected RoleEbo role;
    protected Person rolePrincipal;

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
    public RoleEbo getRole() {
        if ( StringUtils.isBlank(roleId) ) {
            role = null;
        } else {
            ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(RoleEbo.class);
            if ( moduleService != null ) {
                Map<String,Object> keys = new HashMap<String, Object>(1);
                keys.put(LocationConstants.PrimaryKeyConstants.CODE, roleId);
                role = moduleService.getExternalizableBusinessObject(RoleEbo.class, keys);
            } else {
                throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
            }

        }
        return role;
    }

    /**
     * Sets the role.
     *
     * @param role
     */
    public void setRole(RoleEbo role) {
        this.role = role;
    }

    /**
     * Gets the rolePrincipal.
     *
     * @return rolePrincipal
     */
    public Person getRolePrincipal() {
        rolePrincipal = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(rolePrincipalId, rolePrincipal);
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
