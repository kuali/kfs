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
        <li><portal:portalLink displayTitle="true" title="Cash Receipt" url="${ConfigProperties.application.url}/financialCashReceipt.do?methodToCall=docHandler&command=initiate&docTypeName=CR" /></li>
        <li><portal:portalLink displayTitle="true" title="Disbursement Voucher" url="${ConfigProperties.application.url}/financialDisbursementVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=DV" /></li>
        <li><portal:portalLink displayTitle="true" title="Distribution of Income and Expense" url="${ConfigProperties.application.url}/financialDistributionOfIncomeAndExpense.do?methodToCall=docHandler&command=initiate&docTypeName=DI" /></li>
		<li><portal:portalLink displayTitle="true" title="General Error Correction" url="${ConfigProperties.application.url}/financialGeneralErrorCorrection.do?methodToCall=docHandler&command=initiate&docTypeName=GEC" /></li>
		<li><portal:portalLink displayTitle="true" title="Internal Billing" url="${ConfigProperties.application.url}/financialInternalBilling.do?methodToCall=docHandler&command=initiate&docTypeName=IB" /></li>
		<li><portal:portalLink displayTitle="true" title="Intra-Account Adjustment" url="${ConfigProperties.application.url}/financialIntraAccountAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=IAA" /></li>	
		<li><portal:portalLink displayTitle="true" title="Pre-Encumbrance" url="${ConfigProperties.application.url}/financialPreEncumbrance.do?methodToCall=docHandler&command=initiate&docTypeName=PE" /></li>
		<li><portal:portalLink displayTitle="true" title="Transfer of Funds" url="${ConfigProperties.application.url}/financialTransferOfFunds.do?methodToCall=docHandler&command=initiate&docTypeName=TF" /></li>
		
    </ul>
    
    <c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
	    <strong>Labor Distribution</strong><br />
	    <ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Salary Expense Transfer" url="${ConfigProperties.application.url}/laborSalaryExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=ST" /></li>	
	    </ul>
	</c:if>

	<c:if test="${ConfigProperties.module.purchasing.enabled == 'true'}">
		<strong>Purchasing/Accounts Payable</strong><br />
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title="Bulk Receiving" url="${ConfigProperties.application.url}/purapBulkReceiving.do?methodToCall=docHandler&command=initiate&docTypeName=RCVB" /></li>
	        <li><portal:portalLink displayTitle="true" title="Contract Manager Assignment" url="${ConfigProperties.application.url}/purapContractManagerAssignment.do?methodToCall=docHandler&command=initiate&docTypeName=ACM" /></li>
			<li><portal:portalLink displayTitle="true" title="Receiving" url="${ConfigProperties.application.url}/purapLineItemReceiving.do?methodToCall=docHandler&command=initiate&docTypeName=RCVL" /></li>
	        <li><portal:portalLink displayTitle="true" title="Requisition" url="${ConfigProperties.application.url}/purapRequisition.do?methodToCall=docHandler&command=initiate&docTypeName=REQS" /></li>
	        <li><portal:portalLink displayTitle="true" title="Shop Catalogs" url="${ConfigProperties.application.url}/b2b.do?methodToCall=shopCatalogs" /></li>
	    </ul>
    </c:if>
    <c:if test="${fn:trim(ConfigProperties.environment) != fn:trim(ConfigProperties.production.environment.code)}">
	    <c:if test="${ConfigProperties.module.travel.enabled == 'true'}">
	        <strong>Travel</strong><br />
	        <ul class="chan">
	            <li><portal:portalLink displayTitle="true" title="Entertainment Reimbursement" url="${ConfigProperties.application.url}/temTravelEntertainment.do?methodToCall=docHandler&command=initiate&docTypeName=ENT" /></li>
	            <li><portal:portalLink displayTitle="true" title="Moving and Relocation Reimbursement" url="${ConfigProperties.application.url}/temTravelRelocation.do?methodToCall=docHandler&command=initiate&docTypeName=RELO" /></li>
	            <li><portal:portalLink displayTitle="true" title="Travel Arranger" url="${ConfigProperties.application.url}/temTravelArranger.do?methodToCall=docHandler&command=initiate&docTypeName=TTA" /></li>
	            <li><portal:portalLink displayTitle="true" title="Travel Authorization" url="${ConfigProperties.application.url}/temTravelAuthorization.do?methodToCall=docHandler&command=initiate&docTypeName=TA" /></li>
	            <c:if test="${ConfigProperties.travel.reimbursement.initiatelink.enabled == 'true'}">
	                <li><portal:portalLink displayTitle="true" title="Travel Reimbursement" url="${ConfigProperties.application.url}/temTravelReimbursement.do?methodToCall=docHandler&command=initiate&docTypeName=TR" /></li>
	            </c:if>
	        </ul>
	  	 </c:if>
   </c:if>
</div>
<channel:portalChannelBottom />
