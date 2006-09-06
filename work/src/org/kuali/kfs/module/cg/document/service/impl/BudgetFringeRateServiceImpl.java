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

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.kra.budget.bo.AppointmentType;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetFringeRate;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.dao.AppointmentTypeDao;
import org.kuali.module.kra.budget.dao.BudgetFringeRateDao;
import org.kuali.module.kra.budget.service.BudgetFringeRateService;

/**
 * This class is the service implementation for the Account structure. This is the default, Kuali provided implementation.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetFringeRateServiceImpl implements BudgetFringeRateService {
    private AppointmentTypeDao appointmentTypeDao;
    private BudgetFringeRateDao budgetFringeRateDao;
    private KualiConfigurationService kualiConfigurationService;

    public BudgetFringeRate getBudgetFringeRate(String documentHeaderId, String universityAppointmentTypeCode) {
        BudgetFringeRate budgetFringeRate = budgetFringeRateDao.getBudgetFringeRate(documentHeaderId, universityAppointmentTypeCode);
        if (budgetFringeRate == null) {
            AppointmentType appointmentType = appointmentTypeDao.getAppointmentType(universityAppointmentTypeCode);
            budgetFringeRate = new BudgetFringeRate(documentHeaderId, appointmentType);
        }
        return budgetFringeRate;
    }

    /**
     * @param accountDao The accountDao to set.
     */
    public Collection getDefaultFringeRates() {
        return appointmentTypeDao.getAll();
    }

    public BudgetFringeRate getBudgetFringeRateForDefaultAppointmentType(String documentHeaderId) {
        AppointmentType appointmentType = appointmentTypeDao.getAppointmentType(kualiConfigurationService.getApplicationParameterValue("KraDevelopmentGroup", "defaultAppointmentType"));
        BudgetFringeRate defaultFringeRate = budgetFringeRateDao.getBudgetFringeRate(documentHeaderId, appointmentType.getAppointmentTypeCode());
        if (defaultFringeRate != null) {
            return defaultFringeRate;
        }
        else {
            return new BudgetFringeRate(documentHeaderId, appointmentType);
        }
    }

    public BudgetFringeRate getBudgetFringeRateForPerson(BudgetUser budgetUser) {
        if (StringUtils.isNotEmpty(budgetUser.getAppointmentTypeCode())) {
            return this.getBudgetFringeRate(budgetUser.getDocumentHeaderId(), budgetUser.getAppointmentTypeCode());
        }
        else if (budgetUser.getUserAppointmentTasks().size() > 0 && StringUtils.isNotEmpty(budgetUser.getUserAppointmentTask(0).getUniversityAppointmentTypeCode())) {
            return this.getBudgetFringeRate(budgetUser.getDocumentHeaderId(), budgetUser.getUserAppointmentTask(0).getUniversityAppointmentTypeCode());
        }
        else {
            return this.getBudgetFringeRateForDefaultAppointmentType(budgetUser.getDocumentHeaderId());
        }
    }

    public boolean isValidFringeRate(KualiDecimal fringeRate) {
        if (fringeRate != null) {
            return fringeRate.isLessEqual(Constants.CONTRACTS_AND_GRANTS_FRINGE_RATE_MAX);
        }
        else {
            return false;
        }
    }

    public boolean isValidCostShare(KualiDecimal costShare) {
        if (costShare != null) {
            return costShare.isLessEqual(Constants.CONTRACTS_AND_GRANTS_COST_SHARE_MAX);
        }
        else {
            return false;
        }
    }

    public void setupDefaultFringeRates(Budget budget) {
        for (Iterator iter = getDefaultFringeRates().iterator(); iter.hasNext();) {
            AppointmentType appType = (AppointmentType) iter.next();
            BudgetFringeRate bfr = new BudgetFringeRate(budget.getDocumentHeaderId(), appType.getAppointmentTypeCode(), appType.getFringeRateAmount(), appType.getCostShareFringeRateAmount(), appType);
            budget.getFringeRates().add(bfr);
        }
    }

    /**
     * @param budgetFringeRateDao The budgetFringeRateDao to set.
     */
    public void setBudgetFringeRateDao(BudgetFringeRateDao budgetFringeRateDao) {
        this.budgetFringeRateDao = budgetFringeRateDao;
    }

    /**
     * @param appointmentTypeDao The appointmentTypeDao to set.
     */
    public void setAppointmentTypeDao(AppointmentTypeDao appointmentTypeDao) {
        this.appointmentTypeDao = appointmentTypeDao;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
