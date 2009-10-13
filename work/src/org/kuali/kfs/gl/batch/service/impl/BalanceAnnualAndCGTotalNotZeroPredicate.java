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
