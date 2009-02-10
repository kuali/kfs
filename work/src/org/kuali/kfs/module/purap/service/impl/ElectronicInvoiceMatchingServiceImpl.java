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
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceStep;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestHeader;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceDetailRequestSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReasonType;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.dataaccess.ElectronicInvoiceItemMappingDao;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceLoadService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMappingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceMatchingService;
import org.kuali.kfs.module.purap.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.kfs.vnd.businessobject.PurchaseOrderCostSource;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class ElectronicInvoiceMatchingServiceImpl implements ElectronicInvoiceMatchingService {

    private Logger LOG = Logger.getLogger(ElectronicInvoiceMatchingServiceImpl.class);
    
    private Map<String,ElectronicInvoiceRejectReasonType> rejectReasonTypes;
    private VendorService vendorService;
    private TaxService taxService;
    
    String upperVariancePercentString;
    String lowerVariancePercentString; 
    
    public void doMatchingProcess(ElectronicInvoiceOrderHolder orderHolder) {
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Matching process started");
        }
        
        upperVariancePercentString = SpringContext.getBean(ParameterService.class).getParameterValue(ElectronicInvoiceStep.class, PurapParameterConstants.ElectronicInvoiceParameters.SALES_TAX_UPPER_VARIANCE_PERCENT);
        lowerVariancePercentString = SpringContext.getBean(ParameterService.class).getParameterValue(ElectronicInvoiceStep.class, PurapParameterConstants.ElectronicInvoiceParameters.SALES_TAX_LOWER_VARIANCE_PERCENT);;
        
        try {
            if (orderHolder.isValidateHeaderInformation()) {
                
                validateHeaderInformation(orderHolder);
                
                if (orderHolder.isInvoiceRejected()) {
                    if (LOG.isDebugEnabled()){
                        LOG.debug("Matching process failed at header validation");
                    }
                    return;
                }
            }
            
            validateInvoiceDetails(orderHolder);
            
            if (orderHolder.isInvoiceRejected()) {
                if (LOG.isDebugEnabled()){
                    LOG.debug("Matching process failed at order detail validation");
                }
                return;
            }
            
        }
        catch (NumberFormatException e) {
            if (LOG.isDebugEnabled()){
                LOG.debug("Matching process matching failed due to number format exception " + e.getMessage());
            }
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVALID_NUMBER_FORMAT, e.getMessage(), orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason);
            return;
        }
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Matching process ended successfully");
        }
    }

    private void validateHeaderInformation(ElectronicInvoiceOrderHolder orderHolder){
        
        String dunsField = PurapConstants.ElectronicInvoice.RejectDocumentFields.VENDOR_DUNS_NUMBER;
        String applnResourceKeyName = PurapKeyConstants.ERROR_REJECT_INVALID_DUNS;
        
        if (StringUtils.isEmpty(orderHolder.getDunsNumber())){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUNS_NOT_FOUND,null,orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason,dunsField,applnResourceKeyName);
            return;
        }
        
        if (orderHolder.isRejectDocumentHolder()){
            VendorDetail vendorDetail = SpringContext.getBean(VendorService.class).getVendorByDunsNumber(orderHolder.getDunsNumber());
            if (vendorDetail == null){
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUNS_INVALID, null, orderHolder.getFileName());
                orderHolder.addInvoiceHeaderRejectReason(rejectReason,dunsField,applnResourceKeyName);
                return;
            }
        }else{
            if (orderHolder.getVendorHeaderId() == null && orderHolder.getVendorDetailId() == null) {
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUNS_INVALID, null, orderHolder.getFileName());
                orderHolder.addInvoiceHeaderRejectReason(rejectReason,dunsField,applnResourceKeyName);
                return;
            }
        }

        String invoiceNumberField = PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_FILE_NUMBER;
        if (!orderHolder.isInvoiceNumberAcceptIndicatorEnabled()){
            if (StringUtils.isEmpty(orderHolder.getInvoiceNumber())){
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_ID_EMPTY,null,orderHolder.getFileName());
                orderHolder.addInvoiceHeaderRejectReason(rejectReason,invoiceNumberField,PurapKeyConstants.ERROR_REJECT_INVOICE_NUMBER_EMPTY);
                return;
            }
        }
        
        String invoiceDateField = PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_FILE_DATE;
        
        if (StringUtils.isEmpty(orderHolder.getInvoiceDateString()) || orderHolder.getInvoiceDate() == null){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_INVALID,null,orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason,invoiceDateField,PurapKeyConstants.ERROR_REJECT_INVOICE_DATE_INVALID);
            return;
        }else if (orderHolder.getInvoiceDate().after(new java.util.Date())) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_DATE_GREATER,null,orderHolder.getFileName()); 
            orderHolder.addInvoiceOrderRejectReason(rejectReason,invoiceDateField,PurapKeyConstants.ERROR_REJECT_INVOICE_DATE_GREATER);
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
            /**
             * If there are any rejects related to the summary, we're retaining it since 
             * it's not possible to get the summary amount totals from the reject doc
             */
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

