/*
 * Copyright 2007-2008 The Kuali Foundation
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
