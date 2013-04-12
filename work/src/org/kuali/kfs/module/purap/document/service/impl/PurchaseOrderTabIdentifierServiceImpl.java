/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderTabIdentifierService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.util.ObjectUtils;


public class PurchaseOrderTabIdentifierServiceImpl implements PurchaseOrderTabIdentifierService {

    private static Logger LOG = Logger.getLogger(PurchaseOrderTabIdentifierServiceImpl.class);

    private List<String> vendorAttributes;
    private List<String> purapItemAttributes;
    private List<String> stipulationAttributes;
    private List<String> stipulationListAttributes;
    private List<String> deliveryTabAttributes;
    private List<String> paymentTabArrributes;
    private List<String> camsTabAttributes;
    private List<String> additionalTabAttributes;
    private List<String> quoteTabAttributes;
    private List<String> quoteTabListAttributes;
    private List<String> itemAttributes;
    private List<String> accountAttributes;
    private List<String> purchasingCapItemsAttributes;
    private List<String> capitalAssetSystemAttributes;
    private List<String> sensitiveDataAttributes;
    private List<String> itemCapitalAssetNumberAttributes;
    private List<String> assetLocationAttributes;



    private PurchaseOrderDocument previousPurchaseOrderDocument;
    public List<String> getVendorAttributes() {
        return vendorAttributes;
    }


    public void setVendorAttributes(List<String> vendorAttributes) {
        this.vendorAttributes = vendorAttributes;
    }


    public List<String> getPurapItemAttributes() {
        return purapItemAttributes;
    }


    public void setPurapItemAttributes(List<String> purapItemAttributes) {
        this.purapItemAttributes = purapItemAttributes;
    }


    public List<String> getStipulationAttributes() {
        return stipulationAttributes;
    }


    public void setStipulationAttributes(List<String> stipulationAttributes) {
        this.stipulationAttributes = stipulationAttributes;
    }


    public List<String> getStipulationListAttributes() {
        return stipulationListAttributes;
    }


    public void setStipulationListAttributes(List<String> stipulationListAttributes) {
        this.stipulationListAttributes = stipulationListAttributes;
    }


    public List<String> getDeliveryTabAttributes() {
        return deliveryTabAttributes;
    }


    public void setDeliveryTabAttributes(List<String> deliveryTabAttributes) {
        this.deliveryTabAttributes = deliveryTabAttributes;
    }


    public List<String> getPaymentTabArrributes() {
        return paymentTabArrributes;
    }


    public void setPaymentTabArrributes(List<String> paymentTabArrributes) {
        this.paymentTabArrributes = paymentTabArrributes;
    }


    public List<String> getCamsTabAttributes() {
        return camsTabAttributes;
    }


    public void setCamsTabAttributes(List<String> camsTabAttributes) {
        this.camsTabAttributes = camsTabAttributes;
    }


    public List<String> getAdditionalTabAttributes() {
        return additionalTabAttributes;
    }


    public void setAdditionalTabAttributes(List<String> additionalTabAttributes) {
        this.additionalTabAttributes = additionalTabAttributes;
    }


    public List<String> getQuoteTabAttributes() {
        return quoteTabAttributes;
    }


    public void setQuoteTabAttributes(List<String> quoteTabAttributes) {
        this.quoteTabAttributes = quoteTabAttributes;
    }


    public List<String> getQuoteTabListAttributes() {
        return quoteTabListAttributes;
    }


    public void setQuoteTabListAttributes(List<String> quoteTabListAttributes) {
        this.quoteTabListAttributes = quoteTabListAttributes;
    }


    public List<String> getItemAttributes() {
        return itemAttributes;
    }


    public void setItemAttributes(List<String> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }


    public List<String> getAccountAttributes() {
        return accountAttributes;
    }


    public void setAccountAttributes(List<String> accountAttributes) {
        this.accountAttributes = accountAttributes;
    }


    public List<String> getPurchasingCapItemsAttributes() {
        return purchasingCapItemsAttributes;
    }


    public void setPurchasingCapItemsAttributes(List<String> purchasingCapItemsAttributes) {
        this.purchasingCapItemsAttributes = purchasingCapItemsAttributes;
    }


