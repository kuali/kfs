/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/ElectronicInvoiceLoadSummary.java,v $
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

package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
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
	 * @return Returns the accountsPayableElectronicInvoiceLoadSummaryIdentifier
	 * 
	 */
	public Long getAccountsPayableElectronicInvoiceLoadSummaryIdentifier() { 
		return accountsPayableElectronicInvoiceLoadSummaryIdentifier;
	}

	/**
	 * Sets the accountsPayableElectronicInvoiceLoadSummaryIdentifier attribute.
	 * 
	 * @param accountsPayableElectronicInvoiceLoadSummaryIdentifier The accountsPayableElectronicInvoiceLoadSummaryIdentifier to set.
	 * 
	 */
	public void setAccountsPayableElectronicInvoiceLoadSummaryIdentifier(Long accountsPayableElectronicInvoiceLoadSummaryIdentifier) {
		this.accountsPayableElectronicInvoiceLoadSummaryIdentifier = accountsPayableElectronicInvoiceLoadSummaryIdentifier;
	}


	/**
	 * Gets the vendorDunsNumber attribute.
	 * 
	 * @return Returns the vendorDunsNumber
	 * 
	 */
	public String getVendorDunsNumber() { 
		return vendorDunsNumber;
	}

	/**
	 * Sets the vendorDunsNumber attribute.
	 * 
	 * @param vendorDunsNumber The vendorDunsNumber to set.
	 * 
	 */
	public void setVendorDunsNumber(String vendorDunsNumber) {
		this.vendorDunsNumber = vendorDunsNumber;
	}


	/**
	 * Gets the fileProcessDate attribute.
	 * 
	 * @return Returns the fileProcessDate
	 * 
	 */
	public Date getFileProcessDate() { 
		return fileProcessDate;
	}

	/**
	 * Sets the fileProcessDate attribute.
	 * 
	 * @param fileProcessDate The fileProcessDate to set.
	 * 
	 */
	public void setFileProcessDate(Date fileProcessDate) {
		this.fileProcessDate = fileProcessDate;
	}


	/**
	 * Gets the invoiceLoadSuccessCount attribute.
	 * 
	 * @return Returns the invoiceLoadSuccessCount
	 * 
	 */
	public Integer getInvoiceLoadSuccessCount() { 
		return invoiceLoadSuccessCount;
	}

	/**
	 * Sets the invoiceLoadSuccessCount attribute.
	 * 
	 * @param invoiceLoadSuccessCount The invoiceLoadSuccessCount to set.
	 * 
	 */
	public void setInvoiceLoadSuccessCount(Integer invoiceLoadSuccessCount) {
		this.invoiceLoadSuccessCount = invoiceLoadSuccessCount;
	}


	/**
	 * Gets the invoiceLoadSuccessAmount attribute.
	 * 
	 * @return Returns the invoiceLoadSuccessAmount
	 * 
	 */
	public BigDecimal getInvoiceLoadSuccessAmount() { 
		return invoiceLoadSuccessAmount;
	}

	/**
	 * Sets the invoiceLoadSuccessAmount attribute.
	 * 
	 * @param invoiceLoadSuccessAmount The invoiceLoadSuccessAmount to set.
	 * 
	 */
	public void setInvoiceLoadSuccessAmount(BigDecimal invoiceLoadSuccessAmount) {
		this.invoiceLoadSuccessAmount = invoiceLoadSuccessAmount;
	}


	/**
	 * Gets the invoiceLoadFailCount attribute.
	 * 
	 * @return Returns the invoiceLoadFailCount
	 * 
	 */
	public Integer getInvoiceLoadFailCount() { 
		return invoiceLoadFailCount;
	}

	/**
	 * Sets the invoiceLoadFailCount attribute.
	 * 
	 * @param invoiceLoadFailCount The invoiceLoadFailCount to set.
	 * 
	 */
	public void setInvoiceLoadFailCount(Integer invoiceLoadFailCount) {
		this.invoiceLoadFailCount = invoiceLoadFailCount;
	}


	/**
	 * Gets the invoiceLoadFailAmount attribute.
	 * 
	 * @return Returns the invoiceLoadFailAmount
	 * 
	 */
	public BigDecimal getInvoiceLoadFailAmount() { 
		return invoiceLoadFailAmount;
	}

	/**
	 * Sets the invoiceLoadFailAmount attribute.
	 * 
	 * @param invoiceLoadFailAmount The invoiceLoadFailAmount to set.
	 * 
	 */
	public void setInvoiceLoadFailAmount(BigDecimal invoiceLoadFailAmount) {
		this.invoiceLoadFailAmount = invoiceLoadFailAmount;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
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
