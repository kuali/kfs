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
import java.util.List;
import java.util.Set;

import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.util.jaxb.RiceXmlExportList;
import org.kuali.rice.core.util.jaxb.RiceXmlImportList;
import org.kuali.rice.core.util.jaxb.RiceXmlListAdditionListener;
import org.kuali.rice.core.util.jaxb.RiceXmlListGetterListener;
import org.kuali.rice.kim.api.role.RoleContract;

/**
 * This class represents a &lt;roles&gt; element. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="RolesType", propOrder={"roles"})
public class RolesXmlDTO implements RiceXmlListAdditionListener<RoleXmlDTO>, RiceXmlListGetterListener<RoleXmlDTO,Object>, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @XmlElement(name="role")
    private List<RoleXmlDTO> roles;
    
    public RolesXmlDTO() {}
    
    public RolesXmlDTO(List<? extends Object> rolesToExport) {
        this.roles = new RiceXmlExportList<RoleXmlDTO,Object>(rolesToExport, this);
    }
    
    public List<RoleXmlDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleXmlDTO> roles) {
        this.roles = roles;
    }

    void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {
        roles = new RiceXmlImportList<RoleXmlDTO>(this);
    }
    
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        roles = null;
    }
    
    /**
     * @see org.kuali.rice.core.util.jaxb.RiceXmlListAdditionListener#newItemAdded(java.lang.Object)
     */
    public void newItemAdded(RoleXmlDTO item) {
        // Persist the role if it has not already been persisted yet.
        if (!item.isAlreadyPersisted()) {
            try {
                RoleXmlUtil.validateAndPersistNewRole(item);
            } catch (UnmarshalException e) {
                throw new RuntimeException(e);
            }
        }
        
        // If a "roleMembers" element was present, remove any existing roles that do not match the new ones.
        Set<String> existingRoleMemberIds = item.getExistingRoleMemberIds();
        if (existingRoleMemberIds != null) {
            RoleXmlUtil.removeRoleMembers(item.getRoleId(), existingRoleMemberIds);
        }
        item.setExistingRoleMemberIds(null);
    }

    void afterMarshal(Marshaller marshaller) {
        roles = null;
    }
    
    /**
     * @see org.kuali.rice.core.util.jaxb.RiceXmlListGetterListener#gettingNextItem(java.lang.Object, int)
     */
    public RoleXmlDTO gettingNextItem(Object nextItem, int index) {
        if (!(nextItem instanceof RoleContract)) {
            throw new IllegalStateException("Object for exportation should have been a role");
        }
        RoleContract role = ((RoleContract)nextItem);
        return new RoleXmlDTO(role, new RoleMembersXmlDTO.WithinRole(role.getId()), new RolePermissionsXmlDTO.WithinRole(role.getId()));
    }
}
