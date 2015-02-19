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
package org.kuali.rice.core.impl.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.util.jaxb.RiceXmlListAdditionListener;
import org.kuali.rice.kim.impl.jaxb.PermissionDataXmlDTO;
import org.kuali.rice.kim.impl.jaxb.RoleDataXmlDTO;
import org.w3c.dom.Element;

/**
 * This class represents the root &lt;data&gt; XML element.
 * 
 * <p>Please see the Javadocs for PermissionDataXmlDTO and RoleDataXmlDTO for more information
 * on their expected structure.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name="data")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="DataType", propOrder={"permissionData", "roleData", CoreConstants.CommonElements.FUTURE_ELEMENTS})
public class DataXmlDTO  extends AbstractDataTransferObject implements RiceXmlListAdditionListener<Element> {

    private static final long serialVersionUID = 1L;
    
    @XmlElement(name="permissionData")
    private PermissionDataXmlDTO permissionData;
    
    @XmlElement(name="roleData")
    private RoleDataXmlDTO roleData;
    
    @XmlAnyElement
    private final List<Element> _futureElements = null;
    
    public DataXmlDTO() {}
    
    public DataXmlDTO(PermissionDataXmlDTO permissionData, RoleDataXmlDTO roleData) {
        this.permissionData = permissionData;
        this.roleData = roleData;
    }

    /**
     * @return the permissionData
     */
    public PermissionDataXmlDTO getPermissionData() {
        return this.permissionData;
    }

    /**
     * @param permissionData the permissionData to set
     */
    public void setPermissionData(PermissionDataXmlDTO permissionData) {
        this.permissionData = permissionData;
    }

    /**
     * @return the roleData
     */
    public RoleDataXmlDTO getRoleData() {
        return this.roleData;
    }

    /**
     * @param roleData the roleData to set
     */
    public void setRoleData(RoleDataXmlDTO roleData) {
        this.roleData = roleData;
    }

    /**
     * @see org.kuali.rice.core.util.jaxb.RiceXmlListAdditionListener#newItemAdded(java.lang.Object)
     */
    @Override
    public void newItemAdded(Element item) {
        // Do nothing; this class just implements the streaming unmarshalling listener so that it doesn't hold onto all the DOM elements.
    }
}
