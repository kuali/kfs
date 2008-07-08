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
package org.kuali.kfs.module.bc.document.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPayRateHolding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.dataaccess.PayrateExportDao;
import org.kuali.kfs.module.bc.document.service.PayrateExportService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.springframework.transaction.annotation.Transactional;

public class PayrateExportServiceImpl implements PayrateExportService {
    private BusinessObjectService businessObjectService;
    private PayrateExportDao payrateExportDao;
    private int exportCount;

    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateExportService#buildExportFile()
     */
    @Transactional
    public StringBuilder buildExportFile(Integer budgetYear, String positionUnionCode) {
        clearPayrateHoldingTable();
        
        StringBuilder results = new StringBuilder();
        
        populatePayrateHoldingRecords(budgetYear, positionUnionCode);
        List<BudgetConstructionPayRateHolding> holdingRecords = (List<BudgetConstructionPayRateHolding>) this.businessObjectService.findAll(BudgetConstructionPayRateHolding.class);
        for (BudgetConstructionPayRateHolding record : holdingRecords) {
            results.append(buildExportLine(record));
        }
        
        return results;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateExportService#getExportCount()
     */
    public int getExportCount() {
        return exportCount;
    }

    /**
     * 
     * This method...
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setPayrateExportDao(PayrateExportDao payrateExportDao) {
        this.payrateExportDao = payrateExportDao;
    }
    
    /**
     * Populates payrate holding table for export
     * 
     * @param budgetYear
     * @param positionUnionCode
     */
    @Transactional
    private void populatePayrateHoldingRecords(Integer budgetYear, String positionUnionCode) {
        List<PendingBudgetConstructionAppointmentFunding> fundingRecordList = this.payrateExportDao.getFundingRecords(budgetYear, positionUnionCode);
        
        BudgetConstructionPayRateHolding temp;
        for (PendingBudgetConstructionAppointmentFunding fundingRecord : fundingRecordList) {
            temp = new BudgetConstructionPayRateHolding();
            temp.setEmplid(fundingRecord.getEmplid());
            temp.setPositionNumber(fundingRecord.getPositionNumber());
            temp.setPersonName((fundingRecord.getBudgetConstructionIntendedIncumbent().getPersonName() == null) ? "NOT ASSIGNED" : fundingRecord.getBudgetConstructionIntendedIncumbent().getPersonName());
            temp.setSetidSalary(fundingRecord.getBudgetConstructionPosition().getSetidSalary());
            temp.setSalaryAdministrationPlan(fundingRecord.getBudgetConstructionPosition().getPositionSalaryPlanDefault());
            temp.setGrade(fundingRecord.getBudgetConstructionPosition().getPositionGradeDefault());
            temp.setUnionCode(fundingRecord.getBudgetConstructionPosition().getPositionUnionCode());
            temp.setAppointmentRequestedPayRate(new BigDecimal(0));
            
            this.businessObjectService.save(temp);
        }
    }
    
    /**
     * Creates the export line
     * 
     * @param holdingRecord
     * @return
     */
    @NonTransactional
    private StringBuilder buildExportLine(BudgetConstructionPayRateHolding holdingRecord) {
        StringBuilder line = new StringBuilder();
        
        String emplid = padString(holdingRecord.getEmplid(), 11);
        String positionNumber = padString(holdingRecord.getPositionNumber(), 8);
        String personName = padString(holdingRecord.getPersonName(), 50);
        String setIdSalary = padString(holdingRecord.getSetidSalary(), 5);
        String salAdminPlan = padString(holdingRecord.getSalaryAdministrationPlan(), 4);
        String grade = padString(holdingRecord.getGrade(), 3);
        String unionCode = padString(holdingRecord.getUnionCode(), 3);
        String payRate = padString(String.valueOf(holdingRecord.getAppointmentRequestedPayRate().multiply(new BigDecimal(100)).intValue()), 10);
        //TODO: where should I retrieve csf freeze date. Should I use value from form?
        String csfFreezeDate = "";
        
        line.append(emplid);
        line.append(positionNumber);
        line.append(personName);
        line.append(setIdSalary);
        line.append(salAdminPlan);
        line.append(grade);
        line.append(unionCode);
        line.append(payRate);
        line.append(csfFreezeDate);
        line.append("\r\n");
        
        return line;
    }
    
    /**
     * Returns a field that is the length of fieldSize, to facilitate formatting payrate export file
     * 
     * @param stringToPad
     * @param fieldSize
     * @return
     */
    private String padString(String stringToPad, int fieldSize) {
        if (stringToPad.length() < fieldSize) return StringUtils.leftPad(stringToPad, fieldSize - stringToPad.length());
        else if (stringToPad.length() > fieldSize) return stringToPad.substring(0, fieldSize - 1);
        
        return stringToPad;
    }
    
    private void clearPayrateHoldingTable() {
        this.businessObjectService.delete((List<PersistableBusinessObject>)this.businessObjectService.findAll(BudgetConstructionPayRateHolding.class));
    }
}
