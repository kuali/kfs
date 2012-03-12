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
package org.kuali.kfs.module.purap.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceItemMapping;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceOrder;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoicePostalAddress;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectItem;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceRejectReason;
import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.util.ElectronicInvoiceUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This is a holder class which can be passed to the matching service to validate einvoice or reject doc data 
 * against the po document.   
 * 
 */

public class ElectronicInvoiceOrderHolder {
    
    private final static Logger LOG = Logger.getLogger(ElectronicInvoiceOrderHolder.class);
    
    private ElectronicInvoiceRejectDocument rejectDocument;
    private ElectronicInvoiceOrder invoiceOrder;
    private ElectronicInvoice eInvoice;
    private PurchaseOrderDocument poDocument;
    private Map<String,ElectronicInvoiceItemMapping> itemTypeMappings;
    private Map<String,ItemType> kualiItemTypes;
    
    private Map<String, FieldErrorHelper> errorFieldDetails = new HashMap<String, FieldErrorHelper>(); 
    
    private List<ElectronicInvoiceItemHolder> items = new ArrayList<ElectronicInvoiceItemHolder>();
    
    private boolean isRejected = false;
    private boolean isRejectDocumentHolder;
    private boolean validateHeader;
    
    private String[] summaryRejectCodes = new String[]{PurapConstants.ElectronicInvoice.TAX_SUMMARY_AMT_MISMATCH,
                                                       PurapConstants.ElectronicInvoice.SHIPPING_SUMMARY_AMT_MISMATCH,
                                                       PurapConstants.ElectronicInvoice.SPL_HANDLING_SUMMARY_AMT_MISMATCH,
                                                       PurapConstants.ElectronicInvoice.DISCOUNT_SUMMARY_AMT_MISMATCH};
    
    public ElectronicInvoiceOrderHolder(ElectronicInvoiceRejectDocument rejectDocument,
                                        Map itemTypeMappings,
                                        Map itemTypes){
        
        /**
         * This class has been designed based on good citizen pattern.
         */
        if (rejectDocument == null){
            throw new NullPointerException("ElectronicInvoiceRejectDocument should not be null");
        }
        
        this.rejectDocument = rejectDocument;
        this.itemTypeMappings = itemTypeMappings;
        this.poDocument = rejectDocument.getCurrentPurchaseOrderDocument();
        this.kualiItemTypes = itemTypes;
        
        isRejectDocumentHolder = true;
        validateHeader = true;
        
        for (int i = 0; i < rejectDocument.getInvoiceRejectItems().size(); i++) {
            
            ElectronicInvoiceRejectItem invoiceRejectItem = rejectDocument.getInvoiceRejectItems().get(i);
            
            PurApItem poItem = null;
            if (poDocument != null){
                try{
                    poItem = poDocument.getItemByLineNumber(invoiceRejectItem.getInvoiceReferenceItemLineNumber());
                }catch(NullPointerException e){
                    /**
                     * Not needed to handle this invalid item here, this will be handled in the matching process 
                     */
                }
            }
            
            items.add(new ElectronicInvoiceItemHolder(invoiceRejectItem,itemTypeMappings,poItem == null ? null : (PurchaseOrderItem)poItem,this));
        }
        
        /**
         * It's needed to retain any reject reasons which are related to summary amount matching
         * which should not escape from the matching process 
         */
        retainSummaryRejects(rejectDocument);
        
    }
    
