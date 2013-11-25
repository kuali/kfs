/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.document.web.struts;

import java.util.Map;

import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.LaborAccountingLineOverride;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Base Struts Action Form for all expense transfer documents.
 */
public abstract class ExpenseTransferDocumentFormBase extends LaborDocumentFormBase implements MultipleValueLookupBroker {

    protected String lookupResultsSequenceNumber; // Indicates which result set we are using when refreshing/returning from a
                                                // multi-value lookup.
    protected String lookupResultsBOClassName; // Type of result returned by the multi-value lookup. ?to be persisted in the lookup
                                                // results service instead?
    protected String lookedUpCollectionName; // The name of the collection looked up (by a multiple value lookup)
    protected Integer universityFiscalYear;

    /**
     * Constructs a ExpenseTransferDocumentFormBase instance for the dependent object.
     */
    public ExpenseTransferDocumentFormBase() {
        super();
        setUniversityFiscalYear(SpringContext.getBean(OptionsService.class).getCurrentYearOptions().getUniversityFiscalYear());
    }

    @Override
    public void addRequiredNonEditableProperties() {
        super.addRequiredNonEditableProperties();
        registerRequiredNonEditableProperty(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER);
    }

    /**
     * @see org.kuali.kfs.module.ld.document.web.struts.MultipleValueLookupBroker#getLookupResultsSequenceNumber()
     */
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.web.struts.MultipleValueLookupBroker#setLookupResultsSequenceNumber(java.lang.String)
     */
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.web.struts.MultipleValueLookupBroker#getLookupResultsBOClassName()
     */
    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.web.struts.MultipleValueLookupBroker#setLookupResultsBOClassName(java.lang.String)
     */
    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.web.struts.MultipleValueLookupBroker#getLookedUpCollectionName()
     */
    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    /**
     * @see org.kuali.kfs.module.ld.document.web.struts.MultipleValueLookupBroker#setLookedUpCollectionName(java.lang.String)
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
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#getForcedReadOnlyFields()
     */
    public Map getForcedReadOnlyTargetFields() {
        Map map = super.getForcedReadOnlyFields();
        map.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, Boolean.TRUE);
        map.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, Boolean.TRUE);
        return map;
    }

    /**
     * Deal with the labor-specific override code to the given source accounting line
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#populateSourceAccountingLine(org.kuali.kfs.sys.businessobject.SourceAccountingLine)
     */
    @Override
    public void populateSourceAccountingLine(SourceAccountingLine sourceLine, String accountingLinePropertyName, Map parameterMap) {
        super.populateSourceAccountingLine(sourceLine, accountingLinePropertyName, parameterMap);
        LaborAccountingLineOverride.populateFromInput(sourceLine);
    }

    /**
     * Deal with the labor-specific override code to the given target accounting line
     * 
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#populateTargetAccountingLine(org.kuali.kfs.sys.businessobject.TargetAccountingLine)
     */
    @Override
    public void populateTargetAccountingLine(TargetAccountingLine targetLine, String accountingLinePropertyName, Map parameterMap) {
        super.populateTargetAccountingLine(targetLine, accountingLinePropertyName, parameterMap);
        LaborAccountingLineOverride.populateFromInput(targetLine);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase#repopulateOverrides(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void repopulateOverrides(AccountingLine line, String accountingLinePropertyName, Map parameterMap) {
        super.repopulateOverrides(line, accountingLinePropertyName, parameterMap);
        LaborAccountingLineOverride.determineNeededOverrides(getFinancialDocument(), line);
        if (line.getNonFringeAccountOverrideNeeded()) {
            if (parameterMap.containsKey(accountingLinePropertyName+".nonFringeAccountOverride.present")) {
                line.setNonFringeAccountOverride(parameterMap.containsKey(accountingLinePropertyName+".nonFringeAccountOverride"));
            }
        } else {
            line.setNonFringeAccountOverride(false);
        }
    }
    
}
