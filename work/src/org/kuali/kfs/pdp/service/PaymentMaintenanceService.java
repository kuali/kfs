/*
 * Copyright 2007 The Kuali Foundation
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
/*
 * Created on Aug 12, 2004
 */
package org.kuali.kfs.pdp.service;

import org.kuali.rice.kim.api.identity.Person;

/**
 * This class has methods for payment maintenance.
 */
public interface PaymentMaintenanceService {

    /**
     * This method cancels the pending payment of the given payment id if the following rules apply. -
     * Payment status must be: "open", "held", or "pending/ACH".
     * @param paymentGroupId Primary key of the PaymentGroup that the Payment Detail to be canceled belongs to.
     * @param paymentDetailId Primary key of the PaymentDetail that was actually canceled.
     * @param note Change note text entered by user.
     * @param user The user that cancels the payment
     * @return true if cancel payment succesful, false otherwise
     */
    public boolean cancelPendingPayment(Integer paymentGroupId, Integer paymentDetailId, String note, Person user);

    /**
     * This method holds pending payment of the given payment id if the following rules apply. - Payment status
     * must be: "open".
     * @param paymentGroupId Primary key of the PaymentGroup that the Payment Detail to be held belongs to.
     * @param note Change note text entered by user.
     * @param user The user that holds the payment
     */
    public boolean holdPendingPayment(Integer paymentGroupId, String note, Person user);

    /**
     * This method removes holds on pending payments of the given payment id if the following rules
     * apply. - Payment status must be: "held".
     * 
     * @param paymentGroupId Primary key of the PaymentGroup that the Payment Detail to be un-held belongs to
     * @param note  Change note text entered by user.
     * @param user the user that removes hold on payment
     */
    public boolean removeHoldPendingPayment(Integer paymentGroupId, String note, Person user);

    /**
     * This method cancels all disbursements with the same disbursment number as that of the given payment id
     * if the following rules apply. - Payment status must be: "extr".
     * @param paymentGroupId Primary key of the PaymentGroup that the Payment Detail to be cancelled belongs to.
     * @param paymentDetailId Primary key of the PaymentDetail that was actually cancelled.
     * @param note Change note text entered by user.
     * @param user The user that cancels the disbursement
     */
    public boolean cancelDisbursement(Integer paymentGroupId, Integer paymentDetailId, String note, Person user);

    /**
     * This method re-opens all disbursements with the same disbursment number as that of
     * the given payment id if the following rules apply. - Payment status must be: "cdis".
     * @param paymentGroupId Primary key of the PaymentGroup that the Payment Detail to be canceled/reissued belongs to.
     * @param changeText Change note text entered by user.
     * @param user The user that cancels/reissues disbursement
     */
    public boolean reissueDisbursement(Integer paymentGroupId, String changeText, Person user);

    /**
     * This method cancels and re-opens all disbursements with the same disbursment number as that of
     * the given payment id if the following rules apply. - Payment status must be: "extr".
     * @param paymentGroupId Primary key of the PaymentGroup that the Payment Detail to be canceled/reissued belongs to.
     * @param changeText Change note text entered by user.
     * @param user The user that cancels/reissues disbursement
     */
    public boolean cancelReissueDisbursement(Integer paymentGroupId, String changeText, Person user);

    /**
     * This method changes the immediate flag
     * @param paymentGroupId the payment group id
     * @param changeText the change text
     * @param user the user that changes the immediate flag
     */
    public void changeImmediateFlag(Integer paymentGroupId, String changeText, Person user);
}

