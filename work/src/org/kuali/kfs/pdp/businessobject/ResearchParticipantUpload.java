/*
 * Copyright 2011 The Kuali Foundation.
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
