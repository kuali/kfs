/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.cam.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class is a calculator which will distribute the amounts and balance them by ratio. Inputs received are
 * <li>Source Asset</li>
 * <li>Source Payments</li>
 * <li>Current max of payment number used by source Asset</li>
 * <li>AssetGlobal Document performing the separate action</li>
 * <li>List of new assets to be created for this separate request document</li>
 * Logic is best explained as below
 * <li>Compute the ratio of amounts to be removed from source payments</li>
 * <li>Compute the ratio by which each new asset should receive the allocated amount</li>
 * <li>Separate the allocate amount from the source payment using ratio computed above</li>
 * <li>Apply the allocate amount by ratio to each new asset</li>
 * <li>Adjust the last payment to round against the source from which split is done</li>
 * <li>Adjust the account charge amount of each asset by rounding the last payment with reference to user input separate amount</li>
 * <li>Create offset payments for the source asset</li>
 * <li>Compute accumulated depreciation amount for each payment, including offsets</li>
 */
public class AssetSeparatePaymentDistributor {
    private Asset sourceAsset;
    private AssetGlobal assetGlobal;
    private List<Asset> newAssets;
    private List<AssetPayment> sourcePayments = new ArrayList<AssetPayment>();
    private List<AssetPayment> separatedPayments = new ArrayList<AssetPayment>();
    private List<AssetPayment> offsetPayments = new ArrayList<AssetPayment>();
    private List<AssetPayment> remainingPayments = new ArrayList<AssetPayment>();
    private HashMap<Long, KualiDecimal> totalByAsset = new HashMap<Long, KualiDecimal>();
    private HashMap<Integer, List<AssetPayment>> paymentSplitMap = new HashMap<Integer, List<AssetPayment>>();
    private double[] assetAllocateRatios;
    private double separateRatio;
    private double retainRatio;
    private Integer maxPaymentSeqNo;
    private static PropertyDescriptor[] assetPaymentProperties = PropertyUtils.getPropertyDescriptors(AssetPayment.class);


    /**
     * Constructs a AssetSeparatePaymentDistributor.java.
     * 
     * @param sourceAsset Source Asset
     * @param sourcePayments Source Payments
     * @param maxPaymentSeqNo Current max of payment number used by source Asset
     * @param assetGlobal AssetGlobal Document performing the separate action
     * @param newAssets List of new assets to be created for this separate request document
     */
    public AssetSeparatePaymentDistributor(Asset sourceAsset, List<AssetPayment> sourcePayments, Integer maxPaymentSeqNo, AssetGlobal assetGlobal, List<Asset> newAssets) {
        super();
        this.sourceAsset = sourceAsset;
        this.sourcePayments = sourcePayments;
        this.maxPaymentSeqNo = maxPaymentSeqNo;
        this.assetGlobal = assetGlobal;
        this.newAssets = newAssets;
    }


    public void distribute() {
        KualiDecimal totalSourceAmount = this.assetGlobal.getTotalCostAmount();
        KualiDecimal totalSeparateAmount = this.assetGlobal.getSeparateSourceTotalAmount();
        KualiDecimal remainingAmount = totalSourceAmount.subtract(totalSeparateAmount);
        // Compute separate ratio
        separateRatio = totalSeparateAmount.doubleValue() / totalSourceAmount.doubleValue();
        // Compute the retained ratio
        retainRatio = remainingAmount.doubleValue() / totalSourceAmount.doubleValue();
        List<AssetGlobalDetail> assetGlobalDetails = this.assetGlobal.getAssetGlobalDetails();
        int size = assetGlobalDetails.size();
        assetAllocateRatios = new double[size];
        AssetGlobalDetail assetGlobalDetail = null;
        // Compute ratio by each asset
        for (int i = 0; i < size; i++) {
            assetGlobalDetail = assetGlobalDetails.get(i);
            Long capitalAssetNumber = assetGlobalDetail.getCapitalAssetNumber();
            totalByAsset.put(capitalAssetNumber, KualiDecimal.ZERO);
            assetAllocateRatios[i] = assetGlobalDetail.getSeparateSourceAmount().doubleValue() / totalSeparateAmount.doubleValue();
        }
        // Prepare the source and offset payments for split
        prepareSourcePaymentsForSplit();
        // Distribute payments by ratio
        allocatePaymentAmountsByRatio();
        // Round and balance by each payment line
        roundPaymentAmounts();
        // Round and balance by separate source amount
        roundAccountChargeAmount();
        // create offset payments
        createOffsetPayments();
    }


