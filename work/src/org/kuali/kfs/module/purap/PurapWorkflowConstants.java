/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;

/**
 * Holds Workflow constants for PURAP documents
 */
public class PurapWorkflowConstants {

    public interface NodeDetails {
        public String getName();

        public String getAwaitingStatusCode();

        public String getDisapprovedStatusCode();

        public NodeDetails getPreviousNodeDetails();

        public NodeDetails getNextNodeDetails();

        public NodeDetails getNodeDetailByName(String name);

        public int getOrdinal();
    }

    // Global
    public static final String AMOUNT_REQUIRES_SEPARATION_OF_DUTIES_REVIEW_SPLIT = "AmountRequiresSeparationOfDutiesReview";
    public static final String CONTRACT_MANAGEMENT_REVIEW_REQUIRED = "RequiresContractManagementReview";
    public static final String VENDOR_IS_EMPLOYEE_OR_NON_RESIDENT_ALIEN = "VendorIsEmployeeOrNonResidentAlien";
    public static final String BUDGET_REVIEW_REQUIRED = "RequiresBudgetReview";
    public static final String TRANSMISSION_METHOD_IS_PRINT = "TransmissionMethodIsPrint";
    public static final String HAS_NEW_UNORDERED_ITEMS = "HasNewUnorderedItems";
    public static final String RELATES_TO_OUTSTANDING_TRANSACTIONS = "RelatesToOutstandingTransactions";
    public static final String REQUIRES_INVOICE_ATTACHMENT = "RequiresInvoiceAttachment";
    public static final String PURCHASE_WAS_RECEIVED = "PurchaseWasReceived";
    
    public static final String DOC_ADHOC_NODE_NAME = "AdHoc";

    public static class ContractManagerAssignmentDocument {
        public static final String WORKFLOW_DOCUMENT_TITLE = "Contract Manager Assignment";
        public static final String ASSIGN_CONTRACT_DOC_ERROR_COMPLETING_POST_PROCESSING = "Unable to save the contract manager for the following Requisitions: ";
    }

    public static class RequisitionDocument {
        public enum NodeDetailEnum implements NodeDetails {
            ADHOC_REVIEW(DOC_ADHOC_NODE_NAME, null, PurapConstants.RequisitionStatuses.CANCELLED), CONTENT_REVIEW("Organization", PurapConstants.RequisitionStatuses.AWAIT_CONTENT_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_CONTENT), SUB_ACCOUNT_REVIEW("SubAccount", PurapConstants.RequisitionStatuses.AWAIT_SUB_ACCT_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_SUB_ACCT), ACCOUNT_REVIEW("Account", PurapConstants.RequisitionStatuses.AWAIT_FISCAL_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_FISCAL), ORG_REVIEW("OrganizationHierarchy", PurapConstants.RequisitionStatuses.AWAIT_CHART_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_CHART), COMMODITY_CODE_REVIEW("Commodity", PurapConstants.RequisitionStatuses.AWAIT_COMMODITY_CODE_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_COMMODITY_CODE), SEPARATION_OF_DUTIES_REVIEW("SeparationOfDuties", PurapConstants.RequisitionStatuses.AWAIT_SEP_OF_DUTY_REVIEW, PurapConstants.RequisitionStatuses.DAPRVD_SEP_OF_DUTY), ;
            private final String name;
            private final String awaitingStatusCode;
            private final String disapprovedStatusCode;

            NodeDetailEnum(String name, String awaitingStatusCode, String disapprovedStatusCode) {
                this.name = name;
                this.awaitingStatusCode = awaitingStatusCode;
                this.disapprovedStatusCode = disapprovedStatusCode;
            }

            public String getName() {
                return name;
            }

            public String getAwaitingStatusCode() {
                return awaitingStatusCode;
            }

            public String getDisapprovedStatusCode() {
                return disapprovedStatusCode;
            }

            public NodeDetails getPreviousNodeDetails() {
                if (this.ordinal() > 0) {
                    return NodeDetailEnum.values()[this.ordinal() - 1];
                }
                return null;
            }

            public NodeDetails getNextNodeDetails() {
                if (this.ordinal() < (NodeDetailEnum.values().length - 1)) {
                    return NodeDetailEnum.values()[this.ordinal() + 1];
                }
                return null;
            }

            public NodeDetails getNodeDetailByName(String name) {
                return getNodeDetailEnumByName(name);
            }

            public static NodeDetails getNodeDetailEnumByName(String name) {
                for (NodeDetailEnum nodeDetailEnum : NodeDetailEnum.values()) {
                    if (nodeDetailEnum.name.equals(name)) {
                        return nodeDetailEnum;
                    }
                }
                return null;
            }

            public int getOrdinal() {
                return this.ordinal();
            }
        }

    }

