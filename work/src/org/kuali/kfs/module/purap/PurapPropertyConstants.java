/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.purap;


/**
 * Property name constants.
 */
public class PurapPropertyConstants {

    public static final String VENDOR_HEADER_GENERATED_ID = "vendorHeaderGeneratedIdentifier";
    public static final String VENDOR_DETAIL_ASSIGNED_ID = "vendorDetailAssignedIdentifier";
    public static final String DOCUMENT_NUMBER = "documentNumber";
    // ITEM
    public static final String ITEM_IDENTIFIER = "itemIdentifier";
    public static final String ACCOUNTS = "sourceAccountingLines";
    public static final String QUANTITY = "itemQuantity";
    public static final String EXTENDED_PRICE = "extendedPrice";
    public static final String ITEM_TYPE = "itemType";
    public static final String ITEM = "item";
    public static final String ITEMS = "items";
    public static final String ITEM_CATALOG_NUMBER = "itemCatalogNumber";
    public static final String ITEM_DESCRIPTION = "itemDescription";
    public static final String ITEM_QUANTITY = "itemQuantity";
    public static final String ITEM_UNIT_PRICE = "itemUnitPrice";
    public static final String ITEM_COMMODITY_CODE = "purchasingCommodityCode";
    public static final String NEW_PURCHASING_ITEM_LINE = "newPurchasingItemLine";
    public static final String NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE = "newPurchasingCapitalAssetLocationLine";
    public static final String NEW_LINE_ITEM_RECEIVING_ITEM_LINE = "newLineItemReceivingItemLine";
    public static final String ITEM_CAPITAL_ASSET_TRANSACTION_TYPE = "capitalAssetTransactionType";
    public static final String ITEM_CAPITAL_ASSET_TRANSACTION_TYPE_CODE = "capitalAssetTransactionTypeCode";
    public static final String CAPITAL_ASSET_ITEM_IDENTIFIER = "capitalAssetItemIdentifier";
    public static final String CAPITAL_ASSET_SYSTEM_IDENTIFIER = "capitalAssetSystemIdentifier";
    public static final String ITEM_REASON_ADDED = "itemReasonAdded";
    public static final String ITEM_TAX_AMOUNT = "itemTaxAmount";
    public static final String ITEM_SALES_TAX_AMOUNT = "itemSalesTaxAmount";
    public static final String TOTAL_AMOUNT = "totalAmount";
    
    // ITEM TYPE
    public static final String ITEM_TYPE_QUANTITY_BASED = "quantityBasedGeneralLedgerIndicator";
    
    // accounting line
    public static final String ACCOUNT_DISTRIBUTION_NEW_SRC_LINE = "accountDistributionnewSourceLine";
    public static final String ACCOUNT_IDENTIFIER = "accountIdentifier";
    public static final String ACCOUNT_LINE_PERCENT = "accountLinePercent";
    public static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
    public static final String SUB_FUND_GROUP_CODE = "subFundGroupCode";
    public static final String ACCOUNT_TYPE_CODE = "accountTypeCode";
    public static final String ORGANIZATION_CODE = "organizationCode";
    public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";    
    public static final String ACCOUNT_NUMBER = "accountNumber";
    public static final String DATA_OBJ_MAINT_CD_ACTIVE_IND = "dataObjectMaintenanceCodeActiveIndicator";

    // document fields
    public static final String GENERAL_LEDGER_PENDING_ENTRIES = "generalLedgerPendingEntries";

    // PURCHASING AND ACCOUNTS PAYABLE DOCUMENT BASE
    public static final String PURAP_DOC = "purapDocument";
    public static final String PURAP_DOC_ID = "purapDocumentIdentifier";
    public static final String PURAP_LINK_ID = "accountsPayablePurchasingDocumentLinkIdentifier";
    public static final String PURAP_ITEM = "purapItem";
    public static final String OLD_STATUS = "oldStatus";
    public static final String NEW_STATUS = "newStatus";
    public static final String STATUS_CODE = "statusCode";
    public static final String STATUS = "status";
    public static final String VENDOR_NUMBER = "vendorNumber";
    public static final String VENDOR_POSTAL_CODE = "vendorPostalCode";
    public static final String VENDOR_COUNTRY_CODE = "vendorCountryCode";
    public static final String VENDOR_STATE_CODE = "vendorStateCode";
    public static final String VENDOR_ADDRESS_ID = "vendorAddressGeneratedIdentifier";
    public static final String DELIVERY_CAMPUS_CODE = "deliveryCampusCode";
    public static final String DELIVERY_POSTAL_CODE = "deliveryPostalCode";
    public static final String DELIVERY_STATE_CODE = "deliveryStateCode";
    public static final String REQUESTOR_PERSON_NAME = "requestorPersonName";
    public static final String DELIVERY_TO_NAME = "deliveryToName";
    public static final String BANK_CODE = "bankCode";
    
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
    public static final String COMMODITY_CODE = "commodityCode";
    public static final String RECEIVING_DOCUMENT_REQUIRED_ID = "receivingDocumentRequiredIndicator";
    public static final String CAPITAL_ASSET_SYSTEM_TYPE = "capitalAssetManagementSystemType";
    public static final String CAPITAL_ASSET_TRANSACTION_TYPE = "capitalAssetTransactionType";
    public static final String CAPITAL_ASSET_TRANSACTION_TYPE_CODE = "capitalAssetTransactionTypeCode";
    public static final String PURCHASING_CAPITAL_ASSET_ITEMS = "purchasingCapitalAssetItems";
    public static final String PURCHASING_CAPITAL_ASSET_LOCATION = "purchasingCapitalAssetItems";
    public static final String PURCHASING_CAPITAL_ASSET_SYSTEMS = "purchasingCapitalAssetSystems";
    public static final String PURCHASING_CAPITAL_ASSET_SYSTEM = "purchasingCapitalAssetSystem";
    public static final String CAPITAL_ASSET_LOCATIONS = "capitalAssetLocations";
    public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
    public static final String VENDOR_CONTRACT = "vendorContract";
    public static final String ITEM_UNIT_OF_MEASURE = "itemUnitOfMeasure";
        