    protected void retainSummaryRejects(ElectronicInvoiceRejectDocument rejectDocument){
        
        if (LOG.isInfoEnabled()){
            LOG.info("Searching for summary rejects");
        }
        
        List<ElectronicInvoiceRejectReason> retainList = new ArrayList<ElectronicInvoiceRejectReason>();
        List<ElectronicInvoiceRejectReason> rejectReasons = rejectDocument.getInvoiceRejectReasons();
        
        for (int i = 0; i < rejectReasons.size(); i++) {
            if (ArrayUtils.contains(summaryRejectCodes,rejectReasons.get(i).getInvoiceRejectReasonTypeCode())){
                retainList.add(rejectReasons.get(i));
                if (LOG.isInfoEnabled()){
                    LOG.info("Retaining Reject [Code=" + rejectReasons.get(i).getInvoiceRejectReasonTypeCode() + ",Desc=" + rejectReasons.get(i).getInvoiceRejectReasonDescription());
                }
            }
        }
        
        if (LOG.isInfoEnabled()){
            if (retainList.size() == 0){
                LOG.info("No summary rejects found");
            }
        }
        
        /**
         * FIXME: Use rejectDocument.getInvoiceRejectReasons().remove(index) instead of creating a new list
         */
        rejectDocument.getInvoiceRejectReasons().clear();
        
        for (int i = 0; i < retainList.size(); i++) {
            rejectDocument.addRejectReason(retainList.get(i));
        }
    }
    
    
    public ElectronicInvoiceOrderHolder(ElectronicInvoice eInvoice,
                                        ElectronicInvoiceOrder invoiceOrder,
                                        PurchaseOrderDocument poDocument,
                                        Map itemTypeMappings,
                                        Map itemTypes,
                                        boolean validateHeader){
        
        if (eInvoice == null){
            throw new NullPointerException("ElectronicInvoice should not be null");
        }
        
        if (invoiceOrder == null){
            throw new NullPointerException("ElectronicInvoiceOrder should not be null");
        }
        
        this.eInvoice = eInvoice;
        this.invoiceOrder = invoiceOrder;
        this.itemTypeMappings = itemTypeMappings;
        this.validateHeader = validateHeader;
        this.kualiItemTypes = itemTypes;
        
        this.poDocument = poDocument;
        
        isRejectDocumentHolder = false;
        
        for (int i = 0; i < invoiceOrder.getInvoiceItems().size(); i++) {

            ElectronicInvoiceItem orderItem = invoiceOrder.getInvoiceItems().get(i);
            
            PurApItem poItem = null;
            if (poDocument != null){
                try{
                    poItem = poDocument.getItemByLineNumber(orderItem.getReferenceLineNumberInteger());
                }catch(NullPointerException e){
                    /**
                     * Not needed to handle this invalid item here, this will be handled in the matching process 
                     */
                }
            }
            
            items.add(new ElectronicInvoiceItemHolder(orderItem,itemTypeMappings,poItem == null ? null : (PurchaseOrderItem)poItem,this));
        }
        
    }
    
