/*
 * Copyright 2006-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.businessobject;

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class is an empty derived class
 */
public class CustomerAgingReportDetail extends TransientBusinessObjectBase {

    private String customerName;
    private String customerNumber;
    private String processingOrBillingChartOfAccountsCode;
    private String accountChartOfAccountsCode;
    private String organizationCode;
    private String accountNumber;
    private String reportOption = ArConstants.CustomerAgingReportFields.PROCESSING_ORG;
    private Date reportRunDate;
    private KualiDecimal unpaidBalance0to30 = KualiDecimal.ZERO;
    private KualiDecimal unpaidBalance31to60 = KualiDecimal.ZERO;
    private KualiDecimal unpaidBalance61to90 = KualiDecimal.ZERO;
    private KualiDecimal unpaidBalance91toSYSPR = KualiDecimal.ZERO;
    private KualiDecimal unpaidBalanceSYSPRplus1orMore = KualiDecimal.ZERO;

    private KualiDecimal totalOpenInvoices = KualiDecimal.ZERO;
    private KualiDecimal totalWriteOff = KualiDecimal.ZERO;

    /**
     * Constructs a CustomerAgingReportDetail.java.
     */
    public CustomerAgingReportDetail() {
        super();
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("customerNumber", this.customerNumber);
        m.put("customerName", this.customerName);
        m.put("accountNumber", this.accountNumber);
        return m;
    }

    /**
     * Gets the customerName attribute.
     * 
     * @return Returns the customerName.
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute value.
     * 
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the customerNumber attribute.
     * 
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute value.
     * 
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the processingOrBillingChartOfAccountsCode attribute.
     * 
     * @return Returns the processingOrBillingChartOfAccountsCode.
     */
    public String getProcessingOrBillingChartOfAccountsCode() {
        return processingOrBillingChartOfAccountsCode;
    }

    /**
     * Sets the processingOrBillingChartOfAccountsCode attribute value.
     * 
     * @param processingOrBillingChartOfAccountsCode The processingOrBillingChartOfAccountsCode to set.
     */
    public void setProcessingOrBillingChartOfAccountsCode(String processingOrBillingChartOfAccountsCode) {
        this.processingOrBillingChartOfAccountsCode = processingOrBillingChartOfAccountsCode;
    }

    /**
     * Gets the accountChartOfAccountsCode attribute.
     * 
     * @return Returns the accountChartOfAccountsCode.
     */
    public String getAccountChartOfAccountsCode() {
        return accountChartOfAccountsCode;
    }

    /**
     * Sets the accountChartOfAccountsCode attribute value.
     * 
     * @param accountChartOfAccountsCode The accountChartOfAccountsCode to set.
     */
    public void setAccountChartOfAccountsCode(String accountChartOfAccountsCode) {
        this.accountChartOfAccountsCode = accountChartOfAccountsCode;
    }

    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the reportOption attribute.
     * 
     * @return Returns the reportOption.
     */
    public String getReportOption() {
        return reportOption;
    }

    /**
     * Sets the reportOption attribute value.
     * 
     * @param reportOption The reportOption to set.
     */
    public void setReportOption(String reportOption) {
        this.reportOption = reportOption;
    }

    /**
     * Gets the reportRunDate attribute.
     * 
     * @return Returns the reportRunDate.
     */
    public Date getReportRunDate() {
        return reportRunDate;
    }

    /**
     * Sets the reportRunDate attribute value.
     * 
     * @param reportRunDate The reportRunDate to set.
     */
    public void setReportRunDate(Date reportRunDate) {
        this.reportRunDate = reportRunDate;
    }

    /**
     * Gets the unpaidBalance0to30 attribute.
     * 
     * @return Returns the unpaidBalance0to30.
     */
    public KualiDecimal getUnpaidBalance0to30() {
        return unpaidBalance0to30;
    }

