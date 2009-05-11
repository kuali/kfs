/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.KimDelegationTypeService;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

public abstract class OrganizationHierarchyAwareRoleTypeServiceBase extends KimRoleTypeServiceBase implements KimDelegationTypeService {
    private static final Logger LOG = Logger.getLogger(OrganizationHierarchyAwareRoleTypeServiceBase.class);
    private ChartService chartService;
    private OrganizationService organizationService;

    protected boolean isParentOrg(String qualificationChartCode, String qualificationOrgCode, String roleChartCode, String roleOrgCode, boolean descendHierarchy) {
        if (roleChartCode == null && roleOrgCode == null) return false;
        if (roleOrgCode == null) {
            return roleChartCode.equals(qualificationChartCode) || (descendHierarchy && chartService.isParentChart(qualificationChartCode, roleChartCode));
        }
        return (roleChartCode.equals(qualificationChartCode) && roleOrgCode.equals(qualificationOrgCode)) || (descendHierarchy && organizationService.isParentOrganization(qualificationChartCode, qualificationOrgCode, roleChartCode, roleOrgCode));
    }
    
    @Override
    public AttributeSet convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, AttributeSet qualification) {
        AttributeSet newQualification = new AttributeSet(qualification);
        try {
            newQualification.put(KfsKimAttributes.CAMPUS_CODE, organizationService.getByPrimaryId(qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), qualification.get(KfsKimAttributes.ORGANIZATION_CODE)).getOrganizationPhysicalCampusCode());
        }
        catch (Exception e) {
            if (LOG.isDebugEnabled()) LOG.warn("Unable to convert organization qualification to physical campus", e);
        }
        return newQualification;
    }

    public boolean doesDelegationQualifierMatchQualification(AttributeSet qualification, AttributeSet delegationQualifier) {
        return performMatch(translateInputAttributeSet(qualification), delegationQualifier);
    }

    public List<DelegateInfo> doDelegationQualifiersMatchQualification(AttributeSet qualification, List<DelegateInfo> delegationMemberList) {
        AttributeSet translatedQualification = translateInputAttributeSet(qualification);
        List<DelegateInfo> matchingMemberships = new ArrayList<DelegateInfo>();
        for (DelegateInfo dmi : delegationMemberList) {
            if (performMatch(translatedQualification, dmi.getQualifier())) {
                matchingMemberships.add(dmi);
            }
        }
        return matchingMemberships;
    }

    
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }
    
    @Override
    public List<RoleMembershipInfo> sortRoleMembers(List<RoleMembershipInfo> roleMembers) {
        List<SortableRoleMembershipHolder> listToSort = new ArrayList<SortableRoleMembershipHolder>( roleMembers.size() );
        // build the sortable list
        for ( RoleMembershipInfo rmi : roleMembers ) {
            listToSort.add( new SortableRoleMembershipHolder( rmi ) );
        }
        // sort it
        Collections.sort(listToSort);
        // restore them to the list in their sorted order
        roleMembers.clear();
        for ( SortableRoleMembershipHolder srmh : listToSort ) {
            roleMembers.add( srmh.rmi );
        }
        return roleMembers;
    }

    private class SortableRoleMembershipHolder implements Comparable<SortableRoleMembershipHolder> {
        
        public String chart;
        public String org;
        public RoleMembershipInfo rmi;
        
        public SortableRoleMembershipHolder( RoleMembershipInfo rmi ) {
            this.rmi = rmi;
            chart = rmi.getQualifier().get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            org = rmi.getQualifier().get(KfsKimAttributes.ORGANIZATION_CODE);
            rmi.setRoleSortingCode( chart+"-"+org );
        }
        
        public int compareTo(SortableRoleMembershipHolder o) {
            if ( chart == null || org == null || (o.chart == chart && o.org == org) ) {
                return 0;
            }
            if ( organizationService.isParentOrganization(o.chart, o.org, chart, org) ) {
                return 1;
            } else {
                return -1;
            }
        }
    }
    
    
    
}