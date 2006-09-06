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

package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ElectronicInvoiceLoadSummary extends BusinessObjectBase {

	private Long accountsPayableElectronicInvoiceLoadSummaryIdentifier;
	private String vendorDunsNumber;
	private Date fileProcessDate;
	private Integer invoiceLoadSuccessCount;
	private BigDecimal invoiceLoadSuccessAmount;
	private Integer invoiceLoadFailCount;
	private BigDecimal invoiceLoadFailAmount;

	/**
	 * Default constructor.
	 */
	public ElectronicInvoiceLoadSummary() {

	}

	/**
	 * Gets the accountsPayableElectronicInvoiceLoadSummaryIdentifier attribute.
	 * 
	 * @return - Returns the accountsPayableElectronicInvoiceLoadSummaryIdentifier
	 * 
	 */
	public Long getAccountsPayableElectronicInvoiceLoadSummaryIdentifier() { 
		return accountsPayableElectronicInvoiceLoadSummaryIdentifier;
	}

	/**
	 * Sets the accountsPayableElectronicInvoiceLoadSummaryIdentifier attribute.
	 * 
	 * @param - accountsPayableElectronicInvoiceLoadSummaryIdentifier The accountsPayableElectronicInvoiceLoadSummaryIdentifier to set.
	 * 
	 */
	public void setAccountsPayableElectronicInvoiceLoadSummaryIdentifier(Long accountsPayableElectronicInvoiceLoadSummaryIdentifier) {
		this.accountsPayableElectronicInvoiceLoadSummaryIdentifier = accountsPayableElectronicInvoiceLoadSummaryIdentifier;
	}


	/**
	 * Gets the vendorDunsNumber attribute.
	 * 
	 * @return - Returns the vendorDunsNumber
	 * 
	 */
	public String getVendorDunsNumber() { 
		return vendorDunsNumber;
	}

	/**
	 * Sets the vendorDunsNumber attribute.
	 * 
	 * @param - vendorDunsNumber The vendorDunsNumber to set.
	 * 
	 */
	public void setVendorDunsNumber(String vendorDunsNumber) {
		this.vendorDunsNumber = vendorDunsNumber;
	}


	/**
	 * Gets the fileProcessDate attribute.
	 * 
	 * @return - Returns the fileProcessDate
	 * 
	 */
	public Date getFileProcessDate() { 
		return fileProcessDate;
	}

	/**
	 * Sets the fileProcessDate attribute.
	 * 
	 * @param - fileProcessDate The fileProcessDate to set.
	 * 
	 */
	public void setFileProcessDate(Date fileProcessDate) {
		this.fileProcessDate = fileProcessDate;
	}


	/**
	 * Gets the invoiceLoadSuccessCount attribute.
	 * 
	 * @return - Returns the invoiceLoadSuccessCount
	 * 
	 */
	public Integer getInvoiceLoadSuccessCount() { 
		return invoiceLoadSuccessCount;
	}

	/**
	 * Sets the invoiceLoadSuccessCount attribute.
	 * 
	 * @param - invoiceLoadSuccessCount The invoiceLoadSuccessCount to set.
	 * 
	 */
	public void setInvoiceLoadSuccessCount(Integer invoiceLoadSuccessCount) {
		this.invoiceLoadSuccessCount = invoiceLoadSuccessCount;
	}


	/**
	 * Gets the invoiceLoadSuccessAmount attribute.
	 * 
	 * @return - Returns the invoiceLoadSuccessAmount
	 * 
	 */
	public BigDecimal getInvoiceLoadSuccessAmount() { 
		return invoiceLoadSuccessAmount;
	}

	/**
	 * Sets the invoiceLoadSuccessAmount attribute.
	 * 
	 * @param - invoiceLoadSuccessAmount The invoiceLoadSuccessAmount to set.
	 * 
	 */
	public void setInvoiceLoadSuccessAmount(BigDecimal invoiceLoadSuccessAmount) {
		this.invoiceLoadSuccessAmount = invoiceLoadSuccessAmount;
	}


	/**
	 * Gets the invoiceLoadFailCount attribute.
	 * 
	 * @return - Returns the invoiceLoadFailCount
	 * 
	 */
	public Integer getInvoiceLoadFailCount() { 
		return invoiceLoadFailCount;
	}

	/**
	 * Sets the invoiceLoadFailCount attribute.
	 * 
	 * @param - invoiceLoadFailCount The invoiceLoadFailCount to set.
	 * 
	 */
	public void setInvoiceLoadFailCount(Integer invoiceLoadFailCount) {
		this.invoiceLoadFailCount = invoiceLoadFailCount;
	}


	/**
	 * Gets the invoiceLoadFailAmount attribute.
	 * 
	 * @return - Returns the invoiceLoadFailAmount
	 * 
	 */
	public BigDecimal getInvoiceLoadFailAmount() { 
		return invoiceLoadFailAmount;
	}

	/**
	 * Sets the invoiceLoadFailAmount attribute.
	 * 
	 * @param - invoiceLoadFailAmount The invoiceLoadFailAmount to set.
	 * 
	 */
	public void setInvoiceLoadFailAmount(BigDecimal invoiceLoadFailAmount) {
		this.invoiceLoadFailAmount = invoiceLoadFailAmount;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.accountsPayableElectronicInvoiceLoadSummaryIdentifier != null) {
            m.put("accountsPayableElectronicInvoiceLoadSummaryIdentifier", this.accountsPayableElectronicInvoiceLoadSummaryIdentifier.toString());
        }
        m.put("vendorDunsNumber", this.vendorDunsNumber);
	    return m;
    }
}