//        if (lineItemTotalAmount.compareTo(BigDecimal.ZERO) != 0) { // EPIC way, but it's not needed
            if ((lineItemTotalAmount.compareTo(summaryAmount)) != 0) {
                String extraDescription = "Line Total Amount:" + lineItemTotalAmount + ",Summary Total Amount:" + summaryAmount;
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(rejectDescriptionCode, extraDescription, orderHolder.getFileName());
                orderHolder.addInvoiceHeaderRejectReason(rejectReason);
            }
//        }
    }

    private void validateItemTypes(ElectronicInvoiceOrderHolder orderHolder) {
        
        validateItemMapping(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_TAX);
        validateItemMapping(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SHIPPING);
        validateItemMapping(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING);
        validateItemMapping(orderHolder, ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT);

    }

    private void validateItemMapping(ElectronicInvoiceOrderHolder orderHolder, String kualiItemTypeCode) {

        if (!orderHolder.isItemTypeAvailableInItemMapping(kualiItemTypeCode)) {
            String extraDescription = kualiItemTypeCode;
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.ITEM_MAPPING_NOT_AVAILABLE, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceHeaderRejectReason(rejectReason);
            return;
        }
        
    }
    
//    private void validateKualiItemTypeCode(ElectronicInvoiceOrderHolder orderHolder, String invoiceItemTypeCode, String fieldName) {
//
//        String kualiItemTypeCode = orderHolder.getInvoiceItemTypeCodeFromMappings(invoiceItemTypeCode);
//
//        if (StringUtils.isEmpty(kualiItemTypeCode)) {
//            String extraDescription = invoiceItemTypeCode;
//            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.ITEM_TYPE_NAME_NOT_AVAILABLE, extraDescription, orderHolder.getFileName());
//            orderHolder.addInvoiceHeaderRejectReason(rejectReason);
//        }
//    }
    
    private void validateInvoiceDetails(ElectronicInvoiceOrderHolder orderHolder){
        
        validatePurchaseOrderMatch(orderHolder);
        
        if (orderHolder.isInvoiceRejected()){
            return;
        }
        
        validateInvoiceItems(orderHolder);
        
        if (LOG.isDebugEnabled()){
            if (!orderHolder.isInvoiceRejected()){
                LOG.debug("Purchase order document match done successfully");
            }
        }
    }
    
    private void validatePurchaseOrderMatch(ElectronicInvoiceOrderHolder orderHolder){
        
        String poIDFieldName = PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_PO_ID;
        String poID = orderHolder.getInvoicePurchaseOrderID();
        
        if (StringUtils.isEmpty(poID)){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ID_EMPTY,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,poIDFieldName,PurapKeyConstants.ERROR_REJECT_INVOICE_POID_EMPTY);
            return;
        }
        
        if (!NumberUtils.isDigits(poID)){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ID_INVALID_FORMAT,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,poIDFieldName,PurapKeyConstants.ERROR_REJECT_INVOICE_POID_INVALID);
            return;
        }
        
        PurchaseOrderDocument poDoc = orderHolder.getPurchaseOrderDocument();
        
        if (poDoc == null){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_NOT_EXISTS,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,poIDFieldName,PurapKeyConstants.ERROR_REJECT_INVOICE__PO_NOT_EXISTS);
            return;
        }
        
        if (!(poDoc.getVendorHeaderGeneratedIdentifier().equals(orderHolder.getVendorHeaderId()) &&
              poDoc.getVendorDetailAssignedIdentifier().equals(orderHolder.getVendorDetailId()))){
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_VENDOR_NOT_MATCHES_WITH_INVOICE_VENDOR,null,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason);
            return;
        }
        
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
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();
        
        if (poItem == null){
            String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.NO_MATCHING_PO_ITEM,extraDescription,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_LINE_NUMBER,PurapKeyConstants.ERROR_REJECT_INVOICE__ITEM_NOMATCH);
            return;
        }
        
        if (poLineNumbers.contains(itemHolder.getInvoiceItemLineNumber())){
            String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.DUPLIATE_INVOICE_LINE_ITEM,extraDescription,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_LINE_NUMBER,PurapKeyConstants.ERROR_REJECT_PO_ITEM_DUPLICATE);
            return;
        }else{
            poLineNumbers.add(itemHolder.getInvoiceItemLineNumber());
        }
        
        if (!poItem.isItemActiveIndicator()){
            String extraDescription = "PO Item Line Number:" + poItem.getItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INACTIVE_LINE_ITEM,extraDescription,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_LINE_NUMBER,PurapKeyConstants.ERROR_REJECT_PO_ITEM_INACTIVE);
            return; 
        }

        if (!itemHolder.isCatalogNumberAcceptIndicatorEnabled()){
            validateCatalogNumber(itemHolder);
            if (orderHolder.isInvoiceRejected()){
                return;
            }
        }
        
        if (!itemHolder.isUnitOfMeasureAcceptIndicatorEnabled()){
            if (!StringUtils.equals(poItem.getItemUnitOfMeasureCode(), itemHolder.getInvoiceItemUnitOfMeasureCode())){
                String extraDescription = "Invoice UOM:" + itemHolder.getInvoiceItemUnitOfMeasureCode() + ", PO UOM:" + poItem.getItemUnitOfMeasureCode();
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.UNIT_OF_MEASURE_MISMATCH,extraDescription,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_UOM,PurapKeyConstants.ERROR_REJECT_UOM_MISMATCH);
                return; 
            }
        }
        
        validateUnitPrice(itemHolder);
            
        if (orderHolder.isInvoiceRejected()){
            return;
        }
        
        validateSalesTax(itemHolder);
        
        if (orderHolder.isInvoiceRejected()){
            return;
        }
        
        if (poItem.getItemQuantity() != null) {
            validateQtyBasedItem(itemHolder);
        }else{
            validateNonQtyBasedItem(itemHolder);
        }
        
    }
    
    private void validateCatalogNumber(ElectronicInvoiceItemHolder itemHolder){
        
        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();
        
        String invoiceCatalogNumberStripped = itemHolder.getCatalogNumberStripped();

        /**
         * If Catalog number in invoice and po are not empty, create reject reason if it doesn't match 
         */
        if (StringUtils.isNotEmpty(invoiceCatalogNumberStripped) &&
            StringUtils.isNotEmpty(poItem.getItemCatalogNumber())){
            
            if (!StringUtils.equals(poItem.getItemCatalogNumber(), invoiceCatalogNumberStripped)){
                
                String extraDescription = "Invoice Catalog No:" + invoiceCatalogNumberStripped + ", PO Catalog No:" + poItem.getItemCatalogNumber();
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.CATALOG_NUMBER_MISMATCH,extraDescription,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_CATALOG_NUMBER,PurapKeyConstants.ERROR_REJECT_CATALOG_MISMATCH);
            }
        }else{
            
            /**
             * If catalog number is empty in PO/&Invoice, check whether the catalog check is required for the requisition source.
             * If exists in param, create reject reason.
             * If not exists, continue with UOM and unit price match.
             */
            String reqSourceRequiringCatalogMatch = SpringContext.getBean(ParameterService.class).getParameterValue(ElectronicInvoiceStep.class, PurapParameterConstants.ElectronicInvoiceParameters.REQUISITION_SOURCES_REQUIRING_CATALOG_MATCHING);
            String requisitionSourceCodeInPO = orderHolder.getPurchaseOrderDocument().getRequisitionSourceCode();
            
            if (StringUtils.isNotEmpty(reqSourceRequiringCatalogMatch)){
                String[] requisitionSourcesFromParam = StringUtils.split(reqSourceRequiringCatalogMatch,';');
                if (ArrayUtils.contains(requisitionSourcesFromParam, requisitionSourceCodeInPO)){
                    String extraDescription = "Invoice Catalog No:" + invoiceCatalogNumberStripped + ", PO Catalog No:" + poItem.getItemCatalogNumber();
                    ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.CATALOG_NUMBER_MISMATCH,extraDescription,orderHolder.getFileName());
                    orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_CATALOG_NUMBER,PurapKeyConstants.ERROR_REJECT_CATALOG_MISMATCH);
                }
            }
        }
    }
    
    private void validateQtyBasedItem(ElectronicInvoiceItemHolder itemHolder){
        
        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        
        String fileName = itemHolder.getInvoiceOrderHolder().getFileName();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();
        
        if (KualiDecimal.ZERO.compareTo(poItem.getItemOutstandingEncumberedQuantity()) >= 0) {
            //we have no quantity left encumbered on the po item
            String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.OUTSTANDING_ENCUMBERED_QTY_AVAILABLE,extraDescription,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_QUANTITY,PurapKeyConstants.ERROR_REJECT_POITEM_OUTSTANDING_QTY);
            return;
        }
        
        if (itemHolder.getInvoiceItemQuantity() == null){
            //we have quantity entered on the PO Item but the Invoice has no quantity
            String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_QTY_EMPTY,extraDescription,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_QUANTITY,PurapKeyConstants.ERROR_REJECT_POITEM_INVOICE_QTY_EMPTY);
            return;
        }else{
            
            if ((itemHolder.getInvoiceItemQuantity().compareTo(poItem.getItemOutstandingEncumberedQuantity().bigDecimalValue())) > 0) {
                //we have more quantity on the e-invoice than left outstanding encumbered on the PO item
                String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ITEM_QTY_LESSTHAN_INVOICE_ITEM_QTY,extraDescription,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_QUANTITY,PurapKeyConstants.ERROR_REJECT_POITEM_LESS_OUTSTANDING_QTY);
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
            String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.OUTSTANDING_ENCUMBERED_AMT_AVAILABLE,extraDescription,orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_LINE_NUMBER,PurapKeyConstants.ERROR_REJECT_POITEM_OUTSTANDING_EMCUMBERED_AMOUNT);
            return;
        }else{
            //we have encumbered dollars left on PO
            if ((itemHolder.getInvoiceItemSubTotalAmount().compareTo(poItem.getItemOutstandingEncumberedAmount().bigDecimalValue())) > 0) {
                String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.PO_ITEM_AMT_LESSTHAN_INVOICE_ITEM_AMT,extraDescription,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_LINE_NUMBER,PurapKeyConstants.ERROR_REJECT_POITEM_LESS_OUTSTANDING_EMCUMBERED_AMOUNT);
                return;
            }
            
        }
    }
    
    private void validateUnitPrice(ElectronicInvoiceItemHolder itemHolder){
        
        PurchaseOrderCostSource costSource = itemHolder.getInvoiceOrderHolder().getPurchaseOrderDocument().getPurchaseOrderCostSource();
        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();
        
        String extraDescription = "Invoice Item Line Number:" + itemHolder.getInvoiceItemLineNumber();
        
        BigDecimal actualVariance = itemHolder.getInvoiceItemUnitPrice().subtract(poItem.getItemUnitPrice());
        
        BigDecimal lowerPercentage = null;
        if (costSource.getItemUnitPriceLowerVariancePercent() != null){
            //Checking for lower variance
            lowerPercentage = costSource.getItemUnitPriceLowerVariancePercent();
        }
        else {
            //If the cost source itemUnitPriceLowerVariancePercent is null then
            //we'll use the exact match (100%).
            lowerPercentage = new BigDecimal(100);
        }
        
        BigDecimal lowerAcceptableVariance = (lowerPercentage.divide(new BigDecimal(100))).multiply(poItem.getItemUnitPrice()).negate();

        if (lowerAcceptableVariance.compareTo(actualVariance) > 0) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_AMT_LESSER_THAN_LOWER_VARIANCE, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_REJECT_UNITPRICE_LOWERVARIANCE);
        }
        
        BigDecimal upperPercentage = null;
        
        if (costSource.getItemUnitPriceUpperVariancePercent() != null){
            //Checking for upper variance
            upperPercentage = costSource.getItemUnitPriceUpperVariancePercent();
        }
        else {
            //If the cost source itemUnitPriceLowerVariancePercent is null then
            //we'll use the exact match (100%).
            upperPercentage = new BigDecimal(100);
        }
        BigDecimal upperAcceptableVariance = (upperPercentage.divide(new BigDecimal(100))).multiply(poItem.getItemUnitPrice());

        if (upperAcceptableVariance.compareTo(actualVariance) < 0) {
            ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.INVOICE_AMT_GREATER_THAN_UPPER_VARIANCE, extraDescription, orderHolder.getFileName());
            orderHolder.addInvoiceOrderRejectReason(rejectReason, PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_UNIT_PRICE, PurapKeyConstants.ERROR_REJECT_UNITPRICE_UPPERVARIANCE);
        }
        
    }
    
    private void validateSalesTax(ElectronicInvoiceItemHolder itemHolder){

        if (LOG.isDebugEnabled()){
            LOG.debug("Validating sales tax");
        }

        ElectronicInvoiceOrderHolder orderHolder = itemHolder.getInvoiceOrderHolder();
        PurchaseOrderItem poItem = itemHolder.getPurchaseOrderItem();
        KualiDecimal invoiceSalesTaxAmount = new KualiDecimal(itemHolder.getTaxAmount());
        
        // For reject doc, trans date should be the einvoice processed date.
        java.sql.Date transTaxDate = itemHolder.getInvoiceOrderHolder().getInvoiceProcessedDate();
        String deliveryPostalCode = poItem.getPurchaseOrder().getDeliveryPostalCode();
        KualiDecimal extendedPrice = new KualiDecimal(getExtendedPrice(itemHolder));
        
        KualiDecimal salesTaxAmountCalculated = taxService.getTotalSalesTaxAmount(transTaxDate, deliveryPostalCode, extendedPrice);
        KualiDecimal actualVariance = invoiceSalesTaxAmount.subtract(salesTaxAmountCalculated);
        
        if (LOG.isDebugEnabled()){
            LOG.debug("Sales Tax Upper Variance param - " + upperVariancePercentString);
            LOG.debug("Sales Tax Lower Variance param - " + lowerVariancePercentString);
            LOG.debug("Trans date (from invoice/rejectdoc) - " + transTaxDate);
            LOG.debug("Delivery Postal Code - " + deliveryPostalCode);
            LOG.debug("Extended price - " + extendedPrice);
            LOG.debug("Invoice item tax amount - " + invoiceSalesTaxAmount);
            LOG.debug("Sales Tax amount (from sales tax service) - " + salesTaxAmountCalculated);
        }
        
        if (StringUtils.isNotEmpty(upperVariancePercentString)){
            
            KualiDecimal upperVariancePercent = new KualiDecimal(upperVariancePercentString);
            BigDecimal upperAcceptableVariance = (upperVariancePercent.divide(new KualiDecimal(100))).multiply(salesTaxAmountCalculated).bigDecimalValue();
            
            if (upperAcceptableVariance.compareTo(actualVariance.bigDecimalValue()) < 0){
                ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.SALES_TAX_AMT_GREATER_THAN_UPPER_VARIANCE,null,orderHolder.getFileName());
                orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_TAX_AMT,PurapKeyConstants.ERROR_REJECT_TAXAMOUNT_UPPERVARIANCE);
                return;
            }
            
        }
        
        if (StringUtils.isNotEmpty(lowerVariancePercentString)){
            
            KualiDecimal lowerVariancePercent = new KualiDecimal(lowerVariancePercentString);
            BigDecimal lowerAcceptableVariance = (lowerVariancePercent.divide(new KualiDecimal(100))).multiply(salesTaxAmountCalculated).bigDecimalValue().negate();
            
            if (lowerAcceptableVariance.compareTo(BigDecimal.ZERO) >= 0 && 
                actualVariance.compareTo(BigDecimal.ZERO) >= 0){
                if (actualVariance.compareTo(lowerAcceptableVariance) > 0){
                    ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.SALES_TAX_AMT_LESSER_THAN_LOWER_VARIANCE,null,orderHolder.getFileName());
                    orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_TAX_AMT,PurapKeyConstants.ERROR_REJECT_TAXAMOUNT_LOWERVARIANCE);
                }
            }else{
                if (actualVariance.compareTo(lowerAcceptableVariance) < 0){
                    ElectronicInvoiceRejectReason rejectReason = createRejectReason(PurapConstants.ElectronicInvoice.SALES_TAX_AMT_LESSER_THAN_LOWER_VARIANCE,null,orderHolder.getFileName());
                    orderHolder.addInvoiceOrderRejectReason(rejectReason,PurapConstants.ElectronicInvoice.RejectDocumentFields.INVOICE_ITEM_TAX_AMT,PurapKeyConstants.ERROR_REJECT_TAXAMOUNT_LOWERVARIANCE);
                }
            }
        }
        
    }
    
    
    
    //Copied from PurApItemBase.calculateExtendedPrice
    private BigDecimal getExtendedPrice(ElectronicInvoiceItemHolder itemHolder){
        if (itemHolder.getPurchaseOrderItem().getItemType().isAmountBasedGeneralLedgerIndicator()) {
            // SERVICE ITEM: return unit price as extended price
            return itemHolder.getUnitPrice();
        }
        else if (ObjectUtils.isNotNull(itemHolder.getQuantity())) { // qty wont be null since it's defined as a reqd field in xsd 
            BigDecimal calcExtendedPrice = itemHolder.getUnitPrice().multiply(itemHolder.getQuantity());
            // ITEM TYPE (qty driven): return (unitPrice x qty)
            return calcExtendedPrice;
        }
        return BigDecimal.ZERO;
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

    public void setTaxService(TaxService taxService) {
        this.taxService = taxService;
    }

}
