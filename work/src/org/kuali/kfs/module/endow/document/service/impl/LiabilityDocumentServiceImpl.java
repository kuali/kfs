/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.document.service.impl;

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocumentBase;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.LiabilityDocumentService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class...
 */
public class LiabilityDocumentServiceImpl extends EndowmentTransactionLinesDocumentServiceImpl implements LiabilityDocumentService {

    private HoldingTaxLotService taxLotService;
    private SecurityService securityService;
    private KEMService kemService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService#updateTransactionLineTaxLots(boolean,
     *      org.kuali.kfs.module.endow.document.AssetIncreaseDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public void updateLiabilityIncreaseTransactionLineTaxLots(boolean isSource, EndowmentTaxLotLinesDocumentBase document, EndowmentTransactionLine transLine) {
        EndowmentTransactionSecurity endowmentTransactionSecurity = null;
        String securityID = null;
        String registrationCode = null;

        if (isSource) {
            endowmentTransactionSecurity = document.getSourceTransactionSecurity();
            securityID = document.getSourceTransactionSecurity().getSecurityID();
            registrationCode = document.getSourceTransactionSecurity().getRegistrationCode();
        }
        else {
            endowmentTransactionSecurity = document.getTargetTransactionSecurity();
            securityID = document.getTargetTransactionSecurity().getSecurityID();
            registrationCode = document.getTargetTransactionSecurity().getRegistrationCode();
        }

        EndowmentTransactionTaxLotLine taxLotLine = obtainTaxLotLine(transLine, endowmentTransactionSecurity);

        // Update the Cost to -ve
        taxLotLine.setLotHoldingCost(taxLotLine.getLotHoldingCost().negate());


        HoldingTaxLot holdingTaxLot = taxLotService.getByPrimaryKey(transLine.getKemid(), securityID, registrationCode, 1, transLine.getTransactionIPIndicatorCode());

        if (ObjectUtils.isNotNull(holdingTaxLot)) {
            if (holdingTaxLot.getUnits().equals(KualiDecimal.ZERO) && holdingTaxLot.getCost().equals(KualiDecimal.ZERO)) {
                taxLotLine.setLotAcquiredDate(kemService.getCurrentDate());
            }
            else {
                taxLotLine.setLotAcquiredDate(holdingTaxLot.getAcquiredDate());
            }
        }
        else {
            taxLotLine.setLotAcquiredDate(kemService.getCurrentDate());
        }
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.UpdateAssetIncreaseDocumentTaxLotsService#updateTransactionLineTaxLots(boolean,
     *      org.kuali.kfs.module.endow.document.AssetIncreaseDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public void updateLiabilityDecreaseTransactionLineTaxLots(boolean isSource, EndowmentTaxLotLinesDocumentBase document, EndowmentTransactionLine transLine) {
        EndowmentTransactionSecurity endowmentTransactionSecurity = null;
        String securityID = null;
        String registrationCode = null;

        if (isSource) {
            endowmentTransactionSecurity = document.getSourceTransactionSecurity();
            securityID = document.getSourceTransactionSecurity().getSecurityID();
            registrationCode = document.getSourceTransactionSecurity().getRegistrationCode();
        }
        else {
            endowmentTransactionSecurity = document.getTargetTransactionSecurity();
            securityID = document.getTargetTransactionSecurity().getSecurityID();
            registrationCode = document.getTargetTransactionSecurity().getRegistrationCode();
        }

        EndowmentTransactionTaxLotLine taxLotLine = obtainTaxLotLine(transLine, endowmentTransactionSecurity);

        BigDecimal postiveUnitValue = taxLotLine.getLotUnits();
        // Negate the Units for Liability
        taxLotLine.setLotUnits(taxLotLine.getLotUnits().negate());

        HoldingTaxLot holdingTaxLot = taxLotService.getByPrimaryKey(transLine.getKemid(), securityID, registrationCode, 1, transLine.getTransactionIPIndicatorCode());
        if (ObjectUtils.isNotNull(holdingTaxLot)) {
            if (holdingTaxLot.getUnits().compareTo(postiveUnitValue) < 0) {
                GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.TRANSACTION_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ASSET_DECREASE_INSUFFICIENT_UNITS);
                // Empty out Tax lot lines.
                transLine.getTaxLotLines().remove(0);
            }
        }
        else {
            // Object must exist
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(EndowConstants.TRANSACTION_LINE_ERRORS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_TAXLOT_INVALID, "Liability");
            // Empty out Tax lot lines.
            transLine.getTaxLotLines().remove(0);

        }
    }

    private EndowmentTransactionTaxLotLine obtainTaxLotLine(EndowmentTransactionLine transLine, EndowmentTransactionSecurity endowmentTransactionSecurity) {
        EndowmentTransactionTaxLotLine taxLotLine = null;

        if (transLine.getTaxLotLines() != null && transLine.getTaxLotLines().size() > 0) {
            // there is only one tax lot line per each transaction line
            taxLotLine = transLine.getTaxLotLines().get(0);
        }
        else {
            // Create and set a new tax lot line
            taxLotLine = new EndowmentTransactionTaxLotLine();
            // taxLotLine.setDocumentNumber(aiDocument.getDocumentNumber());
            // taxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());
            taxLotLine.setTransactionHoldingLotNumber(1);

            // Adding Taxlot line
            transLine.getTaxLotLines().add(0, taxLotLine);

        }

        // Updating data in case of refresh
        taxLotLine.setLotUnits(transLine.getTransactionUnits().bigDecimalValue());
        taxLotLine.setLotHoldingCost(transLine.getTransactionAmount().bigDecimalValue());

        // set kemid, security, regostration code, ip indicator
        taxLotLine.setKemid(transLine.getKemid());
        taxLotLine.setSecurityID(endowmentTransactionSecurity.getSecurityID());
        taxLotLine.setRegistrationCode(endowmentTransactionSecurity.getRegistrationCode());
        taxLotLine.setIpIndicator(transLine.getTransactionIPIndicatorCode());

        // set the new lot indicator
        taxLotLine.setNewLotIndicator(false);

        return taxLotLine;
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

    public KEMService getKemService() {
        return kemService;
    }

    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

}
