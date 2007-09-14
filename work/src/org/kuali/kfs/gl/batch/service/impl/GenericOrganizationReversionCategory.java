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
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;

public class GenericOrganizationReversionCategory implements OrganizationReversionCategoryLogic {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericOrganizationReversionCategory.class);

    private String categoryCode;
    private String categoryName;
    private boolean isExpense;
    
    KualiParameterRule consolidationRule;
    KualiParameterRule levelRule;
    KualiParameterRule objectTypeRule;
    KualiParameterRule objectSubTypeRule;

    public GenericOrganizationReversionCategory() {
    }

    public void setCategoryCode(String code) {
        categoryCode = code;
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        isExpense = kualiConfigurationService.getApplicationParameterIndicator(KFSConstants.ORG_REVERSION, categoryCode + KFSConstants.EXPENSE_FLAG);
        consolidationRule = kualiConfigurationService.getApplicationParameterRule(KFSConstants.ORG_REVERSION, categoryCode + KFSConstants.CONSOLIDATION);
        levelRule = kualiConfigurationService.getApplicationParameterRule(KFSConstants.ORG_REVERSION, categoryCode + KFSConstants.LEVEL);
        objectTypeRule = kualiConfigurationService.getApplicationParameterRule(KFSConstants.ORG_REVERSION, categoryCode + KFSConstants.OBJECT_TYPE);
        objectSubTypeRule = kualiConfigurationService.getApplicationParameterRule(KFSConstants.ORG_REVERSION, categoryCode + KFSConstants.OBJECT_SUB_TYPE);
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

        return consolidationRule.succeedsRule(cons) && levelRule.succeedsRule(level) && objectTypeRule.succeedsRule(objTyp) && objectSubTypeRule.succeedsRule(objSubTyp);
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
}
