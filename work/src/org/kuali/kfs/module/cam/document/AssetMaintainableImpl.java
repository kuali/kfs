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
package org.kuali.module.cams.maintenance;

import java.util.Map;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.service.DepreciationCalculatorService;

/**
 * AssetMaintainable for Asset edit.
 */
public class AssetMaintainableImpl extends KualiMaintainableImpl implements Maintainable {

    /**
     * @see org.kuali.core.maintenance.Maintainable#processAfterEdit(org.kuali.core.document.MaintenanceDocument, java.util.Map)
     */
    public void processAfterEdit(MaintenanceDocument document, Map parameters) {
        Asset asset = (Asset) this.getBusinessObject();
        
        calculateDepreciationValues(asset);
    }

    private void calculateDepreciationValues(Asset asset) {
        DepreciationCalculatorService calculatorService = SpringContext.getBean(DepreciationCalculatorService.class);
        asset.setAccumulatedDepreciation(calculatorService.calculatePrimaryAccumulatedDepreciation(asset));
        asset.setBaseAmount(calculatorService.calculatePrimaryBaseAmount(asset));
        asset.setBookValue(calculatorService.calculatePrimaryBookValue(asset));
        asset.setPrevYearDepreciation(calculatorService.calculatePrimaryPrevYearDepreciation(asset));
        asset.setYearToDateDepreciation(calculatorService.calculatePrimaryYTDDepreciation(asset));
        asset.setCurrentMonthDepreciation(calculatorService.calculatePrimaryCurrentMonthDepreciation(asset));
    }
}
