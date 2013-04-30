/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.sql.Date;

import org.kuali.kfs.sys.businessobject.PaymentDocumentationLocation;
import org.kuali.kfs.sys.businessobject.options.PaymentDocumentationLocationValuesFinder;
import org.kuali.kfs.sys.businessobject.options.PaymentMethodValuesFinder;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * BusinessObject which holds fields representing those a travel document needs to make a payment
 */
public class TravelPayment extends PersistableBusinessObjectBase {
    private String documentNumber;
    private KualiDecimal checkTotalAmount;
    private String contactPersonName;
    private String contactPhoneNumber;
    private boolean attachmentCode;
    private boolean specialHandlingCode;
    private String checkStubText;
    protected String documentationLocationCode;
    protected Date dueDate;
    protected String paymentMethodCode;
    private boolean immediatePaymentIndicator = false;
    private Date extractDate;
    private Date paidDate;
    private Date cancelDate;
    private boolean alienPaymentCode;
    private String payeeLine1Addr;
    private String payeeLine2Addr;
    private String payeeCityName;
    private String payeeStateCode;
    private String payeeZipCode;
    private String payeeCountryCode;
    private String specialHandlingPersonName;
    private String specialHandlingLine1Addr;
    private String specialHandlingLine2Addr;
    private String specialHandlingCityName;
    private String specialHandlingStateCode;
    private String specialHandlingZipCode;
    private String specialHandlingCountryCode;


    protected boolean editW9W8BENbox = false;
    protected boolean payeeW9CompleteCode;
    private String payeeTypeCode;
    protected boolean exceptionAttachedIndicator;


    private PaymentDocumentationLocation paymentDocumentationLocation;

