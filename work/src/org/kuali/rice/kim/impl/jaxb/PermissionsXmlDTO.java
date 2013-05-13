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
