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
package org.kuali.kfs.fp.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This helper class works in conjunction with the SourceAccountingLine bo to help build the UI for the Voucher. On the business
 * object side, there is a single attribute that keeps track of this and the amount field is used to keep track of the amount. This
 * helper class sits alongside the typical SourceAccountingLine bo... the synchronization between the two is the guaranteed order.
 */
public class VoucherAccountingLineHelperBase implements VoucherAccountingLineHelper {
    private KualiDecimal debit;
    private KualiDecimal credit;

    /**
     * Constructs a <code>{@link VoucherAccountingLineHelperBase}</code> instance.
     */
    public VoucherAccountingLineHelperBase() {
        this.credit = KualiDecimal.ZERO;
        this.debit = KualiDecimal.ZERO;
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
