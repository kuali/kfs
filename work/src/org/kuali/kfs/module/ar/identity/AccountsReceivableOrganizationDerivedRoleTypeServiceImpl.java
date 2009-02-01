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
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.ChartOrgHolderImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.service.support.impl.PassThruRoleTypeServiceBase;
import org.kuali.rice.kns.service.BusinessObjectService;

public class AccountsReceivableOrganizationDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
    private static final String PROCESSOR_ROLE_NAME = "Processor";

    private BusinessObjectService businessObjectService;
    private FinancialSystemUserService financialSystemUserService;

    protected ChartOrgHolder getProcessingChartOrg( AttributeSet qualification ) {
        ChartOrgHolderImpl chartOrg = null;
        if ( qualification != null ) {
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
                OrganizationOptions oo = (OrganizationOptions) getBusinessObjectService().findByPrimaryKey(OrganizationOptions.class, arOrgOptPk);
                if (oo != null) {
                    chartOrg.setChartOfAccountsCode( oo.getProcessingChartOfAccountCode() );
                    chartOrg.setOrganizationCode( oo.getProcessingOrganizationCode() );
                } else {
                    chartOrg.setChartOfAccountsCode( PassThruRoleTypeServiceBase.UNMATCHABLE_QUALIFICATION );
                    chartOrg.setOrganizationCode( PassThruRoleTypeServiceBase.UNMATCHABLE_QUALIFICATION );
                }
            }
        }
        return chartOrg;
    }

    public boolean hasProcessorRole(ChartOrgHolder userOrg, AttributeSet qualification) {
        ChartOrgHolder processingOrg = getProcessingChartOrg(qualification);
        // if no org passed, check if their primary org is a processing org
        if ( processingOrg == null ) {
            // check the org options for this org
            Map<String, Object> arProcessOrgCriteria = new HashMap<String, Object>( 2 );
            arProcessOrgCriteria.put(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_CHART_OF_ACCOUNTS_CODE, userOrg.getChartOfAccountsCode());
            arProcessOrgCriteria.put(ArPropertyConstants.OrganizationOptionsFields.PROCESSING_ORGANIZATION_CODE, userOrg.getOrganizationCode());
            // return true if any matching org options records
            return getBusinessObjectService().countMatching(OrganizationOptions.class, arProcessOrgCriteria) > 0;
        } else { // org was passed, user's org must match
            return processingOrg.equals(userOrg);
        }        
    }

    public boolean hasBillerRole(ChartOrgHolder userOrg, AttributeSet qualification) {
        ChartOrgHolderImpl billingOrg = new ChartOrgHolderImpl();
        if ( qualification != null ) {
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
    
    
    @Override
    public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification) {
        ChartOrgHolder userOrg = getFinancialSystemUserService().getOrganizationByNamespaceCode(principalId, ArConstants.AR_NAMESPACE_CODE);
        if (PROCESSOR_ROLE_NAME.equals(roleName)) {
            return hasProcessorRole(userOrg, qualification);
        } else {  // billing role
            return hasBillerRole(userOrg, qualification) || hasProcessorRole(userOrg, qualification);
        }
    }
    
    @Override
    public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        Set<String> results = new HashSet<String>();
        if (PROCESSOR_ROLE_NAME.equals(roleName)) {
            ChartOrgHolder processingOrg = getProcessingChartOrg(qualification);
            if ( processingOrg == null ) {
                // get all users for all processing orgs
                // build a set
                List<OrganizationOptions> ooList = (List<OrganizationOptions>)getBusinessObjectService().findAll(OrganizationOptions.class);
                Set<ChartOrgHolder> chartOrgList = new HashSet<ChartOrgHolder>( ooList.size() );
                for ( OrganizationOptions oo : ooList ) {
                    chartOrgList.add( new ChartOrgHolderImpl( oo.getProcessingChartOfAccountCode(), oo.getProcessingOrganizationCode() ) );
                }
                results.addAll( getFinancialSystemUserService().getPrincipalIdsForOrganizationUsers(namespaceCode, new ArrayList<ChartOrgHolder>(chartOrgList)));
            } else {
                // get all users for the given org
                results.addAll( getFinancialSystemUserService().getPrincipalIdsForOrganizationUsers(ArConstants.AR_NAMESPACE_CODE, processingOrg) );
            }
        } else { // billing role
            ChartOrgHolderImpl billingOrg = new ChartOrgHolderImpl();
            if ( qualification != null ) {
                billingOrg.setChartOfAccountsCode( qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) );
                billingOrg.setOrganizationCode( qualification.get(KfsKimAttributes.ORGANIZATION_CODE) );
            }
            if (StringUtils.isBlank(billingOrg.getChartOfAccountsCode()) || StringUtils.isBlank(billingOrg.getOrganizationCode())) {
                // get all users for all billing orgs
                List<OrganizationOptions> ooList = (List<OrganizationOptions>)getBusinessObjectService().findAll(OrganizationOptions.class);
                List<ChartOrgHolder> chartOrgList = new ArrayList<ChartOrgHolder>( ooList.size() );
                for ( OrganizationOptions oo : ooList ) {
                    chartOrgList.add( new ChartOrgHolderImpl( oo.getChartOfAccountsCode(), oo.getOrganizationCode() ) );
                }
                results.addAll( getFinancialSystemUserService().getPrincipalIdsForOrganizationUsers(namespaceCode, chartOrgList));
            } else {
                // get all users for given org
                results.addAll( getFinancialSystemUserService().getPrincipalIdsForOrganizationUsers(ArConstants.AR_NAMESPACE_CODE, billingOrg) );
            }
        }
        return new ArrayList<String>( results );
    }
    

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    

    public final FinancialSystemUserService getFinancialSystemUserService() {
        if (financialSystemUserService == null) {
            financialSystemUserService = SpringContext.getBean(FinancialSystemUserService.class);
        }
        return financialSystemUserService;
    }

}
