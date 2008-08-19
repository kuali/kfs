/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.kfs.module.purap.businessobject;

import java.sql.Timestamp;

import org.kuali.kfs.module.purap.util.PurApObjectUtils;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * Payment Request Account History Business Object.
 */
public class PaymentRequestAccountHistory extends PaymentRequestAccount {

    protected Integer accountHistoryIdentifier;
    private Timestamp accountHistoryTimestamp;

    private PaymentRequestAccount paymentRequestAccount;
    
    /**
     * Default constructor.
     */
    public PaymentRequestAccountHistory() {
        
    }

    /**
     * Constructor.
     * 
     * @param account - payment request account
     */
    public PaymentRequestAccountHistory(PaymentRequestAccount pra) {
        // copy base attributes
        PurApObjectUtils.populateFromBaseClass(PurApAccountingLineBase.class, pra, this);
        PurApObjectUtils.populateFromBaseClass(AccountingLineBase.class, pra, this);
        this.setAccountHistoryTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
    }

    public Integer getAccountHistoryIdentifier() {
        return accountHistoryIdentifier;
    }

    public void setAccountHistoryIdentifier(Integer accountHistoryIdentifier) {
        this.accountHistoryIdentifier = accountHistoryIdentifier;
    }

    public Timestamp getAccountHistoryTimestamp() {
        return accountHistoryTimestamp;
    }

    public void setAccountHistoryTimestamp(Timestamp accountHistoryTimestamp) {
        this.accountHistoryTimestamp = accountHistoryTimestamp;
    }

    public PaymentRequestAccount getPaymentRequestAccount() {
        return paymentRequestAccount;
    }

    @Deprecated
    public void setPaymentRequestAccount(PaymentRequestAccount paymentRequestAccount) {
        this.paymentRequestAccount = paymentRequestAccount;
    }


}
