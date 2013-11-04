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
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService;
import org.kuali.kfs.module.endow.util.KEMCalculationRoundingHelper;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

public class UpdateTaxLotsBasedOnAccMethodAndTransSubtypeServiceImpl implements UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService {

    private HoldingTaxLotService taxLotService;
    private SecurityService securityService;
    private KEMService kemService;
    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.UpdateTaxLotsBasedOnAccMethodAndTransSubtypeService#updateTransactionLineTaxLots(boolean,
     *      org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public void updateTransactionLineTaxLots(boolean isUpdate, EndowmentTaxLotLinesDocument endowmentTaxLotLinesDocument, EndowmentTransactionLine transLine) {

        EndowmentTransactionSecurity endowmentTransactionSecurity = endowmentTaxLotLinesDocument.getSourceTransactionSecurity();
        String accountingMethod = parameterService.getParameterValueAsString(KfsParameterConstants.ENDOWMENT_ALL.class, EndowParameterKeyConstants.TAX_LOTS_ACCOUNTING_METHOD);
        Security security = securityService.getByPrimaryKey(endowmentTransactionSecurity.getSecurityID());

        if (ObjectUtils.isNotNull(security)) {
            // In the Asset Decrease document, if transaction sub type is non-cash and IF the user inserts an amount in the
            // transaction amount, that amount along with the units sill be used to generate the transaction tax lot lines. The
            // amount will not be calculated by the addition of the transaction line. In the event the user has entered an amount in
            // the transaction amount field, that amount along with the units will be spread proportionately among the tax lots.
            if (endowmentTaxLotLinesDocument instanceof AssetDecreaseDocument && EndowConstants.TransactionSubTypeCode.NON_CASH.equalsIgnoreCase(endowmentTaxLotLinesDocument.getTransactionSubTypeCode()) && transLine.getTransactionAmount() != null && transLine.getTransactionAmount().bigDecimalValue().compareTo(BigDecimal.ZERO) != 0) {
                updateTaxLotsForSubTypeNonCashAndTransAmtNonZero(isUpdate, endowmentTransactionSecurity, transLine);
            }
            else {

                if (EndowConstants.TaxLotsAccountingMethodOptions.AVERAGE_BALANCE.equalsIgnoreCase(accountingMethod) || (EndowConstants.TaxLotsAccountingMethodOptions.FIFO.equalsIgnoreCase(accountingMethod) && !security.getClassCode().isTaxLotIndicator())) {
                    if (EndowConstants.TransactionSubTypeCode.CASH.equalsIgnoreCase(endowmentTaxLotLinesDocument.getTransactionSubTypeCode())) {
                        updateTaxLotsForAccountingMethodAverageBalance(true, isUpdate, endowmentTransactionSecurity, transLine);
                    }
                    if (EndowConstants.TransactionSubTypeCode.NON_CASH.equalsIgnoreCase(endowmentTaxLotLinesDocument.getTransactionSubTypeCode())) {
                        updateTaxLotsForAccountingMethodAverageBalance(false, isUpdate, endowmentTransactionSecurity, transLine);
                        setTransactionLineTotal(transLine);
                    }
                }

                if ((EndowConstants.TaxLotsAccountingMethodOptions.FIFO.equalsIgnoreCase(accountingMethod) || EndowConstants.TaxLotsAccountingMethodOptions.LIFO.equalsIgnoreCase(accountingMethod)) && security.getClassCode().isTaxLotIndicator()) {
                    boolean isFIFO = EndowConstants.TaxLotsAccountingMethodOptions.FIFO.equalsIgnoreCase(accountingMethod);

                    if (EndowConstants.TransactionSubTypeCode.CASH.equalsIgnoreCase(endowmentTaxLotLinesDocument.getTransactionSubTypeCode())) {
                        updateTaxLotsForAccountingMethodFIFOorLIFO(true, isUpdate, isFIFO, endowmentTransactionSecurity, transLine);
                    }
                    if (EndowConstants.TransactionSubTypeCode.NON_CASH.equalsIgnoreCase(endowmentTaxLotLinesDocument.getTransactionSubTypeCode())) {
                        updateTaxLotsForAccountingMethodFIFOorLIFO(false, isUpdate, isFIFO, endowmentTransactionSecurity, transLine);
                        setTransactionLineTotal(transLine);
                    }
                }
            }
        }

    }

