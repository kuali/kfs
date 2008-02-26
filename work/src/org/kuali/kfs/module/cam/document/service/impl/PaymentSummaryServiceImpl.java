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
package org.kuali.module.cams.service.impl;

import static org.kuali.module.cams.CamsConstants.DEPRECIATION_METHOD_SALVAGE_VALUE_CODE;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetDisposition;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.service.PaymentSummaryService;

public class PaymentSummaryServiceImpl implements PaymentSummaryService {

    private static final int TOTAL_MONTHS = 12;
    private static Map<String, Method> DEPR_AMT_FIELDS = new HashMap<String, Method>();
    static {
        try {
            Class<?>[] emptyParams = new Class[] {};
            DEPR_AMT_FIELDS.put("1", AssetPayment.class.getMethod("getPeriod1Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("2", AssetPayment.class.getMethod("getPeriod2Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("3", AssetPayment.class.getMethod("getPeriod3Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("4", AssetPayment.class.getMethod("getPeriod4Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("5", AssetPayment.class.getMethod("getPeriod5Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("6", AssetPayment.class.getMethod("getPeriod6Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("7", AssetPayment.class.getMethod("getPeriod7Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("8", AssetPayment.class.getMethod("getPeriod8Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("9", AssetPayment.class.getMethod("getPeriod9Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("10", AssetPayment.class.getMethod("getPeriod10Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("11", AssetPayment.class.getMethod("getPeriod11Depreciation1Amount", emptyParams));
            DEPR_AMT_FIELDS.put("12", AssetPayment.class.getMethod("getPeriod12Depreciation1Amount", emptyParams));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static final String ASSET_DISCOMPOSTION_CODE_MERGE = "M";
    public static final String ASSET_DISCOMPOSTION_CODE_SEPARATE = "S";
    
    private OptionsService optionsService;


    private KualiDecimal addAmount(KualiDecimal amount, KualiDecimal addend) {
        if (addend != null) {
            return amount.add(addend);
        }
        return amount;
    }

    public void calculateAndSetPaymentSummary(Asset asset) {
        asset.setPaymentTotalCost(calculatePaymentTotalCost(asset));
        asset.setAssetMergeHistory(getMergeHistroty(asset));
        asset.setAssetSeparateHistory(getSeparateHistroty(asset));
        asset.setAccumulatedDepreciation(calculatePrimaryAccumulatedDepreciation(asset));
        asset.setBaseAmount(calculatePrimaryBaseAmount(asset));
        asset.setBookValue(calculatePrimaryBookValue(asset));
        asset.setPrevYearDepreciation(calculatePrimaryPrevYearDepreciation(asset));
        asset.setYearToDateDepreciation(calculatePrimaryYTDDepreciation(asset));
        asset.setCurrentMonthDepreciation(calculatePrimaryCurrentMonthDepreciation(asset));
    }

    private AssetDisposition getMergeHistroty(Asset asset) {
        List<AssetDisposition> assetDispositon = asset.getAssetDisposition();
        
        for (AssetDisposition disposition : assetDispositon) {
            if (disposition.getDispositionCode().toUpperCase().equals(ASSET_DISCOMPOSTION_CODE_MERGE)) {
                return disposition;
            }
        } 
        return null;
    }
    
    private AssetDisposition getSeparateHistroty(Asset asset) {
        List<AssetDisposition> assetDispositon = asset.getAssetDisposition();
        
        for (AssetDisposition disposition : assetDispositon) {
            if (disposition.getDispositionCode().toUpperCase().equals(ASSET_DISCOMPOSTION_CODE_SEPARATE)) {
                return disposition;
            }
        } 
        return null;
    }
    
    private KualiDecimal calculatePaymentTotalCost(Asset asset) {
        List<AssetPayment> payments = asset.getAssetPayments();
        KualiDecimal totalCost = new KualiDecimal(0);
        for (AssetPayment payment : payments) {
            totalCost = addAmount(totalCost, payment.getAccountChargeAmount());
        }
        return totalCost;
    }


    private KualiDecimal calculatePrimaryAccumulatedDepreciation(Asset asset) {
        List<AssetPayment> assetPayments = asset.getAssetPayments();
        KualiDecimal amount = new KualiDecimal(0);
        if (assetPayments != null) {
            for (AssetPayment assetPayment : assetPayments) {
                amount = addAmount(amount, assetPayment.getAccumulatedPrimaryDepreciationAmount());
            }
        }
        return amount;
    }

    private KualiDecimal calculatePrimaryBaseAmount(Asset asset) {
        List<AssetPayment> assetPayments = asset.getAssetPayments();
        KualiDecimal amount = new KualiDecimal(0);
        if (assetPayments != null) {
            for (AssetPayment assetPayment : assetPayments) {
                amount = addAmount(amount, assetPayment.getPrimaryDepreciationBaseAmount());
            }
        }
        return amount;
    }


    private KualiDecimal calculatePrimaryBookValue(Asset asset) {
        KualiDecimal baseAmount = calculatePrimaryBaseAmount(asset);
        KualiDecimal accumDeprAmount = calculatePrimaryAccumulatedDepreciation(asset);
        KualiDecimal salvageAmount = asset.getSalvageAmount();
        if (DEPRECIATION_METHOD_SALVAGE_VALUE_CODE.equals(asset.getPrimaryDepreciationMethodCode()) && salvageAmount != null) {
            return baseAmount.subtract(accumDeprAmount).subtract(salvageAmount);
        }
        return baseAmount.subtract(accumDeprAmount);
    }


    private KualiDecimal calculatePrimaryCurrentMonthDepreciation(Asset asset) {
        List<AssetPayment> assetPayments = asset.getAssetPayments();
        KualiDecimal amount = new KualiDecimal(0);
        if (assetPayments != null) {
            for (AssetPayment assetPayment : assetPayments) {
                amount = addAmount(amount, getCurrentMonthDepreciationAmount(assetPayment));

            }
        }
        return amount;
    }


    private KualiDecimal calculatePrimaryPrevYearDepreciation(Asset asset) {
        List<AssetPayment> assetPayments = asset.getAssetPayments();
        KualiDecimal amount = new KualiDecimal(0);
        if (assetPayments != null) {
            for (AssetPayment assetPayment : assetPayments) {
                amount = addAmount(amount, assetPayment.getPreviousYearPrimaryDepreciationAmount());
            }
        }
        return amount;
    }

    private KualiDecimal calculatePrimaryYTDDepreciation(Asset asset) {
        List<AssetPayment> assetPayments = asset.getAssetPayments();
        KualiDecimal amount = new KualiDecimal(0);
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
        return amount;
    }

    private KualiDecimal getCurrentMonthDepreciationAmount(AssetPayment assetPayment) {
        Object[] emptyParams = new Object[] {};
        int fiscalStartMonth = Integer.parseInt(optionsService.getCurrentYearOptions().getUniversityFiscalYearStartMo());
        int currPeriod = ((TOTAL_MONTHS + GregorianCalendar.getInstance().get(Calendar.MONTH) + 1 - fiscalStartMonth) % 12) + 1;

        KualiDecimal amount = null;

        try {
            amount = (KualiDecimal) (DEPR_AMT_FIELDS.get(String.valueOf(currPeriod)).invoke(assetPayment, emptyParams));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return amount;
    }

    public OptionsService getOptionsService() {
        return optionsService;
    }

    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

}
