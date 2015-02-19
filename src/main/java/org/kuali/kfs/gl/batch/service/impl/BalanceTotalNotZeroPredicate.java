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
 * A predicate that only allows the selection of balances with a total that isn't zero (those with zero as a total are somewhat
 * pointless to process)
 */
public class BalanceTotalNotZeroPredicate implements BalancePredicate {

    /**
     * Selects only balances whose total (annual accounting line balance + beginning balance + contracts and grants beginning
     * balance) is not zero
     * 
     * @param balance the balance to qualify
     * @return true if the balance total is not zero, false if it is
     * @see org.kuali.kfs.gl.batch.service.BalancePredicate#select(org.kuali.kfs.gl.businessobject.Balance)
     */
    public boolean select(Balance balance) {
        return !balance.getAccountLineAnnualBalanceAmount().add(balance.getBeginningBalanceLineAmount()).add(balance.getContractsGrantsBeginningBalanceAmount()).isZero();
    }

}
