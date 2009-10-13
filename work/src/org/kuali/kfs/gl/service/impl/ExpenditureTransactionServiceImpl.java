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
package org.kuali.kfs.gl.service.impl;

import org.kuali.kfs.gl.dataaccess.ExpenditureTransactionDao;
import org.kuali.kfs.gl.service.ExpenditureTransactionService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The basic implementation of ExpenditureTransactionService
 */
@Transactional
public class ExpenditureTransactionServiceImpl implements ExpenditureTransactionService {
    private ExpenditureTransactionDao expenditureTransactionDao;

    /**
     * Removes all of the expenditure transactions, which are temporary records for ICR generation
     *
     * @see org.kuali.kfs.gl.service.ExpenditureTransactionService#deleteAllExpenditureTransactions()
     */
    public void deleteAllExpenditureTransactions() {
        expenditureTransactionDao.deleteAllExpenditureTransactions();
    }

    public void setExpenditureTransactionDao(ExpenditureTransactionDao expenditureTransactionDao) {
        this.expenditureTransactionDao = expenditureTransactionDao;
    }
}
