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
 * Holds error key constants for PURAP.
 */
public class PurapKeyConstants {

    public static final String PURAP_GENERAL_POTENTIAL_DUPLICATE = "error.document.purap.potentialDuplicate";
    public static final String PURAP_GENERAL_NO_ACCOUNTS_TO_DISTRIBUTE = "error.document.purap.noAccountsToDistribute";
    public static final String PURAP_GENERAL_NO_ITEMS_TO_DISTRIBUTE_TO = "error.document.purap.noItemsToDistributeTo";
    public static final String PURAP_GENERAL_ACCOUNTS_DISTRIBUTED = "message.document.purap.accountsDistributed";
    public static final String PURAP_GENERAL_ACCOUNTS_REMOVED = "message.document.purap.accountsRemoved";
    public static final String ERROR_INVALID_CHART_OF_ACCOUNTS_CODE = "error.document.purap.invalidChartOfAccountsCode";
    public static final String ERROR_INVALID_ORGANIZATION_CODE = "error.document.purap.invalidOrganizationCode";
    public static final String ERROR_INVALID_COA_ORG_CODE = "error.document.purap.invalidCoaOrgCode";
    public static final String ERROR_CANNOT_INACTIVATE_USED_IN_SYSTEM_PARAMETERS = "error.document.purap.cannot.inactivate.used.in.system.parameters";
    public static final String ERROR_CANNOT_INACTIVATE_USED_BY_ACTIVE_RECORDS = "error.document.purap.cannot.inactivate.used.by.active.records";
    
    // Purchase Order & Requisition
    public static final String WARNING_REQUESTOR_NAME_TRUNCATED = "warning.requestor.name.truncated";
    public static final String WARNING_DELIVERY_TO_NAME_TRUNCATED = "warning.delivery.to.name.truncated";
    public static final String DEFAULT_BUILDING_SAVED = "message.default.building.saved";
    public static final String ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END = "error.purchaseOrder.beginDateAfterEnd";
    public static final String ERROR_PURCHASE_ORDER_BEGIN_DATE_NO_END_DATE = "error.purchaseOrder.beginDateNoEndDate";
    public static final String ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE = "error.purchaseOrder.endDateNoBeginDate";
    public static final String ERROR_RECURRING_DATE_NO_TYPE = "errors.recurring.type";
    public static final String ERROR_RECURRING_TYPE_NO_DATE = "errors.recurring.dates";
    public static final String ERROR_POSTAL_CODE_INVALID = "errors.postalCode.invalid";
    public static final String ERROR_FAX_NUMBER_INVALID = "errors.faxNumber.invalid";
    public static final String ERROR_INVALID_PH0NE_NUMBER = "errors.invalid.requestorPhoneNumber";
    public static final String ERROR_INVALID_EMAIL_ADDRESS = "errors.invalid.requestorEmailAddress";
    public static final String ERROR_FAX_NUMBER_PO_TRANSMISSION_TYPE = "error.faxNumber.PoTransmissionType";
    public static final String ERROR_INVALID_VENDOR_TYPE = "error.vendorType.invalid";
    public static final String ERROR_DEBARRED_VENDOR = "error.debarred.vendor";
    public static final String WARNING_DEBARRED_VENDOR = "warning.debarred.vendor";
    public static final String ERROR_INACTIVE_VENDOR = "error.inactive.vendor";
    public static final String ERROR_NONEXIST_VENDOR = "error.nonexist.vendor";
    public static final String ERROR_NONEXIST_ASSIGNED_USER = "error.nonexist.assignedUser";
    public static final String PO_TOTAL_GREATER_THAN_PO_TOTAL_LIMIT = "warning.purchaseOrderTotalLimit";
    public static final String INVALID_CONTRACT_MANAGER_CODE = "error.invalidContractManagerCode";
    public static final String ERROR_APO_CONTRACT_MANAGER_CODE_CHOSEN = "error.apoContractManagerCodeChosen";
    public static final String NO_CONTRACT_MANAGER_ASSIGNED = "error.noContractManagerAssigned";
    public static final String ERROR_REQ_COPY_EXPIRED_CONTRACT = "error.requisition.copy.expired.contract";
    public static final String ERROR_EXPIRED_CONTRACT_END_DATE = "error.vendor.contract.expired.endDate";
    public static final String ERROR_REQ_COPY_INACTIVE_VENDOR = "error.requisition.copy.inactive.vendor";
    public static final String ERROR_STIPULATION_DESCRIPTION = "error.purchaseOrder.stipulationDescriptionEmpty";
    public static final String ERROR_NO_ITEMS = "error.requisition.no.items";
    public static final String ERROR_DISTRIBUTE_ACCOUNTS_NOT_100_PERCENT = "error.distribute.accounts.not.100.percent";
    public static final String ERROR_REQUISITION_ACCOUNT_CLOSED = "error.document.requisition.accountClosed";
    public static final String ERROR_AUTHORIZATION_ACM_INITIATION = "error.authorization.contractManagerAssignmentInitiation";
    public static final String REQ_QUESTION_FIX_CAPITAL_ASSET_WARNINGS = "requisition.question.fix.capitalAsset.warnings";
    public static final String PUR_COMMODITY_CODES_CLEARED = "message.document.pur.commodityCodesCleared";
    public static final String PUR_COMMODITY_CODE_DISTRIBUTED = "message.document.pur.commodityCodeDistributed";
    public static final String PUR_COMMODITY_CODE_INVALID = "error.commodity.code.invalid";
    public static final String PUR_ITEM_UNIT_OF_MEASURE_CODE_INVALID = "error.item.unitOfMeasureCode.invalid";
    public static final String PUR_COMMODITY_CODE_INACTIVE= "error.commodity.code.inactive";
    public static final String ERROR_RCVNG_ADDR_UNSET_DFLT = "error.rcvng.addr.unset.dflt";
    public static final String ERROR_RCVNG_ADDR_DEACTIVATE_DFLT = "error.rcvng.addr.deactivate.dflt";
    public static final String PUR_CAPITAL_ASSET_SYSTEM_TYPE_SWITCHED = "message.document.pur.systemTypeSwitched";
    public static final String ERROR_DELIVERY_CAMPUS_INVALID = "error.delivery.campus.invalid";
    public static final String PURCHASING_QUESTION_CONFIRM_CHANGE_SYSTEM = "purchasing.question.change.system";
    public static final String PURCHASING_MESSAGE_SYSTEM_CHANGED = "purchasing.message.system.changed";
    