    /**
     *
     *
     * @return
     */
    public String getDocumentNumber() {
        return this.documentNumber;
    }
    /**
     *
     *
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }
    /**
     *
     *
     * @return
     */
    public KualiDecimal getCheckTotalAmount() {
        return checkTotalAmount;
    }
    /**
     *
     *
     * @param disbVchrCheckTotalAmount
     */
    public void setCheckTotalAmount(KualiDecimal disbVchrCheckTotalAmount) {
        this.checkTotalAmount = disbVchrCheckTotalAmount;
    }
    /**
     *
     *
     * @return
     */
    public String getContactPersonName() {
        return contactPersonName;
    }
    /**
     *
     *
     * @param disbVchrContactPersonName
     */
    public void setContactPersonName(String disbVchrContactPersonName) {
        this.contactPersonName = disbVchrContactPersonName;
    }
    /**
     *
     *
     * @return
     */
    public String getContactPhoneNumber() {
        return contactPhoneNumber;
    }
    /**
     *
     *
     * @param disbVchrContactPhoneNumber
     */
    public void setContactPhoneNumber(String disbVchrContactPhoneNumber) {
        this.contactPhoneNumber = disbVchrContactPhoneNumber;
    }
    /**
     *
     *
     * @return
     */
    public boolean isAttachmentCode() {
        return attachmentCode;
    }
    /**
     *
     *
     * @param disbVchrAttachmentCode
     */
    public void setAttachmentCode(boolean disbVchrAttachmentCode) {
        this.attachmentCode = disbVchrAttachmentCode;
    }
    /**
     *
     *
     * @return
     */
    public boolean isSpecialHandlingCode() {
        return specialHandlingCode;
    }
    /**
     *
     *
     * @param disbVchrSpecialHandlingCode
     */
    public void setSpecialHandlingCode(boolean disbVchrSpecialHandlingCode) {
        this.specialHandlingCode = disbVchrSpecialHandlingCode;
    }
    /**
     *
     *
     * @return
     */
    public String getPayeeLine1Addr() {
        return payeeLine1Addr;
    }
    /**
     *
     *
     * @param disbVchrPayeeLine1Addr
     */
    public void setPayeeLine1Addr(String disbVchrPayeeLine1Addr) {
        this.payeeLine1Addr = disbVchrPayeeLine1Addr;
    }
    /**
     *
     *
     * @return
     */
    public String getPayeeLine2Addr() {
        return payeeLine2Addr;
    }
    /**
     *
     *
     * @param disbVchrPayeeLine2Addr
     */
    public void setPayeeLine2Addr(String disbVchrPayeeLine2Addr) {
        this.payeeLine2Addr = disbVchrPayeeLine2Addr;
    }
    /**
     *
     *
     * @return
     */
    public String getPayeeCityName() {
        return payeeCityName;
    }
    /**
     *
     *
     * @param disbVchrPayeeCityName
     */
    public void setPayeeCityName(String disbVchrPayeeCityName) {
        this.payeeCityName = disbVchrPayeeCityName;
    }
    /**
     *
     *
     * @return
     */
    public String getPayeeStateCode() {
        return payeeStateCode;
    }
    /**
     *
     *
     * @param disbVchrPayeeStateCode
     */
    public void setPayeeStateCode(String disbVchrPayeeStateCode) {
        this.payeeStateCode = disbVchrPayeeStateCode;
    }
    /**
     *
     *
     * @return
     */
    public String getPayeeZipCode() {
        return payeeZipCode;
    }
    /**
     *
     *
     * @param disbVchrPayeeZipCode
     */
    public void setPayeeZipCode(String disbVchrPayeeZipCode) {
        this.payeeZipCode = disbVchrPayeeZipCode;
    }
    /**
     *
     *
     * @return
     */
    public String getPayeeCountryCode() {
        return payeeCountryCode;
    }
    /**
     *
     *
     * @param disbVchrPayeeCountryCode
     */
    public void setPayeeCountryCode(String disbVchrPayeeCountryCode) {
        this.payeeCountryCode = disbVchrPayeeCountryCode;
    }
    /**
     *
     *
     * @return
     */
    public String getCheckStubText() {
        return checkStubText;
    }
    /**
     *
     *
     * @param disbVchrCheckStubText
     */
    public void setCheckStubText(String disbVchrCheckStubText) {
        this.checkStubText = disbVchrCheckStubText;
    }
    /**
     *
     *
     * @return
     */
    public boolean isImmediatePaymentIndicator() {
        return immediatePaymentIndicator;
    }
    /**
     *
     *
     * @param immediatePaymentIndicator
     */
    public void setImmediatePaymentIndicator(boolean immediatePaymentIndicator) {
        this.immediatePaymentIndicator = immediatePaymentIndicator;
    }
    /**
     *
     *
     * @return
     */
    public Date getExtractDate() {
        return extractDate;
    }
    /**
     *
     *
     * @param extractDate
     */
    public void setExtractDate(Date extractDate) {
        this.extractDate = extractDate;
    }
    /**
     *
     *
     * @return
     */
    public Date getPaidDate() {
        return paidDate;
    }
    /**
     *
     *
     * @param paidDate
     */
    public void setPaidDate(Date paidDate) {
        this.paidDate = paidDate;
    }
    /**
     *
     *
     * @return
     */
    public Date getCancelDate() {
        return cancelDate;
    }
    /**
     *
     *
     * @param cancelDate
     */
    public void setCancelDate(Date cancelDate) {
        this.cancelDate = cancelDate;
    }
    /**
     *
     *
     * @return
     */
    public boolean getAlienPaymentCode() {
        return alienPaymentCode;
    }
    /**
     *
     *
     * @param disbVchrAlienPaymentCode
     */
    public void setAlienPaymentCode(boolean disbVchrAlienPaymentCode) {
        this.alienPaymentCode = disbVchrAlienPaymentCode;
    }
    /**
     *
     *
     * @return
     */
    public String getSpecialHandlingPersonName() {
        return specialHandlingPersonName;
    }
    /**
     *
     *
     * @param disbVchrSpecialHandlingPersonName
     */
    public void setSpecialHandlingPersonName(String disbVchrSpecialHandlingPersonName) {
        this.specialHandlingPersonName = disbVchrSpecialHandlingPersonName;
    }
    /**
     *
     *
     * @return
     */
    public String getSpecialHandlingLine1Addr() {
        return specialHandlingLine1Addr;
    }
    /**
     *
     *
     * @param disbVchrSpecialHandlingLine1Addr
     */
    public void setSpecialHandlingLine1Addr(String disbVchrSpecialHandlingLine1Addr) {
        this.specialHandlingLine1Addr = disbVchrSpecialHandlingLine1Addr;
    }
    /**
     *
     *
     * @return
     */
    public String getSpecialHandlingLine2Addr() {
        return specialHandlingLine2Addr;
    }
    /**
     *
     *
     * @param disbVchrSpecialHandlingLine2Addr
     */
    public void setSpecialHandlingLine2Addr(String disbVchrSpecialHandlingLine2Addr) {
        this.specialHandlingLine2Addr = disbVchrSpecialHandlingLine2Addr;
    }
    /**
     *
     *
     * @return
     */
    public String getSpecialHandlingCityName() {
        return specialHandlingCityName;
    }
    /**
     *
     *
     * @param disbVchrSpecialHandlingCityName
     */
    public void setSpecialHandlingCityName(String disbVchrSpecialHandlingCityName) {
        this.specialHandlingCityName = disbVchrSpecialHandlingCityName;
    }
    /**
     *
     *
     * @return
     */
    public String getSpecialHandlingStateCode() {
        return specialHandlingStateCode;
    }
    /**
     *
     *
     * @param disbVchrSpecialHandlingStateCode
     */
    public void setSpecialHandlingStateCode(String disbVchrSpecialHandlingStateCode) {
        this.specialHandlingStateCode = disbVchrSpecialHandlingStateCode;
    }
    /**
     *
     *
     * @return
     */
    public String getSpecialHandlingZipCode() {
        return specialHandlingZipCode;
    }
    /**
     *
     *
     * @param disbVchrSpecialHandlingZipCode
     */
    public void setSpecialHandlingZipCode(String disbVchrSpecialHandlingZipCode) {
        this.specialHandlingZipCode = disbVchrSpecialHandlingZipCode;
    }
    /**
     *
     *
     * @return
     */
    public String getSpecialHandlingCountryCode() {
        return specialHandlingCountryCode;
    }
    /**
     *
     *
     * @param disbVchrSpecialHandlingCountryCode
     */
    public void setSpecialHandlingCountryCode(String disbVchrSpecialHandlingCountryCode) {
        this.specialHandlingCountryCode = disbVchrSpecialHandlingCountryCode;
    }
    /**
     *
     *
     * @return
     */
    public String getDocumentationLocationCode() {
        return documentationLocationCode;
    }
    /**
     *
     *
     * @param disbursementVoucherDocumentationLocationCode
     */
    public void setDocumentationLocationCode(String disbursementVoucherDocumentationLocationCode) {
        this.documentationLocationCode = disbursementVoucherDocumentationLocationCode;
    }
    /**
     *
     *
     * @return
     */
    public Date getDueDate() {
        return dueDate;
    }
    /**
     *
     *
     * @param disbursementVoucherDueDate
     */
    public void setDueDate(Date disbursementVoucherDueDate) {
        this.dueDate = disbursementVoucherDueDate;
    }
    /**
     *
     *
     * @return
     */
    public String getPaymentMethodCode() {
        return paymentMethodCode;
    }
    /**
     *
     *
     * @param disbVchrPaymentMethodCode
     */
    public void setPaymentMethodCode(String disbVchrPaymentMethodCode) {
        this.paymentMethodCode = disbVchrPaymentMethodCode;
    }
    /**
     *
     *
     * @return
     */
    public PaymentDocumentationLocation getPaymentDocumentationLocation() {
        return paymentDocumentationLocation;
    }
    /**
     *
     *
     * @param paymentDocumentationLocation
     */
    public void setPaymentDocumentationLocation(PaymentDocumentationLocation paymentDocumentationLocation) {
        this.paymentDocumentationLocation = paymentDocumentationLocation;
    }

