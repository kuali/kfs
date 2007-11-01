/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OrganizationReversionCategory;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;

public class GenericOrganizationReversionCategory implements OrganizationReversionCategoryLogic {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericOrganizationReversionCategory.class);

    private String categoryCode;
    private String categoryName;
    private boolean isExpense;

    private ParameterService parameterService;
    private ParameterEvaluator consolidationRules;
    private ParameterEvaluator levelRules;
    private ParameterEvaluator objectTypeRules;
    private ParameterEvaluator objectSubTypeRules;

    public GenericOrganizationReversionCategory() {
    }

    public void setCategoryCode(String code) {
        categoryCode = code;
        isExpense = parameterService.getParameterEvaluator(OrganizationReversionCategory.class, KFSConstants.OrgReversion.IS_EXPENSE_PARAM, categoryCode).evaluationSucceeds();
    }

    public void setCategoryName(String name) {
        categoryName = name;
    }

    public boolean containsObjectCode(ObjectCode oc) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("containsObjectCode() started");
        }

        String cons = oc.getFinancialObjectLevel().getFinancialConsolidationObjectCode();
        String level = oc.getFinancialObjectLevelCode();
        String objType = oc.getFinancialObjectTypeCode();
        String objSubType = oc.getFinancialObjectSubType().getCode();

        if (consolidationRules == null) {
            consolidationRules = parameterService.getParameterEvaluator(OrganizationReversionCategory.class, KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_CONSOL_PARAM_SUFFIX, KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_CONSOL_PARAM_SUFFIX, categoryCode, cons);
        }
        else {
            consolidationRules.setConstrainedValue(cons);
        }

        if (levelRules == null) {
            levelRules = parameterService.getParameterEvaluator(OrganizationReversionCategory.class, KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_LEVEL_PARAM_SUFFIX, KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_LEVEL_PARAM_SUFFIX, categoryCode, level);
        }
        else {
            levelRules.setConstrainedValue(level);
        }

        if (objectTypeRules == null) {
            objectTypeRules = parameterService.getParameterEvaluator(OrganizationReversionCategory.class, KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_TYPE_PARAM_SUFFIX, KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_TYPE_PARAM_SUFFIX, categoryCode, objType);
        }
        else {
            objectTypeRules.setConstrainedValue(objType);
        }

        if (objectSubTypeRules == null) {
            objectSubTypeRules = parameterService.getParameterEvaluator(OrganizationReversionCategory.class, KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_SUB_TYPE_PARAM_SUFFIX, KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_SUB_TYPE_PARAM_SUFFIX, categoryCode, objSubType);
        }
        else {
            objectSubTypeRules.setConstrainedValue(objSubType);
        }

        boolean consolidationRulesPassed = consolidationRules.evaluationSucceeds();
        boolean levelRulesPassed = levelRules.evaluationSucceeds();
        boolean objectTypeRulesPassed = objectTypeRules.evaluationSucceeds();
        boolean objectSubTypeRulesPassed = objectSubTypeRules.evaluationSucceeds();

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

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
