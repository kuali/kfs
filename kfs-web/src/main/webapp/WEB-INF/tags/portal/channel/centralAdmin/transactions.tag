<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Transactions" />
<div class="body">
	<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
		<strong>Accounts Receivable</strong>
		<br />
		<ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Cash Control" url="${ConfigProperties.application.url}/arCashControlDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CTRL" /></li>
			<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
				<li><portal:portalLink displayTitle="true" title="Contracts & Grants Collection Activity" url="${ConfigProperties.application.url}/arContractsGrantsCollectionActivityDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CCA" /></li>
				<li><portal:portalLink displayTitle="true" title="Contracts & Grants Invoice" url="${ConfigProperties.application.url}/arContractsGrantsInvoiceLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceLookupResult&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
				<li><portal:portalLink displayTitle="true" title="Contracts & Grants LOC Review" url="${ConfigProperties.application.url}/arContractsGrantsLetterOfCreditReviewDocument.do?methodToCall=docHandler&command=initiate&docTypeName=LCR" /></li>			
			</c:if>
			<li><portal:portalLink displayTitle="true" title="Customer Credit Memo" url="${ConfigProperties.application.url}/arCustomerCreditMemoDocument.do?methodToCall=docHandler&command=initiate&docTypeName=CRM" /></li>
			<li><portal:portalLink displayTitle="true" title="Customer Invoice" url="${ConfigProperties.application.url}/arCustomerInvoiceDocument.do?methodToCall=docHandler&command=initiate&docTypeName=INV" /></li>
			<li><portal:portalLink displayTitle="true" title="Customer Invoice Writeoff" url="${ConfigProperties.application.url}/arCustomerInvoiceWriteoffDocument.do?methodToCall=docHandler&command=initiate&docTypeName=INVW" /></li>
			<li><portal:portalLink displayTitle="true" title="Customer Invoice Writeoff Lookup" url="${ConfigProperties.application.url}/arCustomerInvoiceWriteoffLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
			<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
				<li><portal:portalLink displayTitle="true" title="Final Billed Indicator" url="${ConfigProperties.application.url}/arFinalBilledIndicatorDocument.do?methodToCall=docHandler&command=initiate&docTypeName=FBI" /></li>
			</c:if>
			<li><portal:portalLink displayTitle="true" title="Payment Application" url="${ConfigProperties.application.url}/arPaymentApplicationDocument.do?methodToCall=docHandler&command=initiate&docTypeName=APP" /></li>
		</ul>
	</c:if>

	<strong>Financial Processing</strong><br />
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Advance Deposit" url="${ConfigProperties.application.url}/financialAdvanceDeposit.do?methodToCall=docHandler&command=initiate&docTypeName=AD" /></li>
		<li><portal:portalLink displayTitle="true" title="Auxiliary Voucher" url="${ConfigProperties.application.url}/financialAuxiliaryVoucher.do?methodToCall=docHandler&command=initiate&docTypeName=AV" /></li>
        <li><portal:portalLink displayTitle="true" title="Budget Adjustment" url="${ConfigProperties.application.url}/financialBudgetAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=BA" /></li>
        <li><portal:portalLink displayTitle="true" title="Credit Card Receipt" url="${ConfigProperties.application.url}/financialCreditCardReceipt.do?methodToCall=docHandler&command=initiate&docTypeName=CCR" /></li>
		<li><portal:portalLink displayTitle="true" title="Indirect Cost Adjustment" url="${ConfigProperties.application.url}/financialIndirectCostAdjustment.do?methodToCall=docHandler&command=initiate&docTypeName=ICA" /></li>
    </ul>
    
    <c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
	    <strong>Labor Distribution</strong><br />
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title="Benefit Expense Transfer" url="${ConfigProperties.application.url}/laborBenefitExpenseTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=BT" /></li>	            
	    </ul>
	</c:if>

	<c:if test="${ConfigProperties.module.purchasing.enabled == 'true'}">
		<strong>Purchasing/Accounts Payable</strong><br />
	    <ul class="chan">
			<li><portal:portalLink displayTitle="true" title="Payment Request" url="${ConfigProperties.application.url}/purapPaymentRequest.do?methodToCall=docHandler&command=initiate&docTypeName=PREQ" /></li>
	        <li><portal:portalLink displayTitle="true" title="Vendor Credit Memo" url="${ConfigProperties.application.url}/purapVendorCreditMemo.do?methodToCall=docHandler&command=initiate&docTypeName=CM" /></li>
	    </ul>
    </c:if>

    <strong>Check Reconciliation</strong>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Check Reconciliation" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=edu.arizona.kfs.module.cr.businessobject.CheckReconciliation&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    </ul>

</div>
<channel:portalChannelBottom />