    /**
     * Returns the name associated with the payment method code
     *
     * @return String the name associated with the payment method code
     */
    public String getPaymentMethodName() {
        return new PaymentMethodValuesFinder().getKeyLabel(this.paymentMethodCode);
    }

    /**
     * Does not set a name of a payment method.  The real name is going to come from a standard location.
     * @param paymentMethodName a payment method name which will be diligently ignored
     */
    public void setPaymentMethodName(String paymentMethodName) {
        // I'm just here to make bean utils happy, not to actually set values.
    }

    /**
     * Returns the name associated with the documentation location name
     *
     * @return String the name associated with the documentation location name
     */
    public String getPaymentDocumentationLocationName() {
        return new PaymentDocumentationLocationValuesFinder().getKeyLabel(this.documentationLocationCode);
    }

    /**
     * Fails to set the documentation location name
     * @param paymentDocumentationLocationName a name of a documentation location which will be blithely tossed away
     */
    public void setPaymentDocumentationLocationName(String paymentDocumentationLocationName) {
        // this setter is here just for the sake of bean utils
    }

    /**
     * Based on which pdp dates are present (extract, paid, canceled), determines a String for the status
     * @return a String representation of the status
     */
    public String getPaymentPdpStatus() {
        if (cancelDate != null) {
            return "Canceled";
        }
        if (paidDate != null) {
            return "Paid";
        }
        if (extractDate != null) {
            return "Extracted";
        }
        return "Pre-Extraction";
    }

    /**
     * Pretends to set the PDP status for this document
     *
     * @param status the status to pretend to set
     */
    public void setPaymentPdpStatus(String status) {
        // don't do nothing, 'cause this ain't a real field
    }
    /**
     *
     *
     * @return
     */
    public boolean isEditW9W8BENbox() {
        return editW9W8BENbox;
    }
    /**
     *
     *
     * @param editW9W8BENbox
     */
    public void setEditW9W8BENbox(boolean editW9W8BENbox) {
        this.editW9W8BENbox = editW9W8BENbox;
    }
    /**
     *
     *
     * @return
     */
    public boolean isPayeeW9CompleteCode() {
        return payeeW9CompleteCode;
    }
    /**
     *
     *
     * @param payeeW9CompleteCode
     */
    public void setPayeeW9CompleteCode(boolean payeeW9CompleteCode) {
        this.payeeW9CompleteCode = payeeW9CompleteCode;
    }
    /**
     *
     *
     * @return
     */
    public String getPayeeTypeCode() {
        return payeeTypeCode;
    }
    /**
     *
     *
     * @param payeeTypeCode
     */
    public void setPayeeTypeCode(String payeeTypeCode) {
        this.payeeTypeCode = payeeTypeCode;
    }
    /**
     *
     *
     * @return
     */
    public boolean isExceptionAttachedIndicator() {
        return exceptionAttachedIndicator;
    }
    /**
     *
     *
     * @param exceptionAttachedIndicator
     */
    public void setExceptionAttachedIndicator(boolean exceptionAttachedIndicator) {
        this.exceptionAttachedIndicator = exceptionAttachedIndicator;
    }
}