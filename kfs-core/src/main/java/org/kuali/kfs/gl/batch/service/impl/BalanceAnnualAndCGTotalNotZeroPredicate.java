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
package org.kuali.kfs.gl.batch.service.impl;

import org.kuali.kfs.gl.batch.service.BalancePredicate;
import org.kuali.kfs.gl.businessobject.Balance;

/**
 * An implementation of BalancePredicate to only select balances where the annual account line total and contracts and grants total
 * summed are not equal to zero
 */
public class BalanceAnnualAndCGTotalNotZeroPredicate implements BalancePredicate {

    /**
     * Selects balances that where the annual account line balance and contracts and grants beginning balance summed are not zero
     * 
     * @param balance the balance to qualify
     * @returns true if the annual account line balance and contracts and grants balance summed are not zero, false otherwise
     * @see org.kuali.kfs.gl.batch.service.BalancePredicate#select(org.kuali.kfs.gl.businessobject.Balance)
     */
    public boolean select(Balance balance) {
        return !balance.getAccountLineAnnualBalanceAmount().add(balance.getContractsGrantsBeginningBalanceAmount()).isZero();
    }

}
