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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlpeSourceDetail;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.AssetOrganization;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.service.AssetObjectCodeService;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.AssetTransferService;
import org.kuali.module.cams.util.ObjectValueUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.service.UniversityDateService;

public class AssetTransferServiceImpl implements AssetTransferService {
    private static enum AmountCategory {
        CAPITALIZATION {
            public void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode, boolean isSource, OffsetDefinition offsetDefinition) {
                postable.setFinancialDocumentLineDescription("" + (isSource ? "Reverse" : "Transfer") + " asset cost");
                postable.setAmount(assetPayment.getAccountChargeAmount());
                postable.setFinancialObjectCode(assetObjectCode.getCapitalizationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
                postable.setCapitalization(true);
            };

        },
        ACCUM_DEPRECIATION {
            public void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode, boolean isSource, OffsetDefinition offsetDefinition) {
                postable.setFinancialDocumentLineDescription("" + (isSource ? "Reverse" : "Transfer") + " accumulated depreciation");
                postable.setAmount(assetPayment.getAccumulatedPrimaryDepreciationAmount());
                postable.setFinancialObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObject());
                postable.setAccumulatedDepreciation(true);
            };

        },
        OFFSET_AMOUNT {
            public void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode, boolean isSource, OffsetDefinition offsetDefinition) {
                postable.setFinancialDocumentLineDescription("" + (isSource ? "Reverse" : "Transfer") + " offset amount");
                postable.setAmount(assetPayment.getAccountChargeAmount().subtract(assetPayment.getAccumulatedPrimaryDepreciationAmount()));
                postable.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
                postable.setObjectCode(offsetDefinition.getFinancialObject());
                postable.setOffset(true);
            };

        };
        abstract void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode, boolean isSource, OffsetDefinition offsetDefinition);
    }

    private static final Logger LOG = Logger.getLogger(AssetTransferServiceImpl.class);
    private AssetService assetService;
    private UniversityDateService universityDateService;
    private BusinessObjectService businessObjectService;
    private AssetPaymentService assetPaymentService;
    private AssetObjectCodeService assetObjectCodeService;


    /**
     * Creates an instance of AssetGlpeSourceDetail depending on the source flag
     * 
     * @param universityDateService University Date Service
     * @param plantAccount Plant account for the organization
     * @param assetPayment Payment record for which postable is created
     * @param isSource Indicates if postable is for source organization
     * @return GL Postable source detail
     */
    private AssetGlpeSourceDetail createAssetGlpePostable(AssetTransferDocument document, Account plantAccount, AssetPayment assetPayment, boolean isSource, AmountCategory amountCategory) {
        LOG.debug("Start - createAssetGlpePostable (" + document.getDocumentNumber() + "-" + plantAccount.getAccountNumber() + ")");
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();
        postable.setSource(isSource);
        postable.setAccount(plantAccount);
        postable.setAccountNumber(plantAccount.getAccountNumber());
        postable.setBalanceTypeCode(CamsConstants.GL_BALANCE_TYPE_CDE_AC);
        String organizationOwnerChartOfAccountsCode = null;
        if (isSource) {
            organizationOwnerChartOfAccountsCode = document.getAsset().getOrganizationOwnerChartOfAccountsCode();
        }
        else {
            organizationOwnerChartOfAccountsCode = document.getOrganizationOwnerChartOfAccountsCode();
        }
        postable.setChartOfAccountsCode(organizationOwnerChartOfAccountsCode);
        postable.setDocumentNumber(document.getDocumentNumber());
        postable.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        postable.setPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        postable.setProjectCode(assetPayment.getProjectCode());
        postable.setSubAccountNumber(assetPayment.getSubAccountNumber());
        postable.setOrganizationReferenceId(assetPayment.getOrganizationReferenceId());
        AssetObjectCode assetObjectCode = getAssetObjectCodeService().findAssetObjectCode(organizationOwnerChartOfAccountsCode, assetPayment);
        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), organizationOwnerChartOfAccountsCode, CamsConstants.ASSET_TRANSFER_DOCTYPE_CD, CamsConstants.GL_BALANCE_TYPE_CDE_AC);
        amountCategory.setParams(postable, assetPayment, assetObjectCode, isSource, offsetDefinition);
        LOG.debug("End - createAssetGlpePostable(" + document.getDocumentNumber() + "-" + plantAccount.getAccountNumber() + "-" + ")");
        return postable;
    }

    /**
     * @see org.kuali.module.cams.service.AssetTransferService#createGLPostables(org.kuali.module.cams.document.AssetTransferDocument)
     */
    public void createGLPostables(AssetTransferDocument document) {
        // Create GL entries only for capital assets
        Asset asset = document.getAsset();
        if (getAssetService().isCapitalAsset(asset) && !asset.getAssetPayments().isEmpty()) {
            asset.refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
            document.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
            String finObjectSubTypeCode = asset.getFinancialObjectSubTypeCode();
            if (finObjectSubTypeCode == null) {
                AssetPayment firstAssetPayment = asset.getAssetPayments().get(0);
                firstAssetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
                finObjectSubTypeCode = firstAssetPayment.getFinancialObject().getFinancialObjectSubTypeCode();
            }
            boolean movableAsset = getAssetService().isMovableFinancialObjectSubtypeCode(finObjectSubTypeCode);
            if (isGLPostable(document, asset, movableAsset)) {
                asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
                List<AssetPayment> assetPayments = asset.getAssetPayments();
                createSourceGLPostables(document, assetPayments, movableAsset);
                createTargetGLPostables(document, assetPayments, movableAsset);
            }
        }
    }


    /**
     * Creates new payment records for new organization account
     * 
     * @param document Current document
     * @param persistableObjects Saved objects list
     * @param originalPayments Original payments for the asset
     * @param maxSequence Payment sequence number
     * @return Incremented sequence number
     */
    private Integer createNewPayments(AssetTransferDocument document, List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments, Integer maxSequence) {
        Integer maxSequenceNo = maxSequence;
        for (AssetPayment assetPayment : originalPayments) {
            if (!CamsConstants.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode())) {
                // copy and create new payment
                AssetPayment newPayment;
                try {
                    if (maxSequenceNo == null) {
                        maxSequenceNo = SpringContext.getBean(AssetPaymentService.class).getMaxSequenceNumber(assetPayment.getCapitalAssetNumber());
                    }
                    // create new payment info
                    // newPayment = (AssetPayment) ObjectUtils.deepCopy(assetPayment);
                    newPayment = new AssetPayment();
                    ObjectValueUtils.copySimpleProperties(assetPayment, newPayment);
                    newPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    newPayment.setAccountNumber(document.getOrganizationOwnerAccountNumber());
                    newPayment.setChartOfAccountsCode(document.getOrganizationOwnerChartOfAccountsCode());
                    newPayment.setSubAccountNumber(null);
                    newPayment.setDocumentNumber(document.getDocumentNumber());
                    newPayment.setFinancialDocumentTypeCode(CamsConstants.ASSET_TRANSFER_DOCTYPE_CD);
                    newPayment.setFinancialDocumentPostingDate(DateUtils.convertToSqlDate(new Date()));
                    newPayment.setFinancialDocumentPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
                    newPayment.setFinancialDocumentPostingPeriodCode(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
                    getAssetPaymentService().adjustPaymentAmounts(newPayment, false, true);
                    // add new payment
                    persistableObjects.add(newPayment);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return maxSequenceNo;
    }


    /**
     * Creates offset payment copying the details from original payments and reversing the amounts
     * 
     * @param document Current Document
     * @param persistableObjects List of update objects
     * @param originalPayments Original list of payments
     * @return Incremented sequence number
     */
    private Integer createOffsetPayments(AssetTransferDocument document, List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments) {
        Integer maxSequenceNo = null;
        for (AssetPayment assetPayment : originalPayments) {
            if (!CamsConstants.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode())) {
                // copy and create an offset payment
                try {
                    if (maxSequenceNo == null) {
                        maxSequenceNo = SpringContext.getBean(AssetPaymentService.class).getMaxSequenceNumber(assetPayment.getCapitalAssetNumber());
                    }
                    // AssetPayment offsetPayment = (AssetPayment) ObjectUtils.deepCopy(assetPayment);
                    AssetPayment offsetPayment = new AssetPayment();
                    ObjectValueUtils.copySimpleProperties(assetPayment, offsetPayment);
                    offsetPayment.setDocumentNumber(document.getDocumentNumber());
                    offsetPayment.setFinancialDocumentTypeCode(CamsConstants.ASSET_TRANSFER_DOCTYPE_CD);
                    offsetPayment.setFinancialDocumentPostingDate(DateUtils.convertToSqlDate(new Date()));
                    offsetPayment.setFinancialDocumentPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
                    offsetPayment.setFinancialDocumentPostingPeriodCode(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
                    getAssetPaymentService().adjustPaymentAmounts(offsetPayment, true, true);
                    offsetPayment.setTransferPaymentCode(CamsConstants.TRANSFER_PAYMENT_CODE_Y);
                    offsetPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    persistableObjects.add(offsetPayment);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return maxSequenceNo;
    }


    /**
     * Creates GL Postables for the source organization
     * 
     * @param universityDateService University Date Service to get the current fiscal year and period
     * @param assetPayments Payments for which GL entries needs to be created
     */
    private void createSourceGLPostables(AssetTransferDocument document, List<AssetPayment> assetPayments, boolean movableAsset) {
        Account srcPlantAcct = null;
        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), document.getAsset().getOrganizationOwnerChartOfAccountsCode(), CamsConstants.ASSET_TRANSFER_DOCTYPE_CD, CamsConstants.GL_BALANCE_TYPE_CDE_AC);

        if (movableAsset) {
            srcPlantAcct = document.getAsset().getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
        }
        else {
            srcPlantAcct = document.getAsset().getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
        }
        for (AssetPayment assetPayment : assetPayments) {
            if (isPaymentEligibleForGLPosting(assetPayment)) {
                assetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
                if (ObjectUtils.isNotNull(assetPayment.getFinancialObject())) {
                    KualiDecimal accountChargeAmount = assetPayment.getAccountChargeAmount();
                    if (accountChargeAmount != null && !accountChargeAmount.isZero()) {
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, srcPlantAcct, assetPayment, true, AmountCategory.CAPITALIZATION));
                    }
                    KualiDecimal accPrimaryDepreciationAmount = assetPayment.getAccumulatedPrimaryDepreciationAmount();
                    if (accPrimaryDepreciationAmount != null && !accPrimaryDepreciationAmount.isZero()) {
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, srcPlantAcct, assetPayment, true, AmountCategory.ACCUM_DEPRECIATION));
                    }
                    if (accountChargeAmount != null && accPrimaryDepreciationAmount != null && !accountChargeAmount.subtract(accPrimaryDepreciationAmount).isZero()) {
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, srcPlantAcct, assetPayment, true, AmountCategory.OFFSET_AMOUNT));

                    }
                }
            }
        }
    }


    /**
     * Creates target GL Postable for the receiving organization
     * 
     * @param universityDateService University Date Service to get the current fiscal year and period
     * @param assetPayments Payments for which GL entries needs to be created
     */
    private void createTargetGLPostables(AssetTransferDocument document, List<AssetPayment> assetPayments, boolean movableAsset) {
        Account targetPlantAcct = null;

        if (movableAsset) {
            targetPlantAcct = document.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
        }
        else {
            targetPlantAcct = document.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
        }
        for (AssetPayment assetPayment : assetPayments) {
            if (isPaymentEligibleForGLPosting(assetPayment)) {
                KualiDecimal accountChargeAmount = assetPayment.getAccountChargeAmount();
                if (accountChargeAmount != null && !accountChargeAmount.isZero()) {
                    document.getTargetAssetGlpeSourceDetails().add(createAssetGlpePostable(document, targetPlantAcct, assetPayment, false, AmountCategory.CAPITALIZATION));
                }
                KualiDecimal accPrimaryDepreciationAmount = assetPayment.getAccumulatedPrimaryDepreciationAmount();
                if (accPrimaryDepreciationAmount != null && !accPrimaryDepreciationAmount.isZero()) {
                    document.getTargetAssetGlpeSourceDetails().add(createAssetGlpePostable(document, targetPlantAcct, assetPayment, false, AmountCategory.ACCUM_DEPRECIATION));
                }
                if (accountChargeAmount != null && accPrimaryDepreciationAmount != null && !accountChargeAmount.subtract(accPrimaryDepreciationAmount).isZero()) {
                    document.getTargetAssetGlpeSourceDetails().add(createAssetGlpePostable(document, targetPlantAcct, assetPayment, false, AmountCategory.OFFSET_AMOUNT));
                }
            }
        }
    }


    public AssetPaymentService getAssetPaymentService() {
        return assetPaymentService;
    }


    public AssetService getAssetService() {
        return assetService;
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    /**
     * Checks if it is ready for GL Posting by validating the accounts and plant account numbers
     * 
     * @return true if all accounts are valid
     */
    private boolean isGLPostable(AssetTransferDocument document, Asset asset, boolean movableAsset) {
        boolean isGLPostable = true;

        Account srcPlantAcct = null;

        if (ObjectUtils.isNotNull(asset.getOrganizationOwnerAccount())) {
            if (movableAsset) {
                srcPlantAcct = asset.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
            }
            else {
                srcPlantAcct = asset.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
            }
        }

        if (ObjectUtils.isNull(srcPlantAcct)) {
            isGLPostable &= false;
        }
        Account targetPlantAcct = null;
        if (ObjectUtils.isNotNull(document.getOrganizationOwnerAccount())) {
            if (movableAsset) {
                targetPlantAcct = document.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
            }
            else {
                targetPlantAcct = document.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
            }
        }
        if (ObjectUtils.isNull(targetPlantAcct)) {
            isGLPostable &= false;

        }
        return isGLPostable;
    }

    /**
     * Helper method to check conditions if a payment is eligible for GL posting
     * 
     * @param assetPayment Asset Payment record
     * @return True is record can be used for GL entry creation
     */
    private boolean isPaymentEligibleForGLPosting(AssetPayment assetPayment) {
        // Payment transfer code is not "Y", Financial Object Code is active for the Payment and is not a Federal Contribution
        return !CamsConstants.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode()) && getAssetPaymentService().isPaymentFinancialObjectActive(assetPayment) && !getAssetPaymentService().isPaymentFederalContribution(assetPayment);
    }


    /**
     * @see org.kuali.module.cams.service.AssetTransferService#saveApprovedChanges(org.kuali.module.cams.document.AssetTransferDocument)
     */
    public void saveApprovedChanges(AssetTransferDocument document) {
        // save new asset location details to asset table, inventory date
        List<PersistableBusinessObject> persistableObjects = new ArrayList<PersistableBusinessObject>();
        Asset saveAsset = new Asset();
        saveAsset.setCapitalAssetNumber(document.getCapitalAssetNumber());
        saveAsset = (Asset) getBusinessObjectService().retrieve(saveAsset);
        saveAssetOwnerData(document, saveAsset);
        saveLocationChanges(document, saveAsset);
        saveOrganizationChanges(document, saveAsset);

        if (getAssetService().isCapitalAsset(saveAsset)) {
            // for capital assets, create new asset payment records and offset payment records
            if (saveAsset.getAssetPayments() == null) {
                saveAsset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
            }
            List<AssetPayment> originalPayments = saveAsset.getAssetPayments();
            Integer maxSequence = createOffsetPayments(document, persistableObjects, originalPayments);
            maxSequence = createNewPayments(document, persistableObjects, originalPayments, maxSequence);
            updateOriginalPayments(persistableObjects, originalPayments);
        }
        // save asset
        persistableObjects.add(saveAsset);
        getBusinessObjectService().save(persistableObjects);
    }


    /**
     * Updates organization data for the asset
     * 
     * @param document Current document
     * @param saveAsset Asset
     */
    private void saveAssetOwnerData(AssetTransferDocument document, Asset saveAsset) {
        saveAsset.setOrganizationOwnerAccountNumber(document.getOrganizationOwnerAccountNumber());
        saveAsset.setOrganizationOwnerChartOfAccountsCode(document.getOrganizationOwnerChartOfAccountsCode());
    }

    /**
     * Updates location details to the asset
     * 
     * @param document Current document
     * @param saveAsset Asset
     */
    private void saveLocationChanges(AssetTransferDocument document, Asset saveAsset) {
        // change inventory date
        saveAsset.setLastInventoryDate(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        // save asset location details
        saveAsset.setCampusCode(document.getCampusCode());
        saveAsset.setBuildingCode(document.getBuildingCode());
        saveAsset.setBuildingRoomNumber(document.getBuildingRoomNumber());
        saveAsset.setBuildingSubRoomNumber(document.getBuildingSubRoomNumber());

        // save off campus location details
        AssetLocation offCampusLocation = null;
        List<AssetLocation> orginalLocations = saveAsset.getAssetLocations();
        for (AssetLocation assetLocation : orginalLocations) {
            if (CamsConstants.AssetLocationTypeCode.OFF_CAMPUS.equals(assetLocation.getAssetLocationTypeCode())) {
                offCampusLocation = assetLocation;
                break;
            }
        }
        if (offCampusLocation == null) {
            offCampusLocation = new AssetLocation();
            offCampusLocation.setCapitalAssetNumber(saveAsset.getCapitalAssetNumber());
            offCampusLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.OFF_CAMPUS);
            saveAsset.getAssetLocations().add(offCampusLocation);
        }
        // save details
        offCampusLocation.setAssetLocationStreetAddress(document.getOffCampusAddress());
        offCampusLocation.setAssetLocationCityName(document.getOffCampusCityName());
        offCampusLocation.setAssetLocationStateCode(document.getOffCampusStateCode());
        offCampusLocation.setAssetLocationZipCode(document.getOffCampusZipCode());
    }


    /**
     * Updates organization changes
     * 
     * @param document Current document
     * @param saveAsset Asset
     */
    private void saveOrganizationChanges(AssetTransferDocument document, Asset saveAsset) {
        AssetOrganization assetOrganization = null;
        if ((assetOrganization = saveAsset.getAssetOrganization()) == null) {
            assetOrganization = new AssetOrganization();
            assetOrganization.setCapitalAssetNumber(saveAsset.getCapitalAssetNumber());
            saveAsset.setAssetOrganization(assetOrganization);
        }
        saveAsset.setOrganizationInventoryName(document.getOrganizationInventoryName());
        saveAsset.setRepresentativeUniversalIdentifier(document.getRepresentativeUniversalIdentifier());
        assetOrganization.setOrganizationTagNumber(document.getOrganizationTagNumber());
        assetOrganization.setOrganizationText(document.getOrganizationText());
    }

    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }


    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    /**
     * Updates original payment records
     * 
     * @param persistableObjects List of saveable objects
     * @param originalPayments Original payments list
     */
    private void updateOriginalPayments(List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments) {
        for (AssetPayment assetPayment : originalPayments) {
            if (!CamsConstants.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode())) {
                // change payment code
                assetPayment.setTransferPaymentCode(CamsConstants.TRANSFER_PAYMENT_CODE_Y);
                persistableObjects.add(assetPayment);
            }
        }
    }

    public AssetObjectCodeService getAssetObjectCodeService() {
        return assetObjectCodeService;
    }

    public void setAssetObjectCodeService(AssetObjectCodeService assetObjectCodeService) {
        this.assetObjectCodeService = assetObjectCodeService;
    }
}