    public static final String ERROR_CAPITAL_ASSET_ASSET_NUMBERS_NOT_ALLOWED_TRANS_TYPE = "error.capitalAsset.asset.numbers.not.allowed.trans.type";
    public static final String ERROR_CAPITAL_ASSET_LOCATIONS_QUANTITY_MUST_EQUAL_ITEM_QUANTITY = "error.capitalAsset.locations.quantity.must.equal.item.quantity";
    public static final String ERROR_CAPITAL_ASSET_TRANS_TYPE_NOT_ALLOWING_NON_QUANTITY_ITEMS = "error.capitalAsset.trans.type.not.allowing.non.quantity.items";
    public static final String ERROR_CAPITAL_ASSET_ASSET_NUMBER_MUST_BE_LONG_NOT_NULL="errors.item.capitalAsset.assetNumber.must.be.longValue.notNull";
    public static final String ERROR_CAPITAL_ASSET_ITEM_NOT_CAMS_ELIGIBLE="error.capitalAsset.item.not.cams.eligible";
    public static final String ERROR_CAPITAL_ASSET_TRANSACTION_TYPE_MUST_BE_ASSET_GIVEN_TRADE_IN = "error.capitalAsset.trans.type.must.be.asset.given.tradein";
    public static final String ERROR_CAPITAL_ASSET_NO_VENDOR = "error.capitalAsset.no.vendor";
    public static final String ERROR_CAPITAL_ASSET_INCOMPLETE_ADDRESS = "error.capitalAsset.incomplete.address";
    public static final String ERROR_CAPITAL_ASSET_REQD_FOR_PUR_OBJ_SUB_TYPE = "error.capitalAsset.required.for.purchase.order.sub.type";
    
