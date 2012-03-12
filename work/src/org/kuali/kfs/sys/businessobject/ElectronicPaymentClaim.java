/*
 * Copyright 2006-2008 The Kuali Foundation
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

package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.fp.businessobject.AdvanceDepositDetail;
import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

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
    private SourceAccountingLine generatingAccountingLine;
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
    public AdvanceDepositDocument getGeneratingDocument() {
        final boolean docNumbersAreDifferentAndNotNull = (generatingDocumentHeader != null && !documentNumber.equals(this.generatingDocumentHeader.getDocumentNumber()));
        if (StringUtils.isNotBlank(documentNumber) && (this.generatingDocument == null || docNumbersAreDifferentAndNotNull)) {
            try {
                generatingDocument = (AdvanceDepositDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
            }
            catch (WorkflowException we) {
                throw new RuntimeException("Could not retrieve Document #"+documentNumber, we);
            }
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
    public SourceAccountingLine getGeneratingAccountingLine() {
        if (generatingAccountingLine == null) {
            final AdvanceDepositDocument generatingDocument = getGeneratingDocument();
            if (generatingDocument != null && generatingDocument.getSourceAccountingLines() != null) {
                int count = 0;
                
                while (generatingAccountingLine == null && count < generatingDocument.getSourceAccountingLines().size()) {
                    if (generatingDocument.getSourceAccountingLine(count).getSequenceNumber().equals(getFinancialDocumentLineNumber())) {
                        generatingAccountingLine = generatingDocument.getSourceAccountingLine(count);
                    }
                    count += 1;
                }
                
            }
        }
        return generatingAccountingLine;
    }
    
    /**
     * Returns the AdvanceDepositDetail for the first deposit detail on this document
     * @return the advance deposit detail that describes the transaction responsible for the creation of this record
     */
    public AdvanceDepositDetail getGeneratingAdvanceDepositDetail() {
        final AdvanceDepositDocument generatingDocument = getGeneratingDocument();
        if (generatingDocument != null && !ObjectUtils.isNull(generatingDocument.getAdvanceDeposits()) && !generatingDocument.getAdvanceDeposits().isEmpty()) {
            return generatingDocument.getAdvanceDepositDetail(0);
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
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
    
    /**
     * @return a descriptive version of the paymentClaimStatusCode field
     */
    public String getPaymentClaimStatus() {
        return getPaymentClaimStatusCode().equals("C") ? "Claimed" : "Unclaimed";
    }
}
