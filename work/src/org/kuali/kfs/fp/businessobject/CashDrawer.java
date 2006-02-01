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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CashDrawer extends BusinessObjectBase {

	private String workgroupName;
	private String financialDocumentOpenDepositCode;
	private KualiDecimal cashDrawerTotalAmount;
	private KualiDecimal financialDocumentHundredDollarAmount;
	private KualiDecimal financialDocumentFiftyDollarAmount;
	private KualiDecimal financialDocumentTwentyDollarAmount;
	private KualiDecimal financialDocumentTenDollarAmount;
	private KualiDecimal financialDocumentFiveDollarAmount;
	private KualiDecimal financialDocumentTwoDollarAmount;
	private KualiDecimal financialDocumentOneDollarAmount;
	private KualiDecimal financialDocumentOtherDollarAmount;
	private KualiDecimal financialDocumentFiftyCentAmount;
	private KualiDecimal financialDocumentTwentyfiveCentAmount;
	private KualiDecimal financialDocumentTenCentAmount;
	private KualiDecimal financialDocumentFiveCentAmount;
	private KualiDecimal financialDocumentOneCentAmount;
	private KualiDecimal financialDocumentOtherCentAmount;
	private KualiDecimal financialDocumentHundredCentAmount;
	private KualiDecimal financialDocumentMiscellaneousAdvanceAmount;
	private String financialDocumentReferenceNumber;

	/**
	 * Default constructor.
	 */
	public CashDrawer() {

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
	 * Gets the financialDocumentOpenDepositCode attribute.
	 * 
	 * @return - Returns the financialDocumentOpenDepositCode
	 * 
	 */
	public String getFinancialDocumentOpenDepositCode() { 
		return financialDocumentOpenDepositCode;
	}

	/**
	 * Sets the financialDocumentOpenDepositCode attribute.
	 * 
	 * @param - financialDocumentOpenDepositCode The financialDocumentOpenDepositCode to set.
	 * 
	 */
	public void setFinancialDocumentOpenDepositCode(String financialDocumentOpenDepositCode) {
		this.financialDocumentOpenDepositCode = financialDocumentOpenDepositCode;
	}


	/**
	 * Gets the cashDrawerTotalAmount attribute.
	 * 
	 * @return - Returns the cashDrawerTotalAmount
	 * 
	 */
	public KualiDecimal getCashDrawerTotalAmount() { 
		return cashDrawerTotalAmount;
	}

	/**
	 * Sets the cashDrawerTotalAmount attribute.
	 * 
	 * @param - cashDrawerTotalAmount The cashDrawerTotalAmount to set.
	 * 
	 */
	public void setCashDrawerTotalAmount(KualiDecimal cashDrawerTotalAmount) {
		this.cashDrawerTotalAmount = cashDrawerTotalAmount;
	}


	/**
	 * Gets the financialDocumentHundredDollarAmount attribute.
	 * 
	 * @return - Returns the financialDocumentHundredDollarAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentHundredDollarAmount() { 
		return financialDocumentHundredDollarAmount;
	}

	/**
	 * Sets the financialDocumentHundredDollarAmount attribute.
	 * 
	 * @param - financialDocumentHundredDollarAmount The financialDocumentHundredDollarAmount to set.
	 * 
	 */
	public void setFinancialDocumentHundredDollarAmount(KualiDecimal financialDocumentHundredDollarAmount) {
		this.financialDocumentHundredDollarAmount = financialDocumentHundredDollarAmount;
	}


	/**
	 * Gets the financialDocumentFiftyDollarAmount attribute.
	 * 
	 * @return - Returns the financialDocumentFiftyDollarAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentFiftyDollarAmount() { 
		return financialDocumentFiftyDollarAmount;
	}

	/**
	 * Sets the financialDocumentFiftyDollarAmount attribute.
	 * 
	 * @param - financialDocumentFiftyDollarAmount The financialDocumentFiftyDollarAmount to set.
	 * 
	 */
	public void setFinancialDocumentFiftyDollarAmount(KualiDecimal financialDocumentFiftyDollarAmount) {
		this.financialDocumentFiftyDollarAmount = financialDocumentFiftyDollarAmount;
	}


	/**
	 * Gets the financialDocumentTwentyDollarAmount attribute.
	 * 
	 * @return - Returns the financialDocumentTwentyDollarAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentTwentyDollarAmount() { 
		return financialDocumentTwentyDollarAmount;
	}

	/**
	 * Sets the financialDocumentTwentyDollarAmount attribute.
	 * 
	 * @param - financialDocumentTwentyDollarAmount The financialDocumentTwentyDollarAmount to set.
	 * 
	 */
	public void setFinancialDocumentTwentyDollarAmount(KualiDecimal financialDocumentTwentyDollarAmount) {
		this.financialDocumentTwentyDollarAmount = financialDocumentTwentyDollarAmount;
	}


	/**
	 * Gets the financialDocumentTenDollarAmount attribute.
	 * 
	 * @return - Returns the financialDocumentTenDollarAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentTenDollarAmount() { 
		return financialDocumentTenDollarAmount;
	}

	/**
	 * Sets the financialDocumentTenDollarAmount attribute.
	 * 
	 * @param - financialDocumentTenDollarAmount The financialDocumentTenDollarAmount to set.
	 * 
	 */
	public void setFinancialDocumentTenDollarAmount(KualiDecimal financialDocumentTenDollarAmount) {
		this.financialDocumentTenDollarAmount = financialDocumentTenDollarAmount;
	}


	/**
	 * Gets the financialDocumentFiveDollarAmount attribute.
	 * 
	 * @return - Returns the financialDocumentFiveDollarAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentFiveDollarAmount() { 
		return financialDocumentFiveDollarAmount;
	}

	/**
	 * Sets the financialDocumentFiveDollarAmount attribute.
	 * 
	 * @param - financialDocumentFiveDollarAmount The financialDocumentFiveDollarAmount to set.
	 * 
	 */
	public void setFinancialDocumentFiveDollarAmount(KualiDecimal financialDocumentFiveDollarAmount) {
		this.financialDocumentFiveDollarAmount = financialDocumentFiveDollarAmount;
	}


	/**
	 * Gets the financialDocumentTwoDollarAmount attribute.
	 * 
	 * @return - Returns the financialDocumentTwoDollarAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentTwoDollarAmount() { 
		return financialDocumentTwoDollarAmount;
	}

	/**
	 * Sets the financialDocumentTwoDollarAmount attribute.
	 * 
	 * @param - financialDocumentTwoDollarAmount The financialDocumentTwoDollarAmount to set.
	 * 
	 */
	public void setFinancialDocumentTwoDollarAmount(KualiDecimal financialDocumentTwoDollarAmount) {
		this.financialDocumentTwoDollarAmount = financialDocumentTwoDollarAmount;
	}


	/**
	 * Gets the financialDocumentOneDollarAmount attribute.
	 * 
	 * @return - Returns the financialDocumentOneDollarAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentOneDollarAmount() { 
		return financialDocumentOneDollarAmount;
	}

	/**
	 * Sets the financialDocumentOneDollarAmount attribute.
	 * 
	 * @param - financialDocumentOneDollarAmount The financialDocumentOneDollarAmount to set.
	 * 
	 */
	public void setFinancialDocumentOneDollarAmount(KualiDecimal financialDocumentOneDollarAmount) {
		this.financialDocumentOneDollarAmount = financialDocumentOneDollarAmount;
	}


	/**
	 * Gets the financialDocumentOtherDollarAmount attribute.
	 * 
	 * @return - Returns the financialDocumentOtherDollarAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentOtherDollarAmount() { 
		return financialDocumentOtherDollarAmount;
	}

	/**
	 * Sets the financialDocumentOtherDollarAmount attribute.
	 * 
	 * @param - financialDocumentOtherDollarAmount The financialDocumentOtherDollarAmount to set.
	 * 
	 */
	public void setFinancialDocumentOtherDollarAmount(KualiDecimal financialDocumentOtherDollarAmount) {
		this.financialDocumentOtherDollarAmount = financialDocumentOtherDollarAmount;
	}


	/**
	 * Gets the financialDocumentFiftyCentAmount attribute.
	 * 
	 * @return - Returns the financialDocumentFiftyCentAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentFiftyCentAmount() { 
		return financialDocumentFiftyCentAmount;
	}

	/**
	 * Sets the financialDocumentFiftyCentAmount attribute.
	 * 
	 * @param - financialDocumentFiftyCentAmount The financialDocumentFiftyCentAmount to set.
	 * 
	 */
	public void setFinancialDocumentFiftyCentAmount(KualiDecimal financialDocumentFiftyCentAmount) {
		this.financialDocumentFiftyCentAmount = financialDocumentFiftyCentAmount;
	}


	/**
	 * Gets the financialDocumentTwentyfiveCentAmount attribute.
	 * 
	 * @return - Returns the financialDocumentTwentyfiveCentAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentTwentyfiveCentAmount() { 
		return financialDocumentTwentyfiveCentAmount;
	}

	/**
	 * Sets the financialDocumentTwentyfiveCentAmount attribute.
	 * 
	 * @param - financialDocumentTwentyfiveCentAmount The financialDocumentTwentyfiveCentAmount to set.
	 * 
	 */
	public void setFinancialDocumentTwentyfiveCentAmount(KualiDecimal financialDocumentTwentyfiveCentAmount) {
		this.financialDocumentTwentyfiveCentAmount = financialDocumentTwentyfiveCentAmount;
	}


	/**
	 * Gets the financialDocumentTenCentAmount attribute.
	 * 
	 * @return - Returns the financialDocumentTenCentAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentTenCentAmount() { 
		return financialDocumentTenCentAmount;
	}

	/**
	 * Sets the financialDocumentTenCentAmount attribute.
	 * 
	 * @param - financialDocumentTenCentAmount The financialDocumentTenCentAmount to set.
	 * 
	 */
	public void setFinancialDocumentTenCentAmount(KualiDecimal financialDocumentTenCentAmount) {
		this.financialDocumentTenCentAmount = financialDocumentTenCentAmount;
	}


	/**
	 * Gets the financialDocumentFiveCentAmount attribute.
	 * 
	 * @return - Returns the financialDocumentFiveCentAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentFiveCentAmount() { 
		return financialDocumentFiveCentAmount;
	}

	/**
	 * Sets the financialDocumentFiveCentAmount attribute.
	 * 
	 * @param - financialDocumentFiveCentAmount The financialDocumentFiveCentAmount to set.
	 * 
	 */
	public void setFinancialDocumentFiveCentAmount(KualiDecimal financialDocumentFiveCentAmount) {
		this.financialDocumentFiveCentAmount = financialDocumentFiveCentAmount;
	}


	/**
	 * Gets the financialDocumentOneCentAmount attribute.
	 * 
	 * @return - Returns the financialDocumentOneCentAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentOneCentAmount() { 
		return financialDocumentOneCentAmount;
	}

	/**
	 * Sets the financialDocumentOneCentAmount attribute.
	 * 
	 * @param - financialDocumentOneCentAmount The financialDocumentOneCentAmount to set.
	 * 
	 */
	public void setFinancialDocumentOneCentAmount(KualiDecimal financialDocumentOneCentAmount) {
		this.financialDocumentOneCentAmount = financialDocumentOneCentAmount;
	}


	/**
	 * Gets the financialDocumentOtherCentAmount attribute.
	 * 
	 * @return - Returns the financialDocumentOtherCentAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentOtherCentAmount() { 
		return financialDocumentOtherCentAmount;
	}

	/**
	 * Sets the financialDocumentOtherCentAmount attribute.
	 * 
	 * @param - financialDocumentOtherCentAmount The financialDocumentOtherCentAmount to set.
	 * 
	 */
	public void setFinancialDocumentOtherCentAmount(KualiDecimal financialDocumentOtherCentAmount) {
		this.financialDocumentOtherCentAmount = financialDocumentOtherCentAmount;
	}


	/**
	 * Gets the financialDocumentHundredCentAmount attribute.
	 * 
	 * @return - Returns the financialDocumentHundredCentAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentHundredCentAmount() { 
		return financialDocumentHundredCentAmount;
	}

	/**
	 * Sets the financialDocumentHundredCentAmount attribute.
	 * 
	 * @param - financialDocumentHundredCentAmount The financialDocumentHundredCentAmount to set.
	 * 
	 */
	public void setFinancialDocumentHundredCentAmount(KualiDecimal financialDocumentHundredCentAmount) {
		this.financialDocumentHundredCentAmount = financialDocumentHundredCentAmount;
	}


	/**
	 * Gets the financialDocumentMiscellaneousAdvanceAmount attribute.
	 * 
	 * @return - Returns the financialDocumentMiscellaneousAdvanceAmount
	 * 
	 */
	public KualiDecimal getFinancialDocumentMiscellaneousAdvanceAmount() { 
		return financialDocumentMiscellaneousAdvanceAmount;
	}

	/**
	 * Sets the financialDocumentMiscellaneousAdvanceAmount attribute.
	 * 
	 * @param - financialDocumentMiscellaneousAdvanceAmount The financialDocumentMiscellaneousAdvanceAmount to set.
	 * 
	 */
	public void setFinancialDocumentMiscellaneousAdvanceAmount(KualiDecimal financialDocumentMiscellaneousAdvanceAmount) {
		this.financialDocumentMiscellaneousAdvanceAmount = financialDocumentMiscellaneousAdvanceAmount;
	}


	/**
	 * Gets the financialDocumentReferenceNumber attribute.
	 * 
	 * @return - Returns the financialDocumentReferenceNumber
	 * 
	 */
	public String getFinancialDocumentReferenceNumber() { 
		return financialDocumentReferenceNumber;
	}

	/**
	 * Sets the financialDocumentReferenceNumber attribute.
	 * 
	 * @param - financialDocumentReferenceNumber The financialDocumentReferenceNumber to set.
	 * 
	 */
	public void setFinancialDocumentReferenceNumber(String financialDocumentReferenceNumber) {
		this.financialDocumentReferenceNumber = financialDocumentReferenceNumber;
	}


	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();
        m.put("workgroupName", this.workgroupName);
	    return m;
    }
}
