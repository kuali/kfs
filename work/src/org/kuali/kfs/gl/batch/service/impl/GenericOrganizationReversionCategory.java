/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.batch.service.impl;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OrganizationReversionCategory;
import org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;

/**
 * A generic implementation of OrganizationReversionCategoryLogic; it is completely based off of parameters
 * @see org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic
 */
public class GenericOrganizationReversionCategory implements OrganizationReversionCategoryLogic {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericOrganizationReversionCategory.class);

    protected String categoryCode;
    protected String categoryName;
    protected boolean isExpense;

    /**
     * Sets the category code for this logic, so that the parameters for this category can be looked
     * up in the database
     *
     * @param code the code for this logic
     */
    public void setCategoryCode(String code) {
        categoryCode = code;
        isExpense = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(OrganizationReversionCategory.class, KFSConstants.OrgReversion.IS_EXPENSE_PARAM, categoryCode).evaluationSucceeds();
    }

    /**
     * Sets the name of this category
     *
     * @param name the name to set
     */
    public void setCategoryName(String name) {
        categoryName = name;
    }

    /**
     * Determines if balances with a given object code should be processed by this logic or not
     *
     * @param oc the object code to qualify
     * @return true if balances with the given object code should be processed by this logic, false if otherwise
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic#containsObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode)
     */
    @Override
    public boolean containsObjectCode(ObjectCode oc) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("containsObjectCode() started");
        }

        String cons = oc.getFinancialObjectLevel().getFinancialConsolidationObjectCode();
        String level = oc.getFinancialObjectLevelCode();
        String objType = oc.getFinancialObjectTypeCode();
        String objSubType = oc.getFinancialObjectSubTypeCode();

        boolean consolidationRulesPassed = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(OrganizationReversionCategory.class, KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_CONSOL_PARAM_SUFFIX, KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_CONSOL_PARAM_SUFFIX, categoryCode, cons).evaluationSucceeds();
        boolean levelRulesPassed = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(OrganizationReversionCategory.class, KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_LEVEL_PARAM_SUFFIX, KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_LEVEL_PARAM_SUFFIX, categoryCode, level).evaluationSucceeds();
        boolean objectTypeRulesPassed = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(OrganizationReversionCategory.class, KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_TYPE_PARAM_SUFFIX, KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_TYPE_PARAM_SUFFIX, categoryCode, objType).evaluationSucceeds();
        boolean objectSubTypeRulesPassed = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(OrganizationReversionCategory.class, KFSConstants.OrgReversion.VALID_PREFIX + KFSConstants.OrgReversion.OBJECT_SUB_TYPE_PARAM_SUFFIX, KFSConstants.OrgReversion.INVALID_PREFIX + KFSConstants.OrgReversion.OBJECT_SUB_TYPE_PARAM_SUFFIX, categoryCode, objSubType).evaluationSucceeds();

        return consolidationRulesPassed && levelRulesPassed && objectTypeRulesPassed && objectSubTypeRulesPassed;
    }

    /**
     * Returns the name of the category
     *
     * @return the name of the category
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic#getName()
     */
    @Override
    public String getName() {
        return categoryName;
    }

    /**
     * Returns the code of this category
     *
     * @return the code of this category
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic#getCode()
     */
    @Override
    public String getCode() {
        return categoryCode;
    }

    /**
     * Returns whether this category represents an expense or not
     *
     * @return true if this category represents expenses, false if otherwise
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic#isExpense()
     */
    @Override
    public boolean isExpense() {
        return isExpense;
    }
}
