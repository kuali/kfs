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
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Transactions" />
<div class="body">
	<strong>Accounts Receivable</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Cash Control" url="arCashControlDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CTRL" /></li>
        <li><portal:portalLink displayTitle="true" title="Customer Credit Memo" url="arCustomerCreditMemoDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CRM" /></li>
        <li><portal:portalLink displayTitle="true" title="Customer Invoice" url="arCustomerInvoiceDocument.do?methodToCall=docHandler&command=initiate&docTypeName=INV" /></li>
        <li><portal:portalLink displayTitle="true" title="Customer Invoice Writeoff" url="arCustomerInvoiceWriteoffDocument.do?methodToCall=docHandler&command=initiate&docTypeName=INVW" /></li>
		<li><portal:portalLink displayTitle="true" title="Customer Invoice Writeoff Lookup" url="arCustomerInvoiceWriteoffLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>        
        <li><portal:portalLink displayTitle="true" title="Payment Application" url="arPaymentApplicationDocument.do?methodToCall=docHandler&command=initiate&docTypeName=APP" /></li>
    </ul>

    <strong>Budget Construction</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Budget Construction Selection" url="budgetBudgetConstructionSelection.do?methodToCall=loadExpansionScreen" /></li>
    </ul>

	<strong>Financial Processing</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Advance Deposit" url="financialAdvanceDeposit.do?methodToCall=docHandler&command=initiate&docTypeName=AD" /></li>
		<li><portal:portalLink displayTitle="true" title="Auxiliary Voucher" url="financialAuxiliaryVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=AV" /></li>
        <li><portal:portalLink displayTitle="true" title="Budget Adjustment" url="financialBudgetAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=BA" /></li>
        <li><portal:portalLink displayTitle="true" title="Cash Receipt" url="financialCashReceipt.do?methodToCall=docHandler&command=initiate&docTypeName=CR" /></li>
        <li><portal:portalLink displayTitle="true" title="Credit Card Receipt" url="financialCreditCardReceipt.do?methodToCall=docHandler&command=initiate&docTypeName=CCR" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher" url="financialDisbursementVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=DV" /></li>
        <li><portal:portalLink displayTitle="true" title="Distribution of Income and Expense" url="financialDistributionOfIncomeAndExpense.do?methodToCall=docHandler&command=initiate&docTypeName=DI" /></li>
		<li><portal:portalLink displayTitle="true" title="General Error Correction" url="financialGeneralErrorCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=GEC" /></li>
		<li><portal:portalLink displayTitle="true" title="Indirect Cost Adjustment" url="financialIndirectCostAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=ICA" /></li>
		<li><portal:portalLink displayTitle="true" title="Internal Billing" url="financialInternalBilling.do?methodToCall=docHandler&command=initiate&docTypeName=IB" /></li>
		<li><portal:portalLink displayTitle="true" title="Pre-Encumbrance" url="financialPreEncumbrance.do?methodToCall=docHandler&command=initiate&docTypeName=PE" /></li>
		<li><portal:portalLink displayTitle="true" title="Transfer of Funds" url="financialTransferOfFunds.do?methodToCall=docHandler&command=initiate&docTypeName=TF" /></li>
    </ul>
    
    <strong>Labor Distribution</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Benefit Expense Transfer" url="laborBenefitExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=BT" /></li>	            
		<li><portal:portalLink displayTitle="true" title="Salary Expense Transfer" url="laborSalaryExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=ST" /></li>	
    </ul>

	<strong>Purchasing/Accounts Payable</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Bulk Receiving" url="purapBulkReceiving.do?methodToCall=docHandler&command=initiate&docTypeName=RCVB" /></li>
        <li><portal:portalLink displayTitle="true" title="Contract Manager Assignment" url="purapContractManagerAssignment.do?methodToCall=docHandler&command=initiate&docTypeName=ACM" /></li>
		<li><portal:portalLink displayTitle="true" title="Payment Request" url="purapPaymentRequest.do?methodToCall=docHandler&command=initiate&docTypeName=PREQ" /></li>
		<li><portal:portalLink displayTitle="true" title="Receiving" url="purapLineItemReceiving.do?methodToCall=docHandler&command=initiate&docTypeName=RCVL" /></li>
        <li><portal:portalLink displayTitle="true" title="Requisition" url="purapRequisition.do?methodToCall=docHandler&command=initiate&docTypeName=REQS" /></li>
        <li><portal:portalLink displayTitle="true" title="Shop Catalogs" url="b2b.do?methodToCall=shopCatalogs" /></li>
        <li><portal:portalLink displayTitle="true" title="Vendor Credit Memo" url="purapVendorCreditMemo.do?methodToCall=docHandler&command=initiate&docTypeName=CM" /></li>
    </ul>
</div>
<channel:portalChannelBottom />