    // Requisition APO Ineligibility reasons
    public static final String NON_APO_REQUISITION_TOTAL_GREATER_THAN_APO_LIMIT = "requisition.nonAPO.requisitionTotal.greaterThan.APOLimit";
    public static final String NON_APO_REQUISITION_TOTAL_NOT_GREATER_THAN_ZERO="requisition.nonAPO.requisitionTotal.notGreaterThan.zero";
    public static final String NON_APO_REQUISITION_CONTAINS_RESTRICTED_ITEM="requisition.nonAPO.requisition.contains.restricted.item";
    public static final String NON_APO_VENDOR_NOT_SELECTED_FROM_VENDOR_DATABASE="requisition.nonAPO.vendor.not.selected.from.vendor.database";
    public static final String NON_APO_ERROR_RETRIEVING_VENDOR_FROM_DATABASE="requisition.nonAPO.error.retrieving.vendor.from.database";
    public static final String NON_APO_SELECTED_VENDOR_IS_RESTRICTED="requisition.nonAPO.selected.vendor.is.restricted";
    public static final String NON_APO_PAYMENT_TYPE_IS_RECURRING="requisition.nonAPO.paymentType.is.recurring";
    public static final String NON_APO_PO_TOTAL_LIMIT_IS_NOT_EMPTY="requisition.nonAPO.po.total.limit.is.not.empty";
    public static final String NON_APO_REQUISITION_CONTAINS_ALTERNATE_VENDOR_NAMES="requisition.nonAPO.requisition.contains.alternate.vendor.names";
    public static final String NON_APO_REQUISITION_CONTAINS_INACTIVE_COMMODITY_CODE="requisition.nonAPO.requisition.contains.inactive.commodity.code";
    public static final String NON_APO_REQUISITION_MISSING_COMMODITY_CODE="requisition.nonAPO.requisition.missing.commodity.code";
    public static final String NON_APO_REQUISITION_CONTAINS_RESTRICTED_COMMODITY_CODE="requisition.nonAPO.requisition.contains.restricted.commodity.code";
    public static final String NON_APO_REQUISITION_MISSING_SOME_VENDOR_ADDRESS_FIELDS="requisition.nonAPO.missing.some.vendor.address.fields";
    public static final String NON_APO_REQUISITION_FAILS_CAPITAL_ASSET_RULES="requisition.nonAPO.capital.asset.rules";
    public static final String NON_APO_REQUISITION_ACCT_LINE_CAPITAL_OBJ_LEVEL = "requisition.nonAPO.accounting.line.capital.object.level";
    public static final String NON_APO_REQUISITION_OUTSIDE_NEXT_FY_APPROVAL_RANGE="requisition.nonAPO.outside.nextFY.approval.range";
    // Purchase Order
    public static final String PURCHASE_ORDER_QUESTION_DOCUMENT = "purchaseOrder.question.text";
    public static final String PURCHASE_ORDER_SPLIT_QUESTION_TEXT = "purchaseOrder.split.question.text";
    public static final String PURCHASE_ORDER_MESSAGE_CLOSE_DOCUMENT = "purchaseOrder.route.message.close.text";
    public static final String ERROR_PURCHASE_ORDER_REASON_REQUIRED = "error.purchaseOrder.reasonRequired";
    public static final String ERROR_PURCHASE_ORDER_STATUS_INCORRECT = "error.purchaseOrder.status.incorrect";
    public static final String ERROR_PURCHASE_ORDER_STATUS_NOT_REQUIRED_STATUS = "error.close.purchaseOrder.status.not.required.status";
    public static final String ERROR_PURCHASE_ORDER_CLOSE_NO_PREQ = "error.close.purchaseOrder.no.paymentRequest";
    public static final String ERROR_PURCHASE_ORDER_CLOSE_PREQ_IN_PROCESS = "error.close.purchaseOrder.paymentRequest.inProcess";
    public static final String PURCHASE_ORDER_MESSAGE_VOID_DOCUMENT = "purchaseOrder.route.message.void.text";
    public static final String PURCHASE_ORDER_MESSAGE_PAYMENT_HOLD = "purchaseOrder.route.message.payment.hold.text";
    public static final String PURCHASE_ORDER_MESSAGE_REMOVE_HOLD = "purchaseOrder.route.message.remove.hold.text";
    public static final String PURCHASE_ORDER_MESSAGE_REOPEN_DOCUMENT = "purchaseOrder.route.message.reopen.text";
    public static final String PURCHASE_ORDER_MESSAGE_SPLIT_DOCUMENT = "purchaseOrder.route.message.split.text";
    public static final String PURCHASE_ORDER_AMEND_MESSAGE_CHANGE_SYSTEM_TYPE = "purchaseOrder.amend.message.change.systemType";
    public static final String PURCHASE_ORDER_QUESTION_MANUAL_STATUS_CHANGE = "purchaseOrder.question.manual.status.change";
    public static final String PURCHASE_ORDER_MANUAL_STATUS_CHANGE_NOTE_PREFIX = "purchaseOrder.manual.status.change.note.prefix";
    public static final String ERROR_PURCHASE_ORDER_PDF = "error.purchaseOrder.pdf";
    public static final String WARNING_PURCHASE_ORDER_NOT_CURRENT = "warning.purchaseOrder.notCurrent";
    public static final String WARNING_PURCHASE_ORDER_PENDING_ACTION_NOT_CURRENT = "warning.purchaseOrder.pendingAction.notCurrent";
    public static final String WARNING_PURCHASE_ORDER_PENDING_ACTION = "warning.purchaseOrder.pendingAction";
    public static final String WARNING_PURCHASE_ORDER_ALL_NOTES = "warning.purchaseOrder.allNotes";
    public static final String PURCHASE_ORDER_QUESTION_CONFIRM_AWARD = "purchaseOrder.route.message.confirm.award.text";
    public static final String PURCHASE_ORDER_QUESTION_CONFIRM_AWARD_ROW = "purchaseOrder.route.message.confirm.award.row.text";
    public static final String PURCHASE_ORDER_QUESTION_CONFIRM_CANCEL_QUOTE = "purchaseOrder.route.message.confirm.cancel.quote.text";
    public static final String PURCHASE_ORDER_QUESTION_OVERRIDE_NOT_TO_EXCEED = "purchaseOrder.question.notToExceed.override";
    public static final String PURCHASE_ORDER_CANCEL_QUOTE_NOTE_TEXT = "purchaseOrder.route.message.cancel.note.text";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_ALREADY_TRASNMITTED = "error.transmit.purchaseOrder.already.transmitted";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_NO_VENDOR_AWARDED = "error.transmit.purchaseOrder.noVendorAwarded";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_NOT_TRANSMITTED = "error.transmit.purchaseOrder.notTransmitted";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_NOT_IN_PROCESS = "error.purchaseOrder.quote.notInProcess";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_TRANSMIT_TYPE_NOT_SELECTED = "error.purchaseOrder.quote.transmit.type.not.selected";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_FAX_TRANSMIT_SERVICE_NOT_IMPLEMENTED = "error.purchaseOrder.quote.fax.transmit.service.not.implemented";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_STATUS_NOT_SELECTED = "error.purchaseOrder.quote.status.not.selected";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_INACTIVE_VENDOR = "error.purchaseOrder.quote.inactive.vendor";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_DEBARRED_VENDOR = "error.purchaseOrder.quote.debarred.vendor";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_AWARD_NON_PO = "error.purchaseOrder.quote.award.nonPO";
        