    /**
     * Sets the unpaidBalance0to30 attribute value.
     * 
     * @param unpaidBalance0to30 The unpaidBalance0to30 to set.
     */
    public void setUnpaidBalance0to30(KualiDecimal unpaidBalance0to30) {
        this.unpaidBalance0to30 = unpaidBalance0to30;
    }

    /**
     * Gets the unpaidBalance31to60 attribute.
     * 
     * @return Returns the unpaidBalance31to60.
     */
    public KualiDecimal getUnpaidBalance31to60() {
        return unpaidBalance31to60;
    }

    /**
     * Sets the unpaidBalance31to60 attribute value.
     * 
     * @param unpaidBalance31to60 The unpaidBalance31to60 to set.
     */
    public void setUnpaidBalance31to60(KualiDecimal unpaidBalance31to60) {
        this.unpaidBalance31to60 = unpaidBalance31to60;
    }

    /**
     * Gets the unpaidBalance61to90 attribute.
     * 
     * @return Returns the unpaidBalance61to90.
     */
    public KualiDecimal getUnpaidBalance61to90() {
        return unpaidBalance61to90;
    }

    /**
     * Sets the unpaidBalance61to90 attribute value.
     * 
     * @param unpaidBalance61to90 The unpaidBalance61to90 to set.
     */
    public void setUnpaidBalance61to90(KualiDecimal unpaidBalance61to90) {
        this.unpaidBalance61to90 = unpaidBalance61to90;
    }

    /**
     * Gets the unpaidBalance91toSYSPR attribute.
     * 
     * @return Returns the unpaidBalance91toSYSPR.
     */
    public KualiDecimal getUnpaidBalance91toSYSPR() {
        return unpaidBalance91toSYSPR;
    }

    /**
     * Sets the unpaidBalance91toSYSPR attribute value.
     * 
     * @param unpaidBalance91toSYSPR The unpaidBalance91toSYSPR to set.
     */
    public void setUnpaidBalance91toSYSPR(KualiDecimal unpaidBalance91toSYSPR) {
        this.unpaidBalance91toSYSPR = unpaidBalance91toSYSPR;
    }

    /**
     * Gets the unpaidBalanceSYSPRplus1orMore attribute.
     * 
     * @return Returns the unpaidBalanceSYSPRplus1orMore.
     */
    public KualiDecimal getUnpaidBalanceSYSPRplus1orMore() {
        return unpaidBalanceSYSPRplus1orMore;
    }

    /**
     * Sets the unpaidBalanceSYSPRplus1orMore attribute value.
     * 
     * @param unpaidBalanceSYSPRplus1orMore The unpaidBalanceSYSPRplus1orMore to set.
     */
    public void setUnpaidBalanceSYSPRplus1orMore(KualiDecimal unpaidBalanceSYSPRplus1orMore) {
        this.unpaidBalanceSYSPRplus1orMore = unpaidBalanceSYSPRplus1orMore;
    }

    /**
     * Gets the totalOpenInvoices attribute.
     * 
     * @return Returns the totalOpenInvoices.
     */
    public KualiDecimal getTotalOpenInvoices() {
        return totalOpenInvoices;
    }

    /**
     * Sets the totalOpenInvoices attribute value.
     * 
     * @param totalOpenInvoices The totalOpenInvoices to set.
     */
    public void setTotalOpenInvoices(KualiDecimal totalOpenInvoices) {
        this.totalOpenInvoices = totalOpenInvoices;
    }

    /**
     * Gets the totalWriteOff attribute.
     * 
     * @return Returns the totalWriteOff.
     */
    public KualiDecimal getTotalWriteOff() {
        return totalWriteOff;
    }

    /**
     * Sets the totalWriteOff attribute value.
     * 
     * @param totalWriteOff The totalWriteOff to set.
     */
    public void setTotalWriteOff(KualiDecimal totalWriteOff) {
        this.totalWriteOff = totalWriteOff;
    }


}
