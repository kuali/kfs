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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.document.DocumentHeader;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashReceiptHeader extends BusinessObjectBase {

	private String financialDocumentNumber;
	private String financialDocumentExplanationText;
	private Integer nextCheckLineNumber;
	private Integer nextCreditCardCashReceiptLineNumber;
	private Integer nextCreditCardCashieringDocumentLineNumber;
	private Integer nextRevolvingFundLineNumber;
	private Integer nextAdvanceDepositLineNumber;
	private String workgroupName;
	private Date financialDocumentDepositDate;
	private DocumentHeader documentHeader;
	private List depositCashReceiptControl;

	/**
	 * Default constructor.
	 */
	public CashReceiptHeader() {
        depositCashReceiptControl = new ArrayList();        

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
	 * Gets the financialDocumentExplanationText attribute.
	 * 
	 * @return - Returns the financialDocumentExplanationText
	 * 
	 */
	public String getFinancialDocumentExplanationText() { 
		return financialDocumentExplanationText;
	}

	/**
	 * Sets the financialDocumentExplanationText attribute.
	 * 
	 * @param - financialDocumentExplanationText The financialDocumentExplanationText to set.
	 * 
	 */
	public void setFinancialDocumentExplanationText(String financialDocumentExplanationText) {
		this.financialDocumentExplanationText = financialDocumentExplanationText;
	}


	/**
	 * Gets the nextCheckLineNumber attribute.
	 * 
	 * @return - Returns the nextCheckLineNumber
	 * 
	 */
	public Integer getNextCheckLineNumber() { 
		return nextCheckLineNumber;
	}

	/**
	 * Sets the nextCheckLineNumber attribute.
	 * 
	 * @param - nextCheckLineNumber The nextCheckLineNumber to set.
	 * 
	 */
	public void setNextCheckLineNumber(Integer nextCheckLineNumber) {
		this.nextCheckLineNumber = nextCheckLineNumber;
	}


	/**
	 * Gets the nextCreditCardCashReceiptLineNumber attribute.
	 * 
	 * @return - Returns the nextCreditCardCashReceiptLineNumber
	 * 
	 */
	public Integer getNextCreditCardCashReceiptLineNumber() { 
		return nextCreditCardCashReceiptLineNumber;
	}

	/**
	 * Sets the nextCreditCardCashReceiptLineNumber attribute.
	 * 
	 * @param - nextCreditCardCashReceiptLineNumber The nextCreditCardCashReceiptLineNumber to set.
	 * 
	 */
	public void setNextCreditCardCashReceiptLineNumber(Integer nextCreditCardCashReceiptLineNumber) {
		this.nextCreditCardCashReceiptLineNumber = nextCreditCardCashReceiptLineNumber;
	}


	/**
	 * Gets the nextCreditCardCashieringDocumentLineNumber attribute.
	 * 
	 * @return - Returns the nextCreditCardCashieringDocumentLineNumber
	 * 
	 */
	public Integer getNextCreditCardCashieringDocumentLineNumber() { 
		return nextCreditCardCashieringDocumentLineNumber;
	}

	/**
	 * Sets the nextCreditCardCashieringDocumentLineNumber attribute.
	 * 
	 * @param - nextCreditCardCashieringDocumentLineNumber The nextCreditCardCashieringDocumentLineNumber to set.
	 * 
	 */
	public void setNextCreditCardCashieringDocumentLineNumber(Integer nextCreditCardCashieringDocumentLineNumber) {
		this.nextCreditCardCashieringDocumentLineNumber = nextCreditCardCashieringDocumentLineNumber;
	}


	/**
	 * Gets the nextRevolvingFundLineNumber attribute.
	 * 
	 * @return - Returns the nextRevolvingFundLineNumber
	 * 
	 */
	public Integer getNextRevolvingFundLineNumber() { 
		return nextRevolvingFundLineNumber;
	}

	/**
	 * Sets the nextRevolvingFundLineNumber attribute.
	 * 
	 * @param - nextRevolvingFundLineNumber The nextRevolvingFundLineNumber to set.
	 * 
	 */
	public void setNextRevolvingFundLineNumber(Integer nextRevolvingFundLineNumber) {
		this.nextRevolvingFundLineNumber = nextRevolvingFundLineNumber;
	}


	/**
	 * Gets the nextAdvanceDepositLineNumber attribute.
	 * 
	 * @return - Returns the nextAdvanceDepositLineNumber
	 * 
	 */
	public Integer getNextAdvanceDepositLineNumber() { 
		return nextAdvanceDepositLineNumber;
	}

	/**
	 * Sets the nextAdvanceDepositLineNumber attribute.
	 * 
	 * @param - nextAdvanceDepositLineNumber The nextAdvanceDepositLineNumber to set.
	 * 
	 */
	public void setNextAdvanceDepositLineNumber(Integer nextAdvanceDepositLineNumber) {
		this.nextAdvanceDepositLineNumber = nextAdvanceDepositLineNumber;
	}


	/**
	 * Gets the workgroupName attribute.
	 * 
	 * @return - Returns the workgroupName
	 * 
	 */
	public String getWorkgroupName() { 
		return workgroupName;
	}

	/**
	 * Sets the workgroupName attribute.
	 * 
	 * @param - workgroupName The workgroupName to set.
	 * 
	 */
	public void setWorkgroupName(String workgroupName) {
		this.workgroupName = workgroupName;
	}


	/**
	 * Gets the financialDocumentDepositDate attribute.
	 * 
	 * @return - Returns the financialDocumentDepositDate
	 * 
	 */
	public Date getFinancialDocumentDepositDate() { 
		return financialDocumentDepositDate;
	}

	/**
	 * Sets the financialDocumentDepositDate attribute.
	 * 
	 * @param - financialDocumentDepositDate The financialDocumentDepositDate to set.
	 * 
	 */
	public void setFinancialDocumentDepositDate(Date financialDocumentDepositDate) {
		this.financialDocumentDepositDate = financialDocumentDepositDate;
	}


	/**
	 * Gets the documentHeader attribute.
	 * 
	 * @return - Returns the documentHeader
	 * 
	 */
	public DocumentHeader getDocumentHeader() { 
		return documentHeader;
	}

	/**
	 * Sets the documentHeader attribute.
	 * 
	 * @param - documentHeader The documentHeader to set.
	 * @deprecated
	 */
	public void setDocumentHeader(DocumentHeader documentHeader) {
		this.documentHeader = documentHeader;
	}

	/**
	 * Gets the depositCashReceiptControl list.
	 * 
	 * @return - Returns the depositCashReceiptControl list
	 * 
	 */
	public List getDepositCashReceiptControl() { 
		return depositCashReceiptControl;
	}

	/**
	 * Sets the depositCashReceiptControl list.
	 * 
	 * @param - depositCashReceiptControl The depositCashReceiptControl list to set.
	 * 
	 */
	public void setDepositCashReceiptControl(List depositCashReceiptControl) {
		this.depositCashReceiptControl = depositCashReceiptControl;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("financialDocumentNumber", this.financialDocumentNumber);
	    return m;
    }
}