    /**
     * Updates the tax lots for the transaction line in the case the accounting method is Average Balance.
     * 
     * @param isSubTypeCash
     * @param endowmentTransactionSecurity
     * @param transLine
     */
    private void updateTaxLotsForAccountingMethodAverageBalance(boolean isSubTypeCash, boolean isUpdate, EndowmentTransactionSecurity endowmentTransactionSecurity, EndowmentTransactionLine transLine) {

        BigDecimal transactionUnits = transLine.getTransactionUnits().bigDecimalValue();
        BigDecimal totalTaxLotsUnits = BigDecimal.ZERO;
        BigDecimal transactionAmount = BigDecimal.ZERO;
        BigDecimal perUnitValue = BigDecimal.ZERO;

        List<HoldingTaxLot> holdingTaxLots = new ArrayList<HoldingTaxLot>();
        Map<Integer, HoldingTaxLot> lotsMap = new HashMap<Integer, HoldingTaxLot>();

        if (!isUpdate) {
            transLine.getTaxLotLines().clear();
            holdingTaxLots = taxLotService.getAllTaxLots(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), transLine.getTransactionIPIndicatorCode());
        }
        else {
            List<EndowmentTransactionTaxLotLine> existingTransactionLines = transLine.getTaxLotLines();
            for (EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine : existingTransactionLines) {
                HoldingTaxLot holdingTaxLot = taxLotService.getByPrimaryKey(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), endowmentTransactionTaxLotLine.getTransactionHoldingLotNumber(), transLine.getTransactionIPIndicatorCode());

                if (ObjectUtils.isNotNull(holdingTaxLot)) {
                    holdingTaxLots.add(holdingTaxLot);
                }
            }

