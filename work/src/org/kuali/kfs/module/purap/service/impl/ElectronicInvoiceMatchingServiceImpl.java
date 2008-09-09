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
package org.kuali.kfs.module.purap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestHeader;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReasonType;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoiceItemMappingDao;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceLoadService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMatchingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceParserService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.PurchaseOrderCostSource;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class ElectronicInvoiceMatchingServiceImpl implements ElectronicInvoiceMatchingService {

    private Logger LOG = Logger.getLogger(ElectronicInvoiceMatchingServiceImpl.class);
    
    private Map<String,ElectronicInvoiceRejectReasonType> rejectReasonTypes;
    private VendorService vendorService;
    
    public void doMatchingProcess(ElectronicInvoiceOrderHolder orderHolder) {
        
        try {
            if (orderHolder.isValidateHeaderInformation()) {
                validateHeaderInformation(orderHolder);
                if (orderHolder.isInvoiceRejected()) {
                    return;
                }
            }
            validateInvoiceDetails(orderHolder);
        }
        catch (NumberFormatException e) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVALID_NUMBER_FORMAT, e.getMessage(), orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason);
            return;
        }
    }

    private void validateHeaderInformation(ElectronicInvoiceOrderHolder orderHolder){
        
        String dunsFieldName = "";
        
        if (StringUtils.isEmpty(orderHolder.getDunsNumber())){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUNS_NOT_FOUND,null,orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason,dunsFieldName);
            return;
        }
         
        if (orderHolder.getVendorHeaderId() == null && orderHolder.getVendorDetailId() == null) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUNS_INVALID, null, orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason,dunsFieldName);
            return;
        }

        String invoiceNumberField = "";
        
        if (!orderHolder.isInvoiceNumberAcceptIndicatorEnabled()){
            if (StringUtils.isEmpty(orderHolder.getInvoiceNumber())){
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_ID_EMPTY,null,orderHolder.getFileName());
                orderHolder.addInvoiceHeaderRejectReason(rejectReason,invoiceNumberField);
                return;
            }else{
                /**
                 * TODO: Check for dupliate invoice number
                 */
            }
        }
        
        String invoiceDateField = "";
        
        if (StringUtils.isEmpty(orderHolder.getInvoiceDateString()) || orderHolder.getInvoiceDate() == null){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_INVALID,null,orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason,invoiceDateField);
            return;
        }else if (orderHolder.getInvoiceDate().after(new java.util.Date())) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_GREATER,null,orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason,invoiceDateField);
            return;
        }

        if (orderHolder.isInformationOnly()){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INFORMATION_ONLY,null,orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason);
            return;
        }
        
        validateSummaryAmounts(orderHolder);
            
        if (orderHolder.isInvoiceRejected()) {
            return;
        }
        
        validateItemTypes(orderHolder);
        
        if (orderHolder.isInvoiceRejected()) {
            return;
        }
        
    }

    private void validateSummaryAmounts(ElectronicInvoiceOrderHolder orderHolder) {
        
        if (orderHolder.isRejectDocumentHolder()){
            return;
        }

        ElectronicInvoiceDetailRequestSummary summary = orderHolder.getElectronicInvoice().getInvoiceDetailRequestSummary();

        if (orderHolder.isTaxInLine()) {
            validateSummaryAmount(orderHolder, summary.getInvoiceTaxAmount(), ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_TAX, PurapConstants.ElectronicInvoice.TAX_SUMMARY_AMT_MISMATCH);
        }

        if (orderHolder.isShippingInLine()) {
            validateSummaryAmount(orderHolder, summary.getInvoiceShippingAmount(), ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SHIPPING, PurapConstants.ElectronicInvoice.SHIPPING_SUMMARY_AMT_MISMATCH);
        }

        if (orderHolder.isSpecialHandlingInLine()) {
            validateSummaryAmount(orderHolder, summary.getInvoiceSpecialHandlingAmount(), ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING, PurapConstants.ElectronicInvoice.SPL_HANDLING_SUMMARY_AMT_MISMATCH);
        }

        if (orderHolder.isDiscountInLine()) {
            validateSummaryAmount(orderHolder, summary.getInvoiceDiscountAmount(), ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT, PurapConstants.ElectronicInvoice.DISCOUNT_SUMMARY_AMT_MISMATCH);
        }
    }

    private void validateSummaryAmount(ElectronicInvoiceOrderHolder orderHolder, 
                                       BigDecimal summaryAmount, 
                                       String invoiceLineItemTypeCode, 
                                       String rejectDescriptionCode) {

        BigDecimal lineItemTotalAmount = orderHolder.getElectronicInvoice().getFileTotalAmountForInLineItems(invoiceLineItemTypeCode);

        if (lineItemTotalAmount.compareTo(BigDecimal.ZERO) != 0) {
            if ((lineItemTotalAmount.compareTo(summaryAmount)) != 0) {
                String extraDescription = "LineTotalAmt=" + lineItemTotalAmount + ",SummaryTotalAmt=" + summaryAmount;
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(rejectDescriptionCode, extraDescription, orderHolder.getFileName());
                orderHolder.addInvoiceHeaderRejectReason(rejectReason);
            }
        }
    }

    private void validateItemTypes(ElectronicInvoiceOrderHolder orderHolder) {
        
        String taxAmoutFieldName = "";
        String shippingAmoutFieldName = "";
        String specialHandlingAmoutFieldName = "";
        String discountAmountFieldName = "";

        if (validateKualiItemType(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_TAX, taxAmoutFieldName)) {
            validateKualiItemTypeCode(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_TAX, taxAmoutFieldName);
        }

        if (validateKualiItemType(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SHIPPING, shippingAmoutFieldName)) {
            validateKualiItemTypeCode(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SHIPPING, shippingAmoutFieldName);
        }

        if (validateKualiItemType(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING, specialHandlingAmoutFieldName)) {
            validateKualiItemTypeCode(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING, specialHandlingAmoutFieldName);
        }

        if (validateKualiItemType(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT, discountAmountFieldName)) {
            validateKualiItemTypeCode(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT, discountAmountFieldName);
        }

    }

    private boolean validateKualiItemType(ElectronicInvoiceOrderHolder orderHolder, String invoiceItemTypeCode, String fieldName) {

        if (!orderHolder.isItemTypeAvailableInKuali(invoiceItemTypeCode)) {
            String extraDescription = invoiceItemTypeCode;
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.ITEM_TYPE_MAPPING_NOT_AVAILABLE, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason);
            return false;
        }
        
        return true;

    }
    
    private void validateKualiItemTypeCode(ElectronicInvoiceOrderHolder orderHolder, String invoiceItemTypeCode, String fieldName) {

        String kualiItemTypeCode = orderHolder.getKualiItemTypeCode(invoiceItemTypeCode);

        if (StringUtils.isEmpty(kualiItemTypeCode)) {
            String extraDescription = invoiceItemTypeCode;
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.ITEM_TYPE_NAME_NOT_AVAILABLE, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason);
        }
    }
    
    private void validateInvoiceDetails(ElectronicInvoiceOrderHolder orderHolder){
        
        validatePurchaseOrderMatch(orderHolder);
        
        if (orderHolder.isInvoiceRejected()){
            return;
        }
        
        validateInvoiceItems(orderHolder);
    }
    
    private void validatePurchaseOrderMatch(ElectronicInvoiceOrderHolder orderHolder){
        
        String poIDFieldName = "";
        String poID = orderHolder.getInvoicePurchaseOrderID();
        
        if (StringUtils.isEmpty(poID)){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ID_EMPTY,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,poIDFieldName);
            return;
        }
        
        if (!NumberUtils.isDigits(poID)){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ID_INVALID_FORMAT,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,poIDFieldName);
            return;
        }
        
        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();
        
        if (poDoc == null){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_NOT_EXISTS,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,poIDFieldName);
            return;
        }
        
        if (!(poDoc.getVendorHeaderGeneratedIdentifier().equals(orderHolder.getVendorHeaderId()) &&
              poDoc.getVendorDetailAssignedIdentifier().equals(orderHolder.getVendorDetailId()))){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_VENDOR_NOT_MATCHES_WITH_INVOICE_VENDOR,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
        }
        
        /**
         * TODO: Move this code where PREQ is created
         */
