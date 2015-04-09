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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.Incumbent;
import org.kuali.kfs.module.bc.exception.BudgetIncumbentAlreadyExistsException;
import org.kuali.kfs.module.bc.service.BudgetConstructionIntendedIncumbentService;
import org.kuali.kfs.module.bc.service.HumanResourcesPayrollService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * implements the service methods defined in BudgetConstructionIntendedIncumbentService
 */
public class BudgetConstructionIntendedIncumbentServiceImpl implements BudgetConstructionIntendedIncumbentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionIntendedIncumbentServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private HumanResourcesPayrollService humanResourcesPayrollService;

    /**
     * @see org.kuali.kfs.module.bc.service.BudgetConstructionIntendedIncumbentService#pullNewIncumbentFromExternal(java.lang.String)
     */
    @Transactional
    public synchronized void pullNewIncumbentFromExternal(String emplid) throws BudgetIncumbentAlreadyExistsException {
        // call humanResourcesPayrollService service to pull record
        Incumbent incumbent = humanResourcesPayrollService.getIncumbent(emplid);

        // populate BudgetConstructionIntendedIncumbent
        BudgetConstructionIntendedIncumbent bcIncumbent = new BudgetConstructionIntendedIncumbent();
        bcIncumbent.setEmplid(incumbent.getEmplid());
        bcIncumbent.setName(incumbent.getName());
        bcIncumbent.setIuClassificationLevel("TL");
        bcIncumbent.setSetidSalary("XXXXX");
        bcIncumbent.setSalaryAdministrationPlan("XXX");
        bcIncumbent.setGrade("YYY");

        bcIncumbent.setActive(Boolean.TRUE);

        // check if incumbent already exists in budget incumbent table, if not add incumbent
        BudgetConstructionIntendedIncumbent retrievedIncumbent = getByPrimaryId(emplid);
        if (retrievedIncumbent != null) {
            throw new BudgetIncumbentAlreadyExistsException(emplid);
        }

        businessObjectService.save(bcIncumbent);
    }

    /**
     * @see org.kuali.kfs.module.bc.service.BudgetConstructionIntendedIncumbentService#refreshIncumbentFromExternal(java.lang.String)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void refreshIncumbentFromExternal(String emplid) {
        // call humanResourcesPayrollService service to pull record
        Incumbent incumbent = humanResourcesPayrollService.getIncumbent(emplid);

        // populate BudgetConstructionIntendedIncumbent
        BudgetConstructionIntendedIncumbent bcIncumbent = new BudgetConstructionIntendedIncumbent();
        bcIncumbent.setEmplid(incumbent.getEmplid());
        bcIncumbent.setName(incumbent.getName());

        // update budget record
        BudgetConstructionIntendedIncumbent retrievedIncumbent = getByPrimaryId(emplid);
        bcIncumbent.setVersionNumber(retrievedIncumbent.getVersionNumber());
        bcIncumbent.setIuClassificationLevel("TL");
        bcIncumbent.setSetidSalary("XXXXX");
        bcIncumbent.setSalaryAdministrationPlan("XXX");
        bcIncumbent.setGrade("YYY");
        bcIncumbent.setActive(retrievedIncumbent.isActive());

        businessObjectService.save(bcIncumbent);
    }

    /**
     * @see org.kuali.kfs.module.bc.service.BudgetConstructionIntendedIncumbentService#getByPrimaryId(java.lang.String)
     */
    @NonTransactional
    public BudgetConstructionIntendedIncumbent getByPrimaryId(String emplid) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.EMPLID, emplid);

        return (BudgetConstructionIntendedIncumbent) businessObjectService.findByPrimaryKey(BudgetConstructionIntendedIncumbent.class, primaryKeys);
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the humanResourcesPayrollService attribute value.
     * 
     * @param humanResourcesPayrollService The humanResourcesPayrollService to set.
     */
    @NonTransactional
    public void setHumanResourcesPayrollService(HumanResourcesPayrollService humanResourcesPayrollService) {
        this.humanResourcesPayrollService = humanResourcesPayrollService;
    }
}

