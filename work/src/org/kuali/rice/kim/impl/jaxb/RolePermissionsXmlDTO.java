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
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.util.jaxb.RiceXmlExportList;
import org.kuali.rice.core.util.jaxb.RiceXmlImportList;
import org.kuali.rice.core.util.jaxb.RiceXmlListAdditionListener;
import org.kuali.rice.core.util.jaxb.RiceXmlListGetterListener;
import org.kuali.rice.kim.api.permission.PermissionContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

/**
 * Base class representing an unmarshalled &lt;rolePermissions&gt; element.
 * Refer to the static inner classes for more information about the specific contexts.
 * 
 * TODO: Alter the role/permission service APIs so that finding all the permissions assigned to a role is possible; the
 * current lack of such an API method prevents role permissions from being exported.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlTransient
public abstract class RolePermissionsXmlDTO<T extends RolePermissionXmlDTO> implements RiceXmlListAdditionListener<T>, Serializable {
    
    private static final long serialVersionUID = 1L;

    public abstract List<T> getRolePermissions();
    
    public abstract void setRolePermissions(List<T> rolePermissions);
    
    void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) throws UnmarshalException {
        setRolePermissions(new RiceXmlImportList<T>(this));
    }
    
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) throws UnmarshalException {
        setRolePermissions(null);
    }
    
    // =======================================================================================================
    
    /**
     * This class represents a &lt;rolePermissions&gt; element that is not a child of a &lt;role&gt; element.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name="StandaloneRolePermissionsType", propOrder={"rolePermissions"})
    public static class OutsideOfRole extends RolePermissionsXmlDTO<RolePermissionXmlDTO.OutsideOfRole> {
        
        private static final long serialVersionUID = 1L;
        
        @XmlElement(name="rolePermission")
        private List<RolePermissionXmlDTO.OutsideOfRole> rolePermissions;
        
        public List<RolePermissionXmlDTO.OutsideOfRole> getRolePermissions() {
            return rolePermissions;
        }

        public void setRolePermissions(List<RolePermissionXmlDTO.OutsideOfRole> rolePermissions) {
            this.rolePermissions = rolePermissions;
        }
        
        public void newItemAdded(RolePermissionXmlDTO.OutsideOfRole item) {
            try {
                RoleXmlUtil.validateAndPersistNewRolePermission(item);
            } catch (UnmarshalException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    // =======================================================================================================
    
    /**
     * This class represents a &lt;rolePermissions&gt; element that is a child of a &lt;role&gt; element.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name="RolePermissionsType", propOrder={"rolePermissions"})
    public static class WithinRole extends RolePermissionsXmlDTO<RolePermissionXmlDTO.WithinRole>
            implements RiceXmlListGetterListener<RolePermissionXmlDTO.WithinRole,String> {
        
        private static final long serialVersionUID = 1L;

        @XmlElement(name="rolePermission")
        private List<RolePermissionXmlDTO.WithinRole> rolePermissions;
        
        @XmlTransient
        private String roleId;
        
        public WithinRole() {}
        
        public WithinRole(String roleId) {
            this.roleId = roleId;
        }
        
        public List<RolePermissionXmlDTO.WithinRole> getRolePermissions() {
            return rolePermissions;
        }

        public void setRolePermissions(List<RolePermissionXmlDTO.WithinRole> rolePermissions) {
            this.rolePermissions = rolePermissions;
        }
        
        public String getRoleId() {
            return roleId;
        }
        
        @Override
        void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) throws UnmarshalException {
            if (parent instanceof RoleXmlDTO) {
                // Obtain the role ID from the enclosing role, and persist the role if it has not been persisted yet.
                RoleXmlDTO parentRole = (RoleXmlDTO) parent;
                if (!parentRole.isAlreadyPersisted()) {
                    RoleXmlUtil.validateAndPersistNewRole(parentRole);
                }
                roleId = parentRole.getRoleId();
            }
            super.beforeUnmarshal(unmarshaller, parent);
        }
        
        public void newItemAdded(RolePermissionXmlDTO.WithinRole item) {
            try {
                RoleXmlUtil.validateAndPersistNewRolePermission(item);
            } catch (UnmarshalException e) {
                throw new RuntimeException(e);
            }
        }
        
        void beforeMarshal(Marshaller marshaller) {
            // TODO: Use new API method once it becomes available!!!!
            List<String> permissionIds = new ArrayList<String>();// KIMServiceLocator.getPermissionService().getRoleIdsForPermission(permissionId);
            if (permissionIds != null && !permissionIds.isEmpty()) {
                setRolePermissions(new RiceXmlExportList<RolePermissionXmlDTO.WithinRole,String>(permissionIds, this));
            }
        }
        
        void afterMarshal(Marshaller marshaller) {
            setRolePermissions(null);
        }

        public RolePermissionXmlDTO.WithinRole gettingNextItem(String nextItem, int index) {
            PermissionContract permission = KimApiServiceLocator.getPermissionService().getPermission(nextItem);
            if (permission == null) {
                throw new RuntimeException("Cannot find permission with ID \"" + nextItem + "\"");
            }
            return new RolePermissionXmlDTO.WithinRole(permission, false);
        }
    }
}