//        if (!poDoc.getStatusCode().equals(PurchaseOrderStatuses.OPEN)) {
//            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_NOT_OPEN,null,orderHolder.getFileName());
//            orderHolder.addInvoiceOrderRejectReason(rejectReason);            
//        }
        
    }
    
    private void validateInvoiceItems(ElectronicInvoiceOrderHolder orderHolder){
        
        Set poLineNumbers = new HashSet();
        
        ElectronicInvoiceItemHolder[] itemHolders = orderHolder.getItems();  
        if (itemHolders != null){
            for (int i = 0; i < itemHolders.length; i++) {
                validateInvoiceItem(itemHolders[i],poLineNumbers);
            }
        }
    }
    
    private void validateInvoiceItem(ElectronicInvoiceItemHolder itemHolder,
                                     Set poLineNumbers){
        
        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        
        String fileName = itemHolder.getInvoiceOrderHolder().getFileName();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();
        
        if (poItem == null){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.NO_MATCHING_PO_ITEM,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return;
        }
        
        if (poLineNumbers.contains(itemHolder.getInvoiceItemLineNumber())){
            String extraDescription = "Invoice Item Line Number=" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUPLIATE_INVOICE_LINE_ITEM,extraDescription,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return;
        }else{
            poLineNumbers.add(itemHolder.getInvoiceItemLineNumber());
        }
        
        if (!poItem.isItemActiveIndicator()){
            String extraDescription = "PO Item Line Number=" + poItem.getItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INACTIVE_LINE_ITEM,extraDescription,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return; 
        }

        /**
         * TODO: Have to find an api which escapes all the spl chars. (Not found in StringEscapeUtils)
         */
        if (!itemHolder.isCatalogNumberAcceptIndicatorEnabled()){
            if (StringUtils.isNotEmpty(poItem.getItemCatalogNumber())){
                if (!StringUtils.equals(poItem.getItemCatalogNumber(), itemHolder.getCatalogNumber())){
                    String catalogFieldName = "";
                    ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.CATALOG_NUMBER_MISMATCH,null,orderHolder.getFileName());
                    orderHolder.addInvoiceOrderRejectReason(rejectReason,catalogFieldName);
                    return; 
                }
            }
        }
        
        if (!itemHolder.isUnitOfMeasureAcceptIndicatorEnabled()){
            if (!StringUtils.equals(poItem.getItemUnitOfMeasureCode(), itemHolder.getInvoiceItemUnitOfMeasureCode())){
                String uomFieldName = "";
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.UNIT_OF_MEASURE_MISMATCH,null,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason,uomFieldName);
                return; 
            }
        }
        
