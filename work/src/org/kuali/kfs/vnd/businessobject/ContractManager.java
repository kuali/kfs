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

package org.kuali.kfs.vnd.businessobject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.identity.ContractManagerRoleTypeServiceImpl;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Individuals who are assigned to manage a particular set of Contracts with Vendors, who must therefore look at associated Purchase
 * Orders.
 *
 * @see org.kuali.kfs.vnd.businessobject.VendorContract
 */
public class ContractManager extends PersistableBusinessObjectBase implements MutableInactivatable {

    private Integer contractManagerCode;
    private String contractManagerName;
    private String contractManagerPhoneNumber;
    private String contractManagerFaxNumber;
    private KualiDecimal contractManagerDelegationDollarLimit;
    private boolean active;


    /**
     * Default constructor.
     */
    public ContractManager() {
    }

    public Integer getContractManagerCode() {
        return contractManagerCode;
    }

    public void setContractManagerCode(Integer contractManagerCode) {
        this.contractManagerCode = contractManagerCode;
    }

    public String getContractManagerName() {
        return contractManagerName;
    }

    public void setContractManagerName(String contractManagerName) {
        this.contractManagerName = contractManagerName;
    }

    public String getContractManagerPhoneNumber() {
        return contractManagerPhoneNumber;
    }

    public void setContractManagerPhoneNumber(String contractManagerPhoneNumber) {
        this.contractManagerPhoneNumber = contractManagerPhoneNumber;
    }

    public String getContractManagerFaxNumber() {
        return contractManagerFaxNumber;
    }

    public void setContractManagerFaxNumber(String contractManagerFaxNumber) {
        this.contractManagerFaxNumber = contractManagerFaxNumber;
    }

    public KualiDecimal getContractManagerDelegationDollarLimit() {
        return contractManagerDelegationDollarLimit;
    }

    public void setContractManagerDelegationDollarLimit(KualiDecimal contractManagerDelegationDollarLimit) {
        this.contractManagerDelegationDollarLimit = contractManagerDelegationDollarLimit;
    }

    /**
     * This method gets the contract manager user identifier.
     * @return contractManagerId
     */
    public String getContractManagerUserIdentifier() {
        String contractManagerId = null;
        Map<String,String> qualification = new HashMap<String,String>();

        RoleService roleService = KimApiServiceLocator.getRoleService();
        String roleId = roleService.getRoleIdByNamespaceCodeAndName(KfsParameterConstants.PURCHASING_NAMESPACE, ContractManagerRoleTypeServiceImpl.CONTRACT_MANAGER_ROLE_NAME);
//        String roleId = roleService.getRoleIdByName(KFSConstants.ParameterNamespaces.PURCHASING, ContractManagerRoleTypeServiceImpl.CONTRACT_MANAGER_ROLE_NAME);

        qualification.put(KfsKimAttributes.CONTRACT_MANAGER_CODE, String.valueOf(contractManagerCode));
        Collection<RoleMembership> roleMemberships = roleService.getRoleMembers(Collections.singletonList(roleId), qualification);

        for (RoleMembership membership : roleMemberships) {
            contractManagerId = membership.getMemberId();
            break;
        }

        return contractManagerId;
    }

    public Person getContractManagerPerson() {
        Person contractManager = SpringContext.getBean(PersonService.class).getPerson(getContractManagerUserIdentifier());
        if (ObjectUtils.isNotNull(contractManager)) {
            return contractManager;
        }
        return null;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.contractManagerCode != null) {
            m.put("contractManagerCode", this.contractManagerCode.toString());
        }

        return m;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
