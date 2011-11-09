/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sec.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.KimApiConstants; import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiInteger;


/**
 * Associates a member (principal, role, or group) to a model. These become the members of the model role created in KIM
 */
public class SecurityModelMember extends PersistableBusinessObjectBase {
    private KualiInteger modelId;
    private String memberId;
    private String memberTypeCode;
    private Timestamp activeFromDate;
    private Timestamp activeToDate;

    private SecurityModel securityModel;

    // non db
    private String memberName;

    private ModelMember modelMember;

    public SecurityModelMember() {
        super();
    }


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
        if (StringUtils.isNotBlank(memberName)) {
            return memberName;
        }

        if (StringUtils.isNotBlank(memberTypeCode) && StringUtils.isNotBlank(memberId)) {
            if (KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(memberTypeCode)) {
                Person person = SpringContext.getBean(PersonService.class).getPerson(memberId);
                if (person != null) {
                    return person.getName();
                }
            }
            else if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(memberTypeCode)) {
                Role roleInfo = SpringContext.getBean(RoleService.class).getRole(memberId);
                if (roleInfo != null) {
                    return roleInfo.getName();
                }
            }
            else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(memberTypeCode)) {
                Group groupInfo = SpringContext.getBean(GroupService.class).getGroup(memberId);
                if (groupInfo != null) {
                    return groupInfo.getGroupName();
                }
            }
        }

        return "";
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


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(SecPropertyConstants.MODEL_ID, this.modelId);
        m.put(SecPropertyConstants.MEMBER_ID, this.memberId);

        return m;
    }

}
