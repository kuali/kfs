/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service.impl;

import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.service.AccountPresenceService;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.dao.BalanceDao;

/**
 * Imlementation of AccountPresenceService
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class AccountPresenceServiceImpl implements AccountPresenceService {
    private BalanceDao balanceDao;

    /**
     * @see org.kuali.module.financial.service.AccountPresenceService#isObjectCodeBudgetedForAccountPresence(org.kuali.module.chart.bo.Account,
     *      org.kuali.module.chart.bo.ObjectCode)
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
     * @return Returns the balanceDao.
     */
    public BalanceDao getBalanceDao() {
        return balanceDao;
    }

    /**
     * @param balanceDao The balanceDao to set.
     */
    public void setBalanceDao(BalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }
}