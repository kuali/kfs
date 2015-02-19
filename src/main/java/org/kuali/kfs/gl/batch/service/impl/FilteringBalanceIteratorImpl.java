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

import java.util.Iterator;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;
import org.kuali.kfs.gl.batch.service.BalancePredicate;
import org.kuali.kfs.gl.batch.service.FilteringBalanceIterator;
import org.kuali.kfs.gl.businessobject.Balance;

/**
 * An implementation of FilteringBalanceIterator that only selects balances that should be selected, according to the predicate that
 * has been injected in
 */
public class FilteringBalanceIteratorImpl implements FilteringBalanceIterator {
    private FilterIterator filteredBalances;
    private Iterator<Balance> balancesSource;
    private BalancePredicate balancePredicate;
    private boolean initialized = false;

    /**
     * Are there any more balances left in this iterator?
     * 
     * @return true if there are more balances, false otherwise
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        if (!initialized) {
            initialize();
        }
        return filteredBalances.hasNext();
    }

    /**
     * Retrieves the next balance in the iterator
     * 
     * @return the next balance in the iterator
     * @see java.util.Iterator#next()
     */
    public Balance next() {
        if (!initialized) {
            initialize();
        }
        return (Balance) filteredBalances.next();
    }

    /**
     * Removes the next balance in the iterator.
     * 
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        if (!initialized) {
            initialize();
        }
        filteredBalances.remove();
    }

    /**
     * A convenience method to get the enhanced for loops to work with this thing, this simply returns this iterator
     * 
     * @return an iterator of Balances...which is this Iterator
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<Balance> iterator() {
        return this;
    }

    /**
     * Creates the FilterIterator that underlies this Iterator
     */
    protected void initialize() {
        if (!initialized) {
            filteredBalances = new FilterIterator(balancesSource, new Predicate() {
                public boolean evaluate(Object obj) {
                    return balancePredicate.select((Balance) obj);
                }
            });
            initialized = true;
        }
    }

    /**
     * Sets the balancePredicate attribute value.
     * 
     * @param balancePredicate The balancePredicate to set.
     */
    public void setBalancePredicate(BalancePredicate balancePredicate) {
        this.balancePredicate = balancePredicate;
    }

    /**
     * Sets the balancesSource attribute value.
     * 
     * @param balancesSource The balancesSource to set.
     */
    public void setBalancesSource(Iterator<Balance> balancesSource) {
        this.balancesSource = balancesSource;
    }

}
