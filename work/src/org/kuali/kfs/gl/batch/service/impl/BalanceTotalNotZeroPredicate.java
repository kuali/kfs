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
