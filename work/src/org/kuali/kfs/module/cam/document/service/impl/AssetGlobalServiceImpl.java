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
package org.kuali.kfs.module.cam.document.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetAcquisitionType;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetGlpeSourceDetail;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.service.AssetObjectCodeService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.util.KualiDecimalUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.ParameterConstants.CAPITAL_ASSETS_BATCH;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class AssetGlobalServiceImpl implements AssetGlobalService {

    private enum AmountCategory {

        PAYMENT {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setPayment(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_PAYMENT);
                postable.setFinancialObjectCode(assetPaymentDetail.getFinancialObjectCode());
                postable.setObjectCode(assetPaymentDetail.getObjectCode());
            };

        },
        PAYMENT_OFFSET {
            void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType) {
                postable.setPaymentOffset(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetGlobal.LINE_DESCRIPTION_PAYMENT_OFFSET);
                postable.setFinancialObjectCode(acquisitionType.getIncomeAssetObjectCode());
                postable.setObjectCode(SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(assetPaymentDetail.getPostingYear(), assetPaymentDetail.getChartOfAccountsCode(), acquisitionType.getIncomeAssetObjectCode()));
            };

        };

        abstract void setParams(AssetGlpeSourceDetail postable, AssetPaymentDetail assetPaymentDetail, AssetObjectCode assetObjectCode, OffsetDefinition offsetDefinition, AssetAcquisitionType acquisitionType);
    }

    private ParameterService parameterService;
    private AssetService assetService;
    private UniversityDateService universityDateService;
    private AssetObjectCodeService assetObjectCodeService;
    private BusinessObjectService businessObjectService;
    private AssetPaymentService assetPaymentService;

    private static final Logger LOG = Logger.getLogger(AssetGlobalServiceImpl.class);

    // ***********
    /**
     * Creates an instance of AssetGlpeSourceDetail depending on the source flag
     * 
     * @param universityDateService University Date Service
     * @param assetPayment Payment record for which postable is created
     * @param isSource Indicates if postable is for source organization
     * @return GL Postable source detail
     */
    protected AssetGlpeSourceDetail createAssetGlpePostable(AssetGlobal document, AssetPaymentDetail assetPaymentDetail, AmountCategory amountCategory) {
        LOG.debug("Start - createAssetGlpePostable (" + document.getDocumentNumber() + "-" + assetPaymentDetail.getAccountNumber() + ")");
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();

        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.ACCOUNT);
        postable.setAccount(assetPaymentDetail.getAccount());

        postable.setAmount(assetPaymentDetail.getAmount());
        postable.setAccountNumber(assetPaymentDetail.getAccountNumber());
        postable.setBalanceTypeCode(CamsConstants.Postable.GL_BALANCE_TYPE_CODE_AC);
        postable.setChartOfAccountsCode(assetPaymentDetail.getChartOfAccountsCode());
        postable.setDocumentNumber(document.getDocumentNumber());
        postable.setFinancialSubObjectCode(assetPaymentDetail.getFinancialSubObjectCode());
        postable.setPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        postable.setProjectCode(assetPaymentDetail.getProjectCode());
        postable.setSubAccountNumber(assetPaymentDetail.getSubAccountNumber());
        postable.setOrganizationReferenceId(assetPaymentDetail.getOrganizationReferenceId());

        assetPaymentDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentDetail.OBJECT_CODE);
        AssetObjectCode assetObjectCode = getAssetObjectCodeService().findAssetObjectCode(assetPaymentDetail.getChartOfAccountsCode(), assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode());

        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), assetPaymentDetail.getChartOfAccountsCode(), CamsConstants.AssetTransfer.DOCUMENT_TYPE_CODE, CamsConstants.Postable.GL_BALANCE_TYPE_CODE_AC);
        document.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ACQUISITION_TYPE);
        amountCategory.setParams(postable, assetPaymentDetail, assetObjectCode, offsetDefinition, document.getAcquisitionType());

        LOG.debug("End - createAssetGlpePostable(" + document.getDocumentNumber() + "-" + assetPaymentDetail.getAccountNumber() + "-" + ")");
        return postable;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#createGLPostables(org.kuali.module.cams.document.AssetGlobal)
     */
    public void createGLPostables(AssetGlobal assetGlobal, CamsGeneralLedgerPendingEntrySourceBase assetGlobalGlPoster) {
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();

        for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
            if (isPaymentFinancialObjectActive(assetPaymentDetail)) {
                KualiDecimal accountChargeAmount = assetPaymentDetail.getAmount();
                if (accountChargeAmount != null && !accountChargeAmount.isZero()) {
                    assetGlobalGlPoster.getGeneralLedgerPendingEntrySourceDetails().add(createAssetGlpePostable(assetGlobal, assetPaymentDetail, AmountCategory.PAYMENT));
                    assetGlobalGlPoster.getGeneralLedgerPendingEntrySourceDetails().add(createAssetGlpePostable(assetGlobal, assetPaymentDetail, AmountCategory.PAYMENT_OFFSET));
                }
            }
        }
    }


    public ParameterService getParameterService() {
        return parameterService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    /**
     * Gets the assetObjectCodeService attribute.
     * 
     * @return Returns the assetObjectCodeService.
     */
    public AssetObjectCodeService getAssetObjectCodeService() {
        return assetObjectCodeService;
    }


    /**
     * Sets the assetObjectCodeService attribute value.
     * 
     * @param assetObjectCodeService The assetObjectCodeService to set.
     */
    public void setAssetObjectCodeService(AssetObjectCodeService assetObjectCodeService) {
        this.assetObjectCodeService = assetObjectCodeService;
    }


    /**
     * Gets the assetPaymentService attribute.
     * 
     * @return Returns the assetPaymentService.
     */
    public AssetPaymentService getAssetPaymentService() {
        return assetPaymentService;
    }


    /**
     * Sets the assetPaymentService attribute value.
     * 
     * @param assetPaymentService The assetPaymentService to set.
     */
    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }
    
    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * Gets the universityDateService attribute.
     * 
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }


    /**
     * Sets the universityDateService attribute value.
     * 
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * We need to calculate asset total amount from the sum of its payments. Otherwise, the asset total amount could mismatch with
     * the sum of payments.
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#totalPaymentByAsset(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public KualiDecimal totalPaymentByAsset(AssetGlobal assetGlobal, boolean lastEntry) {
        KualiDecimal assetTotalAmount = KualiDecimal.ZERO;
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
        int numberOfTotalAsset = assetGlobal.getAssetGlobalDetails().size();
        if (numberOfTotalAsset > 0) {
            for (AssetPaymentDetail assetPaymentDetail : assetPaymentDetails) {
                KualiDecimal assetPaymentUnitCost = assetPaymentDetail.getAmount().divide(new KualiDecimal(numberOfTotalAsset));
                if (lastEntry) {
                    assetPaymentUnitCost = assetPaymentDetail.getAmount().subtract(assetPaymentUnitCost.multiply(new KualiDecimal(numberOfTotalAsset - 1)));
                }
                assetTotalAmount = assetTotalAmount.add(assetPaymentUnitCost);
            }
        }

        return assetTotalAmount;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#existsInGroup(java.lang.String, java.lang.String)
     */
    public boolean existsInGroup(String groupName, String memberName) {
        if (StringUtils.isBlank(groupName) || StringUtils.isBlank(memberName)) {
            return false;
        }
        return Arrays.asList(groupName.split(";")).contains(memberName);
    }

    private boolean isPaymentFinancialObjectActive(AssetPaymentDetail assetPayment) {
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
     * Gets the assetService attribute.
     * 
     * @return Returns the assetService.
     */
    public AssetService getAssetService() {
        return assetService;
    }

    /**
     * Sets the assetService attribute value.
     * 
     * @param assetService The assetService to set.
     */
    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#isAssetSeparate(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public boolean isAssetSeparate(AssetGlobal assetGlobal) {
        if (ObjectUtils.isNotNull(assetGlobal.getFinancialDocumentTypeCode()) && assetGlobal.getFinancialDocumentTypeCode().equals(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE)) {
            return true;
        }
        return false;
    }
    
    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#isAssetSeparateByPayment(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public boolean isAssetSeparateByPayment(AssetGlobal assetGlobal) {
        if (this.isAssetSeparate(assetGlobal) && ObjectUtils.isNotNull(assetGlobal.getSeparateSourcePaymentSequenceNumber())) {
            return true;
        }
        return false;
    }

    /**
     * Add and return the total amount for all unique assets created.
     * 
     * @param assetGlobal
     * @return Returns the total separate source amount
     */
    public KualiDecimal getUniqueAssetsTotalAmount(AssetGlobal assetGlobal) {
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        // add new asset location
        for (AssetGlobalDetail assetSharedDetail : assetGlobal.getAssetSharedDetails()) {
            // existing
            for (AssetGlobalDetail assetGlobalUniqueDetail : assetSharedDetail.getAssetGlobalUniqueDetails()) {
                KualiDecimal separateSourceAmount = assetGlobalUniqueDetail.getSeparateSourceAmount();
                if (separateSourceAmount != null) {
                    totalAmount = totalAmount.add(separateSourceAmount);
                }
            }
        }
        return totalAmount;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#getCreateNewAssets(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public List<PersistableBusinessObject> getCreateNewAssets(AssetGlobal assetGlobal) {
        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();

        // create new assets with inner loop handling payments
        Iterator<AssetGlobalDetail> assetGlobalDetailsIterator = assetGlobal.getAssetGlobalDetails().iterator();
        for (int assetGlobalDetailsIndex = 0; assetGlobalDetailsIterator.hasNext(); assetGlobalDetailsIndex++) {
            AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) assetGlobalDetailsIterator.next();

            // Create the asset with most fields set as required
            Asset asset = setupAsset(assetGlobal, assetGlobalDetail, false);

            // track total cost of all payments for this assetGlobalDetail
            KualiDecimal paymentsAccountChargeAmount = new KualiDecimal(0);

            // take care of all the payments for this asset
            for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
                AssetPayment assetPayment = setupCreateNewAssetPayment(assetGlobalDetail.getCapitalAssetNumber(), assetGlobal.getAcquisitionTypeCode(), assetGlobal.getAssetGlobalDetails().size(), assetGlobalDetailsIndex, assetPaymentDetail);

                paymentsAccountChargeAmount = paymentsAccountChargeAmount.add(assetPayment.getAccountChargeAmount());

                asset.getAssetPayments().add(assetPayment);
            }

            // set the amount generically. Note for separate this should equal assetGlobalDetail.getSeparateSourceAmount()
            asset.setTotalCostAmount(paymentsAccountChargeAmount);

            persistables.add(asset);
        }

        return persistables;
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.AssetGlobalService#getSeparateAssets(org.kuali.kfs.module.cam.businessobject.AssetGlobal)
     */
    public List<PersistableBusinessObject> getSeparateAssets(AssetGlobal assetGlobal) {
        List<PersistableBusinessObject> persistables = new ArrayList<PersistableBusinessObject>();
        KualiDecimal assetsAccountChargeAmount = new KualiDecimal(0);

        // set the source asset amounts properly
        Asset separateSourceCapitalAsset = assetGlobal.getSeparateSourceCapitalAsset();

        // Need to make a copy because offsetPayments are changed and saved
        HashMap<String, AssetPayment> offsetAssetPayments = new HashMap<String, AssetPayment>();
        for (AssetPayment assetPayment : separateSourceCapitalAsset.getAssetPayments()) {
            if (!this.isAssetSeparateByPayment(assetGlobal)) {
                offsetAssetPayments.put(assetPayment.getObjectId(), new AssetPayment(assetPayment, false));
            } else if (assetPayment.getPaymentSequenceNumber().equals(assetGlobal.getSeparateSourcePaymentSequenceNumber())) {
                // If this is separate by payment, then only add the payment that we are interested in
                offsetAssetPayments.put(assetPayment.getObjectId(), new AssetPayment(assetPayment, false));
                break;
            }
        }

        // create new assets with inner loop handling payments
        for (AssetGlobalDetail assetGlobalDetail : assetGlobal.getAssetGlobalDetails()) {
            // Create the asset with most fields set as required
            Asset asset = setupAsset(assetGlobal, assetGlobalDetail, true);

            // track total cost of all payments for this assetGlobalDetail
            KualiDecimal paymentsAccountChargeAmount = new KualiDecimal(0);

            // take care of all the payments for this asset
            for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
                AssetPayment assetPayment = setupSeparateAssetPayment(assetGlobalDetail.getCapitalAssetNumber(), assetGlobal.getAcquisitionTypeCode(), assetGlobalDetail.getSeparateSourceAmount(), assetGlobal.getTotalCostAmount(), assetPaymentDetail, separateSourceCapitalAsset.getAssetPayments(), offsetAssetPayments);

                paymentsAccountChargeAmount = paymentsAccountChargeAmount.add(assetPayment.getAccountChargeAmount());

                asset.getAssetPayments().add(assetPayment);
            }

            // set the amount generically. Note for separate this should equal assetGlobalDetail.getSeparateSourceAmount()
            asset.setTotalCostAmount(paymentsAccountChargeAmount);
            assetsAccountChargeAmount = assetsAccountChargeAmount.add(paymentsAccountChargeAmount);

            // TODO following is better done in a junit test case
            if (!asset.getTotalCostAmount().equals(assetGlobalDetail.getSeparateSourceAmount())) {
                throw new IllegalStateException("Unexpected amount calculation discreptancy while seperating by asset.");
            }
            
            persistables.add(asset);
        }

        // reduce the amount of the (new) target assets from the source asset
        separateSourceCapitalAsset.setTotalCostAmount(assetGlobal.getSeparateSourceCapitalAsset().getTotalCostAmount().subtract(assetsAccountChargeAmount));
        persistables.add(separateSourceCapitalAsset);

        int paymentSequenceNumber = assetPaymentService.getMaxSequenceNumber(separateSourceCapitalAsset.getCapitalAssetNumber()) + 1;

        // add all target payment to the source assets
        for (AssetPayment offsetAssetPayment : offsetAssetPayments.values()) {
            offsetAssetPayment.setPaymentSequenceNumber(paymentSequenceNumber++);
            offsetAssetPayment.setFinancialDocumentTypeCode(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE);
            offsetAssetPayment.setDocumentNumber(assetGlobal.getDocumentNumber());
            // Don't need to re-add original items because the payments list isn't deletion aware
            separateSourceCapitalAsset.getAssetPayments().add(offsetAssetPayment);
        }

        return persistables;
    }

    /**
     * Creates a new Asset appropriate for either create new or separate. This does not create the payments for this asset.
     * 
     * @param assetGlobal containing data for the asset to be created
     * @param assetGlobalDetail containing data for the asset to be created
     * @param separate indicating whether this is a separate and asset or not
     * @return set of assets appropriate for this creation without payments
     */
    protected Asset setupAsset(AssetGlobal assetGlobal, AssetGlobalDetail assetGlobalDetail, boolean separate) {
        Asset asset = new Asset(assetGlobal, assetGlobalDetail, separate);

        // set financialObjectSubTypeCode per first payment entry if one exists
        if (!assetGlobal.getAssetPaymentDetails().isEmpty() && ObjectUtils.isNotNull(assetGlobal.getAssetPaymentDetails().get(0).getObjectCode())) {
            asset.setFinancialObjectSubTypeCode(assetGlobal.getAssetPaymentDetails().get(0).getObjectCode().getFinancialObjectSubTypeCode());
        }

        // create off campus location for each asset only if it was filled in
        boolean offCampus = StringUtils.isNotBlank(assetGlobalDetail.getOffCampusName()) || StringUtils.isNotBlank(assetGlobalDetail.getOffCampusAddress()) || StringUtils.isNotBlank(assetGlobalDetail.getOffCampusCityName()) || StringUtils.isNotBlank(assetGlobalDetail.getOffCampusStateCode()) || StringUtils.isNotBlank(assetGlobalDetail.getOffCampusZipCode()) || StringUtils.isNotBlank(assetGlobalDetail.getOffCampusCountryCode());
        if (offCampus) {
            setupAssetLocationOffCampus(assetGlobalDetail, asset);
        }

        // set specific values for new assets if document is Asset Separate
        if (separate) {
            double separateRatio = assetGlobalDetail.getSeparateSourceAmount().doubleValue() / assetGlobal.getSeparateSourceCapitalAsset().getTotalCostAmount().doubleValue();

            asset.setSalvageAmount(safeMultiply(assetGlobal.getSeparateSourceCapitalAsset().getSalvageAmount(), separateRatio));
        }

        return asset;
    }

    /**
     * Off campus location appropriately set for this asset.
     * 
     * @param assetGlobalDetail containing data for the location
     * @param asset object that location is set in
     */
    protected void setupAssetLocationOffCampus(AssetGlobalDetail assetGlobalDetail, Asset asset) {
        // We are not checking if it already exists since on a new asset it can't
        AssetLocation offCampusAssetLocation = new AssetLocation();
        offCampusAssetLocation.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        offCampusAssetLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.OFF_CAMPUS);
        asset.getAssetLocations().add(offCampusAssetLocation);

        // Set the location fields either way
        offCampusAssetLocation.setAssetLocationContactName(assetGlobalDetail.getOffCampusName());
        offCampusAssetLocation.setAssetLocationContactIdentifier(assetGlobalDetail.getRepresentativeUniversalIdentifier());
        offCampusAssetLocation.setAssetLocationInstitutionName(assetGlobalDetail.getAssetRepresentative().getPrimaryDepartmentCode());
        offCampusAssetLocation.setAssetLocationStreetAddress(assetGlobalDetail.getOffCampusAddress());
        offCampusAssetLocation.setAssetLocationCityName(assetGlobalDetail.getOffCampusCityName());
        offCampusAssetLocation.setAssetLocationStateCode(assetGlobalDetail.getOffCampusStateCode());
        offCampusAssetLocation.setAssetLocationCountryCode(assetGlobalDetail.getOffCampusCountryCode());
        offCampusAssetLocation.setAssetLocationZipCode(assetGlobalDetail.getOffCampusZipCode());

        // There is no phone number field on Asset Global... odd...
        offCampusAssetLocation.setAssetLocationPhoneNumber(null);
    }

    /**
     * Creates a payment for an asset in create new mode.
     * 
     * @param capitalAssetNumber to use for the payment
     * @param acquisitionTypeCode for logic in determining how dates are to be set
     * @param assetGlobalDetailsSize for logic in determining depreciation amounts (if asset is depreciable)
     * @param assetGlobalDetailsIndex for logic in determining depreciation amounts (if asset is depreciable)
     * @param assetPaymentDetail containing data for the payment
     * @return payment for an asset in create new
     */
    protected AssetPayment setupCreateNewAssetPayment(Long capitalAssetNumber, String acquisitionTypeCode, int assetGlobalDetailsSize, int assetGlobalDetailsIndex, AssetPaymentDetail assetPaymentDetail) {
        AssetPayment assetPayment = new AssetPayment(assetPaymentDetail, acquisitionTypeCode);
        assetPayment.setCapitalAssetNumber(capitalAssetNumber);
        assetPayment.setPaymentSequenceNumber(assetPaymentDetail.getSequenceNumber());

        // Running this every time of the loop is inefficient, could be put into HashMap
        KualiDecimalUtils kualiDecimalService = new KualiDecimalUtils(assetPaymentDetail.getAmount(), CamsConstants.CURRENCY_USD);
        KualiDecimal[] amountBuckets = kualiDecimalService.allocate(assetGlobalDetailsSize);

        assetPayment.setAccountChargeAmount(amountBuckets[assetGlobalDetailsIndex]);

        boolean isDepreciablePayment = ObjectUtils.isNotNull(assetPaymentDetail.getObjectCode()) && !Arrays.asList(parameterService.getParameterValue(CAPITAL_ASSETS_BATCH.class, CamsConstants.Parameters.NON_DEPRECIABLE_FEDERALLY_OWNED_OBJECT_SUB_TYPES).split(";")).contains(assetPaymentDetail.getObjectCode().getFinancialObjectSubTypeCode());
        if (isDepreciablePayment) {
            assetPayment.setPrimaryDepreciationBaseAmount(amountBuckets[assetGlobalDetailsIndex]);
        }
        else {
            assetPayment.setPrimaryDepreciationBaseAmount(KualiDecimal.ZERO);
        }

        return assetPayment;
    }

    /**
     * Creates a payment for an asset in separate mode.
     * 
     * @param capitalAssetNumber to use for the payment
     * @param acquisitionTypeCode for logic in determining how dates are to be set
     * @param separateSourceAmount amount for this asset
     * @param totalCostToAllocate amount that may be allocated (in the case of splitting by asset it's the asset total cost, in the case of splitting by payment it's the payment to be split cost)
     * @param assetPaymentDetail containing data for the payment
     * @param separateSourceCapitalAssetPayments AssetPayments from the source capital asset
     * @param offsetAssetPayments AssetPayments to be created as offset entries to the capital asset
     * @return payment for an asset in separate
     */
    protected AssetPayment setupSeparateAssetPayment(Long capitalAssetNumber, String acquisitionTypeCode, KualiDecimal separateSourceAmount, KualiDecimal totalCostToAllocate, AssetPaymentDetail assetPaymentDetail, List<AssetPayment> separateSourceCapitalAssetPayments, HashMap<String, AssetPayment> offsetAssetPayments) {
        AssetPayment assetPayment = new AssetPayment(assetPaymentDetail, acquisitionTypeCode);
        assetPayment.setCapitalAssetNumber(capitalAssetNumber);
        assetPayment.setPaymentSequenceNumber(assetPaymentDetail.getSequenceNumber());

        assetPayment.setFinancialDocumentTypeCode(CamsConstants.PaymentDocumentTypeCodes.ASSET_GLOBAL_SEPARATE);
        assetPayment.setDocumentNumber(assetPaymentDetail.getDocumentNumber());
        AssetPayment sourceAssetPayment = null;
        // Need the sourceAssetPayment for split of misc. fields that we don't track on the document. Finding the correct source
        // asset payment isn't very efficient particularly if we are doing this (repeatedly) for several target assets. HashMap?
        // Note: This is NOT the offsetAssetPayments HashMap because the offsetAssetPayments are modified each iteration.
        for (AssetPayment currentAssetPayment : separateSourceCapitalAssetPayments) {
            if (assetPaymentDetail.getSequenceNumber().equals(currentAssetPayment.getPaymentSequenceNumber())) {
                sourceAssetPayment = currentAssetPayment;
                break;
            }
        }

        // TODO following is better done in a junit test case
        if (sourceAssetPayment == null) {
            throw new IllegalStateException("sourceAssetPayment could not be found.");
        }

        // separate ratio for this asset
        double separateRatio = separateSourceAmount.doubleValue() / totalCostToAllocate.doubleValue();

        // account amount from current payment source asset * target total cost / source total cost
        assetPayment.setAccountChargeAmount(safeMultiply(assetPaymentDetail.getAmount(), separateRatio));

        assetPayment.setPrimaryDepreciationBaseAmount(safeMultiply(sourceAssetPayment.getPrimaryDepreciationBaseAmount(), separateRatio));
        assetPayment.setAccumulatedPrimaryDepreciationAmount(safeMultiply(sourceAssetPayment.getAccumulatedPrimaryDepreciationAmount(), separateRatio));
        assetPayment.setPreviousYearPrimaryDepreciationAmount(safeMultiply(sourceAssetPayment.getPreviousYearPrimaryDepreciationAmount(), separateRatio));
        assetPayment.setPeriod1Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod1Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod2Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod2Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod3Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod3Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod4Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod4Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod5Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod5Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod6Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod6Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod7Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod7Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod8Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod8Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod9Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod9Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod10Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod10Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod11Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod11Depreciation1Amount(), separateRatio));
        assetPayment.setPeriod12Depreciation1Amount(safeMultiply(sourceAssetPayment.getPeriod12Depreciation1Amount(), separateRatio));

        // Reduce the amounts of the original assets payments by what was added to the newly created asset. Note
        // separateSourceAmount may vary by asset, and each payment may have a different value. So we need to do
        // this for each
        AssetPayment offsetAssetPayment = offsetAssetPayments.get(sourceAssetPayment.getObjectId());

        offsetAssetPayment.setAccountChargeAmount(safeSubtract(offsetAssetPayment.getAccountChargeAmount(), assetPayment.getAccountChargeAmount()));
        offsetAssetPayment.setPrimaryDepreciationBaseAmount(safeSubtract(offsetAssetPayment.getPrimaryDepreciationBaseAmount(), assetPayment.getPrimaryDepreciationBaseAmount()));
        offsetAssetPayment.setAccumulatedPrimaryDepreciationAmount(safeSubtract(offsetAssetPayment.getAccumulatedPrimaryDepreciationAmount(), assetPayment.getAccumulatedPrimaryDepreciationAmount()));
        offsetAssetPayment.setPreviousYearPrimaryDepreciationAmount(safeSubtract(offsetAssetPayment.getPreviousYearPrimaryDepreciationAmount(), assetPayment.getPreviousYearPrimaryDepreciationAmount()));
        offsetAssetPayment.setPeriod1Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod1Depreciation1Amount(), assetPayment.getPeriod1Depreciation1Amount()));
        offsetAssetPayment.setPeriod2Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod2Depreciation1Amount(), assetPayment.getPeriod2Depreciation1Amount()));
        offsetAssetPayment.setPeriod3Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod3Depreciation1Amount(), assetPayment.getPeriod3Depreciation1Amount()));
        offsetAssetPayment.setPeriod4Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod4Depreciation1Amount(), assetPayment.getPeriod4Depreciation1Amount()));
        offsetAssetPayment.setPeriod5Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod5Depreciation1Amount(), assetPayment.getPeriod5Depreciation1Amount()));
        offsetAssetPayment.setPeriod6Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod6Depreciation1Amount(), assetPayment.getPeriod6Depreciation1Amount()));
        offsetAssetPayment.setPeriod7Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod7Depreciation1Amount(), assetPayment.getPeriod7Depreciation1Amount()));
        offsetAssetPayment.setPeriod8Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod8Depreciation1Amount(), assetPayment.getPeriod8Depreciation1Amount()));
        offsetAssetPayment.setPeriod9Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod9Depreciation1Amount(), assetPayment.getPeriod9Depreciation1Amount()));
        offsetAssetPayment.setPeriod10Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod10Depreciation1Amount(), assetPayment.getPeriod10Depreciation1Amount()));
        offsetAssetPayment.setPeriod11Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod11Depreciation1Amount(), assetPayment.getPeriod11Depreciation1Amount()));
        offsetAssetPayment.setPeriod12Depreciation1Amount(safeSubtract(offsetAssetPayment.getPeriod12Depreciation1Amount(), assetPayment.getPeriod12Depreciation1Amount()));

        return assetPayment;
    }

    /**
     * TODO this should move to some more central location if it doesn't exist already somewhere...<br>
     * Makes sure no null pointer exception occurs on fields that can accurately be null when multiplying. If either field are null
     * the value is returned.
     * 
     * @param value
     * @param multiplier
     * @return
     */
    protected KualiDecimal safeMultiply(KualiDecimal value, double multiplier) {
        if (value == null) {
            return value;
        }
        else {
            return new KualiDecimal(value.doubleValue() * multiplier);
        }
    }

    /**
     * TODO this should move to some more central location if it doesn't exist already somewhere...<br>
     * Makes sure no null pointer exception occurs on fields that can accurately be null when subtracting. If either field are null
     * the value is returned.
     * 
     * @param value
     * @param subtrahend
     * @return
     */
    protected KualiDecimal safeSubtract(KualiDecimal value, KualiDecimal subtrahend) {
        if (subtrahend == null) {
            return value;
        }

        if (value == null) {
            value = KualiDecimal.ZERO;
        }

        return value.subtract(subtrahend);
    }
}
