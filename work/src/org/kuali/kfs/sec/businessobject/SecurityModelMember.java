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
package org.kuali.kfs.sec.businessobject;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;


/**
 * Associates a member (principal, role, or group) to a model. These become the members of the model role created in KIM
 */
public class SecurityModelMember extends PersistableBusinessObjectBase {
    protected KualiInteger modelId;
    protected String memberId;
    protected String memberTypeCode;
    protected Timestamp activeFromDate;
    protected Timestamp activeToDate;

    protected SecurityModel securityModel;

    // non db
    protected String memberName = "";

    protected ModelMember modelMember;

    /**
     * Gets the modelId attribute.
     *
     * @return Returns the modelId.
     */
    public KualiInteger getModelId() {
        return modelId;
    }


    /**
     * Sets the modelId attribute value.
     *
     * @param modelId The modelId to set.
     */
    public void setModelId(KualiInteger modelId) {
        this.modelId = modelId;
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
        memberName = "";
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
        memberName = "";
    }


    /**
     * Gets the activeFromDate attribute.
     *
     * @return Returns the activeFromDate.
     */
    public Timestamp getActiveFromDate() {
        return activeFromDate;
    }


    /**
     * Sets the activeFromDate attribute value.
     *
     * @param activeFromDate The activeFromDate to set.
     */
    public void setActiveFromDate(Timestamp activeFromDate) {
        this.activeFromDate = activeFromDate;
    }


    /**
     * Gets the activeToDate attribute.
     *
     * @return Returns the activeToDate.
     */
    public Timestamp getActiveToDate() {
        return activeToDate;
    }


    /**
     * Sets the activeToDate attribute value.
     *
     * @param activeToDate The activeToDate to set.
     */
    public void setActiveToDate(Timestamp activeToDate) {
        this.activeToDate = activeToDate;
    }


    /**
     * Gets the memberName attribute.
     *
     * @return Returns the memberName.
     */
    public String getMemberName() {
        if ( StringUtils.isBlank(memberName) ) {
            if (StringUtils.isNotBlank(memberTypeCode) && StringUtils.isNotBlank(memberId)) {
                if (MemberType.PRINCIPAL.getCode().equals(memberTypeCode)) {
                    Person person = SpringContext.getBean(PersonService.class).getPerson(memberId);
                    if (person != null) {
                        memberName = person.getName();
                    }
                } else if (MemberType.ROLE.getCode().equals(memberTypeCode)) {
                    Role roleInfo = KimApiServiceLocator.getRoleService().getRole(memberId);
                    if (roleInfo != null) {
                        memberName = roleInfo.getName();
                    }
                } else if (MemberType.GROUP.getCode().equals(memberTypeCode)) {
                    Group groupInfo = KimApiServiceLocator.getGroupService().getGroup(memberId);
                    if (groupInfo != null) {
                        memberName = groupInfo.getName();
                    }
                }
            }
        }

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
     * Gets the securityModel attribute.
     *
     * @return Returns the securityModel.
     */
    public SecurityModel getSecurityModel() {
        return securityModel;
    }


    /**
     * Sets the securityModel attribute value.
     *
     * @param securityModel The securityModel to set.
     */
    public void setSecurityModel(SecurityModel securityModel) {
        this.securityModel = securityModel;
    }


    /**
     * Gets the modelMember attribute.
     *
     * @return Returns the modelMember.
     */
    public ModelMember getModelMember() {
        return modelMember;
    }


    /**
     * Sets the modelMember attribute value.
     *
     * @param modelMember The modelMember to set.
     */
    public void setModelMember(ModelMember modelMember) {
        this.modelMember = modelMember;
    }

    /**
     * Builds a string representation of the model definition assignmentss
     *
     * @return String
     */
    public String getModelDefinitionSummary() {
        String summary = "";

        for (SecurityModelDefinition modelDefinition : securityModel.getModelDefinitions()) {
            summary += "Definition Name: " + modelDefinition.getSecurityDefinition().getName();
            summary += ", Constraint Code: " + modelDefinition.getConstraintCode();
            summary += ", Operator Code: " + modelDefinition.getOperatorCode();
            summary += ", Value: " + modelDefinition.getAttributeValue();
            summary += "; ";
        }

        return summary;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecurityModelMember [");
        if (modelId != null) {
            builder.append("modelId=");
            builder.append(modelId);
            builder.append(", ");
        }
        if (memberId != null) {
            builder.append("memberId=");
            builder.append(memberId);
            builder.append(", ");
        }
        if (memberTypeCode != null) {
            builder.append("memberTypeCode=");
            builder.append(memberTypeCode);
            builder.append(", ");
        }
        if (activeFromDate != null) {
            builder.append("activeFromDate=");
            builder.append(activeFromDate);
            builder.append(", ");
        }
        if (activeToDate != null) {
            builder.append("activeToDate=");
            builder.append(activeToDate);
            builder.append(", ");
        }
        if (getMemberName() != null) {
            builder.append("getMemberName()=");
            builder.append(getMemberName());
            builder.append(", ");
        }
        if (getModelDefinitionSummary() != null) {
            builder.append("getModelDefinitionSummary()=");
            builder.append(getModelDefinitionSummary());
        }
        builder.append("]");
        return builder.toString();
    }


}
