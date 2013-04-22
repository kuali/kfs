/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.pdp.service;

import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.rice.krad.util.MessageMap;

public interface ResearchParticipantPaymentValidationService {

    /**
     * validate the account information provided by research participant payment file
     *
     * @param paymentFile the given payment file
     * @param errorMap the given error map that can hold the error information
     */
    boolean validatePaymentAccount(PaymentFileLoad paymentFile, MessageMap errorMap);

    /**
     * get the account detail provided by the given payment file
     *
     * @param paymentFile the given payment file
     * @return the account detail provided by the given payment file
     */
    PaymentAccountDetail getPaymentAccountDetail(PaymentFileLoad paymentFile);

    public boolean isResearchParticipantPayment(CustomerProfile customer);
}