    public List<String> getCapitalAssetSystemAttributes() {
        return capitalAssetSystemAttributes;
    }


    public void setCapitalAssetSystemAttributes(List<String> capitalAssetSystemAttributes) {
        this.capitalAssetSystemAttributes = capitalAssetSystemAttributes;
    }


    public List<String> getSensitiveDataAttributes() {
        return sensitiveDataAttributes;
    }


    public void setSensitiveDataAttributes(List<String> sensitiveDataAttributes) {
        this.sensitiveDataAttributes = sensitiveDataAttributes;
    }


    public List<String> getItemCapitalAssetNumberAttributes() {
        return itemCapitalAssetNumberAttributes;
    }


    public void setItemCapitalAssetNumberAttributes(List<String> itemCapitalAssetNumberAttributes) {
        this.itemCapitalAssetNumberAttributes = itemCapitalAssetNumberAttributes;
    }


    public List<String> getAssetLocationAttributes() {
        return assetLocationAttributes;
    }


    public void setAssetLocationAttributes(List<String> assetLocationAttributes) {
        this.assetLocationAttributes = assetLocationAttributes;
    }


    public PurchaseOrderDocument getPreviousPurchaseOrderDocument() {
        return previousPurchaseOrderDocument;
    }


    public void setPreviousPurchaseOrderDocument(PurchaseOrderDocument previousPurchaseOrderDocument) {
        this.previousPurchaseOrderDocument = previousPurchaseOrderDocument;
    }




    /**
     * This method checks whether list is modified
     *
     * @param attributes
     * @param slist
     * @param tlist
     * @return
     */
    private boolean isListModified(List attributes, List newList, List oldList) {
        if(isListModified(newList, oldList)){
            return true;

        }else if(isBothListsEmptyOrNull(newList, oldList)){
            return false;
        }

        int listSize = newList.size();

        for (int i = 0; i < listSize; i++) {
            Object  res1 = newList.get(i);
            Object  res2 = oldList.get(i);
             if (this.isAttributeModified(attributes, res1, res2)) {
                 return true;
             }

        }

        return false;
    }

    private  List sortItemsBasedOnItemTypeCode(List<PurApItem> items) {
        List<PurApItem> sortedItems = new ArrayList<PurApItem>();
        List<String> itemTypeCodes = new ArrayList<String>();
        for(PurApItem item : items){
            itemTypeCodes.add(item.getItemTypeCode());
        }
        Collections.sort(itemTypeCodes);
        for(String itemTypeCode : itemTypeCodes){
            for(PurApItem item : items){
                if(item.getItemTypeCode().equalsIgnoreCase(itemTypeCode)){
                    sortedItems.add(item);
                }
            }
        }

        return sortedItems;
    }


    private boolean isItemsModified(List attributes, List<PurApItem> newItems, List<PurApItem> oldItems) {

        if(isListModified(newItems, oldItems)) {
            return true;
        }else if(isBothListsEmptyOrNull(newItems, oldItems)){
            return false;
        }

        List<PurApItem> newList = sortItemsBasedOnItemTypeCode(newItems);
        List<PurApItem> oldList = sortItemsBasedOnItemTypeCode(oldItems);
        int listSize = newList.size();
        PurApItem newItem = null;
        PurApItem oldItem = null;
        for (int i = 0; i < listSize; i++) {
                newItem = newList.get(i);
                oldItem = oldList.get(i);
                if (!this.isAttributeModified(attributes, newItem, oldItem)) {
                    if (isListModified(accountAttributes, newItem.getSourceAccountingLines(), oldItem.getSourceAccountingLines())) {
                        return true;
                    }
                }
                else {
                    return true;
                }
        }
        return false;
    }



