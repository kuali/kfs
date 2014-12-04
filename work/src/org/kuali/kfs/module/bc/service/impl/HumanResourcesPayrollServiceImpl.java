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
package org.kuali.kfs.module.bc.service.impl;

import org.apache.cxf.common.util.StringUtils;
import org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.Incumbent;
import org.kuali.kfs.module.bc.businessobject.Position;
import org.kuali.kfs.module.bc.dataaccess.HumanResourcesPayrollDao;
import org.kuali.kfs.module.bc.exception.IncumbentNotFoundException;
import org.kuali.kfs.module.bc.exception.PositionNotFoundException;
import org.kuali.kfs.module.bc.service.HumanResourcesPayrollService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kim.api.identity.PersonService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Bootstrap implementation of HumanResourcesPayrollService. Only implements the methods so that Budget will function. Data is not
 * correct and should not be used in production.
 *
 * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService
 */
public class HumanResourcesPayrollServiceImpl implements HumanResourcesPayrollService {
    HumanResourcesPayrollDao humanResourcesPayrollDao;
    private PersonService personService;

    /**
     * This is just a bootstrap implementation. Should be replaced by the real integration with the payroll/hr system.
     *
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#validatePositionUnionCode(java.lang.String)
     */
    @Override
    @NonTransactional
    public boolean validatePositionUnionCode(String positionUnionCode) {
        return true;
    }

    /**
     * This is just a bootstrap implementation. Should be replaced by the real integration with the payroll/hr system.
     *
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#getPosition(java.lang.Integer, java.lang.String)
     */
    @Override
    @Transactional
    public Position getPosition(Integer universityFiscalYear, String positionNumber) throws PositionNotFoundException {
        Position position = humanResourcesPayrollDao.getPosition(universityFiscalYear, positionNumber);

        if (position == null) {
            throw new PositionNotFoundException(universityFiscalYear, positionNumber);
        }

        return position;
    }

    /**
     * This is just a bootstrap implementation. Should be replaced by the real integration with the payroll/hr system.
     *
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#getIncumbent(java.lang.String)
     */
    @Override
    @Transactional
    public Incumbent getIncumbent(String emplid) throws IncumbentNotFoundException {


        String name = SpringContext.getBean(FinancialSystemUserService.class).getPersonNameByEmployeeId(emplid);
        if (!StringUtils.isEmpty(name)) {

            Incumbent incumbent = new BudgetConstructionIntendedIncumbent();
            incumbent.setEmplid(emplid);
            incumbent.setName(name);

            return incumbent;
        } else {
            throw new IncumbentNotFoundException(emplid);

        }

    }

    /**
     * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService#isActiveJob(java.lang.String, java.lang.String,
     *      java.lang.Integer, org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType)
     */
    @Override
    @Transactional
    public boolean isActiveJob(String emplid, String positionNumber, Integer fiscalYear, SynchronizationCheckType synchronizationCheckType) {
        return true;
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
     * @return Returns the personService.
     */
    protected PersonService getPersonService() {
        if(personService==null) {
            personService = SpringContext.getBean(PersonService.class);
        }
        return personService;
    }

}
