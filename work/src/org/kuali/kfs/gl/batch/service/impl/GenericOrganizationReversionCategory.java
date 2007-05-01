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

import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;

public class GenericOrganizationReversionCategory implements OrganizationReversionCategoryLogic {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericOrganizationReversionCategory.class);

    private KualiConfigurationService kualiConfigurationService;

    private String categoryCode;
    private String categoryName;
    private boolean isExpense;

    public GenericOrganizationReversionCategory() {
    }

    public void setCategoryCode(String code) {
        categoryCode = code;
        isExpense = kualiConfigurationService.getApplicationParameterIndicator(KFSConstants.ORG_REVERSION, categoryCode + KFSConstants.EXPENSE_FLAG);
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

        KualiParameterRule consolidationRules = kualiConfigurationService.getApplicationParameterRule(KFSConstants.ORG_REVERSION, categoryCode + KFSConstants.CONSOLIDATION);
        KualiParameterRule levelRules = kualiConfigurationService.getApplicationParameterRule(KFSConstants.ORG_REVERSION, categoryCode + KFSConstants.LEVEL);
        KualiParameterRule objectTypeRules = kualiConfigurationService.getApplicationParameterRule(KFSConstants.ORG_REVERSION, categoryCode + KFSConstants.OBJECT_TYPE);
        KualiParameterRule objectSubTypeRules = kualiConfigurationService.getApplicationParameterRule(KFSConstants.ORG_REVERSION, categoryCode + KFSConstants.OBJECT_SUB_TYPE);

        return consolidationRules.succeedsRule(cons) && levelRules.succeedsRule(level) && objectTypeRules.succeedsRule(objTyp) && objectSubTypeRules.succeedsRule(objSubTyp);
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

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
