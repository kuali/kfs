/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.service.impl;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.Incumbent;
import org.kuali.kfs.module.bc.businessobject.Position;
import org.kuali.kfs.module.bc.dataaccess.HumanResourcesPayrollDao;
import org.kuali.kfs.module.bc.exception.IncumbentNotFoundException;
import org.kuali.kfs.module.bc.exception.PositionNotFoundException;
import org.kuali.kfs.module.bc.service.HumanResourcesPayrollService;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bootstrap implementation of HumanResourcesPayrollService. Only implements the methods so that Budget will function. Data is not
 * correct and should not be used in production.
 * 
 * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService
 */
public class HumanResourcesPayrollServiceImpl implements HumanResourcesPayrollService {
    HumanResourcesPayrollDao humanResourcesPayrollDao;
    FinancialSystemUserService financialSystemUserService;

    /**
     * @TODO: This is just a bootstrap implementation. Should be replaced by the real integration with the payroll/hr system.
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#validatePositionUnionCode(java.lang.String)
     */
    @NonTransactional
    public boolean validatePositionUnionCode(String positionUnionCode) {
        return true;
    }

    /**
     * @TODO: This is just a bootstrap implementation. Should be replaced by the real integration with the payroll/hr system.
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#getPosition(java.lang.Integer, java.lang.String)
     */
    @Transactional
    public Position getPosition(Integer universityFiscalYear, String positionNumber) throws PositionNotFoundException {
        Position position = humanResourcesPayrollDao.getPosition(universityFiscalYear, positionNumber);

        if (position == null) {
            throw new PositionNotFoundException(universityFiscalYear, positionNumber);
        }

        return position;
    }

    /**
     * @TODO: This is just a bootstrap implementation. Should be replaced by the real integration with the payroll/hr system.
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#getIncumbent(java.lang.String)
     */
    @Transactional
    public Incumbent getIncumbent(String emplid) throws IncumbentNotFoundException {
        UniversalUser user = financialSystemUserService.getUniversalUserByPersonPayrollIdentifier(emplid);

        if (user == null) {
            throw new IncumbentNotFoundException(emplid);
        }

        Incumbent incumbent = new BudgetConstructionIntendedIncumbent();
        incumbent.setEmplid(emplid);
        incumbent.setPersonName(user.getPersonName());

        return incumbent;
    }

    /**
     * Sets the humanResourcesPayrollDao attribute value.
     * 
     * @param humanResourcesPayrollDao The humanResourcesPayrollDao to set.
     */
    @NonTransactional
    public void setHumanResourcesPayrollDao(HumanResourcesPayrollDao humanResourcesPayrollDao) {
        this.humanResourcesPayrollDao = humanResourcesPayrollDao;
    }

    /**
     * Sets the financialSystemUserService attribute value.
     * 
     * @param financialSystemUserService The financialSystemUserService to set.
     */
    @NonTransactional
    public void setFinancialSystemUserService(FinancialSystemUserService financialSystemUserService) {
        this.financialSystemUserService = financialSystemUserService;
    }

}
