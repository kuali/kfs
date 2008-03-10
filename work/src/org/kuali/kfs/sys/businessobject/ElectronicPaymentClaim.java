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

package org.kuali.kfs.bo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.service.DocumentService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.financial.bo.AdvanceDepositDetail;
import org.kuali.module.financial.document.AdvanceDepositDocument;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class is used to represent an electronic payment claim.
 */
public class ElectronicPaymentClaim extends PersistableBusinessObjectBase {
    
    public final static class ClaimStatusCodes {
        public final static String CLAIMED = "C";
        public final static String UNCLAIMED = "U";
    }

    private String documentNumber;
    private Integer financialDocumentLineNumber;
    private String referenceFinancialDocumentNumber;
    private Integer financialDocumentPostingYear;
    private String financialDocumentPostingPeriodCode;
    private String paymentClaimStatusCode;
    
    private AdvanceDepositDocument generatingDocument;
    private AccountingPeriod financialDocumentPostingPeriod;
    private DocumentHeader generatingDocumentHeader;

    /**
     * Default constructor.  It constructs.
     */
    public ElectronicPaymentClaim() {}

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the financialDocumentLineNumber attribute.
     * 
     * @return Returns the financialDocumentLineNumber
     */
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }

    /**
     * Sets the financialDocumentLineNumber attribute.
     * 
     * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
     */
    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
    }


    /**
     * Gets the referenceFinancialDocumentNumber attribute.
     * 
     * @return Returns the referenceFinancialDocumentNumber
     */
    public String getReferenceFinancialDocumentNumber() {
        return referenceFinancialDocumentNumber;
    }

    /**
     * Sets the referenceFinancialDocumentNumber attribute.
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
        this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
    }


    /**
     * Gets the financialDocumentPostingYear attribute.
     * 
     * @return Returns the financialDocumentPostingYear
     */
    public Integer getFinancialDocumentPostingYear() {
        return financialDocumentPostingYear;
    }

    /**
     * Sets the financialDocumentPostingYear attribute.
     * 
     * @param financialDocumentPostingYear The financialDocumentPostingYear to set.
     */
    public void setFinancialDocumentPostingYear(Integer financialDocumentPostingYear) {
        this.financialDocumentPostingYear = financialDocumentPostingYear;
    }


    /**
     * Gets the financialDocumentPostingPeriodCode attribute.
     * 
     * @return Returns the financialDocumentPostingPeriodCode
     */
    public String getFinancialDocumentPostingPeriodCode() {
        return financialDocumentPostingPeriodCode;
    }

    /**
     * Sets the financialDocumentPostingPeriodCode attribute.
     * 
     * @param financialDocumentPostingPeriodCode The financialDocumentPostingPeriodCode to set.
     */
    public void setFinancialDocumentPostingPeriodCode(String financialDocumentPostingPeriodCode) {
        this.financialDocumentPostingPeriodCode = financialDocumentPostingPeriodCode;
    }

    /**
     * Gets the paymentClaimStatusCode attribute. 
     * @return Returns the paymentClaimStatusCode.
     */
    public String getPaymentClaimStatusCode() {
        return paymentClaimStatusCode;
    }

    /**
     * Sets the paymentClaimStatusCode attribute value.
     * @param paymentClaimStatusCode The paymentClaimStatusCode to set.
     */
    public void setPaymentClaimStatusCode(String paymentClaimStatusCode) {
        this.paymentClaimStatusCode = paymentClaimStatusCode;
    }

    /**
     * Gets the generatingDocument attribute. 
     * @return Returns the generatingDocument.
     */
    public AdvanceDepositDocument getGeneratingDocument() throws WorkflowException {
        if (this.generatingDocument == null || !this.generatingDocument.getDocumentNumber().equals(documentNumber)) {
            generatingDocument = (AdvanceDepositDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
        }
        return this.generatingDocument;
    }
    
    /**
     * Gets the generatingDocumentHeader attribute. 
     * @return Returns the generatingDocumentHeader.
     */
    public DocumentHeader getGeneratingDocumentHeader() {
        return generatingDocumentHeader;
    }

    /**
     * Sets the generatingDocumentHeader attribute value.
     * @param generatingDocumentHeader The generatingDocumentHeader to set.
     * @deprecated
     */
    public void setGeneratingDocumentHeader(DocumentHeader generatingDocumentHeader) {
        this.generatingDocumentHeader = generatingDocumentHeader;
    }

    /**
     * Returns the accounting line on the generating Advance Deposit document for the transaction which generated this record
     * @return the accounting line that describes the transaction responsible for the creation of this record
     */
    public SourceAccountingLine getGeneratingAccountingLine() throws WorkflowException {
        AdvanceDepositDocument generatingDocument = getGeneratingDocument();
        if (generatingDocument != null && generatingDocument.getSourceAccountingLines() != null) {
            return generatingDocument.getSourceAccountingLine(financialDocumentLineNumber.intValue() - 1);
        }
        return null;
    }
    
    /**
     * Returns the AdvanceDepositDetail on the generating Advance Deposit document for the transaction which generated this record
     * @return the advance deposit detail that describes the transaction responsible for the creation of this record
     */
    public AdvanceDepositDetail getGeneratingAdvanceDepositDetail() throws WorkflowException {
        AdvanceDepositDocument generatingDocument = getGeneratingDocument();
        if (generatingDocument != null && generatingDocument.getSourceAccountingLines() != null) {
            return generatingDocument.getAdvanceDepositDetail(financialDocumentLineNumber.intValue() - 1);
        }
        return null;
    }

    /**
     * Gets the financialDocumentPostingPeriod attribute. 
     * @return Returns the financialDocumentPostingPeriod.
     */
    public AccountingPeriod getFinancialDocumentPostingPeriod() {
        return financialDocumentPostingPeriod;
    }

    /**
     * Sets the financialDocumentPostingPeriod attribute value.
     * @param financialDocumentPostingPeriod The financialDocumentPostingPeriod to set.
     */
    public void setFinancialDocumentPostingPeriod(AccountingPeriod financialDocumentPostingPeriod) {
        this.financialDocumentPostingPeriod = financialDocumentPostingPeriod;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
    
    /**
     * Returns the String representation for an Electronic Payment Claim record, to be used by the claimed
     * checkbox
     * @param claim a claim to get a String representation for
     * @return the representation in the form of "{generating document number}::{generating document accounting line sequence number}"
     */
    public String getElectronicPaymentClaimRepresentation() {
        StringBuilder representation = new StringBuilder();
        representation.append(getDocumentNumber());
        representation.append("::");
        representation.append(getFinancialDocumentLineNumber());
        return representation.toString();
    }
}
