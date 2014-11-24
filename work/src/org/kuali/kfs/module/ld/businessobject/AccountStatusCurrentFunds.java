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

package org.kuali.kfs.module.ld.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Labor business object for Account Status (Current Funds).
 */
public class AccountStatusCurrentFunds extends LedgerBalance {
    private String personName;
    private KualiDecimal outstandingEncum;
    private KualiDecimal july1BudgetAmount;
    private KualiDecimal annualActualAmount;
    private KualiDecimal variance;

    /**
     * Constructs an AccountStatusCurrentFunds.java.
     */
    public AccountStatusCurrentFunds() {
        super();
        setMonth1Amount(KualiDecimal.ZERO);
        this.setOutstandingEncum(KualiDecimal.ZERO);
        this.setVariance(KualiDecimal.ZERO);
        this.setJuly1BudgetAmount(KualiDecimal.ZERO);
        this.setAnnualActualAmount(KualiDecimal.ZERO);
    }

    /**
     * Gets the person name.
     * @return the person name
     */
    public String getName() {
        /*
        Person person = SpringContext.getBean(PersonService.class).getPersonByEmployeeId(getEmplid());
        if (person == null) {
            return LaborConstants.BalanceInquiries.UnknownPersonName;
        }

        return person.getName();
        */

        /*
         * KFSCNTRB-1344
         * Replace the above logic that uses PersonService with the following one that uses IdentityService, since the former is a lot slower.
         */
        String name = SpringContext.getBean(FinancialSystemUserService.class).getPersonNameByEmployeeId(getEmplid());
        if (!StringUtils.isEmpty(name)) {
            return name;
        }
        return LaborConstants.BalanceInquiries.UnknownPersonName;
    }

    /**
     * Sets the persons name
     *
     * @param personName
     */
    public void setName(String personName) {
        this.personName = personName;
    }

    /**
     * Gets an outstanding encumberance value
     *
     * @return outstanding encumberance value
     */
    public KualiDecimal getOutstandingEncum() {
        return outstandingEncum;
    }

    /**
     * Sets an outstanding encumberance value
     *
     * @param outstandingEncum
     */
    public void setOutstandingEncum(KualiDecimal outstandingEncum) {
        this.outstandingEncum = outstandingEncum;
    }

    /**
     * Gets the Jul1BudgerAmount
     *
     * @return July1st amount
     */
    public KualiDecimal getJuly1BudgetAmount() {
        return july1BudgetAmount;
    }

    /**
     * Sets the july1BudgetAmount
     *
     * @param july1BudgetAmount
     */
    public void setJuly1BudgetAmount(KualiDecimal july1BudgetAmount) {
        this.july1BudgetAmount = july1BudgetAmount;
    }

    /**
     * Gets the variance which is calculated by substracting from July1BudgetAmount the YTD Actual, and outstanding encumbrance.
     *
     * @return
     */
    public KualiDecimal getVariance() {
        return this.getJuly1BudgetAmount().subtract(getAnnualActualAmount()).subtract(getOutstandingEncum());
    }

    /**
     * Sets the variance which is calculated by substracting from July1BudgetAmount the YTD Actual, and outstanding encumbrance.
     *
     * @param variance
     */
    public void setVariance(KualiDecimal variance) {
        this.variance = variance;
    }

    /**
     * Returns a list of keys used to generate a query.
     *
     * @param consolidated
     * @return a list with the keys needed to generate a query
     */
    public List<String> getKeyFieldList(boolean consolidated) {
        List<String> primaryKeyList = new ArrayList<String>();
        primaryKeyList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        primaryKeyList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        primaryKeyList.add(KFSPropertyConstants.ACCOUNT_NUMBER);

        if (!consolidated) {
            primaryKeyList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        }

        primaryKeyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        if (!consolidated) {
            primaryKeyList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        }
        primaryKeyList.add(KFSPropertyConstants.POSITION_NUMBER);
        primaryKeyList.add(KFSPropertyConstants.EMPLID);

        return primaryKeyList;
    }

    /**
     * Gets the annualActualAmount attribute.
     *
     * @return Returns the annualActualAmount.
     */
    public KualiDecimal getAnnualActualAmount() {
        return this.getAccountLineAnnualBalanceAmount().add(this.getContractsGrantsBeginningBalanceAmount());
    }

    /**
     * Sets the annualActualAmount attribute value.
     *
     * @param annualActualAmount The annualActualAmount to set.
     */
    public void setAnnualActualAmount(KualiDecimal annualActualAmount) {
        this.annualActualAmount = annualActualAmount;
    }
}

