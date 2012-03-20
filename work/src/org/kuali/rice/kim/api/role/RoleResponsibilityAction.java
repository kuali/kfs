/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.kim.api.role;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.ModelObjectComplete;
import org.kuali.rice.kim.api.KimConstants;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlRootElement(name = RoleResponsibilityAction.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = RoleResponsibilityAction.Constants.TYPE_NAME, propOrder = {
        RoleResponsibilityAction.Elements.ID,
        RoleResponsibilityAction.Elements.ROLE_RESPONSIBILITY_ID,
        RoleResponsibilityAction.Elements.ROLE_MEMBER_ID,
        RoleResponsibilityAction.Elements.ACTION_TYPE_CODE,
        RoleResponsibilityAction.Elements.ACTION_POLICY_CODE,
        RoleResponsibilityAction.Elements.FORCE_ACTION,
        RoleResponsibilityAction.Elements.PRIORITY_NUMBER,
        RoleResponsibilityAction.Elements.ROLE_RESPONSIBILITY,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public class RoleResponsibilityAction extends AbstractDataTransferObject implements RoleResponsibilityActionContract {

    @XmlElement(name = RoleResponsibilityAction.Elements.ID, required = true)
    private final String id;

    @XmlElement(name = RoleResponsibilityAction.Elements.ROLE_RESPONSIBILITY_ID)
    private final String roleResponsibilityId;

    @XmlElement(name = RoleResponsibilityAction.Elements.ROLE_MEMBER_ID)
    private final String roleMemberId;

    @XmlElement(name = RoleResponsibilityAction.Elements.ACTION_TYPE_CODE)
    private final String actionTypeCode;

    @XmlElement(name = RoleResponsibilityAction.Elements.ACTION_POLICY_CODE)
    private final String actionPolicyCode;

    @XmlElement(name = RoleResponsibilityAction.Elements.FORCE_ACTION)
    private final boolean forceAction;

    @XmlElement(name = RoleResponsibilityAction.Elements.PRIORITY_NUMBER)
    private final Integer priorityNumber;

    @XmlElement(name = RoleResponsibilityAction.Elements.ROLE_RESPONSIBILITY)
    private final RoleResponsibility roleResponsibility;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER)
    private final Long versionNumber;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private default constructor should only be called by JAXB
     */
    @SuppressWarnings("unused")
    private RoleResponsibilityAction() {
        id = null;
        roleResponsibilityId = null;
        roleMemberId = null;
        actionTypeCode = null;
        actionPolicyCode = null;
        forceAction = false;
        priorityNumber = null;
        roleResponsibility = null;
        versionNumber = null;
    }

    private RoleResponsibilityAction(Builder b) {
        id = b.getId();
        roleResponsibilityId = b.getRoleResponsibilityId();
        roleMemberId = b.getRoleMemberId();
        actionTypeCode = b.getActionTypeCode();
        actionPolicyCode = b.getActionPolicyCode();
        forceAction = b.isForceAction();
        priorityNumber = b.getPriorityNumber();
        roleResponsibility = b.getRoleResponsibility();
        versionNumber = b.getVersionNumber();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getRoleResponsibilityId() {
        return this.roleResponsibilityId;
    }

    @Override
    public String getActionTypeCode() {
        return this.actionTypeCode;
    }

    @Override
    public Integer getPriorityNumber() {
        return this.priorityNumber;
    }

    @Override
    public String getActionPolicyCode() {
        return this.actionPolicyCode;
    }

    @Override
    public String getRoleMemberId() {
        return this.roleMemberId;
    }

    @Override
    public RoleResponsibility getRoleResponsibility() {
        return this.roleResponsibility;
    }

    @Override
    public boolean isForceAction() {
        return this.forceAction;
    }

    @Override
    public Long getVersionNumber() {
        return this.versionNumber;
    }

    public static class Builder implements RoleResponsibilityActionContract, ModelBuilder, ModelObjectComplete {

        private String Id;
        private String roleResponsibilityId;
        private String roleMemberId;
        private String actionTypeCode;
        private String actionPolicyCode;
        private boolean forceAction;
        private Integer priorityNumber;
        private RoleResponsibility roleResponsibility;
        private Long versionNumber;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(RoleResponsibilityActionContract rra) {
            Builder b = new Builder();
            b.setForceAction(rra.isForceAction());
            b.setActionPolicyCode(rra.getActionPolicyCode());
            b.setActionTypeCode(rra.getActionTypeCode());
            b.setPriorityNumber(rra.getPriorityNumber());
            b.setRoleMemberId(rra.getRoleMemberId());
            if (rra.getRoleResponsibility() != null) {
                b.setRoleResponsibility(RoleResponsibility.Builder.create(rra.getRoleResponsibility()).build());
            }
            b.setId(rra.getId());
            b.setRoleResponsibilityId(rra.getRoleResponsibilityId());
            b.setVersionNumber(rra.getVersionNumber());
            return b;
        }

        @Override
        public RoleResponsibilityAction build() {
            return new RoleResponsibilityAction(this);
        }

        @Override
        public String getId() {
            return Id;
        }

        public void setId(String id) {
            this.Id = id;
        }

        @Override
        public String getRoleResponsibilityId() {
            return roleResponsibilityId;
        }

        public void setRoleResponsibilityId(String roleResponsibilityId) {
            this.roleResponsibilityId = roleResponsibilityId;
        }

        @Override
        public String getActionTypeCode() {
            return actionTypeCode;
        }

        public void setActionTypeCode(String actionTypeCode) {
            this.actionTypeCode = actionTypeCode;
        }

        @Override
        public Integer getPriorityNumber() {
            return priorityNumber;
        }

        public void setPriorityNumber(Integer priorityNumber) {
            this.priorityNumber = priorityNumber;
        }

        @Override
        public String getActionPolicyCode() {
            return actionPolicyCode;
        }

        public void setActionPolicyCode(String actionPolicyCode) {
            this.actionPolicyCode = actionPolicyCode;
        }

        @Override
        public String getRoleMemberId() {
            return roleMemberId;
        }

        public void setRoleMemberId(String roleMemberId) {
            this.roleMemberId = roleMemberId;
        }

        @Override
        public RoleResponsibility getRoleResponsibility() {
            return roleResponsibility;
        }

        public void setRoleResponsibility(RoleResponsibility roleResponsibility) {
            this.roleResponsibility = roleResponsibility;
        }

        @Override
        public boolean isForceAction() {
            return forceAction;
        }

        public void setForceAction(boolean forceAction) {
            this.forceAction = forceAction;
        }

        @Override
        public Long getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(obj, this);
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }


    /**
     * A private class which exposes constants which define the XML element names to use
     * when this object is marshalled to XML.
     */
    static class Elements {
        final static String ID = "id";
        final static String ROLE_RESPONSIBILITY_ID = "roleResponsibilityId";
        final static String ROLE_MEMBER_ID = "roleMemberId";
        final static String ACTION_TYPE_CODE = "actionTypeCode";
        final static String ACTION_POLICY_CODE = "actionPolicyCode";
        final static String FORCE_ACTION = "forceAction";
        final static String PRIORITY_NUMBER = "priorityNumber";
        final static String ROLE_RESPONSIBILITY = "roleResponsibility";
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {
        final static String ROOT_ELEMENT_NAME = "roleResponsibilityAction";
        final static String TYPE_NAME = "RoleResponsibilityActionType";
    }

    public static class Cache {
        public static final String NAME = KimConstants.Namespaces.KIM_NAMESPACE_2_0 + "/" + RoleResponsibilityAction.Constants.TYPE_NAME;
    }
}
