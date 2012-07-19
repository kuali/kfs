/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleMembership.Builder;
import org.kuali.rice.kim.framework.common.delegate.DelegationTypeService;
import org.kuali.rice.kim.impl.common.delegate.DelegateMemberBo;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

public abstract class OrganizationHierarchyAwareRoleTypeServiceBase extends RoleTypeServiceBase implements DelegationTypeService {
    private static final Logger LOG = Logger.getLogger(OrganizationHierarchyAwareRoleTypeServiceBase.class);
    protected ChartService chartService;
    protected OrganizationService organizationService;
    protected static final String DOCUMENT_TYPE_NAME = "ORR";

    private List<String> workflowRoutingAttributes = new ArrayList<String>(2);
    {
        workflowRoutingAttributes.add( KfsKimAttributes.CHART_OF_ACCOUNTS_CODE );
        workflowRoutingAttributes.add( KfsKimAttributes.ORGANIZATION_CODE );
        workflowRoutingAttributes = Collections.unmodifiableList(workflowRoutingAttributes);
    }

    private List<String> uniqueAttributes = new ArrayList<String>(3);
    {
        uniqueAttributes.add(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
        uniqueAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        uniqueAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);
        uniqueAttributes = Collections.unmodifiableList(uniqueAttributes);
    }

    @Override
    public List<String> getUniqueAttributes(String kimTypeId){
        return uniqueAttributes;
    }

   @Override
   public List<String> getWorkflowRoutingAttributes(String routeLevel) {
       return workflowRoutingAttributes;
   }

    @Override
    public String getWorkflowDocumentTypeName() {
        return DOCUMENT_TYPE_NAME;
    }

    protected boolean isParentOrg(String qualificationChartCode, String qualificationOrgCode, String roleChartCode, String roleOrgCode, boolean descendHierarchy) {
        if ( StringUtils.isBlank(qualificationChartCode) || StringUtils.isBlank(qualificationOrgCode) ) {
            if ( LOG.isInfoEnabled() ) {
                LOG.info("No chart/org qualifications passed into isParentOrg()");
            }
            return false;
        }
        if (roleChartCode == null && roleOrgCode == null) {
            LOG.warn("Call to "+this.getClass().getName()+" with no organization role qualifiers; both chart and organization code are null.  Please ensure that qualification data has organization information for this role.");
            return false;
        }
        if (roleOrgCode == null) {
            return roleChartCode.equals(qualificationChartCode)
                    || (descendHierarchy && chartService.isParentChart(qualificationChartCode, roleChartCode));
        }
        return (roleChartCode.equals(qualificationChartCode) && roleOrgCode.equals(qualificationOrgCode))
                || (descendHierarchy && organizationService.isParentOrganization(qualificationChartCode, qualificationOrgCode, roleChartCode, roleOrgCode));
    }

    @Override
    public Map<String,String> convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, Map<String,String> qualification) {
        if ( qualification == null ) {
            return null;
        }
        // only attempt the conversion if :
        // (a) there is not already a campus code provided by the document
        // and (b) we have a chart and organization to resolve
        if ( StringUtils.isBlank( qualification.get(KimConstants.AttributeConstants.CAMPUS_CODE ) )
                && StringUtils.isNotBlank( KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)
                && StringUtils.isNotBlank( KfsKimAttributes.ORGANIZATION_CODE ) ) {
            Map<String,String> newQualification = new HashMap<String,String>(qualification);
            try {
                Organization org = organizationService.getByPrimaryId(qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), qualification.get(KfsKimAttributes.ORGANIZATION_CODE));
                if ( org != null ) {
                    newQualification.put(KimConstants.AttributeConstants.CAMPUS_CODE, org.getOrganizationPhysicalCampusCode());
                } else {
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug( "Invalid Chart/Org passed to convertQualificationForMemberRoles: " + namespaceCode + "/" + roleName + "/" + memberRoleNamespaceCode + "/" + memberRoleName + "/" + qualification );
                    }
                }
            } catch (Exception ex) {
                LOG.warn("Unable to convert organization qualification to physical campus", ex);
            }
            return newQualification;
        } else {
            return qualification;
        }
    }

    @Override
    public boolean doesDelegationQualifierMatchQualification(Map<String,String> qualification, Map<String,String> delegationQualifier) {
        return performMatch(translateInputAttributes(qualification), delegationQualifier);
    }

    public List<DelegateMember> doDelegationQualifiersMatchQualification(Map<String,String> qualification, List<DelegateMemberBo> delegationMemberList) {
        Map<String,String> translatedQualification = translateInputAttributes(qualification);
        List<DelegateMember> matchingMemberships = new ArrayList<DelegateMember>();
        for (DelegateMemberBo dmi : delegationMemberList) {
             if (performMatch(translatedQualification, dmi.getQualifier())) {
                matchingMemberships.add(DelegateMemberBo.to(dmi));
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
    public List<RoleMembership> sortRoleMembers(List<RoleMembership> roleMembers) {
        List<SortableRoleMembershipHolder> listToSort = new ArrayList<SortableRoleMembershipHolder>( roleMembers.size() );
        // build the sortable list
        for ( RoleMembership rmi : roleMembers ) {
            listToSort.add( new SortableRoleMembershipHolder( rmi ) );
        }
        // sort it
        Collections.sort(listToSort);
        // restore them to the list in their sorted order
        List<RoleMembership> newRoleMembers = new ArrayList<RoleMembership>(); // need a new list, because roleMembers is not mutable
        int group = 0; // counter for the group number to add to the roleSortingCode
        String lastRoleSortingCode = "";
        for ( SortableRoleMembershipHolder srmh : listToSort ) {
            if ( !srmh.rmi.getRoleSortingCode().equals( lastRoleSortingCode ) ) {
                group++;
                lastRoleSortingCode = srmh.rmi.getRoleSortingCode();
            }
            Builder builder = RoleMembership.Builder.create( srmh.rmi);
            builder.setRoleSortingCode(StringUtils.leftPad(Integer.toString(group), 3, '0') + "/" + srmh.rmi.getRoleSortingCode() );
            newRoleMembers.add(builder.build());
        }
        return newRoleMembers;
    }

    protected class SortableRoleMembershipHolder implements Comparable<SortableRoleMembershipHolder> {

        public String chart;
        public String org;
        public RoleMembership rmi;

        public SortableRoleMembershipHolder( RoleMembership rmi ) {
            chart = rmi.getQualifier().get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            org = rmi.getQualifier().get(KfsKimAttributes.ORGANIZATION_CODE);
            Builder builder = RoleMembership.Builder.create(rmi);
            builder.setRoleSortingCode(chart+"-"+org);
            this.rmi = builder.build();
        }

        @Override
        public int compareTo(SortableRoleMembershipHolder o) {
            if ( chart == null || org == null || o.chart == null || o.org == null || (o.chart == chart && o.org == org) ) {
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
