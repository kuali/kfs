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
 * Holds error key constants for PURAP.
 * 
 */
public class PurapKeyConstants {

    public static final String PURAP_GENERAL_POTENTIAL_DUPLICATE = "error.document.purap.potentialDuplicate";
    
    //Purchase Order & Requisition
    public static final String ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END = "error.purchaseOrder.beginDateAfterEnd";
    public static final String ERROR_PURCHASE_ORDER_BEGIN_DATE_NO_END_DATE = "error.purchaseOrder.beginDateNoEndDate";
    public static final String ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE = "error.purchaseOrder.endDateNoBeginDate";
    public static final String ERROR_RECURRING_DATE_NO_TYPE = "errors.recurring.type";
    public static final String ERROR_RECURRING_TYPE_NO_DATE = "errors.recurring.dates";
    public static final String ERROR_POSTAL_CODE_INVALID = "errors.postalCode.invalid";
    public static final String ERROR_FAX_NUMBER_INVALID = "errors.faxNumber.invalid";
    public static final String ERROR_FAX_NUMBER_PO_TRANSMISSION_TYPE = "error.faxNumber.PoTransmissionType";
    public static final String REQ_TOTAL_GREATER_THAN_PO_TOTAL_LIMIT = "error.purchaseOrderTotalLimit";
    public static final String INVALID_CONTRACT_MANAGER_CODE = "error.invalidContractManagerCode";
    public static final String ERROR_REQ_COPY_EXPIRED_CONTRACT = "error.requisition.copy.expired.contract";
    public static final String ERROR_REQ_COPY_INACTIVE_VENDOR = "error.requisition.copy.inactive.vendor";
    public static final String ERROR_STIPULATION_DESCRIPTION = "error.purchaseOrder.stipulationDescriptionEmpty";

    //Purchase Order
    public static final String PURCHASE_ORDER_MESSAGE_CLOSE_DOCUMENT = "purchaseOrder.route.message.close.text";
    public static final String PURCHASE_ORDER_QUESTION_DOCUMENT = "purchaseOrder.question.text";
    public static final String PURCHASE_ORDER_CLOSE_NOTE_TEXT_INTRO = "purchaseOrder.message.close.noteTextIntro";
    public static final String ERROR_PURCHASE_ORDER_CLOSE_REASON_REQUIRED = "error.close.purchaseOrder.reasonRequired";    
    public static final String ERROR_PURCHASE_ORDER_CLOSE_STATUS = "error.close.purchaseOrder.status.incorrect";
    public static final String ERROR_PURCHASE_ORDER_CLOSE_NO_PREQ = "error.close.purchaseOrder.no.paymentRequest";
    public static final String ERROR_PURCHASE_ORDER_CLOSE_PREQ_IN_PROCESS = "error.close.purchaseOrder.paymentRequest.inProcess";
    public static final String MESSAGE_ROUTE_REOPENED="message.route.reopened";
    public static final String ERROR_USER_NONPURCHASING="errors.user.nonPurchasing";
    public static final String ERROR_PURCHASE_ORDER_PDF = "error.purchaseOrder.pdf";
    public static final String PURCHASE_ORDER_REOPEN_STATUS="purchaseOrder.reopen.status";
    public static final String WARNING_PURCHASE_ORDER_NOT_CURRENT="warning.purchaseOrder.notCurrent";
    public static final String WARNING_PURCHASE_ORDER_PENDING_ACTION_NOT_CURRENT="warning.purchaseOrder.pendingAction.notCurrent";
    public static final String WARNING_PURCHASE_ORDER_PENDING_ACTION="warning.purchaseOrder.pendingAction";
    public static final String WARNING_PURCHASE_ORDER_ENTIRE_STATUS_HISTORY="warning.purchaseOrder.entireStatusHistory";
    public static final String WARNING_PURCHASE_ORDER_ALL_NOTES="warning.purchaseOrder.allNotes";
    
    //Payment Request
    public static final String ERROR_PURCHASE_ORDER_NOT_EXIST="error.paymentRequest.purchaseOrder.notExist";
    public static final String ERROR_PURCHASE_ORDER_NOT_OPEN="error.paymentRequest.purchaseOrder.notOpen";
}