/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.kfs.rule;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.BusinessRule;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;

/**
 * Defines methods common to all *AccountingLineRule interfaces
 */
public interface AccountingLineRule<F extends AccountingDocument> extends BusinessRule {
    /**
     * This method checks the amount of a given accounting line to make sure it's a valid amount.
     * 
     * @param document
     * @param accountingLine
     * @return boolean True if there aren't any issues, false otherwise.
     */
    public boolean isAmountValid(F document, AccountingLine accountingLine);

    /**
     * This method checks to see if the object code for the passed in accounting is allowed.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code is allowed.
     */
    public boolean isObjectCodeAllowed(AccountingLine accountingLine);

    /**
     * This checks the accounting line's object type code to ensure that it is allowed.
     * 
     * @param accountingLine
     * @return boolean
     */
    public boolean isObjectTypeAllowed(AccountingLine accountingLine);

    /**
     * This method checks to see if the object sub-type code for the accouting line's object code is allowed.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code's object sub type code is allowed; false otherwise.
     */
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine);

    /**
     * This method checks to see if the object level for the accouting line's object code is allowed.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code's object sub type code is allowed; false otherwise.
     */
    public boolean isObjectLevelAllowed(AccountingLine accountingLine);

    /**
     * This method checks to see if the object consolidation for the accouting line's object code is allowed.
     * 
     * @param accountingLine
     * @return boolean True if the use of the object code's object sub type code is allowed; false otherwise.
     */
    public boolean isObjectConsolidationAllowed(AccountingLine accountingLine);

    /**
     * This method checks to see if the sub fund group code for the accouting line's account is allowed.
     * 
     * @param accountingLine
     * @return boolean
     */
    public boolean isSubFundGroupAllowed(AccountingLine accountingLine);

    /**
     * This method checks to see if the fund group code for the accouting line's account
     * 
     * @param accountingLine
     * @return boolean
     */
    public boolean isFundGroupAllowed(AccountingLine accountingLine);

    /**
     * This method determines if the passed in accounting line is a debit or not.
     * 
     * @param financialDocument
     * @param accountingLine
     * 
     * @return boolean
     */
    public boolean isDebit(AccountingDocument financialDocument, AccountingLine accountingLine);

    /**
     * This method determines if the passed in accounting line is a credit or not.
     * 
     * @param accountingLine
     * @param financialDocument TODO
     * @return boolean
     */
    public boolean isCredit(AccountingLine accountingLine, F financialDocument);
}