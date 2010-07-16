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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;

public class HoldingTaxLotRebalanceRule extends MaintenanceDocumentRuleBase {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document)
    {
        boolean isValid = true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();
        
        HoldingTaxLotRebalance newDocument = getNewHoldingTaxLotRebalanceMaintenceDocument(document);
        HoldingTaxLotRebalance oldDocument = getOldHoldingTaxLotRebalanceMaintenceDocument(document);

        if (isValid) {
            isValid &= validateUnitValue(newDocument);
            isValid &= validateCostValue(newDocument);
            
            if (!isValid) {
                return isValid;
            }
            
            isValid &= validateAllZero(newDocument);
            
            if (!isValid) {
                return isValid;
            }
            
            isValid &= validateTotalUnits(oldDocument, newDocument);
            isValid &= validateTotalCost(oldDocument, newDocument);
        }
        
        return isValid;
    }
    
    private String getHoldingTaxLotErrorPath(int index)
    {
        return EndowPropertyConstants.HOLDING_TAX_LOTS_TAB + "[" + index + "].";
    }
    
    /**
     * 
     * This method...
     * @param document
     * @return
     */
    private HoldingTaxLotRebalance getNewHoldingTaxLotRebalanceMaintenceDocument(MaintenanceDocument document)
    {
        return (HoldingTaxLotRebalance) document.getNewMaintainableObject().getBusinessObject();
    }
    
    /**
     * 
     * This method...
     * @param document
     * @return
     */
    private HoldingTaxLotRebalance getOldHoldingTaxLotRebalanceMaintenceDocument(MaintenanceDocument document)
    {
        return (HoldingTaxLotRebalance) document.getOldMaintainableObject().getBusinessObject();
    }
    
    /**
     * 
     * This method...
     * @param oldDocument
     * @param newDocument
     * @return
     */
    private boolean validateTotalUnits(HoldingTaxLotRebalance oldDocument,
                                       HoldingTaxLotRebalance newDocument)
    {
        boolean isValid = true;
        
        // Calculate the total number of units from the new document to ensure
        // that they still match the original (old) document.
        BigDecimal totalUnits  = new BigDecimal(0);
        for (HoldingTaxLot taxLot : newDocument.getHoldingTaxLots()) {
            totalUnits = totalUnits.add(taxLot.getUnits());
        }
        
        if (!totalUnits.equals(oldDocument.getTotalUnits())) {

            putFieldError(EndowPropertyConstants.HOLDING_TAX_LOTS_TAB, 
                    EndowKeyConstants.HoldingTaxLotRebalanceConstants.ERROR_HLDG_TAX_LOT_REBALANCE_TOTAL_UNITS_NOT_BALANCED);
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * 
     * This method...
     * @param oldDocument
     * @param newDocument
     * @return
     */
    private boolean validateTotalCost(HoldingTaxLotRebalance oldDocument,
                                      HoldingTaxLotRebalance newDocument)
    {
        boolean isValid = true;
        // Calculate the total cost from the new document to ensure
        // that they still match the original (old) document.
        BigDecimal totalCost = new BigDecimal(0);
        for (HoldingTaxLot taxLot : newDocument.getHoldingTaxLots()) {
            totalCost = totalCost.add(taxLot.getCost());
        }
        
        if (!totalCost.equals(oldDocument.getTotalCost())) {
            putFieldError(EndowPropertyConstants.HOLDING_TAX_LOTS_TAB, 
                    EndowKeyConstants.HoldingTaxLotRebalanceConstants.ERROR_HLDG_TAX_LOT_REBALANCE_TOTAL_COST_NOT_BALANCED);
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * 
     * This method...
     * @param newDocument
     * @return
     */
    private boolean validateUnitValue(HoldingTaxLotRebalance newDocument)
    {
        boolean isValid = true;
        for (HoldingTaxLot taxLot : newDocument.getHoldingTaxLots()) {
            if (taxLot.getUnits().signum() < 0) {
                int index = newDocument.getHoldingTaxLots().indexOf(taxLot);
                putFieldError(getHoldingTaxLotErrorPath(index) + EndowPropertyConstants.HOLDING_TAX_LOT_UNITS, 
                        EndowKeyConstants.HoldingTaxLotRebalanceConstants.ERROR_HLDG_TAX_LOT_REBALANCE_UNITS_INVALID);
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    /**
     * 
     * This method...
     * @param newDocument
     * @return
     */
    private boolean validateCostValue(HoldingTaxLotRebalance newDocument)
    {
        boolean isValid = true;
        for (HoldingTaxLot taxLot : newDocument.getHoldingTaxLots()) {
            if (taxLot.getCost().signum() < 0) {
                int index = newDocument.getHoldingTaxLots().indexOf(taxLot);
                putFieldError(getHoldingTaxLotErrorPath(index) + EndowPropertyConstants.HOLDING_TAX_LOT_COST, 
                        EndowKeyConstants.HoldingTaxLotRebalanceConstants.ERROR_HLDG_TAX_LOT_REBALANCE_COST_INVALID);
                isValid = false;
            }
        }
        return isValid;
    }
    
    /**
     * 
     * This method...
     * @param newDocument
     * @return
     */
    private boolean validateAllZero(HoldingTaxLotRebalance newDocument)
    {
        boolean isValid = true;
        for (HoldingTaxLot taxLot : newDocument.getHoldingTaxLots()) {
            boolean zeroUnits = (taxLot.getUnits().signum() == 0);
            boolean zeroCost  = (taxLot.getCost().signum()  == 0);
            if (zeroUnits || zeroCost) {
                if (!(zeroUnits && zeroCost)) {
                    int index = newDocument.getHoldingTaxLots().indexOf(taxLot);
                    putFieldError(getHoldingTaxLotErrorPath(index) + EndowPropertyConstants.HOLDING_TAX_LOT_UNITS, 
                            EndowKeyConstants.HoldingTaxLotRebalanceConstants.ERROR_HLDG_TAX_LOT_REBALANCE_UNITS_COST_ZERO);
                    isValid = false;
                }
            }
        }
        
        return isValid;
    }
}
