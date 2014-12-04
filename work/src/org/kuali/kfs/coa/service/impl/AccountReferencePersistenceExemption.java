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
package org.kuali.kfs.coa.service.impl;

import org.apache.commons.lang.StringUtils;

/**
 * Represents an exemption in AccountReferencePersistencestructureServiceImpl - a business object and related-to-object
 * keys which will lawlessly not abide by the rules
 */
public class AccountReferencePersistenceExemption {
    protected Class<?> parentBusinessObjectClass;
    protected String chartOfAccountsCodePropertyName;
    protected String accountNumberPropertyName;
    
    /**
     * @return the business object class which owns a relationship that will be exempted
     */
    public Class<?> getParentBusinessObjectClass() {
        return parentBusinessObjectClass;
    }
    /**
     * Sets the business object class which owns a relationship that will be exempted
     * @param parentBusinessObjectClass the business object class which owns a relationship that will be exempted
     */
    public void setParentBusinessObjectClass(Class<?> parentBusinessObjectClass) {
        this.parentBusinessObjectClass = parentBusinessObjectClass;
    }
    /**
     * @return the name of the property on the parentBusinessObjectClass which represents the chart of accounts code portion of the Account relationship 
     */
    public String getChartOfAccountsCodePropertyName() {
        return chartOfAccountsCodePropertyName;
    }
    /**
     * Sets the name of the property on the parentBusinessObjectClass which represents the chart of accounts code portion of the Account relationship
     * @param chartOfAccountsCodePropertyName the name of the property on the parentBusinessObjectClass which represents the chart of accounts code portion of the Account relationship
     */
    public void setChartOfAccountsCodePropertyName(String chartOfAccountsCodePropertyName) {
        this.chartOfAccountsCodePropertyName = chartOfAccountsCodePropertyName;
    }
    /**
     * @return the name of the property on the parentBusinessObjectClass which represents the account number portion of the Account relationship
     */
    public String getAccountNumberPropertyName() {
        return accountNumberPropertyName;
    }
    /**
     * Sets the name of the property on the parentBusinessObjectClass which represents the account number portion of the Account relationship
     * @param accountNumberPropertyName the name of the property on the parentBusinessObjectClass which represents the account number portion of the Account relationship
     */
    public void setAccountNumberPropertyName(String accountNumberPropertyName) {
        this.accountNumberPropertyName = accountNumberPropertyName;
    }

    /**
     * Determines if the given chart of accounts code property name and account number property name would match the relationship-to-account chart of accounts code property name and account number property name encapsulated herein
     * @param chartOfAccountsCodePropertyName the name of the chart of accounts code property name in the relationship
     * @param accountNumberPropertyName the name of the account number property name in the relationship
     * @return true if the property names all match
     */
    public boolean matches(String chartOfAccountsCodePropertyName, String accountNumberPropertyName) {
        return ( ( !StringUtils.isBlank(chartOfAccountsCodePropertyName) && !StringUtils.isBlank(getChartOfAccountsCodePropertyName()) && chartOfAccountsCodePropertyName.equals(getChartOfAccountsCodePropertyName()) ) && ( !StringUtils.isBlank(accountNumberPropertyName) && !StringUtils.isBlank(getAccountNumberPropertyName()) && accountNumberPropertyName.equals(getAccountNumberPropertyName()) ) );
    }
}
