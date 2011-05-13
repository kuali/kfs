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

import java.util.List;

import org.kuali.kfs.gl.document.web.struts.CorrectionForm;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.document.LaborCorrectionDocument;
import org.kuali.kfs.module.ld.document.service.LaborCorrectionDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.web.ui.Column;


/**
 * Struts Action Form for the Labor Ledger Correction Process.
 */
public class LaborCorrectionForm extends CorrectionForm {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCorrectionForm.class);

    protected LaborOriginEntry laborEntryForManualEdit;
    protected String laborEntryUniversityFiscalYear;
    protected String laborEntryFinancialDocumentReversalDate;
    protected String laborEntryTransactionDate;
    protected String laborEntryTransactionLedgerEntrySequenceNumber;
    protected String laborEntryTransactionLedgerEntryAmount;
    
    protected String laborEntryTransactionPostingDate;
    protected String laborEntryPayPeriodEndDate;
    protected String laborEntryTransactionTotalHours;
    protected String laborEntryPayrollEndDateFiscalYear;
    protected String laborEntryEmployeeRecord;

    /**
     * Constructs a LaborCorrectionForm instance.
     */
    public LaborCorrectionForm() {
        super();
        laborEntryForManualEdit = new LaborOriginEntry();
        laborEntryForManualEdit.setEntryId(0);

    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "LLCP";
    }
    
    /**
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionForm#clearForm()
     */
    public void clearForm() {
        super.clearForm();

    }

    /**
     * This method is for clearing Labor Origin Entry for Manual Edit
     */
    public void clearLaborEntryForManualEdit() {
        LaborOriginEntry loe = new LaborOriginEntry();
        loe.setEntryId(0);
        loe.setSubAccountNumber("");
        loe.setFinancialSubObjectCode("");
        loe.setProjectCode("");
        setLaborEntryFinancialDocumentReversalDate("");
        setLaborEntryTransactionDate("");
        setLaborEntryTransactionLedgerEntryAmount("");
        setLaborEntryTransactionLedgerEntrySequenceNumber("");
        setLaborEntryUniversityFiscalYear("");
        
        setLaborEntryTransactionPostingDate("");
        setLaborEntryPayPeriodEndDate("");
        setLaborEntryTransactionTotalHours("");
        setLaborEntryPayrollEndDateFiscalYear("");
        setLaborEntryEmployeeRecord("");
        
        
        setLaborEntryForManualEdit(loe);
    }

    /**
     * @see org.kuali.kfs.gl.document.web.struts.CorrectionForm#setDocType()
     */
    public void setDocType() {
        setDocumentType("LLCP");
        //setDocTitle("Labor Ledger Correction Process");
        setDocTitle("LaborCorrectionDocument");
        setHtmlFormAction("laborLedgerCorrection");
    }

    /**
     * Gets the laborEntryForManualEdit attribute.
     * 
     * @return Returns the laborEntryForManualEdit.
     */
    public LaborOriginEntry getLaborEntryForManualEdit() {
        return laborEntryForManualEdit;
    }

    /**
     * Sets the laborEntryForManualEdit attribute value.
     * 
     * @param laborEntryForManualEdit The laborEntryForManualEdit to set.
     */
    public void setLaborEntryForManualEdit(LaborOriginEntry laborEntryForManualEdit) {
        this.laborEntryForManualEdit = laborEntryForManualEdit;
    }

    /**
     * Updates labor entries for manual edit.
     */
    public void updateLaborEntryForManualEdit() {
        laborEntryForManualEdit.setFieldValue("universityFiscalYear", getLaborEntryUniversityFiscalYear());
        laborEntryForManualEdit.setFieldValue("transactionLedgerEntrySequenceNumber", getLaborEntryTransactionLedgerEntrySequenceNumber());
        laborEntryForManualEdit.setFieldValue("transactionLedgerEntryAmount", getLaborEntryTransactionLedgerEntryAmount());
        laborEntryForManualEdit.setFieldValue("transactionDate", getLaborEntryTransactionDate());
        laborEntryForManualEdit.setFieldValue("financialDocumentReversalDate", getLaborEntryFinancialDocumentReversalDate());
        
        
        laborEntryForManualEdit.setFieldValue("transactionPostingDate", getLaborEntryTransactionPostingDate());
        laborEntryForManualEdit.setFieldValue("payPeriodEndDate", getLaborEntryPayPeriodEndDate());
        laborEntryForManualEdit.setFieldValue("transactionTotalHours", getLaborEntryTransactionTotalHours());
        laborEntryForManualEdit.setFieldValue("payrollEndDateFiscalYear", getLaborEntryPayrollEndDateFiscalYear());
        laborEntryForManualEdit.setFieldValue("employeeRecord", getLaborEntryEmployeeRecord());
        
    }

    /**
     * Gets the laborEntryFinancialDocumentReversalDate attribute.
     * 
     * @return Returns the laborEntryFinancialDocumentReversalDate.
     */
    public String getLaborEntryFinancialDocumentReversalDate() {
        return laborEntryFinancialDocumentReversalDate;
    }

    /**
     * Sets the laborEntryFinancialDocumentReversalDate attribute value.
     * 
     * @param laborEntryFinancialDocumentReversalDate The laborEntryFinancialDocumentReversalDate to set.
     */
    public void setLaborEntryFinancialDocumentReversalDate(String laborEntryFinancialDocumentReversalDate) {
        this.laborEntryFinancialDocumentReversalDate = laborEntryFinancialDocumentReversalDate;
    }

    /**
     * Gets the laborEntryTransactionDate attribute.
     * 
     * @return Returns the laborEntryTransactionDate.
     */
    public String getLaborEntryTransactionDate() {
        return laborEntryTransactionDate;
    }

    /**
     * Sets the laborEntryTransactionDate attribute value.
     * 
     * @param laborEntryTransactionDate The laborEntryTransactionDate to set.
     */
    public void setLaborEntryTransactionDate(String laborEntryTransactionDate) {
        this.laborEntryTransactionDate = laborEntryTransactionDate;
    }

    /**
     * Gets the laborEntryTransactionLedgerEntryAmount attribute.
     * 
     * @return Returns the laborEntryTransactionLedgerEntryAmount.
     */
    public String getLaborEntryTransactionLedgerEntryAmount() {
        return laborEntryTransactionLedgerEntryAmount;
    }

    /**
     * Sets the laborEntryTransactionLedgerEntryAmount attribute value.
     * 
     * @param laborEntryTransactionLedgerEntryAmount The laborEntryTransactionLedgerEntryAmount to set.
     */
    public void setLaborEntryTransactionLedgerEntryAmount(String laborEntryTransactionLedgerEntryAmount) {
        this.laborEntryTransactionLedgerEntryAmount = laborEntryTransactionLedgerEntryAmount;
    }

    /**
     * Gets the laborEntryTransactionLedgerEntrySequenceNumber attribute.
     * 
     * @return Returns the laborEntryTransactionLedgerEntrySequenceNumber.
     */
    public String getLaborEntryTransactionLedgerEntrySequenceNumber() {
        return laborEntryTransactionLedgerEntrySequenceNumber;
    }

    /**
     * Sets the laborEntryTransactionLedgerEntrySequenceNumber attribute value.
     * 
     * @param laborEntryTransactionLedgerEntrySequenceNumber The laborEntryTransactionLedgerEntrySequenceNumber to set.
     */
    public void setLaborEntryTransactionLedgerEntrySequenceNumber(String laborEntryTransactionLedgerEntrySequenceNumber) {
        this.laborEntryTransactionLedgerEntrySequenceNumber = laborEntryTransactionLedgerEntrySequenceNumber;
    }

    /**
     * Gets the laborEntryUniversityFiscalYear attribute.
     * 
     * @return Returns the laborEntryUniversityFiscalYear.
     */
    public String getLaborEntryUniversityFiscalYear() {
        return laborEntryUniversityFiscalYear;
    }

    /**
     * Sets the laborEntryUniversityFiscalYear attribute value.
     * 
     * @param laborEntryUniversityFiscalYear The laborEntryUniversityFiscalYear to set.
     */
    public void setLaborEntryUniversityFiscalYear(String laborEntryUniversityFiscalYear) {
        this.laborEntryUniversityFiscalYear = laborEntryUniversityFiscalYear;
    }

    /**
     * Gets the LaborCorrectionDocument attribute.
     * 
     * @return Returns the LaborCorrectionDocument.
     */
    public LaborCorrectionDocument getLaborCorrectionDocument() {
        return (LaborCorrectionDocument) getDocument();
    }

    /**
     * Gets the TableRenderColumnMetadata attribute.
     * 
     * @return Returns the TableRenderColumnMetadata.
     *      
     * KRAD Conversion: gets the column metadata.
     */
    public List<Column> getTableRenderColumnMetadata() {
        return SpringContext.getBean(LaborCorrectionDocumentService.class).getTableRenderColumnMetadata(getDocument().getDocumentNumber());
    }

    public String getLaborEntryEmployeeRecord() {
        return laborEntryEmployeeRecord;
    }

    public void setLaborEntryEmployeeRecord(String laborEntryEmployeeRecord) {
        this.laborEntryEmployeeRecord = laborEntryEmployeeRecord;
    }

    public String getLaborEntryPayPeriodEndDate() {
        return laborEntryPayPeriodEndDate;
    }

    public void setLaborEntryPayPeriodEndDate(String laborEntryPayPeriodEndDate) {
        this.laborEntryPayPeriodEndDate = laborEntryPayPeriodEndDate;
    }

    public String getLaborEntryPayrollEndDateFiscalYear() {
        return laborEntryPayrollEndDateFiscalYear;
    }

    public void setLaborEntryPayrollEndDateFiscalYear(String laborEntryPayrollEndDateFiscalYear) {
        this.laborEntryPayrollEndDateFiscalYear = laborEntryPayrollEndDateFiscalYear;
    }

    public String getLaborEntryTransactionPostingDate() {
        return laborEntryTransactionPostingDate;
    }

    public void setLaborEntryTransactionPostingDate(String laborEntryTransactionPostingDate) {
        this.laborEntryTransactionPostingDate = laborEntryTransactionPostingDate;
    }

    public String getLaborEntryTransactionTotalHours() {
        return laborEntryTransactionTotalHours;
    }

    public void setLaborEntryTransactionTotalHours(String laborEntryTransactionTotalHours) {
        this.laborEntryTransactionTotalHours = laborEntryTransactionTotalHours;
    }


}
