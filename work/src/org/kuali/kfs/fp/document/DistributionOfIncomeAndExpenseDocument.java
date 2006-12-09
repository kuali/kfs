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

package org.kuali.module.financial.document;

import org.kuali.Constants;
import org.kuali.core.document.TransactionalDocumentBase;


/**
 * The Distribution of Income and Expense (DI) document is used to distribute income or expense, or assets and liabilities. Amounts
 * being distributed are usually the result of an accumulation of transactions that need to be divided up between various accounts.
 * 
 * 
 */
public class DistributionOfIncomeAndExpenseDocument extends TransactionalDocumentBase {

    /**
     * Constructs a DistributionOfIncomeAndExpenseDocument.java.
     */
    public DistributionOfIncomeAndExpenseDocument() {
        super();
    }

    /**
     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.FROM;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.TO;
    }

}