    public static final String ERROR_PURCHASE_ORDER_ALTERNATE_VENDOR_INACTIVE = "error.inactive";
    public static final String ERROR_PURCHASE_ORDER_ALTERNATE_VENDOR_DV_TYPE = "error.purchaseOrder.alternateVendor.dvType";
    public static final String ERROR_PURCHASE_ORDER_ALTERNATE_VENDOR_DEBARRED = "error.purchaseOrder.alternateVendor.debarred";
    public static final String ERROR_PURCHASE_ORDER_RECEIVING_DOC_REQUIRED_ID_PENDING_PREQ = "error.purchaseOrder.receivingDocRequiredId.pendingPreq";
    public static final String ERROR_PURCHASE_ORDER_SPLIT_ONE_ITEM_MUST_MOVE = "error.purchaseOrder.split.atLeastOneItem.mustMove";
    public static final String ERROR_PURCHASE_ORDER_SPLIT_ONE_ITEM_MUST_REMAIN = "error.purchaseOrder.split.atLeastOneItem.mustRemain";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_LIST_NO_VENDOR = "error.purchaseOrder.quoteList.no.vendor";
    public static final String ERROR_PURCHASE_ORDER_QUOTE_LIST_NON_EXISTENCE_VENDOR = "error.purchaseOrder.quoteList.nonExistence.vendor";
    public static final String ERROR_ASSIGN_SENSITIVE_DATA_REASON_EMPTY = "error.assign.sensitiveData.reason.empty";
    public static final String ERROR_ASSIGN_SENSITIVE_DATA_INACTIVE = "error.assign.sensitiveData.inactive";
    public static final String ERROR_ASSIGN_SENSITIVE_DATA_REDUNDANT = "error.assign.sensitiveData.redundant";
    
    public static final String ERROR_PURCHASING_REQUIRES_CALCULATE = "errors.purchasing.calculationRequired";
    
    // Accounts Payable
    public static final String ERROR_SAVE_REQUIRES_CALCULATE = "errors.save.calculationRequired";
    public static final String ERROR_APPROVE_REQUIRES_CALCULATE = "errors.save.calculationRequired";
    public static final String ERROR_AP_REQUIRES_ATTACHMENT = "errors.ap.attachmentRequired";
    public static final String AP_QUESTION_CONFIRM_INVOICE_MISMATCH = "ap.question.confirm.invoice.mismatch";
    public static final String AP_QUESTION_PREFIX = "ap.question.";
    public static final String AP_REOPENS_PURCHASE_ORDER_NOTE = "ap.reopens.purchase.order.note";

    // Payment Request
    public static final String ERROR_PURCHASE_ORDER_NOT_EXIST = "error.invoice.purchaseOrder.notExist";
    public static final String ERROR_PURCHASE_ORDER_NOT_OPEN = "error.invoice.purchaseOrder.notOpen";
    public static final String ERROR_PURCHASE_PENDING_ACTION = "error.invoice.purchaseOrder.pending.action";
    public static final String ERROR_PURCHASE_ORDER_IS_PENDING = "error.invoice.purchaseOrder.isPending";
    public static final String ERROR_INVALID_INVOICE_DATE = "errors.invalid.invoice.date";
    public static final String ERROR_INVALID_PAY_DATE = "errors.invalid.pay.date";
    public static final String ERROR_NO_ITEMS_TO_INVOICE = "errors.invoice.items.noneLeft";
    public static final String ERROR_PAYMENT_REQUEST_REASON_REQUIRED = "error.paymentRequest.reasonRequired";
    public static final String ERROR_CANCEL_CANCELLED = "errors.cancel.cancelled";
    public static final String ERROR_CANCEL_EXTRACTED = "errors.cancel.extracted";
    public static final String ERROR_PAYMENT_REQUEST_ITEM_TOTAL_NOT_EQUAL = "error.paymentRequest.item.TotalInvoice.notEqual";
    public static final String ERROR_PAYMENT_REQUEST_GRAND_TOTAL_NOT_POSITIVE = "error.paymentRequest.grandTotal.not.positive";
    public static final String ERROR_PAYMENT_REQUEST_INVOICE_REQUIRED = "error.paymentRequest.invoice.required";
    public static final String ERROR_PAYMENT_REQUEST_LINE_ITEM_QUANTITY_ZERO = "error.paymentRequest.lineItem.quantity.zero";
    
    public static final String ERROR_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT_ORG_AND_ACCOUNT_EXCLUSIVE = "error.negativePaymentRequestApprovalLimit.organizationAndAccountMutuallyExclusive";
    public static final String ERROR_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT_ORG_AND_ACCOUNT_UNIQUE = "error.negativePaymentRequestApprovalLimit.organizationAndAccountUniqueEntry";
    public static final String MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT = "message.duplicate.invoice.date.amount";
    public static final String MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLEDORVOIDED = "message.duplicate.invoice.date.amount.cancelledOrVoided";
    public static final String MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_VOIDED = "message.duplicate.invoice.date.amount.voided";
    public static final String MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLED = "message.duplicate.invoice.date.amount.cancelled";
    public static final String MESSAGE_INVOICE_DATE_A_YEAR_OR_MORE_PAST = "message.invoice.date.a.year.or.more.past";

