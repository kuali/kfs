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

package org.kuali.module.kra.budget.service.impl;

import java.util.Collection;
import java.util.Iterator;

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetGraduateAssistantRate;
import org.kuali.module.kra.budget.bo.GraduateAssistantRate;
import org.kuali.module.kra.budget.dao.BudgetGraduateAssistantRateDao;
import org.kuali.module.kra.budget.dao.GraduateAssistantRateDao;
import org.kuali.module.kra.budget.service.BudgetGraduateAssistantRateService;

/**
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 * 
 */
public class BudgetGraduateAssistantRateServiceImpl implements BudgetGraduateAssistantRateService {

    private GraduateAssistantRateDao graduateAssistantRateDao;
    private BudgetGraduateAssistantRateDao budgetGraduateAssistantRateDao;


    public GraduateAssistantRateDao getGraduateAssistantRateDao() {
        return graduateAssistantRateDao;
    }

    public void setGraduateAssistantRateDao(GraduateAssistantRateDao graduateAssistantRateDao) {
        this.graduateAssistantRateDao = graduateAssistantRateDao;
    }

    public BudgetGraduateAssistantRateDao getBudgetGraduateAssistantRateDao() {
        return budgetGraduateAssistantRateDao;
    }

    public void setBudgetGraduateAssistantRateDao(BudgetGraduateAssistantRateDao budgetGraduateAssistantRateDao) {
        this.budgetGraduateAssistantRateDao = budgetGraduateAssistantRateDao;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.kra.budget.service.BudgetGraduateAssistantRateService#getAllGraduateAssistantRates()
     */
    public Collection getAllGraduateAssistantRates() {
        return graduateAssistantRateDao.getAllGraduateAssistantRates();
    }

    public boolean isValidGraduateAssistantRate(KualiDecimal fringeRate) {
        return fringeRate == null ? false : fringeRate.isLessEqual(Constants.GRADUATE_ASSISTANT_RATE_MAX);
    }

    /**
     * This method...
     * 
     * @param budgetForm
     */
    public void setupDefaultGradAssistantRates(Budget budget) {
        for (Iterator iter = getAllGraduateAssistantRates().iterator(); iter.hasNext();) {
            GraduateAssistantRate graduateAssistantRate = (GraduateAssistantRate) iter.next();
            BudgetGraduateAssistantRate budgetGraduateAssistantRate = new BudgetGraduateAssistantRate(budget.getDocumentHeaderId(), graduateAssistantRate.getCampusCode(), graduateAssistantRate.getCampusMaximumPeriod1Rate(), graduateAssistantRate.getCampusMaximumPeriod2Rate(), graduateAssistantRate.getCampusMaximumPeriod3Rate(), graduateAssistantRate.getCampusMaximumPeriod4Rate(), graduateAssistantRate.getCampusMaximumPeriod5Rate(), graduateAssistantRate.getCampusMaximumPeriod6Rate(), graduateAssistantRate);
            budget.getGraduateAssistantRates().add(budgetGraduateAssistantRate);
        }
    }
}