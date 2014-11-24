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

import java.io.Serializable;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Interface to aid voucher documents for handling debit/credit amounts.
 */
public interface VoucherAccountingLineHelper extends Serializable {
    /**
     * This method retrieves the credit amount.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getCredit();

    /**
     * This method sets the credit amount.
     * 
     * @param credit
     */
    public void setCredit(KualiDecimal credit);

    /**
     * This method retrieves the debit amount.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getDebit();

    /**
     * This method sets the debit amount.
     * 
     * @param debit
     */
    public void setDebit(KualiDecimal debit);
}
