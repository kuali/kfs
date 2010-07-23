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
        
        // First check for errors from parent class, and determine if any previous errors
        // were found.
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();
        
        // 'newDocument' represents the re-balanced  document, and 'oldDocument' represents the original document.
        HoldingTaxLotRebalance newDocument = getNewHoldingTaxLotRebalanceMaintenceDocument(document);
        HoldingTaxLotRebalance oldDocument = getOldHoldingTaxLotRebalanceMaintenceDocument(document);

        // If no previous errors have been found, then perform our own custom error checks.
        if (isValid) {
            
            // First verify that the unit and cost values are valid.
            isValid &= validateUnitValue(newDocument);
            isValid &= validateCostValue(newDocument);
            
            // Are the unit and cost values valid?  If not, then throw the error(s)
            // early before further testing.  There's no reason to perform future
            // checks on invalid values.
            if (!isValid) {
                return isValid;
            }
            
            // Verify that if either units or cost are zero, they're both zero.
            isValid &= validateAllZero(newDocument);
            
            // If only one of the units or cost fields are zero, show error to 
            // user before continuing.
            if (!isValid) {
                return isValid;
            }
            
            // Verify that total units and total cost for all tax lot balances
            // are still the same.
            isValid &= validateTotalUnits(oldDocument, newDocument);
            isValid &= validateTotalCost(oldDocument, newDocument);
        }
        
        return isValid;
    }
    
    /**
     * Helper method, used to generate the correct path of the cost/unit value
     * of a given tax lot.
     * 
     * @param index
     * @return
     */
    private String getHoldingTaxLotErrorPath(int index)
    {
        return EndowPropertyConstants.HOLDING_TAX_LOTS_TAB + "[" + index + "].";
    }
    
    /**
     * Helper method to get the re-balanced i.e. new business object.
     * 
     * @param document
     * @return Re-balanced HoldingTaxLotRebalance business object
     */
    private HoldingTaxLotRebalance getNewHoldingTaxLotRebalanceMaintenceDocument(MaintenanceDocument document)
    {
        return (HoldingTaxLotRebalance) document.getNewMaintainableObject().getBusinessObject();
    }
    
    /**
     * 
     * Helper method to get the original i.e. old business object.
     * 
     * @param document
     * @return Original HoldingTaxLotRebalance business object 
     */
    private HoldingTaxLotRebalance getOldHoldingTaxLotRebalanceMaintenceDocument(MaintenanceDocument document)
    {
        return (HoldingTaxLotRebalance) document.getOldMaintainableObject().getBusinessObject();
    }
    
    /**
     * Verifies that the total units for all the tax lots is still the same i.e. balanced. 
     *
     * @param oldDocument
     * @param newDocument
     * @return True if total units are balanced
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
        
        // Determine if the calculated total value of all the tax lots is still equal
        // to the original total i.e. is still balanced.
        if (!totalUnits.equals(oldDocument.getTotalUnits())) {

            putFieldError(EndowPropertyConstants.HOLDING_TAX_LOTS_TAB, 
                    EndowKeyConstants.HoldingTaxLotRebalanceConstants.ERROR_HLDG_TAX_LOT_REBALANCE_TOTAL_UNITS_NOT_BALANCED);
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Verifies that the total cost for all the tax lots is still the same i.e. balanced.
     * 
     * @param oldDocument
     * @param newDocument
     * @return True if total cost is balanced
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
        
        // Determine if the calculated total value of all the tax lots is still equal
        // to the original total i.e. is still balanced.
        if (!totalCost.equals(oldDocument.getTotalCost())) {
            putFieldError(EndowPropertyConstants.HOLDING_TAX_LOTS_TAB, 
                    EndowKeyConstants.HoldingTaxLotRebalanceConstants.ERROR_HLDG_TAX_LOT_REBALANCE_TOTAL_COST_NOT_BALANCED);
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Validates if the units value isn't negative.
     * 
     * @param newDocument
     * @return True if units is non-negative.
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
     * Validates if the cost value isn't negative.
     * 
     * @param newDocument
     * @return True if cost is non-negative.
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
     * This method ensures that if the unit or cost field for a particular tax
     * is zero that both are zero.  If one field is zero, both must be zero.
     * 
     * @param newDocument
     * @return false if only one of the units or cost fields are zero.
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