    private boolean isAttributeModified(List<String> attributes, Object newDoc, Object oldDoc) {

        for (String attr : attributes) {
            LOG.debug("checkingf attribute "+attr);
            Object res1 = null;
            Object res2 = null;

            try {
                res1 = PropertyUtils.getProperty(newDoc, attr);
                if (res1 instanceof String && StringUtils.isEmpty(String.valueOf(res1))) {
                    res1 = null;
                }
            }
            catch (Exception e) {
                LOG.error(e);
                continue;
            }

            try {
                res2 = PropertyUtils.getProperty(oldDoc, attr);

                if (res2 instanceof String && StringUtils.isEmpty(String.valueOf(res2))) {
                    res2 = null;
                }

            }
            catch (Exception e) {
                LOG.error(e);
                continue;
            }

            if (!org.apache.commons.lang.ObjectUtils.equals(res1, res2)) {
                return true;
            }

        }
        return false;

    }

    /**
     * Identifies whether the vendor tab has been modified
     *
     * @see edu.msu.ebsp.kfs.module.purap.document.service.PurchaseOrderTabIdentifierService#isVendorTabModified(edu.msu.ebsp.kfs.module.purap.document.PurchaseOrderAmendmentDocument)
     */
    @Override
    public boolean isVendorTabModified(PurchaseOrderAmendmentDocument newDoc) {

        return isAttributeModified(vendorAttributes, newDoc,previousPurchaseOrderDocument);
    }


    /**
     * Identifies whether the stipulation tab has been modified
     *
     * @see edu.msu.ebsp.kfs.module.purap.document.service.PurchaseOrderTabIdentifierService#isStipulationTabModified(edu.msu.ebsp.kfs.module.purap.document.PurchaseOrderAmendmentDocument)
     */
    @Override
    public boolean isStipulationTabModified(PurchaseOrderAmendmentDocument newDoc) {

        if (!isAttributeModified(stipulationAttributes, newDoc, previousPurchaseOrderDocument)) {
            return isListModified(stipulationListAttributes, newDoc.getPurchaseOrderVendorStipulations(), previousPurchaseOrderDocument.getPurchaseOrderVendorStipulations());
        }
        return true;
    }



    /**
     * This method identifies whether the additional tab has been modified for the passed PO document.
     *
     * @param newDoc the new purchase order document top be compared
     * @return true if the tab is modified else false
     */
    @Override
    public boolean isAdditionalTabModified(PurchaseOrderAmendmentDocument newDoc) {

        if(!isAttributeModified(additionalTabAttributes, newDoc, previousPurchaseOrderDocument)){
            return isListModified(sensitiveDataAttributes,newDoc.getPurchaseOrderSensitiveData(), previousPurchaseOrderDocument.getPurchaseOrderSensitiveData());
        }
        return true;
    }


    /**
     * This method identifies whether the delivery tab has been modified for the passed PO document.
     *
     * @param newDoc the new purchase order document top be compared
     * @return true if the tab has been modified else false
     */
    @Override
    public boolean isDeliveryTabModifed(PurchaseOrderAmendmentDocument newDoc) {

        return isAttributeModified(deliveryTabAttributes, newDoc, previousPurchaseOrderDocument);
    }


    /**
     * This method identifies whether the Payment tab has been modified for the passed PO document.
     *
     * @param newDoc the new purchase order document top be compared
     * @return true if the tab has been modified else false
     */
    @Override
    public boolean isPaymentTabModified(PurchaseOrderAmendmentDocument newDoc) {
        return isAttributeModified(paymentTabArrributes, newDoc, previousPurchaseOrderDocument);
    }


    /**
     * This method identifies whether the Quote tab has been modified for the passed PO document.
     *
     * @param newDoc the new purchase order document top be compared
     * @return true if the tab has been modified else false
     */
    @Override
    public boolean isQuoteTabModified(PurchaseOrderAmendmentDocument newDoc) {


        if(!isAttributeModified(quoteTabAttributes, newDoc, previousPurchaseOrderDocument)){
            LOG.debug("checking quote tab collection attributes");
            return  isListModified(quoteTabListAttributes, newDoc.getPurchaseOrderVendorQuotes(), previousPurchaseOrderDocument.getPurchaseOrderVendorQuotes());
        }
        return true;
    }

