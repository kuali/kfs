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
package org.kuali.kfs.sys.document;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;

/**
 * This defines methods common for all ledger posting.
 */
public interface LedgerPostingDocument extends FinancialSystemTransactionalDocument {
    /**
     * @return posting year for this document
     */
    public Integer getPostingYear();

    /**
     * Sets the posting year for this document
     * 
     * @param postingYear
     */
    public void setPostingYear(Integer postingYear);

    /**
     * Gets the postingPeriodCode attribute. Part of <code>AccountingPeriod</code> Primary Key.
     * 
     * @return Returns the postingPeriodCode.
     */
    public String getPostingPeriodCode();

    /**
     * Sets the postingPeriodCode attribute value. Part of <code>AccountingPeriod</code> Primary Key.
     * 
     * @param postingPeriodCode The postingPeriodCode to set.
     */
    public void setPostingPeriodCode(String postingPeriodCode);


    /**
     * This method retrieves the accounting period associated with this document.
     */
    public AccountingPeriod getAccountingPeriod();

    /**
     * This method sets the accounting period associated with this document.
     * 
     * @param accountingPeriod
     */
    public void setAccountingPeriod(AccountingPeriod accountingPeriod);
}
