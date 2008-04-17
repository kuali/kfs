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

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlpeSourceDetail;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.bo.AssetObjectCode;
import org.kuali.module.cams.bo.AssetOrganization;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.rules.AssetTransferDocumentRule;
import org.kuali.module.cams.service.AssetHeaderService;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.AssetTransferService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.service.UniversityDateService;

public class AssetTransferServiceImpl implements AssetTransferService {
    public static enum AmountCategory {
        EXPENSE, CAPITALIZATION, ACCUM_DEPRECIATION, OFFSET_AMOUNT;
    }

    private static final Logger LOG = Logger.getLogger(AssetTransferServiceImpl.class);
    private static final KualiDecimal KUALI_DECIMAL_ZERO = new KualiDecimal(0);
    private AssetService assetService;
    private UniversityDateService universityDateService;
    private BusinessObjectService businessObjectService;
    private AssetPaymentService assetPaymentService;


    /**
     * This method uses reflection and performs below steps on all Amount fields
     * <li>If it is a depreciation field, then reset the value to null, so that they don't get copied to offset payments </li>
     * <li>If it is an amount field, then reverse the amount by multiplying with -1 </li>
     * 
     * @param offsetPayment Offset payment
     * @param reverseAmount true if amounts needs to be multiplied with -1
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void adjustAmounts(AssetPayment offsetPayment, boolean reverseAmount) throws IllegalAccessException, InvocationTargetException {
        LOG.debug("Starting - adjustAmounts() ");
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(AssetPayment.class);
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod != null && propertyDescriptor.getPropertyType() != null && propertyDescriptor.getPropertyType().isAssignableFrom(KualiDecimal.class)) {
                KualiDecimal amount = (KualiDecimal) readMethod.invoke(offsetPayment);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                if (writeMethod != null && amount != null) {
                    // Reset periodic depreciation expenses
                    if (Pattern.matches(CamsConstants.SET_PERIOD_DEPRECIATION_AMOUNT_REGEX, writeMethod.getName().toLowerCase())) {
                        Object[] nullVal = new Object[] { null };
                        writeMethod.invoke(offsetPayment, nullVal);
                    }
                    else if (reverseAmount) {
                        // reverse the amounts
                        writeMethod.invoke(offsetPayment, (amount).multiply(new KualiDecimal(-1)));
                    }
                }

            }
        }
        LOG.debug("Finished - adjustAmounts()");

    }


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
        AssetObjectCode assetObjectCode = findAssetObjectCode(organizationOwnerChartOfAccountsCode, assetPayment);

        postable.setDocumentNumber(document.getDocumentNumber());
        if (AmountCategory.CAPITALIZATION.equals(amountCategory)) {
            postable.setFinancialDocumentLineDescription("" + (isSource ? "Reverse" : "Transfer") + " asset cost ");
            postable.setAmount(assetPayment.getAccountChargeAmount());
            postable.setFinancialObjectCode(assetObjectCode.getCapitalizationFinancialObjectCode());
            postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
            postable.setCapitalization(true);
        }
        else if (AmountCategory.ACCUM_DEPRECIATION.equals(amountCategory)) {
            postable.setFinancialDocumentLineDescription("" + (isSource ? "Reverse" : "Transfer") + " accumulated depreciation");
            postable.setAmount(assetPayment.getAccumulatedPrimaryDepreciationAmount());
            postable.setFinancialObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
            postable.setObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObject());
            postable.setAccumulatedDepreciation(true);
        }
        else if (AmountCategory.OFFSET_AMOUNT.equals(amountCategory)) {
            OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(getUniversityDateService().getCurrentFiscalYear(), document.getAsset().getOrganizationOwnerChartOfAccountsCode(), AssetTransferDocument.ASSET_TRANSFER_DOCTYPE_CD, CamsConstants.GL_BALANCE_TYPE_CDE_AC);
            postable.setFinancialDocumentLineDescription("" + (isSource ? "Reverse" : "Transfer") + " offset amount");
            postable.setAmount(assetPayment.getAccountChargeAmount().subtract(assetPayment.getAccumulatedPrimaryDepreciationAmount()));
            postable.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            postable.setObjectCode(offsetDefinition.getFinancialObject());
            postable.setOffset(true);
        }
        postable.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        postable.setPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        postable.setProjectCode(assetPayment.getProjectCode());
        postable.setSubAccountNumber(assetPayment.getSubAccountNumber());
        postable.setOrganizationReferenceId(assetPayment.getOrganizationReferenceId());
        return postable;
    }

    /**
     * @see org.kuali.module.cams.service.AssetTransferService#createGLPostables(org.kuali.module.cams.document.AssetTransferDocument)
     */
    public void createGLPostables(AssetTransferDocument document) {
        // Create GL entries only for capital assets
        Asset asset = document.getAsset();
        if (getAssetService().isCapitalAsset(asset)) {
            asset.refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
            document.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
            boolean movableAsset = getAssetService().isAssetMovable(asset);
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
     * @param persistableObjects Saveable objects list
     * @param originalPayments Original payments for the asset
     * @param maxSequence Payment sequence number
     * @return Incremented sequence number
     */
    private Integer createNewPayments(AssetTransferDocument document, List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments, Integer maxSequence) {
        Integer maxSequenceNo = maxSequence;
        for (AssetPayment assetPayment : originalPayments) {
            if (CamsConstants.TRANSFER_PAYMENT_CODE_N.equals(assetPayment.getTransferPaymentCode())) {
                // copy and create new payment
                AssetPayment newPayment;
                try {
                    if (maxSequenceNo == null) {
                        maxSequenceNo = SpringContext.getBean(AssetPaymentService.class).getMaxSequenceNumber(assetPayment.getCapitalAssetNumber());
                    }
                    // create new payment info
                    newPayment = (AssetPayment) ObjectUtils.fromByteArray(ObjectUtils.toByteArray(assetPayment));
                    newPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    newPayment.setAccountNumber(document.getOrganizationOwnerAccountNumber());
                    newPayment.setChartOfAccountsCode(document.getOrganizationOwnerChartOfAccountsCode());
                    newPayment.setSubAccountNumber(null);
                    newPayment.setDocumentNumber(document.getDocumentNumber());
                    newPayment.setFinancialDocumentTypeCode(AssetTransferDocument.ASSET_TRANSFER_DOCTYPE_CD);
                    newPayment.setFinancialDocumentPostingDate(DateUtils.convertToSqlDate(new Date()));
                    newPayment.setFinancialDocumentPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
                    newPayment.setFinancialDocumentPostingPeriodCode(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
                    adjustAmounts(newPayment, false);
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
     * @param persistableObjects List of saveable objects
     * @param originalPayments Original list of payments
     * @return Incremented sequence number
     */
    private Integer createOffsetPayments(AssetTransferDocument document, List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments) {
        Integer maxSequenceNo = null;
        for (AssetPayment assetPayment : originalPayments) {
            if (CamsConstants.TRANSFER_PAYMENT_CODE_N.equals(assetPayment.getTransferPaymentCode())) {
                // copy and create an offset payment
                AssetPayment offsetPayment;
                try {
                    if (maxSequenceNo == null) {
                        maxSequenceNo = SpringContext.getBean(AssetPaymentService.class).getMaxSequenceNumber(assetPayment.getCapitalAssetNumber());
                    }
                    offsetPayment = (AssetPayment) ObjectUtils.fromByteArray(ObjectUtils.toByteArray(assetPayment));
                    offsetPayment.setPaymentSequenceNumber(++maxSequenceNo);
                    offsetPayment.setTransferPaymentCode(CamsConstants.TRANSFER_PAYMENT_CODE_Y);
                    offsetPayment.setDocumentNumber(document.getDocumentNumber());
                    offsetPayment.setFinancialDocumentTypeCode(AssetTransferDocument.ASSET_TRANSFER_DOCTYPE_CD);
                    offsetPayment.setFinancialDocumentPostingDate(DateUtils.convertToSqlDate(new Date()));
                    offsetPayment.setFinancialDocumentPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
                    offsetPayment.setFinancialDocumentPostingPeriodCode(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
                    adjustAmounts(offsetPayment, true);
                    // add offset payment
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
                    if (accountChargeAmount != null && !accountChargeAmount.equals(KualiDecimal.ZERO)) {
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, srcPlantAcct, assetPayment, true, AmountCategory.CAPITALIZATION));
                    }
                    KualiDecimal accPrimaryDepreciationAmount = assetPayment.getAccumulatedPrimaryDepreciationAmount();
                    if (accPrimaryDepreciationAmount != null && !accPrimaryDepreciationAmount.equals(KualiDecimal.ZERO)) {
                        document.getSourceAssetGlpeSourceDetails().add(createAssetGlpePostable(document, srcPlantAcct, assetPayment, true, AmountCategory.ACCUM_DEPRECIATION));
                    }
                    if (accountChargeAmount != null && accPrimaryDepreciationAmount != null && accountChargeAmount.subtract(accPrimaryDepreciationAmount).isGreaterThan(KualiDecimal.ZERO)) {
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
                if (accountChargeAmount != null && !accountChargeAmount.equals(KualiDecimal.ZERO)) {
                    document.getTargetAssetGlpeSourceDetails().add(createAssetGlpePostable(document, targetPlantAcct, assetPayment, false, AmountCategory.CAPITALIZATION));
                }
                KualiDecimal accPrimaryDepreciationAmount = assetPayment.getAccumulatedPrimaryDepreciationAmount();
                if (accPrimaryDepreciationAmount != null && !accPrimaryDepreciationAmount.equals(KualiDecimal.ZERO)) {
                    document.getTargetAssetGlpeSourceDetails().add(createAssetGlpePostable(document, targetPlantAcct, assetPayment, false, AmountCategory.ACCUM_DEPRECIATION));
                }
                if (accountChargeAmount != null && accPrimaryDepreciationAmount != null && accountChargeAmount.subtract(accPrimaryDepreciationAmount).isGreaterThan(KualiDecimal.ZERO)) {
                    document.getTargetAssetGlpeSourceDetails().add(createAssetGlpePostable(document, targetPlantAcct, assetPayment, false, AmountCategory.OFFSET_AMOUNT));
                }
            }
        }
    }


    /**
     * Retrieves the asset object code from database for a given Chart code and payment record
     * 
     * @param chartOfAccountsCode Charts of Accounts code
     * @param assetPayment Current Payment Record
     * @return AssetObjectCode
     */
    private AssetObjectCode findAssetObjectCode(String chartOfAccountsCode, AssetPayment assetPayment) {
        Map<String, Object> pkKeys = new HashMap<String, Object>();
        pkKeys.put("universityFiscalYear", getUniversityDateService().getCurrentFiscalYear());
        pkKeys.put("chartOfAccountsCode", chartOfAccountsCode);
        pkKeys.put("financialObjectSubTypeCode", assetPayment.getFinancialObject().getFinancialObjectSubTypeCode());
        AssetObjectCode assetObjectCode = (AssetObjectCode) getBusinessObjectService().findByPrimaryKey(AssetObjectCode.class, pkKeys);
        if (ObjectUtils.isNull(assetObjectCode)) {
            throw new ReferentialIntegrityException("Asset object code is not defined for this organizationOwnerChartOfAccountsCode=" + chartOfAccountsCode + ", financialObjectSubTypeCode=" + assetPayment.getFinancialObject().getFinancialObjectSubTypeCode() + " for current fiscal year  " + getUniversityDateService().getCurrentFiscalYear());
        }
        return assetObjectCode;
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


    /**
     * Prepares the list of document URLs that can be used to displayed along with error message
     * 
     * @param headerNos Pending headers
     * @return String with list of document view URLS
     */
    private String getHeaders(String[] headerNos) {
        String value = " [";
        for (int i = 0; i < headerNos.length; i++) {
            Properties params = new Properties();
            params.put("command", "displayDocSearchView");
            params.put("docId", headerNos[i]);
            value += "<a href=\"" + UrlFactory.parameterizeUrl(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do", params) + "\">" + headerNos[i] + "</a>";
            if (i < headerNos.length - 1) {
                value += ", ";
            }
        }
        value += "]";
        return value;
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
     * @see org.kuali.module.cams.service.AssetTransferService#isTransferable(org.kuali.module.cams.document.AssetTransferDocument)
     */
    public boolean isTransferable(AssetTransferDocument document) {
        Asset asset = document.getAsset();
        boolean transferable = true;
        if (assetService.isAssetRetired(asset)) {
            transferable &= false;
            GlobalVariables.getErrorMap().putError(AssetTransferDocumentRule.DOC_HEADER_PATH, CamsKeyConstants.Transfer.ERROR_ASSET_RETIRED_NOTRANSFER, asset.getCapitalAssetNumber().toString(), asset.getRetirementReason().getRetirementReasonName());
        }
        // check if any pending transactions
        if (transferable) {
            Collection<AssetHeader> pendingHeaders = SpringContext.getBean(AssetHeaderService.class).findPendingHeadersByAsset(asset, document);
            if (pendingHeaders != null && !pendingHeaders.isEmpty()) {
                transferable &= false;
                String[] headerNos = new String[pendingHeaders.size()];
                int pos = 0;
                for (AssetHeader assetHeader : pendingHeaders) {
                    headerNos[pos] = assetHeader.getDocumentNumber();
                    pos++;
                }
                GlobalVariables.getErrorMap().putError(AssetTransferDocumentRule.DOC_HEADER_PATH, CamsKeyConstants.Transfer.ERROR_ASSET_DOCS_PENDING, getHeaders(headerNos));
            }
        }
        return transferable;
    }


    /**
     * @see org.kuali.module.cams.service.AssetTransferService#saveApprovedChanges(org.kuali.module.cams.document.AssetTransferDocument)
     */
    public void saveApprovedChanges(AssetTransferDocument document) {
        AssetHeader assetHeader = document.getAssetHeader();
        // save new asset location details to asset table, inventory date
        List<PersistableBusinessObject> persistableObjects = new ArrayList<PersistableBusinessObject>();
        Asset saveAsset = new Asset();
        saveAsset.setCapitalAssetNumber(assetHeader.getCapitalAssetNumber());
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
        saveAsset.setLastInventoryDate(new Timestamp(new Date().getTime()));
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
            if (CamsConstants.TRANSFER_PAYMENT_CODE_N.equals(assetPayment.getTransferPaymentCode())) {
                // change payment code
                assetPayment.setTransferPaymentCode(CamsConstants.TRANSFER_PAYMENT_CODE_Y);
                persistableObjects.add(assetPayment);
            }
        }
    }


}
