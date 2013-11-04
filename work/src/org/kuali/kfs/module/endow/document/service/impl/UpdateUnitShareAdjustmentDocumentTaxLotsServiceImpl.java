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
import org.kuali.kfs.module.endow.document.EndowmentUnitShareAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.UpdateUnitShareAdjustmentDocumentTaxLotsService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.rice.krad.util.ObjectUtils;

public class UpdateUnitShareAdjustmentDocumentTaxLotsServiceImpl implements UpdateUnitShareAdjustmentDocumentTaxLotsService {

    private HoldingTaxLotService taxLotService;
    private SecurityService securityService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.UpdateUnitShareAdjustmentDocumentTaxLotsService#updateTransactionLineTaxLots(boolean,
     *      org.kuali.kfs.module.endow.document.EndowmentUnitShareAdjustmentDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, boolean)
     */
    public void updateTransactionLineTaxLots(boolean isUpdate, EndowmentUnitShareAdjustmentDocument unitShareAdjustmentDocument, EndowmentTransactionLine transLine, boolean isSource) {
        EndowmentTransactionSecurity endowmentTransactionSecurity = unitShareAdjustmentDocument.getSourceTransactionSecurity();

        Security security = securityService.getByPrimaryKey(endowmentTransactionSecurity.getSecurityID());
        if (ObjectUtils.isNotNull(security)) {

            List<HoldingTaxLot> holdingTaxLots = new ArrayList<HoldingTaxLot>();

            if (isUpdate) {
                List<EndowmentTransactionTaxLotLine> existingTransactionLines = transLine.getTaxLotLines();
                for (EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine : existingTransactionLines) {
                    HoldingTaxLot holdingTaxLot = taxLotService.getByPrimaryKey(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), endowmentTransactionTaxLotLine.getTransactionHoldingLotNumber(), transLine.getTransactionIPIndicatorCode());

                    if (ObjectUtils.isNotNull(holdingTaxLot)) {
                        holdingTaxLots.add(holdingTaxLot);
                    }
                }

                transLine.getTaxLotLines().clear();
            }
            else {
                transLine.getTaxLotLines().clear();
                holdingTaxLots = taxLotService.getAllTaxLotsWithPositiveUnits(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), transLine.getTransactionIPIndicatorCode());
            }

            BigDecimal taxLotsTotalUnits = BigDecimal.ZERO;

            if (holdingTaxLots != null) {
                for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                    EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine = new EndowmentTransactionTaxLotLine();
                    endowmentTransactionTaxLotLine.setDocumentNumber(unitShareAdjustmentDocument.getDocumentNumber());
                    endowmentTransactionTaxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());
                    endowmentTransactionTaxLotLine.setTransactionHoldingLotNumber(holdingTaxLot.getLotNumber().intValue());
                    endowmentTransactionTaxLotLine.setKemid(transLine.getKemid());
                    endowmentTransactionTaxLotLine.setSecurityID(holdingTaxLot.getSecurityId());
                    endowmentTransactionTaxLotLine.setRegistrationCode(holdingTaxLot.getRegistrationCode());
                    endowmentTransactionTaxLotLine.setIpIndicator(holdingTaxLot.getIncomePrincipalIndicator());
                    endowmentTransactionTaxLotLine.setLotAcquiredDate(holdingTaxLot.getAcquiredDate());
                    // set it just for future computation
                    endowmentTransactionTaxLotLine.setLotUnits(holdingTaxLot.getUnits());
                    transLine.getTaxLotLines().add(endowmentTransactionTaxLotLine);

                    taxLotsTotalUnits = taxLotsTotalUnits.add(holdingTaxLot.getUnits());

                    // set the new lot indicator
                    endowmentTransactionTaxLotLine.setNewLotIndicator(false);
                }
            }

            if (transLine.getTaxLotLines() != null) {
                BigDecimal totalUnits = BigDecimal.ZERO;

                EndowmentTransactionTaxLotLine oldestTaxLot = null;
                for (EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine : transLine.getTaxLotLines()) {
                    if (endowmentTransactionTaxLotLine.getTransactionHoldingLotNumber().compareTo(1) != 0) {
                        oldestTaxLot = endowmentTransactionTaxLotLine;
                    }
                    BigDecimal percentage = KEMCalculationRoundingHelper.divide(endowmentTransactionTaxLotLine.getLotUnits(), taxLotsTotalUnits, 5);
                    endowmentTransactionTaxLotLine.setLotUnits(KEMCalculationRoundingHelper.multiply(percentage, transLine.getTransactionUnits().bigDecimalValue(), 0));

                    totalUnits = totalUnits.add(endowmentTransactionTaxLotLine.getLotUnits());

                    if (isSource) {
                        endowmentTransactionTaxLotLine.setLotUnits(endowmentTransactionTaxLotLine.getLotUnits().negate());
                    }
                }

                BigDecimal transLineUnits = transLine.getTransactionUnits().bigDecimalValue();
                adjustTaxLotsUnits(totalUnits, transLineUnits, oldestTaxLot, isSource);
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
