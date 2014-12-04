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

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.core.util.jaxb.NameAndNamespacePair;
import org.kuali.rice.core.util.jaxb.NameAndNamespacePairValidatingAdapter;
import org.kuali.rice.kim.api.group.GroupContract;
import org.kuali.rice.kim.api.identity.principal.PrincipalContract;
import org.kuali.rice.kim.api.jaxb.QualificationListAdapter;
import org.kuali.rice.kim.api.role.RoleContract;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

/**
 * Base class representing an unmarshalled &lt;roleMember&gt; element.
 * Refer to the static inner classes for more information about the specific contexts.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlTransient
public abstract class RoleMemberXmlDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name="principalId")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String principalId;

    @XmlElement(name="principalName")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String principalName;

    @XmlElement(name="groupId")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String groupId;

    @XmlElement(name="groupName")
    @XmlJavaTypeAdapter(NameAndNamespacePairValidatingAdapter.class)
    private NameAndNamespacePair groupName;

    @XmlElement(name="roleIdAsMember")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String roleIdAsMember;

    @XmlElement(name="roleNameAsMember")
    @XmlJavaTypeAdapter(NameAndNamespacePairValidatingAdapter.class)
    private NameAndNamespacePair roleNameAsMember;

    @XmlElement(name="activeFromDate")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private DateTime activeFromDate;

    @XmlElement(name="activeToDate")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    private DateTime activeToDate;

    @XmlElement(name="qualifications")
    @XmlJavaTypeAdapter(QualificationListAdapter.class)
    private Map<String, String> qualifications;

    @XmlTransient
    private MemberType memberType;

    /**
     * Constructs an empty RoleMemberXmlDTO instance.
     */
    public RoleMemberXmlDTO() {}

    /**
     * Constructs a RoleMemberXmlDTO instance that is populated with the info from the given role member.
     *
     * @param roleMember The role member that this DTO should populate its data from.
     * @param populateMemberId If true, the member principal/group/role ID will get populated; otherwise, only
     * the member principal/group/role name and (if applicable) namespace will get populated.
     * @throws IllegalArgumentException if roleMember is null, has an invalid member type, or refers to a nonexistent principal/group/role.
     */
    public RoleMemberXmlDTO(RoleMember roleMember, boolean populateMemberId) {
        if (roleMember == null) {
            throw new IllegalArgumentException("roleMember cannot be null");
        }
        this.memberType = roleMember.getType();
        this.activeFromDate = roleMember.getActiveFromDate();
        this.activeToDate = roleMember.getActiveToDate();
        this.qualifications = (roleMember.getAttributes() != null) ? roleMember.getAttributes() : new HashMap<String, String>();

        if (MemberType.PRINCIPAL.equals(memberType)) {
            if (populateMemberId) {
                this.principalId = roleMember.getMemberId();
            }
            PrincipalContract principal = KimApiServiceLocator.getIdentityService().getPrincipal(
                    roleMember.getMemberId());
            if (principal == null) {
                throw new IllegalArgumentException("Cannot find principal with ID \"" +  roleMember.getMemberId() + "\"");
            }
            this.principalName = principal.getPrincipalName();
        } else if (MemberType.GROUP.equals(memberType)) {
            if (populateMemberId) {
                this.groupId = roleMember.getMemberId();
            }
            GroupContract group = KimApiServiceLocator.getGroupService().getGroup(roleMember.getMemberId());
            if (group == null) {
                throw new IllegalArgumentException("Cannot find group with ID \"" + roleMember.getMemberId() + "\"");
            }
            this.groupName = new NameAndNamespacePair(group.getNamespaceCode(), group.getName());
        } else if (MemberType.ROLE.equals(memberType)) {
            if (populateMemberId) {
                this.roleIdAsMember = roleMember.getMemberId();
            }
            RoleContract role = KimApiServiceLocator.getRoleService().getRole(roleMember.getMemberId());
            if (role == null) {
                throw new IllegalArgumentException("Cannot find role with ID \"" + roleMember.getMemberId() + "\"");
            }
            this.roleNameAsMember = new NameAndNamespacePair(role.getNamespaceCode(), role.getName());
        } else {
            throw new IllegalArgumentException("Cannot construct a RoleMemberXmlDTO from a role member with an unrecognized member type code of \"" +
                    memberType + "\"");
        }
    }

    /**
     * @return the principalId
     */
    public String getPrincipalId() {
        return this.principalId;
    }

    /**
     * @param principalId the principalId to set
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * @return the principalName
     */
    public String getPrincipalName() {
        return this.principalName;
    }

    /**
     * @param principalName the principalName to set
     */
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return this.groupId;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the groupName
     */
    public NameAndNamespacePair getGroupName() {
        return this.groupName;
    }

    /**
     * @param groupName the groupName to set
     */
    public void setGroupName(NameAndNamespacePair groupName) {
        this.groupName = groupName;
    }

    /**
     * @return the roleIdAsMember
     */
    public String getRoleIdAsMember() {
        return this.roleIdAsMember;
    }

    /**
     * @param roleIdAsMember the roleIdAsMember to set
     */
    public void setRoleIdAsMember(String roleIdAsMember) {
        this.roleIdAsMember = roleIdAsMember;
    }

    /**
     * @return the roleNameAsMember
     */
    public NameAndNamespacePair getRoleNameAsMember() {
        return this.roleNameAsMember;
    }

    /**
     * @param roleNameAsMember the roleNameAsMember to set
     */
    public void setRoleNameAsMember(NameAndNamespacePair roleNameAsMember) {
        this.roleNameAsMember = roleNameAsMember;
    }

    /**
     * @return the activeFromDate
     */
    public DateTime getActiveFromDate() {
        return this.activeFromDate;
    }

    /**
     * @param activeFromDate the activeFromDate to set
     */
    public void setActiveFromDate(DateTime activeFromDate) {
        this.activeFromDate = activeFromDate;
    }

    /**
     * @return the activeToDate
     */
    public DateTime getActiveToDate() {
        return this.activeToDate;
    }

    /**
     * @param activeToDate the activeToDate to set
     */
    public void setActiveToDate(DateTime activeToDate) {
        this.activeToDate = activeToDate;
    }

    /**
     * @return the qualifications
     */
    public Map<String, String> getQualifications() {
        return this.qualifications;
    }

    /**
     * @param qualifications the qualifications to set
     */
    public void setQualifications(Map<String, String> qualifications) {
        this.qualifications = qualifications;
    }

    /**
     * Retrieves the member type.
     *
     * <p>If the member type is null at the time that this method is invoked, an attempt will be made to set its
     * value based on any populated member principal/group/role ID/name information.
     *
     * @return the member type, or null if no membership identification information has been set on this member.
     * @throws IllegalStateException if the role member is populated simultaneously with multiple member ID/name information
     */
    public MemberType getMemberType() {
        if (memberType == null) {
            boolean foundMemberInfo = false;

            if (StringUtils.isNotBlank(principalId) || StringUtils.isNotBlank(principalName)) {
                memberType = MemberType.PRINCIPAL;
                foundMemberInfo = true;
            }

            if (StringUtils.isNotBlank(groupId) || groupName != null) {
                if (foundMemberInfo) {
                    memberType = null;
                    throw new IllegalStateException("Cannot have a role member that is simultaneously populated with member principal, member group, and/or member role information");
                }
                memberType = MemberType.GROUP;
                foundMemberInfo = true;
            }

            if (StringUtils.isNotBlank(roleIdAsMember) || roleNameAsMember != null) {
                if (foundMemberInfo) {
                    memberType = null;
                    throw new IllegalStateException("Cannot have a role member that is simultaneously populated with member principal, member group, and/or member role information");
                }
                memberType = MemberType.ROLE;
                foundMemberInfo = true;
            }
        }
        return this.memberType;
    }

    /**
     * Retrieves the role member's ID, based on the member type and any populated member principal/group/role IDs.
     *
     * <p>If the member type is null at the time that this method is invoked, an attempt will be made to set its
     * value based on any populated member principal/group/role ID/name information.
     *
     * @return The member's ID, or null if the member type is null or the associated member ID information is null.
     */
    public String getMemberId() {
        if (MemberType.PRINCIPAL.equals(getMemberType())) {
            return principalId;
        } else if (MemberType.GROUP.equals(getMemberType())) {
            return groupId;
        } else if (MemberType.ROLE.equals(getMemberType())) {
            return roleIdAsMember;
        }
        return null;
    }

    /**
     * Retrieves the role member's name, based on the member type and any populated member principal/group/role names.
     *
     * <p>If the member type is null at the time that this method is invoked, an attempt will be made to set its
     * value based on any populated member principal/group/role ID/name information.
     *
     * @return The member's name, or null if the member type is null or the associated member name information is null.
     */
    public String getMemberName() {
        if (MemberType.PRINCIPAL.equals(getMemberType())) {
            return principalName;
        } else if (MemberType.GROUP.equals(getMemberType())) {
            return (groupName != null) ? groupName.getName() : null;
        } else if (MemberType.ROLE.equals(getMemberType())) {
            return (roleNameAsMember != null) ? roleNameAsMember.getName() : null;
        }
        return null;
    }

    /**
     * Retrieves the role member's namespace code, based on the member type and any populated member principal/group/role names.
     *
     * <p>If the member type is null at the time that this method is invoked, an attempt will be made to set its
     * value based on any populated member principal/group/role ID/name information.
     *
     * @return The member's namespace code, or null if the member type is null, the associated member name information is null,
     * or the role member is a principal.
     */
    public String getMemberNamespaceCode() {
        if (MemberType.PRINCIPAL.equals(getMemberType())) {
            return null;
        } else if (MemberType.GROUP.equals(getMemberType())) {
            return (groupName != null) ? groupName.getName() : null;
        } else if (MemberType.ROLE.equals(getMemberType())) {
            return (roleNameAsMember != null) ? roleNameAsMember.getName() : null;
        }
        return null;
    }

    /**
     * Retrieves the ID of the role that this member belongs to.
     * Subclasses are responsible for implementing this method so that it does so.
     *
     * @return The role ID of the role that this member belongs to.
     */
    public abstract String getRoleId();

    // =======================================================================================================

    /**
     * This class represents a &lt;roleMember&gt; element that is not a descendant of a &lt;role&gt; element.
     *
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name="StandaloneRoleMemberType", propOrder={
            "roleId", "roleNameAndNamespace", "principalId", "principalName", "groupId", "groupName", "roleIdAsMember",
                    "roleNameAsMember", "activeFromDate", "activeToDate", "qualifications"
    })
    public static class OutsideOfRole extends RoleMemberXmlDTO {

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

        public OutsideOfRole(RoleMember roleMember, boolean populateMemberId) {
            super(roleMember, populateMemberId);
            this.roleId = roleMember.getRoleId();
            RoleContract tempRole = KimApiServiceLocator.getRoleService().getRole(roleId);
            if (tempRole == null) {
                throw new IllegalArgumentException("Cannot find role with ID \"" + roleId + "\"");
            }
            this.roleNameAndNamespace = new NameAndNamespacePair(tempRole.getNamespaceCode(), tempRole.getName());
        }

        /**
         * @see org.kuali.rice.kim.impl.jaxb.RoleMemberXmlDTO#getRoleId()
         */
        @Override
        public String getRoleId() {
            return roleId;
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
         * @return The name of the role that this member belongs to, or null if the role-name-and-namespace combo is null.
         */
        public String getRoleName() {
            return (roleNameAndNamespace != null) ? roleNameAndNamespace.getName() : null;
        }

        /**
         * Retrieves the role namespace code from the role-name-and-namespace combo.
         *
         * @return The namespace code of the role that this member belongs to, or null if the role-name-and-namespace combo is null.
         */
        public String getRoleNamespaceCode() {
            return (roleNameAndNamespace != null) ? roleNameAndNamespace.getNamespaceCode() : null;
        }
    }

    // =======================================================================================================

    /**
     * This class represents a &lt;roleMember&gt; element that is a descendant of a &lt;role&gt; element.
     *
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name="RoleMemberType", propOrder={
            "principalId", "principalName", "groupId", "groupName", "roleIdAsMember",
                    "roleNameAsMember", "activeFromDate", "activeToDate", "qualifications"
    })
    public static class WithinRole extends RoleMemberXmlDTO {

        private static final long serialVersionUID = 1L;

        @XmlTransient
        private String roleId;

        public WithinRole() {
            super();
        }

        public WithinRole(RoleMember roleMember, boolean populateMemberId) {
            super(roleMember, populateMemberId);
            this.roleId = roleMember.getRoleId();
        }

        void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) {
            if (parent instanceof RoleMembersXmlDTO.WithinRole) {
                this.roleId = ((RoleMembersXmlDTO.WithinRole)parent).getRoleId();
            }
        }

        /**
         * @see org.kuali.rice.kim.impl.jaxb.RoleMemberXmlDTO#getRoleId()
         */
        @Override
        public String getRoleId() {
            return roleId;
        }

    }
}