    // Capital Asset Locations
    public static final String CAPITAL_ASSET_LOCATION_ADDRESS_LINE1 = "capitalAssetLine1Address";
    public static final String CAPITAL_ASSET_LOCATION_CITY = "capitalAssetCityName";
    public static final String CAPITAL_ASSET_LOCATION_STATE = "capitalAssetStateCode";
    public static final String CAPITAL_ASSET_LOCATION_POSTAL_CODE = "capitalAssetPostalCode";
    public static final String CAPITAL_ASSET_LOCATION_COUNTRY = "capitalAssetCountryCode";
    public static final String CAPITAL_ASSET_LOCATION_CAMPUS = "campusCode";
    public static final String CAPITAL_ASSET_LOCATION_BUILDING = "buildingCode";
    public static final String CAPITAL_ASSET_LOCATION_ROOM = "buildingRoomNumber";
    
    // Purchasing Capital Asset System Base
    public static final String CAPITAL_ASSET_TYPE_CODE = "capitalAssetTypeCode";
    
    // Receiving Address
    public static final String RECEIVING_ADDRESS_DEFAULT_INDICATOR = "defaultIndicator";
    public static final String RECEIVING_ADDRESS_STATE = "receivingStateCode";
    public static final String RECEIVING_ADDRESS_POSTAL_CODE = "receivingPostalCode";

    // Building Address
    public static final String BILLING_ADDRESS_STATE = "billingStateCode";
    public static final String BILLING_ADDRESS_POSTAL_CODE = "billingPostalCode";

    // Business Object
    public static final String BO_ACTIVE = "active";

    // AP DOCUMENT BASE
    public static final String PURCHASE_ORDER_IDENTIFIER = "purchaseOrderIdentifier";

    // REQUISITION DOCUMENT
    public static final String REQUESTOR_PERSON_PHONE_NUMBER = "requestorPersonPhoneNumber";
    public static final String REQUESTOR_PERSON_EMAIL_ADDRESS = "requestorPersonEmailAddress";

    // PURCHASE ORDER DOCUMENT
    public static final String PURCHASE_ORDER_CURRENT_INDICATOR = "purchaseOrderCurrentIndicator";
    public static final String VENDOR_STIPULATION = "purchaseOrderVendorStipulations";
    public static final String QUOTE_TRANSMITTED = "purchaseOrderQuoteTransmitted";
    public static final String VENDOR_QUOTES = "purchaseOrderVendorQuotes";
    public static final String ALTERNATE_VENDOR_NAME = "alternateVendorName";
    public static final String PURCHASE_ORDER_STATUS_CODE = "purchaseOrderStatusCode";
    public static final String TOTAL_ENCUMBRANCE = "totalEncumbrance";
    public static final String VENDOR_CHOICE_CODE = "vendorChoiceCode";
    public static final String CONTRACT_MANAGER = "contractManager";
    public static final String  ASSIGNED_USER_PRINCIPAL_NAME = "assignedUserPrincipalName";

    // Quote Language
    public static final String PURCHASE_ORDER_QUOTE_LANGUAGE_ID = "purchaseOrderQuoteLanguageIdentifier";