    public static final String MESSAGE_DUPLICATE_INVOICE = "errors.duplicate.vendor.invoice";
    public static final String MESSAGE_DUPLICATE_INVOICE_CANCELLEDORVOIDED = "errors.duplicate.vendor.invoice.cancelledOrVoided";
    public static final String MESSAGE_DUPLICATE_INVOICE_CANCELLED = "errors.duplicate.vendor.invoice.cancelled";
    public static final String MESSAGE_DUPLICATE_INVOICE_VOIDED = "errors.duplicate.vendor.invoice.voided";

    public static final String MESSAGE_CLOSED_OR_EXPIRED_ACCOUNTS_REPLACED = "message.closed.or.expired.accounts.replaced";

    public static final String PAYMENT_REQUEST_MESSAGE_HOLD_DOCUMENT = "paymentRequest.message.hold.text";

    public static final String PAYMENT_REQUEST_MESSAGE_REMOVE_HOLD_DOCUMENT = "paymentRequest.message.remove.hold.text";

    public static final String PAYMENT_REQUEST_MESSAGE_CANCEL_DOCUMENT = "paymentRequest.message.cancel.text";

    public static final String PAYMENT_REQUEST_MESSAGE_REMOVE_CANCEL_DOCUMENT = "paymentRequest.message.remove.cancel.text";

    public static final String ERROR_NEXT_FY_BEGIN_DATE_INVALID = "error.nextFY.beginDate.invalid";
    public static final String WARNING_PURCHASE_ORDER_ENCUMBER_NEXT_FY = "warning.purchaseOrder.encumber.nextFY";
    public static final String WARNING_ENCUMBER_NEXT_FY = "warning.encumber.nextFY";
    public static final String WARNING_ENCUMBER_PRIOR_FY = "warning.encumber.priorFY";
    public static final String WARNING_CANCEL_REOPEN_PO = "warning.cancel.reOpenPO";
    public static final String WARNING_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS = "warning.paymentRequest.payDate.over.threshold.days";
    public static final String MESSAGE_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS = "message.paymentRequest.payDate.over.threshold.days";
    public static final String WARNING_PAYMENT_REQUEST_VENDOR_INVOICE_AMOUNT_INVALID = "warning.paymentRequest.vendorInvoiceAmount.invalid";

    public static final String ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED = "errors.paymentRequest.tax.field.required";
    public static final String ERROR_PAYMENT_REQUEST_TAX_FIELD_REQUIRED_IF = "errors.paymentRequest.tax.field.required.if";
    public static final String ERROR_PAYMENT_REQUEST_TAX_FIELD_DISALLOWED_IF = "errors.paymentRequest.tax.field.disallowed.if";
    public static final String ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_INVALID_IF = "errors.paymentRequest.tax.field.value.invalid.if";
    public static final String ERROR_PAYMENT_REQUEST_TAX_FIELD_VALUE_MUST_NOT_NEGATIVE = "errors.paymentRequest.tax.field.value.must.not.negative";
    public static final String ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_ZERO_IF = "errors.paymentRequest.tax.rate.must.zero.if";
    public static final String ERROR_PAYMENT_REQUEST_TAX_RATE_MUST_NOT_ZERO_IF = "errors.paymentRequest.tax.rate.must.not.zero.if";
    public static final String ERROR_PAYMENT_REQUEST_EXEMPT_UNDER_OTHER_CODE_MUST_EXIST = "errors.paymentRequest.tax.exempt.under.other.code.must.be.checked";

    // Item parser
    public static final String ERROR_ITEMPARSER_INVALID_FILE_FORMAT = "error.itemParser.invalidFileFormat";
    public static final String ERROR_ITEMPARSER_WRONG_PROPERTY_NUMBER = "error.itemParser.wrongPropertyNumber";
    public static final String ERROR_ITEMPARSER_INVALID_UOM_CODE = "error.itemParser.invalidUOMCode";
    public static final String ERROR_ITEMPARSER_INVALID_NUMERIC_VALUE = "error.itemParser.invalidNumericValue";
    public static final String ERROR_ITEMPARSER_ITEM_LINE = "error.itemParser.itemLine";
    public static final String ERROR_ITEMPARSER_ITEM_PROPERTY = "error.itemParser.itemProperty";

