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

import static org.kuali.rice.core.api.criteria.PredicateFactory.equal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.util.jaxb.RiceXmlExportList;
import org.kuali.rice.core.util.jaxb.RiceXmlImportList;
import org.kuali.rice.core.util.jaxb.RiceXmlListAdditionListener;
import org.kuali.rice.core.util.jaxb.RiceXmlListGetterListener;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberContract;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

/**
 * Base class representing an unmarshalled &lt;roleMembers&gt; element.
 * Refer to the static inner classes for more information about the specific contexts.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@XmlTransient
public abstract class RoleMembersXmlDTO<T extends RoleMemberXmlDTO> implements RiceXmlListAdditionListener<T>, Serializable {

    private static final long serialVersionUID = 1L;

    public abstract List<T> getRoleMembers();
    
    public abstract void setRoleMembers(List<T> roleMembers);
    
    void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) throws UnmarshalException {
        setRoleMembers(new RiceXmlImportList<T>(this));
    }
    
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) throws UnmarshalException {
        setRoleMembers(null);
    }
    
    // =======================================================================================================
    
    /**
     * This class represents a &lt;roleMembers&gt; element that is not a child of a &lt;role&gt; element.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name="StandaloneRoleMembersType", propOrder={"roleMembers"})
    public static class OutsideOfRole extends RoleMembersXmlDTO<RoleMemberXmlDTO.OutsideOfRole> {

        private static final long serialVersionUID = 1L;
        
        @XmlElement(name="roleMember")
        private List<RoleMemberXmlDTO.OutsideOfRole> roleMembers;

        /**
         * @see org.kuali.rice.kim.impl.jaxb.RoleMembersXmlDTO#getRoleMembers()
         */
        @Override
        public List<RoleMemberXmlDTO.OutsideOfRole> getRoleMembers() {
            return this.roleMembers;
        }

        /**
         * @see org.kuali.rice.kim.impl.jaxb.RoleMembersXmlDTO#setRoleMembers(java.util.List)
         */
        @Override
        public void setRoleMembers(List<RoleMemberXmlDTO.OutsideOfRole> roleMembers) {
            this.roleMembers = roleMembers;
        }

        /**
         * @see org.kuali.rice.core.util.jaxb.RiceXmlListAdditionListener#newItemAdded(java.lang.Object)
         */
        @Override
        public void newItemAdded(RoleMemberXmlDTO.OutsideOfRole item) {
            try {
                RoleXmlUtil.validateAndPersistNewRoleMember(item);
            } catch (UnmarshalException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    // =======================================================================================================
    
    /**
     * This class represents a &lt;roleMembers&gt; element that is a child of a &lt;role&gt; element.
     * 
     * @author Kuali Rice Team (rice.collab@kuali.org)
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name="RoleMembersType", propOrder={"roleMembers"})
    public static class WithinRole extends RoleMembersXmlDTO<RoleMemberXmlDTO.WithinRole>
            implements RiceXmlListGetterListener<RoleMemberXmlDTO.WithinRole,String> {

        private static final long serialVersionUID = 1L;
        
        @XmlElement(name="roleMember")
        private List<RoleMemberXmlDTO.WithinRole> roleMembers;

        @XmlTransient
        private String roleId;
        
        @XmlTransient
        private Set<String> existingRoleMemberIds;
        
        public WithinRole() {}
        
        public WithinRole(String roleId) {
            this.roleId = roleId;
        }
        
        /**
         * @see org.kuali.rice.kim.impl.jaxb.RoleMembersXmlDTO#getRoleMembers()
         */
        @Override
        public List<org.kuali.rice.kim.impl.jaxb.RoleMemberXmlDTO.WithinRole> getRoleMembers() {
            return this.roleMembers;
        }

        /**
         * @see org.kuali.rice.kim.impl.jaxb.RoleMembersXmlDTO#setRoleMembers(java.util.List)
         */
        @Override
        public void setRoleMembers(List<org.kuali.rice.kim.impl.jaxb.RoleMemberXmlDTO.WithinRole> roleMembers) {
            this.roleMembers = roleMembers;
        }
        
        /**
         * @return the roleId
         */
        public String getRoleId() {
            return this.roleId;
        }

        /**
         * @see org.kuali.rice.kim.impl.jaxb.RoleMembersXmlDTO#beforeUnmarshal(javax.xml.bind.Unmarshaller, java.lang.Object)
         */
        @Override
        void beforeUnmarshal(Unmarshaller unmarshaller, Object parent) throws UnmarshalException {
            if (parent instanceof RoleXmlDTO) {
                // Obtain the role ID from the enclosing role, and persist the role if it has not been persisted yet.
                RoleXmlDTO parentRole = (RoleXmlDTO) parent;
                if (!parentRole.isAlreadyPersisted()) {
                    RoleXmlUtil.validateAndPersistNewRole(parentRole);
                }
                roleId = parentRole.getRoleId();
            }
            existingRoleMemberIds = new HashSet<String>();
            super.beforeUnmarshal(unmarshaller, parent);
        }

        /**
         * This overridden method ...
         * 
         * @see org.kuali.rice.kim.impl.jaxb.RoleMembersXmlDTO#afterUnmarshal(javax.xml.bind.Unmarshaller, java.lang.Object)
         */
        @Override
        void afterUnmarshal(Unmarshaller unmarshaller, Object parent) throws UnmarshalException {
            super.afterUnmarshal(unmarshaller, parent);
            if (parent instanceof RoleXmlDTO) {
                ((RoleXmlDTO)parent).setExistingRoleMemberIds(existingRoleMemberIds);
            }
            existingRoleMemberIds = null;
        }

        /**
         * @see org.kuali.rice.core.util.jaxb.RiceXmlListAdditionListener#newItemAdded(java.lang.Object)
         */
        @Override
        public void newItemAdded(org.kuali.rice.kim.impl.jaxb.RoleMemberXmlDTO.WithinRole item) {
            // Persist the role member and add it to the set of role members that should not be removed from the role.
            try {
                existingRoleMemberIds.add(RoleXmlUtil.validateAndPersistNewRoleMember(item));
            } catch (UnmarshalException e) {
                throw new RuntimeException(e);
            }
        }
        
        void beforeMarshal(Marshaller marshaller) {
            List<RoleMember> tempMembers = KimApiServiceLocator.getRoleService().findRoleMembers(
                    QueryByCriteria.Builder.fromPredicates(equal("roleId", roleId))).getResults();
            if (tempMembers != null && !tempMembers.isEmpty()) {
                List<String> roleMemberIds = new ArrayList<String>();
                
                for (RoleMemberContract tempMember : tempMembers) {
                    if (tempMember.isActive(null)) {
                        roleMemberIds.add(tempMember.getId());
                    }
                }
                
                if (!roleMemberIds.isEmpty()) {
                    setRoleMembers(new RiceXmlExportList<RoleMemberXmlDTO.WithinRole,String>(roleMemberIds, this));
                }
            }
        }
        
        void afterMarshal(Marshaller marshaller) {
            setRoleMembers(null);
        }

        /**
         * @see org.kuali.rice.core.util.jaxb.RiceXmlListGetterListener#gettingNextItem(java.lang.Object, int)
         */
        @Override
        public RoleMemberXmlDTO.WithinRole gettingNextItem(String nextItem, int index) {
            return new RoleMemberXmlDTO.WithinRole(KimApiServiceLocator.getRoleService().findRoleMembers(QueryByCriteria.Builder.fromPredicates(equal("roleMemberId", nextItem))).getResults().get(0), false);
        }
    }
}
