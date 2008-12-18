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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.util.NumberUtils;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Base implementation for a ledger posting document.
 */
public class LedgerPostingDocumentBase extends FinancialSystemTransactionalDocumentBase implements LedgerPostingDocument {
    private Integer tmpPostingYear;
    private String tmpPostingPeriodCode;

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
        Date date = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        return SpringContext.getBean(AccountingPeriodService.class).getByDate(date);
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
        this.tmpPostingYear = postingYear;
        handleAccountingPeriodChange();
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
        this.tmpPostingPeriodCode = postingPeriodCode;
        handleAccountingPeriodChange();
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#getAccountingPeriod()
     */
    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    /**
     * @see org.kuali.kfs.sys.document.LedgerPostingDocument#setAccountingPeriod(AccountingPeriod)
     */
    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        Integer postingYear = null;
        String postingPeriodCode = null;
        if (accountingPeriod != null) {
            postingYear = accountingPeriod.getUniversityFiscalYear();
            postingPeriodCode = accountingPeriod.getUniversityFiscalPeriodCode();
        }
        this.tmpPostingPeriodCode = postingPeriodCode;
        this.tmpPostingYear = postingYear;
        handleAccountingPeriodChange();
    }

    /**
     * Uses <code>{@link AccountingPeriod}</code> key to set new key values at once. <br/>
     * <p>
     * This is called whenever <code>postingYear</code> or <code>postingPeriodCode</code> is changed. If neither value in the
     * key is null, then a change has occured and the values are handled at once.
     * </p>
     */
    private void handleAccountingPeriodChange() {
        Integer year = this.tmpPostingYear;
        String code = this.tmpPostingPeriodCode;

        if (year != null && StringUtils.isNotBlank(code)) {
            AccountingPeriod accountingPeriod = SpringContext.getBean(AccountingPeriodService.class).getByPeriod(code, year);
            if (ObjectUtils.isNotNull(accountingPeriod)) {
                //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(), 
                //AccountingPeriod does not have any updatable references
                accountingPeriod.refreshNonUpdateableReferences();
                this.accountingPeriod = accountingPeriod;
                this.postingPeriodCode = code;
                this.postingYear = year;
                this.tmpPostingPeriodCode = null;
                this.tmpPostingYear = null;
            }
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
     * Returns the financial document type code for the given document, using the DocumentTypeService
     * @return the financial document type code for the given document
     */
    public String getFinancialDocumentTypeCode() {
        return SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(this.getClass());
    }
}
