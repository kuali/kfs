/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Electronic Invoice Invoice Load Summary Business Object.
 */
public class ElectronicInvoiceLoadSummary extends PersistableBusinessObjectBase {

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

    public Long getAccountsPayableElectronicInvoiceLoadSummaryIdentifier() {
        return accountsPayableElectronicInvoiceLoadSummaryIdentifier;
    }

    public void setAccountsPayableElectronicInvoiceLoadSummaryIdentifier(Long accountsPayableElectronicInvoiceLoadSummaryIdentifier) {
        this.accountsPayableElectronicInvoiceLoadSummaryIdentifier = accountsPayableElectronicInvoiceLoadSummaryIdentifier;
    }

    public Date getFileProcessDate() {
        return fileProcessDate;
    }

    public void setFileProcessDate(Date fileProcessDate) {
        this.fileProcessDate = fileProcessDate;
    }

    public BigDecimal getInvoiceLoadFailAmount() {
        return invoiceLoadFailAmount;
    }

    public void setInvoiceLoadFailAmount(BigDecimal invoiceLoadFailAmount) {
        this.invoiceLoadFailAmount = invoiceLoadFailAmount;
    }

    public Integer getInvoiceLoadFailCount() {
        return invoiceLoadFailCount;
    }

    public void setInvoiceLoadFailCount(Integer invoiceLoadFailCount) {
        this.invoiceLoadFailCount = invoiceLoadFailCount;
    }

    public BigDecimal getInvoiceLoadSuccessAmount() {
        return invoiceLoadSuccessAmount;
    }

    public void setInvoiceLoadSuccessAmount(BigDecimal invoiceLoadSuccessAmount) {
        this.invoiceLoadSuccessAmount = invoiceLoadSuccessAmount;
    }

    public Integer getInvoiceLoadSuccessCount() {
        return invoiceLoadSuccessCount;
    }

    public void setInvoiceLoadSuccessCount(Integer invoiceLoadSuccessCount) {
        this.invoiceLoadSuccessCount = invoiceLoadSuccessCount;
    }

    public String getVendorDunsNumber() {
        return vendorDunsNumber;
    }

    public void setVendorDunsNumber(String vendorDunsNumber) {
        this.vendorDunsNumber = vendorDunsNumber;
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
