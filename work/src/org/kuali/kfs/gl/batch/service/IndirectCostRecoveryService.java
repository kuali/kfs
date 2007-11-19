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
package org.kuali.module.gl.service;

import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;

/**
 * An interface with a predicate that tells if a transaction with the given fields would be an ICR transaction or not
 */
public interface IcrTransaction {
    /**
     * Determines if a transaction with the given parameters would be subject to indirect cost recovery
     * 
     * @param objectType the object type of the transaction
     * @param account the account of the transaction
     * @param subAccountNumber the sub account number of the transaction
     * @param objectCode the financial object code of the transaction
     * @param universityFiscalPeriodCode the period code of the transactions
     * @return true if these all mean the transaction is an indirect cost recovery transaction, false otherwise
     */
    public boolean isIcrTransaction(ObjectType objectType, Account account, String subAccountNumber, ObjectCode objectCode, String universityFiscalPeriodCode);
}
