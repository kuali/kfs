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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * This class represents a &lt;roleData&gt; element.
 * 
 * <p>The expected XML structure is as follows:
 * 
 * <br>
 * <br>&lt;roleData&gt;
 * <br>&nbsp;&nbsp;&lt;roles&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;role&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleName namespaceCode=""&gt;&lt;/roleName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;kimTypeName namespaceCode=""&gt;&lt;/kimTypeName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;description&gt;&lt;/description&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;active&gt;&lt;/active&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleMembers&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleMember&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;principalId&gt;&lt;/principalId&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;principalName&gt;&lt;/principalName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;&lt;/groupId&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupName namespaceCode=""&gt;&lt;/groupName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleIdAsMember&gt;&lt;/roleIdAsMember&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleNameAsMember namespaceCode=""&gt;&lt;/roleNameAsMember&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;activeFromDate&gt;&lt;/activeFromDate&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;activeToDate&gt;&lt;/activeToDate&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;qualifications&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;qualification key=""&gt;&lt;/qualification&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/qualifications&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/roleMember&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/roleMembers&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;rolePermissions&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;rolePermission&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;permissionId&gt;&lt;/permissionId&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;permissionName namespaceCode=""&gt;&lt;/permissionName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/rolePermission&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/rolePermissions&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/role&gt;
 * <br>&nbsp;&nbsp;&lt;/roles&gt;
 * <br>&nbsp;&nbsp;&lt;roleMembers&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleMember&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleId&gt;&lt;/roleId&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleName namespaceCode=""&gt;&lt;/roleName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;principalId&gt;&lt;/principalId&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;principalName&gt;&lt;/principalName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;&lt;/groupId&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupName namespaceCode=""&gt;&lt;/groupName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleIdAsMember&gt;&lt;/roleIdAsMember&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleNameAsMember namespaceCode=""&gt;&lt;/roleNameAsMember&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;activeFromDate&gt;&lt;/activeFromDate&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;activeToDate&gt;&lt;/activeToDate&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;qualifications&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;qualification key=""&gt;&lt;/qualification&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/qualifications&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/roleMember&gt;
 * <br>&nbsp;&nbsp;&lt;/roleMembers&gt;
 * <br>&nbsp;&nbsp;&lt;rolePermissions&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;rolePermission&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleId&gt;&lt;/roleId&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;roleName namespaceCode=""&gt;&lt;/roleName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;permissionId&gt;&lt;/permissionId&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;permissionName namespaceCode=""&gt;&lt;/permissionName&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;/rolePermission&gt;
 * <br>&nbsp;&nbsp;&lt;/rolePermissions&gt;
 * <br>&lt;/roleData&gt;
 * 
 * <p>Note the following:
 * <ul>
 *   <li>The &lt;roles&gt; element is optional, and can contain zero or more &lt;role&gt; elements.
 *   <li>The &lt;roleName&gt; element on the &lt;role&gt; element and its "namespaceCode" attribute
 *   are required, and must be non-blank. The namespace code must map to a valid namespace.
 *   If the name and namespace combo matches an existing role, then the role in the XML will
 *   overwrite the existing role.
 *   <li>The &lt;kimTypeName&gt; and its "namespaceCode" attribute are both required, and the
 *   name and namespace combo must match an existing KIM type.
 *   <li>The &lt;description&gt; element is required, and must be non-blank.
 *   <li>The &lt;active&gt; element is optional, and will be set to true if not specified.
 *   <li>Both &lt;roleMembers&gt; elements are optional, and can contain zero or more
 *   &lt;roleMember&gt; elements. If the &lt;roleMembers&gt; element within the &lt;role&gt;
 *   element is specified, then any role members that are not within that element will be removed
 *   from the role if the XML is overwriting an existing one. (The &lt;roleMembers&gt; element
 *   outside of the &lt;role&gt; element can still add or re-add members that are not located
 *   within the other &lt;roleMembers&gt; element.)
 *   <li>For both &lt;roleMember&gt; elements:
 *     <ul>
 *       <li>Exactly one of these sets of member identification must be specified:
 *         <ol>
 *           <li>A &lt;principalId&gt; and/or &lt;principalName&gt; element, where the former
 *           must contain a valid principal ID and the latter must contain a valid principal name.
 *           <li>A &lt;groupId&gt; and/or &lt;groupName&gt; element, where the former must contain
 *           a valid group ID and the latter must contain a valid group name and namespace.
 *           <li>A &lt;roleIdAsMember&gt; and/or &lt;roleNameAsMember&gt; element, where the former
 *           must contain a valid role ID and the latter must contain a valid role name and namespace.
 *         </ol>
 *       <li>The &lt;activeFromDate&gt; element is optional, and its content must be a date String
 *       that can be parsed by the DateTimeService.
 *       <li>The &lt;activeToDate&gt; element is optional, and its content must be a date String
 *       that can be parsed by the DateTimeService.
 *       <li>The &lt;qualifications&gt; element is optional, and can contain zero or more
 *       &lt;qualification&gt; elements.
 *       <li>The &lt;qualification&gt; element's "key" attribute is required, and must be non-blank.
 *       Duplicate keys within a &lt;qualifications&gt; element are not permitted.
 *     </ul>
 *   <li>For both &lt;rolePermission&gt; elements:
 *     <ul>
 *       <li>A &lt;permissionId&gt; and/or &lt;permissionName&gt; element must be specified, where the
 *       former must contain a valid permission ID and the latter must contain a valid permission
 *       name and namespace.
 *     </ul>
 *   <li>For the &lt;roleMember&gt; and &lt;rolePermission&gt; elements not inside a &lt;role&gt; element:
 *     <ul>
 *       <li>A &lt;roleId&gt; and/or &lt;roleName&gt; element must be specified, where the former must
 *       contain a valid role ID and the latter must contain a valid role name and namespace.
 *     </ul>
 *   <li>The ingestion process is currently order-dependent, which should be kept in mind when adding
 *   roles as members of another role or assigning permissions to roles. (The permission XML always
 *   gets ingested prior to the role XML.)
 *   <li>The assignments of permissions to roles can only be added, not removed or deactivated.
 *   (TODO: Improve the role/permission-updating API to allow for updates and removals.)
 *   <li>The same roles, role members, and role permissions can be ingested within the same file,
 *   where subsequent ones will overwrite previous ones. (TODO: Is this acceptable?)
 *   <li>The IDs of principals, groups, roles, and permissions are not included when exporting the XML.
 *   <li>Delegations and responsibility actions are currently not supported by the ingestion process.
 * </ul>
 * 
 * TODO: Verify that the above behavior is correct.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="RoleDataType", propOrder={"roles", "roleMembers", "rolePermissions"})
