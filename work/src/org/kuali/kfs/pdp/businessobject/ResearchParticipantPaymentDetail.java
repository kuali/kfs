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

import org.kuali.rice.core.api.util.type.KualiDecimal;

/*
 * This is a simple java bean class created for
 * Research Participant Upload. It
 * represents the entire Payment Detail row, which is
 * part of the format that will appear in the spreadsheet
 * from the Accounting Office.
 */

public class ResearchParticipantPaymentDetail {

    private String payeeName;
    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String city;
    private String state;
    private String zip;
    private String checkStubText;
    private KualiDecimal amount;

    public String getPayeeName() {
        return payeeName;
    }
    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }
    public String getAddressLine1() {
        return addressLine1;
    }
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    public String getAddressLine2() {
        return addressLine2;
    }
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    public String getAddressLine3() {
        return addressLine3;
    }
    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public String getZip() {
        return zip;
    }
    public void setZip(String zip) {
        this.zip = zip;
    }
    public String getCheckStubText() {
        return checkStubText;
    }
    public void setCheckStubText(String checkStubText) {
        this.checkStubText = checkStubText;
    }
    public KualiDecimal getAmount() {
        return amount;
    }
    public void setAmount(KualiDecimal amount) {
        this.amount = amount;
    }


}
