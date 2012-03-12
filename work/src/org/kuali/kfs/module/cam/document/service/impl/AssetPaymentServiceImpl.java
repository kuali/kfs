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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAllocationType;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.dataaccess.AssetPaymentDao;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.util.distribution.AssetDistribution;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AssetPaymentServiceImpl implements AssetPaymentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private AssetPaymentDao assetPaymentDao;
    private ParameterService parameterService;
    private UniversityDateService universityDateService;
    private ObjectCodeService objectCodeService;
    private AssetRetirementService assetRetirementService;
    private AssetService assetService;
    private AssetGlobalService assetGlobalService;

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#getMaxSequenceNumber(org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public Integer getMaxSequenceNumber(Long capitalAssetNumber) {
        return this.getAssetPaymentDao().getMaxSquenceNumber(capitalAssetNumber);
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#isPaymentFederalOwned(org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public boolean isPaymentFederalOwned(AssetPayment assetPayment) {
        assetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.OBJECT_CODE_CURRENT);
        if (ObjectUtils.isNotNull(assetPayment.getObjectCodeCurrent())) {
            String subTypeCode = assetPayment.getObjectCodeCurrent().getFinancialObjectSubTypeCode();
            return this.getParameterService().getParameterValuesAsString(Asset.class, CamsConstants.Parameters.FEDERAL_OWNED_OBJECT_SUB_TYPES).contains(subTypeCode);
        }
        return false;
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#isPaymentEligibleForAccumDeprGLPosting(org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public boolean isPaymentEligibleForAccumDeprGLPosting(AssetPayment assetPayment) {
        KualiDecimal accumlatedDepreciationAmount = assetPayment.getAccumulatedPrimaryDepreciationAmount();
        return accumlatedDepreciationAmount == null ? false : !accumlatedDepreciationAmount.isZero();
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#isPaymentEligibleForCapitalizationGLPosting(org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public boolean isPaymentEligibleForCapitalizationGLPosting(AssetPayment assetPayment) {
        KualiDecimal accountChargeAmount = assetPayment.getAccountChargeAmount();
        return accountChargeAmount == null ? false : !accountChargeAmount.isZero();
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#isPaymentEligibleForOffsetGLPosting(org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public boolean isPaymentEligibleForOffsetGLPosting(AssetPayment assetPayment) {
        KualiDecimal accountChargeAmount = assetPayment.getAccountChargeAmount();
        if (assetPayment.getAccumulatedPrimaryDepreciationAmount() == null) {
            assetPayment.setAccumulatedPrimaryDepreciationAmount(KualiDecimal.ZERO);
        }
        KualiDecimal accumlatedDepreciationAmount = assetPayment.getAccumulatedPrimaryDepreciationAmount();
        return accountChargeAmount == null ? false : !accountChargeAmount.subtract(accumlatedDepreciationAmount).isZero();
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#isPaymentFinancialObjectActive(org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public boolean isPaymentFinancialObjectActive(AssetPayment assetPayment) {
        ObjectCode financialObjectCode = new ObjectCode();
        financialObjectCode.setUniversityFiscalYear(getUniversityDateService().getCurrentFiscalYear());
        financialObjectCode.setChartOfAccountsCode(assetPayment.getChartOfAccountsCode());
        financialObjectCode.setFinancialObjectCode(assetPayment.getFinancialObjectCode());
        financialObjectCode = (ObjectCode) getBusinessObjectService().retrieve(financialObjectCode);
        if (ObjectUtils.isNotNull(financialObjectCode)) {
            return financialObjectCode.isActive();
        }
        return false;
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#processApprovedAssetPayment(org.kuali.kfs.module.cam.document.AssetPaymentDocument)
     */
    public void processApprovedAssetPayment(AssetPaymentDocument document) {
        // Creating new asset payment records
        processPayments(document);
    }


    /**
     * Creates a new asset payment record for each new asset payment detail record and then save them
     * 
     * @param document
     */
    protected void processPayments(AssetPaymentDocument document) {
        List<AssetPaymentDetail> assetPaymentDetailLines = document.getSourceAccountingLines();
        List<AssetPaymentAssetDetail> assetPaymentAssetDetails = document.getAssetPaymentAssetDetail();
        List<PersistableBusinessObject> assetPayments = new ArrayList<PersistableBusinessObject>();
        Integer maxSequenceNo = new Integer(0);

        //instantiate asset payment distributor
        AssetDistribution paymentDistributor = document.getAssetPaymentDistributor();
        
        // Calculating the asset payments distributions for each individual asset on the list
        Map<String, Map<AssetPaymentAssetDetail, KualiDecimal>> assetPaymentDistributionMap = paymentDistributor.getAssetPaymentDistributions();

        try {
            // Creating a new payment record for each asset that has payments.
            for (AssetPaymentAssetDetail assetPaymentAssetDetail : assetPaymentAssetDetails) {

                maxSequenceNo = getMaxSequenceNumber(assetPaymentAssetDetail.getCapitalAssetNumber());

                KualiDecimal totalAmount = KualiDecimal.ZERO;
                for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetailLines) {

                    // Retrieve the asset payment from the distribution map
                    KualiDecimal amount = assetPaymentDistributionMap.get(assetPaymentDetail.getAssetPaymentDetailKey()).get(assetPaymentAssetDetail);
                    totalAmount = totalAmount.add(amount);

                    AssetPayment assetPayment = new AssetPayment(assetPaymentDetail);
                    assetPayment.setCapitalAssetNumber(assetPaymentAssetDetail.getCapitalAssetNumber());
                    assetPayment.setTransferPaymentCode(CamsConstants.AssetPayment.TRANSFER_PAYMENT_CODE_N);
                    assetPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    if (StringUtils.isBlank(assetPayment.getDocumentNumber())) {
                        assetPayment.setDocumentNumber(document.getDocumentNumber());
                    }
                    assetPayment.setAccountChargeAmount(amount);

                    KualiDecimal baseAmount = KualiDecimal.ZERO;

                    // If the object sub type is not in the list of federally owned object sub types, then...
                    ObjectCode objectCode = this.getObjectCodeService().getByPrimaryId(assetPaymentDetail.getPostingYear(), assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getFinancialObjectCode());

                    // Depreciation Base Amount will be assigned to each payment only when the object code's sub type code is not a
                    // federally owned one
                    if (!this.isNonDepreciableFederallyOwnedObjSubType(objectCode.getFinancialObjectSubTypeCode())) {
                        baseAmount = baseAmount.add(amount);
                    }
                    assetPayment.setPrimaryDepreciationBaseAmount(baseAmount);

                    // Resetting each period field its value with nulls
                    this.adjustPaymentAmounts(assetPayment, false, true);

                    // add new payment
                    assetPayments.add(assetPayment);
                }
                // *********************BEGIN - Updating Asset ***********************************************************
                // Retrieving the asset that will have its cost updated
                HashMap<String, Long> keys = new HashMap<String, Long>();
                keys.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, assetPaymentAssetDetail.getCapitalAssetNumber());
                Asset asset = (Asset) getBusinessObjectService().findByPrimaryKey(Asset.class, keys);

                // Set previous total cost 
                if (assetPaymentAssetDetail.getPreviousTotalCostAmount() == null) {
                    assetPaymentAssetDetail.setPreviousTotalCostAmount(new KualiDecimal(0));
                }
                
                // Setting the asset's new cost.
                asset.setTotalCostAmount(assetPaymentAssetDetail.getPreviousTotalCostAmount().add(totalAmount));

                // Setting the asset's financial object sub-type Code. Only when the asset doesn't have one.
                if (asset.getFinancialObjectSubTypeCode() == null || asset.getFinancialObjectSubTypeCode().trim().equals("")) {
                    asset.setFinancialObjectSubTypeCode(assetPaymentDetailLines.get(0).getObjectCode().getFinancialObjectSubTypeCode());
                }
                // Saving changes
                getBusinessObjectService().save(asset);
                // *********************END - Updating Asset ***********************************************************
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Finally, saving all the asset payment records.
        this.getBusinessObjectService().save(assetPayments);
    }

    
    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#adjustPaymentAmounts(org.kuali.kfs.module.cam.businessobject.AssetPayment,
     *      boolean, boolean)
     */
    public void adjustPaymentAmounts(AssetPayment assetPayment, boolean reverseAmount, boolean nullPeriodDepreciation) throws IllegalAccessException, InvocationTargetException {
        LOG.debug("Starting - adjustAmounts() ");
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(AssetPayment.class);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null && propertyDescriptor.getPropertyType() != null && KualiDecimal.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
                KualiDecimal amount = (KualiDecimal) readMethod.invoke(assetPayment);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                if (writeMethod != null && amount != null) {
                    // Reset periodic depreciation expenses
                    if (nullPeriodDepreciation && Pattern.matches(CamsConstants.SET_PERIOD_DEPRECIATION_AMOUNT_REGEX, writeMethod.getName().toLowerCase())) {
                        Object[] nullVal = new Object[] { null };
                        writeMethod.invoke(assetPayment, nullVal);
                    }
                    else if (reverseAmount) {
                        // reverse the amounts
                        writeMethod.invoke(assetPayment, (amount.negated()));
                    }
                }

            }
        }
        LOG.debug("Finished - adjustAmounts()");
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#isPaymentEligibleForGLPosting(org.kuali.kfs.module.cam.businessobject.AssetPayment)
     */
    public boolean isPaymentEligibleForGLPosting(AssetPayment assetPayment) {
        // Transfer payment code flag is not Y
        boolean isEligible = !CamsConstants.AssetPayment.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode());
        // Financial object code is currently active
        isEligible &= isPaymentFinancialObjectActive(assetPayment);
        // Payment is not federally funded
        isEligible &= !isPaymentFederalOwned(assetPayment);
        return isEligible;
    }

    /**
     * Checks if object sub type is a non-depreciable federally owned object sub type
     * 
     * @param string objectSubType2
     * @return true if is NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES
     */
    public boolean isNonDepreciableFederallyOwnedObjSubType(String objectSubType) {
        List<String> federallyOwnedObjectSubTypes = new ArrayList<String>();
        if (this.getParameterService().parameterExists(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES)) {
            federallyOwnedObjectSubTypes = new ArrayList<String>( this.getParameterService().getParameterValuesAsString(KfsParameterConstants.CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES) );
        }
        return federallyOwnedObjectSubTypes.contains(objectSubType);
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#extractPostedDatePeriod(org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail)
     */
    public boolean extractPostedDatePeriod(AssetPaymentDetail assetPaymentDetail) {
        boolean valid = true;
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_DATE, assetPaymentDetail.getExpenditureFinancialDocumentPostedDate());
        UniversityDate universityDate = (UniversityDate) businessObjectService.findByPrimaryKey(UniversityDate.class, primaryKeys);
        if (universityDate != null) {
            assetPaymentDetail.setPostingYear(universityDate.getUniversityFiscalYear());
            assetPaymentDetail.setPostingPeriodCode(universityDate.getUniversityFiscalAccountingPeriod());
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#getAssetPaymentDetailQuantity(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public Integer getAssetPaymentDetailQuantity(AssetGlobal assetGlobal) {
        Integer assetPaymentDetailQuantity = 0;
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            assetPaymentDetailQuantity++;
        }
        return assetPaymentDetailQuantity;
    }


    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#validateAssets(java.lang.String,
     *      org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public boolean validateAssets(String errorPath, Asset asset) {
        boolean valid = true;

        // Validating the asset is a capital asset
        if (!this.getAssetService().isCapitalAsset(asset)) {
            GlobalVariables.getMessageMap().putError(errorPath, CamsKeyConstants.Payment.ERROR_NON_CAPITAL_ASSET, asset.getCapitalAssetNumber().toString());
            valid &= false;
        }

        // Validating the asset hasn't been retired
        if (this.getAssetService().isAssetRetired(asset)) {
            GlobalVariables.getMessageMap().putError(errorPath, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, asset.getCapitalAssetNumber().toString());
            valid &= false;
        }
        return valid;
    }


    /**
     * This method determines whether or not an asset has different object sub type codes in its documents.
     * 
     * @return true when the asset has payments with object codes that point to different object sub type codes
     */
    public boolean hasDifferentObjectSubTypes(AssetPaymentDocument document) {
        List<String> subTypes = new ArrayList<String>();
        subTypes = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(Asset.class, CamsConstants.Parameters.OBJECT_SUB_TYPE_GROUPS) );

        List<AssetPaymentDetail> assetPaymentDetails = document.getSourceAccountingLines();
        List<String> validObjectSubTypes = new ArrayList<String>();

        String objectSubTypeCode = null;

        /*
         * Expected system parameter elements (object sub types). [BD,BF] [CM,CF,CO] [UC,UF,UO] [LI,LF]
         */

        // Getting the list of object sub type codes from the asset payments on the jsp.
        List<String> objectSubTypeList = new ArrayList<String>();
        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
            assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
            if (ObjectUtils.isNull(assetPaymentDetail.getObjectCode())) {
                return false;
            }
            objectSubTypeList.add(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode());
        }

        if (!SpringContext.getBean(AssetService.class).isObjectSubTypeCompatible(objectSubTypeList)) {
            return true;
        }

        List<AssetPaymentAssetDetail> assetPaymentAssetDetails = document.getAssetPaymentAssetDetail();
        for (AssetPaymentAssetDetail assetPaymentAssetDetail : assetPaymentAssetDetails) {
            assetPaymentAssetDetail.getAsset().refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
            List<AssetPayment> assetPayments = assetPaymentAssetDetail.getAsset().getAssetPayments();

            // Comparing against the already approved asset payments
            if (!assetPayments.isEmpty()) {
                for (AssetPayment assetPayment : assetPayments) {
                    String paymentSubObjectType = assetPayment.getFinancialObject().getFinancialObjectSubTypeCode();

                    validObjectSubTypes = new ArrayList<String>();
                    for (String subType : subTypes) {
                        validObjectSubTypes = Arrays.asList(StringUtils.split(subType, ","));
                        if (validObjectSubTypes.contains(paymentSubObjectType)) {
                            break;
                        }
                        validObjectSubTypes = new ArrayList<String>();
                    }
                    if (validObjectSubTypes.isEmpty())
                        validObjectSubTypes.add(paymentSubObjectType);

                    // Comparing the same asset payment document
                    for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
                        if (!validObjectSubTypes.contains(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode())) {
                            // Differences where found.
                            return true;
                        }
                    }
                }
            }
        }
        // If none object sub types are different...
        return false;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    public AssetPaymentDao getAssetPaymentDao() {
        return assetPaymentDao;
    }


    public void setAssetPaymentDao(AssetPaymentDao assetPaymentDao) {
        this.assetPaymentDao = assetPaymentDao;
    }

    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public AssetRetirementService getAssetRetirementService() {
        return assetRetirementService;
    }

    public void setAssetRetirementService(AssetRetirementService assetRetirementService) {
        this.assetRetirementService = assetRetirementService;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

	/**
	 * @see org.kuali.kfs.module.cam.document.service.AssetPaymentService#getAssetDistributionTypeColumnName(java.lang.String)
	 */
	public AssetPaymentAllocationType getAssetDistributionType(String distributionCode) {
		HashMap<String, String> keys = new HashMap<String, String>();
		keys.put("distributionCode", distributionCode);
		AssetPaymentAllocationType d = (AssetPaymentAllocationType) getBusinessObjectService().findByPrimaryKey(AssetPaymentAllocationType.class, keys);
		return d;
	}
	
}
