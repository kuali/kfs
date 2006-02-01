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

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DepositCashReceiptControl extends BusinessObjectBase {

	private String financialDocumentDepositNumber;
	private String financialDocumentCashReceiptNumber;
	private Timestamp financialSystemsCashReceiptProcessingTimestamp;
	private String financialSystemsProcessingOperatorIdentifier;
	private String financialDocumentDepositTypeCode;
	private DepositHeader depositHeader;
	private CashReceiptHeader cashReceiptHeader;

	/**
	 * Default constructor.
	 */
	public DepositCashReceiptControl() {

	}

	/**
	 * Gets the financialDocumentDepositNumber attribute.
	 * 
	 * @return - Returns the financialDocumentDepositNumber
	 * 
	 */
	public String getFinancialDocumentDepositNumber() { 
		return financialDocumentDepositNumber;
	}

	/**
	 * Sets the financialDocumentDepositNumber attribute.
	 * 
	 * @param - financialDocumentDepositNumber The financialDocumentDepositNumber to set.
	 * 
	 */
	public void setFinancialDocumentDepositNumber(String financialDocumentDepositNumber) {
		this.financialDocumentDepositNumber = financialDocumentDepositNumber;
	}


	/**
	 * Gets the financialDocumentCashReceiptNumber attribute.
	 * 
	 * @return - Returns the financialDocumentCashReceiptNumber
	 * 
	 */
	public String getFinancialDocumentCashReceiptNumber() { 
		return financialDocumentCashReceiptNumber;
	}

	/**
	 * Sets the financialDocumentCashReceiptNumber attribute.
	 * 
	 * @param - financialDocumentCashReceiptNumber The financialDocumentCashReceiptNumber to set.
	 * 
	 */
	public void setFinancialDocumentCashReceiptNumber(String financialDocumentCashReceiptNumber) {
		this.financialDocumentCashReceiptNumber = financialDocumentCashReceiptNumber;
	}


	/**
	 * Gets the financialSystemsCashReceiptProcessingTimestamp attribute.
	 * 
	 * @return - Returns the financialSystemsCashReceiptProcessingTimestamp
	 * 
	 */
	public Timestamp getFinancialSystemsCashReceiptProcessingTimestamp() { 
		return financialSystemsCashReceiptProcessingTimestamp;
	}

	/**
	 * Sets the financialSystemsCashReceiptProcessingTimestamp attribute.
	 * 
	 * @param - financialSystemsCashReceiptProcessingTimestamp The financialSystemsCashReceiptProcessingTimestamp to set.
	 * 
	 */
	public void setFinancialSystemsCashReceiptProcessingTimestamp(Timestamp financialSystemsCashReceiptProcessingTimestamp) {
		this.financialSystemsCashReceiptProcessingTimestamp = financialSystemsCashReceiptProcessingTimestamp;
	}


	/**
	 * Gets the financialSystemsProcessingOperatorIdentifier attribute.
	 * 
	 * @return - Returns the financialSystemsProcessingOperatorIdentifier
	 * 
	 */
	public String getFinancialSystemsProcessingOperatorIdentifier() { 
		return financialSystemsProcessingOperatorIdentifier;
	}

	/**
	 * Sets the financialSystemsProcessingOperatorIdentifier attribute.
	 * 
	 * @param - financialSystemsProcessingOperatorIdentifier The financialSystemsProcessingOperatorIdentifier to set.
	 * 
	 */
	public void setFinancialSystemsProcessingOperatorIdentifier(String financialSystemsProcessingOperatorIdentifier) {
		this.financialSystemsProcessingOperatorIdentifier = financialSystemsProcessingOperatorIdentifier;
	}


	/**
	 * Gets the financialDocumentDepositTypeCode attribute.
	 * 
	 * @return - Returns the financialDocumentDepositTypeCode
	 * 
	 */
	public String getFinancialDocumentDepositTypeCode() { 
		return financialDocumentDepositTypeCode;
	}

	/**
	 * Sets the financialDocumentDepositTypeCode attribute.
	 * 
	 * @param - financialDocumentDepositTypeCode The financialDocumentDepositTypeCode to set.
	 * 
	 */
	public void setFinancialDocumentDepositTypeCode(String financialDocumentDepositTypeCode) {
		this.financialDocumentDepositTypeCode = financialDocumentDepositTypeCode;
	}


	/**
	 * Gets the depositHeader attribute.
	 * 
	 * @return - Returns the depositHeader
	 * 
	 */
	public DepositHeader getDepositHeader() { 
		return depositHeader;
	}

	/**
	 * Sets the depositHeader attribute.
	 * 
	 * @param - depositHeader The depositHeader to set.
	 * @deprecated
	 */
	public void setDepositHeader(DepositHeader depositHeader) {
		this.depositHeader = depositHeader;
	}

	/**
	 * Gets the cashReceiptHeader attribute.
	 * 
	 * @return - Returns the cashReceiptHeader
	 * 
	 */
	public CashReceiptHeader getCashReceiptHeader() { 
		return cashReceiptHeader;
	}

	/**
	 * Sets the cashReceiptHeader attribute.
	 * 
	 * @param - cashReceiptHeader The cashReceiptHeader to set.
	 * @deprecated
	 */
	public void setCashReceiptHeader(CashReceiptHeader cashReceiptHeader) {
		this.cashReceiptHeader = cashReceiptHeader;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("financialDocumentDepositNumber", this.financialDocumentDepositNumber);
        m.put("financialDocumentCashReceiptNumber", this.financialDocumentCashReceiptNumber);
	    return m;
    }
}
