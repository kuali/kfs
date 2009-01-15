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
package org.kuali.kfs.sys.document;

import java.sql.Date;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.GeneralLedgerInputTypeService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Base implementation for a ledger posting document.
 */
public class LedgerPostingDocumentBase extends FinancialSystemTransactionalDocumentBase implements LedgerPostingDocument {
    static private transient DateTimeService dateTimeService;
    static private transient AccountingPeriodService accountingPeriodService;
    static private transient GeneralLedgerInputTypeService generalLedgerInputTypeService;
       
    protected AccountingPeriod accountingPeriod;
    protected Integer postingYear;
    protected String postingPeriodCode;
    protected boolean checkPostingYearForCopy;

    /**
     * Constructs a LedgerPostingDocumentBase.java.
     */
    public LedgerPostingDocumentBase() {
        super();
        createInitialAccountingPeriod();
    }

    /**
     * Used during initialization to provide a base <code>{@link AccountingPeriod}</code>.<br/>
     * <p>
     * This is a hack right now because its intended to be set by the
     * <code>{@link org.kuali.kfs.coa.service.AccountingPeriodService}</code>
     * 
     * @return AccountingPeriod
     */
    private void createInitialAccountingPeriod() { 
        AccountingPeriod accountingPeriod = retrieveCurrentAccountingPeriod();
        setAccountingPeriod(accountingPeriod);
    }
    
    /**
     * Finds the accounting period for the current date
     * @return the current accounting period
     */
    private AccountingPeriod retrieveCurrentAccountingPeriod() {
        try {
            Date date = getDateTimeService().getCurrentSqlDate();
            return getAccountingPeriodService().getByDate(date);
        } catch ( RuntimeException ex ) {
            // catch and ignore - prevent blowup when called before services initialized
            return null;
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#getPostingYear()
     */
    public Integer getPostingYear() {
        return postingYear;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#setPostingYear(java.lang.Integer)
     */
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#getPostingPeriodCode()
     */
    public String getPostingPeriodCode() {
        return postingPeriodCode;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#setPostingPeriodCode(java.lang.String)
     */
    public void setPostingPeriodCode(String postingPeriodCode) {
        this.postingPeriodCode = postingPeriodCode;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#getAccountingPeriod()
     */
    public AccountingPeriod getAccountingPeriod() {
        accountingPeriod = getAccountingPeriodService().getByPeriod(postingPeriodCode, postingYear);

        return accountingPeriod;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#setAccountingPeriod(AccountingPeriod)
     */
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
        
        if(ObjectUtils.isNotNull(accountingPeriod)) {
            this.setPostingYear(accountingPeriod.getUniversityFiscalYear());
            this.setPostingPeriodCode(accountingPeriod.getUniversityFiscalPeriodCode());
        }
    }
    
    /**
     * If we've copied, we need to update the posting period and year
     * @see org.kuali.rice.kns.document.DocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException, IllegalStateException {
        super.toCopy();
        setAccountingPeriod(retrieveCurrentAccountingPeriod());
    }
    
    /**
     * Returns the financial document type code for the given document, using the GeneralLedgerInputTypeService
     * @return the financial document type code for the given document
     */
    public String getFinancialDocumentTypeCode() {
        return getGeneralLedgerInputTypeService().getGeneralLedgerInputTypeByDocumentClass(this.getClass()).getInputTypeCode();
    }
    

    public static GeneralLedgerInputTypeService getGeneralLedgerInputTypeService() {
        if ( generalLedgerInputTypeService == null ) {
            generalLedgerInputTypeService = SpringContext.getBean(GeneralLedgerInputTypeService.class);
        }
        return generalLedgerInputTypeService;
    }

    public static DateTimeService getDateTimeService() {
        if ( dateTimeService == null ) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

    public static AccountingPeriodService getAccountingPeriodService() {
        if ( accountingPeriodService == null ) {
            accountingPeriodService = SpringContext.getBean(AccountingPeriodService.class);
        }
        return accountingPeriodService;
    }
}