    /**
     * Split the amount to be assigned from the source payments
     */
    private void prepareSourcePaymentsForSplit() {
        // Call the allocate with ratio for each payments
        for (AssetPayment assetPayment : this.sourcePayments) {
            if (assetPayment.getAccountChargeAmount() != null && assetPayment.getAccountChargeAmount().isNonZero()) {
                // Separate amount
                AssetPayment separatePayment = new AssetPayment();
                ObjectValueUtils.copySimpleProperties(assetPayment, separatePayment);
                this.separatedPayments.add(separatePayment);

                // Remaining amount
                AssetPayment remainingPayment = new AssetPayment();
                ObjectValueUtils.copySimpleProperties(assetPayment, remainingPayment);
                this.remainingPayments.add(remainingPayment);

                applyRatioToPaymentAmounts(assetPayment, new AssetPayment[] { separatePayment, remainingPayment }, new double[] { separateRatio, retainRatio });
            }
        }


    }

    /**
     * Creates offset payment by copying and negating the separated payments
     */
    private void createOffsetPayments() {
        // create offset payment by negating the amount fields
        for (AssetPayment separatePayment : this.separatedPayments) {
            AssetPayment offsetPayment = new AssetPayment();
            ObjectValueUtils.copySimpleProperties(separatePayment, offsetPayment);
            try {
                negatePaymentAmounts(offsetPayment);
            }
            catch (Exception e) {
                throw new RuntimeException();
            }
            offsetPayment.setDocumentNumber(assetGlobal.getDocumentNumber());
            offsetPayment.setFinancialDocumentTypeCode(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE);
            offsetPayment.setVersionNumber(null);
            offsetPayment.setObjectId(null);
            offsetPayment.setPaymentSequenceNumber(++maxPaymentSeqNo);
            this.offsetPayments.add(offsetPayment);
        }
        this.sourceAsset.getAssetPayments().addAll(this.offsetPayments);
    }

    /**
     * Applies the asset allocate ratio for each payment line to be created and adds to the new asset. In addition it keeps track of
     * how amount is consumed by each asset and how each payment is being split
     */
    private void allocatePaymentAmountsByRatio() {
        int index = 0;
        for (AssetPayment source : this.separatedPayments) {

            // for each source payment, create target payments by ratio
            AssetPayment[] targets = new AssetPayment[assetAllocateRatios.length];
            for (int j = 0; j < assetAllocateRatios.length; j++) {
                AssetPayment newPayment = new AssetPayment();
                ObjectValueUtils.copySimpleProperties(source, newPayment);
                Asset currentAsset = this.newAssets.get(j);
                Long capitalAssetNumber = currentAsset.getCapitalAssetNumber();
                newPayment.setCapitalAssetNumber(capitalAssetNumber);
                newPayment.setDocumentNumber(assetGlobal.getDocumentNumber());
                newPayment.setFinancialDocumentTypeCode(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE);
                targets[j] = newPayment;
                newPayment.setVersionNumber(null);
                newPayment.setObjectId(null);
                currentAsset.getAssetPayments().add(index, newPayment);
            }
            applyRatioToPaymentAmounts(source, targets, assetAllocateRatios);

            // keep track of split happened for the source
            this.paymentSplitMap.put(source.getPaymentSequenceNumber(), Arrays.asList(targets));

            // keep track of total amount by asset
            for (int j = 0; j < targets.length; j++) {
                Asset currentAsset = this.newAssets.get(j);
                Long capitalAssetNumber = currentAsset.getCapitalAssetNumber();
                this.totalByAsset.put(capitalAssetNumber, this.totalByAsset.get(capitalAssetNumber).add(targets[j].getAccountChargeAmount()));
            }
            index++;
        }
    }

    /**
     * Rounds the last payment by adjusting the amounts against source amount
     */
    private void roundPaymentAmounts() {
        for (int i = 0; i < this.separatedPayments.size(); i++) {
            applyBalanceToPaymentAmounts(separatedPayments.get(i), this.paymentSplitMap.get(separatedPayments.get(i).getPaymentSequenceNumber()));
        }
    }

    /**
     * Rounds the last payment by adjusting the amount compared against separate source amount and copies account charge amount to
     * primary depreciation base amount if not zero
     */
    private void roundAccountChargeAmount() {
        for (int j = 0; j < this.newAssets.size(); j++) {
            Asset currentAsset = this.newAssets.get(j);
            AssetGlobalDetail detail = this.assetGlobal.getAssetGlobalDetails().get(j);
            AssetPayment lastPayment = currentAsset.getAssetPayments().get(currentAsset.getAssetPayments().size() - 1);
            KualiDecimal totalForAsset = this.totalByAsset.get(currentAsset.getCapitalAssetNumber());
            KualiDecimal diff = detail.getSeparateSourceAmount().subtract(totalForAsset);
            lastPayment.setAccountChargeAmount(lastPayment.getAccountChargeAmount().add(diff));
            currentAsset.setTotalCostAmount(totalForAsset.add(diff));
            AssetPayment lastSource = this.separatedPayments.get(this.separatedPayments.size() - 1);
            lastSource.setAccountChargeAmount(lastSource.getAccountChargeAmount().add(diff));
            // adjust primary depreciation base amount, same as account charge amount
            if (lastPayment.getPrimaryDepreciationBaseAmount() != null && lastPayment.getPrimaryDepreciationBaseAmount().isNonZero()) {
                lastPayment.setPrimaryDepreciationBaseAmount(lastPayment.getAccountChargeAmount());
                lastSource.setPrimaryDepreciationBaseAmount(lastSource.getAccountChargeAmount());
            }
        }
    }

