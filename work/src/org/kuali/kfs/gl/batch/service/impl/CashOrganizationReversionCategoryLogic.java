/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.gl.batch.service.impl;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic;

/**
 * The implementation of OrganizationReversionCategoryLogic for cash balances.
 * @see org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic
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
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic#containsObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode)
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
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic#getCode()
     */
    public String getCode() {
        return CODE;
    }

    /**
     * Returns the name of this category, always "Cash"
     * 
     * @return the name of this category
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic#getName()
     */
    public String getName() {
        return NAME;
    }

    /**
     * Returns if this category represents an expense or not; it never does
     * 
     * @return false, as the cash category always represents non-expense
     * @see org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic#isExpense()
     */
    public boolean isExpense() {
        return false;
    }
}
