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
package org.kuali.kfs.module.ar.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class is an empty derived class 
 */
public class CustomerAgingReportDetail extends TransientBusinessObjectBase {

private String customerName;
private String customerNumber;
private String chartOfAccountsCode;
private String organizationCode;
private String accountNumber;
private KualiDecimal unpaidBalance0to30;
private KualiDecimal unpaidBalance31to60;
private KualiDecimal unpaidBalance61to90;
private KualiDecimal unpaidBalance91toSYSPR;
private KualiDecimal unpaidBalanceSYSPRplus1orMore;

    /**
     * Constructs a CustomerAgingReportDetail.java.
     */
    public CustomerAgingReportDetail() {
        super();
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public KualiDecimal getUnpaidBalance0to30() {
        return unpaidBalance0to30;
    }

    public void setUnpaidBalance0to30(KualiDecimal unpaidBalance0to30) {
        this.unpaidBalance0to30 = unpaidBalance0to30;
    }

    public KualiDecimal getUnpaidBalance31to60() {
        return unpaidBalance31to60;
    }

    public void setUnpaidBalance31to60(KualiDecimal unpaidBalance31to60) {
        this.unpaidBalance31to60 = unpaidBalance31to60;
    }

    public KualiDecimal getUnpaidBalance61to90() {
        return unpaidBalance61to90;
    }

    public void setUnpaidBalance61to90(KualiDecimal unpaidBalance61to90) {
        this.unpaidBalance61to90 = unpaidBalance61to90;
    }

    public KualiDecimal getUnpaidBalance91toSYSPR() {
        return unpaidBalance91toSYSPR;
    }

    public void setUnpaidBalance91toSYSPR(KualiDecimal unpaidBalance91toSYSPR) {
        this.unpaidBalance91toSYSPR = unpaidBalance91toSYSPR;
    }

    public KualiDecimal getUnpaidBalanceSYSPRplus1orMore() {
        return unpaidBalanceSYSPRplus1orMore;
    }

    public void setUnpaidBalanceSYSPRplus1orMore(KualiDecimal unpaidBalanceSYSPRplus1orMore) {
        this.unpaidBalanceSYSPRplus1orMore = unpaidBalanceSYSPRplus1orMore;
    }


}