    // Item and Accounting line
    public static final String ERROR_ITEM_AMOUNT_BELOW_ZERO = "errors.item.amount.belowZero";
    public static final String ERROR_ITEM_AMOUNT_NOT_BELOW_ZERO = "errors.item.amount.notBelowZero";
    public static final String ERROR_ITEM_ACCOUNTING_NOT_UNIQUE = "errors.item.accounting.notunique";
    public static final String ERROR_ITEM_TOTAL_NEGATIVE = "errors.item.total.negative";
    public static final String ERROR_ITEM_QUANTITY = "errors.item.quantity";
    public static final String ERROR_ITEM_ACCOUNTING_NOT_ALLOWED = "errors.item.accounting.notallowed";
    public static final String ERROR_ITEM_ACCOUNTING_INCOMPLETE = "errors.item.accounting.incomplete";    
    public static final String ERROR_ITEM_ACCOUNTING_AMOUNT_INVALID = "errors.item.accounting.amount.invalid";
    public static final String ERROR_ITEM_ACCOUNTING_TOTAL = "errors.item.accounting.total";
    public static final String ERROR_ITEM_ACCOUNTING_PERCENT_OR_AMOUNT_INVALID = "errors.item.accounting.percent.or.total.invalid";
    public static final String ERROR_ITEM_ACCOUNTING_TOTAL_AMOUNT = "errors.item.accounting.total.amount";
    public static final String ERROR_ITEM_ACCOUNTING_LINE_ATLEAST_ONE_PERCENT_MISSING = "errors.item.accounting.atleast.one.percent.missing";
    public static final String ERROR_PURCHASE_ORDER_EXCEEDING_TOTAL_LIMIT = "errors.purchaseorder.exceedingTotalLimit";
    public static final String ERROR_ITEM_REQUIRED = "errors.item.required";
    public static final String ERROR_ITEM_TRADEIN_DISCOUNT_COEXISTENCE = "errors.purchaseOrderItems.TradeInAndDiscountCoexistence";
    public static final String ERROR_ITEM_BELOW_THE_LINE = "errors.item.belowTheLine";
    public static final String ERROR_ITEM_QUANTITY_NOT_ALLOWED = "errors.item.quantity.isNotAllowed";
    public static final String ERROR_ITEM_AMND_NULL = "errors.item.amnd.null";
    public static final String ERROR_ITEM_AMND_INVALID = "errors.item.amnd.invalid";
    public static final String ERROR_ITEM_AMND_INVALID_AMT = "errors.item.amnd.invalidAmt";
    public static final String ERROR_ITEM_QUANTITY_TOO_MANY = "errors.item.quantity.tooMany";
    public static final String ERROR_ITEM_QUANTITY_REQUIRED = "errors.item.quantity.required";
    public static final String ERROR_ITEM_AMOUNT_ALREADY_PAID = "errors.item.amount.alreadyPaid";
    public static final String ERROR_ITEM_PERCENT = "errors.item.percent";
    public static final String ERROR_ITEM_ACCOUNTING_ROUNDING = "errors.item.accounting.rounding";
    public static final String ERROR_ITEM_ACCOUNTING_DOLLAR_TOTAL = "errors.item.accounting.dollar.total";
    public static final String ERROR_ITEM_BELOW_THE_LINE_NO_UNIT_COST = "errors.item.belowTheLine.noUnitCost";
    public static final String ERROR_PURCHASING_PERCENT_NOT_WHOLE = "errors.purchasing.percent.not.whole";
    public static final String ERROR_PURCHASING_AMOUNT_MISSING = "errors.purchasing.amount.missing";
    public static final String ERROR_PURCHASING_AMOUNT_AND_PERCENT_MISSING = "errors.purchasing.amount.and.percent.missing";
    public static final String ERROR_ITEM_ACCOUNTING_AMOUNT_TOTAL = "errors.item.accounting.amount.total";
    public static final String ERROR_ACCOUNT_AMOUNT_TOTAL = "errors.accountString.totalAmount.negative";
    public static final String ERROR_ITEM_ACCOUNT_EXPIRED = "errors.item.accounting.expired";
    public static final String ERROR_ITEM_ACCOUNT_EXPIRED_REPLACE = "errors.item.accounting.expired.replace";
    public static final String ERROR_ITEM_ACCOUNT_INACTIVE = "errors.item.accounting.inactive";
    public static final String ERROR_ITEM_ACCOUNT_NEGATIVE = "errors.item.account.negative";
    public static final String ERROR_RECEIVING_REQUIRED = "errors.purchasing.receivingrequired.nonquantity";
    public static final String ERROR_ITEM_TRADE_IN_OUTSTANDING_ENCUMBERED_AMOUNT_NEGATIVE = "errors.item.tradeIn.outstanding.encumbered.amount.negative";
    public static final String WARNING_ITEM_TRADE_IN_AMOUNT_UNUSED = "warning.item.tradeIn.amount.unused";
    public static final String ERROR_ITEM_TRADE_IN_NEEDS_TO_BE_ASSIGNED = "errors.item.tradeIn.needs.to.be.assigned";
    public static final String ERROR_SUMMARY_ACCOUNTS_LIST_EMPTY = "errors.summary.accounts.list.empty";
    public static final String ERROR_ITEM_TYPE_QUANTITY_BASED_NOT_ALLOWED_WITH_ADDITIONAL_CHARGE = "errors.itemType.quantityBased.isNotAllowedWithAdditionalCharge";
    
