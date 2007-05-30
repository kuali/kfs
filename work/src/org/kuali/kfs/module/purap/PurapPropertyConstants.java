/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap;

/**
 * Property name constants.
 * 
 */
public class PurapPropertyConstants {
    
    // ITEM
    public static final String ITEM_IDENTIFIER = "itemIdentifier";
    public static final String ACCOUNTS = "sourceAccountingLines";
    
    public static final String DATA_OBJ_MAINT_CD_ACTIVE_IND = "dataObjectMaintenanceCodeActiveIndicator";
    
    // Purchase Order & Requisition
    public static final String PURAP_DOC_ID = "purapDocumentIdentifier";
    public static final String RECURRING_PAYMENT_TYPE_CODE = "recurringPaymentTypeCode";
    public static final String PURCHASE_ORDER_BEGIN_DATE = "purchaseOrderBeginDate";
    public static final String PURCHASE_ORDER_CURRENT_INDICATOR = "purchaseOrderCurrentIndicator";
    public static final String PURCHASE_ORDER_END_DATE = "purchaseOrderEndDate";
    public static final String VENDOR_POSTAL_CODE = "document.vendorPostalCode";
    public static final String VENDOR_COUNTRY_CODE = "document.vendorCountryCode";
    public static final String VENDOR_STATE_CODE = "document.vendorStateCode";
    public static final String PURCHASE_ORDER_TOTAL_LIMIT = "document.purchaseOrderTotalLimit";
    public static final String REQUISITION_VENDOR_FAX_NUMBER = "document.vendorFaxNumber";
    public static final String STATUS_CODE = "statusCode";
    public static final String DOCUMENT_NUMBER = "documentNumber";
    public static final String CONTRACT_MANAGER_CODE = "contractManagerCode";
    public static final String VENDOR_STIPULATION = "purchaseOrderVendorStipulations";
    public static final String VENDOR_STIPULATION_DESCRIPTION = "vendorStipulationDescription";
    public static final String VENDOR_CONTRACT_ID = "document.vendorContractGeneratedIdentifier";
    public static final String VENDOR_ADDRESS_ID = "document.vendorAddressGeneratedIdentifier";
    
    // Payment Request
    public static final String PURCHASE_ORDER_IDENTIFIER = "document.purchaseOrderIdentifier";
    public static final String INVOICE_DATE = "document.invoiceDate";
    
    // Quote Language
    public static final String PURCHASE_ORDER_QUOTE_LANGUAGE_ID = "purchaseOrderQuoteLanguageIdentifier";
}