    public static class PurchaseOrderDocument {
        public enum NodeDetailEnum implements NodeDetails {
            ADHOC_REVIEW(DOC_ADHOC_NODE_NAME, null, PurapConstants.PurchaseOrderStatuses.CANCELLED), AWAIT_NEW_UNORDERED_ITEM_REVIEW("NewUnorderedItems", PurapConstants.PurchaseOrderStatuses.AWAIT_NEW_UNORDERED_ITEM_REVIEW, PurapConstants.PurchaseOrderStatuses.VOID), INTERNAL_PURCHASING_REVIEW("ContractManagement", PurapConstants.PurchaseOrderStatuses.AWAIT_PURCHASING_REVIEW, PurapConstants.PurchaseOrderStatuses.DAPRVD_PURCHASING), COMMODITY_CODE_REVIEW("Commodity", PurapConstants.PurchaseOrderStatuses.AWAIT_COMMODITY_CODE_REVIEW, PurapConstants.PurchaseOrderStatuses.DAPRVD_COMMODITY_CODE), CONTRACTS_AND_GRANTS_REVIEW("Award", PurapConstants.PurchaseOrderStatuses.AWAIT_CONTRACTS_GRANTS_REVIEW, PurapConstants.PurchaseOrderStatuses.DAPRVD_CONTRACTS_GRANTS), BUDGET_OFFICE_REVIEW("Budget", PurapConstants.PurchaseOrderStatuses.AWAIT_BUDGET_REVIEW, PurapConstants.PurchaseOrderStatuses.DAPRVD_BUDGET), VENDOR_TAX_REVIEW("Tax", PurapConstants.PurchaseOrderStatuses.AWAIT_TAX_REVIEW, PurapConstants.PurchaseOrderStatuses.DAPRVD_TAX), DOCUMENT_TRANSMISSION("PrintTransmission", null, PurapConstants.PurchaseOrderStatuses.VOID), ;

            private final String name;
            private final String awaitingStatusCode;
            private final String disapprovedStatusCode;

            NodeDetailEnum(String name, String awaitingStatusCode, String disapprovedStatusCode) {
                this.name = name;
                this.awaitingStatusCode = awaitingStatusCode;
                this.disapprovedStatusCode = disapprovedStatusCode;
            }

            public String getName() {
                return name;
            }

            public String getAwaitingStatusCode() {
                return awaitingStatusCode;
            }

            public String getDisapprovedStatusCode() {
                return disapprovedStatusCode;
            }

            public NodeDetails getPreviousNodeDetails() {
                if (this.ordinal() > 0) {
                    return NodeDetailEnum.values()[this.ordinal() - 1];
                }
                return null;
            }

            public NodeDetails getNextNodeDetails() {
                if (this.ordinal() < (NodeDetailEnum.values().length - 1)) {
                    return NodeDetailEnum.values()[this.ordinal() + 1];
                }
                return null;
            }

            public NodeDetails getNodeDetailByName(String name) {
                return getNodeDetailEnumByName(name);
            }

            public static NodeDetails getNodeDetailEnumByName(String name) {
                for (NodeDetailEnum nodeDetailEnum : NodeDetailEnum.values()) {
                    if (nodeDetailEnum.name.equals(name)) {
                        return nodeDetailEnum;
                    }
                }
                return null;
            }

            public int getOrdinal() {
                return this.ordinal();
            }
        }
    }

    public static class PaymentRequestDocument {
        public enum NodeDetailEnum implements NodeDetails {
            ADHOC_REVIEW(DOC_ADHOC_NODE_NAME, null, PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS, false), ACCOUNTS_PAYABLE_REVIEW("InvoiceAttachment", PurapConstants.PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW, PurapConstants.PaymentRequestStatuses.CANCELLED_PRIOR_TO_AP_APPROVAL, false), AWAITING_RECEIVING_REVIEW("Receiving", PurapConstants.PaymentRequestStatuses.AWAITING_RECEIVING_REVIEW, PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE, false), SUB_ACCOUNT_REVIEW("SubAccount", PurapConstants.PaymentRequestStatuses.AWAITING_SUB_ACCT_MGR_REVIEW, PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE, false), ACCOUNT_REVIEW("Account", PurapConstants.PaymentRequestStatuses.AWAITING_FISCAL_REVIEW, PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE, true), ORG_REVIEW("OrganizationHierarchy", PurapConstants.PaymentRequestStatuses.AWAITING_ORG_REVIEW, PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE, false), VENDOR_TAX_REVIEW("Tax",
                    PurapConstants.PaymentRequestStatuses.AWAITING_TAX_REVIEW, PurapConstants.PaymentRequestStatuses.CANCELLED_POST_AP_APPROVE, false), ;

            private final String name;
            private final String awaitingStatusCode;
            private final String disapprovedStatusCode;
            private final boolean correctingGeneralLedgerEntriesRequired;

