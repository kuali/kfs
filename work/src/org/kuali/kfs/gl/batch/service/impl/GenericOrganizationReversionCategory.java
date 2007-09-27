/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl.orgreversion;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;

public class GenericOrganizationReversionCategory implements OrganizationReversionCategoryLogic {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericOrganizationReversionCategory.class);

    private String categoryCode;
    private String categoryName;
    private boolean isExpense;
    
    private KualiConfigurationService kualiConfigurationService;
    
    public GenericOrganizationReversionCategory() {
    }

    public void setCategoryCode(String code) {
        categoryCode = code;
        isExpense = kualiConfigurationService.evaluateConstrainedParameter(
                KFSConstants.CHART_NAMESPACE, 
                KFSConstants.Components.ORGANIZATION_REVERSION_CATEGORY, 
                KFSConstants.OrgReversion.IS_EXPENSE_PARAM, 
                categoryCode,
                "Y" );
    }

    public void setCategoryName(String name) {
        categoryName = name;
    }

    public boolean containsObjectCode(ObjectCode oc) {
        LOG.debug("containsObjectCode() started");

        String cons = oc.getFinancialObjectLevel().getFinancialConsolidationObjectCode();
        String level = oc.getFinancialObjectLevelCode();
        String objTyp = oc.getFinancialObjectTypeCode();
        String objSubTyp = oc.getFinancialObjectSubType().getCode();

        boolean consolidationRulesPassed = kualiConfigurationService.evaluateConstrainedParameter(
                KFSConstants.CHART_NAMESPACE, 
                KFSConstants.Components.ORGANIZATION_REVERSION_CATEGORY,
                KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_CONSOL_PARAM_SUFFIX,
                KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_CONSOL_PARAM_SUFFIX,
                categoryCode,
                cons );
        boolean levelRulesPassed = kualiConfigurationService.evaluateConstrainedParameter(
                KFSConstants.CHART_NAMESPACE, 
                KFSConstants.Components.ORGANIZATION_REVERSION_CATEGORY,
                KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_LEVEL_PARAM_SUFFIX,
                KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_LEVEL_PARAM_SUFFIX,
                categoryCode,
                level );
        boolean objectTypeRulesPassed = kualiConfigurationService.evaluateConstrainedParameter(
                KFSConstants.CHART_NAMESPACE, 
                KFSConstants.Components.ORGANIZATION_REVERSION_CATEGORY,
                KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_TYPE_PARAM_SUFFIX,
                KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_TYPE_PARAM_SUFFIX,
                categoryCode,
                objTyp );
        boolean objectSubTypeRulesPassed = kualiConfigurationService.evaluateConstrainedParameter(
                KFSConstants.CHART_NAMESPACE, 
                KFSConstants.Components.ORGANIZATION_REVERSION_CATEGORY,
                KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_SUB_TYPE_PARAM_SUFFIX,
                KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_SUB_TYPE_PARAM_SUFFIX,
                categoryCode,
                objSubTyp );

        return consolidationRulesPassed && levelRulesPassed && objectTypeRulesPassed && objectSubTypeRulesPassed;
    }

    public String getName() {
        return categoryName;
    }

    public String getCode() {
        return categoryCode;
    }

    public boolean isExpense() {
        return isExpense;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
