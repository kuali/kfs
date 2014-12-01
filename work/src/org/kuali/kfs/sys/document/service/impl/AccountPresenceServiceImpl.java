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
package org.kuali.kfs.sys.document.service.impl;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.dataaccess.BalanceDao;
import org.kuali.kfs.sys.document.service.AccountPresenceService;
import org.kuali.kfs.sys.service.NonTransactional;

/**
 * 
 * This is the default implementation of the AccountPresenceService interface.
 * 
 */

@NonTransactional
public class AccountPresenceServiceImpl implements AccountPresenceService {
    private BalanceDao balanceDao;

    /**
     * This method determines if an object code has been budgeted for account presence.  
     * 
     * @param account The account to be checked for the presence control flag.
     * @param objectCode The object code being reviewed.
     * @return True if the object code has been budgeted for an account presence, false otherwise.
     * 
     * @see org.kuali.kfs.sys.document.service.AccountPresenceService#isObjectCodeBudgetedForAccountPresence(org.kuali.kfs.coa.businessobject.Account, org.kuali.kfs.coa.businessobject.ObjectCode)
     */
    public boolean isObjectCodeBudgetedForAccountPresence(Account account, ObjectCode objectCode) {
        boolean objectCodeValid = true;

        /*
         * first check if account has presence control turned on, if not no checks need to take place on object code budgeting
         */
        if (account.isFinancialObjectivePrsctrlIndicator()) {
            /*
             * can have budgeting record for object code, it's consolidation code, or object level
             */

            // try to find budget record for object code
            Balance foundBalance = (Balance) balanceDao.getCurrentBudgetForObjectCode(objectCode.getUniversityFiscalYear(), account.getChartOfAccountsCode(), account.getAccountNumber(), objectCode.getFinancialObjectCode());

            // if object code budget not found, try consolidation object code
            if (foundBalance == null) {
                foundBalance = (Balance) balanceDao.getCurrentBudgetForObjectCode(objectCode.getUniversityFiscalYear(), account.getChartOfAccountsCode(), account.getAccountNumber(), objectCode.getFinancialObjectLevel().getConsolidatedObjectCode());

                // if consolidation object code budget not found, try object level
                if (foundBalance == null) {
                    foundBalance = (Balance) balanceDao.getCurrentBudgetForObjectCode(objectCode.getUniversityFiscalYear(), account.getChartOfAccountsCode(), account.getAccountNumber(), objectCode.getFinancialObjectLevelCode());

                    // object not budgeted
                    if (foundBalance == null) {
                        objectCodeValid = false;
                    }
                }
            }
        }

        return objectCodeValid;
    }

    /**
     * Simple getter used to retrieve an instance of the BalanceDao.
     * 
     * @return Returns the balanceDao.
     */
    public BalanceDao getBalanceDao() {
        return balanceDao;
    }

    /**
     * Simple setter used to set the local BalanceDao attribute.
     * 
     * @param balanceDao The balanceDao to set.
     */
    public void setBalanceDao(BalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }
}
