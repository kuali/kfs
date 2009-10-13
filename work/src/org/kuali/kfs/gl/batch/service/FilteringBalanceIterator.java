/*
 * Copyright 2007 The Kuali Foundation
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
