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
package org.kuali.kfs.gl.businessobject;

import org.kuali.kfs.coa.businessobject.Account;

/**
 * An interface that declares the methods that a Business Object must implement, if it should have its flexible offset account updated by
 * the FlexibleOffsetAccountService.
 * @see org.kuali.kfs.sys.service.FlexibleOffsetAccountService
 */
public interface FlexibleAccountUpdateable {
    /**
     * Returns the university fiscal year of the business object to update
     * @return a valid university fiscal year
     */
    public abstract Integer getUniversityFiscalYear();
    /**
     * Returns the chart of accounts code of the business object to update
     * @return a valid chart of accounts code
     */
    public abstract String getChartOfAccountsCode();
    /**
     * Returns the account number of the business object to update
     * @return a valid account number
     */
    public abstract String getAccountNumber();
    /**
     * Returns the balance type code of the business object to update
     * @return a valid balance type code
     */
    public abstract String getFinancialBalanceTypeCode();
    /**
     * Returns the document type code of the business object to update
     * @return a valid document code
     */
    public abstract String getFinancialDocumentTypeCode();
    
    /**
     * Returns the object code of the business object to update
     * @return a valid object code
     */
    public abstract String getFinancialObjectCode();
    
    /**
     * Sets the business object's account attribute
     * @param a an account business object to set
     */
    public abstract void setAccount(Account a);
    /**
     * Sets the chart of accounts code of the business object
     * @param chartCode the chart code to set
     */
    public abstract void setChartOfAccountsCode(String chartCode);
    /**
     * Sets the account number of the business object
     * @param accountNumber the account number to set
     */
    public abstract void setAccountNumber(String accountNumber);
    /**
     * Sets the sub-account number of the business object
     * @param subAccountNumber the sub account number to set
     */
    public abstract void setSubAccountNumber(String subAccountNumber);
    /**
     * Sets the financial sub-object code of the business object
     * @param financialSubObjectCode the financial sub-object code to set
     */
    public abstract void setFinancialSubObjectCode(String financialSubObjectCode);
}
