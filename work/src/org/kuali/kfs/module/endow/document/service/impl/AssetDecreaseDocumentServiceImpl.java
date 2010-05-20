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
package org.kuali.kfs.module.endow.document.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.service.AssetDecreaseDocumentService;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;

public class AssetDecreaseDocumentServiceImpl implements AssetDecreaseDocumentService {

    private HoldingTaxLotService taxLotService;
    private SecurityService securityService;
    private KEMService kemService;
    private ParameterService parameterService;


    /**
     * @see org.kuali.kfs.module.endow.document.service.AssetDecreaseDocumentService#updateTransactionLineTaxLots(boolean,
     *      org.kuali.kfs.module.endow.document.AssetDecreaseDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public void updateTransactionLineTaxLots(boolean isSource, AssetDecreaseDocument assetDecreaseDocument, EndowmentTransactionLine transLine) {

        EndowmentTransactionSecurity endowmentTransactionSecurity = assetDecreaseDocument.getSourceTransactionSecurity();
        String accountingMethod = parameterService.getParameterValue(KfsParameterConstants.ENDOWMENT_ALL.class, EndowConstants.EndowmentSystemParameter.TAX_LOTS_ACCOUNTING_METHOD);

        if (EndowConstants.TaxLotsAccountingMethodOptions.AVERAGE_BALANCE.equalsIgnoreCase(accountingMethod) && EndowConstants.TransactionSubTypeCode.CASH.equalsIgnoreCase(assetDecreaseDocument.getTransactionSubTypeCode())) {
            updateTaxLotsForAccountingMethodAverageBalanceSubTypeCash(assetDecreaseDocument, endowmentTransactionSecurity, transLine);
        }

    }

    private void updateTaxLotsForAccountingMethodAverageBalanceSubTypeCash(AssetDecreaseDocument assetDecreaseDocument, EndowmentTransactionSecurity endowmentTransactionSecurity, EndowmentTransactionLine transLine) {
        Security security = securityService.getByPrimaryKey(endowmentTransactionSecurity.getSecurityID());
        BigDecimal transactionAmount = transLine.getTransactionAmount().bigDecimalValue();
        BigDecimal transactionUnits = transLine.getTransactionUnits().bigDecimalValue();
        // 1. Calculate per unit value
        BigDecimal perUnitValue = KEMCalculationRoundingHelper.divide(transactionAmount, transactionUnits, 5);

        BigDecimal totalTaxLotsUnits = new BigDecimal(0);

        if (ObjectUtils.isNotNull(security)) {
            List<HoldingTaxLot> holdingTaxLots = taxLotService.getAllTaxLots(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), transLine.getTransactionIPIndicatorCode());
            Map<KualiInteger, EndowmentTransactionTaxLotLine> decreaseHoldingTaxLots = new HashMap<KualiInteger, EndowmentTransactionTaxLotLine>();

            if (holdingTaxLots != null && holdingTaxLots.size() > 0) {
                for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                    totalTaxLotsUnits = totalTaxLotsUnits.add(holdingTaxLot.getUnits());

                }

                for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                    EndowmentTransactionTaxLotLine taxLotLine = new EndowmentTransactionTaxLotLine();
                    taxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());
                    // 2. Calculate percentage each lot contains of the total units
                    BigDecimal percentage = KEMCalculationRoundingHelper.divide(holdingTaxLot.getUnits(), totalTaxLotsUnits, 5);
                    // 3. Calculate the number of units to be transacted in each lot
                    BigDecimal lotUnits = KEMCalculationRoundingHelper.multiply(percentage, holdingTaxLot.getUnits(), 5);
                    taxLotLine.setLotUnits(lotUnits);

                    // 4. Calculate the value received for units sold in each tax lot
                    BigDecimal valueReceived = lotUnits.multiply(perUnitValue);

                    // 5. Calculate original unit value for each tax lot
                    BigDecimal originalUnitValue = KEMCalculationRoundingHelper.divide(holdingTaxLot.getCost(), holdingTaxLot.getUnits(), 5);

                    // 6. Calculate original cost
                    BigDecimal originalCost = KEMCalculationRoundingHelper.multiply(lotUnits, originalUnitValue, 2);
                    taxLotLine.setLotHoldingCost(originalCost);

                    // 7. Calculate Gain or loss
                    BigDecimal gainOrLoss = valueReceived.subtract(originalCost);

                    // Determine if short or long term gain/loss
                    Date currentDate = kemService.getCurrentDate();
                    Date acquiredDate = holdingTaxLot.getAcquiredDate();

                    Calendar calendarAcquiredDate = Calendar.getInstance();
                    calendarAcquiredDate.setTime(acquiredDate);
                    calendarAcquiredDate.add(Calendar.MONTH, 6);

                    if (calendarAcquiredDate.getTime().before(currentDate)) {
                        // long term gain/loss
                        taxLotLine.setLotLongTermGainLoss(gainOrLoss);
                    }
                    // short term gain/loss
                    else {
                        taxLotLine.setLotShortTermGainLoss(gainOrLoss);
                    }

                    taxLotLine.setTransactionHoldingLotNumber(holdingTaxLot.getLotNumber().intValue());
                    taxLotLine.setLotAcquiredDate(holdingTaxLot.getAcquiredDate());

                    transLine.getTaxLotLines().add(taxLotLine);

                }
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

    /**
     * Gets the kemService.
     * 
     * @return kemService
     */
    public KEMService getKemService() {
        return kemService;
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
     * Gets the parameterService.
     * 
     * @return parameterService
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService.
     * 
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
