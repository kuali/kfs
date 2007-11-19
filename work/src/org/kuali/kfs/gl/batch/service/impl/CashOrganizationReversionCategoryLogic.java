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

import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;

/**
 * The implementation of OrganizationReversionCategoryLogic for cash balances.
 * @see org.kuali.module.gl.service.OrganizationReversionCategoryLogic
 */
public class CashOrganizationReversionCategoryLogic implements OrganizationReversionCategoryLogic {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashOrganizationReversionCategoryLogic.class);

    static final public String NAME = "Cash";
    static final public String CODE = "CASH";

    /**
     * Determines if the given object code is an object code for cash balances
     * 
     * @param oc the object code to qualify
     * @return true if it is a cash object code, false if otherwise
     * @see org.kuali.module.gl.service.OrganizationReversionCategoryLogic#containsObjectCode(org.kuali.module.chart.bo.ObjectCode)
     */
    public boolean containsObjectCode(ObjectCode oc) {
        LOG.debug("containsObjectCode() started");

        ObjectCode chartCashObject = oc.getChartOfAccounts().getFinancialCashObject();
        return (chartCashObject.getChartOfAccountsCode().equals(oc.getChartOfAccountsCode()) && chartCashObject.getFinancialObjectCode().equals(oc.getFinancialObjectCode()));
    }

    /**
     * Returns the code for this category, always "CASH"
     * 
     * @return the code for this category
     * @see org.kuali.module.gl.service.OrganizationReversionCategoryLogic#getCode()
     */
    public String getCode() {
        return CODE;
    }

    /**
     * Returns the name of this category, always "Cash"
     * 
     * @return the name of this category
     * @see org.kuali.module.gl.service.OrganizationReversionCategoryLogic#getName()
     */
    public String getName() {
        return NAME;
    }

    /**
     * Returns if this category represents an expense or not; it never does
     * 
     * @return false, as the cash category always represents non-expense
     * @see org.kuali.module.gl.service.OrganizationReversionCategoryLogic#isExpense()
     */
    public boolean isExpense() {
        return false;
    }
}