            NodeDetailEnum(String name, String awaitingStatusCode, String disapprovedStatusCode, boolean correctingGeneralLedgerEntriesRequired) {
                this.name = name;
                this.awaitingStatusCode = awaitingStatusCode;
                this.disapprovedStatusCode = disapprovedStatusCode;
                this.correctingGeneralLedgerEntriesRequired = correctingGeneralLedgerEntriesRequired;
            }

            public String getName() {
                return name;
            }

            public String getAwaitingStatusCode() {
                return awaitingStatusCode;
            }

            public String getDisapprovedStatusCode() {
                return disapprovedStatusCode;
            }

            public boolean isCorrectingGeneralLedgerEntriesRequired() {
                return correctingGeneralLedgerEntriesRequired;
            }

            public NodeDetails getPreviousNodeDetails() {
                if (this.ordinal() > 0) {
                    return NodeDetailEnum.values()[this.ordinal() - 1];
                }
                return null;
            }

            public NodeDetails getNextNodeDetails() {
                if (this.ordinal() < (NodeDetailEnum.values().length - 1)) {
                    return NodeDetailEnum.values()[this.ordinal() + 1];
                }
                return null;
            }

            public NodeDetails getNodeDetailByName(String name) {
                return getNodeDetailEnumByName(name);
            }

            public static NodeDetails getNodeDetailEnumByName(String name) {
                for (NodeDetailEnum nodeDetailEnum : NodeDetailEnum.values()) {
                    if (nodeDetailEnum.name.equals(name)) {
                        return nodeDetailEnum;
                    }
                }
                return null;
            }

            public static List<String> getNodesRequiringCorrectingGeneralLedgerEntries() {
                List<String> returnEnumNames = new ArrayList<String>();
                for (NodeDetailEnum currentEnum : NodeDetailEnum.values()) {
                    if (currentEnum.isCorrectingGeneralLedgerEntriesRequired()) {
                        returnEnumNames.add(currentEnum.getName());
                    }
                }
                return returnEnumNames;
            }

            public int getOrdinal() {
                return this.ordinal();
            }
        }
    }

    public static class CreditMemoDocument {
        public enum NodeDetailEnum implements NodeDetails {
            ADHOC_REVIEW(DOC_ADHOC_NODE_NAME, null, PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS, false), ACCOUNTS_PAYABLE_REVIEW("Accounts Payable Review", PurapConstants.CreditMemoStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW, PurapConstants.CreditMemoStatuses.CANCELLED_PRIOR_TO_AP_APPROVAL, false), ;

            private final String name;
            private final String awaitingStatusCode;
            private final String disapprovedStatusCode;
            private final boolean correctingGeneralLedgerEntriesRequired;

            NodeDetailEnum(String name, String awaitingStatusCode, String disapprovedStatusCode, boolean correctingGeneralLedgerEntriesRequired) {
                this.name = name;
                this.awaitingStatusCode = awaitingStatusCode;
                this.disapprovedStatusCode = disapprovedStatusCode;
                this.correctingGeneralLedgerEntriesRequired = correctingGeneralLedgerEntriesRequired;
            }

            public String getName() {
                return name;
            }

            public String getAwaitingStatusCode() {
                return awaitingStatusCode;
            }

            public String getDisapprovedStatusCode() {
                return disapprovedStatusCode;
            }

            public boolean isCorrectingGeneralLedgerEntriesRequired() {
                return correctingGeneralLedgerEntriesRequired;
            }

            public NodeDetails getPreviousNodeDetails() {
                if (this.ordinal() > 0) {
                    return NodeDetailEnum.values()[this.ordinal() - 1];
                }
                return null;
            }

            public NodeDetails getNextNodeDetails() {
                if (this.ordinal() < (NodeDetailEnum.values().length - 1)) {
                    return NodeDetailEnum.values()[this.ordinal() + 1];
                }
                return null;
            }

            public NodeDetails getNodeDetailByName(String name) {
                return getNodeDetailEnumByName(name);
            }

            public static NodeDetails getNodeDetailEnumByName(String name) {
                for (NodeDetailEnum nodeDetailEnum : NodeDetailEnum.values()) {
                    if (nodeDetailEnum.name.equals(name)) {
                        return nodeDetailEnum;
                    }
                }
                return null;
            }

            public static List<String> getNodesRequiringCorrectingGeneralLedgerEntries() {
                List<String> returnEnumNames = new ArrayList<String>();
                for (NodeDetailEnum currentEnum : NodeDetailEnum.values()) {
                    if (currentEnum.isCorrectingGeneralLedgerEntriesRequired()) {
                        returnEnumNames.add(currentEnum.getName());
                    }
                }
                return returnEnumNames;
            }

            public int getOrdinal() {
                return this.ordinal();
            }
        }
    }
}
