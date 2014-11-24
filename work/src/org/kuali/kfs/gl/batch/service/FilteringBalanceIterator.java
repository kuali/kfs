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
package org.kuali.kfs.gl.batch.service;

import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.Balance;

/**
 * An extension of the Iterator interface that allows for balances to be selected or not
 * 
 * @see org.kuali.kfs.gl.batch.service.BalancePredicate
 */
public interface FilteringBalanceIterator extends Iterator, Iterable {

    /**
     * Set the balance predicate that should be used to filter this iterator
     * 
     * @param balancePredicate the BalancePredicate with the logic to use for this filtering balance iterator
     * @see org.kuali.kfs.gl.batch.service.BalancePredicate
     */
    public void setBalancePredicate(BalancePredicate balancePredicate);

    /**
     * Sets the source iterator of balances
     * 
     * @param balancesSource an iterator chuck full of balances to either process or not
     */
    public void setBalancesSource(Iterator<Balance> balancesSource);
}