    public String getFileName() {
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceFileName();
        }else{
            return eInvoice.getFileName();
        }
    }
    
    public String getDunsNumber() {
        if (isRejectDocumentHolder()){
            return rejectDocument.getVendorDunsNumber();
        }else{
            return eInvoice.getDunsNumber();
        }
    }
    
    public String getCustomerNumber() {
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceCustomerNumber();
        }else{
            return eInvoice.getCustomerNumber();
        }
    }
    
    public Integer getVendorHeaderId(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getVendorHeaderGeneratedIdentifier();
        }else{
            return eInvoice.getVendorHeaderID();
        }
    }
    
    public Integer getVendorDetailId(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getVendorDetailAssignedIdentifier();
        }else{
            return eInvoice.getVendorDetailID();
        }
    }
    
    public String getVendorName(){
        if (isRejectDocumentHolder()){
            if (rejectDocument.getVendorDetail() != null){
                return rejectDocument.getVendorDetail().getVendorName();
            }else{
               return StringUtils.EMPTY; 
            }
        }else{
            return eInvoice.getVendorName();
        }
    }
    
    public String getInvoiceNumber(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceFileNumber();
        }else{
            return eInvoice.getInvoiceDetailRequestHeader().getInvoiceId();
        }
    }
    
    public Date getInvoiceDate(){
        if (isRejectDocumentHolder()){
            return ElectronicInvoiceUtils.getDate(rejectDocument.getInvoiceFileDate());
        }else{
            return eInvoice.getInvoiceDetailRequestHeader().getInvoiceDate();
        }
    }
    
    public String getInvoiceDateString(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceFileDate();
        }else{
            return eInvoice.getInvoiceDetailRequestHeader().getInvoiceDateString();
        }
    }
    
    
    public boolean isInformationOnly(){
        if (isRejectDocumentHolder()){
            return rejectDocument.isInvoiceFileInformationOnlyIndicator();
        }else{
            return eInvoice.getInvoiceDetailRequestHeader().isInformationOnly();
        }
    }
    
    public String getInvoicePurchaseOrderID(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoicePurchaseOrderNumber();
        }else{
            return invoiceOrder.getOrderReferenceOrderID();
        }
    }
    
    public boolean isTaxInLine(){
        if (isRejectDocumentHolder()){
            return rejectDocument.isInvoiceFileTaxInLineIndicator();
        }else{
            return eInvoice.getInvoiceDetailRequestHeader().isTaxInLine();
        }
    }
    
    public BigDecimal getTaxAmount(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceItemTaxAmount();
        }else{
            return eInvoice.getInvoiceTaxAmount(invoiceOrder);
        }
    }
    
    public String getTaxDescription(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceItemTaxDescription();
        }else{
            return eInvoice.getInvoiceTaxDescription(invoiceOrder);
        }
    }
    public boolean isSpecialHandlingInLine(){
        if (isRejectDocumentHolder()){
            return rejectDocument.isInvoiceFileSpecialHandlingInLineIndicator();
        }else{
            return eInvoice.getInvoiceDetailRequestHeader().isSpecialHandlingInLine();
        }
    }
    
    public BigDecimal getInvoiceSpecialHandlingAmount(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceItemSpecialHandlingAmount();
        }else{
            return eInvoice.getInvoiceSpecialHandlingAmount(invoiceOrder);
        }
    }
    
    public String getInvoiceSpecialHandlingDescription(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceItemSpecialHandlingDescription();
        }else{
            return eInvoice.getInvoiceSpecialHandlingDescription(invoiceOrder);
        }
    }
    
    public boolean isShippingInLine(){
        if (isRejectDocumentHolder()){
            return rejectDocument.isInvoiceFileShippingInLineIndicator();
        }else{
            return eInvoice.getInvoiceDetailRequestHeader().isShippingInLine();
        }
    }
    
    public BigDecimal getInvoiceShippingAmount(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceItemShippingAmount();
        }else{
            return eInvoice.getInvoiceShippingAmount(invoiceOrder);
        }
    }
    
    public String getInvoiceShippingDescription(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceItemShippingDescription();
        }else{
            return eInvoice.getInvoiceShippingDescription(invoiceOrder);
        }
    }
    
    public boolean isDiscountInLine(){
        if (isRejectDocumentHolder()){
            return rejectDocument.isInvoiceFileDiscountInLineIndicator();
        }else{
            return eInvoice.getInvoiceDetailRequestHeader().isDiscountInLine();
        }
    }
    
    public BigDecimal getInvoiceDiscountAmount(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceItemDiscountAmount();
        }else{
            return eInvoice.getInvoiceDiscountAmount(invoiceOrder);
        }
    }
    
    public BigDecimal getInvoiceDepositAmount() {
        if (isRejectDocumentHolder()){
            throw new UnsupportedOperationException("Deposit amount not available for the reject document");
        }else{
            return eInvoice.getInvoiceDepositAmount();    
        }
    }

    public BigDecimal getInvoiceDueAmount() {
        if (isRejectDocumentHolder()){
            throw new UnsupportedOperationException("Deposit amount not available for the reject document");
        }else{
            return eInvoice.getInvoiceDueAmount();
        }
    }
      
    public PurchaseOrderDocument getPurchaseOrderDocument(){
        return poDocument;
    }
    
    public ElectronicInvoiceItemHolder[] getItems() {
        if (items != null){
            ElectronicInvoiceItemHolder[] returnItems = new ElectronicInvoiceItemHolder[items.size()];
            items.toArray(returnItems);
            return returnItems;
        }
        return null;
    }
    
    public ElectronicInvoiceItemHolder getItemByLineNumber(int lineNumber){
        
        if (items != null){
            for (int i = 0; i < items.size(); i++) {
                ElectronicInvoiceItemHolder itemHolder = items.get(i);
                if (itemHolder.getInvoiceItemLineNumber().intValue() == lineNumber){
                    return itemHolder;
                }
            }
        }
        return null;
    }
    
    public void addInvoiceHeaderRejectReason(ElectronicInvoiceRejectReason rejectReason){
        addInvoiceHeaderRejectReason(rejectReason,null,null);
    }
    
    public void addInvoiceHeaderRejectReason(ElectronicInvoiceRejectReason rejectReason,
                                             String fieldName,
                                             String applnResourceKey){
        
        if (LOG.isInfoEnabled()){
            LOG.info("Adding reject reason - " + rejectReason.getInvoiceRejectReasonDescription());
        }
        
        if (isRejectDocumentHolder()){
            rejectDocument.addRejectReason(rejectReason);
            if (fieldName != null && applnResourceKey != null){
                /**
                 * FIXME : Create a helper method to get the fieldname and key name in the resource bundle
                 * for a specific reject reason type code instead of getting it from the 
                 * calling method. Matching service should not do these things. It should 
                 * not know whether it's doing the matching process for a reject doc or for einvoice. It should
                 * be independent of the incoming data
                 * 
                 */
                GlobalVariables.getMessageMap().putError(fieldName, applnResourceKey);    
            }
        }else{
            eInvoice.addFileRejectReasonToList(rejectReason);
            eInvoice.setFileRejected(true);
        }
    }
    
    public void addInvoiceOrderRejectReason(ElectronicInvoiceRejectReason rejectReason,
                                            String fieldName){
        addInvoiceOrderRejectReason(rejectReason,fieldName,null);
    }

    public void addInvoiceOrderRejectReason(ElectronicInvoiceRejectReason rejectReason,
                                            String fieldName,
                                            String applnResourceKey){
        
        if (LOG.isInfoEnabled()){
            LOG.info("Adding reject reason - " + rejectReason.getInvoiceRejectReasonDescription());
        }
        
        if (isRejectDocumentHolder()){
            rejectDocument.addRejectReason(rejectReason);
            if (fieldName != null && applnResourceKey != null){
                /**
                 * FIXME : Create a helper method to get the fieldname and key name in the resource bundle
                 * for a specific reject reason type code instead of getting it from the 
                 * calling method. Matching service should not do these things. It should 
                 * not know whether it's doing the matching process for a reject doc or for einvoice. It should
                 * be independent of the incoming data
                 * 
                 * Also, needs to analyze the way of handling errors in specific line item
                 */
                GlobalVariables.getMessageMap().putError(fieldName, applnResourceKey);
            }
        }else{
            invoiceOrder.addRejectReasonToList(rejectReason);
            eInvoice.setFileRejected(true);
        }
    }
    
    public void addInvoiceOrderRejectReason(ElectronicInvoiceRejectReason rejectReason) {
        addInvoiceOrderRejectReason(rejectReason,null,null);
    }
    
    public boolean isValidateHeaderInformation(){
        return validateHeader;
    }
    
    public boolean isRejectDocumentHolder(){
        return isRejectDocumentHolder;
    }

    public ElectronicInvoiceItemMapping getInvoiceItemMapping(String invoiceItemTypeCode){
        if (itemTypeMappings == null){
            return null;
        }else{
            return itemTypeMappings.get(invoiceItemTypeCode);
        }
    }
    
    /*public boolean isItemTypeAvailableInKuali(String invoiceItemTypeCode) {
        if (itemTypeMappings == null) {
            return false;
        }
        else {
            return itemTypeMappings.containsKey(invoiceItemTypeCode);
        }
    }*/
    
    public boolean isItemTypeAvailableInItemMapping(String invoiceItemTypeCode) {
        if (itemTypeMappings == null) {
            return false;
        }
        else {
            return itemTypeMappings.containsKey(invoiceItemTypeCode);
        }
    }

    
    public boolean isInvoiceRejected() {
        if (isRejectDocumentHolder()) {
            return rejectDocument.getInvoiceRejectReasons() != null && rejectDocument.getInvoiceRejectReasons().size() > 0;
        }
        else {
            return eInvoice.isFileRejected();
        }
    }

    
    public String getKualiItemTypeCodeFromMappings(String invoiceItemTypeCode) {
        
        ElectronicInvoiceItemMapping itemMapping = getInvoiceItemMapping(invoiceItemTypeCode);
        
        if (itemMapping != null) {
            return itemMapping.getItemTypeCode();
        } else {
            return null;
        }
    }
    
   /* public String getKualiItemTypeCode(String invoiceItemTypeCode) {
        
        ItemType itemType = kualiItemTypes.get(invoiceItemTypeCode);
        
        if (itemType != null) {
            return itemType.getItemTypeCode();
        }
        else {
            return null;
        }
    }*/
    
   /* public boolean isKualiItemTypeExistsInVendorItemTypeMappings(String kualiItemType){
        ElectronicInvoiceItemMapping[] mappings = getInvoiceItemTypeMappings();
        if (mappings != null){
            for (int i = 0; i < mappings.length; i++) {
                if (StringUtils.equals(kualiItemType,mappings[i].getItemTypeCode())){
                    return true;
                }
            }
        }
        
        return false;
    }*/
    
    public ElectronicInvoiceItemMapping[] getInvoiceItemTypeMappings(){
        if (itemTypeMappings != null){
            ElectronicInvoiceItemMapping[] itemMappings = new ElectronicInvoiceItemMapping[itemTypeMappings.size()];
            itemTypeMappings.values().toArray(itemMappings);
            return itemMappings;
        }else{
            return null;
        }
    }
    
    public boolean isInvoiceNumberAcceptIndicatorEnabled(){
        if (isRejectDocumentHolder()) {
            return rejectDocument.isInvoiceNumberAcceptIndicator();
        }else {
            return false;
        }
    }
    
    public ElectronicInvoice getElectronicInvoice(){
        if (isRejectDocumentHolder()){
            throw new UnsupportedOperationException("ElectronicInvoice object not available for ElectronicInvoiceRejectDocument");
        }else{
            return eInvoice;
        }
    }
    
    public BigDecimal getInvoiceNetAmount(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getInvoiceItemNetAmount();
        }else{
            return eInvoice.getInvoiceNetAmount(invoiceOrder);
        }
    }
    
    public Date getInvoiceProcessedDate(){
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        if (isRejectDocumentHolder()){
            try {
                return dateTimeService.convertToSqlDate(rejectDocument.getInvoiceProcessTimestamp());
            }
            catch (ParseException e) {
                throw new RuntimeException("ParseException thrown when trying to convert a Timestamp to SqlDate.", e);
            }
        }else{
            return dateTimeService.getCurrentSqlDate();
        }
    }
    
    public String getInvoiceShipToAddressAsString() {

        StringBuffer noteBuffer = new StringBuffer();
        noteBuffer.append("Shipping Address from Electronic Invoice:\n\n");

        if (!isRejectDocumentHolder()) {

            ElectronicInvoicePostalAddress shipToAddress = eInvoice.getCxmlPostalAddress(invoiceOrder, PurapConstants.ElectronicInvoice.CXML_ADDRESS_SHIP_TO_ROLE_ID, PurapConstants.ElectronicInvoice.CXML_ADDRESS_SHIP_TO_NAME);
            if (shipToAddress != null) {

                if (StringUtils.isNotEmpty(shipToAddress.getName())) {
                    noteBuffer.append(shipToAddress.getName() + "\n");
                }

                noteBuffer.append(shipToAddress.getLine1() + "\n");

                if (StringUtils.isNotEmpty(shipToAddress.getLine2())) {
                    noteBuffer.append(shipToAddress.getLine2() + "\n");
                }

                if (StringUtils.isNotEmpty(shipToAddress.getLine3())) {
                    noteBuffer.append(shipToAddress.getLine3() + "\n");
                }

                noteBuffer.append(shipToAddress.getCityName() + ", " + shipToAddress.getStateCode() + " " + shipToAddress.getPostalCode() + "\n");
                noteBuffer.append(shipToAddress.getCountryName());
            }

        } else {

            if (StringUtils.isNotEmpty(rejectDocument.getInvoiceShipToAddressName())) {
                noteBuffer.append(rejectDocument.getInvoiceShipToAddressName() + "\n");
            }

            noteBuffer.append(rejectDocument.getInvoiceShipToAddressLine1() + "\n");

            if (StringUtils.isNotEmpty(rejectDocument.getInvoiceShipToAddressLine2())) {
                noteBuffer.append(rejectDocument.getInvoiceShipToAddressLine2() + "\n");
            }

            if (StringUtils.isNotEmpty(rejectDocument.getInvoiceShipToAddressLine3())) {
                noteBuffer.append(rejectDocument.getInvoiceShipToAddressLine3() + "\n");
            }

            noteBuffer.append(rejectDocument.getInvoiceShipToAddressCityName() + ", " + rejectDocument.getInvoiceShipToAddressStateCode() + " " + rejectDocument.getInvoiceShipToAddressPostalCode() + "\n");
            noteBuffer.append(rejectDocument.getInvoiceShipToAddressCountryName());

        }
        return noteBuffer.toString();
    }
    
    public String getInvoiceBillToAddressAsString(){
        
        StringBuffer noteBuffer = new StringBuffer();
        noteBuffer.append("Billing Address from Electronic Invoice:\n\n");
        
        if (!isRejectDocumentHolder()){
            
            ElectronicInvoicePostalAddress billToAddress = eInvoice.getCxmlPostalAddress(invoiceOrder,
                                                                                         PurapConstants.ElectronicInvoice.CXML_ADDRESS_BILL_TO_ROLE_ID,
                                                                                         PurapConstants.ElectronicInvoice.CXML_ADDRESS_BILL_TO_NAME);
            
            if (billToAddress != null) {

                if (StringUtils.isNotEmpty(billToAddress.getName())) {
                    noteBuffer.append(billToAddress.getName() + "\n");
                }

                noteBuffer.append(billToAddress.getLine1() + "\n");

                if (StringUtils.isNotEmpty(billToAddress.getLine2())) {
                    noteBuffer.append(billToAddress.getLine2() + "\n");
                }

                if (StringUtils.isNotEmpty(billToAddress.getLine3())) {
                    noteBuffer.append(billToAddress.getLine3() + "\n");
                }

                noteBuffer.append(billToAddress.getCityName() + ", " + billToAddress.getStateCode() + " " + billToAddress.getPostalCode() + "\n");
                noteBuffer.append(billToAddress.getCountryName());
            }   
        }else{
            
            if (StringUtils.isNotEmpty(rejectDocument.getInvoiceBillToAddressName())) {
                noteBuffer.append(rejectDocument.getInvoiceBillToAddressName() + "\n");
              }
              
              noteBuffer.append(rejectDocument.getInvoiceBillToAddressLine1() + "\n");
              
              if (StringUtils.isNotEmpty(rejectDocument.getInvoiceBillToAddressLine2())){
                noteBuffer.append(rejectDocument.getInvoiceBillToAddressLine2() + "\n");
              }
            
              if (StringUtils.isNotEmpty(rejectDocument.getInvoiceBillToAddressLine3())){
                  noteBuffer.append(rejectDocument.getInvoiceBillToAddressLine3() + "\n");
              }
        
              noteBuffer.append(rejectDocument.getInvoiceBillToAddressCityName() + ", " + rejectDocument.getInvoiceBillToAddressStateCode() + " " + rejectDocument.getInvoiceBillToAddressPostalCode() + "\n");
              noteBuffer.append(rejectDocument.getInvoiceBillToAddressCountryName());
        }
        
        return noteBuffer.toString();
    }
    
    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier(){
        if (isRejectDocumentHolder()){
            return rejectDocument.getAccountsPayablePurchasingDocumentLinkIdentifier();
        }else{
            if (poDocument != null){
                return poDocument.getAccountsPayablePurchasingDocumentLinkIdentifier();
            }else{
                return null;
            }
        }
    }
    
    protected class FieldErrorHelper {
        
        private String fieldName;
        private String applicationResourceKeyName;
        private String rejectReasonTypeCode;
        
        FieldErrorHelper(String fieldName,
                         String applicationResourceKeyName,
                         String rejectReasonTypeCode){
            
            if (StringUtils.isEmpty(fieldName) ||
                StringUtils.isEmpty(applicationResourceKeyName) ||
                StringUtils.isEmpty(rejectReasonTypeCode)){
                throw new NullPointerException("Invalid field Values [fieldName=" + fieldName + ",applicationResourceKeyName=" + applicationResourceKeyName + ",rejectReasonTypeCode=" + rejectReasonTypeCode + "]");
            }
            
            this.fieldName = fieldName;
            this.applicationResourceKeyName = applicationResourceKeyName;
            this.rejectReasonTypeCode = rejectReasonTypeCode;
        }
        
        public String getApplicationResourceKeyName() {
            return applicationResourceKeyName;
        }
        public void setApplicationResourceKeyName(String applicationResourceKeyName) {
            this.applicationResourceKeyName = applicationResourceKeyName;
        }
        public String getFieldName() {
            return fieldName;
        }
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
        public String getRejectReasonTypeCode() {
            return rejectReasonTypeCode;
        }
        public void setRejectReasonTypeCode(String rejectReasonTypeCode) {
            this.rejectReasonTypeCode = rejectReasonTypeCode;
        }
        
        public String toString(){
            ToStringBuilder toString = new ToStringBuilder(this);
            toString.append("fieldName",fieldName);
            toString.append("applicationResourceKeyName",applicationResourceKeyName);
            toString.append("rejectReasonTypeCode",rejectReasonTypeCode);
            return toString.toString();
        }
        
    }
    
    
}
