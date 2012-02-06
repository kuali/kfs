/*
 * Copyright 2007 The Kuali Foundation
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
    
    public static final String DOC_ADHOC_NODE_NAME = "AdHoc";

    public static class ContractManagerAssignmentDocument {
        public static final String WORKFLOW_DOCUMENT_TITLE = "Contract Manager Assignment";
        public static final String ASSIGN_CONTRACT_DOC_ERROR_COMPLETING_POST_PROCESSING = "Unable to save the contract manager for the following Requisitions: ";
    }

}
