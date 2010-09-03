/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.batch.service.AccrualProcessingService;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AccrualProcessingServiceImpl implements AccrualProcessingService {

    private ClassCodeService classCodeService;
    private SecurityService securityService;
    private HoldingTaxLotService holdingTaxLotService;
    private KEMService kemService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.batch.service.AccrualProcessingService#processAccruals()
     */
    public boolean processAccruals() {

        boolean success = true;
        List<Security> securities = getSecuritiesToProcess();

        for (Security security : securities) {
            if (EndowConstants.AccrualMethod.AUTOMATED_CASH_MANAGEMENT.equals(security.getClassCode().getAccrualMethod())) {
                processAccrualForAutomatedCashManagement(security);
            }

        }

        return success;
    }

    /**
     * This method...
     * 
     * @return
     */
    private List<Security> getSecuritiesToProcess() {
        Collection<ClassCode> classCodes = classCodeService.getClassCodesForAccrualProcessing();
        List<String> classCodesForAccrualProc = new ArrayList<String>();

        for (ClassCode classCode : classCodes) {
            classCodesForAccrualProc.add(classCode.getCode());
        }
        List<Security> securities = securityService.getSecuritiesByClassCodeWithUnitsGreaterThanZero(classCodesForAccrualProc);

        return securities;
    }

    /**
     * This method...
     * 
     * @param security
     * @return
     */
    private boolean processAccrualForAutomatedCashManagement(Security security) {
        boolean success = true;
        BigDecimal securityRate = security.getIncomeRate();
        List<HoldingTaxLot> holdingTaxLots = holdingTaxLotService.getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(security.getId());

        for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
            BigDecimal accrualAmount = securityRate.multiply(holdingTaxLot.getUnits()).divide(new BigDecimal(kemService.getNumberOfDaysInCalendarYear()));
            holdingTaxLot.setCurrentAccrual(holdingTaxLot.getCurrentAccrual().add(accrualAmount));
            businessObjectService.save(holdingTaxLot);
        }
        return success;

    }

    /**
     * Sets the classCodeService.
     * 
     * @param classCodeService
     */
    public void setClassCodeService(ClassCodeService classCodeService) {
        this.classCodeService = classCodeService;
    }

    /**
     * Sets the holdingTaxLotService.
     * 
     * @param holdingTaxLotService
     */
    public void setHoldingTaxLotService(HoldingTaxLotService holdingTaxLotService) {
        this.holdingTaxLotService = holdingTaxLotService;
    }

    /**
     * Sets the kemService.
     * 
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the securityService.
     * 
     * @param securityService
     */
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }
}
