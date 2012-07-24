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
package org.kuali.kfs.module.bc.document.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPayRateHolding;
import org.kuali.kfs.module.bc.document.dataaccess.PayrateExportDao;
import org.kuali.kfs.module.bc.document.service.PayrateExportService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class PayrateExportServiceImpl implements PayrateExportService {
    protected BusinessObjectService businessObjectService;
    protected PayrateExportDao payrateExportDao;
    protected int exportCount;

    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateExportService#buildExportFile()
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW)
    public StringBuilder buildExportFile(Integer budgetYear, String positionUnionCode, String csfFreezeDate, String principalId) {
        this.exportCount = 0;
        Map payRateHoldingPersonUniversalIdentifierKey = new HashMap();
        payRateHoldingPersonUniversalIdentifierKey.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalId);
        
        this.businessObjectService.deleteMatching(BudgetConstructionPayRateHolding.class, payRateHoldingPersonUniversalIdentifierKey);
        
        StringBuilder results = new StringBuilder();
        
        this.payrateExportDao.buildPayRateHoldingRows(budgetYear, positionUnionCode, principalId);
        List<BudgetConstructionPayRateHolding> holdingRecords = (List<BudgetConstructionPayRateHolding>) this.businessObjectService.findMatching(BudgetConstructionPayRateHolding.class, payRateHoldingPersonUniversalIdentifierKey);
        for (BudgetConstructionPayRateHolding record : holdingRecords) {
            results.append(buildExportLine(record, csfFreezeDate));
            exportCount++;
        }
        results.append("\r\n");
        results.append("\r\n");
        results.append("Export complete. Export Count: " + exportCount + "\r\n");
        return results;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.document.service.PayrateExportService#isValidPositionUnionCode(java.lang.String)
     */
    @Transactional
    public boolean isValidPositionUnionCode(String positionUnionCode) {
        
        return this.payrateExportDao.isValidPositionUnionCode(positionUnionCode);
    }

    /**
     * 
     * @param businessObjectService
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    /**
     * 
     * 
     * @param payrateExportDao
     */
    @NonTransactional
    public void setPayrateExportDao(PayrateExportDao payrateExportDao) {
        this.payrateExportDao = payrateExportDao;
    }
    
    /**
     * Creates the export line
     * 
     * @param holdingRecord
     * @return
     */
    @NonTransactional
    protected StringBuilder buildExportLine(BudgetConstructionPayRateHolding holdingRecord, String csfFreezeDate) {
        StringBuilder line = new StringBuilder();
        String emplid = padString(holdingRecord.getEmplid(), 11, true);
        String positionNumber = padString(holdingRecord.getPositionNumber(), 8, true);
        String name = padString(holdingRecord.getName(), 50, true);
        String setIdSalary = padString(holdingRecord.getSetidSalary(), 5, true);
        String salAdminPlan = padString(holdingRecord.getSalaryAdministrationPlan(), 4, true);
        String grade = padString(holdingRecord.getGrade(), 3, true);
        String unionCode = padString(holdingRecord.getUnionCode(), 3, true);
        String payRate = padString(String.valueOf(holdingRecord.getAppointmentRequestedPayRate().multiply(new BigDecimal(100)).intValue()), 10, false);
        csfFreezeDate = padString(csfFreezeDate, 8, true);
        
        line.append(emplid);
        line.append(positionNumber);
        line.append(name);
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
    @NonTransactional
    protected String padString(String stringToPad, int fieldSize, boolean leftJustifiy) {
        if (stringToPad.length() < fieldSize) {
            if (leftJustifiy) return StringUtils.rightPad(stringToPad, fieldSize);
            else return StringUtils.leftPad(stringToPad, fieldSize);
        }
        else if (stringToPad.length() > fieldSize) return stringToPad.substring(0, fieldSize - 1);
        
        return stringToPad;
    }
}

