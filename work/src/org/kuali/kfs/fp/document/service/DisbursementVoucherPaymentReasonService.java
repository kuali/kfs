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
package org.kuali.kfs.fp.document.service;

import java.util.Collection;

import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.PaymentReasonCode;
import org.kuali.rice.kns.util.MessageList;

/**
 * define a set of service methods related to payment reason code
 */
public interface DisbursementVoucherPaymentReasonService {

    /**
     * determine whether the given payee is qualified for the payment with the given reason code
     * 
     * @param payee the given payee
     * @param paymentReasonCode the givne payment reason code
     * @return true if the given payee is qualified for the payment with the given reason code; otherwise, return false
     */
    public boolean isPayeeQualifiedForPayment(DisbursementPayee payee, String paymentReasonCode);

    /**
     * determine whether the given payee is qualified for the payment with the given reason code and the payee type must be in the
     * given payee type code list.
     * 
     * @param payee the given payee
     * @param paymentReasonCode the givne payment reason code
     * @param payeeTypeCodes the given payee type codes
     * @return true if the given payee is qualified for the payment with the given reason code; otherwise, return false
     */
    public boolean isPayeeQualifiedForPayment(DisbursementPayee payee, String paymentReasonCode, Collection<String> payeeTypeCodes);

    /**
     * determine whether the given payment reason is a non-employee travel payment reason
     * 
     * @param paymentReasonCode the givne payment reason code
     * @return true if the given payment reason is a moving payment reason; otherwise, return false
     */
    public boolean isNonEmployeeTravelPaymentReason(String paymentReasonCode);

    /**
     * determine whether the given payment reason is a moving payment reason
     * 
     * @param paymentReasonCode the givne payment reason code
     * @return true if the given payment reason is a moving payment reason; otherwise, return false
     */
    public boolean isMovingPaymentReason(String paymentReasonCode);

    /**
     * determine whether the given payment reason is a prepaid travel payment reason
     * 
     * @param paymentReasonCode the givne payment reason code
     * @return true if the given payment reason is a prepaid travel payment reason; otherwise, return false
     */
    public boolean isPrepaidTravelPaymentReason(String paymentReasonCode);

    /**
     * determine whether the given payment reason is a research payment reason
     * 
     * @param paymentReasonCode the givne payment reason code
     * @return true if the given payment reason is a research payment reason; otherwise, return false
     */
    public boolean isResearchPaymentReason(String paymentReasonCode);

    /**
     * determine whether the given payment reason is a revolving fund payment reason
     * 
     * @param paymentReasonCode the givne payment reason code
     * @return true if the given payment reason is a revolving fund payment reason; otherwise, return false
     */
    public boolean isRevolvingFundPaymentReason(String paymentReasonCode);
    
    /**
     * determine whether the given payment reason is a decedent compensation payment reason
     * 
     * @param paymentReasonCode the givne payment reason code
     * @return true if the given payment reason is a decedent compensation payment reason; otherwise, return false
     */
    public boolean isDecedentCompensationPaymentReason(String paymentReasonCode);

    /**
     * determine whether the given payment reason is of type that is specified by the given type parameter name. The type parameter
     * must be defined as an application parameter(@see org.kuali.rice.core.api.parameter.Parameter)
     * 
     * @param typeParameterName the given type parameter name
     * @param paymentReasonCode the given reason code
     * @return true if the given payment reason is of type that is specified by typeParameterName; otherwise, false
     */
    public boolean isPaymentReasonOfType(String typeParameterName, String paymentReasonCode);

    /**
     * get the payment limit to research non-vendor employee for research payment reason
     * 
     * @return the payment limit to research non-vendor employee for research payment reason.
     */
    public String getReserchNonVendorPayLimit();

    /**
     * get the payee type codes valid for the given payment reason
     * 
     * @param paymentReasonCode the given payment reason
     * @return the payee type codes valid for the given payment reason
     */
    public Collection<String> getPayeeTypesByPaymentReason(String paymentReasonCode);

    /**
     * get the payment reason with its primary key: the given payment reason code
     * 
     * @param paymentReasonCode the given payment reason
     * @return the payment reason with its primary key: the given payment reason code
     */
    public PaymentReasonCode getPaymentReasonByPrimaryId(String paymentReasonCode);

    /**
     * post the usage of the given payment reason code into error map
     * 
     * @param paymentReasonCode the given payment reason code
     * @param messageList the message list that will hold the usage of the given payment reason
     */
    public void postPaymentReasonCodeUsage(String paymentReasonCode, MessageList messageList);

    /**
     * determine whether the given payment reason is required for tax review
     * 
     * @param paymentReasonCode the given payment reason
     * @return true if the given payment reason is required for tax review; otherwise, false
     */
    public boolean isTaxReviewRequired(String paymentReasonCode);

    /**
     * get the vendor owership type codes for the given payment reason code
     * 
     * @param paymentReasonCode the given payment reason code
     * @return the vendor owership type codes for the given payment reason code if any; otherwise, null
     */
    public Collection<String> getVendorOwnershipTypesByPaymentReason(String paymentReasonCode);
}
