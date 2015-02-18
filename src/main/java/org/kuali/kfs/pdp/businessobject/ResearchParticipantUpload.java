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
package org.kuali.kfs.pdp.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/*
 * This is a simple java bean class created for
 * Research Participant Upload. It
 * represents the entire spreadsheet file
 * from the Accounting Office which contains all
 * the information for the payment in the format
 * that have been agreed upon to be uploaded to PDP.
 */
public class ResearchParticipantUpload {

    PaymentHeader paymentHeader;
    PaymentAccountDetail paymentAccountDetail;
    List<ResearchParticipantPaymentDetail> paymentDetails;
    String genericDescription;

    public ResearchParticipantUpload() {
        this.paymentDetails = new ArrayList<ResearchParticipantPaymentDetail>();
    }
    public PaymentHeader getPaymentHeader() {
        return paymentHeader;
    }
    public void setPaymentHeader(PaymentHeader paymentHeader) {
        this.paymentHeader = paymentHeader;
    }
    public PaymentAccountDetail getPaymentAccountDetail() {
        return paymentAccountDetail;
    }
    public void setPaymentAccountDetail(PaymentAccountDetail paymentAccountDetail) {
        this.paymentAccountDetail = paymentAccountDetail;
    }
    public String getGenericDescription() {
        return genericDescription;
    }
    public void setGenericDescription(String genericDescription) {
        this.genericDescription = genericDescription;
    }
    public List<ResearchParticipantPaymentDetail> getPaymentDetails() {
        return paymentDetails;
    }
    public void setPaymentDetails(List<ResearchParticipantPaymentDetail> paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public void addPaymentDetail(ResearchParticipantPaymentDetail detail) {
        this.getPaymentDetails().add(detail);
    }

    public KualiDecimal getPaymentTotalAmount() {
        KualiDecimal total = new KualiDecimal(0);
        for (ResearchParticipantPaymentDetail detail : paymentDetails) {
            total = total.add(detail.getAmount());
        }
        return total;
    }

}
