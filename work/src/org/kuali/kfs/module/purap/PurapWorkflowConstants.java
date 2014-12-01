/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;

/**
 * Holds Workflow constants for PURAP documents
 */
public class PurapWorkflowConstants {

    // Global
    public static final String HAS_ACCOUNTING_LINES = "HasAccountingLines";
    public static final String AMOUNT_REQUIRES_SEPARATION_OF_DUTIES_REVIEW_SPLIT = "AmountRequiresSeparationOfDutiesReview";
    public static final String CONTRACT_MANAGEMENT_REVIEW_REQUIRED = "RequiresContractManagementReview";
    public static final String VENDOR_IS_EMPLOYEE_OR_NON_RESIDENT_ALIEN = "VendorIsEmployeeOrNonResidentAlien";
    public static final String AWARD_REVIEW_REQUIRED = "RequiresAwardReview";
    public static final String BUDGET_REVIEW_REQUIRED = "RequiresBudgetReview";
    public static final String TRANSMISSION_METHOD_IS_PRINT = "TransmissionMethodIsPrint";
    public static final String HAS_NEW_UNORDERED_ITEMS = "HasNewUnorderedItems";
    public static final String RELATES_TO_OUTSTANDING_TRANSACTIONS = "RelatesToOutstandingTransactions";
    public static final String REQUIRES_IMAGE_ATTACHMENT = "RequiresImageAttachment";
    public static final String PURCHASE_WAS_RECEIVED = "PurchaseWasReceived";
    public static final String IS_DOCUMENT_AUTO_APPROVED = "RequiresAutoApprovalNotification";
    
    public static final String DOC_ADHOC_NODE_NAME = "AdHoc";

    public static class ContractManagerAssignmentDocument {
        public static final String WORKFLOW_DOCUMENT_TITLE = "Contract Manager Assignment";
        public static final String ASSIGN_CONTRACT_DOC_ERROR_COMPLETING_POST_PROCESSING = "Unable to save the contract manager for the following Requisitions: ";
    }

}
