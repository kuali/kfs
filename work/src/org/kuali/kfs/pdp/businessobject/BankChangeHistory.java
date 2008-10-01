/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.businessobject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;

/**
 * Report object for the bank change lookup
 */
public class BankChangeHistory extends TransientBusinessObjectBase {
    private String chartCode;
    private String orgCode;
    private String subUnitCode;
    private String custPaymentDocNbr;
    private String purchaseOrderNbr;
    private String invoiceNbr;
    private String payeeName;
    private Timestamp paymentDate;
    private Timestamp disbursementDate;
    private String paymentStatusCode;
    private String disbursementTypeName;
    private Integer disbursementNbr;
    private BigDecimal netPaymentAmount;
    private String origBankCode;
    private String bankCode;

    public BankChangeHistory() {

    }

    /**
     * Gets the chartCode attribute.
     * 
     * @return Returns the chartCode.
     */
    public String getChartCode() {
        return chartCode;
    }

    /**
     * Sets the chartCode attribute value.
     * 
     * @param chartCode The chartCode to set.
     */
    public void setChartCode(String chartCode) {
        this.chartCode = chartCode;
    }

    /**
     * Gets the orgCode attribute.
     * 
     * @return Returns the orgCode.
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * Sets the orgCode attribute value.
     * 
     * @param orgCode The orgCode to set.
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * Gets the subUnitCode attribute.
     * 
     * @return Returns the subUnitCode.
     */
    public String getSubUnitCode() {
        return subUnitCode;
    }

    /**
     * Sets the subUnitCode attribute value.
     * 
     * @param subUnitCode The subUnitCode to set.
     */
    public void setSubUnitCode(String subUnitCode) {
        this.subUnitCode = subUnitCode;
    }

    /**
     * Gets the custPaymentDocNbr attribute.
     * 
     * @return Returns the custPaymentDocNbr.
     */
    public String getCustPaymentDocNbr() {
        return custPaymentDocNbr;
    }

    /**
     * Sets the custPaymentDocNbr attribute value.
     * 
     * @param custPaymentDocNbr The custPaymentDocNbr to set.
     */
    public void setCustPaymentDocNbr(String custPaymentDocNbr) {
        this.custPaymentDocNbr = custPaymentDocNbr;
    }

    /**
     * Gets the purchaseOrderNbr attribute.
     * 
     * @return Returns the purchaseOrderNbr.
     */
    public String getPurchaseOrderNbr() {
        return purchaseOrderNbr;
    }

    /**
     * Sets the purchaseOrderNbr attribute value.
     * 
     * @param purchaseOrderNbr The purchaseOrderNbr to set.
     */
    public void setPurchaseOrderNbr(String purchaseOrderNbr) {
        this.purchaseOrderNbr = purchaseOrderNbr;
    }

    /**
     * Gets the invoiceNbr attribute.
     * 
     * @return Returns the invoiceNbr.
     */
    public String getInvoiceNbr() {
        return invoiceNbr;
    }

    /**
     * Sets the invoiceNbr attribute value.
     * 
     * @param invoiceNbr The invoiceNbr to set.
     */
    public void setInvoiceNbr(String invoiceNbr) {
        this.invoiceNbr = invoiceNbr;
    }

    /**
     * Gets the payeeName attribute.
     * 
     * @return Returns the payeeName.
     */
    public String getPayeeName() {
        return payeeName;
    }

    /**
     * Sets the payeeName attribute value.
     * 
     * @param payeeName The payeeName to set.
     */
    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    /**
     * Gets the paymentDate attribute.
     * 
     * @return Returns the paymentDate.
     */
    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the paymentDate attribute value.
     * 
     * @param paymentDate The paymentDate to set.
     */
    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Gets the disbursementDate attribute.
     * 
     * @return Returns the disbursementDate.
     */
    public Timestamp getDisbursementDate() {
        return disbursementDate;
    }

    /**
     * Sets the disbursementDate attribute value.
     * 
     * @param disbursementDate The disbursementDate to set.
     */
    public void setDisbursementDate(Timestamp disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    /**
     * Gets the paymentStatusCode attribute.
     * 
     * @return Returns the paymentStatusCode.
     */
    public String getPaymentStatusCode() {
        return paymentStatusCode;
    }

    /**
     * Sets the paymentStatusCode attribute value.
     * 
     * @param paymentStatusCode The paymentStatusCode to set.
     */
    public void setPaymentStatusCode(String paymentStatusCode) {
        this.paymentStatusCode = paymentStatusCode;
    }

    /**
     * Gets the disbursementTypeName attribute.
     * 
     * @return Returns the disbursementTypeName.
     */
    public String getDisbursementTypeName() {
        return disbursementTypeName;
    }

    /**
     * Sets the disbursementTypeName attribute value.
     * 
     * @param disbursementTypeName The disbursementTypeName to set.
     */
    public void setDisbursementTypeName(String disbursementTypeName) {
        this.disbursementTypeName = disbursementTypeName;
    }

    /**
     * Gets the disbursementNbr attribute.
     * 
     * @return Returns the disbursementNbr.
     */
    public Integer getDisbursementNbr() {
        return disbursementNbr;
    }

    /**
     * Sets the disbursementNbr attribute value.
     * 
     * @param disbursementNbr The disbursementNbr to set.
     */
    public void setDisbursementNbr(Integer disbursementNbr) {
        this.disbursementNbr = disbursementNbr;
    }

    /**
     * Gets the netPaymentAmount attribute.
     * 
     * @return Returns the netPaymentAmount.
     */
    public BigDecimal getNetPaymentAmount() {
        return netPaymentAmount;
    }

    /**
     * Sets the netPaymentAmount attribute value.
     * 
     * @param netPaymentAmount The netPaymentAmount to set.
     */
    public void setNetPaymentAmount(BigDecimal netPaymentAmount) {
        this.netPaymentAmount = netPaymentAmount;
    }

    /**
     * Gets the origBankCode attribute.
     * 
     * @return Returns the origBankCode.
     */
    public String getOrigBankCode() {
        return origBankCode;
    }

    /**
     * Sets the origBankCode attribute value.
     * 
     * @param origBankCode The origBankCode to set.
     */
    public void setOrigBankCode(String origBankCode) {
        this.origBankCode = origBankCode;
    }

    /**
     * Gets the bankCode attribute.
     * 
     * @return Returns the bankCode.
     */
    public String getBankCode() {
        return bankCode;
    }

    /**
     * Sets the bankCode attribute value.
     * 
     * @param bankCode The bankCode to set.
     */
    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(PdpPropertyConstants.DISBURSEMENT_NBR, this.disbursementNbr);
        m.put(PdpPropertyConstants.ORIG_BANK_CODE, this.origBankCode);
        m.put(KFSPropertyConstants.BANK_CODE, this.bankCode);

        return m;
    }
}
