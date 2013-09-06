/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cam.document;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetDepreciationConvention;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetOrganization;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.gl.AssetGlobalGeneralLedgerPendingEntrySource;
import org.kuali.kfs.module.cam.document.service.AssetDateService;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.validation.impl.AssetGlobalRule;
import org.kuali.kfs.module.cam.util.KualiDecimalUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.LedgerPostingMaintainable;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global assets
 */
public class AssetGlobalMaintainableImpl extends LedgerPostingMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalMaintainableImpl.class);

    protected static final String REQUIRES_REVIEW = "RequiresReview";

    /**
     * Lock on purchase order document since post processor will update PO document by adding notes.
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getWorkflowEngineDocumentIdsToLock()
     */
    @Override
    public List<String> getWorkflowEngineDocumentIdsToLock() {
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        if (ObjectUtils.isNotNull(assetGlobal) && assetGlobal.isCapitalAssetBuilderOriginIndicator()) {
            String poDocId = SpringContext.getBean(CapitalAssetBuilderModuleService.class).getCurrentPurchaseOrderDocumentNumber(getDocumentNumber());
            if (StringUtils.isNotBlank(poDocId)) {
                List<String> documentIds = new ArrayList<String>();
                documentIds.add(poDocId);
                return documentIds;
            }
        }
        return null;
    }

    /**
     * If the Add Asset Global document is submit from CAB, bypass all the approvers.
     */
    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (REQUIRES_REVIEW.equals(nodeName)) {
            return !isAccountAndOrganizationReviewRequired();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     * check whether or not isCapitalAssetBuilderOriginIndicator
     */
    protected boolean isAccountAndOrganizationReviewRequired(){
        return ((AssetGlobal) getBusinessObject()).isCapitalAssetBuilderOriginIndicator();
    }


    /**
     * Get Asset from AssetGlobal
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {

        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();

        // set "asset number" and "type code" from URL
        setSeparateSourceCapitalAssetParameters(assetGlobal, parameters);
        setFinancialDocumentTypeCode(assetGlobal, parameters);

        // populate required fields for "Asset Separate" doc
        if (getAssetGlobalService().isAssetSeparate(assetGlobal)) {
            Asset asset = getAsset(assetGlobal);
            AssetOrganization assetOrganization = getAssetOrganization(assetGlobal);
            populateAssetSeparateAssetDetails(assetGlobal, asset, assetOrganization);
            populateAssetSeparatePaymentDetails(assetGlobal, asset);
            populateAssetLocationTabInformation(asset);
            AssetGlobalRule.validateAssetTotalCostMatchesPaymentTotalCost(assetGlobal);

            if (getAssetGlobalService().isAssetSeparateByPayment(assetGlobal)) {
                AssetGlobalRule.validateAssetAlreadySeparated(assetGlobal.getSeparateSourceCapitalAssetNumber());
            }
            // populate doc header description with the doc type
            document.getDocumentHeader().setDocumentDescription(CamsConstants.AssetSeparate.SEPARATE_AN_ASSET_DESCRIPTION);
        }

        super.processAfterNew(document, parameters);
    }

    @Override
    public void setGenerateDefaultValues(String docTypeName) {
    }

    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.setupNewFromExisting(document, parameters);

        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();

        // CSU 6702 BEGIN
        String docType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
        ParameterEvaluatorService parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        ParameterEvaluator evaluator = parameterEvaluatorService.getParameterEvaluator(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE,  KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES, docType);
        if (evaluator.evaluationSucceeds() && isPeriod13(assetGlobal) ) {
            Integer closingYear = new Integer(SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));
            String closingDate = getClosingDate(closingYear);
            try {
                updateAssetGlobalForPeriod13(assetGlobal, closingYear, closingDate);
                assetGlobal.refreshNonUpdateableReferences();
            } catch (Exception e) {
                LOG.error(e);
            }
        }
        // CSU 6702 END

        assetGlobal.setLastInventoryDate(getDateTimeService().getCurrentSqlDate());
    }


    /**
     * Get Asset from AssetGlobal
     *
     * @param assetGlobal
     * @return Asset
     */
    protected Asset getAsset(AssetGlobal assetGlobal) {
        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(Asset.class, assetGlobal.getSeparateSourceCapitalAssetNumber());
    }

    /**
     * Get AssetOrganization from AssetGlobal
     *
     * @param assetGlobal
     * @return AssetOrganization
     */
    protected AssetOrganization getAssetOrganization(AssetGlobal assetGlobal) {
        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(AssetOrganization.class, assetGlobal.getSeparateSourceCapitalAssetNumber());
    }

    /**
     * Populate Asset Details for Asset Separate document
     *
     * @param assetGlobal
     * @param asset
     * @param assetOrganization
     */
    private void populateAssetSeparateAssetDetails(AssetGlobal assetGlobal, Asset asset, AssetOrganization assetOrganization) {
        assetGlobal.setOrganizationOwnerAccountNumber(asset.getOrganizationOwnerAccountNumber());
        assetGlobal.setOrganizationOwnerChartOfAccountsCode(asset.getOrganizationOwnerChartOfAccountsCode());
        assetGlobal.setAgencyNumber(asset.getAgencyNumber());
        assetGlobal.setAcquisitionTypeCode(asset.getAcquisitionTypeCode());
        assetGlobal.setInventoryStatusCode(asset.getInventoryStatusCode());
        assetGlobal.setConditionCode(asset.getConditionCode());
        assetGlobal.setCapitalAssetDescription(asset.getCapitalAssetDescription());
        assetGlobal.setCapitalAssetTypeCode(asset.getCapitalAssetTypeCode());
        assetGlobal.setVendorName(asset.getVendorName());
        assetGlobal.setManufacturerName(asset.getManufacturerName());
        assetGlobal.setManufacturerModelNumber(asset.getManufacturerModelNumber());
        if (ObjectUtils.isNotNull(assetOrganization)) {
            assetGlobal.setOrganizationText(assetOrganization.getOrganizationText());
        }
        // added in case of NULL date in DB
        if (asset.getLastInventoryDate() == null) {
            assetGlobal.setLastInventoryDate(getDateTimeService().getCurrentSqlDate());
        }
        else {
            assetGlobal.setLastInventoryDate(new java.sql.Date(asset.getLastInventoryDate().getTime()));
        }
        assetGlobal.setCreateDate(asset.getCreateDate());
        assetGlobal.setCapitalAssetInServiceDate(asset.getCapitalAssetInServiceDate());
        assetGlobal.setLandCountyName(asset.getLandCountyName());
        assetGlobal.setLandAcreageSize(asset.getLandAcreageSize());
        assetGlobal.setLandParcelNumber(asset.getLandParcelNumber());

        // CSU 6702 BEGIN
        doPeriod13Changes(assetGlobal);
        // CSU 6702 END

        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);
    }

    /**
     * Populate Asset Payment Details for Asset Separate document. It will do this whether we are separating by asset or payment. If
     * it is by asset it picks up all the payments and sets the total amount on the document of that per the asset. If it is by
     * payment it picks only the payment out we are interested in and set the document total amount to that payment only.
     *
     * @param assetGlobal
     * @param asset
     */
    private void populateAssetSeparatePaymentDetails(AssetGlobal assetGlobal, Asset asset) {
        // clear and create temp AssetPaymentDetail list
        assetGlobal.getAssetPaymentDetails().clear();
        List<AssetPaymentDetail> newAssetPaymentDetailList = assetGlobal.getAssetPaymentDetails();

        if (!getAssetGlobalService().isAssetSeparateByPayment(assetGlobal)) {
            // Separate by Asset. Pick all payments up

            for (AssetPayment assetPayment : asset.getAssetPayments()) {

                // create new AssetPaymentDetail
                AssetPaymentDetail assetPaymentDetail = new AssetPaymentDetail(assetPayment);

                // add assetPaymentDetail to AssetPaymentDetail list
                newAssetPaymentDetailList.add(assetPaymentDetail);
            }

            // Set total amount per asset
            assetGlobal.setTotalCostAmount(asset.getTotalCostAmount());
            assetGlobal.setSeparateSourceRemainingAmount(asset.getTotalCostAmount());
        }
        else {
            for (AssetPayment assetPayment : asset.getAssetPayments()) {
                // Separate by Payment. Pick only the appropriate payment up and then break

                if (assetPayment.getPaymentSequenceNumber().equals(assetGlobal.getSeparateSourcePaymentSequenceNumber())) {
                    // create new AssetPaymentDetail
                    AssetPaymentDetail assetPaymentDetail = new AssetPaymentDetail(assetPayment);

                    // add assetPaymentDetail to AssetPaymentDetail list
                    newAssetPaymentDetailList.add(assetPaymentDetail);

                    // Set total amount per payment
                    assetGlobal.setTotalCostAmount(assetPayment.getAccountChargeAmount());
                    assetGlobal.setSeparateSourceRemainingAmount(assetPayment.getAccountChargeAmount());

                    break;
                }
            }
        }
        assetGlobal.setSeparateSourceTotalAmount(KualiDecimal.ZERO);

        // set AssetGlobal payment details with new payment details
        assetGlobal.setAssetPaymentDetails(newAssetPaymentDetailList);
    }

    /**
     * Set capital asset number and payment sequence number from URL on the AssetGlobal BO. It only does so if each is available.
     *
     * @see org.kuali.module.cams.lookup.AssetLookupableHelperServiceImpl#getSeparateUrl(BusinessObject)
     * @see org.kuali.module.cams.lookup.AssetPaymentLookupableHelperServiceImpl#getSeparateUrl(BusinessObject)
     * @param assetGlobal
     * @param parameters
     */
    private void setSeparateSourceCapitalAssetParameters(AssetGlobal assetGlobal, Map<String, String[]> parameters) {
        String[] separateSourceCapitalAssetNumber = parameters.get(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER);
        if (separateSourceCapitalAssetNumber != null) {
            assetGlobal.setSeparateSourceCapitalAssetNumber(Long.parseLong(separateSourceCapitalAssetNumber[0].toString()));
        }

        String[] separateSourcePaymentSequenceNumber = parameters.get(CamsPropertyConstants.AssetGlobal.SEPERATE_SOURCE_PAYMENT_SEQUENCE_NUMBER);
        if (separateSourcePaymentSequenceNumber != null) {
            assetGlobal.setSeparateSourcePaymentSequenceNumber(Integer.parseInt(separateSourcePaymentSequenceNumber[0].toString()));
        }
    }

    /**
     * Set document type code from URL.
     *
     * @see org.kuali.module.cams.lookup.AssetLookupableHelperServiceImpl#getSeparateUrl(BusinessObject)
     * @param assetGlobal
     * @param parameters
     */
    private void setFinancialDocumentTypeCode(AssetGlobal assetGlobal, Map<String, String[]> parameters) {
        String[] financialDocumentTypeCode = parameters.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        if (financialDocumentTypeCode != null) {
            assetGlobal.setFinancialDocumentTypeCode(financialDocumentTypeCode[0].toString());
        }
    }

    /**
     * Hook for quantity and setting asset numbers.
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();

        if (CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS.equalsIgnoreCase(collectionName)) {
            handAssetPaymentsCollection(collectionName, assetGlobal);
        }
        if (CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS.equalsIgnoreCase(collectionName)) {
            handleAssetSharedDetailsCollection(collectionName, assetGlobal);
        }
        int sharedDetailsIndex = assetGlobal.getAssetSharedDetails().size() - 1;
        if (sharedDetailsIndex > -1 && (CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + sharedDetailsIndex + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS).equalsIgnoreCase(collectionName)) {
            handleAssetUniqueCollection(collectionName, assetGlobal);
        }
        super.addNewLineToCollection(collectionName);
    }

    /**
     * Sets required fields with specific values when an individual unique asset added.
     *
     * @param collectionName
     */
    private void handleAssetUniqueCollection(String collectionName, AssetGlobal assetGlobal) {
        AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) newCollectionLines.get(collectionName);

        if (ObjectUtils.isNotNull(assetGlobalDetail)) {
            assetGlobalDetail.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());

            // if not set, populate unique asset fields using original asset data. "Asset Separate" doc (location tab)
            if (ObjectUtils.isNotNull(assetGlobal)) {
                if (getAssetGlobalService().isAssetSeparate(assetGlobal)) {
                    if (assetGlobalDetail.getCapitalAssetTypeCode() == null) {
                        assetGlobalDetail.setCapitalAssetTypeCode(assetGlobal.getCapitalAssetTypeCode());
                    }
                    if (assetGlobalDetail.getCapitalAssetDescription() == null) {
                        assetGlobalDetail.setCapitalAssetDescription(assetGlobal.getCapitalAssetDescription());
                    }
                    if (assetGlobalDetail.getManufacturerName() == null) {
                        assetGlobalDetail.setManufacturerName(assetGlobal.getManufacturerName());
                    }
                    if (assetGlobalDetail.getSeparateSourceAmount() == null) {
                        assetGlobalDetail.setSeparateSourceAmount(KualiDecimal.ZERO);
                    }
                }
            }
        }
    }

    /**
     * Sets required fields with specific values when multiple unique assets added (i.e. field "Quantity Of Assets To Be Created").
     *
     * @param collectionName
     * @param assetGlobal
     */
    private void handleAssetSharedDetailsCollection(String collectionName, AssetGlobal assetGlobal) {
        AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) newCollectionLines.get(collectionName);
        Integer locationQuantity = assetGlobalDetail.getLocationQuantity();
        while (locationQuantity != null && locationQuantity > 0) {
            AssetGlobalDetail newAssetUnique = new AssetGlobalDetail();
            newAssetUnique.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());

            // populate unique asset fields using original asset data. "Asset Separate" doc (location tab)
            if (getAssetGlobalService().isAssetSeparate(assetGlobal)) {
                newAssetUnique.setCapitalAssetTypeCode(assetGlobal.getCapitalAssetTypeCode());
                newAssetUnique.setCapitalAssetDescription(assetGlobal.getCapitalAssetDescription());
                newAssetUnique.setManufacturerName(assetGlobal.getManufacturerName());
                newAssetUnique.setOrganizationInventoryName(this.getAsset(assetGlobal).getOrganizationInventoryName());
                newAssetUnique.setSeparateSourceAmount(KualiDecimal.ZERO);
            }
            assetGlobalDetail.getAssetGlobalUniqueDetails().add(newAssetUnique);
            newAssetUnique.setNewCollectionRecord(true);
            locationQuantity--;
        }
    }

    /**
     * Sets the default values in some of the fields of the asset payment section
     *
     * @param collectionName
     * @param assetGlobal
     */
    private void handAssetPaymentsCollection(String collectionName, AssetGlobal assetGlobal) {
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) newCollectionLines.get(collectionName);
        if (assetPaymentDetail != null) {
            assetPaymentDetail.setSequenceNumber(assetGlobal.incrementFinancialDocumentLineNumber());
            // Set for document number and document type code
            if (getAssetGlobalService().existsInGroup(getAssetGlobalService().getNonNewAcquisitionCodeGroup(), assetGlobal.getAcquisitionTypeCode())) {
                assetPaymentDetail.setExpenditureFinancialDocumentNumber(getDocumentNumber());
                assetPaymentDetail.setExpenditureFinancialDocumentTypeCode(CamsConstants.DocumentTypeName.ASSET_ADD_GLOBAL);
                assetPaymentDetail.setExpenditureFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
            }

            // CSU 6702 BEGIN
            //year end logic
            if (isPeriod13(assetGlobal)) {
                assetPaymentDetail.setPostingPeriodCode(assetGlobal.getFinancialDocumentPostingPeriodCode());
                assetPaymentDetail.setPostingYear(assetGlobal.getFinancialDocumentPostingYear());
            }
            // CSU 6702 END
        }
    }

    /**
     * We are using a substitute mechanism for asset locking which can lock on assets when rule check passed. Return empty list from
     * this method.
     *
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        return new ArrayList<MaintenanceLock>();
    }

    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        super.prepareForSave();
        AssetGlobal assetGlobal = (AssetGlobal) this.getBusinessObject();

        //we need to set the posting period and posting year from the value of the drop-down box...
        if (StringUtils.isNotBlank(assetGlobal.getUniversityFiscalPeriodName())) {
            assetGlobal.setFinancialDocumentPostingPeriodCode(StringUtils.left(assetGlobal.getUniversityFiscalPeriodName(), 2));
            assetGlobal.setFinancialDocumentPostingYear(new Integer(StringUtils.right(assetGlobal.getUniversityFiscalPeriodName(), 4)));
        }

        List<AssetGlobalDetail> assetSharedDetails = assetGlobal.getAssetSharedDetails();
        List<AssetGlobalDetail> newDetails = new ArrayList<AssetGlobalDetail>();
        AssetGlobalDetail newAssetGlobalDetail = null;
        if (!assetSharedDetails.isEmpty() && !assetSharedDetails.get(0).getAssetGlobalUniqueDetails().isEmpty()) {

            for (AssetGlobalDetail locationDetail : assetSharedDetails) {
                List<AssetGlobalDetail> assetGlobalUniqueDetails = locationDetail.getAssetGlobalUniqueDetails();

                for (AssetGlobalDetail detail : assetGlobalUniqueDetails) {
                    // read from location and set it to detail
                    if (ObjectUtils.isNotNull(locationDetail.getCampusCode())) {
                        detail.setCampusCode(locationDetail.getCampusCode().toUpperCase());
                    }
                    else {
                        detail.setCampusCode(locationDetail.getCampusCode());
                    }
                    if (ObjectUtils.isNotNull(locationDetail.getBuildingCode())) {
                        detail.setBuildingCode(locationDetail.getBuildingCode().toUpperCase());
                    }
                    else {
                        detail.setBuildingCode(locationDetail.getBuildingCode());
                    }
                    detail.setBuildingRoomNumber(locationDetail.getBuildingRoomNumber());
                    detail.setBuildingSubRoomNumber(locationDetail.getBuildingSubRoomNumber());
                    detail.setOffCampusName(locationDetail.getOffCampusName());
                    detail.setOffCampusAddress(locationDetail.getOffCampusAddress());
                    detail.setOffCampusCityName(locationDetail.getOffCampusCityName());
                    detail.setOffCampusStateCode(locationDetail.getOffCampusStateCode());
                    detail.setOffCampusCountryCode(locationDetail.getOffCampusCountryCode());
                    detail.setOffCampusZipCode(locationDetail.getOffCampusZipCode());
                    newDetails.add(detail);
                }
            }
        }

        if (assetGlobal.getCapitalAssetTypeCode() != null) {
            assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE);
            AssetType capitalAssetType = assetGlobal.getCapitalAssetType();
            if (ObjectUtils.isNotNull(capitalAssetType)) {
                if (capitalAssetType.getDepreciableLifeLimit() != null && capitalAssetType.getDepreciableLifeLimit().intValue() != 0) {
                    assetGlobal.setCapitalAssetInServiceDate(assetGlobal.getCreateDate() == null ? getDateTimeService().getCurrentSqlDate() : assetGlobal.getCreateDate());
                }
                else {
                    assetGlobal.setCapitalAssetInServiceDate(null);
                }
                computeDepreciationDate(assetGlobal);
                // CSU 6702 BEGIN
                doPeriod13Changes(assetGlobal);
                // CSU 6702 END
            }
        }
        assetGlobal.getAssetGlobalDetails().clear();
        assetGlobal.getAssetGlobalDetails().addAll(newDetails);
    }

    /**
     * computes depreciation date
     *
     * @param assetGlobal
     */
    private void computeDepreciationDate(AssetGlobal assetGlobal) {
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
        if (assetPaymentDetails != null && !assetPaymentDetails.isEmpty()) {

            LOG.debug("Compute depreciation date based on asset type, depreciation convention and in-service date");
            AssetPaymentDetail firstAssetPaymentDetail = assetPaymentDetails.get(0);
            ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(firstAssetPaymentDetail.getPostingYear(), firstAssetPaymentDetail.getChartOfAccountsCode(), firstAssetPaymentDetail.getFinancialObjectCode());
            if (ObjectUtils.isNotNull(objectCode)) {
                Map<String, String> primaryKeys = new HashMap<String, String>();
                primaryKeys.put(CamsPropertyConstants.AssetDepreciationConvention.FINANCIAL_OBJECT_SUB_TYPE_CODE, objectCode.getFinancialObjectSubTypeCode());
                AssetDepreciationConvention depreciationConvention = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AssetDepreciationConvention.class, primaryKeys);
                Date depreciationDate = SpringContext.getBean(AssetDateService.class).computeDepreciationDate(assetGlobal.getCapitalAssetType(), depreciationConvention, assetGlobal.getCapitalAssetInServiceDate());
                assetGlobal.setCapitalAssetDepreciationDate(depreciationDate);
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        assetGlobal.refresh();
        assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET);
        if (ObjectUtils.isNotNull(assetGlobal.getSeparateSourceCapitalAsset())) {
            assetGlobal.setLastInventoryDate(new java.sql.Date(assetGlobal.getSeparateSourceCapitalAsset().getLastInventoryDate().getTime()));
            // CSU 6702 BEGIN
            //year end logic
            doPeriod13Changes(assetGlobal);
            // CSU 6702 END
        }
        else {
            assetGlobal.setLastInventoryDate(getDateTimeService().getCurrentSqlDate());
            // CSU 6702 BEGIN
            doPeriod13Changes(assetGlobal);
            // CSU 6702 END
        }
        List<AssetGlobalDetail> assetGlobalDetails = assetGlobal.getAssetGlobalDetails();
        AssetGlobalDetail currLocationDetail = null;
        HashMap<String, AssetGlobalDetail> locationMap = new HashMap<String, AssetGlobalDetail>();
        AssetGlobalDetail copyValue = null;
        for (AssetGlobalDetail detail : assetGlobalDetails) {
            copyValue = (AssetGlobalDetail) ObjectUtils.deepCopy(detail);
            copyValue.getAssetGlobalUniqueDetails().clear();
            String key = generateLocationKey(copyValue);
            if ((currLocationDetail = locationMap.get(key)) == null) {
                currLocationDetail = copyValue;
                locationMap.put(key, currLocationDetail);
            }
            currLocationDetail.getAssetGlobalUniqueDetails().add(copyValue);
            currLocationDetail.setLocationQuantity(currLocationDetail.getAssetGlobalUniqueDetails().size());
        }
        assetGlobal.getAssetSharedDetails().clear();
        assetGlobal.getAssetSharedDetails().addAll(locationMap.values());

        // When document starts routing, FO won't allow to change asset total amount which is a derived value from Asset payments
        // and the quantity of assets. To compare asset total amount , we need to calculate and save the value before FO made
        // changes. No handle to the workflow document and see if it starts routing. Otherwise, we can add if condition here.
        setAssetTotalAmountFromPersistence(assetGlobal);
    }

    private void setAssetTotalAmountFromPersistence(AssetGlobal assetGlobal) {
        KualiDecimal minAssetTotalAmount = getAssetGlobalService().totalPaymentByAsset(assetGlobal, false);
        KualiDecimal maxAssetTotalAmount = getAssetGlobalService().totalPaymentByAsset(assetGlobal, true);
        if (minAssetTotalAmount.isGreaterThan(maxAssetTotalAmount)) {
            // swap min and max
            KualiDecimal totalPayment = minAssetTotalAmount;
            minAssetTotalAmount = maxAssetTotalAmount;
            maxAssetTotalAmount = totalPayment;
        }
        assetGlobal.setMinAssetTotalAmount(minAssetTotalAmount);
        assetGlobal.setMaxAssetTotalAmount(maxAssetTotalAmount);
    }

    /**
     * Generates a unique using location fields to keep track of user changes
     *
     * @param location Location
     * @return Key String
     */
    private String generateLocationKey(AssetGlobalDetail location) {
        StringBuilder builder = new StringBuilder();
        builder.append(location.getCampusCode() == null ? "" : location.getCampusCode().trim().toLowerCase());
        builder.append(location.getBuildingCode() == null ? "" : location.getBuildingCode().trim().toLowerCase());
        builder.append(location.getBuildingRoomNumber() == null ? "" : location.getBuildingRoomNumber().trim().toLowerCase());
        builder.append(location.getBuildingSubRoomNumber() == null ? "" : location.getBuildingSubRoomNumber().trim().toLowerCase());
        builder.append(location.getOffCampusName() == null ? "" : location.getOffCampusName().trim().toLowerCase());
        builder.append(location.getOffCampusAddress() == null ? "" : location.getOffCampusAddress().trim().toLowerCase());
        builder.append(location.getOffCampusCityName() == null ? "" : location.getOffCampusCityName().trim().toLowerCase());
        builder.append(location.getOffCampusStateCode() == null ? "" : location.getOffCampusStateCode().trim().toLowerCase());
        builder.append(location.getOffCampusZipCode() == null ? "" : location.getOffCampusZipCode().trim().toLowerCase());
        builder.append(location.getOffCampusCountryCode() == null ? "" : location.getOffCampusCountryCode().trim().toLowerCase());
        return builder.toString();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterPost(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);
        // adjust the quantity
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        List<AssetGlobalDetail> sharedDetailsList = assetGlobal.getAssetSharedDetails();

        // each shared detail is a group of new assets to be created.
        // so to equally split the source amount into all new assets (all groups),
        // we need to get the total of ALL location quantities from each shared detail group
        int locationQtyTotal = 0;
        if (!sharedDetailsList.isEmpty()) {
            for (AssetGlobalDetail sharedDetail : sharedDetailsList) {
                sharedDetail.setLocationQuantity(sharedDetail.getAssetGlobalUniqueDetails().size());
                locationQtyTotal += sharedDetail.getLocationQuantity();
            }
        }

        // button actions for Asset Separate document
        if (getAssetGlobalService().isAssetSeparate(assetGlobal) && sharedDetailsList.size() >= 1) {
            String[] customAction = parameters.get(KRADConstants.CUSTOM_ACTION);

            // calculate equal source total amounts and set separate source amount fields
            if (customAction != null && CamsConstants.AssetSeparate.CALCULATE_EQUAL_SOURCE_AMOUNTS_BUTTON.equals(customAction[0])) {
                KualiDecimalUtils kualiDecimalService = new KualiDecimalUtils(assetGlobal.getTotalCostAmount(), CamsConstants.CURRENCY_USD);
                // add source asset to the current location quantity
                KualiDecimal[] equalSourceAmountsArray = kualiDecimalService.allocateByQuantity(locationQtyTotal + 1);
                setEqualSeparateSourceAmounts(equalSourceAmountsArray, assetGlobal);

                recalculateTotalAmount(assetGlobal);
            }

            // calculate source asset remaining amount
            if (customAction != null && (CamsConstants.AssetSeparate.CALCULATE_SEPARATE_SOURCE_REMAINING_AMOUNT_BUTTON.equals(customAction[0]))) {
                // Don't do anything because we are anyway recalculating always below
            }

            // Do recalculate every time even if button (CamsConstants.CALCULATE_SEPARATE_SOURCE_REMAINING_AMOUNT_BUTTON) wasn't
            // pressed. We do that so that it also happens on add / delete lines.
            recalculateTotalAmount(assetGlobal);
        }
    }

    /**
     * Recalculate amounts in the Recalculate Total Amount Tab
     *
     * @param assetGlobal
     */
    protected void recalculateTotalAmount(AssetGlobal assetGlobal) {
        // set Less Additions
        assetGlobal.setSeparateSourceTotalAmount(getAssetGlobalService().getUniqueAssetsTotalAmount(assetGlobal));
        // set Remaining Total Amount
        assetGlobal.setSeparateSourceRemainingAmount(assetGlobal.getTotalCostAmount().subtract(getAssetGlobalService().getUniqueAssetsTotalAmount(assetGlobal)));
    }

    /**
     * Separates the current asset amount equally into new unique assets.
     *
     * @param kualiDecimalArray
     * @param assetGlobal
     */
    public void setEqualSeparateSourceAmounts(KualiDecimal[] equalSourceAmountsArray, AssetGlobal assetGlobal) {
        int i = 0;
        for (AssetGlobalDetail assetSharedDetail : assetGlobal.getAssetSharedDetails()) {
            for (AssetGlobalDetail assetGlobalUniqueDetail : assetSharedDetail.getAssetGlobalUniqueDetails()) {
                assetGlobalUniqueDetail.setSeparateSourceAmount(equalSourceAmountsArray[i]);
                i++;
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        List<GeneralLedgerPendingEntry> generalLedgerPendingEntries = assetGlobal.getGeneralLedgerPendingEntries();
        new AssetGlobalGeneralLedgerPendingEntrySource((FinancialSystemDocumentHeader) documentHeader).doRouteStatusChange(generalLedgerPendingEntries);

        WorkflowDocument workflowDoc = documentHeader.getWorkflowDocument();

        // force pretagDetail active indicators back to true
        if (workflowDoc.isCanceled()) {
            if (ObjectUtils.isNotNull(assetGlobal)) {
                List<AssetGlobalDetail> assetGlobalDetailsList = assetGlobal.getAssetGlobalDetails();
                SpringContext.getBean(CapitalAssetBuilderModuleService.class).reactivatePretagDetails(assetGlobalDetailsList);
            }
        }

        // release lock for separate source asset...We don't include stateIsFinal since document always go to 'processed' first.
        AssetGlobalService assetGlobalService = SpringContext.getBean(AssetGlobalService.class);
        if (assetGlobalService.isAssetSeparate(assetGlobal) && (workflowDoc.isCanceled() || workflowDoc.isDisapproved() || workflowDoc.isProcessed())) {
            this.getCapitalAssetManagementModuleService().deleteAssetLocks(getDocumentNumber(), null);
        }

        // notify CAB of document status change
        if (((AssetGlobal) getBusinessObject()).isCapitalAssetBuilderOriginIndicator()) {
            SpringContext.getBean(CapitalAssetBuilderModuleService.class).notifyRouteStatusChange(documentHeader);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#getPrimaryEditedBusinessObjectClass()
     */
    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return Asset.class;
    }

    /**
     * Returns the AssetGlobalService from context
     *
     * @return AssetGlobalService
     */
    private AssetGlobalService getAssetGlobalService() {
        return SpringContext.getBean(AssetGlobalService.class);
    }

    /**
     * populates the asset location information (add new section)
     *
     * @param asset
     */
    private void populateAssetLocationTabInformation(Asset asset) {
        AssetGlobalDetail assetSharedDetail = (AssetGlobalDetail) this.getNewCollectionLine(CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS);
        assetSharedDetail.setCampusCode(asset.getCampusCode());
        assetSharedDetail.setBuildingCode(asset.getBuildingCode());
        assetSharedDetail.setBuildingRoomNumber(asset.getBuildingRoomNumber());
    }
    // CSU 6702 BEGIN
    /**
     * Checks for Accounting Period 13
     * @param assetGlobal
     * @return true if the accountingPeriod in assetGlobal is 13.
     * TODO Remove hardcoding
     */
    private boolean isPeriod13(AssetGlobal assetGlobal) {
        if (ObjectUtils.isNull(assetGlobal.getAccountingPeriod())) {
            return false;
        }
        return "13".equals(assetGlobal.getAccountingPeriod().getUniversityFiscalPeriodCode());
    }

    /**
     * Return the closing date as mm/dd/yyyy
     * @param closingYear
     * @return the closing date as mm/dd/yyyy

     */
    private String getClosingDate(Integer closingYear) {
        return getAssetGlobalService().getFiscalYearEndDayAndMonth() + closingYear.toString();
    }


    /**
     * Return the calendar Date for the closing year
     * @param closingYear
     * @return 01/01/[closing year]
     * TODO Remove hardcoding
     */
    private String getClosingCalendarDate(Integer closingYear) {
        return "01/01/" + closingYear.toString();
    }

    /**
     * Convenience method to reduce clutter
     * @return {@link DateTimeService}
     */
    private DateTimeService getDateTimeService() {
        return SpringContext.getBean(DateTimeService.class);
    }

    /**
     * Perform changes to assetGlobal on period 13.
     * @param assetGlobal
     */
    private void doPeriod13Changes(AssetGlobal assetGlobal) {
        if (isPeriod13(assetGlobal)) {
            Integer closingYear = new Integer(SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));
            String closingDate = getClosingDate(closingYear);
            try {
                updateAssetGlobalForPeriod13(assetGlobal, closingYear, closingDate);
            } catch (Exception e) {
                LOG.error(e);
            }
        }
    }

    /**
     * Update assetGlobal fields for period 13
     * @param assetGlobal
     * @param closingYear
     * @param closingDate
     * @throws ParseException
     */
    private void updateAssetGlobalForPeriod13(AssetGlobal assetGlobal, Integer closingYear, String closingDate) throws ParseException {
        assetGlobal.setCreateDate(getDateTimeService().getCurrentSqlDate());
        assetGlobal.setCapitalAssetInServiceDate(getDateTimeService().convertToSqlDate(closingDate));
        assetGlobal.setCreateDate(getDateTimeService().convertToSqlDate(closingDate));
        assetGlobal.setCapitalAssetDepreciationDate(getDateTimeService().convertToSqlDate(getClosingCalendarDate(closingYear)));
        assetGlobal.setLastInventoryDate(getDateTimeService().getCurrentSqlDate());
    }
    // CSU 6702 END

    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#populateChartOfAccountsCodeFields()
     *
     * Special treatment is needed to populate the chart code from the account number field in AssetPaymentDetails,
     * as these fields aren't PKs of BO class in the collection.
     */
    @Override
    protected void populateChartOfAccountsCodeFields() {
        super.populateChartOfAccountsCodeFields();

        AccountService acctService = SpringContext.getBean(AccountService.class);
        PersistableBusinessObject newAccount = getNewCollectionLine(CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS);
        String accountNumber = (String)ObjectUtils.getPropertyValue(newAccount, KFSPropertyConstants.ACCOUNT_NUMBER);
        String coaCode = null;

        Account account = acctService.getUniqueAccountForAccountNumber(accountNumber);
        if (ObjectUtils.isNotNull(account)) {
            coaCode = account.getChartOfAccountsCode();
        }

        try {
            ObjectUtils.setObjectProperty(newAccount, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, coaCode);
        }
        catch (Exception e) {
            LOG.error("Error in setting property value for " + KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        }
    }

}