//        if ((poItem.getItemUnitPrice().compareTo(itemHolder.getInvoiceItemUnitPrice())) != 0 ) {
//            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.UNIT_PRICE_INVALID,null,orderHolder.getFileName());
//            orderHolder.addInvoiceOrderRejectReason(rejectReason);
//            return;
//        }else{
            
          validateUnitPrice(itemHolder);
            
          if (orderHolder.isInvoiceRejected()){
              return;
          }
        
        if (poItem.getItemQuantity() != null) {
            validateQtyBasedItem(itemHolder);
        }else{
            validateNonQtyBasedItem(itemHolder);
        }
        
    }
    
    private void validateQtyBasedItem(ElectronicInvoiceItemHolder itemHolder){
        
        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        
        String fileName = itemHolder.getInvoiceOrderHolder().getFileName();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();
        
        if (KualiDecimal.ZERO.compareTo(poItem.getItemOutstandingEncumberedQuantity()) >= 0) {
            //we have no quantity left encumbered on the po item
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.OUTSTANDING_ENCUMBERED_QTY_AVAILABLE,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return;
        }
        
        if (itemHolder.getInvoiceItemQuantity() == null){
            //we have quantity entered on the PO Item but the Invoice has no quantity
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_QTY_EMPTY,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return;
        }else{
            if ((itemHolder.getInvoiceItemQuantity().compareTo(poItem.getItemOutstandingEncumberedQuantity().bigDecimalValue())) > 0) {
                //we have more quantity on the e-invoice than left outstanding encumbered on the PO item
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ITEM_QTY_LESSTHAN_INVOICE_ITEM_QTY,null,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason);
                return;
            }
        }
        
    }
    
    private void validateNonQtyBasedItem(ElectronicInvoiceItemHolder itemHolder){
        
        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        
        String fileName = itemHolder.getInvoiceOrderHolder().getFileName();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();
        
        if ((KualiDecimal.ZERO.compareTo(poItem.getItemOutstandingEncumberedAmount())) >= 0) {
            //we have no dollars left encumbered on the po item
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.OUTSTANDING_ENCUMBERED_AMT_AVAILABLE,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return;
        }else{
            //we have encumbered dollars left on PO
            if ((itemHolder.getInvoiceItemSubTotalAmount().compareTo(poItem.getItemOutstandingEncumberedAmount().bigDecimalValue())) > 0) {
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ITEM_AMT_LESSTHAN_INVOICE_ITEM_AMT,null,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason);
                return;
            }
            
        }
    }
    
    private void validateUnitPrice(ElectronicInvoiceItemHolder itemHolder){
        
        PurchaseOrderCostSource costSource = itemHolder.getInvoiceOrderHolder().getPurchaseOrderDocument().getPurchaseOrderCostSource();
        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        String fileName = itemHolder.getInvoiceOrderHolder().getFileName();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();
        
        /**
         * FIXME: I dont think this check is needed here since we are already defined a rule in the PurchaseOrderCostSource maintenace doc
         * to restrict only one of the variances 
         */
        if (costSource.getItemUnitPriceLowerVariancePercent() == null && costSource.getItemUnitPriceUpperVariancePercent() == null){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_COST_SOURCE_EMPTY,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return;
        }
        
        /**
         * FIXME : Is it needed????????????
         */
        if (costSource.getItemUnitPriceLowerVariancePercent() != null && costSource.getItemUnitPriceUpperVariancePercent() != null){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_COST_SOURCE_INVALID,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return;
        }
        
        BigDecimal actualVariance = itemHolder.getInvoiceItemUnitPrice().subtract(poItem.getItemUnitPrice());
        
        if (costSource.getItemUnitPriceLowerVariancePercent() != null){
            //Checking for lower variance
            BigDecimal percentage = costSource.getItemUnitPriceLowerVariancePercent();
            BigDecimal lowerAcceptableVariance = (percentage.divide(new BigDecimal(100))).multiply(poItem.getItemUnitPrice()).negate();
            
            if (lowerAcceptableVariance.compareTo(actualVariance) > 0){
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_AMT_LESSER_THAN_LOWER_VARIANCE,null,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason);
            }
        }else{
            //Checking for upper variance
            BigDecimal percentage = costSource.getItemUnitPriceUpperVariancePercent();
            BigDecimal upperAcceptableVariance = (percentage.divide(new BigDecimal(100))).multiply(poItem.getItemUnitPrice());

            if (upperAcceptableVariance.compareTo(actualVariance) < 0){
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_AMT_GREATER_THAN_UPPER_VARIANCE,null,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason);
            }
        }
        
    }
    
    public ElectronicInvoiceRejectReason createRejectReason(String rejectReasonTypeCode, String extraDescription, String fileName) {
        
        ElectronicInvoiceRejectReasonType rejectReasonType = getElectronicInvoiceRejectReasonType(rejectReasonTypeCode);
        ElectronicInvoiceRejectReason eInvoiceRejectReason = new ElectronicInvoiceRejectReason();

        if (rejectReasonType == null){
            throw new NullPointerException("Reject reason type for " + rejectReasonTypeCode + " not available in DB");
        }
        eInvoiceRejectReason.setInvoiceFileName(fileName);
        eInvoiceRejectReason.setInvoiceRejectReasonTypeCode(rejectReasonTypeCode);

        if (StringUtils.isNotEmpty(extraDescription)) {
            eInvoiceRejectReason.setInvoiceRejectReasonDescription(rejectReasonType.getInvoiceRejectReasonTypeDescription() + " (" + extraDescription + ")");
        }
        else {
            eInvoiceRejectReason.setInvoiceRejectReasonDescription(rejectReasonType.getInvoiceRejectReasonTypeDescription());
        }

        return eInvoiceRejectReason;
        
    }
    
    public ElectronicInvoiceRejectReasonType getElectronicInvoiceRejectReasonType(String rejectReasonTypeCode){
        if (rejectReasonTypes == null){
            rejectReasonTypes = getElectronicInvoiceRejectReasonTypes();
        }
        return rejectReasonTypes.get(rejectReasonTypeCode);
    }

    private Map<String, ElectronicInvoiceRejectReasonType> getElectronicInvoiceRejectReasonTypes(){
        
        Collection<ElectronicInvoiceRejectReasonType> collection = SpringContext.getBean(BusinessObjectService.class).findAll(ElectronicInvoiceRejectReasonType.class);
        Map rejectReasonTypesMap = new HashMap<String, ElectronicInvoiceRejectReasonType>();
        
        if (collection != null &&
            collection.size() > 0){
            ElectronicInvoiceRejectReasonType[] rejectReasonTypesArr = new ElectronicInvoiceRejectReasonType[collection.size()];
            collection.toArray(rejectReasonTypesArr);
            for (int i = 0; i < rejectReasonTypesArr.length; i++) {
                rejectReasonTypesMap.put(rejectReasonTypesArr[i].getInvoiceRejectReasonTypeCode(), rejectReasonTypesArr[i]);
            }
        }
        
        return rejectReasonTypesMap;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

}
