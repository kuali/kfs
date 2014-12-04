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
