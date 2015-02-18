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
package org.kuali.rice.kim.impl.jaxb;

import java.io.Serializable;
import java.util.List;

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
import org.kuali.rice.kim.api.permission.PermissionContract;

/**
 * This class represents a &lt;permissions&gt; element. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PermissionsType", propOrder={"permissions"})
public class PermissionsXmlDTO implements RiceXmlListAdditionListener<PermissionXmlDTO>,
        RiceXmlListGetterListener<PermissionXmlDTO,Object>, Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @XmlElement(name="permission")
    private List<PermissionXmlDTO> permissions;
    
    public PermissionsXmlDTO() {}
    
    public PermissionsXmlDTO(List<? extends Object> permissionsToExport) {
        this.permissions = new RiceXmlExportList<PermissionXmlDTO,Object>(permissionsToExport, this);
    }
    
    /**
     * @return the permissions
     */
    public List<PermissionXmlDTO> getPermissions() {
        return this.permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(List<PermissionXmlDTO> permissions) {
        this.permissions = permissions;
    }

    void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {
        permissions = new RiceXmlImportList<PermissionXmlDTO>(this);
    }
    
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        permissions = null;
    }
    
    public void newItemAdded(PermissionXmlDTO item) {
        try {
            PermissionXmlUtil.validateAndPersistNewPermission(item);
        } catch (UnmarshalException e) {
            throw new RuntimeException(e);
        }
    }

    void afterMarshal(Marshaller marshaller) {
        permissions = null;
    }
    
    public PermissionXmlDTO gettingNextItem(Object nextItem, int index) {
        if (!(nextItem instanceof PermissionContract)) {
            throw new IllegalStateException("Object for exportation should have been a permission");
        }
        return new PermissionXmlDTO((PermissionContract) nextItem);
    }
}
