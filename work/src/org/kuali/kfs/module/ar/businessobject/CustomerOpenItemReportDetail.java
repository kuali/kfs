/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class CustomerOpenItemReportDetail extends TransientBusinessObjectBase {
    
    private String documentType;
    private String documentNumber;
    private String documentDescription;
    private Date billingDate;
    private Date dueApprovedDate;
    private KualiDecimal documentPaymentAmount;
    private KualiDecimal unpaidUnappliedAmount;

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }
    
    public Date getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(java.util.Date date) {
        this.billingDate = date;
    }

    public Date getDueApprovedDate() {
        return dueApprovedDate;
    }

    public void setDueApprovedDate(Date approvedDate) {
        this.dueApprovedDate = approvedDate;
    }

    public KualiDecimal getDocumentPaymentAmount() {
        return documentPaymentAmount;
    }

    public void setDocumentPaymentAmount(KualiDecimal documentPaymentAmount) {
        this.documentPaymentAmount = documentPaymentAmount;
    }

    public KualiDecimal getUnpaidUnappliedAmount() {
        return unpaidUnappliedAmount;
    }

    public void setUnpaidUnappliedAmount(KualiDecimal unpaidUnappliedAmount) {
        this.unpaidUnappliedAmount = unpaidUnappliedAmount;
    }
}
