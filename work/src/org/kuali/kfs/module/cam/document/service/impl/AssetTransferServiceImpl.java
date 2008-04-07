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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlpeSourceDetail;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetPayment;
import org.kuali.module.cams.document.AssetTransferDocument;
import org.kuali.module.cams.service.AssetPaymentService;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.AssetTransferService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.financial.service.UniversityDateService;

public class AssetTransferServiceImpl implements AssetTransferService {
    private AssetService assetService;
    private UniversityDateService universityDateService;
    private BusinessObjectService businessObjectService;
    private AssetPaymentService assetPaymentService;


    private void adjustAmounts(AssetPayment offsetPayment, boolean reverseAmount) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method[] methods = AssetPayment.class.getMethods();
        for (Method method : methods) {
            if (method.getReturnType().isAssignableFrom(KualiDecimal.class)) {
                String setterName = "set" + method.getName().substring(3);
                Method setter = AssetPayment.class.getMethod(setterName, KualiDecimal.class);
                KualiDecimal amount = (KualiDecimal) method.invoke(offsetPayment);
                if (setter != null && amount != null) {
                    if (setterName.contains("Depreciation")) {
                        Object[] nullVal = new Object[] { null };
                        setter.invoke(offsetPayment, nullVal);
                    }
                    else if (reverseAmount) {
                        // reverse the amounts
                        setter.invoke(offsetPayment, (amount).multiply(new KualiDecimal(-1)));
                    }
                }
            }
        }
    }


    private void changeAssetOwnerData(AssetTransferDocument document, Asset saveAsset, List<PersistableBusinessObject> objects) {
        saveAsset.setOrganizationOwnerAccountNumber(document.getOrganizationOwnerAccountNumber());
        saveAsset.setOrganizationOwnerChartOfAccountsCode(document.getOrganizationOwnerChartOfAccountsCode());
        saveAsset.setLastInventoryDate(new Timestamp(new Date().getTime()));
        objects.add(saveAsset);
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
    private AssetGlpeSourceDetail createAssetGlpePostable(AssetTransferDocument document, Account plantAccount, AssetPayment assetPayment, boolean isSource) {
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();
        postable.setSource(isSource);
        postable.setAccount(plantAccount);
        postable.setAccountNumber(plantAccount.getAccountNumber());
        postable.setAmount(assetPayment.getAccountChargeAmount());
        // TODO Balance type code
        postable.setBalanceTypeCode("");
        if (isSource) {
            postable.setChartOfAccountsCode(document.getAsset().getOrganizationOwnerChartOfAccountsCode());
        }
        else {
            postable.setChartOfAccountsCode(document.getOrganizationOwnerChartOfAccountsCode());
        }
        postable.setDocumentNumber(document.getDocumentNumber());
        postable.setFinancialDocumentLineDescription("Asset Transfer " + (document.isDebit(postable) ? "Debit" : "Credit") + " Line");
        postable.setFinancialObjectCode(assetPayment.getFinancialObjectCode());
        postable.setObjectCode(assetPayment.getFinancialObject());
        postable.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
        postable.setPostingYear(getUniversityDateService().getCurrentUniversityDate().getUniversityFiscalYear());
        return postable;
    }


    /**
     * Creates GL Postables using the source plant account number and target plant account number
     */
    public void createGLPostables(AssetTransferDocument document) {
        // Create GL entries only for capital assets
        Asset asset = document.getAsset();
        if (getAssetService().isCapitalAsset(asset)) {
            asset.refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
            document.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
            boolean movableAsset = getAssetService().isAssetMovable(asset);
            if (isGLPostable(document, asset, movableAsset)) {
                List<AssetPayment> assetPayments = asset.getAssetPayments();
                createSourceGLPostables(document, assetPayments, movableAsset);
                createTargetGLPostables(document, assetPayments, movableAsset);
            }
        }
    }


    private Integer createNewPayments(AssetTransferDocument document, List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments, Integer maxSequence) {
        Integer maxSequenceNo = maxSequence;
        for (AssetPayment assetPayment : originalPayments) {
            if (CamsConstants.TRANSFER_PAYMENT_CODE_N.equals(assetPayment.getTransferPaymentCode())) {
                // copy and create new payment
                AssetPayment newPayment;
                try {
                    if (maxSequenceNo == null) {
                        maxSequenceNo = SpringContext.getBean(AssetPaymentService.class).getMaxSequenceNumber(assetPayment);
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


    private Integer createOffsetPayments(AssetTransferDocument document, List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments) {
        // TODO check if object code is active for the payment
        Integer maxSequenceNo = null;
        for (AssetPayment assetPayment : originalPayments) {
            if (CamsConstants.TRANSFER_PAYMENT_CODE_N.equals(assetPayment.getTransferPaymentCode())) {
                // copy and create an offset payment
                AssetPayment offsetPayment;
                try {
                    if (maxSequenceNo == null) {
                        maxSequenceNo = SpringContext.getBean(AssetPaymentService.class).getMaxSequenceNumber(assetPayment);
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
            srcPlantAcct = document.getAsset().getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
        }
        else {
            srcPlantAcct = document.getAsset().getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
        }
        for (AssetPayment assetPayment : assetPayments) {
            if (isPaymentEligibleForGLPosting(assetPayment)) {
                assetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
                if (ObjectUtils.isNotNull(assetPayment.getFinancialObject())) {
                    document.addGeneralLedgerPostables(createAssetGlpePostable(document, srcPlantAcct, assetPayment, true));
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
                document.addGeneralLedgerPostables(createAssetGlpePostable(document, targetPlantAcct, assetPayment, false));
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

    private boolean isPaymentEligibleForGLPosting(AssetPayment assetPayment) {
        // Payment transfer code is not "Y" and is not a Federal Contribution
        return !CamsConstants.TRANSFER_PAYMENT_CODE_Y.equals(assetPayment.getTransferPaymentCode()) && !getAssetPaymentService().isPaymentFederalContribution(assetPayment);
    }

    public void saveApprovedChanges(AssetTransferDocument document) {
        AssetHeader assetHeader = document.getAssetHeader();
        // save new asset location details to asset table, inventory date
        List<PersistableBusinessObject> persistableObjects = new ArrayList<PersistableBusinessObject>();
        Asset saveAsset = new Asset();
        saveAsset.setCapitalAssetNumber(assetHeader.getCapitalAssetNumber());
        saveAsset = (Asset) getBusinessObjectService().retrieve(saveAsset);
        changeAssetOwnerData(document, saveAsset, persistableObjects);
        if (saveAsset.getAssetPayments() == null) {
            saveAsset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_PAYMENTS);
        }
        if (getAssetService().isCapitalAsset(saveAsset)) {
            List<AssetPayment> originalPayments = saveAsset.getAssetPayments();
            Integer maxSequence = createOffsetPayments(document, persistableObjects, originalPayments);
            maxSequence = createNewPayments(document, persistableObjects, originalPayments, maxSequence);
            updateOriginalPayments(persistableObjects, originalPayments);
        }
        // create new asset payment records and offset payment records
        getBusinessObjectService().save(persistableObjects);
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


    private void updateOriginalPayments(List<PersistableBusinessObject> persistableObjects, List<AssetPayment> originalPayments) {
        for (AssetPayment assetPayment : originalPayments) {
            if (CamsConstants.TRANSFER_PAYMENT_CODE_N.equals(assetPayment.getTransferPaymentCode())) {
                try {
                    // change payment code
                    assetPayment.setTransferPaymentCode(CamsConstants.TRANSFER_PAYMENT_CODE_Y);
                    persistableObjects.add(assetPayment);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


}
