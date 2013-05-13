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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents the &lt;permissionData&gt; element.
 * 
 * <p>The expected XML structure is as follows:
 * 
 * <br>
 * <br>&lt;permissionData&gt;
 * <br>&nbsp;&nbsp;&lt;permissions&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;permission&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;permissionName namespaceCode=""&gt;&lt;/permissionName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;templateName namespaceCode=""&gt;&lt;/templateName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;description&gt;&lt;/description&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;active&gt;&lt;/active&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;permissionDetails&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;permissionDetail key=""&gt;&lt;/permissionDetail&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/permissionDetails&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/permission&gt;
 * <br>&nbsp;&nbsp;&lt;/permissions&gt;
 * <br>&lt;/permissionData&gt;
 * 
 * <p>Note the following:
 * <ul>
 *   <li>The &lt;permissions&gt; element is optional, and can contain zero or more &lt;permission&gt; elements.
 *   <li>The &lt;permissionName&gt; element and its "namespaceCode" attribute are both required.
 *   The namespace code must map to a valid namespace.
 *   If the name and namespace combo matches an existing permission, then the permission in the XML
 *   will overwrite the existing permission.
 *   <li>The &lt;templateName&gt; element and its "namespaceCode" attribute are both required.
 *   The name and namespace combo on this element must match a valid permission template.
 *   <li>The &lt;description&gt; element is required, and must be non-blank.
 *   <li>The &lt;active&gt; element is optional, and will be set to true if not specified.
 *   <li>The &lt;permissionDetails&gt; element is optional, and can contain zero or more
 *   &lt;permissionDetail&gt; elements.
 *   <li>The &lt;permissionDetail&gt; element's "key" attribute is required, and must be non-blank.
 *   Duplicate keys within a &lt;permissionDetails&gt; element are not permitted.
 *   <li>The same permission can be ingested multiple times in the same file, where subsequent ones will
 *   overwrite previous ones. (TODO: Is this acceptable?)
 * </ul>
 * 
 * TODO: Verify that the above behavior is correct.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="PermissionDataType", propOrder={"permissions"})
public class PermissionDataXmlDTO {

    @XmlElement(name="permissions")
    private PermissionsXmlDTO permissions;
    
    public PermissionDataXmlDTO() {}
    
    public PermissionDataXmlDTO(PermissionsXmlDTO permissions) {
        this.permissions = permissions;
    }

    /**
     * @return the permissions
     */
    public PermissionsXmlDTO getPermissions() {
        return this.permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(PermissionsXmlDTO permissions) {
        this.permissions = permissions;
    }
}
