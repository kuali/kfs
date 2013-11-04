/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.cab.businessobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItemCapitalAsset;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchasingAccountsPayableItemAsset extends PersistableBusinessObjectBase implements Comparable<PurchasingAccountsPayableItemAsset> {
    private static final Logger LOG = Logger.getLogger(PurchasingAccountsPayableItemAsset.class);

    private String documentNumber;
    private Integer accountsPayableLineItemIdentifier;
    private Integer capitalAssetBuilderLineNumber;
    private String accountsPayableLineItemDescription;
    private KualiDecimal accountsPayableItemQuantity;
    private String capitalAssetManagementDocumentNumber;
    private String activityStatusCode;

    private PurchasingAccountsPayableDocument purchasingAccountsPayableDocument;
    private List<PurchasingAccountsPayableLineAssetAccount> purchasingAccountsPayableLineAssetAccounts;

    // non persistent fields
    private boolean active;
    private Integer itemLineNumber;
    private boolean additionalChargeNonTradeInIndicator;
    private boolean tradeInAllowance;
    private boolean itemAssignedToTradeInIndicator;
    private KualiDecimal unitCost;
    private KualiDecimal totalCost;
    private String firstFincialObjectCode;
    private KualiDecimal splitQty;
    private boolean selectedValue;
    private String itemTypeCode;
    private String lockingInformation;
    // used for Capital Asset Transaction
    private String capitalAssetTransactionTypeCode;
    private List<ItemCapitalAsset> purApItemAssets;
    private Integer capitalAssetSystemIdentifier;

    private Integer purchaseOrderItemIdentifier;
    // used to control "create asset" and "apply payment" button display
    private boolean createAssetIndicator;
    private boolean applyPaymentIndicator;

    private String preTagInquiryUrl;
    private List<Long> approvedAssetNumbers;

    private Integer paymentRequestIdentifier;

    public PurchasingAccountsPayableItemAsset() {
        this.purchasingAccountsPayableLineAssetAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
        this.selectedValue = false;
        this.createAssetIndicator = false;
        this.applyPaymentIndicator = false;
        this.purApItemAssets = new ArrayList<ItemCapitalAsset>();
    }

    // constructor used for split
    public PurchasingAccountsPayableItemAsset(PurchasingAccountsPayableItemAsset initialItemAsset) {
        this.documentNumber = initialItemAsset.documentNumber;
        this.accountsPayableLineItemIdentifier = initialItemAsset.getAccountsPayableLineItemIdentifier();
        this.accountsPayableLineItemDescription = initialItemAsset.getAccountsPayableLineItemDescription();
        this.itemLineNumber = initialItemAsset.getItemLineNumber();
        this.firstFincialObjectCode = initialItemAsset.getFirstFincialObjectCode();
        this.activityStatusCode = initialItemAsset.getActivityStatusCode();
        this.tradeInAllowance = initialItemAsset.isTradeInAllowance();
        this.itemAssignedToTradeInIndicator = initialItemAsset.isItemAssignedToTradeInIndicator();
        this.additionalChargeNonTradeInIndicator = initialItemAsset.isAdditionalChargeNonTradeInIndicator();
        this.purchasingAccountsPayableLineAssetAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
        this.selectedValue = false;
        this.createAssetIndicator = initialItemAsset.isCreateAssetIndicator();
        this.applyPaymentIndicator = initialItemAsset.isApplyPaymentIndicator();
        this.purchaseOrderItemIdentifier = initialItemAsset.getPurchaseOrderItemIdentifier();
        this.capitalAssetTransactionTypeCode = initialItemAsset.getCapitalAssetTransactionTypeCode();
        this.purApItemAssets = new ArrayList<ItemCapitalAsset>(initialItemAsset.getPurApItemAssets());
        this.capitalAssetSystemIdentifier = initialItemAsset.getCapitalAssetSystemIdentifier();
        this.purchasingAccountsPayableDocument = initialItemAsset.getPurchasingAccountsPayableDocument();
        this.lockingInformation = initialItemAsset.getLockingInformation();
    }


    /**
     * Gets the lockingInformation attribute.
     * @return Returns the lockingInformation.
     */
    public String getLockingInformation() {
        return lockingInformation;
    }

    /**
     * Sets the lockingInformation attribute value.
     * @param lockingInformation The lockingInformation to set.
     */
    public void setLockingInformation(String lockingInformation) {
        this.lockingInformation = lockingInformation;
    }

    /**
     * Gets the capitalAssetSystemIdentifier attribute.
     *
     * @return Returns the capitalAssetSystemIdentifier.
     */
    public Integer getCapitalAssetSystemIdentifier() {
        return capitalAssetSystemIdentifier;
    }

    /**
     * Sets the capitalAssetSystemIdentifier attribute value.
     *
     * @param capitalAssetSystemIdentifier The capitalAssetSystemIdentifier to set.
     */
    public void setCapitalAssetSystemIdentifier(Integer capitalAssetSystemIdentifier) {
        this.capitalAssetSystemIdentifier = capitalAssetSystemIdentifier;
    }

    /**
     * Gets the purchaseOrderItemIdentifier attribute.
     *
     * @return Returns the purchaseOrderItemIdentifier.
     */
    public Integer getPurchaseOrderItemIdentifier() {
        return purchaseOrderItemIdentifier;
    }

    /**
     * Sets the purchaseOrderItemIdentifier attribute value.
     *
     * @param purchaseOrderItemIdentifier The purchaseOrderItemIdentifier to set.
     */
    public void setPurchaseOrderItemIdentifier(Integer purchaseOrderItemIdentifier) {
        this.purchaseOrderItemIdentifier = purchaseOrderItemIdentifier;
    }


    /**
     * Gets the capitalAssetNumbers attribute.
     *
     * @return Returns the capitalAssetNumbers.
     */
    public List<ItemCapitalAsset> getPurApItemAssets() {
        return purApItemAssets;
    }

    /**
     * Sets the capitalAssetNumbers attribute value.
     *
     * @param capitalAssetNumbers The capitalAssetNumbers to set.
     */
    public void setPurApItemAssets(List<ItemCapitalAsset> capitalAssetNumbers) {
        this.purApItemAssets = capitalAssetNumbers;
    }

    /**
     * Gets the capitalAssetNumbers attribute.
     *
     * @return Returns the capitalAssetNumbers.
     */
    public ItemCapitalAsset getPurApItemAsset(int index) {
        while (getPurApItemAssets().size() <= index) {
            getPurApItemAssets().add(new PurchaseOrderItemCapitalAsset());
        }
        return getPurApItemAssets().get(index);
    }


    /**
     * Gets the createAssetIndicator attribute.
     *
     * @return Returns the createAssetIndicator.
     */
    public boolean isCreateAssetIndicator() {
        return createAssetIndicator;
    }

    /**
     * Sets the createAssetIndicator attribute value.
     *
     * @param createAssetIndicator The createAssetIndicator to set.
     */
    public void setCreateAssetIndicator(boolean createAssetIndicator) {
        this.createAssetIndicator = createAssetIndicator;
    }

    /**
     * Gets the applyPaymentIndicator attribute.
     *
     * @return Returns the applyPaymentIndicator.
     */
    public boolean isApplyPaymentIndicator() {
        return applyPaymentIndicator;
    }

    /**
     * Sets the applyPaymentIndicator attribute value.
     *
     * @param applyPaymentIndicator The applyPaymentIndicator to set.
     */
    public void setApplyPaymentIndicator(boolean applyPaymentIndicator) {
        this.applyPaymentIndicator = applyPaymentIndicator;
    }

    /**
     * Gets the selectedValue attribute.
     *
     * @return Returns the selectedValue.
     */
    public boolean isSelectedValue() {
        return selectedValue;
    }

    /**
     * Sets the selectedValue attribute value.
     *
     * @param selectedValue The selectedValue to set.
     */
    public void setSelectedValue(boolean selectedValue) {
        this.selectedValue = selectedValue;
    }

    /**
     * Gets the itemTypeCode attribute.
     *
     * @return Returns the itemTypeCode.
     */
    public String getItemTypeCode() {
        return itemTypeCode;
    }

    /**
     * Sets the itemTypeCode attribute value.
     *
     * @param itemTypeCode The itemTypeCode to set.
     */
    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }

    /**
     * Gets the capitalAssetTransactionTypeCode attribute.
     *
     * @return Returns the capitalAssetTransactionTypeCode.
     */
    public String getCapitalAssetTransactionTypeCode() {
        return capitalAssetTransactionTypeCode;
    }

    /**
     * Sets the capitalAssetTransactionTypeCode attribute value.
     *
     * @param capitalAssetTransactionTypeCode The capitalAssetTransactionTypeCode to set.
     */
    public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    /**
     * Gets the additionalChargeNonTradeInIndicator attribute.
     *
     * @return Returns the additionalChargeNonTradeInIndicator.
     */
    public boolean isAdditionalChargeNonTradeInIndicator() {
        return additionalChargeNonTradeInIndicator;
    }

    /**
     * Sets the additionalChargeNonTradeInIndicator attribute value.
     *
     * @param additionalChargeNonTradeInIndicator The additionalChargeNonTradeInIndicator to set.
     */
    public void setAdditionalChargeNonTradeInIndicator(boolean additionalChargeNonTradeInIndicator) {
        this.additionalChargeNonTradeInIndicator = additionalChargeNonTradeInIndicator;
    }

    /**
     * Gets the tradeInAllowance attribute.
     *
     * @return Returns the tradeInAllowance.
     */
    public boolean isTradeInAllowance() {
        return tradeInAllowance;
    }

    /**
     * Sets the tradeInAllowance attribute value.
     *
     * @param tradeInAllowance The tradeInAllowance to set.
     */
    public void setTradeInAllowance(boolean tradeInAllowance) {
        this.tradeInAllowance = tradeInAllowance;
    }

    /**
     * Gets the splitQty attribute.
     *
     * @return Returns the splitQty.
     */
    public KualiDecimal getSplitQty() {
        return splitQty;
    }

    /**
     * Sets the splitQty attribute value.
     *
     * @param splitQty The splitQty to set.
     */
    public void setSplitQty(KualiDecimal splitQty) {
        this.splitQty = splitQty;
    }


    /**
     * Gets the purchasingAccountsPayableLineAssetAccounts attribute.
     *
     * @return Returns the purchasingAccountsPayableLineAssetAccounts.
     */
    public List<PurchasingAccountsPayableLineAssetAccount> getPurchasingAccountsPayableLineAssetAccounts() {
        return purchasingAccountsPayableLineAssetAccounts;
    }

    /**
     * Sets the purchasingAccountsPayableLineAssetAccounts attribute value.
     *
     * @param purchasingAccountsPayableLineAssetAccounts The purchasingAccountsPayableLineAssetAccounts to set.
     */
    public void setPurchasingAccountsPayableLineAssetAccounts(List<PurchasingAccountsPayableLineAssetAccount> purchasingAccountsPayableLineAssetAccounts) {
        this.purchasingAccountsPayableLineAssetAccounts = purchasingAccountsPayableLineAssetAccounts;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the accountsPayableLineItemIdentifier attribute.
     *
     * @return Returns the accountsPayableLineItemIdentifier.
     */
    public Integer getAccountsPayableLineItemIdentifier() {
        return accountsPayableLineItemIdentifier;
    }

    /**
     * Sets the accountsPayableLineItemIdentifier attribute value.
     *
     * @param accountsPayableLineItemIdentifier The accountsPayableLineItemIdentifier to set.
     */
    public void setAccountsPayableLineItemIdentifier(Integer accountsPayableLineItemIdentifier) {
        this.accountsPayableLineItemIdentifier = accountsPayableLineItemIdentifier;
    }

    /**
     * Gets the accountsPayableLineItemDescription attribute.
     *
     * @return Returns the accountsPayableLineItemDescription.
     */
    public String getAccountsPayableLineItemDescription() {
        return accountsPayableLineItemDescription;
    }

    /**
     * Sets the accountsPayableLineItemDescription attribute value.
     *
     * @param accountsPayableLineItemDescription The accountsPayableLineItemDescription to set.
     */
    public void setAccountsPayableLineItemDescription(String accountsPayableLineItemDescription) {
        this.accountsPayableLineItemDescription = accountsPayableLineItemDescription;
    }

    /**
     * Gets the accountsPayableItemQuantity attribute.
     *
     * @return Returns the accountsPayableItemQuantity.
     */
    public KualiDecimal getAccountsPayableItemQuantity() {
        return accountsPayableItemQuantity;
    }

    /**
     * Sets the accountsPayableItemQuantity attribute value.
     *
     * @param accountsPayableItemQuantity The accountsPayableItemQuantity to set.
     */
    public void setAccountsPayableItemQuantity(KualiDecimal accountsPayableItemQuantity) {
        this.accountsPayableItemQuantity = accountsPayableItemQuantity;
    }

    /**
     * Gets the capitalAssetBuilderLineNumber attribute.
     *
     * @return Returns the capitalAssetBuilderLineNumber
     */
    public Integer getCapitalAssetBuilderLineNumber() {
        return capitalAssetBuilderLineNumber;
    }

    /**
     * Sets the capitalAssetBuilderLineNumber attribute.
     *
     * @param capitalAssetBuilderLineNumber The capitalAssetBuilderLineNumber to set.
     */
    public void setCapitalAssetBuilderLineNumber(Integer capitalAssetBuilderLineNumber) {
        this.capitalAssetBuilderLineNumber = capitalAssetBuilderLineNumber;
    }


    /**
     * Gets the capitalAssetManagementDocumentNumber attribute.
     *
     * @return Returns the capitalAssetManagementDocumentNumber
     */
    public String getCapitalAssetManagementDocumentNumber() {
        return capitalAssetManagementDocumentNumber;
    }

    /**
     * Sets the capitalAssetManagementDocumentNumber attribute.
     *
     * @param capitalAssetManagementDocumentNumber The capitalAssetManagementDocumentNumber to set.
     */
    public void setCapitalAssetManagementDocumentNumber(String capitalAssetManagementDocumentNumber) {
        this.capitalAssetManagementDocumentNumber = capitalAssetManagementDocumentNumber;
    }

    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("accountsPayableLineItemIdentifier", this.accountsPayableLineItemIdentifier);
        m.put("capitalAssetBuilderLineNumber", this.capitalAssetBuilderLineNumber);
        return m;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    public boolean isActive() {
        return CabConstants.ActivityStatusCode.NEW.equalsIgnoreCase(this.getActivityStatusCode()) ||
                CabConstants.ActivityStatusCode.MODIFIED.equalsIgnoreCase(this.getActivityStatusCode());
    }

    /**
     * Gets the activityStatusCode attribute.
     *
     * @return Returns the activityStatusCode.
     */
    public String getActivityStatusCode() {
        return activityStatusCode;
    }

    /**
     * Sets the activityStatusCode attribute value.
     *
     * @param activityStatusCode The activityStatusCode to set.
     */
    public void setActivityStatusCode(String activityStatusCode) {
        this.activityStatusCode = activityStatusCode;
    }

    /**
     * Gets the purchasingAccountsPayableDocument attribute.
     *
     * @return Returns the purchasingAccountsPayableDocument.
     */
    public PurchasingAccountsPayableDocument getPurchasingAccountsPayableDocument() {
        return purchasingAccountsPayableDocument;
    }

    /**
     * Sets the purchasingAccountsPayableDocument attribute value.
     *
     * @param purchasingAccountsPayableDocument The purchasingAccountsPayableDocument to set.
     */
    public void setPurchasingAccountsPayableDocument(PurchasingAccountsPayableDocument purchasingAccountsPayableDocument) {
        this.purchasingAccountsPayableDocument = purchasingAccountsPayableDocument;
    }

    /**
     * Gets the itemAssignedToTradeInIndicator attribute.
     *
     * @return Returns the itemAssignedToTradeInIndicator.
     */
    public boolean isItemAssignedToTradeInIndicator() {
        return itemAssignedToTradeInIndicator;
    }

    /**
     * Sets the itemAssignedToTradeInIndicator attribute value.
     *
     * @param itemAssignedToTradeInIndicator The itemAssignedToTradeInIndicator to set.
     */
    public void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator) {
        this.itemAssignedToTradeInIndicator = itemAssignedToTradeInIndicator;
    }

    /**
     * Gets the unitCost attribute.
     *
     * @return Returns the unitCost.
     */
    public KualiDecimal getUnitCost() {
        return unitCost;
    }

    /**
     * Sets the unitCost attribute value.
     *
     * @param unitCost The unitCost to set.
     */
    public void setUnitCost(KualiDecimal unitCost) {
        this.unitCost = unitCost;
    }

    /**
     * Gets the totalCost attribute.
     *
     * @return Returns the totalCost.
     */
    public KualiDecimal getTotalCost() {
        return totalCost;
    }

    /**
     * Sets the totalCost attribute value.
     *
     * @param totalCost The totalCost to set.
     */
    public void setTotalCost(KualiDecimal totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Gets the firstFincialObjectCode attribute.
     *
     * @return Returns the firstFincialObjectCode.
     */
    public String getFirstFincialObjectCode() {
        return firstFincialObjectCode;
    }

    /**
     * Sets the firstFincialObjectCode attribute value.
     *
     * @param firstFincialObjectCode The firstFincialObjectCode to set.
     */
    public void setFirstFincialObjectCode(String firstFincialObjectCode) {
        this.firstFincialObjectCode = firstFincialObjectCode;
    }

    public String getPreTagInquiryUrl() {
        if (StringUtils.isNotBlank(this.preTagInquiryUrl)) {
            return preTagInquiryUrl;
        }

        if (ObjectUtils.isNotNull(this.getPurchasingAccountsPayableDocument())) {
            Integer purchaseOrderIdentifier = this.getPurchasingAccountsPayableDocument().getPurchaseOrderIdentifier();

            PurApLineService purApLineService = SpringContext.getBean(PurApLineService.class);
            if (purApLineService.isPretaggingExisting(purApLineService.getPreTagLineItem(purchaseOrderIdentifier, this.getItemLineNumber()))) {
                String baseUrl = KFSConstants.INQUIRY_ACTION;
                Properties parameters = new Properties();
                parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
                parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, Pretag.class.getName());
                parameters.put(CabPropertyConstants.Pretag.PURCHASE_ORDER_NUMBER, purchaseOrderIdentifier.toString());
                parameters.put(CabPropertyConstants.Pretag.ITEM_LINE_NUMBER, this.getItemLineNumber().toString());

                this.preTagInquiryUrl = UrlFactory.parameterizeUrl(baseUrl, parameters);

                return this.preTagInquiryUrl;
            }
        }
        return "";
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(PurchasingAccountsPayableItemAsset o) {
        boolean o1ItemTypeBelowTheLine = this.isAdditionalChargeNonTradeInIndicator() || this.isTradeInAllowance();
        boolean o2ItemTypeBelowTheLine = o.isAdditionalChargeNonTradeInIndicator() || o.isTradeInAllowance();
        if (o1ItemTypeBelowTheLine && !o2ItemTypeBelowTheLine) {
            return 1;
        }
        else if (o2ItemTypeBelowTheLine && !o1ItemTypeBelowTheLine) {
            return -1;
        }
        return 0;
    }

    /**
     * Gets the approvedAssetNumbers attribute.
     *
     * @return Returns the approvedAssetNumbers.
     */
    public List<Long> getApprovedAssetNumbers() {
        if (this.approvedAssetNumbers != null && !this.approvedAssetNumbers.isEmpty()) {
            return this.approvedAssetNumbers;
        }
        else {
            this.approvedAssetNumbers = new ArrayList<Long>();
            if (!StringUtils.isEmpty(this.getCapitalAssetManagementDocumentNumber())) {
                Map<String, String> fieldValues = new HashMap<String, String>();
                if (CabConstants.ActivityStatusCode.PROCESSED_IN_CAMS.equalsIgnoreCase(this.getActivityStatusCode())) {
                    // get asset number from asset global add doc
                    fieldValues.put(CamsPropertyConstants.AssetGlobalDetail.DOCUMENT_NUMBER, this.getCapitalAssetManagementDocumentNumber());
                    Collection<AssetGlobalDetail> assetGlobalDetails = SpringContext.getBean(BusinessObjectService.class).findMatching(AssetGlobalDetail.class, fieldValues);
                    for (AssetGlobalDetail detail : assetGlobalDetails) {
                        this.approvedAssetNumbers.add(detail.getCapitalAssetNumber());
                    }
                    if (assetGlobalDetails.isEmpty()) {
                        // get asset number from asset payment doc
                        fieldValues.clear();
                        fieldValues.put(CamsPropertyConstants.DOCUMENT_NUMBER, this.getCapitalAssetManagementDocumentNumber());
                        Collection<AssetPaymentAssetDetail> paymentAssetDetails = SpringContext.getBean(BusinessObjectService.class).findMatching(AssetPaymentAssetDetail.class, fieldValues);
                        for (AssetPaymentAssetDetail detail : paymentAssetDetails) {
                            this.approvedAssetNumbers.add(detail.getCapitalAssetNumber());
                        }
                    }
                }
            }
            return this.approvedAssetNumbers;
        }
    }

    private DataDictionaryService getDataDictionaryService() {
        return SpringContext.getBean(DataDictionaryService.class);
    }

    public PurchasingAccountsPayableLineAssetAccount getPurchasingAccountsPayableLineAssetAccount(int index) {
        int size = getPurchasingAccountsPayableLineAssetAccounts().size();
        while (size <= index || getPurchasingAccountsPayableLineAssetAccounts().get(index) == null) {
            getPurchasingAccountsPayableLineAssetAccounts().add(size++, new PurchasingAccountsPayableLineAssetAccount());
        }
        return getPurchasingAccountsPayableLineAssetAccounts().get(index);

    }

    /**
     * Gets the paymentRequestIdentifier attribute.
     * @return Returns the paymentRequestIdentifier.
     */
    public Integer getPaymentRequestIdentifier() {
        return paymentRequestIdentifier;
    }

    /**
     * Sets the paymentRequestIdentifier attribute value.
     * @param paymentRequestIdentifier The paymentRequestIdentifier to set.
     */
    public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        this.paymentRequestIdentifier = paymentRequestIdentifier;
    }

    /**
     * KFSCNTRB-1676/FSKD-5487
     * Returns true if it is an active TRDI additional charge asset line.
     */
    public boolean isActiveAdditionalTRDI() {
        return isTradeInAllowance() && isActive();
    }

    /**
     * KFSCNTRB-1676/FSKD-5487
     * Returns true if it is an active non-TRDI additional charge asset line.
     */
    public boolean isActiveAdditionalNonTRDI() {
        return isAdditionalChargeNonTradeInIndicator() && isActive();
    }

    /**
     * KFSCNTRB-1676/FSKD-5487
     * Returns true if it is an active trade-in ITEM asset line.
     */
    public boolean isActiveItemTradeIn() {
        return !isAdditionalChargeNonTradeInIndicator() && !isTradeInAllowance() && isItemAssignedToTradeInIndicator() && isActive();
    }

    /**
     * KFSCNTRB-1676/FSKD-5487
     * Returns true if it is an active ITEM asset line.
     */
    public boolean isActiveItemLine() {
        return !isAdditionalChargeNonTradeInIndicator() && !isTradeInAllowance() && isActive();
    }

}