    // Credit Memo
    public static final String ERROR_CREDIT_MEMO_REQUIRED_FIELDS = "errors.creditMemo.required.fields";
    public static final String ERROR_CREDIT_MEMO_PURCHASE_ORDER_INVALID = "errors.creditMemo.purchaseOrder.invalid";
    public static final String ERROR_CREDIT_MEMO_PURCAHSE_ORDER_INVALID_STATUS = "errors.creditMemo.purchaseOrder.invalid.status";
    public static final String ERROR_CREDIT_MEMO_PURCAHSE_ORDER_NOITEMS = "errors.creditMemo.po.noItems";
    public static final String ERROR_CREDIT_MEMO_PAYMENT_REQEUEST_INVALID = "errors.creditMemo.paymentRequest.invalid";
    public static final String ERROR_CREDIT_MEMO_PAYMENT_REQEUEST_INVALID_SATATUS = "errors.creditMemo.paymentRequest.invalid.status";
    public static final String ERROR_CREDIT_MEMO_VENDOR_NUMBER_INVALID = "errors.creditMemo.vendorNumber.invalid";
    public static final String ERROR_CREDIT_MEMO_INVOICE_AMOUNT_NONMATCH = "errors.creditMemo.vendorInvoiceAmount.invalid";
    public static final String ERROR_CREDIT_MEMO_TOTAL_ZERO = "errors.creditMemo.total.zero";
    public static final String ERROR_CREDIT_MEMO_ITEM_AMOUNT_NONPOSITIVE = "errors.creditMemo.itemAmount.aboveZero";
    public static final String ERROR_CREDIT_MEMO_ITEM_QUANTITY_TOOMUCH = "errors.creditMemo.item.quantity.tooMuch";
    public static final String ERROR_CREDIT_MEMO_ITEM_EXTENDEDPRICE_TOOMUCH = "errors.creditMemo.item.extendedPrice.tooMuch";
    public static final String ERROR_CREDIT_MEMO_LINE_PERCENT = "errors.creditMemo.line.percent";

    public static final String CREDIT_MEMO_QUESTION_HOLD_DOCUMENT = "creditMemo.question.hold.text";
    public static final String CREDIT_MEMO_QUESTION_CANCEL_DOCUMENT = "creditMemo.question.cancel.text";
    public static final String CREDIT_MEMO_QUESTION_REMOVE_HOLD_DOCUMENT = "creditMemo.question.removeHold.text";

    public static final String MESSAGE_DUPLICATE_CREDIT_MEMO_VENDOR_NUMBER = "message.duplicate.creditMemo.vendorNumber";
    public static final String MESSAGE_DUPLICATE_CREDIT_MEMO_VENDOR_NUMBER_DATE_AMOUNT = "message.duplicate.creditMemo.vendorNumber.date.amount";
    public static final String ERROR_CREDIT_MEMO_REQUIRES_ATTACHMENT = "errors.creditMemo.attachmentRequired";

    // Receiving Line
    public static final String ERROR_RECEIVING_LINE_DOCUMENT_ACTIVE_FOR_PO = "errors.receivingLine.documentActiveForPo";
    public static final String ERROR_RECEIVING_LINE_DOCUMENT_PO_NOT_ACTIVE = "errors.receivingLine.poNotActive";
    public static final String ERROR_RECEIVING_LINEITEM_REQUIRED = "errors.receiving.lineitem.required";
    public static final String MESSAGE_DUPLICATE_RECEIVING_LINE_PREFIX = "message.duplicate.receivingLine.prefix";
    public static final String MESSAGE_DUPLICATE_RECEIVING_LINE_SUFFIX = "message.duplicate.receivingLine.suffix";
    public static final String MESSAGE_DUPLICATE_RECEIVING_LINE_VENDOR_DATE = "message.duplicate.receivngLine.vendorDate";
    public static final String MESSAGE_DUPLICATE_RECEIVING_LINE_PACKING_SLIP_NUMBER = "message.duplicate.receivingLine.packingSlipNumber";
    public static final String MESSAGE_DUPLICATE_RECEIVING_LINE_BILL_OF_LADING_NUMBER = "message.duplicate.receivingLine.billOfLadingNumber";
    public static final String MESSAGE_RECEIVING_LINEITEM_RETURN_NOTE_TEXT = "message.receiving.lineitem.return";
    public static final String MESSAGE_RECEIVING_LINEITEM_DAMAGE_NOTE_TEXT = "message.receiving.lineitem.damage";
    public static final String WARNING_RECEIVING_LINEITEM_ADD_UNORDERED = "warning.receiving.lineitem.add.unordered";
    public static final String ERROR_RECEIVING_LINE_QTYRETURNED_GT_QTYRECEIVED = "errors.receivingLine.quantityReturnedGreaterThanQuantityReceived";
    public static final String ERROR_RECEIVING_LINE_QTYDAMAGED_GT_QTYRECEIVED = "errors.receivingLine.quantityDamagedGreaterThanQuantityReceived";
    
    //Threshold
    public static final String THRESHOLD_FIELD_INVALID = "errors.threshold.field.invalid";
    public static final String INVALID_THRESHOLD_CRITERIA = "errors.threshold.criteria.invalid";
        
    // Receiving Correction
    public static final String ERROR_RECEIVING_CORRECTION_DOCUMENT_ACTIVE_FOR_RCV_LINE = "errors.receivingCorrection.documentActiveForRcvLine";
    public static final String MESSAGE_RECEIVING_CORRECTION_NOTE = "message.receiving.correction.note"; 
    
