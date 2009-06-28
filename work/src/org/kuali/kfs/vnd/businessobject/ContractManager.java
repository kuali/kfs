/*
 * Copyright 2007 The Kuali Foundation.
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

package org.kuali.kfs.vnd.businessobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.vnd.identity.ContractManagerRoleTypeServiceImpl;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Individuals who are assigned to manage a particular set of Contracts with Vendors, who must therefore look at associated Purchase
 * Orders.
 * 
 * @see org.kuali.kfs.vnd.businessobject.VendorContract
 */
public class ContractManager extends PersistableBusinessObjectBase implements Inactivateable{

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
        AttributeSet qualification = new AttributeSet();
        
        RoleManagementService roleService = SpringContext.getBean(RoleManagementService.class);
        String roleId = roleService.getRoleIdByName(PurapConstants.PURAP_NAMESPACE, ContractManagerRoleTypeServiceImpl.CONTRACT_MANAGER_ROLE_NAME);
        
        qualification.put(KfsKimAttributes.CONTRACT_MANAGER_CODE, String.valueOf(contractManagerCode));
        Collection<RoleMembershipInfo> roleMemberships = roleService.getRoleMembers(Collections.singletonList(roleId), qualification);

        for (RoleMembershipInfo membership : roleMemberships) {
            contractManagerId = membership.getMemberId();
            break;
        }

        return contractManagerId;
    }
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.contractManagerCode != null) {
            m.put("contractManagerCode", this.contractManagerCode.toString());
        }

        return m;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