    /**
     * Utility method which can take one payment and distribute its amount by ratio to the target payments
     * 
     * @param source Source Payment
     * @param targets Target Payment
     * @param ratios Ratio to be applied for each target
     */
    private void applyRatioToPaymentAmounts(AssetPayment source, AssetPayment[] targets, double[] ratios) {
        try {
            for (PropertyDescriptor propertyDescriptor : assetPaymentProperties) {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null && propertyDescriptor.getPropertyType() != null && KualiDecimal.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                    KualiDecimal amount = (KualiDecimal) readMethod.invoke(source);
                    if (amount != null && amount.isNonZero()) {
                        KualiDecimal[] ratioAmounts = KualiDecimalUtils.allocateByRatio(amount, ratios);
                        Method writeMethod = propertyDescriptor.getWriteMethod();
                        if (writeMethod != null) {
                            for (int i = 0; i < ratioAmounts.length; i++) {
                                writeMethod.invoke(targets[i], ratioAmounts[i]);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Utility method which can compute the difference between source amount and consumed amounts, then will adjust the last amount
     * 
     * @param source Source payments
     * @param consumedList Consumed Payments
     */
    private void applyBalanceToPaymentAmounts(AssetPayment source, List<AssetPayment> consumedList) {
        try {
            for (PropertyDescriptor propertyDescriptor : assetPaymentProperties) {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null && propertyDescriptor.getPropertyType() != null && KualiDecimal.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                    KualiDecimal amount = (KualiDecimal) readMethod.invoke(source);
                    if (amount != null && amount.isNonZero()) {
                        Method writeMethod = propertyDescriptor.getWriteMethod();
                        KualiDecimal consumedAmount = KualiDecimal.ZERO;
                        KualiDecimal currAmt = KualiDecimal.ZERO;
                        if (writeMethod != null) {
                            for (int i = 0; i < consumedList.size(); i++) {
                                currAmt = (KualiDecimal) readMethod.invoke(consumedList.get(i));
                                consumedAmount = consumedAmount.add(currAmt != null ? currAmt : KualiDecimal.ZERO);
                            }
                        }
                        if (!consumedAmount.equals(amount)) {
                            AssetPayment lastPayment = consumedList.get(consumedList.size() - 1);
                            writeMethod.invoke(lastPayment, currAmt.add(amount.subtract(consumedAmount)));
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Utility method which will negate the payment amounts for a given payment
     * 
     * @param assetPayment Payment to be negated
     */
    public void negatePaymentAmounts(AssetPayment assetPayment) {
        try {
            for (PropertyDescriptor propertyDescriptor : assetPaymentProperties) {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null && propertyDescriptor.getPropertyType() != null && KualiDecimal.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                    KualiDecimal amount = (KualiDecimal) readMethod.invoke(assetPayment);
                    Method writeMethod = propertyDescriptor.getWriteMethod();
                    if (writeMethod != null && amount != null) {
                        writeMethod.invoke(assetPayment, (amount.negated()));
                    }

                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sums up YTD values and Previous Year value to decide accumulated depreciation amount
     */
    private void computeAccumulatedDepreciationAmount() {
        KualiDecimal previousYearAmount = null;
        for (Asset asset : this.newAssets) {
            List<AssetPayment> assetPayments = asset.getAssetPayments();
            for (AssetPayment currPayment : assetPayments) {
                previousYearAmount = currPayment.getPreviousYearPrimaryDepreciationAmount();
                previousYearAmount = previousYearAmount == null ? KualiDecimal.ZERO : previousYearAmount;
                KualiDecimal computedAmount = previousYearAmount.add(sumPeriodicDepreciationAmounts(currPayment));
                if (computedAmount.isNonZero()) {
                    currPayment.setAccumulatedPrimaryDepreciationAmount(computedAmount);
                }
            }
        }
        for (AssetPayment currPayment : this.offsetPayments) {
            previousYearAmount = currPayment.getPreviousYearPrimaryDepreciationAmount();
            previousYearAmount = previousYearAmount == null ? KualiDecimal.ZERO : previousYearAmount;
            KualiDecimal computedAmount = previousYearAmount.add(sumPeriodicDepreciationAmounts(currPayment));
            if (computedAmount.isNonZero()) {
                currPayment.setAccumulatedPrimaryDepreciationAmount(computedAmount);
            }
        }
    }

    /**
     * Sums up periodic amounts for a payment
     * 
     * @param currPayment Payment
     * @return Sum of payment
     */
    public static KualiDecimal sumPeriodicDepreciationAmounts(AssetPayment currPayment) {
        KualiDecimal ytdAmount = KualiDecimal.ZERO;
        try {
            for (PropertyDescriptor propertyDescriptor : assetPaymentProperties) {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod != null && Pattern.matches(CamsConstants.GET_PERIOD_DEPRECIATION_AMOUNT_REGEX, readMethod.getName().toLowerCase()) && propertyDescriptor.getPropertyType() != null && KualiDecimal.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                    KualiDecimal amount = (KualiDecimal) readMethod.invoke(currPayment);
                    if (amount != null) {
                        ytdAmount = ytdAmount.add(amount);
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ytdAmount;
    }

    /**
     * Gets the remainingPayments attribute.
     * 
     * @return Returns the remainingPayments.
     */
    public List<AssetPayment> getRemainingPayments() {
        return remainingPayments;
    }


    /**
     * Sets the remainingPayments attribute value.
     * 
     * @param remainingPayments The remainingPayments to set.
     */
    public void setRemainingPayments(List<AssetPayment> remainingPayments) {
        this.remainingPayments = remainingPayments;
    }


    /**
     * Gets the offsetPayments attribute.
     * 
     * @return Returns the offsetPayments.
     */
    public List<AssetPayment> getOffsetPayments() {
        return offsetPayments;
    }


    /**
     * Sets the offsetPayments attribute value.
     * 
     * @param offsetPayments The offsetPayments to set.
     */
    public void setOffsetPayments(List<AssetPayment> offsetPayments) {
        this.offsetPayments = offsetPayments;
    }


    /**
     * Gets the separatedPayments attribute.
     * 
     * @return Returns the separatedPayments.
     */
    public List<AssetPayment> getSeparatedPayments() {
        return separatedPayments;
    }


    /**
     * Sets the separatedPayments attribute value.
     * 
     * @param separatedPayments The separatedPayments to set.
     */
    public void setSeparatedPayments(List<AssetPayment> separatedPayments) {
        this.separatedPayments = separatedPayments;
    }


    /**
     * Gets the assetGlobal attribute.
     * 
     * @return Returns the assetGlobal.
     */
    public AssetGlobal getAssetGlobal() {
        return assetGlobal;
    }


    /**
     * Sets the assetGlobal attribute value.
     * 
     * @param assetGlobal The assetGlobal to set.
     */
    public void setAssetGlobal(AssetGlobal assetGlobal) {
        this.assetGlobal = assetGlobal;
    }


    /**
     * Gets the newAssets attribute.
     * 
     * @return Returns the newAssets.
     */
    public List<Asset> getNewAssets() {
        return newAssets;
    }


    /**
     * Sets the newAssets attribute value.
     * 
     * @param newAssets The newAssets to set.
     */
    public void setNewAssets(List<Asset> newAssets) {
        this.newAssets = newAssets;
    }


    /**
     * Gets the assetAllocateRatios attribute.
     * 
     * @return Returns the assetAllocateRatios.
     */
    public double[] getAssetAllocateRatios() {
        return assetAllocateRatios;
    }


    /**
     * Sets the assetAllocateRatios attribute value.
     * 
     * @param assetAllocateRatios The assetAllocateRatios to set.
     */
    public void setAssetAllocateRatios(double[] assetAllocateRatios) {
        this.assetAllocateRatios = assetAllocateRatios;
    }


    /**
     * Gets the separateRatio attribute.
     * 
     * @return Returns the separateRatio.
     */
    public double getSeparateRatio() {
        return separateRatio;
    }


    /**
     * Sets the separateRatio attribute value.
     * 
     * @param separateRatio The separateRatio to set.
     */
    public void setSeparateRatio(double separateRatio) {
        this.separateRatio = separateRatio;
    }


    /**
     * Gets the retainRatio attribute.
     * 
     * @return Returns the retainRatio.
     */
    public double getRetainRatio() {
        return retainRatio;
    }


    /**
     * Sets the retainRatio attribute value.
     * 
     * @param retainRatio The retainRatio to set.
     */
    public void setRetainRatio(double retainRatio) {
        this.retainRatio = retainRatio;
    }


    /**
     * Gets the sourcePayments attribute.
     * 
     * @return Returns the sourcePayments.
     */
    public List<AssetPayment> getSourcePayments() {
        return sourcePayments;
    }


    /**
     * Sets the sourcePayments attribute value.
     * 
     * @param sourcePayments The sourcePayments to set.
     */
    public void setSourcePayments(List<AssetPayment> sourcePayments) {
        this.sourcePayments = sourcePayments;
    }


}
