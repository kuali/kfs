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
package org.kuali.kfs.module.purap.service;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;

public interface PurapAccountRevisionService {
    /**
     * This method will identify the changes happened to existing payment request accounting lines and update the account change
     * history table. If new lines are added, then new account history lines are added too.
     * 
     * @param paymentRequestItems Items from payment request document
     * @param postingYear Posting year
     * @param postingPeriodCode Posting period code
     */
    void savePaymentRequestAccountRevisions(List<PaymentRequestItem> paymentRequestItems, Integer postingYear, String postingPeriodCode);

    /**
     * This method will identify the changes happened to existing credit memo accounting lines and update the account change history
     * table. If new lines are added, then new account history lines are added too.
     * 
     * @param paymentRequestItems Items from payment request document
     * @param postingYear Posting year
     * @param postingPeriodCode Posting period code
     */
    void saveCreditMemoAccountRevisions(List<CreditMemoItem> creditMemoItems, Integer postingYear, String postingPeriodCode);

    /**
     * This method will negate all existing payment request account line revisions
     * 
     * @param paymentRequestItems Items from payment request document
     * @param postingYear Posting year
     * @param postingPeriodCode Posting period code
     */
    void cancelPaymentRequestAccountRevisions(List<PaymentRequestItem> paymentRequestItems, Integer postingYear, String postingPeriodCode);

    /**
     * This method will negate all existing credit memo account revision lines
     * 
     * @param paymentRequestItems Items from payment request document
     * @param postingYear Posting year
     * @param postingPeriodCode Posting period code
     */
    void cancelCreditMemoAccountRevisions(List<CreditMemoItem> creditMemoItems, Integer postingYear, String postingPeriodCode);
}
