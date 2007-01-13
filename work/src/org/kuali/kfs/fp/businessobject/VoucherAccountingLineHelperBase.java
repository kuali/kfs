/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.bo;

import org.kuali.core.util.KualiDecimal;

/**
 * This helper class works in conjunction with the SourceAccountingLine bo to help build the UI for the Voucher. On the business
 * object side, there is a single attribute that keeps track of this and the amount field is used to keep track of the amount. This
 * helper class sits alongside the typical SourceAccountingLine bo... the synchronization between the two is the guaranteed order.
 * 
 * 
 */
public class VoucherAccountingLineHelperBase implements VoucherAccountingLineHelper {
    private KualiDecimal debit;
    private KualiDecimal credit;

    /**
     * Constructs a <code>{@link VoucherAccountingLineHelperBase}</code> instance.
     */
    public VoucherAccountingLineHelperBase() {
        this.credit = new KualiDecimal(0);
        this.debit = new KualiDecimal(0);
    }

    /**
     * This method retrieves the credit amount.
     * 
     * @return
     */
    public KualiDecimal getCredit() {
        return credit;
    }

    /**
     * This method sets the credit amount.
     * 
     * @param credit
     */
    public void setCredit(KualiDecimal credit) {
        this.credit = credit;
    }

    /**
     * This method retrieves the debit amount.
     * 
     * @return
     */
    public KualiDecimal getDebit() {
        return debit;
    }

    /**
     * This method sets the debit amount.
     * 
     * @param debit
     */
    public void setDebit(KualiDecimal debit) {
        this.debit = debit;
    }
}
