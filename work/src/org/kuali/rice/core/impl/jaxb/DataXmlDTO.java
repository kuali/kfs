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
