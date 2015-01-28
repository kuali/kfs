<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Transactions" />
<div class="body">
	<c:if
		test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
		<strong>Accounts Receivable</strong>
		<br />
		<ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Cash Control"
					url="${ConfigProperties.application.url}/arCashControlDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CTRL" /></li>
			<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
				<li><portal:portalLink displayTitle="true"
						title="Contracts & Grants Collection Activity"
						url="${ConfigProperties.application.url}/arContractsGrantsCollectionActivityDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CCA" /></li>
				<li><portal:portalLink displayTitle="true"
						title="Contracts & Grants Invoice"
						url="${ConfigProperties.application.url}/arContractsGrantsInvoiceLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceLookupResult&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
				<li><portal:portalLink displayTitle="true"
						title="Contracts & Grants LOC Review"
						url="${ConfigProperties.application.url}/arContractsGrantsLetterOfCreditReviewDocument.do?methodToCall=docHandler&command=initiate&docTypeName=LCR" /></li>			
			</c:if>
			<li><portal:portalLink displayTitle="true"
					title="Customer Credit Memo"
					url="${ConfigProperties.application.url}/arCustomerCreditMemoDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CRM" /></li>
			<li><portal:portalLink displayTitle="true"
					title="Customer Invoice"
					url="${ConfigProperties.application.url}/arCustomerInvoiceDocument.do?methodToCall=docHandler&command=initiate&docTypeName=INV" /></li>
			<li><portal:portalLink displayTitle="true"
					title="Customer Invoice Writeoff"
					url="${ConfigProperties.application.url}/arCustomerInvoiceWriteoffDocument.do?methodToCall=docHandler&command=initiate&docTypeName=INVW" /></li>
			<li><portal:portalLink displayTitle="true"
					title="Customer Invoice Writeoff Lookup"
					url="${ConfigProperties.application.url}/arCustomerInvoiceWriteoffLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
			<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
				<li><portal:portalLink displayTitle="true"
						title="Final Billed Indicator"
						url="${ConfigProperties.application.url}/arFinalBilledIndicatorDocument.do?methodToCall=docHandler&command=initiate&docTypeName=FBI" /></li>
			</c:if>
			<li><portal:portalLink displayTitle="true"
					title="Payment Application"
					url="${ConfigProperties.application.url}/arPaymentApplicationDocument.do?methodToCall=docHandler&command=initiate&docTypeName=APP" /></li>
		</ul>
	</c:if>

	<c:if
		test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
		<strong>Budget Construction</strong>
		<br />
		<ul class="chan">
			<li><portal:portalLink displayTitle="true"
					title="Budget Construction Selection"
					url="${ConfigProperties.application.url}/budgetBudgetConstructionSelection.do?methodToCall=loadExpansionScreen" /></li>
		</ul>
	</c:if>

	<strong>Financial Processing</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Advance Deposit" url="${ConfigProperties.application.url}/financialAdvanceDeposit.do?methodToCall=docHandler&command=initiate&docTypeName=AD" /></li>
		<li><portal:portalLink displayTitle="true" title="Auxiliary Voucher" url="${ConfigProperties.application.url}/financialAuxiliaryVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=AV" /></li>
        <li><portal:portalLink displayTitle="true" title="Budget Adjustment" url="${ConfigProperties.application.url}/financialBudgetAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=BA" /></li>
        <li><portal:portalLink displayTitle="true" title="Cash Receipt" url="${ConfigProperties.application.url}/financialCashReceipt.do?methodToCall=docHandler&command=initiate&docTypeName=CR" /></li>
        <li><portal:portalLink displayTitle="true" title="Credit Card Receipt" url="${ConfigProperties.application.url}/financialCreditCardReceipt.do?methodToCall=docHandler&command=initiate&docTypeName=CCR" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher" url="${ConfigProperties.application.url}/financialDisbursementVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=DV" /></li>
        <li><portal:portalLink displayTitle="true" title="Distribution of Income and Expense" url="${ConfigProperties.application.url}/financialDistributionOfIncomeAndExpense.do?methodToCall=docHandler&command=initiate&docTypeName=DI" /></li>
		<li><portal:portalLink displayTitle="true" title="General Error Correction" url="${ConfigProperties.application.url}/financialGeneralErrorCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=GEC" /></li>
		<li><portal:portalLink displayTitle="true" title="Indirect Cost Adjustment" url="${ConfigProperties.application.url}/financialIndirectCostAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=ICA" /></li>
		<li><portal:portalLink displayTitle="true" title="Internal Billing" url="${ConfigProperties.application.url}/financialInternalBilling.do?methodToCall=docHandler&command=initiate&docTypeName=IB" /></li>
		<li><portal:portalLink displayTitle="true" title="Intra-Account Adjustment" url="${ConfigProperties.application.url}/financialIntraAccountAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=IAA" /></li>	
		<li><portal:portalLink displayTitle="true" title="Pre-Encumbrance" url="${ConfigProperties.application.url}/financialPreEncumbrance.do?methodToCall=docHandler&command=initiate&docTypeName=PE" /></li>
		<li><portal:portalLink displayTitle="true" title="Transfer of Funds" url="${ConfigProperties.application.url}/financialTransferOfFunds.do?methodToCall=docHandler&command=initiate&docTypeName=TF" /></li>
		
    </ul>
    
    <c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
	    <strong>Labor Distribution</strong><br />
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title="Benefit Expense Transfer" url="${ConfigProperties.application.url}/laborBenefitExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=BT" /></li>	            
			<li><portal:portalLink displayTitle="true" title="Salary Expense Transfer" url="${ConfigProperties.application.url}/laborSalaryExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=ST" /></li>	
	    </ul>
	</c:if>

	<c:if test="${ConfigProperties.module.purchasing.enabled == 'true'}">
		<strong>Purchasing/Accounts Payable</strong><br />
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title="Bulk Receiving" url="${ConfigProperties.application.url}/purapBulkReceiving.do?methodToCall=docHandler&command=initiate&docTypeName=RCVB" /></li>
	        <li><portal:portalLink displayTitle="true" title="Contract Manager Assignment" url="${ConfigProperties.application.url}/purapContractManagerAssignment.do?methodToCall=docHandler&command=initiate&docTypeName=ACM" /></li>
			<li><portal:portalLink displayTitle="true" title="Payment Request" url="${ConfigProperties.application.url}/purapPaymentRequest.do?methodToCall=docHandler&command=initiate&docTypeName=PREQ" /></li>
			<li><portal:portalLink displayTitle="true" title="Receiving" url="${ConfigProperties.application.url}/purapLineItemReceiving.do?methodToCall=docHandler&command=initiate&docTypeName=RCVL" /></li>
	        <li><portal:portalLink displayTitle="true" title="Requisition" url="${ConfigProperties.application.url}/purapRequisition.do?methodToCall=docHandler&command=initiate&docTypeName=REQS" /></li>
	        <li><portal:portalLink displayTitle="true" title="Shop Catalogs" url="${ConfigProperties.application.url}/b2b.do?methodToCall=shopCatalogs" /></li>
	        <li><portal:portalLink displayTitle="true" title="Vendor Credit Memo" url="${ConfigProperties.application.url}/purapVendorCreditMemo.do?methodToCall=docHandler&command=initiate&docTypeName=CM" /></li>
	    </ul>
    </c:if>
    
    <c:if test="${ConfigProperties.module.endowment.enabled == 'true'}">
	    <strong>Endowment</strong><br />
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title="Asset Decrease" url="${ConfigProperties.application.url}/endowAssetDecreaseDocument.do?methodToCall=docHandler&command=initiate&docTypeName=EAD" /></li>
	        <li><portal:portalLink displayTitle="true" title="Asset Increase" url="${ConfigProperties.application.url}/endowAssetIncreaseDocument.do?methodToCall=docHandler&command=initiate&docTypeName=EAI" /></li>
	        <li><portal:portalLink displayTitle="true" title="Cash Decrease" url="${ConfigProperties.application.url}/endowCashDecreaseDocument.do?methodToCall=docHandler&command=initiate&docTypeName=ECDD" /></li>
	        <li><portal:portalLink displayTitle="true" title="Cash Increase" url="${ConfigProperties.application.url}/endowCashIncreaseDocument.do?methodToCall=docHandler&command=initiate&docTypeName=ECI" /></li>
	        <li><portal:portalLink displayTitle="true" title="Cash Transfer" url="${ConfigProperties.application.url}/endowCashTransferDocument.do?methodToCall=docHandler&command=initiate&docTypeName=ECT" /></li>
	        <li><portal:portalLink displayTitle="true" title="Endowment To GL Transfer Of Funds" url="${ConfigProperties.application.url}/endowEndowmentToGLTransferOfFundsDocument.do?methodToCall=docHandler&command=initiate&docTypeName=EGLT" /></li>
	        <li><portal:portalLink displayTitle="true" title="GL To Endowment Transfer Of Funds" url="${ConfigProperties.application.url}/endowGLToEndowmentTransferOfFundsDocument.do?methodToCall=docHandler&command=initiate&docTypeName=GLET" /></li>        
	  		<li><portal:portalLink displayTitle="true" title="Liability Decrease" url="${ConfigProperties.application.url}/endowLiabilityDecreaseDocument.do?methodToCall=docHandler&command=initiate&docTypeName=ELD" /></li>
	        <li><portal:portalLink displayTitle="true" title="Liability Increase" url="${ConfigProperties.application.url}/endowLiabilityIncreaseDocument.do?methodToCall=docHandler&command=initiate&docTypeName=ELI" /></li>
	        <li><portal:portalLink displayTitle="true" title="Security Transfer" url="${ConfigProperties.application.url}/endowSecurityTransferDocument.do?methodToCall=docHandler&command=initiate&docTypeName=EST" /></li>
	     </ul>
	 </c:if>
     <c:if test="${ConfigProperties.module.travel.enabled == 'true'}">
         <strong>Travel</strong><br />
         <ul class="chan">
             <li><portal:portalLink displayTitle="true" title="Entertainment Reimbursement" url="${ConfigProperties.application.url}/temTravelEntertainment.do?methodToCall=docHandler&command=initiate&docTypeName=ENT" /></li>
             <li><portal:portalLink displayTitle="true" title="Moving and Relocation Reimbursement" url="${ConfigProperties.application.url}/temTravelRelocation.do?methodToCall=docHandler&command=initiate&docTypeName=RELO" /></li>
             <li><portal:portalLink displayTitle="true" title="Travel Arranger" url="${ConfigProperties.application.url}/temTravelArranger.do?methodToCall=docHandler&command=initiate&docTypeName=TTA" /></li>
             <li><portal:portalLink displayTitle="true" title="Travel Authorization" url="${ConfigProperties.application.url}/temTravelAuthorization.do?methodToCall=docHandler&command=initiate&docTypeName=TA" /></li>
             <c:if test="${ConfigProperties.module.travel.reimbursement.initiatelink.enabled == 'true'}">
                 <li><portal:portalLink displayTitle="true" title="Travel Reimbursement" url="${ConfigProperties.application.url}/temTravelReimbursement.do?methodToCall=docHandler&command=initiate&docTypeName=TR" /></li>
             </c:if>
         </ul>
   	 </c:if>
</div>
<channel:portalChannelBottom />
