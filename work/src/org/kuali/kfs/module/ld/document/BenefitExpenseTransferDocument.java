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
package org.kuali.module.labor.document;

import org.kuali.Constants;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferTargetAccountingLine;
import org.kuali.module.labor.bo.LaborLedgerAccountingLineParser;

/**
 * 
 */
public class BenefitExpenseTransferDocument extends LaborDocument {

    private String emplid;
    
    public String getEmplid() {
        return emplid;
    }

    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Initializes the array lists and some basic info.
     */
    public BenefitExpenseTransferDocument() {
        super();
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     * 
     * @see org.kuali.core.document.TransactionalDocument#getTargetAccountingLinesSectionTitle()
     */
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.TO;
    }
    
    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new LaborLedgerAccountingLineParser();        
    }    
    
    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return ExpenseTransferSourceAccountingLine.class;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTargetAccountingLineClass()
     */
    @Override
    public Class getTargetAccountingLineClass() {
        return ExpenseTransferTargetAccountingLine.class;
    }
}

