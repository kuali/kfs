/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
