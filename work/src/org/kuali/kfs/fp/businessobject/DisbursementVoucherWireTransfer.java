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
import org.kuali.module.chart.bo.Account;
import org.kuali.module.financial.document.DisbursementVoucherDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherWireTransfer extends BusinessObjectBase {

	private String financialDocumentNumber;
	private String disbursementVoucherBankName;
	private String disbVchrBankRoutingNumber;
	private String disbVchrBankCityName;
	private String disbVchrBankStateCode;
	private String disbVchrBankCountryName;
	private String disbVchrAttentionLineText;
	private String disbVchrAdditionalWireText;
	private String disbVchrPayeeAccountNumber;
	private String disbVchrCurrencyTypeName;
	private String disbVchrCurrencyTypeCode;
	private boolean disbVchrForeignBankIndicator;
	private boolean disbursementVoucherWireTransferFeeWaiverIndicator;
	private String disbursementVoucherPayeeAccountName;
	private String disbursementVoucherPayeeAccountTypeCode;
	private String disbursementVoucherAutomatedClearingHouseProfileNumber;
	
	private DisbursementVoucherDocument financialDocument;
	private Account disbVchrPayeeAccount;

	/**
	 * Default no-arg constructor.
	 */
	public DisbursementVoucherWireTransfer() {
	    disbVchrForeignBankIndicator = false;
	    disbursementVoucherWireTransferFeeWaiverIndicator = false;
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
	 * Gets the disbursementVoucherBankName attribute.
	 * 
	 * @return - Returns the disbursementVoucherBankName
	 * 
	 */
	public String getDisbursementVoucherBankName() { 
		return disbursementVoucherBankName;
	}
	

	/**
	 * Sets the disbursementVoucherBankName attribute.
	 * 
	 * @param - disbursementVoucherBankName The disbursementVoucherBankName to set.
	 * 
	 */
	public void setDisbursementVoucherBankName(String disbursementVoucherBankName) {
		this.disbursementVoucherBankName = disbursementVoucherBankName;
	}

	/**
	 * Gets the disbVchrBankRoutingNumber attribute.
	 * 
	 * @return - Returns the disbVchrBankRoutingNumber
	 * 
	 */
	public String getDisbVchrBankRoutingNumber() { 
		return disbVchrBankRoutingNumber;
	}
	

	/**
	 * Sets the disbVchrBankRoutingNumber attribute.
	 * 
	 * @param - disbVchrBankRoutingNumber The disbVchrBankRoutingNumber to set.
	 * 
	 */
	public void setDisbVchrBankRoutingNumber(String disbVchrBankRoutingNumber) {
		this.disbVchrBankRoutingNumber = disbVchrBankRoutingNumber;
	}

	/**
	 * Gets the disbVchrBankCityName attribute.
	 * 
	 * @return - Returns the disbVchrBankCityName
	 * 
	 */
	public String getDisbVchrBankCityName() { 
		return disbVchrBankCityName;
	}
	

	/**
	 * Sets the disbVchrBankCityName attribute.
	 * 
	 * @param - disbVchrBankCityName The disbVchrBankCityName to set.
	 * 
	 */
	public void setDisbVchrBankCityName(String disbVchrBankCityName) {
		this.disbVchrBankCityName = disbVchrBankCityName;
	}

	/**
	 * Gets the disbVchrBankStateCode attribute.
	 * 
	 * @return - Returns the disbVchrBankStateCode
	 * 
	 */
	public String getDisbVchrBankStateCode() { 
		return disbVchrBankStateCode;
	}
	

	/**
	 * Sets the disbVchrBankStateCode attribute.
	 * 
	 * @param - disbVchrBankStateCode The disbVchrBankStateCode to set.
	 * 
	 */
	public void setDisbVchrBankStateCode(String disbVchrBankStateCode) {
		this.disbVchrBankStateCode = disbVchrBankStateCode;
	}

	/**
	 * Gets the disbVchrBankCountryName attribute.
	 * 
	 * @return - Returns the disbVchrBankCountryName
	 * 
	 */
	public String getDisbVchrBankCountryName() { 
		return disbVchrBankCountryName;
	}
	

	/**
	 * Sets the disbVchrBankCountryName attribute.
	 * 
	 * @param - disbVchrBankCountryName The disbVchrBankCountryName to set.
	 * 
	 */
	public void setDisbVchrBankCountryName(String disbVchrBankCountryName) {
		this.disbVchrBankCountryName = disbVchrBankCountryName;
	}

	/**
	 * Gets the disbVchrAttentionLineText attribute.
	 * 
	 * @return - Returns the disbVchrAttentionLineText
	 * 
	 */
	public String getDisbVchrAttentionLineText() { 
		return disbVchrAttentionLineText;
	}
	

	/**
	 * Sets the disbVchrAttentionLineText attribute.
	 * 
	 * @param - disbVchrAttentionLineText The disbVchrAttentionLineText to set.
	 * 
	 */
	public void setDisbVchrAttentionLineText(String disbVchrAttentionLineText) {
		this.disbVchrAttentionLineText = disbVchrAttentionLineText;
	}

	/**
	 * Gets the disbVchrAdditionalWireText attribute.
	 * 
	 * @return - Returns the disbVchrAdditionalWireText
	 * 
	 */
	public String getDisbVchrAdditionalWireText() { 
		return disbVchrAdditionalWireText;
	}
	

	/**
	 * Sets the disbVchrAdditionalWireText attribute.
	 * 
	 * @param - disbVchrAdditionalWireText The disbVchrAdditionalWireText to set.
	 * 
	 */
	public void setDisbVchrAdditionalWireText(String disbVchrAdditionalWireText) {
		this.disbVchrAdditionalWireText = disbVchrAdditionalWireText;
	}

	/**
	 * Gets the disbVchrPayeeAccountNumber attribute.
	 * 
	 * @return - Returns the disbVchrPayeeAccountNumber
	 * 
	 */
	public String getDisbVchrPayeeAccountNumber() { 
		return disbVchrPayeeAccountNumber;
	}
	

	/**
	 * Sets the disbVchrPayeeAccountNumber attribute.
	 * 
	 * @param - disbVchrPayeeAccountNumber The disbVchrPayeeAccountNumber to set.
	 * 
	 */
	public void setDisbVchrPayeeAccountNumber(String disbVchrPayeeAccountNumber) {
		this.disbVchrPayeeAccountNumber = disbVchrPayeeAccountNumber;
	}

	/**
	 * Gets the disbVchrCurrencyTypeName attribute.
	 * 
	 * @return - Returns the disbVchrCurrencyTypeName
	 * 
	 */
	public String getDisbVchrCurrencyTypeName() { 
		return disbVchrCurrencyTypeName;
	}
	

	/**
	 * Sets the disbVchrCurrencyTypeName attribute.
	 * 
	 * @param - disbVchrCurrencyTypeName The disbVchrCurrencyTypeName to set.
	 * 
	 */
	public void setDisbVchrCurrencyTypeName(String disbVchrCurrencyTypeName) {
		this.disbVchrCurrencyTypeName = disbVchrCurrencyTypeName;
	}

	/**
	 * Gets the disbVchrCurrencyTypeCode attribute.
	 * 
	 * @return - Returns the disbVchrCurrencyTypeCode
	 * 
	 */
	public String getDisbVchrCurrencyTypeCode() { 
		return disbVchrCurrencyTypeCode;
	}
	

	/**
	 * Sets the disbVchrCurrencyTypeCode attribute.
	 * 
	 * @param - disbVchrCurrencyTypeCode The disbVchrCurrencyTypeCode to set.
	 * 
	 */
	public void setDisbVchrCurrencyTypeCode(String disbVchrCurrencyTypeCode) {
		this.disbVchrCurrencyTypeCode = disbVchrCurrencyTypeCode;
	}

	/**
	 * Gets the disbVchrForeignBankIndicator attribute.
	 * 
	 * @return - Returns the disbVchrForeignBankIndicator
	 * 
	 */
	public boolean isDisbVchrForeignBankIndicator() { 
		return disbVchrForeignBankIndicator;
	}
	

	/**
	 * Sets the disbVchrForeignBankIndicator attribute.
	 * 
	 * @param - disbVchrForeignBankIndicator The disbVchrForeignBankIndicator to set.
	 * 
	 */
	public void setDisbVchrForeignBankIndicator(boolean disbVchrForeignBankIndicator) {
		this.disbVchrForeignBankIndicator = disbVchrForeignBankIndicator;
	}

	/**
	 * Gets the disbursementVoucherWireTransferFeeWaiverIndicator attribute.
	 * 
	 * @return - Returns the disbursementVoucherWireTransferFeeWaiverIndicator
	 * 
	 */
	public boolean isDisbursementVoucherWireTransferFeeWaiverIndicator() { 
		return disbursementVoucherWireTransferFeeWaiverIndicator;
	}
	

	/**
	 * Sets the disbursementVoucherWireTransferFeeWaiverIndicator attribute.
	 * 
	 * @param - disbursementVoucherWireTransferFeeWaiverIndicator The disbursementVoucherWireTransferFeeWaiverIndicator to set.
	 * 
	 */
	public void setDisbursementVoucherWireTransferFeeWaiverIndicator(boolean disbursementVoucherWireTransferFeeWaiverIndicator) {
		this.disbursementVoucherWireTransferFeeWaiverIndicator = disbursementVoucherWireTransferFeeWaiverIndicator;
	}

	/**
	 * Gets the disbursementVoucherPayeeAccountName attribute.
	 * 
	 * @return - Returns the disbursementVoucherPayeeAccountName
	 * 
	 */
	public String getDisbursementVoucherPayeeAccountName() { 
		return disbursementVoucherPayeeAccountName;
	}
	

	/**
	 * Sets the disbursementVoucherPayeeAccountName attribute.
	 * 
	 * @param - disbursementVoucherPayeeAccountName The disbursementVoucherPayeeAccountName to set.
	 * 
	 */
	public void setDisbursementVoucherPayeeAccountName(String disbursementVoucherPayeeAccountName) {
		this.disbursementVoucherPayeeAccountName = disbursementVoucherPayeeAccountName;
	}

	/**
	 * Gets the disbursementVoucherPayeeAccountTypeCode attribute.
	 * 
	 * @return - Returns the disbursementVoucherPayeeAccountTypeCode
	 * 
	 */
	public String getDisbursementVoucherPayeeAccountTypeCode() { 
		return disbursementVoucherPayeeAccountTypeCode;
	}
	

	/**
	 * Sets the disbursementVoucherPayeeAccountTypeCode attribute.
	 * 
	 * @param - disbursementVoucherPayeeAccountTypeCode The disbursementVoucherPayeeAccountTypeCode to set.
	 * 
	 */
	public void setDisbursementVoucherPayeeAccountTypeCode(String disbursementVoucherPayeeAccountTypeCode) {
		this.disbursementVoucherPayeeAccountTypeCode = disbursementVoucherPayeeAccountTypeCode;
	}

	/**
	 * Gets the disbursementVoucherAutomatedClearingHouseProfileNumber attribute.
	 * 
	 * @return - Returns the disbursementVoucherAutomatedClearingHouseProfileNumber
	 * 
	 */
	public String getDisbursementVoucherAutomatedClearingHouseProfileNumber() { 
		return disbursementVoucherAutomatedClearingHouseProfileNumber;
	}
	

	/**
	 * Sets the disbursementVoucherAutomatedClearingHouseProfileNumber attribute.
	 * 
	 * @param - disbursementVoucherAutomatedClearingHouseProfileNumber The disbursementVoucherAutomatedClearingHouseProfileNumber to set.
	 * 
	 */
	public void setDisbursementVoucherAutomatedClearingHouseProfileNumber(String disbursementVoucherAutomatedClearingHouseProfileNumber) {
		this.disbursementVoucherAutomatedClearingHouseProfileNumber = disbursementVoucherAutomatedClearingHouseProfileNumber;
	}

	/**
	 * Gets the financialDocument attribute.
	 * 
	 * @return - Returns the financialDocument
	 * 
	 */
	public DisbursementVoucherDocument getFinancialDocument() { 
		return financialDocument;
	}
	

	/**
	 * Sets the financialDocument attribute.
	 * 
	 * @param - financialDocument The financialDocument to set.
	 * @deprecated
	 */
	public void setFinancialDocument(DisbursementVoucherDocument financialDocument) {
		this.financialDocument = financialDocument;
	}

	/**
	 * Gets the disbVchrPayeeAccount attribute.
	 * 
	 * @return - Returns the disbVchrPayeeAccount
	 * 
	 */
	public Account getDisbVchrPayeeAccount() { 
		return disbVchrPayeeAccount;
	}
	

	/**
	 * Sets the disbVchrPayeeAccount attribute.
	 * 
	 * @param - disbVchrPayeeAccount The disbVchrPayeeAccount to set.
	 * @deprecated
	 */
	public void setDisbVchrPayeeAccount(Account disbVchrPayeeAccount) {
		this.disbVchrPayeeAccount = disbVchrPayeeAccount;
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
