/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kim.impl.jaxb;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.util.jaxb.NameAndNamespacePair;
import org.kuali.rice.core.util.jaxb.NameAndNamespacePairValidatingAdapter;
import org.kuali.rice.core.util.jaxb.StringTrimmingAdapter;
import org.kuali.rice.kim.api.jaxb.NameAndNamespacePairToKimTypeIdAdapter;
import org.kuali.rice.kim.api.role.RoleContract;

/**
 * This class represents a &lt;role&gt; XML element.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="RoleType", propOrder={
        "roleNameAndNamespace", "kimTypeId", "roleDescription", "active", "roleMembers", "rolePermissions"
})
public class RoleXmlDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @XmlTransient
    private String roleId;
    
    @XmlElement(name="roleName")
    @XmlJavaTypeAdapter(NameAndNamespacePairValidatingAdapter.class)
    private NameAndNamespacePair roleNameAndNamespace;
    
    @XmlElement(name="kimTypeName")
    @XmlJavaTypeAdapter(NameAndNamespacePairToKimTypeIdAdapter.class)
    private String kimTypeId;
    
    @XmlElement(name="description")
    @XmlJavaTypeAdapter(StringTrimmingAdapter.class)
    private String roleDescription;
    
    @XmlElement(name="active")
    private Boolean active;
    
    @XmlElement(name="roleMembers")
    private RoleMembersXmlDTO.WithinRole roleMembers;
    
    @XmlElement(name="rolePermissions")
    private RolePermissionsXmlDTO.WithinRole rolePermissions;

    @XmlTransient
    private boolean alreadyPersisted = false;
    
    @XmlTransient
    private Set<String> existingRoleMemberIds;
    
    public RoleXmlDTO() {
        this.active = Boolean.TRUE;
    }
    
    public RoleXmlDTO(RoleContract role, RoleMembersXmlDTO.WithinRole roleMembers, RolePermissionsXmlDTO.WithinRole rolePermissions) {
        if (role == null) {
            throw new IllegalArgumentException("role cannot be null");
        }
        
        this.roleNameAndNamespace = new NameAndNamespacePair(role.getNamespaceCode(), role.getName());
        this.kimTypeId = role.getKimTypeId();
        this.roleDescription = role.getDescription();
        this.active = Boolean.valueOf(role.isActive());
        this.roleMembers = roleMembers;
        this.rolePermissions = rolePermissions;
    }

    /**
     * @return the roleId
     */
    public String getRoleId() {
        return this.roleId;
    }

    /**
     * @param roleId the roleId to set
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * @return the roleNameAndNamespace
     */
    public NameAndNamespacePair getRoleNameAndNamespace() {
        return this.roleNameAndNamespace;
    }

    /**
     * @param roleNameAndNamespace the roleNameAndNamespace to set
     */
    public void setRoleNameAndNamespace(NameAndNamespacePair roleNameAndNamespace) {
        this.roleNameAndNamespace = roleNameAndNamespace;
    }

    /**
     * @return the kimTypeId
     */
    public String getKimTypeId() {
        return this.kimTypeId;
    }

    /**
     * @param kimTypeId the kimTypeId to set
     */
    public void setKimTypeId(String kimTypeId) {
        this.kimTypeId = kimTypeId;
    }

    /**
     * @return the roleDescription
     */
    public String getRoleDescription() {
        return this.roleDescription;
    }

    /**
     * @param roleDescription the roleDescription to set
     */
    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return this.active;
    }

    /**
     * @param active the active to set
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    /**
     * @return the roleMembers
     */
    public RoleMembersXmlDTO.WithinRole getRoleMembers() {
        return this.roleMembers;
    }

    /**
     * @param roleMembers the roleMembers to set
     */
    public void setRoleMembers(RoleMembersXmlDTO.WithinRole roleMembers) {
        this.roleMembers = roleMembers;
    }

    /**
     * @return the rolePermissions
     */
    public RolePermissionsXmlDTO.WithinRole getRolePermissions() {
        return this.rolePermissions;
    }

    /**
     * @param rolePermissions the rolePermissions to set
     */
    public void setRolePermissions(RolePermissionsXmlDTO.WithinRole rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    /**
     * @return the alreadyPersisted
     */
    public boolean isAlreadyPersisted() {
        return this.alreadyPersisted;
    }

    /**
     * @param alreadyPersisted the alreadyPersisted to set
     */
    public void setAlreadyPersisted(boolean alreadyPersisted) {
        this.alreadyPersisted = alreadyPersisted;
    }
    
    /**
     * @return the existingRoleMemberIds
     */
    public Set<String> getExistingRoleMemberIds() {
        return this.existingRoleMemberIds;
    }

    /**
     * @param existingRoleMemberIds the existingRoleMemberIds to set
     */
    public void setExistingRoleMemberIds(Set<String> existingRoleMemberIds) {
        this.existingRoleMemberIds = existingRoleMemberIds;
    }

    /**
     * Retrieves the role's name from the role-name-and-namespace combo.
     * 
     * @return The name of the role, or null if the role-name-and-namespace combo is null.
     */
    public String getRoleName() {
        return (roleNameAndNamespace != null) ? roleNameAndNamespace.getName() : null;
    }

    /**
     * Retrieves the role's namespace code from the role-name-and-namespace combo.
     * 
     * @return The namespace code of the role, or null if the role-name-and-namespace combo is null.
     */
    public String getNamespaceCode() {
        return (roleNameAndNamespace != null) ? roleNameAndNamespace.getNamespaceCode() : null;
    }
}