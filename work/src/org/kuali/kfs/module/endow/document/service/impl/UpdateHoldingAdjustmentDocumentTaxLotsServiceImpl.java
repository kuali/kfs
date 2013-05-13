/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.UpdateHoldingAdjustmentDocumentTaxLotsService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

public class UpdateHoldingAdjustmentDocumentTaxLotsServiceImpl implements UpdateHoldingAdjustmentDocumentTaxLotsService {

    private HoldingTaxLotService taxLotService;
    private SecurityService securityService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.UpdateHoldingAdjustmentDocumentTaxLotsService#updateTransactionLineTaxLotsByUnitAdjustmentAmount(boolean,
     *      org.kuali.kfs.module.endow.document.EndowmentUnitShareAdjustmentDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, boolean)
     */
    public void updateTransactionLineTaxLotsByUnitAdjustmentAmount(boolean isUpdate, HoldingAdjustmentDocument holdingAdjustmentDocument, EndowmentTransactionLine transLine, boolean isSource) {
        EndowmentTransactionSecurity endowmentTransactionSecurity = holdingAdjustmentDocument.getSourceTransactionSecurity();

        Security security = securityService.getByPrimaryKey(endowmentTransactionSecurity.getSecurityID());
        if (ObjectUtils.isNotNull(security)) {
            List<HoldingTaxLot> holdingTaxLots = new ArrayList<HoldingTaxLot>();

            // Retrieve the tax lot records
            if (isUpdate) {
                holdingTaxLots = retrieveTaxLotLineForUpdate(transLine, endowmentTransactionSecurity);
            }
            else {
                holdingTaxLots = retrieveAllTaxLotsWithPositiveCost(transLine, endowmentTransactionSecurity);
            }

            // create a record for each lot in holding lot table
            createEndowmentHoldingLotRecord(holdingAdjustmentDocument, transLine, holdingTaxLots);

            // lot holding cost calculations..
            if (transLine.getTaxLotLines() != null) {
                calculateLotHoldingCostForUnitAdjustmentAmount(transLine, isSource);
            }

            // Update Transaction Amount field with absolute value of the lot holding cost
            updateTransactionAmountWithLotHoldingCost(isSource, transLine);
        }
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.UpdateHoldingAdjustmentDocumentTaxLotsService#updateTransactionLineTaxLotsByTransactionAmount(boolean,
     *      org.kuali.kfs.module.endow.document.EndowmentUnitShareAdjustmentDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, boolean)
     */
    public void updateTransactionLineTaxLotsByTransactionAmount(boolean isUpdate, HoldingAdjustmentDocument holdingAdjustmentDocument, EndowmentTransactionLine transLine, boolean isSource) {
        EndowmentTransactionSecurity endowmentTransactionSecurity = holdingAdjustmentDocument.getSourceTransactionSecurity();

        Security security = securityService.getByPrimaryKey(endowmentTransactionSecurity.getSecurityID());
        if (ObjectUtils.isNotNull(security)) {
            List<HoldingTaxLot> holdingTaxLots = new ArrayList<HoldingTaxLot>();

            // Retrieve the tax lot records
            if (isUpdate) {
                holdingTaxLots = retrieveTaxLotLineForUpdate(transLine, endowmentTransactionSecurity);
            }
            else {
                holdingTaxLots = retrieveAllTaxLotsWithPositiveCost(transLine, endowmentTransactionSecurity);
            }

            // create a record for each lot in holding lot table
            createEndowmentHoldingLotRecord(holdingAdjustmentDocument, transLine, holdingTaxLots);

            // lot holding cost calculations..
            if (transLine.getTaxLotLines() != null) {
                BigDecimal taxLotsTotalUnits = BigDecimal.ZERO;
                taxLotsTotalUnits = calculateTotalLotsTotalUnits(holdingTaxLots);
                calculateLotHoldingCostForTransactionAmount(transLine, isSource, taxLotsTotalUnits);
            }
        }
    }

    /**
     * Helper method to calculate the TotalLots total units
     * 
     * @param holdingTaxLots
     * @return taxLotsTotalUnits
     */
    protected BigDecimal calculateTotalLotsTotalUnits(List<HoldingTaxLot> holdingTaxLots) {

        BigDecimal taxLotsTotalUnits = BigDecimal.ZERO;

        for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
            taxLotsTotalUnits = taxLotsTotalUnits.add(holdingTaxLot.getUnits());
        }

        return taxLotsTotalUnits;
    }

    /**
     * Adjusts the oldest tax lot units if the transaction line units do not match the total of the tax lot units.
     * 
     * @param totalTaxLotsUnits
     * @param transLineUnits
     * @param oldestTaxLot
     * @param isSource
     */
    private void adjustTaxLotsUnits(BigDecimal totalTaxLotsUnits, BigDecimal transLineUnits, EndowmentTransactionTaxLotLine oldestTaxLot, boolean isSource) {
        if (totalTaxLotsUnits.compareTo(transLineUnits) != 0 && oldestTaxLot != null) {
            BigDecimal diff = transLineUnits.subtract(totalTaxLotsUnits);

            if (isSource) {
                oldestTaxLot.setLotUnits(oldestTaxLot.getLotUnits().add(diff.negate()));
            }
            else {
                oldestTaxLot.setLotUnits(oldestTaxLot.getLotUnits().add(diff));
            }
        }
    }

    /**
     * Helper method to retrieve all the tax lot records with units greater than zero from endowment holding tax lot table for a
     * specific lot number
     * 
     * @param transLine The endowment transaction line
     * @param endowmentTransactionSecurity endowment transaction security
     * @return holdingTaxLots
     */
    protected List<HoldingTaxLot> retrieveTaxLotLineForUpdate(EndowmentTransactionLine transLine, EndowmentTransactionSecurity endowmentTransactionSecurity) {
        List<HoldingTaxLot> holdingTaxLots = new ArrayList<HoldingTaxLot>();

        List<EndowmentTransactionTaxLotLine> existingTransactionLines = transLine.getTaxLotLines();
        for (EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine : existingTransactionLines) {
            HoldingTaxLot holdingTaxLot = taxLotService.getByPrimaryKey(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), endowmentTransactionTaxLotLine.getTransactionHoldingLotNumber(), transLine.getTransactionIPIndicatorCode());

            if (ObjectUtils.isNotNull(holdingTaxLot)) {
                holdingTaxLots.add(holdingTaxLot);
            }
        }

        transLine.getTaxLotLines().clear();
        return holdingTaxLots;
    }

    /**
     * Helper method to retrieve all tax lots with positive cost holding tax lot lines
     * 
     * @param transLine The endowment transaction line
     * @param endowmentTransactionSecurity endowment transaction security
     * @return holdingTaxLots
     */
    protected List<HoldingTaxLot> retrieveAllTaxLotsWithPositiveCost(EndowmentTransactionLine transLine, EndowmentTransactionSecurity endowmentTransactionSecurity) {
        List<HoldingTaxLot> holdingTaxLots = new ArrayList<HoldingTaxLot>();

        transLine.getTaxLotLines().clear();
        holdingTaxLots = taxLotService.getAllTaxLotsWithPositiveCost(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), transLine.getTransactionIPIndicatorCode());

        return holdingTaxLots;
    }

    /**
     * Helper method to update the transaction amount with the total holding cost
     * 
     * @param transLine The endowment transaction line
     * @param isSource whether this is source or target transaction line
     */
    protected void updateTransactionAmountWithLotHoldingCost(boolean isSource, EndowmentTransactionLine transLine) {
        BigDecimal totalHoldingCost = BigDecimal.ZERO;

        for (EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine : transLine.getTaxLotLines()) {
            totalHoldingCost = totalHoldingCost.add(endowmentTransactionTaxLotLine.getLotHoldingCost());
        }

        // on the decrease side the transaction amount should be positive though the holding cost on the tax lots is negative
        if (isSource && totalHoldingCost.signum() == -1) {
            totalHoldingCost = totalHoldingCost.negate();
        }

        transLine.setTransactionAmount(new KualiDecimal(totalHoldingCost));
    }

    /**
     * Helper method to calculate lot holding cost when unit adjustment amount given
     * 
     * @param transLine
     * @param isSource
     */
    protected void calculateLotHoldingCostForUnitAdjustmentAmount(EndowmentTransactionLine transLine, boolean isSource) {
        BigDecimal holdingCost = BigDecimal.ZERO;

        for (EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine : transLine.getTaxLotLines()) {
            endowmentTransactionTaxLotLine.setLotHoldingCost(KEMCalculationRoundingHelper.multiply(transLine.getUnitAdjustmentAmount(), endowmentTransactionTaxLotLine.getLotUnits(), 2));

            if (isSource) {
                endowmentTransactionTaxLotLine.setLotHoldingCost(endowmentTransactionTaxLotLine.getLotHoldingCost().negate());
            }
        }
    }

    /**
     * Helper method to calculate lot holding cost when transaction amount given
     * 
     * @param transLine
     * @param isSource
     */
    protected void calculateLotHoldingCostForTransactionAmount(EndowmentTransactionLine transLine, boolean isSource, BigDecimal taxLotsTotalUnits) {
        BigDecimal totalUnits = BigDecimal.ZERO;

        EndowmentTransactionTaxLotLine oldestTaxLot = null;
        for (EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine : transLine.getTaxLotLines()) {
            if (endowmentTransactionTaxLotLine.getTransactionHoldingLotNumber().compareTo(1) != 0) {
                oldestTaxLot = endowmentTransactionTaxLotLine;
            }

            BigDecimal percentage = KEMCalculationRoundingHelper.divide(endowmentTransactionTaxLotLine.getLotUnits(), taxLotsTotalUnits, 5);
            endowmentTransactionTaxLotLine.setLotHoldingCost(KEMCalculationRoundingHelper.multiply(percentage, transLine.getTransactionAmount().bigDecimalValue(), 2));

            totalUnits = totalUnits.add(endowmentTransactionTaxLotLine.getLotUnits());

            if (isSource) {
                endowmentTransactionTaxLotLine.setLotHoldingCost(endowmentTransactionTaxLotLine.getLotHoldingCost().negate());
            }
        }

        BigDecimal transLineUnits = transLine.getTransactionUnits().bigDecimalValue();
        adjustTaxLotsUnits(totalUnits, transLineUnits, oldestTaxLot, isSource);
    }

    /**
     * Helper method to create a record for each lot in endowment holding lot table
     * 
     * @param holdingAdjustmentDocument
     * @param transLine
     * @param holdingTaxLots
     */
    /**
     * This method...
     * 
     * @param holdingAdjustmentDocument
     * @param transLine
     * @param holdingTaxLots
     */
    protected void createEndowmentHoldingLotRecord(HoldingAdjustmentDocument holdingAdjustmentDocument, EndowmentTransactionLine transLine, List<HoldingTaxLot> holdingTaxLots) {

        if (holdingTaxLots != null) {
            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine = new EndowmentTransactionTaxLotLine();
                endowmentTransactionTaxLotLine.setDocumentNumber(holdingAdjustmentDocument.getDocumentNumber());
                endowmentTransactionTaxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());
                endowmentTransactionTaxLotLine.setTransactionHoldingLotNumber(holdingTaxLot.getLotNumber().intValue());

                endowmentTransactionTaxLotLine.setSecurityID(holdingAdjustmentDocument.getSourceTransactionSecurity().getSecurityID());
                endowmentTransactionTaxLotLine.setRegistrationCode(holdingAdjustmentDocument.getSourceTransactionSecurity().getRegistrationCode());
                endowmentTransactionTaxLotLine.setIpIndicator(transLine.getTransactionIPIndicatorCode());
                endowmentTransactionTaxLotLine.setKemid(transLine.getKemid());

                endowmentTransactionTaxLotLine.setLotAcquiredDate(holdingTaxLot.getAcquiredDate());
                // set it just for future computation
                endowmentTransactionTaxLotLine.setLotUnits(holdingTaxLot.getUnits());
                transLine.getTaxLotLines().add(endowmentTransactionTaxLotLine);

                // set new lot indicator
                endowmentTransactionTaxLotLine.setNewLotIndicator(false);
            }
        }
    }

    /**
     * Adjusts the oldest tax lot units if the transaction line units do not match the total of the tax lot units.
     * 
     * @param totalTaxLotsUnits
     * @param transLineUnits
     * @param oldestTaxLot
     * @param isSource
     */
    private void adjustTaxLotsCost(BigDecimal totalTaxLotsCost, BigDecimal transLineCost, EndowmentTransactionTaxLotLine oldestTaxLot, boolean isSource) {

        if (totalTaxLotsCost.compareTo(transLineCost) != 0 && oldestTaxLot != null) {
            BigDecimal diff = transLineCost.subtract(totalTaxLotsCost);

            if (isSource) {
                oldestTaxLot.setLotHoldingCost(oldestTaxLot.getLotHoldingCost().add(diff.negate()));
            }
            else {
                oldestTaxLot.setLotHoldingCost(oldestTaxLot.getLotHoldingCost().add(diff));
            }
        }
    }

    /**
     * Gets the taxLotService.
     * 
     * @return taxLotService
     */
    public HoldingTaxLotService getTaxLotService() {
        return taxLotService;
    }

    /**
     * Sets the taxLotService.
     * 
     * @param taxLotService
     */
    public void setTaxLotService(HoldingTaxLotService taxLotService) {
        this.taxLotService = taxLotService;
    }

    /**
     * Gets the securityService.
     * 
     * @return securityService
     */
    public SecurityService getSecurityService() {
        return securityService;
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
