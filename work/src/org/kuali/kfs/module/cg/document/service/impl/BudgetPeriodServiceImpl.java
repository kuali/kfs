/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */
package org.kuali.module.kra.budget.service.impl;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.dao.BudgetPeriodDao;
import org.kuali.module.kra.budget.service.BudgetPeriodService;

/**
 * 
 * This class...
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetPeriodServiceImpl implements BudgetPeriodService {

    private DateTimeService dateTimeService;
    private BudgetPeriodDao budgetPeriodDao;

    /**
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * @return Returns the budgetPeriodDao.
     */
    public BudgetPeriodDao getBudgetPeriodDao() {
        return budgetPeriodDao;
    }

    /**
     * @param budgetPeriodDao The budgetPeriodDao to set.
     */
    public void setBudgetPeriodDao(BudgetPeriodDao budgetPeriodDao) {
        this.budgetPeriodDao = budgetPeriodDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.service.BudgetPeriodService#getBudgetPeriod(java.lang.Long, java.lang.Integer)
     */
    public BudgetPeriod getBudgetPeriod(String documentHeaderId, Integer budgetPeriodSequenceNumber) {
        return budgetPeriodDao.getBudgetPeriod(documentHeaderId, budgetPeriodSequenceNumber);
    }

    public BudgetPeriod getFirstBudgetPeriod(String documentHeaderId) {
        List budgetPeriodList = budgetPeriodDao.getBudgetPeriodList(documentHeaderId);
        return (BudgetPeriod) budgetPeriodList.get(0);
    }

    public int getPeriodIndex(Integer budgetPeriodSequenceNumber, List budgetPeriodList) {
        int periodIndexNumber = -1;
        Iterator budgetPeriodListIter = budgetPeriodList.iterator();

        for (int i = 0; budgetPeriodListIter.hasNext(); i++) {
            BudgetPeriod budgetPeriod = (BudgetPeriod) budgetPeriodListIter.next();

            if (budgetPeriod.getBudgetPeriodSequenceNumber().equals(budgetPeriodSequenceNumber)) {
                periodIndexNumber = i;
                break;
            }
        }

        return periodIndexNumber;
    }

    public int getPeriodsRange(Integer budgetPeriodSequenceNumberA, Integer budgetPeriodSequenceNumberB, List budgetPeriodList) {
        int periodIndexNumberA = getPeriodIndex(budgetPeriodSequenceNumberA, budgetPeriodList);
        int periodIndexNumberB = getPeriodIndex(budgetPeriodSequenceNumberB, budgetPeriodList);

        int periodsRange = -1;

        if (periodIndexNumberA != -1 || periodIndexNumberB != -1) {
            periodsRange = periodIndexNumberB - periodIndexNumberA;
        }

        return periodsRange;
    }

    public BudgetPeriod getPeriodAfterOffset(Integer budgetPeriodSequenceNumber, int offset, List budgetPeriodList) {
        BudgetPeriod period = (BudgetPeriod) budgetPeriodList.get(getPeriodIndex(budgetPeriodSequenceNumber, budgetPeriodList) + offset);

        return period;
    }
}
