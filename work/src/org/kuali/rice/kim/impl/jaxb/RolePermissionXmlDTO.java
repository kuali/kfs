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

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.util.jaxb.NameAndNamespacePair;
import org.kuali.rice.core.util.jaxb.NameAndNamespacePairValidatingAdapter;
import org.kuali.rice.kim.api.permission.PermissionContract;
import org.kuali.rice.kim.api.role.RoleContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;


/**
 * Base class representing an unmarshalled &lt;rolePermission&gt; element.
 * Refer to the static inner classes for more information about the specific contexts.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlTransient
public abstract class RolePermissionXmlDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @XmlElement(name="permissionId")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String permissionId;
    
    @XmlElement(name="permissionName")
    @XmlJavaTypeAdapter(NameAndNamespacePairValidatingAdapter.class)
    private NameAndNamespacePair permissionNameAndNamespace;

    /**
     * Constructs an empty RolePermissionXmlDTO instance.
     */
    public RolePermissionXmlDTO() {}
    
    /**
     * Constructs a RolePermissionXmlDTO that gets populated from the given KIM permission.
     * 
     * @param permission The permission that this DTO should obtain its data from.
     * @param populateIds If true, the permission ID will get populated; otherwise, it will remain null.
     */
    public RolePermissionXmlDTO(PermissionContract permission, boolean populateIds) {
        if (permission == null) {
            throw new IllegalArgumentException("Cannot construct a role permission with a null permission");
        }
        if (populateIds) {
            this.permissionId = permission.getId();
        }
        this.permissionNameAndNamespace = new NameAndNamespacePair(permission.getNamespaceCode(), permission.getName());
    }

    /**
     * @return the permissionId
     */
    public String getPermissionId() {
        return this.permissionId;
    }

    /**
     * @param permissionId the permissionId to set
     */
    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    /**
     * @return the permissionNameAndNamespace
     */
    public NameAndNamespacePair getPermissionNameAndNamespace() {
        return this.permissionNameAndNamespace;
    }

    /**
     * @param permissionNameAndNamespace the permissionNameAndNamespace to set
     */
    public void setPermissionNameAndNamespace(NameAndNamespacePair permissionNameAndNamespace) {
        this.permissionNameAndNamespace = permissionNameAndNamespace;
    }

    /**
     * Retrieves the permission name from the permission-name-and-namespace combo.
     * 
     * @return The name of the permission assigned to the role, or null if the permission-name-and-namespace combo is null.
     */
    public String getPermissionName() {
        return (permissionNameAndNamespace != null) ? permissionNameAndNamespace.getName() : null;
    }

    /**
     * Retrieves the permission namespace code from the permission-name-and-namespace combo.
     * 
     * @return The namespace code of the permission assigned to the role, or null if the permission-name-and-namespace combo is null.
     */
    public String getPermissionNamespaceCode() {
        return (permissionNameAndNamespace != null) ? permissionNameAndNamespace.getNamespaceCode() : null;
    }
    
    /**
     * Retrieves the ID of the role that the permission is assigned to.
     * Subclasses are responsible for implementing this method so that it does so.
     * 
     * @return The role ID of the role that the permission is assigned to.
     */
    public abstract String getRoleId();
    
    // =======================================================================================================
    
    /**
     * This class represents a &lt;rolePermission&gt; element that is not a descendant of a &lt;role&gt; element.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name="StandaloneRolePermissionType", propOrder={
            "roleId", "roleNameAndNamespace", "permissionId", "permissionNameAndNamespace"
    })
    public static class OutsideOfRole extends RolePermissionXmlDTO {
        
        private static final long serialVersionUID = 1L;

        @XmlElement(name="roleId")
        @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
        private String roleId;
        
        @XmlElement(name="roleName")
        @XmlJavaTypeAdapter(NameAndNamespacePairValidatingAdapter.class)
        private NameAndNamespacePair roleNameAndNamespace;
        
        public OutsideOfRole() {
            super();
        }
        
        public OutsideOfRole(PermissionContract permission, String roleId, boolean populateIds) {
            super(permission, populateIds);
            if (populateIds) {
                this.roleId = roleId;
            }
            RoleContract tempRole = KimApiServiceLocator.getRoleService().getRole(roleId);
            if (tempRole == null) {
                throw new IllegalArgumentException("Cannot find role with ID \"" + roleId + "\"");
            }
            this.roleNameAndNamespace = new NameAndNamespacePair(tempRole.getNamespaceCode(), tempRole.getName());
        }

        /**
         * @see org.kuali.rice.kim.impl.jaxb.RolePermissionXmlDTO#getRoleId()
         */
        @Override
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
         * Retrieves the role name from the role-name-and-namespace combo.
         * 
         * @return The name of the role that is assigned to the permission, or null if the role-name-and-namespace combo is null.
         */
        public String getRoleName() {
            return (roleNameAndNamespace != null) ? roleNameAndNamespace.getName() : null;
        }

        /**
         * Retrieves the role namespace code from the role-name-and-namespace combo.
         * 
         * @return The namespace code of the role that is assigned to the permission, or null if the role-name-and-namespace combo is null.
         */
        public String getRoleNamespaceCode() {
            return (roleNameAndNamespace != null) ? roleNameAndNamespace.getNamespaceCode() : null;
        }
    }
    
    // =======================================================================================================
    
    /**
     * This class represents a &lt;rolePermission&gt; element that is a descendant of a &lt;role&gt; element.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name="RolePermissionType", propOrder={
            "permissionId", "permissionNameAndNamespace"
    })
    public static class WithinRole extends RolePermissionXmlDTO {
        
        private static final long serialVersionUID = 1L;
        
        @XmlTransient
        private String roleId;
        
        public WithinRole() {
            super();
        }
        
        public WithinRole(PermissionContract permission, boolean populateIds) {
            super(permission, populateIds);
        }
        
        void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {
            if (parent instanceof RolePermissionsXmlDTO) {
                this.roleId = ((RolePermissionXmlDTO)parent).getRoleId();
            }
        }

        /**
         * @see org.kuali.rice.kim.impl.jaxb.RolePermissionXmlDTO#getRoleId()
         */
        @Override
        public String getRoleId() {
            return this.roleId;
        }
    }
}
