/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.document;

import java.sql.Timestamp;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;


/**
 * This is the business object that represents the AuxiliaryVoucherDocument in Kuali. This 
 * is a transactional document that will eventually post transactions to the G/L.  It 
 * integrates with workflow and also contains two groupings of accounting lines: 
 * Expense and target.  Expense is the expense and target is the income lines. * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class AuxiliaryVoucherDocument extends TransactionalDocumentBase implements VoucherDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AuxiliaryVoucherDocument.class);
	
    private String typeCode = "AVAD";
    private Timestamp reversalDate;
	
	/**
	 * Initializes the array lists and some basic info.
	 */
	public AuxiliaryVoucherDocument() {
		super();
	}
	    
    public Timestamp getReversalDate() {
        return reversalDate;
    }
    
    public void setReversalDate(Timestamp reversalDate) {
        this.reversalDate = reversalDate;
    }
    
    public String getTypeCode() {
        return typeCode;
    }
    
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
    
    //	workflow related methods
    /**
     * This method calculates the debit total for a JV document keying off of the 
     * debit/debit code, only summing the accounting lines with a debitDebitCode 
     * that matched the debit constant, and returns the results.
	 * 
     * @return KualiDecimal
     */
    public KualiDecimal getDebitTotal() {
        KualiDecimal debitTotal = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            if(StringUtils.isNotBlank(al.getDebitCreditCode()) && al.getDebitCreditCode().equals(Constants.GL_DEBIT_CODE)) {
                debitTotal = debitTotal.add(al.getAmount());
            }
        }
        return debitTotal;
    }
    
    /**
     * This method calculates the credit total for a JV document keying off of the 
     * debit/credit code, only summing the accounting lines with a debitCreditCode 
     * that matched the debit constant, and returns the results.
	 *
     * @return KualiDecimal
     */
    public KualiDecimal getCreditTotal() {
        KualiDecimal creditTotal = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            if(StringUtils.isNotBlank(al.getDebitCreditCode()) && al.getDebitCreditCode().equals(Constants.GL_CREDIT_CODE)) {
                creditTotal = creditTotal.add(al.getAmount());
            }
        }
        return creditTotal;
    }
    
    /**
     * This method sums the amounts of all of the accounting lines in 
     * the <code>{@link AuxiliaryVoucherDocument}</code>.  This method should be used to represent 
	 * the total of the document when the balance type of External Encubmrance 
     * is selected.
	 *
     * @return KualiDecimal
     */
    public KualiDecimal getTotal() {        
        KualiDecimal total = new KualiDecimal(0);
        
		total = getCreditTotal().subtract(getDebitTotal());

        return total;
    }
}