            transLine.getTaxLotLines().clear();
        }

        Map<KualiInteger, EndowmentTransactionTaxLotLine> decreaseHoldingTaxLots = new HashMap<KualiInteger, EndowmentTransactionTaxLotLine>();
        if (isSubTypeCash) {
            transactionAmount = transLine.getTransactionAmount().bigDecimalValue();
            // 1. Calculate per unit value
            perUnitValue = KEMCalculationRoundingHelper.divide(transactionAmount, transactionUnits, 5);
        }

        if (holdingTaxLots != null && holdingTaxLots.size() > 0) {
            boolean keepIntegers = true;
            // compute the total number of units for tax lots
            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                totalTaxLotsUnits = totalTaxLotsUnits.add(holdingTaxLot.getUnits());

                // 3. Calculate the number of units to be transacted in each lot
                // check if percentage and tax lot units are integers
                BigDecimal lotUnits = BigDecimal.ZERO;
                try {
                    int lotUnitsInt = holdingTaxLot.getUnits().intValueExact();
                }
                catch (ArithmeticException ex) {
                    keepIntegers = false;

                }

            }
            // 2. Calculate percentage each lot contains of the total units
            BigDecimal percentage = KEMCalculationRoundingHelper.divide(transLine.getTransactionUnits().bigDecimalValue(), totalTaxLotsUnits, 5);


            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                EndowmentTransactionTaxLotLine taxLotLine = new EndowmentTransactionTaxLotLine();
                taxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());

                BigDecimal lotUnits = BigDecimal.ZERO;

                lotUnits = KEMCalculationRoundingHelper.multiply(percentage, holdingTaxLot.getUnits(), 5);

                // IF all original units per lot are integers (no decimal values), the result is rounded to the nearest
                // integer and stored with the five decimals as zero. If the original units are not all integers, then the
                // value is rounded to five decimals and stored as the five decimal values.
                if (keepIntegers) {
                    lotUnits = lotUnits.setScale(0, BigDecimal.ROUND_HALF_UP);
                    lotUnits = lotUnits.setScale(5);
                }
                taxLotLine.setLotUnits(lotUnits);

                // 5. Calculate original unit value for each tax lot
                BigDecimal originalUnitValue = KEMCalculationRoundingHelper.divide(holdingTaxLot.getCost(), holdingTaxLot.getUnits(), 5);

                // 6. Calculate original cost
                BigDecimal originalCost = KEMCalculationRoundingHelper.multiply(lotUnits, originalUnitValue, 2);
                taxLotLine.setLotHoldingCost(originalCost);

                if (isSubTypeCash) {

                    // 4. Calculate the value received for units sold in each tax lot
                    BigDecimal valueReceived = KEMCalculationRoundingHelper.multiply(lotUnits, perUnitValue, 2);

                    // 7. Calculate Gain or loss
                    calculateGainLoss(holdingTaxLot, taxLotLine, valueReceived, originalCost);
                }

                // set tax lot line lot number and acquired date
                taxLotLine.setTransactionHoldingLotNumber(holdingTaxLot.getLotNumber().intValue());
                taxLotLine.setKemid(transLine.getKemid());
                taxLotLine.setSecurityID(holdingTaxLot.getSecurityId());
                taxLotLine.setRegistrationCode(holdingTaxLot.getRegistrationCode());
                taxLotLine.setIpIndicator(holdingTaxLot.getIncomePrincipalIndicator());
                taxLotLine.setLotAcquiredDate(holdingTaxLot.getAcquiredDate());

                // set the new lot indicator
                taxLotLine.setNewLotIndicator(false);

                // add the new tax lot line to the transaction line tax lots
                addTaxLotLine(transLine, taxLotLine);
                lotsMap.put(taxLotLine.getTransactionHoldingLotNumber(), holdingTaxLot);
            }

            // Adjust the number of units if the total is different from the transaction line units
            adjustUnitsNumberAndAmountsForAverageBalance(lotsMap, transLine, keepIntegers, isSubTypeCash, perUnitValue);

        }
    }

    /**
     * Calculates Gain or Loss for the tax lot line and determines if it's long term or short term
     * 
     * @param holdingTaxLot
     * @param taxLotLine
     * @param valueReceived
     * @param originalCost
     */
    private void calculateGainLoss(HoldingTaxLot holdingTaxLot, EndowmentTransactionTaxLotLine taxLotLine, BigDecimal valueReceived, BigDecimal originalCost) {
        BigDecimal gainOrLoss = valueReceived.subtract(originalCost);
        gainOrLoss = gainOrLoss.setScale(2, BigDecimal.ROUND_HALF_UP);

        // Determine if short or long term gain/loss
        Date currentDate = kemService.getCurrentDate();
        Date acquiredDate = holdingTaxLot.getAcquiredDate();

        Calendar calendarAcquiredDate = Calendar.getInstance();
        calendarAcquiredDate.setTime(acquiredDate);
        calendarAcquiredDate.add(Calendar.MONTH, EndowConstants.SHORT_VS_LONG_TERM_PERIOD);

        if (calendarAcquiredDate.getTime().before(currentDate)) {
            // long term gain/loss
            taxLotLine.setLotLongTermGainLoss(gainOrLoss);
        }
        // short term gain/loss
        else {
            taxLotLine.setLotShortTermGainLoss(gainOrLoss);
        }
    }


    /**
     * Adjusts the number of units if the total is different from the transaction line units.
     * 
     * @param lotsMap
     * @param transLine
     * @param keepIntegers
     * @param isSubTypeCash
     * @param perUnitValue
     */
    private void adjustUnitsNumberAndAmountsForAverageBalance(Map<Integer, HoldingTaxLot> lotsMap, EndowmentTransactionLine transLine, boolean keepIntegers, boolean isSubTypeCash, BigDecimal perUnitValue) {
        // Adjust the number of units if the total is different from the transaction line units
        BigDecimal totalComputedTaxLotUnits = BigDecimal.ZERO;
        BigDecimal totalComputedCost = BigDecimal.ZERO;
        EndowmentTransactionTaxLotLine oldestTaxLotLine = null;

        if (transLine.getTaxLotLines() != null && transLine.getTaxLotLines().size() > 0) {
            for (EndowmentTransactionTaxLotLine taxLotLine : transLine.getTaxLotLines()) {
                BigDecimal lotUnits = taxLotLine.getLotUnits().negate();

                // calculate the total number of units to be decreased
                totalComputedTaxLotUnits = totalComputedTaxLotUnits.add(lotUnits);
                totalComputedCost = totalComputedCost.add(taxLotLine.getLotHoldingCost().negate());

                if (taxLotLine.getLotShortTermGainLoss() != null) {
                    totalComputedCost = totalComputedCost.add(taxLotLine.getLotShortTermGainLoss());
                }

                if (taxLotLine.getLotLongTermGainLoss() != null) {
                    totalComputedCost = totalComputedCost.add(taxLotLine.getLotLongTermGainLoss());
                }

                // keep the tax lot with the oldest acquired date so that we can adjust the units for that one in case the
                // number of units needs and adjustment
                if (oldestTaxLotLine != null) {
                    if (oldestTaxLotLine.getLotAcquiredDate().after(taxLotLine.getLotAcquiredDate())) {
                        oldestTaxLotLine = taxLotLine;
                    }
                }
                else {
                    oldestTaxLotLine = taxLotLine;
                }
            }
        }

        // compare with the negated number of units on the transaction line because the units on the tax lots have been negated
        if (totalComputedTaxLotUnits.compareTo(transLine.getTransactionUnits().bigDecimalValue().negate()) != 0) {
            BigDecimal difUnits = transLine.getTransactionUnits().bigDecimalValue().subtract(totalComputedTaxLotUnits);
            oldestTaxLotLine.setLotUnits(oldestTaxLotLine.getLotUnits().add(difUnits.negate()));
            oldestTaxLotLine.setLotUnits(oldestTaxLotLine.getLotUnits().negate());

            if (isSubTypeCash) {
                // update totalComputedCost
                totalComputedCost = totalComputedCost.subtract(oldestTaxLotLine.getLotHoldingCost().negate());
                if (oldestTaxLotLine.getLotShortTermGainLoss() != null) {
                    totalComputedCost = totalComputedCost.subtract(oldestTaxLotLine.getLotShortTermGainLoss());
                }

                if (oldestTaxLotLine.getLotLongTermGainLoss() != null) {
                    totalComputedCost = totalComputedCost.subtract(oldestTaxLotLine.getLotLongTermGainLoss());
                }
            }

            HoldingTaxLot holdingTaxLot = lotsMap.get(oldestTaxLotLine.getTransactionHoldingLotNumber());
            BigDecimal originalUnitValue = KEMCalculationRoundingHelper.divide(holdingTaxLot.getCost(), holdingTaxLot.getUnits(), 5);
            BigDecimal originalCost = KEMCalculationRoundingHelper.multiply(oldestTaxLotLine.getLotUnits(), originalUnitValue, 2);
            oldestTaxLotLine.setLotHoldingCost(originalCost);

            if (isSubTypeCash) {
                // 4. Calculate the value received for units sold in each tax lot
                BigDecimal valueReceived = KEMCalculationRoundingHelper.multiply(oldestTaxLotLine.getLotUnits(), perUnitValue, 2);

                // 7. Calculate Gain or loss
                calculateGainLoss(holdingTaxLot, oldestTaxLotLine, valueReceived, originalCost);
                // update totalComputedCost
                totalComputedCost = totalComputedCost.add(oldestTaxLotLine.getLotHoldingCost());
                if (oldestTaxLotLine.getLotShortTermGainLoss() != null) {
                    totalComputedCost = totalComputedCost.add(oldestTaxLotLine.getLotShortTermGainLoss());
                }

                if (oldestTaxLotLine.getLotLongTermGainLoss() != null) {
                    totalComputedCost = totalComputedCost.add(oldestTaxLotLine.getLotLongTermGainLoss());
                }
            }
            oldestTaxLotLine.setLotHoldingCost(oldestTaxLotLine.getLotHoldingCost().negate());
            oldestTaxLotLine.setLotUnits(oldestTaxLotLine.getLotUnits().negate());

        }

        if (isSubTypeCash) {
            // compare total computed cost with the transaction line amount
            if (totalComputedCost.compareTo(transLine.getTransactionUnits().bigDecimalValue()) != 0) {
                BigDecimal difAmount = transLine.getTransactionAmount().bigDecimalValue().subtract(totalComputedCost);
                oldestTaxLotLine.setLotHoldingCost(oldestTaxLotLine.getLotHoldingCost().negate().add(difAmount));

                oldestTaxLotLine.setLotHoldingCost(oldestTaxLotLine.getLotHoldingCost().negate());
            }
        }

    }


    /**
     * Updates the tax lots for the transaction line in the case the accounting method is FIFO or LIFO.
     * 
     * @param isSubTypeCash
     * @param isFIFO
     * @param endowmentTransactionSecurity
     * @param transLine
     */
    private void updateTaxLotsForAccountingMethodFIFOorLIFO(boolean isSubTypeCash, boolean isUpdate, boolean isFIFO, EndowmentTransactionSecurity endowmentTransactionSecurity, EndowmentTransactionLine transLine) {
        BigDecimal transactionUnits = transLine.getTransactionUnits().bigDecimalValue();
        BigDecimal transactionAmount = BigDecimal.ZERO;
        BigDecimal perUnitVal = BigDecimal.ZERO;
        List<HoldingTaxLot> holdingTaxLots = new ArrayList<HoldingTaxLot>();

        if (!isUpdate) {
            transLine.getTaxLotLines().clear();
            holdingTaxLots = taxLotService.getAllTaxLotsOrderByAcquiredDate(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), transLine.getTransactionIPIndicatorCode(), isFIFO);
        }
        else {
            List<EndowmentTransactionTaxLotLine> existingTransactionLines = transLine.getTaxLotLines();
            for (EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine : existingTransactionLines) {
                HoldingTaxLot holdingTaxLot = taxLotService.getByPrimaryKey(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), endowmentTransactionTaxLotLine.getTransactionHoldingLotNumber(), transLine.getTransactionIPIndicatorCode());

                if (ObjectUtils.isNotNull(holdingTaxLot)) {
                    holdingTaxLots.add(holdingTaxLot);
                }
            }
            transLine.getTaxLotLines().clear();
        }

        Map<KualiInteger, EndowmentTransactionTaxLotLine> decreaseHoldingTaxLots = new HashMap<KualiInteger, EndowmentTransactionTaxLotLine>();

        if (isSubTypeCash) {
            transactionAmount = transLine.getTransactionAmount().bigDecimalValue();
            // 1. Compute per unit value
            perUnitVal = KEMCalculationRoundingHelper.divide(transactionAmount, transactionUnits, 5);
        }

        BigDecimal remainingUnits = transactionUnits;

        if (holdingTaxLots != null && holdingTaxLots.size() > 0) {
            // compute the total number of units for tax lots
            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                EndowmentTransactionTaxLotLine taxLotLine = new EndowmentTransactionTaxLotLine();
                taxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());

                // 2. Set the lot units

                // if transaction units is greater than the holding tax lot units then take all the holding tax lot units
                if (remainingUnits.compareTo(holdingTaxLot.getUnits()) == 1) {
                    taxLotLine.setLotUnits(holdingTaxLot.getUnits());
                    remainingUnits = remainingUnits.subtract(holdingTaxLot.getUnits());
                }
                // if transaction units is less than the holding tax lot units then take just the remaining number of units
                else {
                    taxLotLine.setLotUnits(remainingUnits);
                    remainingUnits = BigDecimal.ZERO;
                }

                // 4. Calculate the original unit value: cost/units
                BigDecimal originalUnitVal = KEMCalculationRoundingHelper.divide(holdingTaxLot.getCost(), holdingTaxLot.getUnits(), 5);

                // 5. Calculate original cost
                BigDecimal originalCost = KEMCalculationRoundingHelper.multiply(originalUnitVal, taxLotLine.getLotUnits(), 2);

                // set the tax lot holding cost
                taxLotLine.setLotHoldingCost(originalCost);

                // if sub type cash then the
                if (isSubTypeCash) {
                    // 3. Calculate the value received for the units sold and round to 2 decimals
                    BigDecimal receivedValue = KEMCalculationRoundingHelper.multiply(perUnitVal, taxLotLine.getLotUnits(), 2);
                    // 6. Calculate the gain or loss
                    calculateGainLoss(holdingTaxLot, taxLotLine, receivedValue, originalCost);
                }

                // set the lot number
                taxLotLine.setTransactionHoldingLotNumber(holdingTaxLot.getLotNumber().intValue());
                taxLotLine.setKemid(transLine.getKemid());
                taxLotLine.setSecurityID(holdingTaxLot.getSecurityId());
                taxLotLine.setRegistrationCode(holdingTaxLot.getRegistrationCode());
                taxLotLine.setIpIndicator(holdingTaxLot.getIncomePrincipalIndicator());
                // set the lot acquired date
                taxLotLine.setLotAcquiredDate(holdingTaxLot.getAcquiredDate());

                addTaxLotLine(transLine, taxLotLine);

                if (remainingUnits.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }
            }
        }
    }

    /**
     * Updates the tax lots for the transaction line in the case the transaction sub type is non-cash and the user entered the
     * transaction amount. This method is specific to the Asset Decrease document.
     * 
     * @param isSubTypeCash
     * @param endowmentTransactionSecurity
     * @param transLine
     */
    private void updateTaxLotsForSubTypeNonCashAndTransAmtNonZero(boolean isUpdate, EndowmentTransactionSecurity endowmentTransactionSecurity, EndowmentTransactionLine transLine) {

        BigDecimal transactionUnits = transLine.getTransactionUnits().bigDecimalValue();
        BigDecimal totalTaxLotsUnits = BigDecimal.ZERO;
        BigDecimal transactionAmount = transLine.getTransactionAmount().bigDecimalValue();
        BigDecimal perUnitValue = BigDecimal.ZERO;

        List<HoldingTaxLot> holdingTaxLots = new ArrayList<HoldingTaxLot>();
        Map<Integer, HoldingTaxLot> lotsMap = new HashMap<Integer, HoldingTaxLot>();

        if (!isUpdate) {
            transLine.getTaxLotLines().clear();
            holdingTaxLots = taxLotService.getAllTaxLots(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), transLine.getTransactionIPIndicatorCode());
        }
        else {
            List<EndowmentTransactionTaxLotLine> existingTransactionLines = transLine.getTaxLotLines();
            for (EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine : existingTransactionLines) {
                HoldingTaxLot holdingTaxLot = taxLotService.getByPrimaryKey(transLine.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), endowmentTransactionTaxLotLine.getTransactionHoldingLotNumber(), transLine.getTransactionIPIndicatorCode());

                if (ObjectUtils.isNotNull(holdingTaxLot)) {
                    holdingTaxLots.add(holdingTaxLot);
                }
            }

            transLine.getTaxLotLines().clear();
        }

        Map<KualiInteger, EndowmentTransactionTaxLotLine> decreaseHoldingTaxLots = new HashMap<KualiInteger, EndowmentTransactionTaxLotLine>();

        if (holdingTaxLots != null && holdingTaxLots.size() > 0) {
            boolean keepIntegers = true;
            // compute the total number of units for tax lots
            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                totalTaxLotsUnits = totalTaxLotsUnits.add(holdingTaxLot.getUnits());

                // 3. Calculate the number of units to be transacted in each lot
                // check if percentage and tax lot units are integers
                BigDecimal lotUnits = BigDecimal.ZERO;
                try {
                    int lotUnitsInt = holdingTaxLot.getUnits().intValueExact();
                }
                catch (ArithmeticException ex) {
                    keepIntegers = false;
                }

            }

            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                EndowmentTransactionTaxLotLine taxLotLine = new EndowmentTransactionTaxLotLine();
                taxLotLine.setDocumentLineNumber(transLine.getTransactionLineNumber());

                BigDecimal lotUnits = BigDecimal.ZERO;
                // 2. Calculate percentage each lot holds out of the total units
                BigDecimal percentage = KEMCalculationRoundingHelper.divide(holdingTaxLot.getUnits(), totalTaxLotsUnits, 5);


                lotUnits = KEMCalculationRoundingHelper.multiply(percentage, transLine.getTransactionUnits().bigDecimalValue(), 5);

                // IF all original units per lot are integers (no decimal values), the result is rounded to the nearest
                // integer and stored with the five decimals as zero. If the original units are not all integers, then the
                // value is rounded to five decimals and stored as the five decimal values.
                if (keepIntegers) {
                    lotUnits = lotUnits.setScale(0, BigDecimal.ROUND_HALF_UP);
                    lotUnits = lotUnits.setScale(5);
                }
                taxLotLine.setLotUnits(lotUnits);

                // 6. Calculate holding cost
                BigDecimal holdingCost = KEMCalculationRoundingHelper.multiply(percentage, transactionAmount, 2);
                taxLotLine.setLotHoldingCost(holdingCost);


                // set tax lot line lot number and acquired date
                taxLotLine.setTransactionHoldingLotNumber(holdingTaxLot.getLotNumber().intValue());
                taxLotLine.setKemid(transLine.getKemid());
                taxLotLine.setSecurityID(holdingTaxLot.getSecurityId());
                taxLotLine.setRegistrationCode(holdingTaxLot.getRegistrationCode());
                taxLotLine.setIpIndicator(holdingTaxLot.getIncomePrincipalIndicator());
                taxLotLine.setLotAcquiredDate(holdingTaxLot.getAcquiredDate());

                // set the new lot indicator
                taxLotLine.setNewLotIndicator(false);

                // add the new tax lot line to the transaction line tax lots
                addTaxLotLine(transLine, taxLotLine);
                lotsMap.put(taxLotLine.getTransactionHoldingLotNumber(), holdingTaxLot);
            }
            adjustUnitsAndAmountsForNonCashAndTransactionAmtNotZero(transLine);

        }
    }

    /**
     * Adjusts the number of units and the amounts if the total is different from the transaction line units. The correction will be
     * done on the oldest tax lot. This method is specific to the Asset Decrease document and is used in
     * updateTaxLotsForSubTypeNonCashAndTransAmtNonZero().
     * 
     * @param transLine
     * @param keepIntegers
     */
    private void adjustUnitsAndAmountsForNonCashAndTransactionAmtNotZero(EndowmentTransactionLine transLine) {
        // Adjust the number of units if the total is different from the transaction line units
        BigDecimal totalComputedTaxLotUnits = BigDecimal.ZERO;
        BigDecimal totalComputedCost = BigDecimal.ZERO;
        EndowmentTransactionTaxLotLine oldestTaxLotLine = null;

        if (transLine.getTaxLotLines() != null && transLine.getTaxLotLines().size() > 0) {
            for (EndowmentTransactionTaxLotLine taxLotLine : transLine.getTaxLotLines()) {
                BigDecimal lotUnits = taxLotLine.getLotUnits().negate();

                // calculate the total number of units to be decreased
                totalComputedTaxLotUnits = totalComputedTaxLotUnits.add(lotUnits);
                totalComputedCost = totalComputedCost.add(taxLotLine.getLotHoldingCost().negate());

                if (taxLotLine.getLotShortTermGainLoss() != null) {
                    totalComputedCost = totalComputedCost.add(taxLotLine.getLotShortTermGainLoss());
                }

                if (taxLotLine.getLotLongTermGainLoss() != null) {
                    totalComputedCost = totalComputedCost.add(taxLotLine.getLotLongTermGainLoss());
                }

                // keep the tax lot with the oldest acquired date so that we can adjust the units for that one in case the
                // number of units needs and adjustment
                if (oldestTaxLotLine != null) {
                    if (oldestTaxLotLine.getLotAcquiredDate().after(taxLotLine.getLotAcquiredDate())) {
                        oldestTaxLotLine = taxLotLine;
                    }
                }
                else {
                    oldestTaxLotLine = taxLotLine;
                }
            }
        }

        // compare with the negated number of units on the transaction line because the units on the tax lots have been negated
        if (totalComputedTaxLotUnits.compareTo(transLine.getTransactionUnits().bigDecimalValue().negate()) != 0) {
            BigDecimal difUnits = transLine.getTransactionUnits().bigDecimalValue().subtract(totalComputedTaxLotUnits);
            oldestTaxLotLine.setLotUnits(oldestTaxLotLine.getLotUnits().add(difUnits.negate()));

            BigDecimal difAmount = transLine.getTransactionAmount().bigDecimalValue().subtract(totalComputedCost);
            oldestTaxLotLine.setLotHoldingCost(oldestTaxLotLine.getLotHoldingCost().add(difAmount.negate()));
        }

    }

    /**
     * Adds a tax lot line to a transaction line but first it negates the units and cost.
     * 
     * @param transactionLine
     * @param taxLotLine
     */
    private void addTaxLotLine(EndowmentTransactionLine transactionLine, EndowmentTransactionTaxLotLine taxLotLine) {
        // negate units and cost
        taxLotLine.setLotUnits(taxLotLine.getLotUnits().negate());
        taxLotLine.setLotHoldingCost(taxLotLine.getLotHoldingCost().negate());

        // add the tax lot line to the transaction line
        transactionLine.getTaxLotLines().add(taxLotLine);
    }

    /**
     * Sets the transaction line amount to be the total amount of all tax lot lines times negative 1. This is applied in case the
     * transaction sub type is non-cash.
     * 
     * @param transactionLine
     */
    private void setTransactionLineTotal(EndowmentTransactionLine transactionLine) {
        List<EndowmentTransactionTaxLotLine> taxLots = transactionLine.getTaxLotLines();
        BigDecimal totalAmount = BigDecimal.ZERO;
        if (taxLots != null && taxLots.size() > 0) {
            for (EndowmentTransactionTaxLotLine taxLot : taxLots) {
                totalAmount = totalAmount.add(taxLot.getLotHoldingCost());
            }
        }

        totalAmount = totalAmount.negate();

        transactionLine.setTransactionAmount(new KualiDecimal(totalAmount));
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
