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
package org.kuali.kfs.pdp.util;

import org.kuali.rice.kim.api.identity.Person;

/**
 * PDP PaymentDetail Question Callback defines a callback method for post processing handling in the question interface.
 */
public interface PdpPaymentDetailQuestionCallback {

    /**
     * Hooks for performing different actions on payment detail after a question has been performed.
     * 
     * @param paymentDetailId the id of the payment
     * @param note a note from the user
     * @param user the user that perfoms the action
     * @return true if succesful, false otherwise
     */
    public boolean doPostQuestion(int paymentDetailId, String note, Person user);
}

