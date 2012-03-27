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
package org.kuali.rice.kim.api.common.delegate;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.core.api.CoreConstants;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.mo.AbstractDataTransferObject;
import org.kuali.rice.core.api.mo.ModelBuilder;
import org.kuali.rice.core.api.mo.common.active.InactivatableFromToUtils;
import org.kuali.rice.core.api.util.jaxb.DateTimeAdapter;
import org.kuali.rice.core.api.util.jaxb.MapStringStringAdapter;
import org.kuali.rice.kim.api.KimConstants;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@XmlRootElement(name = DelegateMember.Constants.ROOT_ELEMENT_NAME)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = DelegateMember.Constants.TYPE_NAME, propOrder = {
        DelegateMember.Elements.DELEGATION_MEMBER_ID,
        DelegateMember.Elements.DELEGATION_ID,
        DelegateMember.Elements.MEMBER_ID,
        DelegateMember.Elements.ROLE_MEMBER_ID,
        DelegateMember.Elements.TYPE_CODE,
        DelegateMember.Elements.ATTRIBUTES,
        CoreConstants.CommonElements.ACTIVE_FROM_DATE,
        CoreConstants.CommonElements.ACTIVE_TO_DATE,
        CoreConstants.CommonElements.VERSION_NUMBER,
        CoreConstants.CommonElements.FUTURE_ELEMENTS
})
public final class DelegateMember extends AbstractDataTransferObject
        implements DelegateMemberContract {

    @XmlElement(name = Elements.DELEGATION_MEMBER_ID, required = false)
    private final String delegationMemberId;

    @XmlElement(name = Elements.DELEGATION_ID, required = false)
    private final String delegationId;

    @XmlElement(name = Elements.MEMBER_ID, required = false)
    private final String memberId;

    @XmlElement(name = Elements.ROLE_MEMBER_ID, required = false)
    private final String roleMemberId;

    @XmlElement(name = Elements.TYPE_CODE, required = false)
    private final String typeCode;

    @XmlElement(name = Elements.ATTRIBUTES, required = false)
    @XmlJavaTypeAdapter(value = MapStringStringAdapter.class)
    private final Map<String, String> attributes;

    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_FROM_DATE)
    private final DateTime activeFromDate;

    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(name = CoreConstants.CommonElements.ACTIVE_TO_DATE)
    private final DateTime activeToDate;

    @XmlElement(name = CoreConstants.CommonElements.VERSION_NUMBER)
    private final Long versionNumber;

    @SuppressWarnings("unused")
    @XmlAnyElement
    private final Collection<Element> _futureElements = null;

    /**
     * Private constructor used only by JAXB.
     */
    @SuppressWarnings("unused")
    private DelegateMember() {
        this.delegationMemberId = null;
        this.delegationId = null;
        this.memberId = null;
        this.roleMemberId = null;
        this.typeCode = null;
        this.versionNumber = null;
        this.activeFromDate = null;
        this.activeToDate = null;
        this.attributes = null;
    }

    private DelegateMember(Builder builder) {
        this.delegationMemberId = builder.getDelegationMemberId();
        this.delegationId = builder.getDelegationId();
        this.memberId = builder.getMemberId();
        this.roleMemberId = builder.getRoleMemberId();
        if (builder.getType() == null) {
            this.typeCode = null;
        } else {
            this.typeCode = builder.getType().getCode();
        }
        this.versionNumber = builder.getVersionNumber();
        this.activeFromDate = builder.getActiveFromDate();
        this.activeToDate = builder.getActiveToDate();
        this.attributes = builder.getAttributes();
    }


    @Override
    public String getDelegationMemberId() {
        return this.delegationMemberId;
    }

    @Override
    public String getDelegationId() {
        return this.delegationId;
    }

    @Override
    public MemberType getType() {
        return MemberType.fromCode(this.typeCode);
    }

    @Override
    public String getRoleMemberId() {
        return this.roleMemberId;
    }

    @Override
    public String getMemberId() {
        return this.memberId;
    }

    @Override
    public Long getVersionNumber() {
        return this.versionNumber;
    }

    @Override
    public DateTime getActiveFromDate() {
        return activeFromDate;
    }

    @Override
    public DateTime getActiveToDate() {
        return activeToDate;
    }

    /**
     * @return the attributes
     */
    @Override
    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    @Override
    public boolean isActive(DateTime activeAsOfDate) {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOfDate);
    }

    @Override
    public boolean isActive() {
        return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, null);
    }

    /**
     * A builder which can be used to construct {@link DelegateMember} instances.  Enforces the constraints of the {@link DelegateMemberContract}.
     */
    public static final class Builder implements Serializable, ModelBuilder, DelegateMemberContract {

        private String delegationMemberId;
        private String delegationId;
        private String memberId;
        private String roleMemberId;
        private MemberType type;
        private Map<String, String> attributes;
        private DateTime activeFromDate;
        private DateTime activeToDate;
        private Long versionNumber;

        private Builder() {

        }

        public static Builder create() {
            return new Builder();
        }

        public static Builder create(DelegateMemberContract contract) {
            if (contract == null) {
                throw new IllegalArgumentException("contract was null");
            }
            Builder builder = create();
            builder.setDelegationMemberId(contract.getDelegationMemberId());
            builder.setDelegationId(contract.getDelegationId());
            builder.setMemberId(contract.getMemberId());
            builder.setRoleMemberId(contract.getRoleMemberId());
            builder.setAttributes(contract.getAttributes());
            builder.setType(contract.getType());
            builder.setActiveFromDate(contract.getActiveFromDate());
            builder.setActiveToDate(contract.getActiveToDate());
            builder.setVersionNumber(contract.getVersionNumber());
            return builder;
        }

        public DelegateMember build() {
            return new DelegateMember(this);
        }

        @Override
        public String getDelegationMemberId() {
            return this.delegationMemberId;
        }

        public void setDelegationMemberId(String delegationMemberId) {
            if (StringUtils.isWhitespace(delegationMemberId)) {
                throw new IllegalArgumentException("delegationMemberId cannot be whitespace");
            }
            this.delegationMemberId = delegationMemberId;
        }

        public String getDelegationId() {
            return delegationId;
        }

        public void setDelegationId(String delegationId) {
            this.delegationId = delegationId;
        }

        public String getMemberId() {
            return memberId;
        }

        public void setMemberId(String memberId) {
            this.memberId = memberId;
        }

        public String getRoleMemberId() {
            return roleMemberId;
        }

        public void setRoleMemberId(String roleMemberId) {
            this.roleMemberId = roleMemberId;
        }

        public MemberType getType() {
            return type;
        }

        public void setType(MemberType type) {
            this.type = type;
        }

        @Override
        public Map<String, String> getAttributes() {
            return this.attributes;
        }

        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }

        @Override
        public Long getVersionNumber() {
            return versionNumber;
        }

        public void setVersionNumber(Long versionNumber) {
            this.versionNumber = versionNumber;
        }

        @Override
        public DateTime getActiveFromDate() {
            return activeFromDate;
        }

        public void setActiveFromDate(DateTime activeFromDate) {
            this.activeFromDate = activeFromDate;
        }

        @Override
        public DateTime getActiveToDate() {
            return activeToDate;
        }

        public void setActiveToDate(DateTime activeToDate) {
            this.activeToDate = activeToDate;
        }

        @Override
        public boolean isActive(DateTime activeAsOfDate) {
            return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, activeAsOfDate);
        }

        @Override
        public boolean isActive() {
            return InactivatableFromToUtils.isActive(activeFromDate, activeToDate, null);
        }
    }

    /**
     * Defines some internal constants used on this class.
     */
    static class Constants {

        final static String ROOT_ELEMENT_NAME = "delegateMember";
        final static String TYPE_NAME = "DelegateMemberType";

    }

    /**
     * A private class which exposes constants which define the XML element names to use when this object is marshalled to XML.
     */
    static class Elements {
        final static String DELEGATION_MEMBER_ID = "delegationMemberId";
        final static String ROLE_ID = "roleId";
        final static String DELEGATION_ID = "delegationId";
        final static String MEMBER_ID = "memberId";
        final static String ATTRIBUTES = "attributes";
        final static String ROLE_MEMBER_ID = "roleMemberId";
        final static String TYPE_CODE = "typeCode";
    }

    public static class Cache {
        public static final String NAME = KimConstants.Namespaces.KIM_NAMESPACE_2_0 + "/" + DelegateMember.Constants.TYPE_NAME;
    }
}
