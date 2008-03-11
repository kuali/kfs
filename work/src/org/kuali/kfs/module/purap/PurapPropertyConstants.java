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
 */
public class PurapPropertyConstants {

    public static final String VENDOR_HEADER_GENERATED_ID = "vendorHeaderGeneratedIdentifier";

    // ITEM
    public static final String ITEM_IDENTIFIER = "itemIdentifier";
    public static final String ACCOUNTS = "sourceAccountingLines";
    public static final String QUANTITY = "itemQuantity";
    public static final String EXTENDED_PRICE = "extendedPrice";
    public static final String ITEM_TYPE = "itemType";
    public static final String ITEM = "item";
    public static final String ITEM_CATALOG_NUMBER = "itemCatalogNumber";
    public static final String ITEM_DESCRIPTION = "itemDescription";
    public static final String ITEM_QUANTITY = "itemQuantity";
    public static final String ITEM_UNIT_OF_MEASURE_CODE = "itemUnitOfMeasureCode";
    public static final String ITEM_UNIT_OF_MEASURE_DESCRIPTION = "itemUnitOfMeasureDescription";
    public static final String ITEM_UNIT_PRICE = "itemUnitPrice";
    public static final String ITEM_COMMODITY_CODE = "purchasingCommodityCode";
    public static final String NEW_PURCHASING_ITEM_LINE = "newPurchasingItemLine";
    public static final String ITEM_CAPITAL_ASSET_TRANSACTION_TYPE = "capitalAssetTransactionType";
    

    // accounting line
    public static final String ACCOUNT_IDENTIFIER = "accountIdentifier";
    public static final String ACCOUNT_LINE_PERCENT = "accountLinePercent";

    public static final String DATA_OBJ_MAINT_CD_ACTIVE_IND = "dataObjectMaintenanceCodeActiveIndicator";

    // document fields
    public static final String GENERAL_LEDGER_PENDING_ENTRIES = "generalLedgerPendingEntries";

    // PURCHASING AND ACCOUNTS PAYABLE DOCUMENT BASE
    public static final String PURAP_DOC_ID = "purapDocumentIdentifier";
    public static final String OLD_STATUS = "oldStatus";
    public static final String NEW_STATUS = "newStatus";
    public static final String STATUS_CODE = "statusCode";
    public static final String STATUS = "status";
    public static final String VENDOR_NUMBER = "vendorNumber";
    public static final String VENDOR_POSTAL_CODE = "vendorPostalCode";
    public static final String VENDOR_COUNTRY_CODE = "vendorCountryCode";
    public static final String VENDOR_STATE_CODE = "vendorStateCode";
    public static final String VENDOR_ADDRESS_ID = "vendorAddressGeneratedIdentifier";

    // PURCHASING DOCUMENT BASE
    public static final String RECURRING_PAYMENT_TYPE_CODE = "recurringPaymentTypeCode";
    public static final String PURCHASE_ORDER_BEGIN_DATE = "purchaseOrderBeginDate";
    public static final String PURCHASE_ORDER_END_DATE = "purchaseOrderEndDate";
    public static final String PURCHASE_ORDER_TOTAL_LIMIT = "purchaseOrderTotalLimit";
    public static final String VENDOR_FAX_NUMBER = "vendorFaxNumber";
    public static final String CONTRACT_MANAGER_CODE = "contractManagerCode";
    public static final String VENDOR_CONTRACT_ID = "vendorContractGeneratedIdentifier";
    public static final String PURCHASE_ORDER = "purchaseOrder";
    public static final String PURCHASE_ORDER_COST_SOURCE = "purchaseOrderCostSource";
    public static final String DELIVERY_REQUIRED_DATE = "deliveryRequiredDate";
    
    // AP DOCUMENT BASE
    public static final String PURCHASE_ORDER_IDENTIFIER = "purchaseOrderIdentifier";

    // REQUISITION DOCUMENT

    // PURCHASE ORDER DOCUMENT
    public static final String PURCHASE_ORDER_CURRENT_INDICATOR = "purchaseOrderCurrentIndicator";
    public static final String VENDOR_STIPULATION = "purchaseOrderVendorStipulations";
    public static final String QUOTE_TRANSMITTED = "purchaseOrderQuoteTransmitted";
    public static final String VENDOR_QUOTES = "purchaseOrderVendorQuotes";
    
    // Quote Language
    public static final String PURCHASE_ORDER_QUOTE_LANGUAGE_ID = "purchaseOrderQuoteLanguageIdentifier";
    // vendor quote
    public static final String PURCHASE_ORDER_VENDOR_QUOTE_IDENTIFIER = "purchaseOrderVendorQuoteIdentifier";
    // Stipulations
    public static final String VENDOR_STIPULATION_DESCRIPTION = "vendorStipulationDescription";

    // PAYMENT REQUEST DOCUMENT
    public static final String INVOICE_DATE = "invoiceDate";
    public static final String INVOICE_NUMBER = "invoiceNumber";
    public static final String VENDOR_INVOICE_AMOUNT = "vendorInvoiceAmount";
    public static final String PAYMENT_REQUEST_PAY_DATE = "paymentRequestPayDate";
    public static final String GRAND_TOTAL = "grandTotal";
    public static final String PAYMENT_REQUEST = "paymentRequest";
    public static final String RECURRING_PAYMENT_TYPE = "recurringPaymentType";

    // CREDIT MEMO DOCUMENT
    public static final String PAYMENT_REQUEST_ID = "paymentRequestIdentifier";
    public static final String CREDIT_MEMO_DATE = "creditMemoDate";
    public static final String CREDIT_MEMO_NUMBER = "creditMemoNumber";
    public static final String CREDIT_MEMO_AMOUNT = "creditMemoAmount";

    // Restricted Material
    public static final String WORKGROUP_NAME = "restrictedMaterialWorkgroupName";
}