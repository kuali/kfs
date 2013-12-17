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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlpeSourceDetail;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetOrganization;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.AssetTransferDocument;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetObjectCodeService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.AssetTransferService;
import org.kuali.kfs.module.cam.util.ObjectValueUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class AssetTransferServiceImpl implements AssetTransferService {
    protected static enum AmountCategory {
        CAPITALIZATION {
            @Override
            public void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode, boolean isSource, OffsetDefinition offsetDefinition) {
                postable.setFinancialDocumentLineDescription("" + (isSource ? "Reverse" : "Transfer") + " asset cost");
                postable.setAmount(assetPayment.getAccountChargeAmount());
                postable.setFinancialObjectCode(assetObjectCode.getCapitalizationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
                postable.setCapitalization(true);
            };

        },
        ACCUM_DEPRECIATION {
            @Override
            public void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode, boolean isSource, OffsetDefinition offsetDefinition) {
                postable.setFinancialDocumentLineDescription("" + (isSource ? "Reverse" : "Transfer") + " accumulated depreciation");
                postable.setAmount(assetPayment.getAccumulatedPrimaryDepreciationAmount());
                postable.setFinancialObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObject());
                postable.setAccumulatedDepreciation(true);
            };

        },
        OFFSET_AMOUNT {
            @Override
            public void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode, boolean isSource, OffsetDefinition offsetDefinition) {
                postable.setFinancialDocumentLineDescription("" + (isSource ? "Reverse" : "Transfer") + " offset amount");
                postable.setAmount(assetPayment.getAccountChargeAmount().subtract(assetPayment.getAccumulatedPrimaryDepreciationAmount()));
                postable.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
                postable.setObjectCode(offsetDefinition.getFinancialObject());
                postable.setCapitalizationOffset(true);
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
    private DateTimeService dateTimeService;
    private AssetLocationService assetLocationService;


    /**
     * Creates an instance of AssetGlpeSourceDetail depending on the source flag
     *
     * @param universityDateService University Date Service
     * @param plantAccount Plant account for the organization
     * @param assetPayment Payment record for which postable is created
     * @param isSource Indicates if postable is for source organization
     * @return GL Postable source detail
     */
    protected AssetGlpeSourceDetail createAssetGlpePostable(AssetTransferDocument document, Account plantAccount, AssetPayment assetPayment, boolean isSource, AmountCategory amountCategory) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Start - createAssetGlpePostable (" + document.getDocumentNumber() + "-" + plantAccount.getAccountNumber() + ")");
        }
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();
        postable.setSource(isSource);
        postable.setAccount(plantAccount);
        postable.setAccountNumber(plantAccount.getAccountNumber());
        postable.setBalanceTypeCode(CamsConstants.Postable.GL_BALANCE_TYPE_CODE_AC);
        String organizationOwnerChartOfAccountsCode = null;
        if (isSource) {
            organizationOwnerChartOfAccountsCode = document.getAsset().getOrganizationOwnerChartOfAccountsCode();
            postable.setSubAccountNumber(assetPayment.getSubAccountNumber());
        }
        else {
            organizationOwnerChartOfAccountsCode = document.getOrganizationOwnerChartOfAccountsCode();
            postable.setSubAccountNumber(null);
        }
        ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);
        ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(assetPayment.getChartOfAccountsCode(), assetPayment.getFinancialObjectCode());
        String plantChartOfAccountsCode = plantAccount.getChartOfAccountsCode();
        postable.setChartOfAccountsCode(plantChartOfAccountsCode);
        postable.setDocumentNumber(document.getDocumentNumber());
        postable.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        postable.setPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        postable.setProjectCode(assetPayment.getProjectCode());
        postable.setOrganizationReferenceId(assetPayment.getOrganizationReferenceId());

        AssetObjectCode assetObjectCode = getAssetObjectCodeService().findAssetObjectCode(organizationOwnerChartOfAccountsCode, objectCode.getFinancialObjectSubTypeCode());
        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), organizationOwnerChartOfAccountsCode, CamsConstants.AssetTransfer.DOCUMENT_TYPE_CODE, CamsConstants.Postable.GL_BALANCE_TYPE_CODE_AC);
        amountCategory.setParams(postable, assetPayment, assetObjectCode, isSource, offsetDefinition);
        if (LOG.isDebugEnabled()) {
            LOG.debug("End - createAssetGlpePostable(" + document.getDocumentNumber() + "-" + plantAccount.getAccountNumber() + "-" + ")");
        }
        return postable;
    }

    @Override
    public void createGLPostables(AssetTransferDocument document) {
        document.clearGlPostables();
        // Create GL entries only for capital assets
        ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);

        Asset asset = document.getAsset();
        if (getAssetService().isCapitalAsset(asset) && !asset.getAssetPayments().isEmpty()) {
            asset.refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
            document.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);

            boolean movableAsset = getAssetService().isAssetMovableCheckByPayment(asset);
            if (isGLPostable(document, asset, movableAsset)) {
                asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
                List<AssetPayment> assetPayments = asset.getAssetPayments();
                createSourceAndTargetGLPostables(document, assetPayments, movableAsset);
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
    protected Integer createNewPayments(AssetTransferDocument document, List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments, Integer maxSequence) {
        Integer maxSequenceNo = maxSequence;
        for (AssetPayment assetPayment : originalPayments) {
            if (!CamsConstants.AssetPayment.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode())) {
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
                    newPayment.setFinancialDocumentTypeCode(CamsConstants.AssetTransfer.DOCUMENT_TYPE_CODE);
                    newPayment.setFinancialDocumentPostingDate(KfsDateUtils.convertToSqlDate(dateTimeService.getCurrentDate()));
                    newPayment.setFinancialDocumentPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
                    newPayment.setFinancialDocumentPostingPeriodCode(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
                    getAssetPaymentService().adjustPaymentAmounts(newPayment, false, true);
                    newPayment.setTransferPaymentCode(CamsConstants.AssetPayment.TRANSFER_PAYMENT_CODE_N);
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
    protected Integer createOffsetPayments(AssetTransferDocument document, List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments) {
        Integer maxSequenceNo = null;
        for (AssetPayment assetPayment : originalPayments) {
            if (!CamsConstants.AssetPayment.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode())) {
                // copy and create an offset payment
                try {
                    if (maxSequenceNo == null) {
                        maxSequenceNo = SpringContext.getBean(AssetPaymentService.class).getMaxSequenceNumber(assetPayment.getCapitalAssetNumber());
                    }
                    // AssetPayment offsetPayment = (AssetPayment) ObjectUtils.deepCopy(assetPayment);
                    AssetPayment offsetPayment = new AssetPayment();
                    ObjectValueUtils.copySimpleProperties(assetPayment, offsetPayment);
                    offsetPayment.setDocumentNumber(document.getDocumentNumber());
                    offsetPayment.setFinancialDocumentTypeCode(CamsConstants.AssetTransfer.DOCUMENT_TYPE_CODE);
                    offsetPayment.setFinancialDocumentPostingDate(KfsDateUtils.convertToSqlDate(dateTimeService.getCurrentDate()));
                    offsetPayment.setFinancialDocumentPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
                    offsetPayment.setFinancialDocumentPostingPeriodCode(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
                    getAssetPaymentService().adjustPaymentAmounts(offsetPayment, true, true);
                    offsetPayment.setTransferPaymentCode(CamsConstants.AssetPayment.TRANSFER_PAYMENT_CODE_Y);
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
     * Creates source and target GL Postable for the source / receiving organizations
     *
     * @param universityDateService University Date Service to get the current fiscal year and period
     * @param assetPayments Payments for which GL entries needs to be created
     */
    protected void createSourceAndTargetGLPostables(AssetTransferDocument document, List<AssetPayment> assetPayments, boolean movableAsset) {
        Account paymentPlantAcct = null;
        ObjectCodeService objectCodeService = SpringContext.getBean(ObjectCodeService.class);

        for (AssetPayment assetPayment : assetPayments) {
            if (getAssetPaymentService().isPaymentEligibleForGLPosting(assetPayment)) {
                 ObjectCode objectCode = objectCodeService.getByPrimaryIdForCurrentYear(assetPayment.getChartOfAccountsCode(), assetPayment.getFinancialObjectCode());
                if (ObjectUtils.isNotNull(objectCode)) {
                    if (movableAsset) {
                        paymentPlantAcct = assetPayment.getAccount().getOrganization().getOrganizationPlantAccount();
                    } else {
                        paymentPlantAcct = assetPayment.getAccount().getOrganization().getCampusPlantAccount();
                    }
                    if (getAssetPaymentService().isPaymentEligibleForCapitalizationGLPosting(assetPayment)) {
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, paymentPlantAcct, assetPayment, true , AmountCategory.CAPITALIZATION));
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, paymentPlantAcct, assetPayment, false, AmountCategory.CAPITALIZATION));
                    }
                    if (getAssetPaymentService().isPaymentEligibleForAccumDeprGLPosting(assetPayment)) {
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, paymentPlantAcct, assetPayment, true , AmountCategory.ACCUM_DEPRECIATION));
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, paymentPlantAcct, assetPayment, false, AmountCategory.ACCUM_DEPRECIATION));
                    }
                    if (getAssetPaymentService().isPaymentEligibleForOffsetGLPosting(assetPayment)) {
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, paymentPlantAcct, assetPayment, true , AmountCategory.OFFSET_AMOUNT));
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, paymentPlantAcct, assetPayment, false, AmountCategory.OFFSET_AMOUNT));
                    }
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
    protected boolean isGLPostable(AssetTransferDocument document, Asset asset, boolean movableAsset) {
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
     * @see org.kuali.kfs.module.cam.document.service.AssetTransferService#saveApprovedChanges(org.kuali.kfs.module.cam.document.AssetTransferDocument)
     */
    @Override
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
            if (ObjectUtils.isNull(saveAsset.getAssetPayments())) {
                saveAsset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
            }
            List<AssetPayment> originalPayments = saveAsset.getAssetPayments();
            Integer maxSequence = createOffsetPayments(document, persistableObjects, originalPayments);
            maxSequence = createNewPayments(document, persistableObjects, originalPayments, maxSequence);
            updateOriginalPayments(persistableObjects, originalPayments);
        }
        saveAsset.setTransferOfFundsFinancialDocumentNumber(document.getTransferOfFundsFinancialDocumentNumber());
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
    protected void saveAssetOwnerData(AssetTransferDocument document, Asset saveAsset) {
        saveAsset.setOrganizationOwnerAccountNumber(document.getOrganizationOwnerAccountNumber());
        saveAsset.setOrganizationOwnerChartOfAccountsCode(document.getOrganizationOwnerChartOfAccountsCode());
    }

    /**
     * Updates location details to the asset
     *
     * @param document Current document
     * @param saveAsset Asset
     */
    protected void saveLocationChanges(AssetTransferDocument document, Asset saveAsset) {
        // change inventory date
        saveAsset.setLastInventoryDate(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        // save asset location details
        saveAsset.setCampusCode(document.getCampusCode());
        saveAsset.setBuildingCode(document.getBuildingCode());
        saveAsset.setBuildingRoomNumber(document.getBuildingRoomNumber());
        saveAsset.setBuildingSubRoomNumber(document.getBuildingSubRoomNumber());
        AssetLocation offCampusLocation = null;

        if (StringUtils.isBlank(saveAsset.getBuildingCode()) && StringUtils.isBlank(saveAsset.getBuildingRoomNumber()) && StringUtils.isBlank(saveAsset.getBuildingSubRoomNumber())) {

            // save off campus location details
            List<AssetLocation> orginalLocations = saveAsset.getAssetLocations();
            for (AssetLocation assetLocation : orginalLocations) {
                if (CamsConstants.AssetLocationTypeCode.OFF_CAMPUS.equals(assetLocation.getAssetLocationTypeCode())) {
                    offCampusLocation = assetLocation;
                    break;
                }
            }

            if (ObjectUtils.isNull(offCampusLocation)) {
                offCampusLocation = new AssetLocation();
                offCampusLocation.setCapitalAssetNumber(saveAsset.getCapitalAssetNumber());
                offCampusLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.OFF_CAMPUS);
                saveAsset.getAssetLocations().add(offCampusLocation);
            }
            offCampusLocation.setAssetLocationContactName(document.getOffCampusName());
            offCampusLocation.setAssetLocationState(document.getOffCampusState());
            offCampusLocation.setPostalZipCode(document.getPostalZipCode());
            offCampusLocation.setAssetLocationCountryCode(document.getOffCampusCountryCode());
            offCampusLocation.setAssetLocationStreetAddress(document.getOffCampusAddress());
            offCampusLocation.setAssetLocationCityName(document.getOffCampusCityName());
            offCampusLocation.setAssetLocationStateCode(document.getOffCampusStateCode());
            offCampusLocation.setAssetLocationZipCode(document.getOffCampusZipCode());
            if (getAssetLocationService().isOffCampusLocationEmpty(offCampusLocation)) {
                // remove off Campus Location if it's an empty record. When asset transfer from off to on campus, the off campus location will be removed.
                saveAsset.getAssetLocations().remove(offCampusLocation);
              //  getBusinessObjectService().delete(offCampusLocation);
            }
        } else {
            for (AssetLocation assetLocation : saveAsset.getAssetLocations()) {
                getBusinessObjectService().delete(assetLocation);
            }
            saveAsset.getAssetLocations().clear();
            saveAsset.setOffCampusLocation(null);
        }

    }


    /**
     * Updates organization changes
     *
     * @param document Current document
     * @param saveAsset Asset
     */
    protected void saveOrganizationChanges(AssetTransferDocument document, Asset saveAsset) {
        if (ObjectUtils.isNull(saveAsset.getAssetOrganization())) {
            AssetOrganization assetOrganization = new AssetOrganization();
            assetOrganization.setCapitalAssetNumber(saveAsset.getCapitalAssetNumber());
            saveAsset.setAssetOrganization(assetOrganization);
        }

        saveAsset.setOrganizationInventoryName(document.getOrganizationInventoryName());
        saveAsset.setRepresentativeUniversalIdentifier(document.getRepresentativeUniversalIdentifier());
        saveAsset.getAssetOrganization().setOrganizationTagNumber(document.getOrganizationTagNumber());
        saveAsset.getAssetOrganization().setOrganizationText(document.getOrganizationText());
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
    protected void updateOriginalPayments(List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments) {
        for (AssetPayment assetPayment : originalPayments) {
            if (!CamsConstants.AssetPayment.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode())) {
                // change payment code
                assetPayment.setTransferPaymentCode(CamsConstants.AssetPayment.TRANSFER_PAYMENT_CODE_Y);
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

    /**
     * Gets the dateTimeService attribute.
     *
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     *
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the assetLocationService attribute.
     *
     * @return Returns the assetLocationService
     */
    public AssetLocationService getAssetLocationService() {
        return assetLocationService;
    }

    /**
     * Sets the assetLocationService attribute.
     *
     * @param assetLocationService  The assetLocationService to set
     */
    public void setAssetLocationService(AssetLocationService assetLocationService) {
        this.assetLocationService = assetLocationService;
    }
}
