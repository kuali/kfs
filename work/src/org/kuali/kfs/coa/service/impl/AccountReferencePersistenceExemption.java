/*
 * Copyright 2011 The Kuali Foundation.
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