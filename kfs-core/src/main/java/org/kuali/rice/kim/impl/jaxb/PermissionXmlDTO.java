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
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.kuali.rice.core.util.jaxb.NameAndNamespacePair;
import org.kuali.rice.core.util.jaxb.NameAndNamespacePairValidatingAdapter;
import org.kuali.rice.core.util.jaxb.StringTrimmingAdapter;
import org.kuali.rice.kim.api.jaxb.NameAndNamespacePairToPermTemplateIdAdapter;
import org.kuali.rice.kim.api.jaxb.PermissionDetailListAdapter;
import org.kuali.rice.kim.api.permission.PermissionContract;

/**
 * This class represents a &lt;permission&gt; XML element.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PermissionType", propOrder={
        "permissionNameAndNamespace", "permissionTemplateId", "permissionDescription", "active", "permissionDetails"
})
public class PermissionXmlDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @XmlTransient
    private String permissionId;
    
    @XmlElement(name="permissionName")
    @XmlJavaTypeAdapter(NameAndNamespacePairValidatingAdapter.class)
    private NameAndNamespacePair permissionNameAndNamespace;
    
    @XmlElement(name="templateName")
    @XmlJavaTypeAdapter(NameAndNamespacePairToPermTemplateIdAdapter.class)
    private String permissionTemplateId;
    
    @XmlElement(name="description")
    @XmlJavaTypeAdapter(StringTrimmingAdapter.class)
    private String permissionDescription;
    
    @XmlElement(name="active")
    private Boolean active;
    
    @XmlElement(name="permissionDetails")
    @XmlJavaTypeAdapter(PermissionDetailListAdapter.class)
    private Map<String, String> permissionDetails;
    
    public PermissionXmlDTO() {
        this.active = Boolean.TRUE;
    }
    
    public PermissionXmlDTO(PermissionContract permission) {
        this.permissionNameAndNamespace = new NameAndNamespacePair(permission.getNamespaceCode(), permission.getName());
        this.permissionTemplateId = permission.getTemplate().getId();
        this.permissionDescription = permission.getDescription();
        this.active = Boolean.valueOf(permission.isActive());
        this.permissionDetails = (permission.getAttributes() != null) ?
                new HashMap<String, String>(permission.getAttributes()) : new HashMap<String, String>();
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
     * @return the permissionTemplateId
     */
    public String getPermissionTemplateId() {
        return this.permissionTemplateId;
    }

    /**
     * @param permissionTemplateId the permissionTemplateId to set
     */
    public void setPermissionTemplateId(String permissionTemplateId) {
        this.permissionTemplateId = permissionTemplateId;
    }

    /**
     * @return the permissionDescription
     */
    public String getPermissionDescription() {
        return this.permissionDescription;
    }

    /**
     * @param permissionDescription the permissionDescription to set
     */
    public void setPermissionDescription(String permissionDescription) {
        this.permissionDescription = permissionDescription;
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
     * @return the permissionDetails
     */
    public Map<String, String> getPermissionDetails() {
        return this.permissionDetails;
    }

    /**
     * @param permissionDetails the permissionDetails to set
     */
    public void setPermissionDetails(Map<String, String> permissionDetails) {
        this.permissionDetails = permissionDetails;
    }

    /**
     * Retrieves the permission's name from the permission-name-and-namespace combo.
     * 
     * @return The name of the permission, or null if the permission-name-and-namespace combo is null.
     */
    public String getPermissionName() {
        return (permissionNameAndNamespace != null) ? permissionNameAndNamespace.getName() : null;
    }

    /**
     * Retrieves the permission's namespace code from the permission-name-and-namespace combo.
     * 
     * @return The namespace code of the permission, or null if the permission-name-and-namespace combo is null.
     */
    public String getNamespaceCode() {
        return (permissionNameAndNamespace != null) ? permissionNameAndNamespace.getNamespaceCode() : null;
    }
}
