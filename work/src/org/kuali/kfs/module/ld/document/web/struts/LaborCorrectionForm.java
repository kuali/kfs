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

package org.kuali.module.labor.web.struts.form;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.Constants;
import org.kuali.core.document.authorization.TransactionalDocumentActionFlags;
import org.kuali.core.web.struts.form.KualiTableRenderFormMetadata;
import org.kuali.core.web.ui.Column;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.web.struts.form.CorrectionForm;
import org.kuali.module.gl.web.struts.form.GroupHolder;
import org.kuali.module.labor.bo.LaborOriginEntry;

public class LaborCorrectionForm extends CorrectionForm {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCorrectionForm.class);
    
    private LaborOriginEntry laborEntryForManualEdit;
    
    private String laborEntryUniversityFiscalYear;
    private String laborEntryFinancialDocumentReversalDate;
    private String laborEntryTransactionDate;
    private String laborEntryTransactionLedgerEntrySequenceNumber;
    private String laborEntryTransactionLedgerEntryAmount;
    
    
     public LaborCorrectionForm() {
        super();
        laborEntryForManualEdit = new LaborOriginEntry();
        laborEntryForManualEdit.setEntryId(0);
        
    }
    public void clearForm() {
        super.clearForm();
        
    }
    
    
    
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
        setLaborEntryForManualEdit(loe);
    }
    
    
    public void setDocType(){
        setDocumentType("LLCP");
        setDocTitle("Labor Ledger Correction Process");
        setHtmlFormAction("laborLedgerCorrection");
    }
    public LaborOriginEntry getLaborEntryForManualEdit() {
        return laborEntryForManualEdit;
    }
    public void setLaborEntryForManualEdit(LaborOriginEntry laborEntryForManualEdit) {
        this.laborEntryForManualEdit = laborEntryForManualEdit;
    }
    
    
    public void updateLaborEntryForManualEdit() {
        laborEntryForManualEdit.setFieldValue("universityFiscalYear", getLaborEntryUniversityFiscalYear());
        laborEntryForManualEdit.setFieldValue("transactionLedgerEntrySequenceNumber",getLaborEntryTransactionLedgerEntrySequenceNumber());
        laborEntryForManualEdit.setFieldValue("transactionLedgerEntryAmount", getLaborEntryTransactionLedgerEntryAmount());
        laborEntryForManualEdit.setFieldValue("transactionDate", getLaborEntryTransactionDate());
        laborEntryForManualEdit.setFieldValue("financialDocumentReversalDate",getLaborEntryFinancialDocumentReversalDate());
    }
    public String getLaborEntryFinancialDocumentReversalDate() {
        return laborEntryFinancialDocumentReversalDate;
    }
    public void setLaborEntryFinancialDocumentReversalDate(String laborEntryFinancialDocumentReversalDate) {
        this.laborEntryFinancialDocumentReversalDate = laborEntryFinancialDocumentReversalDate;
    }
    public String getLaborEntryTransactionDate() {
        return laborEntryTransactionDate;
    }
    public void setLaborEntryTransactionDate(String laborEntryTransactionDate) {
        this.laborEntryTransactionDate = laborEntryTransactionDate;
    }
    public String getLaborEntryTransactionLedgerEntryAmount() {
        return laborEntryTransactionLedgerEntryAmount;
    }
    public void setLaborEntryTransactionLedgerEntryAmount(String laborEntryTransactionLedgerEntryAmount) {
        this.laborEntryTransactionLedgerEntryAmount = laborEntryTransactionLedgerEntryAmount;
    }
    public String getLaborEntryTransactionLedgerEntrySequenceNumber() {
        return laborEntryTransactionLedgerEntrySequenceNumber;
    }
    public void setLaborEntryTransactionLedgerEntrySequenceNumber(String laborEntryTransactionLedgerEntrySequenceNumber) {
        this.laborEntryTransactionLedgerEntrySequenceNumber = laborEntryTransactionLedgerEntrySequenceNumber;
    }
    public String getLaborEntryUniversityFiscalYear() {
        return laborEntryUniversityFiscalYear;
    }
    public void setLaborEntryUniversityFiscalYear(String laborEntryUniversityFiscalYear) {
        this.laborEntryUniversityFiscalYear = laborEntryUniversityFiscalYear;
    }
 }
