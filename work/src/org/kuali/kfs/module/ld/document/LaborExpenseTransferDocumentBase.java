/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.List;

import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocumentHelper;
import org.kuali.module.labor.document.LaborLedgerPostingDocumentBase;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferTargetAccountingLine;

/**
 * Base class for Expense Transfer Documents
 */
public abstract class LaborExpenseTransferDocumentBase extends LaborLedgerPostingDocumentBase implements AmountTotaling, Copyable, Correctable, LaborExpenseTransferDocument {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LaborExpenseTransferDocumentBase.class);

    private String emplid;
    
    private AccountingDocumentHelper helper;

    /**
     * Constructor 
     */
    public LaborExpenseTransferDocumentBase() {
        super();
        setAccountingDocumentHelper(new LaborExpenseTransferDocumentHelper<LaborExpenseTransferDocument>(this));
    }
    
    /**
     * Assign the <code>{@link AccountingDocumentHelper}</code>
     * 
     * @param helper
     */ 
    public void setAccountingDocumentHelper(AccountingDocumentHelper helper) {
        this.helper = helper;
    }
    
    /**
     * Retrieve the <code>{@link AccountingDocumentHelper}</code>
     *
     * @return AccountingDocumentHelper
     */
    public AccountingDocumentHelper getAccountingDocumentHelper() {
        return helper;
    }
    
    /**
     * @see org.kuali.module.labor.document.LaborExpenseTransferDocument#getEmplid()
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * @see org.kuali.module.labor.document.LaborExpenseTransferDocument#setEmplid(String)
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }

    /**
     * Overrides the base implementation to return "From".
     * 
     * @see org.kuali.core.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.FROM;
    }

    /**
     * Overrides the base implementation to return "To".
     * 
     * @see org.kuali.core.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.TO;
    }
        
    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    public Class getSourceAccountingLineClass() {
        return ExpenseTransferSourceAccountingLine.class;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTargetAccountingLineClass()
     */
    public Class getTargetAccountingLineClass() {
        return ExpenseTransferTargetAccountingLine.class;
    }


    @Override
    public List generateSaveEvents() {
        return getAccountingDocumentHelper().generateSaveEvents();
    }
}

