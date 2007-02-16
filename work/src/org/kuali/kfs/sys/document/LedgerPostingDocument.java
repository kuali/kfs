/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.document;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.module.chart.bo.AccountingPeriod;

/**
 * This defines methods common for all ledger posting.
 */
public interface LedgerPostingDocument extends TransactionalDocument{
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