public class RoleDataXmlDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name="roles")
    private RolesXmlDTO roles;
    
    @XmlElement(name="roleMembers")
    private RoleMembersXmlDTO.OutsideOfRole roleMembers;

    @XmlElement(name="rolePermissions")
    private RolePermissionsXmlDTO.OutsideOfRole rolePermissions;
    
    public RoleDataXmlDTO() {}
    
    public RoleDataXmlDTO(RolesXmlDTO roles) {
        this.roles = roles;
    }

    /**
     * @return the roles
     */
    public RolesXmlDTO getRoles() {
        return this.roles;
    }

    /**
     * @param roles the roles to set
     */
    public void setRoles(RolesXmlDTO roles) {
        this.roles = roles;
    }

    /**
     * @return the roleMembers
     */
    public RoleMembersXmlDTO.OutsideOfRole getRoleMembers() {
        return this.roleMembers;
    }

    /**
     * @param roleMembers the roleMembers to set
     */
    public void setRoleMembers(RoleMembersXmlDTO.OutsideOfRole roleMembers) {
        this.roleMembers = roleMembers;
    }

    /**
     * @return the rolePermissions
     */
    public RolePermissionsXmlDTO.OutsideOfRole getRolePermissions() {
        return this.rolePermissions;
    }

    /**
     * @param rolePermissions the rolePermissions to set
     */
    public void setRolePermissions(RolePermissionsXmlDTO.OutsideOfRole rolePermissions) {
        this.rolePermissions = rolePermissions;
    }
        
}