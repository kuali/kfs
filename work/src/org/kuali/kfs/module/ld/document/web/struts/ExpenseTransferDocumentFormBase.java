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

import static org.kuali.Constants.MULTIPLE_VALUE;

import java.util.Map;

import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.labor.bo.LedgerBalance;

/**
 * This class is the base action form for all expense transfer documents.
 */
public abstract class ExpenseTransferDocumentFormBase extends LaborDocumentFormBase implements MultipleValueLookupBroker {

    /**
     * Used to indicate which result set we're using when refreshing/returning from a multi-value lookup
     */
    private String lookupResultsSequenceNumber;

    /**
     * The type of result returned by the multi-value lookup TODO: to be persisted in the lookup results service instead?
     */
    private String lookupResultsBOClassName;

    /**
     * The name of the collection looked up (by a multiple value lookup)
     */
    private String lookedUpCollectionName;

    /**
     * This constructor sets up empty instances for the dependent objects...
     */
    public ExpenseTransferDocumentFormBase() {
        super();
    }

    /**
     * @see org.kuali.core.web.struts.form.AccountingDocumentFormBase#getRefreshCaller()
     */
    @Override
    public String getRefreshCaller() {
        return MULTIPLE_VALUE;
    }

    /**
     * @see MultipleValueLookupBroker#getLookupResultsSequenceNumber()
     */
    public String getLookupResultsSequenceNumber() {
        return lookupResultsSequenceNumber;
    }

    /**
     * @see MultipleValueLookupBroker#setLookupResultsSequenceNumber(String)
     */
    public void setLookupResultsSequenceNumber(String lookupResultsSequenceNumber) {
        this.lookupResultsSequenceNumber = lookupResultsSequenceNumber;
    }

    /**
     * @see MultipleValueLookupBroker#getLookupResultsBOClassName()
     */
    public String getLookupResultsBOClassName() {
        return lookupResultsBOClassName;
    }

    /**
     * @see MultipleValueLookupBroker#setLookupResultsBOClassName(String)
     */
    public void setLookupResultsBOClassName(String lookupResultsBOClassName) {
        this.lookupResultsBOClassName = lookupResultsBOClassName;
    }

    /**
     * @see MultipleValueLookupBroker#getLookupedUpCollectionName()
     */
    public String getLookedUpCollectionName() {
        return lookedUpCollectionName;
    }

    /**
     * @see MultipleValueLookupBroker#getLookupedUpCollectionName(String)
     */
    public void setLookedUpCollectionName(String lookedUpCollectionName) {
        this.lookedUpCollectionName = lookedUpCollectionName;
    }

    /**
     * Get the universityFiscalYear attribute.
     * 
     * @return the universityFiscalYear
     */
    public abstract Integer getUniversityFiscalYear();

    /**
     * Set the universityFiscalYear attribute
     * 
     * @param universityFiscalYear the universityFiscalYear to set
     */
    public abstract void setUniversityFiscalYear(Integer universityFiscalYear);

    /**
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#getForcedReadOnlyFields()
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
        map.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, Boolean.TRUE);
        map.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, Boolean.TRUE);
        return map;
    }

    /**
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#getForcedReadOnlyFields()
     */
    public Map getForcedReadOnlyTargetFields() {
        Map map = super.getForcedReadOnlyFields();
        map.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, Boolean.TRUE);
        map.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, Boolean.TRUE);
        return map;
    }

    /**
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
}
