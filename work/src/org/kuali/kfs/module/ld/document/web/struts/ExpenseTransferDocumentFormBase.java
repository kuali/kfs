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
package org.kuali.module.labor.web.struts.form;

import java.util.Map;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.labor.LaborPropertyConstants;
import org.kuali.module.labor.bo.LaborAccountingLineOverride;

/**
 * Base Struts Action Form for all expense transfer documents.
 */
public abstract class ExpenseTransferDocumentFormBase extends LaborDocumentFormBase implements MultipleValueLookupBroker {

    private String lookupResultsSequenceNumber; // Indicates which result set we are using when refreshing/returning from a
                                                // multi-value lookup.
    private String lookupResultsBOClassName; // Type of result returned by the multi-value lookup. ?to be persisted in the lookup
                                                // results service instead?
    private String lookedUpCollectionName; // The name of the collection looked up (by a multiple value lookup)
    private Integer universityFiscalYear;

    /**
     * Constructs a ExpenseTransferDocumentFormBase instance for the dependent object.
     */
    public ExpenseTransferDocumentFormBase() {
        super();
        setUniversityFiscalYear(SpringContext.getBean(OptionsService.class).getCurrentYearOptions().getUniversityFiscalYear());
    }

    /**
     * @see org.kuali.core.web.struts.form.AccountingDocumentFormBase#getRefreshCaller()
     */
    @Override
    public String getRefreshCaller() {
        return KFSConstants.MULTIPLE_VALUE;
    }

    /**
     * @see org.kuali.module.labor.web.struts.form.MultipleValueLookupBroker#getLookupResultsSequenceNumber()
     */
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    /**
     * @see org.kuali.module.labor.web.struts.form.MultipleValueLookupBroker#setLookupResultsSequenceNumber(java.lang.String)
     */
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    /**
     * @see org.kuali.module.labor.web.struts.form.MultipleValueLookupBroker#getLookupResultsBOClassName()
     */
    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }

    /**
     * @see org.kuali.module.labor.web.struts.form.MultipleValueLookupBroker#setLookupResultsBOClassName(java.lang.String)
     */
    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }

    /**
     * @see org.kuali.module.labor.web.struts.form.MultipleValueLookupBroker#getLookedUpCollectionName()
     */
    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    /**
     * @see org.kuali.module.labor.web.struts.form.MultipleValueLookupBroker#setLookedUpCollectionName(java.lang.String)
     */
    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Populate the search fields with the last search criteria
     */
    public abstract void populateSearchFields();

    /**
     * Gets forced read only source fields
     * 
     * @return Returns a Map of read only source fields
     */
    public Map getForcedReadOnlySourceFields() {
        Map map = super.getForcedReadOnlyFields();
        map.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, Boolean.TRUE);
        map.put(KFSPropertyConstants.ACCOUNT_NUMBER, Boolean.TRUE);
        map.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, Boolean.TRUE);
        map.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, Boolean.TRUE);
        map.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, Boolean.TRUE);
        map.put(KFSPropertyConstants.PROJECT_CODE, Boolean.TRUE);
        map.put(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID, Boolean.TRUE);
        map.put(KFSPropertyConstants.POSITION_NUMBER, Boolean.TRUE);
        map.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, Boolean.TRUE);
        map.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, Boolean.TRUE);
        return map;
    }

    /**
     * Returns forced read only target fields (i.e read only fields plus payroll end date fiscal 
     * period code and payroll end date fiscal year)
     * 
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#getForcedReadOnlyFields()
     */
    public Map getForcedReadOnlyTargetFields() {
        Map map = super.getForcedReadOnlyFields();
        map.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, Boolean.TRUE);
        map.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, Boolean.TRUE);
        return map;
    }

    /**
     * Returns forced accounting line editable fields (i.e accounting line editable fields from LaborDocumentFormBase
     * without chart of accounts code, account number, sub-account number, financial sub object code,\
     * project code, organization reference id, and amount)
     * 
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#getAccountingLineEditableFields()
     */
    @Override
    public Map getAccountingLineEditableFields() {
        Map map = super.getAccountingLineEditableFields();
        map.remove(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        map.remove(KFSPropertyConstants.ACCOUNT_NUMBER);
        map.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        map.remove(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        map.remove(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        map.remove(KFSPropertyConstants.PROJECT_CODE);
        map.remove(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID);
        map.remove(KFSPropertyConstants.POSITION_NUMBER);
        return map;
    }

    /**
     * Deal with the labor-specific override code to the given source accounting line
     * 
     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#populateSourceAccountingLine(org.kuali.kfs.bo.SourceAccountingLine)
     */
    @Override
    public void populateSourceAccountingLine(SourceAccountingLine sourceLine) {
        super.populateSourceAccountingLine(sourceLine);
        LaborAccountingLineOverride.populateFromInput(sourceLine);
    }

    /**
     * Deal with the labor-specific override code to the given target accounting line
     * 
     * @see org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase#populateTargetAccountingLine(org.kuali.kfs.bo.TargetAccountingLine)
     */
    @Override
    public void populateTargetAccountingLine(TargetAccountingLine targetLine) {
        super.populateTargetAccountingLine(targetLine);
        LaborAccountingLineOverride.populateFromInput(targetLine);
    }
}
