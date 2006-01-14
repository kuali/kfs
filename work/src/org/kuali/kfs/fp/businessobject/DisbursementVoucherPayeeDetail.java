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

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.financial.document.DisbursementVoucherDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherPayeeDetail extends BusinessObjectBase {

	private String financialDocumentNumber;
	private String disbVchrPaymentReasonCode;
	private boolean disbVchrAlienPaymentCode;
	private String disbVchrPayeeIdNumber;
	private String disbVchrPayeePersonName;
	private String disbVchrPayeeLine1Addr;
	private String disbVchrPayeeLine2Addr;
	private String disbVchrPayeeCityName;
	private String disbVchrPayeeStateCode;
	private String disbVchrPayeeZipCode;
	private String disbVchrPayeeCountryName;
	private String disbVchrRemitPersonName;
	private String disbVchrRemitLine1Addr;
	private String disbVchrRemitLine2Addr;
	private String disbVchrRemitCityName;
	private String disbVchrRemitStateCode;
	private String disbVchrRemitZipCode;
	private String disbVchrRemitCountryName;
	private boolean disbVchrPayeeEmployeeCode;
	private boolean dvPayeeRevolvingFundCode;
	
	// non-database fields
	private String dvTaxIdNumber;
	private String dvTaxPayerTypeCode;
	private String dvPayeeType;
	
	private DisbursementVoucherDocument financialDocument;
	private PaymentReasonCode disbVchrPaymentReason;
	private UniversalUser disbVchrPayeeId;

	/**
	 * Default no-arg constructor.
	 */
	public DisbursementVoucherPayeeDetail() {

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
	 * Gets the disbVchrPaymentReasonCode attribute.
	 * 
	 * @return - Returns the disbVchrPaymentReasonCode
	 * 
	 */
	public String getDisbVchrPaymentReasonCode() { 
		return disbVchrPaymentReasonCode;
	}
	

	/**
	 * Sets the disbVchrPaymentReasonCode attribute.
	 * 
	 * @param - disbVchrPaymentReasonCode The disbVchrPaymentReasonCode to set.
	 * 
	 */
	public void setDisbVchrPaymentReasonCode(String disbVchrPaymentReasonCode) {
		this.disbVchrPaymentReasonCode = disbVchrPaymentReasonCode;
	}

	/**
	 * Gets the disbVchrAlienPaymentCode attribute.
	 * 
	 * @return - Returns the disbVchrAlienPaymentCode
	 * 
	 */
	public boolean isDisbVchrAlienPaymentCode() { 
		return disbVchrAlienPaymentCode;
	}
	

	/**
	 * Sets the disbVchrAlienPaymentCode attribute.
	 * 
	 * @param - disbVchrAlienPaymentCode The disbVchrAlienPaymentCode to set.
	 * 
	 */
	public void setDisbVchrAlienPaymentCode(boolean disbVchrAlienPaymentCode) {
		this.disbVchrAlienPaymentCode = disbVchrAlienPaymentCode;
	}

	/**
	 * Gets the disbVchrPayeeIdNumber attribute.
	 * 
	 * @return - Returns the disbVchrPayeeIdNumber
	 * 
	 */
	public String getDisbVchrPayeeIdNumber() { 
		return disbVchrPayeeIdNumber;
	}
	

	/**
	 * Sets the disbVchrPayeeIdNumber attribute.
	 * 
	 * @param - disbVchrPayeeIdNumber The disbVchrPayeeIdNumber to set.
	 * 
	 */
	public void setDisbVchrPayeeIdNumber(String disbVchrPayeeIdNumber) {
		this.disbVchrPayeeIdNumber = disbVchrPayeeIdNumber;
	}

	/**
	 * Gets the disbVchrPayeePersonName attribute.
	 * 
	 * @return - Returns the disbVchrPayeePersonName
	 * 
	 */
	public String getDisbVchrPayeePersonName() { 
		return disbVchrPayeePersonName;
	}
	

	/**
	 * Sets the disbVchrPayeePersonName attribute.
	 * 
	 * @param - disbVchrPayeePersonName The disbVchrPayeePersonName to set.
	 * 
	 */
	public void setDisbVchrPayeePersonName(String disbVchrPayeePersonName) {
		this.disbVchrPayeePersonName = disbVchrPayeePersonName;
	}

	/**
	 * Gets the disbVchrPayeeLine1Addr attribute.
	 * 
	 * @return - Returns the disbVchrPayeeLine1Addr
	 * 
	 */
	public String getDisbVchrPayeeLine1Addr() { 
		return disbVchrPayeeLine1Addr;
	}
	

	/**
	 * Sets the disbVchrPayeeLine1Addr attribute.
	 * 
	 * @param - disbVchrPayeeLine1Addr The disbVchrPayeeLine1Addr to set.
	 * 
	 */
	public void setDisbVchrPayeeLine1Addr(String disbVchrPayeeLine1Addr) {
		this.disbVchrPayeeLine1Addr = disbVchrPayeeLine1Addr;
	}

	/**
	 * Gets the disbVchrPayeeLine2Addr attribute.
	 * 
	 * @return - Returns the disbVchrPayeeLine2Addr
	 * 
	 */
	public String getDisbVchrPayeeLine2Addr() { 
		return disbVchrPayeeLine2Addr;
	}
	

	/**
	 * Sets the disbVchrPayeeLine2Addr attribute.
	 * 
	 * @param - disbVchrPayeeLine2Addr The disbVchrPayeeLine2Addr to set.
	 * 
	 */
	public void setDisbVchrPayeeLine2Addr(String disbVchrPayeeLine2Addr) {
		this.disbVchrPayeeLine2Addr = disbVchrPayeeLine2Addr;
	}

	/**
	 * Gets the disbVchrPayeeCityName attribute.
	 * 
	 * @return - Returns the disbVchrPayeeCityName
	 * 
	 */
	public String getDisbVchrPayeeCityName() { 
		return disbVchrPayeeCityName;
	}
	

	/**
	 * Sets the disbVchrPayeeCityName attribute.
	 * 
	 * @param - disbVchrPayeeCityName The disbVchrPayeeCityName to set.
	 * 
	 */
	public void setDisbVchrPayeeCityName(String disbVchrPayeeCityName) {
		this.disbVchrPayeeCityName = disbVchrPayeeCityName;
	}

	/**
	 * Gets the disbVchrPayeeStateCode attribute.
	 * 
	 * @return - Returns the disbVchrPayeeStateCode
	 * 
	 */
	public String getDisbVchrPayeeStateCode() { 
		return disbVchrPayeeStateCode;
	}
	

	/**
	 * Sets the disbVchrPayeeStateCode attribute.
	 * 
	 * @param - disbVchrPayeeStateCode The disbVchrPayeeStateCode to set.
	 * 
	 */
	public void setDisbVchrPayeeStateCode(String disbVchrPayeeStateCode) {
		this.disbVchrPayeeStateCode = disbVchrPayeeStateCode;
	}

	/**
	 * Gets the disbVchrPayeeZipCode attribute.
	 * 
	 * @return - Returns the disbVchrPayeeZipCode
	 * 
	 */
	public String getDisbVchrPayeeZipCode() { 
		return disbVchrPayeeZipCode;
	}
	

	/**
	 * Sets the disbVchrPayeeZipCode attribute.
	 * 
	 * @param - disbVchrPayeeZipCode The disbVchrPayeeZipCode to set.
	 * 
	 */
	public void setDisbVchrPayeeZipCode(String disbVchrPayeeZipCode) {
		this.disbVchrPayeeZipCode = disbVchrPayeeZipCode;
	}

	/**
	 * Gets the disbVchrPayeeCountryName attribute.
	 * 
	 * @return - Returns the disbVchrPayeeCountryName
	 * 
	 */
	public String getDisbVchrPayeeCountryName() { 
		return disbVchrPayeeCountryName;
	}
	

	/**
	 * Sets the disbVchrPayeeCountryName attribute.
	 * 
	 * @param - disbVchrPayeeCountryName The disbVchrPayeeCountryName to set.
	 * 
	 */
	public void setDisbVchrPayeeCountryName(String disbVchrPayeeCountryName) {
		this.disbVchrPayeeCountryName = disbVchrPayeeCountryName;
	}

	/**
	 * Gets the disbVchrRemitPersonName attribute.
	 * 
	 * @return - Returns the disbVchrRemitPersonName
	 * 
	 */
	public String getDisbVchrRemitPersonName() { 
		return disbVchrRemitPersonName;
	}
	

	/**
	 * Sets the disbVchrRemitPersonName attribute.
	 * 
	 * @param - disbVchrRemitPersonName The disbVchrRemitPersonName to set.
	 * 
	 */
	public void setDisbVchrRemitPersonName(String disbVchrRemitPersonName) {
		this.disbVchrRemitPersonName = disbVchrRemitPersonName;
	}

	/**
	 * Gets the disbVchrRemitLine1Addr attribute.
	 * 
	 * @return - Returns the disbVchrRemitLine1Addr
	 * 
	 */
	public String getDisbVchrRemitLine1Addr() { 
		return disbVchrRemitLine1Addr;
	}
	

	/**
	 * Sets the disbVchrRemitLine1Addr attribute.
	 * 
	 * @param - disbVchrRemitLine1Addr The disbVchrRemitLine1Addr to set.
	 * 
	 */
	public void setDisbVchrRemitLine1Addr(String disbVchrRemitLine1Addr) {
		this.disbVchrRemitLine1Addr = disbVchrRemitLine1Addr;
	}

	/**
	 * Gets the disbVchrRemitLine2Addr attribute.
	 * 
	 * @return - Returns the disbVchrRemitLine2Addr
	 * 
	 */
	public String getDisbVchrRemitLine2Addr() { 
		return disbVchrRemitLine2Addr;
	}
	

	/**
	 * Sets the disbVchrRemitLine2Addr attribute.
	 * 
	 * @param - disbVchrRemitLine2Addr The disbVchrRemitLine2Addr to set.
	 * 
	 */
	public void setDisbVchrRemitLine2Addr(String disbVchrRemitLine2Addr) {
		this.disbVchrRemitLine2Addr = disbVchrRemitLine2Addr;
	}

	/**
	 * Gets the disbVchrRemitCityName attribute.
	 * 
	 * @return - Returns the disbVchrRemitCityName
	 * 
	 */
	public String getDisbVchrRemitCityName() { 
		return disbVchrRemitCityName;
	}
	

	/**
	 * Sets the disbVchrRemitCityName attribute.
	 * 
	 * @param - disbVchrRemitCityName The disbVchrRemitCityName to set.
	 * 
	 */
	public void setDisbVchrRemitCityName(String disbVchrRemitCityName) {
		this.disbVchrRemitCityName = disbVchrRemitCityName;
	}

	/**
	 * Gets the disbVchrRemitStateCode attribute.
	 * 
	 * @return - Returns the disbVchrRemitStateCode
	 * 
	 */
	public String getDisbVchrRemitStateCode() { 
		return disbVchrRemitStateCode;
	}
	

	/**
	 * Sets the disbVchrRemitStateCode attribute.
	 * 
	 * @param - disbVchrRemitStateCode The disbVchrRemitStateCode to set.
	 * 
	 */
	public void setDisbVchrRemitStateCode(String disbVchrRemitStateCode) {
		this.disbVchrRemitStateCode = disbVchrRemitStateCode;
	}

	/**
	 * Gets the disbVchrRemitZipCode attribute.
	 * 
	 * @return - Returns the disbVchrRemitZipCode
	 * 
	 */
	public String getDisbVchrRemitZipCode() { 
		return disbVchrRemitZipCode;
	}
	

	/**
	 * Sets the disbVchrRemitZipCode attribute.
	 * 
	 * @param - disbVchrRemitZipCode The disbVchrRemitZipCode to set.
	 * 
	 */
	public void setDisbVchrRemitZipCode(String disbVchrRemitZipCode) {
		this.disbVchrRemitZipCode = disbVchrRemitZipCode;
	}

	/**
	 * Gets the disbVchrRemitCountryName attribute.
	 * 
	 * @return - Returns the disbVchrRemitCountryName
	 * 
	 */
	public String getDisbVchrRemitCountryName() { 
		return disbVchrRemitCountryName;
	}
	

	/**
	 * Sets the disbVchrRemitCountryName attribute.
	 * 
	 * @param - disbVchrRemitCountryName The disbVchrRemitCountryName to set.
	 * 
	 */
	public void setDisbVchrRemitCountryName(String disbVchrRemitCountryName) {
		this.disbVchrRemitCountryName = disbVchrRemitCountryName;
	}

	/**
	 * Gets the disbVchrPayeeEmployeeCode attribute.
	 * 
	 * @return - Returns the disbVchrPayeeEmployeeCode
	 * 
	 */
	public boolean isDisbVchrPayeeEmployeeCode() { 
		return disbVchrPayeeEmployeeCode;
	}
	

	/**
	 * Sets the disbVchrPayeeEmployeeCode attribute.
	 * 
	 * @param - disbVchrPayeeEmployeeCode The disbVchrPayeeEmployeeCode to set.
	 * 
	 */
	public void setDisbVchrPayeeEmployeeCode(boolean disbVchrPayeeEmployeeCode) {
		this.disbVchrPayeeEmployeeCode = disbVchrPayeeEmployeeCode;
	}

	/**
	 * Gets the dvPayeeRevolvingFundCode attribute.
	 * 
	 * @return - Returns the dvPayeeRevolvingFundCode
	 * 
	 */
	public boolean isDvPayeeRevolvingFundCode() { 
		return dvPayeeRevolvingFundCode;
	}
	

	/**
	 * Sets the dvPayeeRevolvingFundCode attribute.
	 * 
	 * @param - dvPayeeRevolvingFundCode The dvPayeeRevolvingFundCode to set.
	 * 
	 */
	public void setDvPayeeRevolvingFundCode(boolean dvPayeeRevolvingFundCode) {
		this.dvPayeeRevolvingFundCode = dvPayeeRevolvingFundCode;
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
	 * Gets the disbVchrPaymentReason attribute.
	 * 
	 * @return - Returns the disbVchrPaymentReason
	 * 
	 */
	public PaymentReasonCode getDisbVchrPaymentReason() { 
		return disbVchrPaymentReason;
	}
	

	/**
	 * Sets the disbVchrPaymentReason attribute.
	 * 
	 * @param - disbVchrPaymentReason The disbVchrPaymentReason to set.
	 * @deprecated
	 */
	public void setDisbVchrPaymentReason(PaymentReasonCode disbVchrPaymentReason) {
		this.disbVchrPaymentReason = disbVchrPaymentReason;
	}

	/**
	 * Gets the disbVchrPayeeId attribute.
	 * 
	 * @return - Returns the disbVchrPayeeId
	 * 
	 */
	public UniversalUser getDisbVchrPayeeId() { 
		return disbVchrPayeeId;
	}
	

	/**
	 * Sets the disbVchrPayeeId attribute.
	 * 
	 * @param - disbVchrPayeeId The disbVchrPayeeId to set.
	 * @deprecated
	 */
	public void setDisbVchrPayeeId(UniversalUser disbVchrPayeeId) {
		this.disbVchrPayeeId = disbVchrPayeeId;
	}
	
	
    /**
     * @return Returns the dvPayeeType.
     */
    public String getDvPayeeType() {
        return dvPayeeType;
    }
    
    
    /**
     * @param dvPayeeType The dvPayeeType to set.
     */
    public void setDvPayeeType(String dvPayeeType) {
        this.dvPayeeType = dvPayeeType;
    }
    
    
    /**
     * @return Returns the dvTaxIdNumber.
     */
    public String getDvTaxIdNumber() {
        return dvTaxIdNumber;
    }
    
    
    /**
     * @param dvTaxIdNumber The dvTaxIdNumber to set.
     */
    public void setDvTaxIdNumber(String dvTaxIdNumber) {
        this.dvTaxIdNumber = dvTaxIdNumber;
    }
    
    
    /**
     * @return Returns the dvTaxPayerTypeCode.
     */
    public String getDvTaxPayerTypeCode() {
        return dvTaxPayerTypeCode;
    }
    
    
    /**
     * @param dvTaxPayerTypeCode The dvTaxPayerTypeCode to set.
     */
    public void setDvTaxPayerTypeCode(String dvTaxPayerTypeCode) {
        this.dvTaxPayerTypeCode = dvTaxPayerTypeCode;
    }
    
    
	/**
	 * Checks the payee id number for a vendor type with the assertion
	 * that vendor ids contain the '-' character.
	 * @return
	 */
	public boolean isVendor(){
	    boolean vendor = false;
	    
	    if (Constants.DV_PAYEE_TYPE_VENDOR.equals(this.getDvPayeeType()) ||
	            (StringUtils.isNotBlank(this.disbVchrPayeeIdNumber) &&
	            StringUtils.contains(this.disbVchrPayeeIdNumber, "-"))) {
	        vendor = true;
	    }
	    
	    return vendor;
	}
	
	/**
	 * Checks the payee id number for a payee type with the assertion
	 * that payee ids start with 'P'.
	 * @return
	 */
	public boolean isPayee(){
	    boolean payee = false;
	   
	    if (Constants.DV_PAYEE_TYPE_PAYEE.equals(this.getDvPayeeType()) ||
	           (StringUtils.isNotBlank(this.disbVchrPayeeIdNumber) &&
	           this.disbVchrPayeeIdNumber.startsWith("P"))) {
	        payee = true;
	    }
	    
	    return payee;
	}
	
	/**
	 * Checks the payee id number for a employee type with the assertion
	 * that employee ids have length 10, start with '1', and do not contain
	 * the character '-'.
	 * @return
	 */
	public boolean isEmployee(){
	    boolean employee = false;
	   
	    if (Constants.DV_PAYEE_TYPE_EMPLOYEE.equals(this.getDvPayeeType()) ||
	           (StringUtils.isNotBlank(this.disbVchrPayeeIdNumber) &&
	           this.disbVchrPayeeIdNumber.length() == 10 &&
	           this.disbVchrPayeeIdNumber.startsWith("1") &&
	           !StringUtils.contains(this.disbVchrPayeeIdNumber, "-"))) {
	        employee = true;
	    }
	    
	    return employee;
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
