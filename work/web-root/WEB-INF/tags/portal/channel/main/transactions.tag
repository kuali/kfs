<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Transactions" />
<div class="body">
	<strong>Accounts Receivable</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Cash Control" url="arCashControlDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CashControlDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Customer Credit Memo" url="arCustomerCreditMemoDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CustomerCreditMemoDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Customer Invoice" url="arCustomerInvoiceDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CustomerInvoiceDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Payment Application" url="arPaymentApplicationDocument.do?methodToCall=docHandler&command=initiate&docTypeName=PaymentApplicationDocument" /></li>
    </ul>

    <strong>Budget Construction</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Budget Construction Selection" url="budgetBudgetConstructionSelection.do?methodToCall=loadExpansionScreen" /></li>
    </ul>

	<strong>Financial Processing</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Advance Deposit" url="financialAdvanceDeposit.do?methodToCall=docHandler&command=initiate&docTypeName=AdvanceDepositDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Auxiliary Voucher" url="financialAuxiliaryVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=AuxiliaryVoucherDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Budget Adjustment" url="financialBudgetAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=BudgetAdjustmentDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Cash Receipt" url="financialCashReceipt.do?methodToCall=docHandler&command=initiate&docTypeName=CashReceiptDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Credit Card Receipt" url="financialCreditCardReceipt.do?methodToCall=docHandler&command=initiate&docTypeName=CreditCardReceiptDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher" url="financialDisbursementVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=DisbursementVoucherDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Distribution of Income and Expense" url="financialDistributionOfIncomeAndExpense.do?methodToCall=docHandler&command=initiate&docTypeName=DistributionOfIncomeAndExpenseDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="General Error Correction" url="financialGeneralErrorCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=GeneralErrorCorrectionDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Indirect Cost Adjustment" url="financialIndirectCostAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=IndirectCostAdjustmentDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Internal Billing" url="financialInternalBilling.do?methodToCall=docHandler&command=initiate&docTypeName=InternalBillingDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Pre-Encumbrance" url="financialPreEncumbrance.do?methodToCall=docHandler&command=initiate&docTypeName=PreEncumbranceDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Transfer of Funds" url="financialTransferOfFunds.do?methodToCall=docHandler&command=initiate&docTypeName=TransferOfFundsDocument" /></li>
    </ul>
    
    <strong>Labor Distribution</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Benefit Expense Transfer" url="laborBenefitExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=BenefitExpenseTransferDocument" /></li>	            
		<li><portal:portalLink displayTitle="true" title="Salary Expense Transfer" url="laborSalaryExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=SalaryExpenseTransferDocument" /></li>	
    </ul>

	<strong>Pre-Award</strong><br />
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title="Research Budget" url="researchBudgetParameters.do?methodToCall=docHandler&command=initiate&docTypeName=KualiBudgetDocument" /></li>
	        <li><portal:portalLink displayTitle="true" title="Routing Form" url="researchRoutingFormMainPage.do?methodToCall=docHandler&command=initiate&docTypeName=KualiRoutingFormDocument" /></li>
		</ul>

	<strong>Purchasing/Accounts Payable</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Contract Manager Assignment" url="purapAssignContractManager.do?methodToCall=docHandler&command=initiate&docTypeName=AssignContractManagerDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Credit Memo" url="purapCreditMemo.do?methodToCall=docHandler&command=initiate&docTypeName=CreditMemoDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Payment Request" url="purapPaymentRequest.do?methodToCall=docHandler&command=initiate&docTypeName=PaymentRequestDocument" /></li>
		<li><portal:portalLink displayTitle="true" title="Receiving" url="purapReceivingLine.do?methodToCall=docHandler&command=initiate&docTypeName=ReceivingLineDocument" /></li>
        <li><portal:portalLink displayTitle="true" title="Requisition" url="purapRequisition.do?methodToCall=docHandler&command=initiate&docTypeName=RequisitionDocument" /></li>
    </ul>
</div>
<channel:portalChannelBottom />