    // vendor quote
    public static final String PURCHASE_ORDER_VENDOR_QUOTE_IDENTIFIER = "purchaseOrderVendorQuoteIdentifier";
    public static final String PURCHASE_ORDER_QUOTE_STATUS ="purchaseOrderQuoteStatus";
    public static final String PURCHASE_ORDER_VENDOR_QUOTES = "purchaseOrderVendorQuotes";
    public static final String PURCHASE_ORDER_QUOTE_INITIALIZATION_DATE = "purchaseOrderQuoteInitializationDate";
    public static final String PURCHASE_ORDER_QUOTE_AWARDED_DATE = "purchaseOrderQuoteAwardedDate";
    public static final String PURCHASE_ORDER_QUOTE_DUE_DATE = "purchaseOrderQuoteDueDate";
    public static final String PURCHASE_ORDER_QUOTE_TYPE_CODE = "purchaseOrderQuoteTypeCode";
    public static final String PURCHASE_ORDER_QUOTE_VENDOR_NOTE_TEXT = "purchaseOrderQuoteVendorNoteText";
    public static final String NEW_PURCHASE_ORDER_VENDOR_QUOTE_TEXT = "newPurchaseOrderVendorQuote";
    public static final String NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_NAME = "newPurchaseOrderVendorQuote.vendorName";
    public static final String NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_LINE_1_ADDR = "newPurchaseOrderVendorQuote.vendorLine1Address";
    public static final String NEW_PURCHASE_ORDER_VENDOR_QUOTE_VENDOR_CITY_NAME = "newPurchaseOrderVendorQuote.vendorCityName";    

    // Stipulations
    public static final String VENDOR_STIPULATION_DESCRIPTION = "vendorStipulationDescription";

    // Sensitive Data
    public static final String SENSITIVE_DATA_CODE = "sensitiveDataCode";
    
    // PAYMENT REQUEST DOCUMENT
    public static final String INVOICE_DATE = "invoiceDate";
    public static final String INVOICE_NUMBER = "invoiceNumber";
    public static final String VENDOR_INVOICE_AMOUNT = "vendorInvoiceAmount";
    public static final String PAYMENT_REQUEST_PAY_DATE = "paymentRequestPayDate";
    public static final String GRAND_TOTAL = "grandTotal";
    public static final String PAYMENT_REQUEST = "paymentRequest";
    public static final String RECURRING_PAYMENT_TYPE = "recurringPaymentType";

    // tax tab fields
    public static final String TAX_CLASSIFICATION_CODE = "taxClassificationCode";
    public static final String TAX_COUNTRY_CODE = "taxCountryCode";
    public static final String TAX_NQI_ID = "taxNQIId";
    public static final String TAX_FEDERAL_PERCENT = "taxFederalPercent";
    public static final String TAX_STATE_PERCENT = "taxStatePercent";
    public static final String TAX_SPECIAL_W4_AMOUNT = "taxSpecialW4Amount";
    public static final String TAX_GROSS_UP_INDICATOR = "taxGrossUpIndicator";
    public static final String TAX_EXEMPT_TREATY_INDICATOR = "taxExemptTreatyIndicator";
    public static final String TAX_FOREIGN_SOURCE_INDICATOR = "taxForeignSourceIndicator";
    public static final String TAX_USAID_PER_DIEM_INDICATOR = "taxUSAIDPerDiemIndicator";
    public static final String TAX_OTHER_EXEMPT_INDICATOR = "taxOtherExemptIndicator";

    // CREDIT MEMO DOCUMENT
    public static final String PAYMENT_REQUEST_ID = "paymentRequestIdentifier";
    public static final String CREDIT_MEMO_ID = "creditMemoIdentifier";
    public static final String CREDIT_MEMO_DATE = "creditMemoDate";
    public static final String CREDIT_MEMO_NUMBER = "creditMemoNumber";
    public static final String CREDIT_MEMO_AMOUNT = "creditMemoAmount";

    // RECEIVING LINE DOCUMENT
    public static final String LINE_ITEM_RECEIVING_DOCUMENT_NUMBER = "lineItemReceivingDocumentNumber";
    public static final String SHIPMENT_RECEIVED_DATE = "shipmentReceivedDate";
    public static final String SHIPMENT_PACKING_SLIP_NUMBER = "shipmentPackingSlipNumber";
    public static final String SHIPMENT_BILL_OF_LADING_NUMBER = "shipmentBillOfLadingNumber";
    public static final String LINE_ITEM_RECEIVING_STATUS = "lineItemReceivingStatus";
    
    //ASSIGN CONTRACT MANAGER DOCUMENT
    public static final String REQUISITION_IDENTIFIER = "requisitionIdentifier";
    public static final String VENDOR_NAME = "vendorName";
    
    // ELECTRONIC INVOICE GENERATION
    public static final String PURCHASE_ORDER_DOCUMENT_NUMBER = "poDocNumber";
    
    //BULK RECEIVING DOCUMENT
    public static final String VENDOR_DATE = "vendorName";
    
    public static final String B2B_PUNCH_BACK_ACTION_FORWARDING_URL = "b2b.punch.back.action.forwarding.url";
    
    public static final String ITEM_UNIT_OF_MEASURE_CODE = "itemUnitOfMeasureCode";
    
}
