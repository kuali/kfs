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
package org.kuali.kfs.module.cam.document.service.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.service.PaymentSummaryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class implements PaymentSummaryService
 */
public class PaymentSummaryServiceImpl implements PaymentSummaryService {


    private static Map<Integer, Method> DEPR_AMT_FIELDS = new HashMap<Integer, Method>();
    /**
     * Map will store getter method mapped to each primary depreciation period column. Based on the current fiscal month, current
     * month depreciation column can be identified easily from this map
     */
    static {
        try {
            Class<?>[] emptyParams = new Class[] {};
            DEPR_AMT_FIELDS.put(1, AssetPayment.class.getMethod("getPeriod1Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(2, AssetPayment.class.getMethod("getPeriod2Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(3, AssetPayment.class.getMethod("getPeriod3Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(4, AssetPayment.class.getMethod("getPeriod4Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(5, AssetPayment.class.getMethod("getPeriod5Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(6, AssetPayment.class.getMethod("getPeriod6Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(7, AssetPayment.class.getMethod("getPeriod7Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(8, AssetPayment.class.getMethod("getPeriod8Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(9, AssetPayment.class.getMethod("getPeriod9Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(10, AssetPayment.class.getMethod("getPeriod10Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(11, AssetPayment.class.getMethod("getPeriod11Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put(12, AssetPayment.class.getMethod("getPeriod12Depreciation1Amount", emptyParams));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private UniversityDateService universityDateService;
    private ParameterService parameterService;

    protected KualiDecimal addAmount(KualiDecimal amount, KualiDecimal addend) {
        if (addend != null) {
            return amount.add(addend);
        }
        return amount;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.PaymentSummaryService#calculateAndSetPaymentSummary(org.kuali.kfs.module.cam.businessobject.Asset)
     */

    public void calculateAndSetPaymentSummary(Asset asset) {
        if (ObjectUtils.isNotNull(asset)) {
            asset.setFederalContribution(calculateFederalContribution(asset));
            setPaymentYearToDate(asset);
            asset.setPaymentTotalCost(calculatePaymentTotalCost(asset));
            asset.setAccumulatedDepreciation(calculatePrimaryAccumulatedDepreciation(asset));
            asset.setBaseAmount(calculatePrimaryBaseAmount(asset));
            asset.setBookValue(calculatePrimaryBookValue(asset));
            asset.setPrevYearDepreciation(calculatePrimaryPrevYearDepreciation(asset));
            asset.setYearToDateDepreciation(calculatePrimaryYTDDepreciation(asset));
            asset.setCurrentMonthDepreciation(calculatePrimaryCurrentMonthDepreciation(asset));
        }
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.PaymentSummaryService#calculateFederalContribution(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public KualiDecimal calculateFederalContribution(Asset asset) {
        KualiDecimal amount = new KualiDecimal(0);
        if (ObjectUtils.isNull(asset)) return amount;
        List<AssetPayment> assetPayments = asset.getAssetPayments();

        for (AssetPayment payment : assetPayments) {
            // Refresh the financial object
            if (ObjectUtils.isNull(payment.getObjectCodeCurrent())) {
                payment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.OBJECT_CODE_CURRENT);
            }
            Collection<String> fedContrTypes = parameterService.getParameterValuesAsString(Asset.class, CamsConstants.Parameters.FEDERAL_CONTRIBUTIONS_OBJECT_SUB_TYPES);
            if (!ObjectUtils.isNull(payment.getObjectCodeCurrent()) 
                    && fedContrTypes.contains( payment.getObjectCodeCurrent().getFinancialObjectSubTypeCode())) {
                amount = addAmount(amount, payment.getAccountChargeAmount());
            }
        }
        return amount;
    }

    /**
     * Sums up total payment cost for an asset
     * 
     * @param asset Asset
     * @return Total Payment Amount
     */
    public KualiDecimal calculatePaymentTotalCost(Asset asset) {
        KualiDecimal totalCost = new KualiDecimal(0);
        if (ObjectUtils.isNotNull(asset)) {
            List<AssetPayment> payments = asset.getAssetPayments();
            for (AssetPayment payment : payments) {
                totalCost = addAmount(totalCost, payment.getAccountChargeAmount());
            }  
        }
        return totalCost;
    }

    /**
     * Sums up primary accumulated depreciation amount
     * 
     * @param asset Asset
     * @return Accumulated Primary Depreciation Amount
     */
    public KualiDecimal calculatePrimaryAccumulatedDepreciation(Asset asset) {
        KualiDecimal amount = new KualiDecimal(0);
        if (ObjectUtils.isNotNull(asset)) {
            List<AssetPayment> assetPayments = asset.getAssetPayments();

            if (assetPayments != null) {
                for (AssetPayment assetPayment : assetPayments) {
                    amount = addAmount(amount, assetPayment.getAccumulatedPrimaryDepreciationAmount());
                }
            }
        }
        return amount;
    }


    /**
     * Sums up primary base amount for an asset
     * 
     * @param asset Asset
     * @return Base Amount
     */
    protected KualiDecimal calculatePrimaryBaseAmount(Asset asset) {
        KualiDecimal amount = new KualiDecimal(0);
        if (ObjectUtils.isNotNull(asset)) {
            List<AssetPayment> assetPayments = asset.getAssetPayments();
            if (assetPayments != null) {
                for (AssetPayment assetPayment : assetPayments) {
                    amount = addAmount(amount, assetPayment.getPrimaryDepreciationBaseAmount());
                }
            }
        }
        return amount;
    }

    /**
     * Sums up primary book value for an asset
     * 
     * @param asset Asset
     * @return Book Value Amount
     */
    public KualiDecimal calculatePrimaryBookValue(Asset asset) {
        KualiDecimal baseAmount = calculatePrimaryBaseAmount(asset);
        KualiDecimal accumDeprAmount = calculatePrimaryAccumulatedDepreciation(asset);
        KualiDecimal salvageAmount = asset.getSalvageAmount();
        // If depreciation method is "SV", then minus it from base amount
        if (CamsConstants.Asset.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE.equals(asset.getPrimaryDepreciationMethodCode()) && salvageAmount != null) {
            return baseAmount.subtract(accumDeprAmount).subtract(salvageAmount);
        }
        return baseAmount.subtract(accumDeprAmount);
    }


    /**
     * Sums current month depreciation amount for an asset
     * 
     * @param asset Asset
     * @return Current month depreciation amount
     */
    protected KualiDecimal calculatePrimaryCurrentMonthDepreciation(Asset asset) {
        KualiDecimal amount = new KualiDecimal(0);
        if (ObjectUtils.isNotNull(asset)) {
            List<AssetPayment> assetPayments = asset.getAssetPayments();   
            if (assetPayments != null) {
                for (AssetPayment assetPayment : assetPayments) {
                    amount = addAmount(amount, getCurrentMonthDepreciationAmount(assetPayment));

                }
            }
        }
        return amount;
    }


    /**
     * Sums up previous year depreciation amount for an asset
     * 
     * @param asset Asset
     * @return Previoud Year Depreciation Amount
     */
    protected KualiDecimal calculatePrimaryPrevYearDepreciation(Asset asset) {
        KualiDecimal amount = new KualiDecimal(0);
        if (ObjectUtils.isNotNull(asset)) {
            List<AssetPayment> assetPayments = asset.getAssetPayments();
            if (assetPayments != null) {
                for (AssetPayment assetPayment : assetPayments) {
                    amount = addAmount(amount, assetPayment.getPreviousYearPrimaryDepreciationAmount());
                }
            }
        }
        return amount;
    }


    /**
     * Sums up year to date depreciation amount for an asset
     * 
     * @param asset Asset
     * @return Year To Date Depreciation Amount
     */
    protected KualiDecimal calculatePrimaryYTDDepreciation(Asset asset) {
        KualiDecimal amount = new KualiDecimal(0);
        if (ObjectUtils.isNotNull(asset)) {
            List<AssetPayment> assetPayments = asset.getAssetPayments();
            if (assetPayments != null) {
                for (AssetPayment assetPayment : assetPayments) {
                    amount = addAmount(amount, assetPayment.getPeriod1Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod2Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod3Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod4Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod5Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod6Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod7Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod8Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod9Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod10Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod11Depreciation1Amount());
                    amount = addAmount(amount, assetPayment.getPeriod12Depreciation1Amount());
                }
            }
        }
        return amount;
    }

    /**
     * Helper methods uses university date service to identify the right depreciation month column for the current month
     * 
     * @param assetPayment Asset Payment Record
     * @return Depreciation Amount for current month
     */
    protected KualiDecimal getCurrentMonthDepreciationAmount(AssetPayment assetPayment) {
        Object[] emptyParams = new Object[] {};
        Integer currPeriod = Integer.valueOf(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
        KualiDecimal amount = null;
        try {
            amount = (KualiDecimal) (DEPR_AMT_FIELDS.get(currPeriod).invoke(assetPayment, emptyParams));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return amount;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    /**
     * Sets sum of depreciation for each asset payment record
     * 
     * @param asset Asset
     */
    protected void setPaymentYearToDate(Asset asset) {
        List<AssetPayment> assetPayments = asset.getAssetPayments();
        if (assetPayments != null) {
            for (AssetPayment assetPayment : assetPayments) {
                KualiDecimal yearToDate = new KualiDecimal(0);
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod1Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod2Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod3Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod4Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod5Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod6Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod7Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod8Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod9Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod10Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod11Depreciation1Amount());
                yearToDate = addAmount(yearToDate, assetPayment.getPeriod12Depreciation1Amount());
                assetPayment.setYearToDate(yearToDate);
            }
        }

    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
