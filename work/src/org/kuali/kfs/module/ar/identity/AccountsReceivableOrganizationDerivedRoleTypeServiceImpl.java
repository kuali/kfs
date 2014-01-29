/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.ChartOrgHolderImpl;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public class AccountsReceivableOrganizationDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {
    protected static final String PROCESSOR_ROLE_NAME = "Processor";

    protected static final String UNMATCHABLE_QUALIFICATION = "!~!~!~!~!~";

    protected BusinessObjectService businessObjectService;
    protected FinancialSystemUserService financialSystemUserService;
    protected UniversityDateService universityDateService;

    protected ChartOrgHolder getProcessingChartOrg( Map<String,String> qualification ) {
        ChartOrgHolderImpl chartOrg = null;
        if ( qualification != null && !qualification.isEmpty() ) {
            chartOrg = new ChartOrgHolderImpl();
            // if the processing org is specified from the qualifications, use that
            chartOrg.setChartOfAccountsCode( qualification.get(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE) );
            chartOrg.setOrganizationCode( qualification.get(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_ORGANIZATION_CODE) );
            // otherwise default to the normal chart/org values and derive the processing chart/org
            if (StringUtils.isBlank(chartOrg.getChartOfAccountsCode()) || StringUtils.isBlank(chartOrg.getOrganizationCode())) {
                chartOrg.setChartOfAccountsCode( qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
                chartOrg.setOrganizationCode( qualification.get(KfsKimAttributes.ORGANIZATION_CODE) );
                if (StringUtils.isBlank(chartOrg.getChartOfAccountsCode()) || StringUtils.isBlank(chartOrg.getOrganizationCode())) {
                    return null;
                }
                Map<String, Object> arOrgOptPk = new HashMap<String, Object>( 2 );
                arOrgOptPk.put(ArPropertyConstants.OrganizationOptionsFields.CHART_OF_ACCOUNTS_CODE, chartOrg.getChartOfAccountsCode());
                arOrgOptPk.put(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CODE, chartOrg.getOrganizationCode());
                OrganizationOptions oo = getBusinessObjectService().findByPrimaryKey(OrganizationOptions.class, arOrgOptPk);
                if (oo != null) {
                    chartOrg.setChartOfAccountsCode( oo.getProcessingChartOfAccountCode() );
                    chartOrg.setOrganizationCode( oo.getProcessingOrganizationCode() );
                } else {
                    chartOrg.setChartOfAccountsCode( UNMATCHABLE_QUALIFICATION );
                    chartOrg.setOrganizationCode( UNMATCHABLE_QUALIFICATION );
                }
            }
        }
        return chartOrg;
    }

    public boolean hasProcessorRole(ChartOrgHolder userOrg, Map<String,String> qualification) {
        ChartOrgHolder processingOrg = getProcessingChartOrg(qualification);
        // if no org passed, check if their primary org is a processing org
        if ( processingOrg == null ) {
            // check the org options for this org
            Map<String, Object> arProcessOrgCriteria = new HashMap<String, Object>( 2 );
            arProcessOrgCriteria.put(ArPropertyConstants.SystemInformationFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, userOrg.getChartOfAccountsCode());
            arProcessOrgCriteria.put(ArPropertyConstants.SystemInformationFields.PROCESSING_ORGANIZATION_CODE, userOrg.getOrganizationCode());
            arProcessOrgCriteria.put(ArPropertyConstants.SystemInformationFields.ACTIVE, "Y");
            arProcessOrgCriteria.put(ArPropertyConstants.SystemInformationFields.UNIVERSITY_FISCAL_YEAR, universityDateService.getCurrentFiscalYear());
            // return true if any matching org options records
            return getBusinessObjectService().countMatching(SystemInformation.class, arProcessOrgCriteria) > 0;
        } else { // org was passed, user's org must match
            return processingOrg.equals(userOrg);
        }
    }

    public boolean hasBillerRole(ChartOrgHolder userOrg, Map<String,String> qualification) {
        ChartOrgHolderImpl billingOrg = new ChartOrgHolderImpl();
        if ( qualification != null && !qualification.isEmpty()) {
            billingOrg.setChartOfAccountsCode( qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
            billingOrg.setOrganizationCode( qualification.get(KfsKimAttributes.ORGANIZATION_CODE) );
        }
        if (StringUtils.isBlank(billingOrg.getChartOfAccountsCode()) || StringUtils.isBlank(billingOrg.getOrganizationCode())) {
            Map<String, Object> arOrgOptPk = new HashMap<String, Object>( 2 );
            arOrgOptPk.put(ArPropertyConstants.OrganizationOptionsFields.CHART_OF_ACCOUNTS_CODE, userOrg.getChartOfAccountsCode());
            arOrgOptPk.put(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CODE, userOrg.getOrganizationCode());
            return getBusinessObjectService().countMatching(OrganizationOptions.class, arOrgOptPk) > 0;
        } else {
            return billingOrg.equals(userOrg);
        }
    }

    /**
     * @see org.kuali.rice.kns.kim.role.RoleTypeServiceBase#hasDerivedRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public boolean hasDerivedRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String,String> qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        if (getFinancialSystemUserService().isActiveFinancialSystemUser(principalId)) {
            ChartOrgHolder userOrg = getFinancialSystemUserService().getPrimaryOrganization(principalId, ArConstants.AR_NAMESPACE_CODE);
            if (PROCESSOR_ROLE_NAME.equals(roleName)) {
                return hasProcessorRole(userOrg, qualification);
            } else {  // billing role
                return hasBillerRole(userOrg, qualification) || hasProcessorRole(userOrg, qualification);
            }
        }
        return false;
    }

    public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Map<String,String> qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        List<RoleMembership> results = new ArrayList<RoleMembership>();
        Set<String> principalIds = new HashSet<String>();
        if (PROCESSOR_ROLE_NAME.equals(roleName)) {
            ChartOrgHolder processingOrg = getProcessingChartOrg(qualification);
            if ( processingOrg == null ) {
                // get all users for all processing orgs
                // build a set
                List<OrganizationOptions> ooList = (List<OrganizationOptions>)getBusinessObjectService().findAll(OrganizationOptions.class);
                for ( OrganizationOptions oo : ooList ) {
                    principalIds.clear();
                    principalIds.addAll(
                            getFinancialSystemUserService().getPrincipalIdsForFinancialSystemOrganizationUsers(
                                    namespaceCode,
                                    new ChartOrgHolderImpl( oo.getProcessingChartOfAccountCode(), oo.getProcessingOrganizationCode() )));
                    if ( !principalIds.isEmpty() ) {
                        Map<String,String> roleQualifier = new HashMap<String,String>(2);
                        roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, oo.getProcessingChartOfAccountCode());
                        roleQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, oo.getProcessingOrganizationCode());
                        for ( String principalId : principalIds ) {
                            RoleMembership.Builder builder = RoleMembership.Builder.create( null, null, principalId, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE, roleQualifier );
                            results.add(builder.build());
                        }
                    }
                }
            } else {
                // get all users for the given org
                principalIds.addAll( getFinancialSystemUserService().getPrincipalIdsForFinancialSystemOrganizationUsers(ArConstants.AR_NAMESPACE_CODE, processingOrg) );
                if ( !principalIds.isEmpty() ) {
                    Map<String,String> roleQualifier = new HashMap<String,String>(2);
                    roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, processingOrg.getChartOfAccountsCode());
                    roleQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, processingOrg.getOrganizationCode());
                    for ( String principalId : principalIds ) {
                        RoleMembership.Builder builder = RoleMembership.Builder.create( null, null, principalId, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE, roleQualifier );
                        results.add(builder.build());
                    }
                }
            }
        } else { // billing role
            ChartOrgHolderImpl billingOrg = new ChartOrgHolderImpl();
            if ( qualification != null && !qualification.isEmpty() ) {
                billingOrg.setChartOfAccountsCode( qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
                billingOrg.setOrganizationCode( qualification.get(KfsKimAttributes.ORGANIZATION_CODE) );
            }
            if (StringUtils.isBlank(billingOrg.getChartOfAccountsCode()) || StringUtils.isBlank(billingOrg.getOrganizationCode())) {
                // get all users for all billing orgs
                List<OrganizationOptions> ooList = (List<OrganizationOptions>)getBusinessObjectService().findAll(OrganizationOptions.class);
                for ( OrganizationOptions oo : ooList ) {
                    principalIds.clear();
                    principalIds.addAll(
                            getFinancialSystemUserService().getPrincipalIdsForFinancialSystemOrganizationUsers(
                                    namespaceCode,
                                    new ChartOrgHolderImpl( oo.getChartOfAccountsCode(), oo.getOrganizationCode() )));
                    if ( !principalIds.isEmpty() ) {
                        Map<String,String> roleQualifier = new HashMap<String,String>(2);
                        roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, oo.getChartOfAccountsCode());
                        roleQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, oo.getOrganizationCode());
                        for ( String principalId : principalIds ) {
                            RoleMembership.Builder builder = RoleMembership.Builder.create( null, null, principalId, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE, roleQualifier );
                            results.add(builder.build());
                        }
                    }
                }
            } else {
                // get all users for given org
                principalIds.addAll( getFinancialSystemUserService().getPrincipalIdsForFinancialSystemOrganizationUsers(ArConstants.AR_NAMESPACE_CODE, billingOrg) );
                if ( !principalIds.isEmpty() ) {
                    Map<String,String> roleQualifier = new HashMap<String,String>(2);
                    roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, billingOrg.getChartOfAccountsCode());
                    roleQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, billingOrg.getOrganizationCode());
                    for ( String principalId : principalIds ) {
                        RoleMembership.Builder builder = RoleMembership.Builder.create( null, null, principalId, KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE, roleQualifier );
                        results.add(builder.build());
                    }
                }
            }
        }
        return results;
    }


    @Override
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public final FinancialSystemUserService getFinancialSystemUserService() {
        return financialSystemUserService;
    }

    public void setFinancialSystemUserService(FinancialSystemUserService financialSystemUserService) {
        this.financialSystemUserService = financialSystemUserService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }



}