    /**
     * This method identifies whether the Cams tab has been modified for the passed PO document.
     *
     * @param newDoc the new purchase order document top be compared
     * @param oldDoc the retired purchase order document to be compared against
     * @return true if the tab has been modified else false
     */
    @Override
    public boolean isCamsTabModified(PurchaseOrderAmendmentDocument newDoc) {

        if (!isAttributeModified(camsTabAttributes, newDoc, previousPurchaseOrderDocument)) {
            LOG.debug("Testing capitalAssetItems attributes");

                try {
                    if(this.isAssetItemsModified(newDoc.getPurchasingCapitalAssetItems(), previousPurchaseOrderDocument.getPurchasingCapitalAssetItems())) {
                        return true;
                    }

                    if(isCapitalAssetSystemsModified(newDoc.getPurchasingCapitalAssetSystems(), previousPurchaseOrderDocument.getPurchasingCapitalAssetSystems())){
                        return true;
                    }
                }
                catch (Exception ex) {

                   LOG.error(ex);
                }


            }

        return false;
    }



    /**
     *
     * This method checks every capital asset item
     * @param newItems
     * @param oldItems
     * @return
     */
    private boolean isAssetItemsModified(List<PurchasingCapitalAssetItem> newCapitalAssetItems , List<PurchasingCapitalAssetItem> oldCapitalAssetItems) throws Exception {


        if(this.isListModified(purchasingCapItemsAttributes, newCapitalAssetItems, oldCapitalAssetItems)){
            return true;
        }



        return false;
    }


    private boolean isBothListsEmptyOrNull(List  newItems, List oldItems) {
        if((ObjectUtils.isNull(newItems) && ObjectUtils.isNull(oldItems))  || (newItems.size() == 0 && oldItems.size() == 0)) {
            return true;
        }

        return false;


    }



    private boolean isCapitalAssetSystemsModified(List<CapitalAssetSystem> newCapitalAssetSystems , List<CapitalAssetSystem> oldCapitalAssetSystems) throws Exception {

        if(this.isListModified(newCapitalAssetSystems, oldCapitalAssetSystems)) {
            return true;
        }else if(isBothListsEmptyOrNull(newCapitalAssetSystems,oldCapitalAssetSystems)){
            return false;
        }

        int newCapitalAssetSystemsSize = newCapitalAssetSystems.size();

        for (int i = 0; i < newCapitalAssetSystemsSize; i++) {
            if (this.isCapitalAssetSystemModifed(newCapitalAssetSystems.get(i), oldCapitalAssetSystems.get(i))) {
             break;
            }
         }


        return false;


    }

    /**
     *
     * This method checks whether capital asset system is modified
     * @param nsystem
     * @param osystem
     * @return
     */
    private boolean isCapitalAssetSystemModifed(CapitalAssetSystem newCapitalAssetSystem, CapitalAssetSystem oldCapitalAssetSyetm){

        //first check AssetSystem attributes followed by itemCapitalAssets, locations

        if(ObjectUtils.isNull(newCapitalAssetSystem) &&  ObjectUtils.isNull(oldCapitalAssetSyetm)){
            return false;
        }

        if(ObjectUtils.isNotNull(newCapitalAssetSystem) &&  ObjectUtils.isNull(oldCapitalAssetSyetm)){
            return true;
        }

        if(ObjectUtils.isNull(newCapitalAssetSystem) &&  ObjectUtils.isNotNull(oldCapitalAssetSyetm)){
            return true;
        }

        if(this.isAttributeModified(capitalAssetSystemAttributes, newCapitalAssetSystem, oldCapitalAssetSyetm)) {
            return true;
        }

        if(this.isListModified(itemCapitalAssetNumberAttributes, newCapitalAssetSystem.getItemCapitalAssets(), oldCapitalAssetSyetm.getItemCapitalAssets())) {
            return true;
        }

        if( this.isListModified(assetLocationAttributes, newCapitalAssetSystem.getCapitalAssetLocations(), oldCapitalAssetSyetm.getCapitalAssetLocations())) {
            return true;
        }

        return false;

    }

