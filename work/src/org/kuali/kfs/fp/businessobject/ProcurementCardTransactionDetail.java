/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.bo;

import java.sql.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ProcurementCardTransactionDetail extends BusinessObjectBase {

    private String financialDocumentNumber;
    private Integer financialDocumentTransactionLineNumber;
    private Date transactionDate;
    private String transactionReferenceNumber;
    private String transactionVendorName;
    private String transactionMerchantCategoryCode;

    private List sourceAccountingLines;
    private List targetAccountingLines;

    /**
     * Default constructor.
     */
    public ProcurementCardTransactionDetail() {
        sourceAccountingLines = new TypedArrayList(ProcurementCardSourceAccountingLine.class);
        targetAccountingLines = new TypedArrayList(ProcurementCardTargetAccountingLine.class);
    }
    
    /**
     * @see org.kuali.core.document.TransactionalDocument#getTargetTotal()
     */
    public KualiDecimal getTargetTotal() {
        KualiDecimal total = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = getTargetAccountingLines().iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();

            KualiDecimal amount = al.getAmount();
            if (amount != null) {
                total = total.add(amount);
            }
        }
        return total;
    }

    /**
     * Gets the financialDocumentNumber attribute.
     * 
     * @return - Returns the financialDocumentNumber
     *  
     */
    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    /**
     * Sets the financialDocumentNumber attribute.
     * 
     * @param - financialDocumentNumber The financialDocumentNumber to set.
     *  
     */
    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
    }


    /**
     * Gets the financialDocumentTransactionLineNumber attribute.
     * 
     * @return - Returns the financialDocumentTransactionLineNumber
     *  
     */
    public Integer getFinancialDocumentTransactionLineNumber() {
        return financialDocumentTransactionLineNumber;
    }

    /**
     * Sets the financialDocumentTransactionLineNumber attribute.
     * 
     * @param - financialDocumentTransactionLineNumber The financialDocumentTransactionLineNumber to set.
     *  
     */
    public void setFinancialDocumentTransactionLineNumber(Integer financialDocumentTransactionLineNumber) {
        this.financialDocumentTransactionLineNumber = financialDocumentTransactionLineNumber;
    }


    /**
     * Gets the transactionDate attribute.
     * 
     * @return - Returns the transactionDate
     *  
     */
    public Date getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the transactionDate attribute.
     * 
     * @param - transactionDate The transactionDate to set.
     *  
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }


    /**
     * Gets the transactionReferenceNumber attribute.
     * 
     * @return - Returns the transactionReferenceNumber
     *  
     */
    public String getTransactionReferenceNumber() {
        return transactionReferenceNumber;
    }

    /**
     * Sets the transactionReferenceNumber attribute.
     * 
     * @param - transactionReferenceNumber The transactionReferenceNumber to set.
     *  
     */
    public void setTransactionReferenceNumber(String transactionReferenceNumber) {
        this.transactionReferenceNumber = transactionReferenceNumber;
    }


    /**
     * Gets the transactionVendorName attribute.
     * 
     * @return - Returns the transactionVendorName
     *  
     */
    public String getTransactionVendorName() {
        return transactionVendorName;
    }

    /**
     * Sets the transactionVendorName attribute.
     * 
     * @param - transactionVendorName The transactionVendorName to set.
     *  
     */
    public void setTransactionVendorName(String transactionVendorName) {
        this.transactionVendorName = transactionVendorName;
    }


    /**
     * Gets the transactionMerchantCategoryCode attribute.
     * 
     * @return - Returns the transactionMerchantCategoryCode
     *  
     */
    public String getTransactionMerchantCategoryCode() {
        return transactionMerchantCategoryCode;
    }

    /**
     * Sets the transactionMerchantCategoryCode attribute.
     * 
     * @param - transactionMerchantCategoryCode The transactionMerchantCategoryCode to set.
     *  
     */
    public void setTransactionMerchantCategoryCode(String transactionMerchantCategoryCode) {
        this.transactionMerchantCategoryCode = transactionMerchantCategoryCode;
    }


    /**
     * @return Returns the sourceAccountingLines.
     */
    public List getSourceAccountingLines() {
        return sourceAccountingLines;
    }

    /**
     * @param sourceAccountingLines The sourceAccountingLines to set.
     */
    public void setSourceAccountingLines(List sourceAccountingLines) {
        this.sourceAccountingLines = sourceAccountingLines;
    }

    /**
     * @return Returns the targetAccountingLines.
     */
    public List getTargetAccountingLines() {
        return targetAccountingLines;
    }

    /**
     * @param targetAccountingLines The targetAccountingLines to set.
     */
    public void setTargetAccountingLines(List targetAccountingLines) {
        this.targetAccountingLines = targetAccountingLines;
    }
    
    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        if (this.financialDocumentTransactionLineNumber != null) {
            m.put("financialDocumentTransactionLineNumber", this.financialDocumentTransactionLineNumber.toString());
        }
        return m;
    }
}