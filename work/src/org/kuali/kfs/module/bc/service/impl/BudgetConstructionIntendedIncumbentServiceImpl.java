/*
 * Copyright 2008 The Kuali Foundation
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

