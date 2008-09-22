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
package org.kuali.kfs.pdp.document.web.struts;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.rice.kns.web.struts.form.KualiForm;

public class PaymentDetailForm extends KualiForm {

    private Map<String, String> payees;
    private PaymentDetail paymentDetail;
    private String payeeIdTypeDesc;
    private String alternatePayeeIdTypeDesc;
    private Integer disbNbrTotalPayments;
    private List<PaymentDetail> disbursementDetailsList;
    private String btnPressed;
    private Integer size;
    private String listType;
    private String lookupLink;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getBtnPressed() {
        return btnPressed;
    }

    public void setBtnPressed(String btnPressed) {
        this.btnPressed = btnPressed;
    }

    public Map<String, String> getPayees() {
        return payees;
    }

    public void setPayees(Map<String, String> payees) {
        this.payees = payees;
    }

    public PaymentDetail getPaymentDetail() {
        return paymentDetail;
    }

    public void setPaymentDetail(PaymentDetail paymentDetail) {
        this.paymentDetail = paymentDetail;
    }

    public String getAlternatePayeeIdTypeDesc() {
        return alternatePayeeIdTypeDesc;
    }

    public void setAlternatePayeeIdTypeDesc(String alternatePayeeIdTypeDesc) {
        this.alternatePayeeIdTypeDesc = alternatePayeeIdTypeDesc;
    }

    public String getPayeeIdTypeDesc() {
        return payeeIdTypeDesc;
    }

    public void setPayeeIdTypeDesc(String payeeIdTypeDesc) {
        this.payeeIdTypeDesc = payeeIdTypeDesc;
    }

    public List<PaymentDetail> getDisbursementDetailsList() {
        return disbursementDetailsList;
    }

    public void setDisbursementDetailsList(List<PaymentDetail> disbursementDetailsList) {
        this.disbursementDetailsList = disbursementDetailsList;
    }

    public Integer getDisbNbrTotalPayments() {
        return disbNbrTotalPayments;
    }

    public void setDisbNbrTotalPayments(Integer disbNbrTotalPayments) {
        this.disbNbrTotalPayments = disbNbrTotalPayments;
    }

    public String getLookupLink() {
        return lookupLink;
    }

    public void setLookupLink(String lookupLink) {
        this.lookupLink = lookupLink;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

}