    /**
     * This method checks whether a list is modified
     *
     * @param l1
     * @param l2
     * @return
     */
    private boolean isListModified(List newList, List oldList) {
        if ((ObjectUtils.isNotNull(newList) && ObjectUtils.isNull(oldList)) ||
                (ObjectUtils.isNull(newList) && ObjectUtils.isNotNull(oldList)) ||
                (ObjectUtils.isNotNull(newList) && ObjectUtils.isNotNull(oldList) &&
                        (newList.size() != oldList.size()))) {
            return true;
        }

        return false;
    }

    /**
     * This method identifies whether the Items tab has been modified for the passed PO document.
     *
     * @param newDoc the new purchase order document top be compared
     * @return true if the tab has been modified else false
     */
    @Override
    public boolean isItemsTabModified(PurchaseOrderAmendmentDocument newDoc) {
    	previousPurchaseOrderDocument = this.getDocumentForComparison(newDoc.getPurapDocumentIdentifier(), newDoc.getDocumentNumber());
        LOG.debug("Testing item attributes");
        return this.isItemsModified(itemAttributes, newDoc.getItems(), previousPurchaseOrderDocument.getItems());


    }


    /**
     * Checks whether any tab other than item tab is modified
     *
     * @see edu.msu.ebsp.kfs.module.purap.document.service.PurchaseOrderTabIdentifierService#isAnyTabsModified(edu.msu.ebsp.kfs.module.purap.document.PurchaseOrderAmendmentDocument)
     */
    @Override
    public boolean isAnyTabsModified(PurchaseOrderAmendmentDocument newDoc) {

        previousPurchaseOrderDocument = this.getDocumentForComparison(newDoc.getPurapDocumentIdentifier(), newDoc.getDocumentNumber());
        LOG.debug("Testing any other tab attributes");
        return (this.isVendorTabModified(newDoc) || this.isAdditionalTabModified(newDoc) || this.isCamsTabModified(newDoc) || this.isDeliveryTabModifed(newDoc) || this.isPaymentTabModified(newDoc) || this.isQuoteTabModified(newDoc) || this.isStipulationTabModified(newDoc));

    }


    /**
     * Returns a list of modified tabs
     *
     * @see edu.msu.ebsp.kfs.module.purap.document.service.PurchaseOrderTabIdentifierService#getModifiedTabs(edu.msu.ebsp.kfs.module.purap.document.PurchaseOrderAmendmentDocument)
     */
    @Override
    public List<String> getModifiedTabs(PurchaseOrderAmendmentDocument newDoc) {

        List<String> result = new ArrayList<String>(0);

        PurchaseOrderDocument oldDoc = this.getDocumentForComparison(newDoc.getPurapDocumentIdentifier(), newDoc.getDocumentNumber());

        if (this.isVendorTabModified(newDoc)) {
            result.add(PurapConstants.EditedTabs.PURAP_VENDOR_TAB);
        }

        if (this.isAdditionalTabModified(newDoc)) {
            result.add(PurapConstants.EditedTabs.PURAP_ADDITIONAL_TAB);
        }

        if (this.isCamsTabModified(newDoc)) {
            result.add(PurapConstants.EditedTabs.PURAP_CAMS_TAB);
        }

        if (this.isDeliveryTabModifed(newDoc)) {
            result.add(PurapConstants.EditedTabs.PURAP_DELIVERY_TAB);
        }

        if (this.isPaymentTabModified(newDoc)) {
            result.add(PurapConstants.EditedTabs.PURAP_PAYMENT_TAB);
        }

        if (this.isQuoteTabModified(newDoc)) {
            result.add(PurapConstants.EditedTabs.PURAP_QUOTES_TAB);
        }

        if (this.isStipulationTabModified(newDoc)) {
            result.add(PurapConstants.EditedTabs.PURAP_STIPULATION_TAB);
        }

        if (this.isItemsTabModified(newDoc)) {
            result.add(PurapConstants.EditedTabs.PURAP_ITEMS_TAB);
        }

        return result;

    }


    /**
     * This method returns the retired PO document
     *
     * @param id
     * @return
     */
    private PurchaseOrderDocument getDocumentForComparison(Integer id, String docNumber) {
        previousPurchaseOrderDocument =SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderDocumentForComparison(id, docNumber);
        return previousPurchaseOrderDocument;
    }


}

