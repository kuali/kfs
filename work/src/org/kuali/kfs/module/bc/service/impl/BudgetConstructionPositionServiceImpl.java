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
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.Position;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao;
import org.kuali.kfs.module.bc.exception.BudgetPositionAlreadyExistsException;
import org.kuali.kfs.module.bc.service.BudgetConstructionPositionService;
import org.kuali.kfs.module.bc.service.HumanResourcesPayrollService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * Implementation of BudgetConstructionPositionService that uses the HumanResourcesPayrollService
 * 
 * @see org.kuali.kfs.module.bc.service.BudgetConstructionPositionService
 * @see org.kuali.kfs.module.bc.service.HumanResourcesPayrollService
 */
public class BudgetConstructionPositionServiceImpl implements BudgetConstructionPositionService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionPositionServiceImpl.class);

    private HumanResourcesPayrollService humanResourcesPayrollService;
    private BusinessObjectService businessObjectService;
    private BudgetConstructionDao budgetConstructionDao;

    /**
     * @see org.kuali.kfs.module.bc.service.BudgetConstructionPositionService#pullNewPositionFromExternal(java.lang.Integer,
     *      java.lang.String)
     */
    @Transactional
    public synchronized void pullNewPositionFromExternal(Integer universityFiscalYear, String positionNumber) throws BudgetPositionAlreadyExistsException {
        // call humanResourcesPayrollService service to pull record
        Position position = humanResourcesPayrollService.getPosition(universityFiscalYear, positionNumber);

        // check if position already exists in budget position table, if not add position
        BudgetConstructionPosition retrievedPosition = getByPrimaryId(universityFiscalYear.toString(), positionNumber);
        if (retrievedPosition != null) {
            throw new BudgetPositionAlreadyExistsException(universityFiscalYear, positionNumber);
        }
        else {
            retrievedPosition = new BudgetConstructionPosition();
        }
        
        // populate BudgetConstructionPosition
        BudgetConstructionPosition budgetConstructionPosition = buildBudgetPosition(position, retrievedPosition);

        // insert position record
        businessObjectService.save(budgetConstructionPosition);
    }

    /**
     * @see org.kuali.kfs.module.bc.service.BudgetConstructionPositionService#refreshPositionFromExternal(java.lang.Integer,
     *      java.lang.String)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void refreshPositionFromExternal(Integer universityFiscalYear, String positionNumber) {
        // call humanResourcesPayrollService service to pull record
        Position position = humanResourcesPayrollService.getPosition(universityFiscalYear, positionNumber);

        // update budget record
        BudgetConstructionPosition retrievedPosition = getByPrimaryId(universityFiscalYear.toString(), positionNumber);
        
        // populate BudgetConstructionPosition
        BudgetConstructionPosition budgetConstructionPosition = buildBudgetPosition(position, retrievedPosition);

        // update position record
        businessObjectService.save(budgetConstructionPosition);

        // update funding position change indicators
        updateFundingPositionChangeIndicators(universityFiscalYear, positionNumber);
    }

    /**
     * Retrieves all funding lines for the position that are not marked as delete and sets the position change indicator fields to
     * true.
     * 
     * @param universityFiscalYear budget fiscal year for the position
     * @param positionNumber position number for the record
     */
    protected void updateFundingPositionChangeIndicators(Integer universityFiscalYear, String positionNumber) {
        // retrieve funding records for the position
        List<PendingBudgetConstructionAppointmentFunding> allPositionFunding = budgetConstructionDao.getAllFundingForPosition(universityFiscalYear, positionNumber);

        // update indicators if the line is not marked for delete
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : allPositionFunding) {
            if (!appointmentFunding.isAppointmentFundingDeleteIndicator()) {
                appointmentFunding.setPositionObjectChangeIndicator(true);
                appointmentFunding.setPositionSalaryChangeIndicator(true);
                appointmentFunding.setVersionNumber(appointmentFunding.getVersionNumber());
                
                businessObjectService.save(appointmentFunding);
            }
        }
    }

    /**
     * Populates a new <code>BudgetConstructionPosition</code> object from a <code>Position</code> object.
     * 
     * @param position object to copy
     * @param budgetConstructionPosition bc position to populate
     * @return BudgetConstructionPosition populated from <code>Position</code>
     * @see org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition
     * @see org.kuali.kfs.module.bc.businessobject.Position
     */
    protected BudgetConstructionPosition buildBudgetPosition(Position position, BudgetConstructionPosition budgetConstructionPosition) {
        budgetConstructionPosition.setBudgetedPosition(position.isBudgetedPosition());
        budgetConstructionPosition.setConfidentialPosition(position.isConfidentialPosition());
        budgetConstructionPosition.setIuDefaultObjectCode(position.getIuDefaultObjectCode());
        budgetConstructionPosition.setIuNormalWorkMonths(position.getIuNormalWorkMonths());
        budgetConstructionPosition.setIuPayMonths(position.getIuPayMonths());
        budgetConstructionPosition.setIuPositionType(position.getIuPositionType());
        budgetConstructionPosition.setJobCode(position.getJobCode());
        budgetConstructionPosition.setJobCodeDescription(position.getJobCodeDescription());
        budgetConstructionPosition.setPositionDepartmentIdentifier(position.getPositionDepartmentIdentifier());
        budgetConstructionPosition.setPositionDescription(position.getPositionDescription());
        budgetConstructionPosition.setPositionEffectiveDate(position.getPositionEffectiveDate());
        budgetConstructionPosition.setPositionEffectiveStatus(position.getPositionEffectiveStatus());
        budgetConstructionPosition.setPositionFullTimeEquivalency(position.getPositionFullTimeEquivalency());
        budgetConstructionPosition.setPositionGradeDefault(position.getPositionGradeDefault());
        budgetConstructionPosition.setPositionNumber(position.getPositionNumber());
        budgetConstructionPosition.setPositionRegularTemporary(position.getPositionRegularTemporary());
        budgetConstructionPosition.setPositionSalaryPlanDefault(position.getPositionSalaryPlanDefault());
        budgetConstructionPosition.setPositionStandardHoursDefault(position.getPositionStandardHoursDefault());
        budgetConstructionPosition.setPositionStatus(position.getPositionStatus());
        budgetConstructionPosition.setPositionUnionCode(position.getPositionUnionCode());
        budgetConstructionPosition.setResponsibilityCenterCode(position.getResponsibilityCenterCode());
        budgetConstructionPosition.setSetidDepartment(position.getSetidDepartment());
        budgetConstructionPosition.setSetidJobCode(position.getSetidJobCode());
        budgetConstructionPosition.setSetidSalary(position.getSetidSalary());
        budgetConstructionPosition.setUniversityFiscalYear(position.getUniversityFiscalYear());

        return budgetConstructionPosition;
    }

    /**
     * @see org.kuali.kfs.module.bc.service.BudgetConstructionPositionService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    @NonTransactional
    public BudgetConstructionPosition getByPrimaryId(String fiscalYear, String positionNumber) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        primaryKeys.put(KFSPropertyConstants.POSITION_NUMBER, positionNumber);

        return (BudgetConstructionPosition) businessObjectService.findByPrimaryKey(BudgetConstructionPosition.class, primaryKeys);
    }

    /**
     * @see org.kuali.kfs.module.bc.service.BudgetConstructionPositionService#isBudgetingPosition(org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition)
     */
    @NonTransactional
    public boolean isBudgetablePosition(BudgetConstructionPosition budgetConstructionPosition) {
        return budgetConstructionPosition != null && budgetConstructionPosition.isBudgetedPosition() && budgetConstructionPosition.isEffective();
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

    /**
     * Sets the budgetConstructionDao attribute value.
     * 
     * @param budgetConstructionDao The budgetConstructionDao to set.
     */
    @NonTransactional
    public void setBudgetConstructionDao(BudgetConstructionDao budgetConstructionDao) {
        this.budgetConstructionDao = budgetConstructionDao;
    }
}
