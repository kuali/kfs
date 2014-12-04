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
package org.kuali.kfs.module.ar.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Returns the active fund managers of a given award using the proposal number. *
 */
public class FundsManagerDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(FundsManagerDerivedRoleTypeServiceImpl.class);
    protected KualiModuleService kualiModuleService;

    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        List<RoleMembership> roleMembers = new ArrayList<RoleMembership>();

        if (ObjectUtils.isNotNull(qualification) && !qualification.isEmpty()) {
            String proposalNumber = qualification.get(KFSPropertyConstants.PROPOSAL_NUMBER);
            if (StringUtils.isNotBlank(proposalNumber)) {
                return getRoleMembersForAward(proposalNumber);
            } else {
                String principalId = qualification.get(KimConstants.AttributeConstants.PRINCIPAL_ID);
                if (StringUtils.isNotBlank(principalId)) {
                    return getRoleMembersForPrincipal(principalId);
                }
            }
        }

        return roleMembers;
    }

    /**
     * Get Role Members (i.e. Fund Managers) for the Award
     *
     * @param roleMembers
     * @param award
     */
    protected List<RoleMembership> getRoleMembersForAward(String proposalNumber) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        return getRoleMembers(map);
    }

    /**
     * If the User principalId passed in is a fund manager, return it as a role member, otherwise return an empty role members list.
     *
     * @param roleMembers
     * @param award
     */
    protected List<RoleMembership> getRoleMembersForPrincipal(String principalId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KimConstants.AttributeConstants.PRINCIPAL_ID, principalId);
        return getRoleMembers(map);
    }

    /**
     * Find the Fund Managers that match the search criteria and return them as role members.
     *
     * @param criteria
     * @return
     */
    protected List<RoleMembership> getRoleMembers(Map<String, Object> criteria) {
        List<RoleMembership> roleMembers = new ArrayList<RoleMembership>();

        criteria.put(KFSPropertyConstants.ACTIVE, true);

        List<ContractsAndGrantsFundManager> awardFundManagers = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsFundManager.class).getExternalizableBusinessObjectsList(ContractsAndGrantsFundManager.class, criteria);
        for (ContractsAndGrantsFundManager awardFundManager : awardFundManagers) {
            roleMembers.add(RoleMembership.Builder.create(null, null, awardFundManager.getPrincipalId(), MemberType.PRINCIPAL, null).build());
        }

        return roleMembers;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
