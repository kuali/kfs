/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * Represents a member who can be assigned to a model. Can be of Person, Role, or Group type
 */
public class ModelMember extends PersistableBusinessObjectBase implements MutableInactivatable {
    private String memberId;
    private String memberName;
    private String memberTypeCode;
    private boolean active;

    // person fields
    private String principalId;
    private String principalName;
    private String entityId;
    private String firstName = "";
    private String middleName = "";
    private String lastName = "";
    private String emailAddress = "";
    private String employeeId = "";

    // role fields
    private String roleId;
    private String roleName;
    private String namespaceCode; // part of group fields as well

    // group fields
    private String groupId;
    private String groupName;

    public ModelMember() {
        super();
    }


    /**
     * Gets the memberId attribute.
     * 
     * @return Returns the memberId.
     */
    public String getMemberId() {
        return memberId;
    }


    /**
     * Sets the memberId attribute value.
     * 
     * @param memberId The memberId to set.
     */
    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }


    /**
     * Gets the memberName attribute.
     * 
     * @return Returns the memberName.
     */
    public String getMemberName() {
        return memberName;
    }


    /**
     * Sets the memberName attribute value.
     * 
     * @param memberName The memberName to set.
     */
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }


    /**
     * Gets the memberTypeCode attribute.
     * 
     * @return Returns the memberTypeCode.
     */
    public String getMemberTypeCode() {
        return memberTypeCode;
    }


    /**
     * Sets the memberTypeCode attribute value.
     * 
     * @param memberTypeCode The memberTypeCode to set.
     */
    public void setMemberTypeCode(String memberTypeCode) {
        this.memberTypeCode = memberTypeCode;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }


    /**
     * Sets the principalId attribute value.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the principalName attribute.
     * 
     * @return Returns the principalName.
     */
    public String getPrincipalName() {
        return principalName;
    }


    /**
     * Sets the principalName attribute value.
     * 
     * @param principalName The principalName to set.
     */
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }


    /**
     * Gets the entityId attribute.
     * 
     * @return Returns the entityId.
     */
    public String getEntityId() {
        return entityId;
    }


    /**
     * Sets the entityId attribute value.
     * 
     * @param entityId The entityId to set.
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }


    /**
     * Gets the firstName attribute.
     * 
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName attribute value.
     * 
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the middleName attribute.
     * 
     * @return Returns the middleName.
     */
    public String getMiddleName() {
        return middleName;
    }


    /**
     * Sets the middleName attribute value.
     * 
     * @param middleName The middleName to set.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }


    /**
     * Gets the lastName attribute.
     * 
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName attribute value.
     * 
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the emailAddress attribute.
     * 
     * @return Returns the emailAddress.
     */
    public String getEmailAddress() {
        return emailAddress;
    }


    /**
     * Sets the emailAddress attribute value.
     * 
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    /**
     * Gets the employeeId attribute.
     * 
     * @return Returns the employeeId.
     */
    public String getEmployeeId() {
        return employeeId;
    }


    /**
     * Sets the employeeId attribute value.
     * 
     * @param employeeId The employeeId to set.
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }


    /**
     * Gets the roleId attribute.
     * 
     * @return Returns the roleId.
     */
    public String getRoleId() {
        return roleId;
    }


    /**
     * Sets the roleId attribute value.
     * 
     * @param roleId The roleId to set.
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }


    /**
     * Gets the roleName attribute.
     * 
     * @return Returns the roleName.
     */
    public String getRoleName() {
        return roleName;
    }


    /**
     * Sets the roleName attribute value.
     * 
     * @param roleName The roleName to set.
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }


    /**
     * Gets the namespaceCode attribute.
     * 
     * @return Returns the namespaceCode.
     */
    public String getNamespaceCode() {
        return namespaceCode;
    }


    /**
     * Sets the namespaceCode attribute value.
     * 
     * @param namespaceCode The namespaceCode to set.
     */
    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }


    /**
     * Gets the groupId attribute.
     * 
     * @return Returns the groupId.
     */
    public String getGroupId() {
        return groupId;
    }


    /**
     * Sets the groupId attribute value.
     * 
     * @param groupId The groupId to set.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    /**
     * Gets the groupName attribute.
     * 
     * @return Returns the groupName.
     */
    public String getGroupName() {
        return groupName;
    }


    /**
     * Sets the groupName attribute value.
     * 
     * @param groupName The groupName to set.
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(SecPropertyConstants.MEMBER_TYPE_CODE, this.memberTypeCode);
        m.put(SecPropertyConstants.MEMBER_ID, this.memberId);

        return m;
    }

}