    //Bulk Receiving
    public static final String ERROR_BULK_RECEIVING_DOCUMENT_ACTIVE_FOR_PO = "errors.bulkReceiving.documentActiveForPo";
    public static final String ERROR_BULK_RECEIVING_DOCUMENT_INVALID_PO = "errors.bulkReceiving.invalidPo";
    public static final String ERROR_BULK_RECEIVING_PO_NOT_OPEN = "errors.bulkReceiving.purchaseOrder.notOpen";
    public static final String MESSAGE_BULK_RECEIVING_GOODSDELIVEREDBY_LABEL = "message.bulkReceiving.goodsDeliveredBy.label";
    public static final String MESSAGE_BULK_RECEIVING_DUPLICATE_PREFIX = "message.bulkReceiving.duplicate.prefix";
    public static final String ERROR_BULK_RECEIVING_PDF = "error.bulkreceiving.pdf";
    
    //Electronic Invoice
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_EINVOICE = "message.batchUpload.title.einvoice";
    public static final String ERROR_REJECT_INVALID_DUNS = "errors.reject.invalidDuns";
    public static final String ERROR_REJECT_INVOICE_NUMBER_EMPTY = "errors.reject.invoicenumber.empty";
    public static final String ERROR_REJECT_INVOICE_DATE_INVALID = "errors.reject.invoicedate.invalid";
    public static final String ERROR_REJECT_INVOICE_DATE_GREATER = "errors.reject.invoicedate.greater";
    public static final String ERROR_REJECT_INVOICE_POID_EMPTY = "errors.reject.poid.empty";
    public static final String ERROR_REJECT_INVOICE_POID_INVALID = "errors.reject.poid.invalid";
    public static final String ERROR_REJECT_INVOICE__PO_NOT_EXISTS = "errors.reject.po.notexists";
    public static final String ERROR_REJECT_INVOICE__ITEM_NOMATCH = "errors.reject.invoice.item.nomatch";
    public static final String ERROR_REJECT_PO_ITEM_DUPLICATE = "errors.reject.po.item.dupliate";
    public static final String ERROR_REJECT_PO_ITEM_INACTIVE = "errors.reject.po.item.inactive";
    public static final String ERROR_REJECT_CATALOG_MISMATCH = "errors.reject.catalog.mismatch";
    public static final String ERROR_REJECT_UOM_MISMATCH = "errors.reject.uom.mismatch";
    public static final String ERROR_REJECT_UNITPRICE_LOWERVARIANCE = "errors.reject.unitprice.lowervariance";
    public static final String ERROR_REJECT_UNITPRICE_UPPERVARIANCE = "errors.reject.unitprice.uppervariance";
    public static final String ERROR_REJECT_TAXAMOUNT_LOWERVARIANCE = "errors.reject.taxamount.lowervariance";
    public static final String ERROR_REJECT_TAXAMOUNT_UPPERVARIANCE = "errors.reject.taxamount.uppervariance";
    public static final String ERROR_REJECT_POITEM_OUTSTANDING_QTY = "errors.reject.poitem.outstanding.qty";
    public static final String ERROR_REJECT_POITEM_INVOICE_QTY_EMPTY = "errors.reject.poitem.invoice.qty.empty";
    public static final String ERROR_REJECT_POITEM_LESS_OUTSTANDING_QTY = "errors.reject.poitem.less.outstanding.qty";
    public static final String ERROR_REJECT_POITEM_OUTSTANDING_EMCUMBERED_AMOUNT = "errors.reject.poitem.outstanding.amt";
    public static final String ERROR_REJECT_POITEM_LESS_OUTSTANDING_EMCUMBERED_AMOUNT = "errors.reject.poitem.less.outstanding.amt";
    public static final String ERROR_REJECT_INVOICE_DUPLICATE = "errors.reject.invoice.duplicate";
    public static final String ERROR_ELECTRONIC_INVOICE_GENERATION_PURCHASE_ORDER_NUMBER_EMPTY = "error.electronicInvoice.generate.purchase.order.number.empty";
    public static final String ERROR_ELECTRONIC_INVOICE_GENERATION_PURCHASE_ORDER_DOES_NOT_EXIST = "error.electronicInvoice.generate.purchase.order.nonexisting";
    
    //B2B
    public static final String B2B_MULTIPLE_REQUISITIONS = "message.b2b.multiple.requisitions";
    public static final String B2B_PO_RETRANSMIT_SUCCESS = "message.b2b.po.retransmit.success";
    public static final String B2B_PO_RETRANSMIT_FAILED = "message.b2b.po.retransmit.failed";
    
    public static final String ERROR_INACTIVE_BUILDING ="error.inactive.building";
    public static final String ERROR_INACTIVE_ROOM ="error.inactive.room";
    
    public static final String ERROR_INACTIVE_ORG ="error.inactive.organization";
    
    public static final String ERROR_ITEM_ACCOUNTING_ZERO = "errors.item.accounting.zero";
    
    public static final String ERROR_ITEM_UOM_NOT_ALLOWED = "errors.item.uom.isNotAllowed";
    public static final String ERROR_ITEM_UOM_INACTIVE = "errors.item.uom.inactive";
    
    public static final String ERROR_PAYMENT_REQUEST_CANNOT_BE_CANCELLED = "error.paymentRequest.cannot.be.cancelled";
    
}
